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
            }
        },
        error: function () {
            $("#footer-text").empty();
            $("<div>bad_ajax_request</div>").appendTo("#footer-text");
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
            $("#footer-text").empty();
            $.each(data, function(key, val) {
                $("<div>"+key+" "+val+"</div>").appendTo("#footer-text");
            });
        },
        error: function () {
            $("#footer-text").empty();
            $("<div>bad_ajax_request</div>").appendTo("#footer-text");
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
            $('#message-input').empty();
        }
    });
}
function getMessages(){
    $.getJSON('chat?action=getmessages', function(data) {
        $.each(data, function(key, val) {
            var message = $('<div class="row"></div>');
            $('<div class="col-xs-1" style="text-decoration-style: dashed">' + key + '</div>').appendTo(message);
            $('<div class="col-xs-8">' + val + '</div>').appendTo(message);
            message.appendTo('#chat-box');
        });
    });
}
function checkAuth() {
    $.getJSON('chat?action=checkauth', function(data) {
        $("#footer-text").empty();
        if (data.auth == "on") {
            $('<div>' + "Вы авторизованы под ником " + data.name + '</div>').appendTo("#footer-text");
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
/*
function getContacts() {
    $.getJSON('contacts?action=list', function(data) {
        $.each(data, function(key, val) {
            var tr = $('<tr></tr>');
            $.each(val, function(k, v) {
                $('<td>' + v + '</td>').appendTo(tr);
            });
            $('<td><a href="#" onclick="removeBtn(this,' + val['id'] + ')" title="Удалить"><i class="icon-trash"></i></a></td>').appendTo(tr);
            tr.appendTo('#mytable');
        });
    });
}

function removeBtn(obj, id) {
    if (confirm("Удалить контакт?")) {
        var parent = $(obj).parent().parent();
        $.getJSON('contacts?action=remove&id=' + id, function(data) {
            parent.fadeOut('slow', function() {
                $(this).remove();
            });
        });
    }
}

function addBtn() {
    $('form').submit(function() {
        var arr = $(this).serializeArray();
        $.getJSON('contacts?action=add&fio=' + arr[0].value + '&phone=' +
                arr[1].value + '&email=' + arr[2].value, function(data) {

            var tr = $('<tr></tr>');
            $('<td>' + data.id + '</td>').appendTo(tr);
            for (var i = 0; i <= 2; i++) {
                $('<td>' + arr[i].value + '</td>').appendTo(tr);
            }
            $('<td><a href="#" onclick="removeBtn(this,' + arr['id'] + ')" title="Удалить"><i class="icon-trash"></i></a></td>').appendTo(tr);
            tr.appendTo('#mytable');

        });
        $('#addcontact').modal('hide');
        return false;
    });
}
*/
