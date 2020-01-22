package com.tatsinktechnologic.bean.page.chat;

import com.tatsinktechnologic.bean.page.chat.websocket_client.ChatClient;
import com.tatsinktechnologic.bean.page.chat.websocket_client.ChatClientEndpoint;
import com.tatsinktechnologic.bean.page.chat.websocket_server.ChatServerEndpoint;
import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.entities.chat_sms.ChatGroup;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.EncodeException;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;

@ManagedBean(name = "chatController")
@SessionScoped
public class ChatController implements Serializable {

    private static final long serialVersionUID = -3770573459254222700L;

    private boolean createUserPanel;

    private String nickname;

    private String textMessage;

    private ChatGroup chatGrp;

    private List<String> allNicknames;

    private ChatClientEndpoint clientEndPoint;

    @Inject
    private Commun_Controller commun_controller;

    // GET AND SET
    public boolean isCreateUserPanel() {
        return createUserPanel;
    }

    public void setCreateUserPanel(boolean createUserPanel) {
        this.createUserPanel = createUserPanel;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List<String> getAllNicknames() {
        return allNicknames;
    }

    public void setAllNicknames(List<String> allNicknames) {
        this.allNicknames = allNicknames;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public ChatGroup getChatGrp() {
        return chatGrp;
    }

    public void setChatGrp(ChatGroup chatGrp) {
        this.chatGrp = chatGrp;
    }

    @PostConstruct
    public void init() {

        this.createUserPanel = true;
        this.nickname = null;
        this.allNicknames = ChatMemory.allOnlines;

        FacesContext facesContext = FacesContext.getCurrentInstance();
        String current_user = SecurityUtils.getSubject().getPrincipal().toString();

        HttpServletRequest req = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        String url = req.getRequestURL().toString();
        String ws_host = url.substring(0, url.length() - req.getRequestURI().length()).replace("http", "ws");

        clientEndPoint = new ChatClientEndpoint(ws_host + "/chatWS/sendmsg");
        chatGrp = commun_controller.getChatGroupByUser(current_user);
        if (chatGrp != null) {
            this.nickname = chatGrp.getGroup_channel().trim();
            try {
                if (StringUtils.isBlank(this.nickname)) {
                    facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Please provide CHANNEL of chat group of this user", ""));
                } else if (!this.allNicknames.contains(this.nickname)) {
                    this.createUserPanel = false;
                    this.allNicknames.add(this.nickname);

                    MessageWs msgWS = new MessageWs();
                    msgWS.setSource(this.nickname);
                    msgWS.setDestination("all");
                    msgWS.setBody("User " + this.nickname + " is online");
                    msgWS.setOperation("addUser");
                    msgWS.setTimestamp(new Date());

                    this.sendMsgWSBroadcast(msgWS);

                    ChatMemory.lastUserOnline = getNickname();

                } else {
                    facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Alias or Channel already exists", ""));
                }
            } catch (Exception e) {
            }
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Alias or Channel don't exists", ""));
        }

    }


//    public void sendText() {
//        try {
//            MessageWs msgWS = new MessageWs();
//            msgWS.setSource(this.nickname);
//            msgWS.setDestination("all");
//            msgWS.setBody(textMessage);
//            msgWS.setOperation("addUser");
//            msgWS.setTimestamp(new Date());
////            ChatClient.sendMsg(msgWS, clientEndPoint);
//            this.sendMsgWSBroadcast(msgWS);
//            // Memorizando o ultimo usuario online
//            // Para add o atributo na sessao WS
//            ChatMemory.lastUserOnline = getNickname();
//
//        } catch (Exception e) {
//        }
//    }
    public String addUser() throws IOException, EncodeException {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        this.nickname = this.nickname.trim();
        if (this.nickname.contains(" ")) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Please insert your nickname", ""));

            return null;
        } else if (!this.allNicknames.contains(this.nickname)) {
            this.createUserPanel = false;
            this.allNicknames.add(this.nickname);

            MessageWs msgWS = new MessageWs();
            msgWS.setSource(this.nickname);
            msgWS.setDestination("all");
            msgWS.setBody("User " + this.nickname + " is online");
            msgWS.setOperation("addUser");
            msgWS.setTimestamp(new Date());
//            ChatClient.sendMsg(msgWS, clientEndPoint);
            this.sendMsgWSBroadcast(msgWS);
            // Memorizando o ultimo usuario online
            // Para add o atributo na sessao WS
            ChatMemory.lastUserOnline = getNickname();
            return "chat.xhtml?faces-redirect=true";
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Nickname already exists", ""));
            return null;
        }
    }

    /**
     * Manda msg broadcast para todos os outros usuarios. Importante: A sessao
     * do WS ainda nao foi criada, quando o usuario envia a msg atraves do
     * metodo addUser.
     *
     * @param msgws JSON da MSG
     * @throws IOException
     * @throws EncodeException
     */
    private void sendMsgWSBroadcast(MessageWs msgws) throws IOException, EncodeException {
        List<Session> sessions = ChatServerEndpoint.getSessions();
        for (Session s : sessions) {
            if (s.isOpen()) {
                s.getBasicRemote().sendObject(msgws);
            }
        }
    }

    /**
     * Metodo utilizado para testar o painel de usuarios onlines
     */
    public void insertManyUsers() {
        for (int i = 0; i < 100; i++) {
            this.allNicknames.add("teste" + i);
        }
    }

}
