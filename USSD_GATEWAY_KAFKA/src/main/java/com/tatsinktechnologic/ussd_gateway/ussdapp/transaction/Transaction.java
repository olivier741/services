/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.transaction;

/**
 *
 * @author olivier.tatsinkou
 */
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.UssdMessage;
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.VasRequest;
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.VasResponse;
import org.apache.log4j.Logger;

public class Transaction {

    private static final String INPUT_HOLDER = "@INPUT@";
    private static int suffixVasReqId;
    private State curState;
    private String id;
    private Logger logger;
    private volatile boolean isClosed;
    private String msisdn;
    private String imsi;
    private TransactionMgr transMgr;
    private CoreProcessModule coreModule;
    private VasRequest curVasReq;
    private RequestTimeoutTask timeoutTask;
    private UssdTimeoutTask ussdTimeoutTask;
    private int ussdgwId;

    public Transaction(String id, String msisdn, String imsi, State beginState, int ussdgwId) {
        this.logger = Logger.getLogger(Transaction.class);
        this.id = id;
        this.msisdn = msisdn;
        this.imsi = imsi;
        this.curState = beginState;
        this.isClosed = false;

        this.ussdgwId = ussdgwId;
    }

    public void setCoreModule(CoreProcessModule coreModule) {
        this.coreModule = coreModule;
    }

    public void setTransMgr(TransactionMgr transMgr) {
        this.transMgr = transMgr;
    }

    public String getId() {
        return this.id;
    }

    public synchronized void processUssdMsg(UssdMessage ussdMsg) {
        if (this.isClosed) {
            this.logger.warn("transaction is closed before: " + this.id);
            return;
        }
        if (this.ussdTimeoutTask != null) {
            this.logger.info("Cancel timer task");
            if (!this.ussdTimeoutTask.cancel()) {
                this.logger.warn("Timer task expire. Can't cancel!");
                if (ussdMsg.getType() == 101) {
                    this.logger.warn("GW still think this transaction is normal. Do cancel transaction.");
                    sendCancelToSub("timeout expire");
                }
                this.isClosed = true;
                return;
            }
            this.ussdTimeoutTask = null;
        }
        switch (ussdMsg.getType()) {
            case 102:
            case 104:
                this.logger.info("error transaction. Close it");
                if (this.curState.getStateType() == 5) {
                    this.logger.info("set current vasrequest is invalid");
                    this.curVasReq.setValid(false);
                    this.timeoutTask.cancel();
                    this.logger.info("remove waited VAS request");

                    this.coreModule.removeWaitedReq(this.curVasReq.getId());

                    this.logger.info("write log VAS request immediately");

                    this.coreModule.pushWaitedVasReqToLog(this.curVasReq.getId(), "Error or Sub cancel transaction");
                }
                releaseTrans();
                break;
            case 100:
            case 101:
                String input = ussdMsg.getUssdString();

                this.logger.info("change state");
                changeState(input);

                procComingState(input);
                break;
            case 103:
            default:
                this.logger.warn("unexpected ussd message type: " + ussdMsg.getType());
        }
    }

    public synchronized void processVasRsp(VasResponse vasRsp) {
        if (this.isClosed) {
            this.logger.warn("transaction is closed before: " + this.id);
            return;
        }
        this.timeoutTask.cancel();
        this.timeoutTask = null;
        this.curVasReq = null;

        String input = vasRsp.getContent();

        this.logger.info("change state");
        changeState(input);

        procComingState(input);
    }

    public synchronized void processErrSendVasReq() {
        if (this.isClosed) {
            this.logger.warn("transaction is closed before: " + this.id);
            return;
        }
        this.timeoutTask.cancel();
        this.timeoutTask = null;
        this.curVasReq = null;

        this.logger.info("send vas request error. Special change state");
        changeState("ERROR");

        procComingState("ERROR");
    }

    public synchronized void processVasTimeout() {
        if (this.isClosed) {
            this.logger.warn("transaction is closed before: " + this.id);
            return;
        }
        this.logger.info("set cur vasrequest is invalid");
        this.curVasReq.setValid(false);
        this.curVasReq = null;

        this.logger.warn("TIMEOUT receive response from VAS.");
        changeState("TIMEOUT");

        procComingState("TIMEOUT");
    }

    private void procComingState(String input) {
        if (this.curState == null) {
            this.logger.error("no state to come. do cancel transaction");

            sendCancelToSub("The ussd string which sub input is invalid");

            this.logger.info("release transaction");
            releaseTrans();
            return;
        }
        switch (this.curState.getStateType()) {
            case 1:
                procStateEndWithoutVas(input);
                break;
            case 3:
                procStateEndWithVas(input);
                break;
            case 2:
                procStateNotEndWithoutVas(input);
                break;
            case 4:
                procStateNotEndWithVas(input);
                break;
            case 5:
                procStateWaitVasRsp(input);
                break;
            default:
                this.logger.error("invalid state. Do cancel transaction");

                sendCancelToSub("The ussd string which sub input is invalid");

                this.logger.info("release transaction");
                releaseTrans();
        }
    }

    private void sendMenuToSub(String content, int charSet)
            throws Exception {
        this.logger.info("make ussd message");
        UssdMessage menu = new UssdMessage(202);
        menu.setTransId(this.id);
        menu.setUssdString(content, charSet);
        menu.setTimeout(this.curState.getTimeout());
        menu.setConnectorId(this.ussdgwId);

        this.logger.info("send to gateway");
        this.coreModule.sendGwUssdMsg(menu);
    }

