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
import com.tatsinktechnologic.ussd_gateway.ussdapp.ussdgwinterface.GwReceiveThread;
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.UssdMessage;
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.Utils;
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.VasRequest;
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.VasResponse;
import com.tatsinktechnologic.ussd_gateway.ussdapp.config.DBConfig;
import com.tatsinktechnologic.ussd_gateway.ussdapp.config.UssdAppConfig;
import com.tatsinktechnologic.ussd_gateway.ussdapp.dblog.DBLog;
import com.tatsinktechnologic.ussd_gateway.ussdapp.ussdgwinterface.UssdGwInfModule;
import com.tatsinktechnologic.ussd_gateway.ussdapp.vasinterface.VasInfModule;
import java.lang.management.ManagementFactory;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Timer;
import javax.management.ObjectName;
import org.apache.log4j.Logger;

public class CoreProcessModule  implements CoreProcessModuleMBean {

    private UssdAppConfig config;
    private DBConfig dbConfig;
    private Logger logger;
    private Hashtable<String, String> waitTbl;
    private GwReceiveThread[] gwThreads;
    private VasReceiveThread[] vasThreads;
    private TransactionMgr tranMgr;
    private UssdGwInfModule gwInfModule;
    private VasInfModule vasInfModule;
    private DBLog dbLog;
    private int sid;
    private Timer[] timers;
    private int timerIndex;

    public CoreProcessModule(UssdAppConfig config, DBConfig dbConfig)  throws Exception {
        this.logger = Logger.getLogger(CoreProcessModule.class);
        this.config = config;
        this.dbConfig = dbConfig;

        this.logger.info("init object");
        init();
        this.logger.info("regist Mbean");
        try {
            ManagementFactory.getPlatformMBeanServer().registerMBean(this, new ObjectName("CoreModule:name=coreModule"));
        } catch (Exception ex) {
            this.logger.error("regist mbean fail: " + ex.getMessage());
        }
    }

    private void init()  throws Exception {
        this.logger.info("get system id");
        this.sid = this.config.getSystemId();
        this.logger.info("system id = " + this.sid);

        this.logger.info("make table to wait response from vas");
        this.waitTbl = new Hashtable(1000);

        this.logger.info("make transaction manager");
        this.tranMgr = new TransactionMgr(1000);
        this.tranMgr.setCoreModule(this);

        this.logger.info("load state set");
        loadStateSet();

        this.logger.info("make threads for process ussd msg");
        int size = this.config.getNumUssdgwWorkerThread();
        this.logger.info("num thread = " + size);
        this.gwThreads = new GwReceiveThread[size];
        for (int i = 0; i < size; i++) {
            this.gwThreads[i] = new GwReceiveThread("gwReceiveThread-" + i);

            this.gwThreads[i].setCoreModule(this);
            this.gwThreads[i].setTranMgr(this.tranMgr);
        }
        this.logger.info("make threads for process vas response");
        size = this.config.getNumVasWorkerThread();
        this.logger.info("num thread = " + size);
        this.vasThreads = new VasReceiveThread[size];
        for (int i = 0; i < size; i++) {
            this.vasThreads[i] = new VasReceiveThread("vasReceiveThread-" + i);

            this.vasThreads[i].setCoreModule(this);
        }
        this.timers = new Timer[Utils.getNumCoreCPU()];
        for (int i = 0; i < this.timers.length; i++) {
            this.timers[i] = new Timer();
        }
        this.timerIndex = 0;
    }

    public int getSid() {
        return this.sid;
    }

