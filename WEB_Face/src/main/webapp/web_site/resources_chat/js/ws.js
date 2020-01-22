var host = "ws://" + document.location.host + "/WEB_Face/sendmsg";
var wSocket = new WebSocket(host);
wSocket.binaryType = "arraybuffer";

var browserSupport = ("WebSocket" in window) ? true : false;
function initWebSocket() {

    if (browserSupport)
    {
        wSocket.onopen = function ()
        {
            console.log("******* Connection openned");
        };
    } else
    {
        alert("WebSocket is NOT supported by your Browser!");
    }

    wSocket.onmessage = function (evt)
    {
        onMessage(evt);
        console.log("****** onMessage event is UP *********");
    };

    wSocket.onclose = function (evt)
    {
        console.log("****** Connection closed!");
    };

    wSocket.onerror = function (evt) {
        onError(evt);
    };
}

function sendText() {
    var inputTextArea = document.getElementById("chatPanel:insertMSG").value;
    var myNickName = document.getElementById("chatPanel:myNickName").innerHTML;

    var objMsgWS = new Object();
    objMsgWS.source = myNickName;
    objMsgWS.destination = "all";
    objMsgWS.body = inputTextArea;
    objMsgWS.timestamp = "";
    objMsgWS.operation = "sendText";

    var objMsgWSToStr = JSON.stringify(objMsgWS);

    wSocket.send(objMsgWSToStr);

    console.log("****** sendText **************");
    console.log(objMsgWSToStr);
}

function onMessage(evt) {
    // called when a message is received
    console.log("****** onMessage **************");

    var receivedMsg = JSON.parse(evt.data);
    console.log(receivedMsg);

    if (receivedMsg.operation == "addUser") {
        welcomeMSG(receivedMsg.body, receivedMsg.timestamp);
        addUserOnlinePanel(receivedMsg.source);

    } else if (receivedMsg.operation == "logoutUser") {
        userLogoutMSG(receivedMsg.body, receivedMsg.timestamp);
        logoutUser(receivedMsg.source);

    } else {
        addMSGArea(receivedMsg.source, receivedMsg.body,
                receivedMsg.timestamp, receivedMsg.destination);
    }

    console.log("****** onMessage **************");
    console.log(receivedMsg);
}

function welcomeMSG(body, timestamp) {
    var tableChatMSG = document.getElementById("chatPanel:chatArea");
    var newRowChatMSG = tableChatMSG.insertRow(-1);
    var newCellChatMSG = newRowChatMSG.insertCell(-1);

    var chatMSG = timestamp + "\n" + body + "\n";
    newCellChatMSG.innerHTML = chatMSG;
    tableChatMSG.scrollTop = tableChatMSG.scrollHeight;

    console.log("****** welcomeMSG **************");
    console.log(tableChatMSG);
}

function userLogoutMSG(body, timestamp) {
    var tableChatMSG = document.getElementById("chatPanel:chatArea");
    var newRowChatMSG = tableChatMSG.insertRow(-1);
    var newCellChatMSG = newRowChatMSG.insertCell(-1);
    var chatMSG = timestamp + "\n" + body + "\n";

    newCellChatMSG.innerHTML = chatMSG;
    tableChatMSG.scrollTop = tableChatMSG.scrollHeight;

    console.log("****** userLogoutMSG **************");
    console.log(tableChatMSG);
}

function addUserOnlinePanel(user) {
    if (user != null) {
        var table = document.getElementById("chatPanel:allOnlines");
        // -1 = end of table
        var newRow = table.insertRow(-1);
        newRow.id = user; // atribuindo id na linha
        var newCell = newRow.insertCell(-1);
        var newUser = document.createTextNode(user);
        newCell.appendChild(newUser);

    }

    console.log("****** addUserOnlinePanel **************");
    console.log(user);
}

function addMSGArea(user, body, timestamp, destination) {
    var tableChatMSG = document.getElementById("chatPanel:chatArea");
    var newRowChatMSG = tableChatMSG.insertRow(-1);
    var newCellChatMSG = newRowChatMSG.insertCell(-1);
    var chatMSG = "";

    if (destination != "all") {
        // Unicast
        chatMSG = "<b>" + user + ", " + timestamp + "<br />" + body + "</b>";
    } else {
        // Broadcast
        chatMSG = user + ", " + timestamp + "<br />" + body;
    }
    newCellChatMSG.innerHTML = chatMSG;
    tableChatMSG.scrollTop = tableChatMSG.scrollHeight;

    document.getElementById("chatPanel:insertMSG").value = "";
    document.getElementById("chatPanel:insertMSG").focus();

    console.log("****** addUserOnlinePanel **************");
    console.log(chatMSG);

}

function logoutUser(user) {
    var userOnList = document.getElementById(user);
    userOnList.remove(userOnList);
    wSocket.onclose("logout: " + user);

    console.log("****** logoutUser **************");
    console.log(user);
}

function onError(evt) {
    var tableChatMSG = document.getElementById("chatPanel:chatArea");
    var newRowChatMSG = tableChatMSG.insertRow(-1);
    var newCellChatMSG = newRowChatMSG.insertCell(-1);
    var chatMSG = '<span style="color: red;">ERROR: </span> ' + evt.data;
    newCellChatMSG.innerHTML = chatMSG;
    tableChatMSG.scrollTop = tableChatMSG.scrollHeight;
    document.getElementById("chatPanel:insertMSG").value = "";
    document.getElementById("chatPanel:insertMSG").focus();
    wSocket.onclose(evt);

    console.log("****** onError **************");
    console.log(tableChatMSG);
}
