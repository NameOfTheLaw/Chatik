function auth() {
    $.ajax({
        method: "POST",
        url: "chat",
        dataType: 'json',
        data: { name: $('#name').val(), pass: $('#pass').val() , action: "auth"},
        success: function(data){
            $("#footer-text").empty();
            $.each(data, function(key, val) {
                $("<div>"+key+" "+val+"</div>").appendTo("#footer-text");
            });
            if (data.auth == "successful") {
                document.location.href = "chat.html";
            }
            if (data.auth == "failed") {
                //какой-то косяк
                $(".alert").remove();
                $('<div class="alert alert-danger alert-reg"><strong>Неудачно!</strong> '+data.cause+'</div>').appendTo("body");
            }
        }
    });
}
function deauth() {
    $.ajax({
        method: "POST",
        url: "chat",
        dataType: 'json',
        data: {action: "deauth"},
        success: function (data) {
            document.location.href = "index.html";
        }
    });
}
function reg() {
    $.ajax({
        method: "POST",
        url: "chat",
        dataType: 'json',
        data: { name: $('#reg_name').val(), pass: $('#reg_pass').val() , action: "reg"},
        success: function(data){
            $(".alert").remove();
            if (data.reg == "successful") {
                //$('<div class="alert alert-success"><strong>Успешно!</strong> Регистрация прошла успешно.</div>').alert();
                $('<div class="alert alert-success alert-reg"><strong>Успешно!</strong> Регистрация прошла успешно.</div>').appendTo("body");
            } else {
                $('<div class="alert alert-danger alert-reg"><strong>Неудачно!</strong> '+data.cause+'</div>').appendTo("body");
            }
            /*
            $("#footer-text").empty();
            $.each(data, function(key, val) {
                $("<div>"+key+" "+val+"</div>").appendTo("#footer-text");
            });
            */
        }
    });
}

function getUsers(){
    $.getJSON('chat?action=getusers', function(data) {
        $('#users-box').empty();
        $.each(data, function(key, val) {
            var ul = $('<ul class="list-unstyled"></ul>');
            $('<li>' + val + '</li>').appendTo(ul);
            ul.appendTo('#users-box');
        });
    });
}
function sendMessage() {
    $.ajax({
        method: "GET",
        url: "chat",
        dataType: 'json',
        data: { text: $('#message-input').val(), action: "sendmessage"},
        beforeSend: function () {
            $('#message-input').val("");
        },
        success: function () {
            getMessages();
        }
    });
}
function getMessages(){
    $.getJSON('chat?action=getmessages', function(data) {
        $.each(data, function(key, val) {
            if (!$("div").is("#message"+key)) {
                var message = $('<div class="row message-row" id="message' + key + '"></div>');
                $('<div class="col-xs-3 col-md-2 col-lg-2 message-author">' + val.user + '</div>').appendTo(message);
                $('<div class="col-xs-8 col-md-8 col-lg-8 message">' + val.text + '</div>').appendTo(message);
                message.appendTo('#chat-box');
            }
        });
    });
}
function checkAuth() {
    $.getJSON('chat?action=checkauth', function(data) {
        $("#footer-text").empty();
        if (data.auth == "on") {
            $('<div>' + "Вы авторизованы под ником " + data.name + '. <a class="text-info" onclick="deauth()">Выйти</a></div>').appendTo("#footer-text");
        }
        if (data.auth == "off") {
            $('<div>' + "Вы не авторизованы" + '</div>').appendTo("#footer-text");
            $('#chat-box').empty();
            $('<div class="text-muted">' + '<a class="text-info" href="index.html">Авторизуйтесь</a>, чтобы видеть сообщения ;)' + '</div>').appendTo('#chat-box');
        }
    });
}
function updateChat() {
    getUsers();
    getMessages();
}
