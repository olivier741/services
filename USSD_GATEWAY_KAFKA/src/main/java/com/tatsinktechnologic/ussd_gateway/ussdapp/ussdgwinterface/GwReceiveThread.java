/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.ussdgwinterface;

import com.tatsinktechnologic.ussd_gateway.mmserver.base.ProcessThreadMX;
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.UssdMessage;
import com.tatsinktechnologic.ussd_gateway.ussdapp.transaction.CoreProcessModule;
import com.tatsinktechnologic.ussd_gateway.ussdapp.transaction.Transaction;
import com.tatsinktechnologic.ussd_gateway.ussdapp.transaction.TransactionMgr;

/**
 *
 * @author olivier.tatsinkou
 */
public class GwReceiveThread extends ProcessThreadMX {

    private CoreProcessModule coreModule;
    private TransactionMgr tranMgr;

    public GwReceiveThread(String name) {
        super(name, "thread for process ussd message");
        try {
            registerAgent("CoreModule:type=GwReceiveThread,name=" + name);
        } catch (Exception ex) {
            this.logger.error("regist mbean fail: " + ex.getMessage());
        }
    }

    public void setCoreModule(CoreProcessModule coreModule) {
        this.coreModule = coreModule;
    }

    public void setTranMgr(TransactionMgr tranMgr) {
        this.tranMgr = tranMgr;
    }

    public void process() {
        this.logger.info("receiving message from ussdgw ...");
        UssdMessage msg = this.coreModule.getMessageFromGw();
        if (msg == null) {
            this.logger.info("no message to receive");
            return;
        }
        this.logger.info("recv message: " + msg);
        Transaction tran;
        if (msg.isFisrtHlrMsg()) {
            this.logger.info("message is the first of transaction. Make transaction");
            tran = this.tranMgr.makeTransaction(msg);
        } else {
            this.logger.info("message belong to exist transaction. Get it.");
            tran = this.tranMgr.getTransaction(msg.getTransId());
            if (tran == null) {
                this.logger.warn("Transaction was closed before: " + msg.getTransId());
                return;
            }
        }
        tran.processUssdMsg(msg);
    }

    public void destroy() {
        stop();
        try {
            unregisterAgent();
        } catch (Exception ex) {
            this.logger.error("unregist mbean fail: " + ex.getMessage(), ex);
        }
    }
}