    private void loadStateSet()  throws Exception {
        HashMap<Integer, State> states = new HashMap();
        ResultSet result = null;
        Iterator i$ = null;
        State beginState = null;
        try {
            result = this.dbConfig.getData("select * from state");
            while (result.next()) {
                State tmp = new State(result.getInt("ID"), result.getInt("TYPE"), result.getInt("CP_ID"), result.getInt("BIZ_ID"), result.getString("CONTENT"), result.getInt("TIMEOUT"), result.getInt("DEFAULT_NEXT_STATE"), result.getInt("ENCRYPTED"), result.getInt("CHARSET"));
                if (states.containsKey(Integer.valueOf(tmp.getId()))) {
                    throw new Exception("Two states have same id: " + tmp.getId());
                }
                states.put(Integer.valueOf(tmp.getId()), tmp);
                if (tmp.getStateType() == 0) {
                    beginState = tmp;
                }
            }
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                    this.logger.error("close result set error: " + ex.getMessage());
                }
            }
            i$ = states.values().iterator();
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ex) {
                    this.logger.error("close result set error: " + ex.getMessage());
                }
            }
        }

        while (i$.hasNext()) {
            State state = (State) i$.next();
            if (state.notEndState()) {
                ChangeStateTbl changeTbl = new ChangeStateTbl();
                result = null;
                State tmp = null;
                try {
                    result = this.dbConfig.getData("select * from STATE_CHANGE where STATE1=" + state.getId() + " order by priority ASC");
                    while (result.next()) {
                        tmp = (State) states.get(Integer.valueOf(result.getInt("STATE2")));
                        if (tmp == null) {
                            throw new Exception("config change state table error. State: " + state.getId());
                        }
                        changeTbl.put(result.getString("INPUT"), tmp);
                    }
                    if (result != null) {
                        try {
                            result.close();
                        } catch (SQLException ex) {
                            this.logger.error("close result set error: " + ex.getMessage());
                        }
                    }
                    tmp = (State) states.get(Integer.valueOf(state.getDefaultStateId()));
                } finally {
                    if (result != null) {
                        try {
                            result.close();
                        } catch (SQLException ex) {
                            this.logger.error("close result set error: " + ex.getMessage());
                        }
                    }
                }

                if ((changeTbl.isEmpty()) && (tmp == null)) {
                    throw new Exception("change state table is null");
                }
                state.setChangeStateTbl(changeTbl);
                state.setDefaultState(tmp);
            }
            this.logger.info(state.printChangeStateTbl());
        }
        if (beginState != null) {
            this.tranMgr.setBeginState(beginState);
        } else {
            throw new Exception("missing begin state");
        }
    }

    public void linkToGwInfModule(UssdGwInfModule module) {
        this.gwInfModule = module;
    }

    public void linkToVasInfModule(VasInfModule module) {
        this.vasInfModule = module;
    }

    public void linkToDbLogModule(DBLog dbLog) {
        this.dbLog = dbLog;
    }

    public UssdMessage getMessageFromGw() {
        return this.gwInfModule.getGWMsg();
    }

    public Object getRspFromVas() {
        return this.vasInfModule.getVasRsp();
    }

    public RequestTimeoutTask sendVasRequest(VasRequest req, String transId, int timeout) throws Exception {
        this.logger.info("put request into wait table");
        this.waitTbl.put(req.getId(), transId);

        this.logger.info("send to vas interface module");
        try {
            this.vasInfModule.sendToVas(req);  // here insert into topic kafka
        } catch (Exception ex) {
            this.logger.error("send to vas fail: " + ex.getMessage());

            this.waitTbl.remove(req.getId());

            throw ex;
        }
        return monitorTimeout(req.getId(), timeout);
    }

    public void sendVasRequestNotWait(VasRequest req)  throws Exception {
        this.logger.info("send to vas interface module");
        this.vasInfModule.sendToVas(req);
    }

    public void sendGwUssdMsg(UssdMessage msg)   throws Exception {
        this.gwInfModule.sendToGW(msg);
    }

    public Transaction getWaitedTrans(String requestId) {
        String transId = (String) this.waitTbl.remove(requestId);
        if (transId == null) {
            return null;
        }
        return this.tranMgr.getTransaction(transId);
    }

    public void removeWaitedReq(String requestId) {
        this.waitTbl.remove(requestId);
    }

    public void pushWaitedVasReqToLog(String requestId, String reason) {
        VasResponse rsp = new VasResponse(requestId);

        rsp.setContent(reason);
        rsp.setSendRecvTime(System.currentTimeMillis());

        this.dbLog.logVasRsp(rsp);
    }

    public void reloadNumGwReceiveThread()
            throws Exception {
        this.logger.info("reload num gw receive threads");
        this.logger.info("get new number thread");
        this.logger.info("reload config file");
        this.config.loadCnfFile();
        int size = this.config.getNumUssdgwWorkerThread();
        this.logger.info("new num thread = " + size);

        this.logger.info("stop and unregist mbean current threads");
        for (GwReceiveThread thread : this.gwThreads) {
            thread.destroy();
        }
        this.logger.info("make new threads");
        this.gwThreads = new GwReceiveThread[size];
        for (int i = 0; i < size; i++) {
            this.gwThreads[i] = new GwReceiveThread("gwReceiveThread-" + i);

            this.gwThreads[i].setCoreModule(this);
            this.gwThreads[i].setTranMgr(this.tranMgr);
        }
        this.logger.info("start all threads");
        for (GwReceiveThread thread : this.gwThreads) {
            thread.start();
        }
    }

    public void reloadNumGwReceiveThread(int numThread)
            throws Exception {
        if ((numThread < 1) || (numThread > 150)) {
            throw new Exception("number must in rang [1, 150]");
        }
        this.logger.info("reload num gw receive threads");
        this.logger.info("new amount: " + numThread);
        this.logger.info("stop and unregist mbean current threads");
        for (GwReceiveThread thread : this.gwThreads) {
            thread.destroy();
        }
        this.logger.info("make new threads");
        this.gwThreads = new GwReceiveThread[numThread];
        for (int i = 0; i < numThread; i++) {
            this.gwThreads[i] = new GwReceiveThread("gwReceiveThread-" + i);

            this.gwThreads[i].setCoreModule(this);
            this.gwThreads[i].setTranMgr(this.tranMgr);
        }
        this.logger.info("start all threads");
        for (GwReceiveThread thread : this.gwThreads) {
            thread.start();
        }
    }

    public void reloadNumVasReceiveThread()
            throws Exception {
        this.logger.info("reload num vas receive threads");
        this.logger.info("get new number thread");
        this.logger.info("reload config file");
        this.config.loadCnfFile();
        int size = this.config.getNumVasWorkerThread();
        this.logger.info("new num thread = " + size);

        this.logger.info("stop and unregist mbean current threads");
        for (VasReceiveThread thread : this.vasThreads) {
            thread.destroy();
        }
        this.logger.info("make new threads");
        this.vasThreads = new VasReceiveThread[size];
        for (int i = 0; i < size; i++) {
            this.vasThreads[i] = new VasReceiveThread("vasReceiveThread-" + i);

            this.vasThreads[i].setCoreModule(this);
        }
        this.logger.info("start all threads");
        for (VasReceiveThread thread : this.vasThreads) {
            thread.start();
        }
    }

    public void reloadNumVasReceiveThread(int numThread)
            throws Exception {
        if ((numThread < 1) || (numThread > 150)) {
            throw new Exception("number must in rang [1, 150]");
        }
        this.logger.info("reload num vas receive threads");
        this.logger.info("new amount: " + numThread);
        this.logger.info("stop and unregist mbean current threads");
        for (VasReceiveThread thread : this.vasThreads) {
            thread.destroy();
        }
        this.logger.info("make new threads");
        this.vasThreads = new VasReceiveThread[numThread];
        for (int i = 0; i < numThread; i++) {
            this.vasThreads[i] = new VasReceiveThread("vasReceiveThread-" + i);

            this.vasThreads[i].setCoreModule(this);
        }
        this.logger.info("start all threads");
        for (VasReceiveThread thread : this.vasThreads) {
            thread.start();
        }
    }

    public String viewInfo() {
        StringBuilder buf = new StringBuilder("Core module info:\r\n");
        buf.append("num thread process message from gw: ");
        buf.append(this.gwThreads.length);

        buf.append("\r\nnum threads process response from vas: ");
        buf.append(this.vasThreads.length);

        buf.append("\r\n\r\nnum current transactions: ");
        buf.append(this.tranMgr.getNumTransaction());

        buf.append("\r\nnum vas request waiting: ");
        buf.append(this.waitTbl.size());

        return buf.toString();
    }

    public void start() {
        this.logger.info("start threads process ussd message");
        for (GwReceiveThread thread : this.gwThreads) {
            thread.start();
        }
        this.logger.info("start threads process vas response");
        for (VasReceiveThread thread : this.vasThreads) {
            thread.start();
        }
    }

    public void stop() {
        this.logger.info("stop threads process ussd message");
        for (GwReceiveThread thread : this.gwThreads) {
            thread.stop();
        }
        this.logger.info("stop threads process vas response");
        for (VasReceiveThread thread : this.vasThreads) {
            thread.stop();
        }
        this.logger.info("stop timers for check timeout");
        for (Timer timer : this.timers) {
            timer.cancel();
        }
    }

    public void reloadStateSet()
            throws Exception {
        synchronized (this.dbConfig.getLock()) {
            this.logger.info("reconnect DB first");
            this.dbConfig.reconnectDb();
            try {
                loadStateSet();
            } finally {
                this.logger.info("disconnect DB, because no longer used");
                this.dbConfig.disconnect();
            }
        }
    }

    private RequestTimeoutTask monitorTimeout(String requestId, int timeout) {
        RequestTimeoutTask task = new RequestTimeoutTask(requestId, this);
        synchronized (this.timers) {
            this.timerIndex += 1;
            if (this.timerIndex >= this.timers.length) {
                this.timerIndex = 0;
            }
            this.timers[this.timerIndex].schedule(task, timeout * 1000);
        }
        return task;
    }

    public UssdTimeoutTask monitorUssdGwTimeout(String transId, int timeout) {
        UssdTimeoutTask task = new UssdTimeoutTask(transId, this.tranMgr);
        synchronized (this.timers) {
            this.timerIndex += 1;
            if (this.timerIndex >= this.timers.length) {
                this.timerIndex = 0;
            }
            this.timers[this.timerIndex].schedule(task, timeout * 1000);
        }
        return task;
    }
}
