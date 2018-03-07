var conversationId = "e717d4f3-a03e-4e57-8af2-c493a2b6a6da";
var userName = 'CR';
var jwt;

function post(url, data) {
    return $.ajax({
        type: 'POST',
        url: url,
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': jwt
        },
        data: JSON.stringify(data)
    })
}

function appendMessage(message) {
    var fromNow = moment(message.time).format('HH:mm:ss');
    var $message = $(`<li class="clearfix">
        <div class="message-data ${message.from == userName ? 'align-left' : 'align-right'}">
        <span class="message-data-name">${message.from}</span>
        <span class="message-data-time">${fromNow}</span>
    </div>
    <div class="message ${message.from == userName ? 'my-message' : 'other-message float-right'}">
        ${message.message}
    </div>
    </li>`);
    var $messages = $('#messages');
    $messages.append($message);
    $messages.scrollTop($messages.prop("scrollHeight"));
}

function login() {
    $.ajax({
        type: 'POST',
        url: '/login',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        data: JSON.stringify({username: userName, password: userName})
    }).done(function (data, textStatus, jqXHR) {
        jwt = jqXHR.getResponseHeader("Authorization");
        getConversations(jwt);
        // getPreviousMessages(jwt);
    })
}

function getConversations(jwt) {
    $.ajax({
        type: 'GET',
        url: '/conversations',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': jwt
        }
    }).done(function (conversations) {
        conversationId = conversations[0]["conversationId"];
        getPreviousMessages(jwt)
        connectWebSocket()
    })
}

function getPreviousMessages(jwt) {
    $.ajax({
        type: 'GET',
        url: '/conversations/' + conversationId + '/messages',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': jwt
        }
    }).done(function (messages) {
        messages.forEach(appendMessage)
    })
}

function sendMessage() {
    var $messageInput = $('#messageInput');
    var message = {message: $messageInput.val()};
    $messageInput.val('');
    post('/conversations/' + conversationId + '/messages', message);
}

function onNewMessage(result) {
    var message = JSON.parse(result.body);
    appendMessage(message);
}

function connectWebSocket() {
    var socket = new SockJS('/messagesWS');
    stompClient = Stomp.over(socket);
    //stompClient.debug = null;
    stompClient.connect({}, (frame) => {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/' + conversationId, onNewMessage);
    });
}

login();