    private void sendResultToSub(String content, int charSet) throws Exception {
        this.logger.info("make ussd message");
        UssdMessage result = new UssdMessage(203);
        result.setTransId(this.id);
        result.setUssdString(content, charSet);
        result.setConnectorId(this.ussdgwId);

        this.logger.info("send to gateway");
        this.coreModule.sendGwUssdMsg(result);
    }

    private void sendCancelToSub(String reason) {
        this.logger.info("make ussd message");
        UssdMessage cancel = new UssdMessage(205);
        cancel.setTransId(this.id);
        cancel.setUssdString(reason);
        cancel.setConnectorId(this.ussdgwId);
        this.logger.info("send to gateway");
        try {
            this.coreModule.sendGwUssdMsg(cancel);
        } catch (Exception ex) {
            this.logger.error("send cancel to sub fail: " + ex.getMessage());
        }
    }

    private void procStateEndWithoutVas(String input) {
        String content = this.curState.getContent();
        if ((content == null) || (content.isEmpty())) {
            sendCancelToSub("transaction error: " + input);
        } else {
            content = content.replace("@INPUT@", input);
            try {
                sendResultToSub(content, this.curState.getCharSet());
            } catch (Exception ex) {
                this.logger.error("send ussd message fail: " + ex.getMessage());
            }
        }
        this.logger.info("release transaction");
        releaseTrans();
    }

    private void procStateNotEndWithoutVas(String input) {
        String content = this.curState.getContent();

        content = content.replace("@INPUT@", input);
        try {
            sendMenuToSub(content, this.curState.getCharSet());

            this.ussdTimeoutTask = this.coreModule.monitorUssdGwTimeout(this.id, this.curState.getTimeout() + 10);
        } catch (Exception ex) {
            this.logger.error("send ussd message fail: " + ex.getMessage());

            this.logger.info("release transaction");
            releaseTrans();
        }
    }

    private void procStateEndWithVas(String input) {
        try {
            this.logger.info("make vas request");
            VasRequest req = makeVasReq(input);
            this.logger.info("send request to vas and not wait");
            this.coreModule.sendVasRequestNotWait(req);
        } catch (Exception e) {
            this.logger.error("send request to vas fail: " + e.getMessage());

            sendCancelToSub("Send request to VAS fail");

            this.logger.info("release transaction");
            releaseTrans();
            return;
        }
        String content = this.curState.getContent();

        content = content.replace("@INPUT@", input);
        try {
            sendResultToSub(content, this.curState.getCharSet());
        } catch (Exception ex) {
            this.logger.error("send ussd message to gw fail: " + ex.getMessage());
        }
        this.logger.info("release transaction");
        releaseTrans();
    }

    private void procStateNotEndWithVas(String input) {
        try {
            this.logger.info("make vas request");
            VasRequest req = makeVasReq(input);
            this.logger.info("send request to vas and not wait");
            this.coreModule.sendVasRequestNotWait(req);
        } catch (Exception e) {
            this.logger.error("send request to vas fail: " + e.getMessage());

            sendCancelToSub("Send request to VAS fail");

            this.logger.info("release transaction");
            releaseTrans();
            return;
        }
        String content = this.curState.getContent();

        content = content.replace("@INPUT@", input);
        try {
            sendMenuToSub(content, this.curState.getCharSet());

            this.ussdTimeoutTask = this.coreModule.monitorUssdGwTimeout(this.id, this.curState.getTimeout() + 10);
        } catch (Exception ex) {
            this.logger.error("send ussd message to gw fail: " + ex.getMessage());

            this.logger.info("release transaction");
            releaseTrans();
        }
    }

    private void procStateWaitVasRsp(String input) {
        try {
            this.logger.info("make vas request");

            this.curVasReq = makeVasReq(input);
            this.logger.info("send request to vas and wait response");

            this.timeoutTask = this.coreModule.sendVasRequest(this.curVasReq, this.id, this.curState.getTimeout());
        } catch (Exception e) {
            this.logger.error("send request to vas fail: " + e.getMessage());

            sendCancelToSub("Send request to VAS fail");

            this.logger.info("release transaction");
            releaseTrans();
        }
    }

    private void changeState(String input) {
        State oldState = this.curState;
        this.curState = this.curState.changeState(input);
        this.logger.info(oldState + " --> " + this.curState);
    }

    private VasRequest makeVasReq(String input) {
        VasRequest req = new VasRequest(String.valueOf(this.coreModule.getSid()) + System.currentTimeMillis() + getSuffixVasReqId());

        req.setBizId(this.curState.getBizId());
        req.setConnectorId(this.curState.getCpId());
        req.setNeedEncrypt(this.curState.isNeedEncrypt());
        req.setParams(input);
        req.setImsi(this.imsi);
        req.setMsisdn(this.msisdn);
        req.setTransId(this.id);

        return req;
    }

    public void closeTransaction() {
        this.isClosed = true;
    }

    private void releaseTrans() {
        this.isClosed = true;

        this.transMgr.removeTrans(this.id);
    }

    private static synchronized int getSuffixVasReqId() {
        return ++suffixVasReqId;
    }
}
