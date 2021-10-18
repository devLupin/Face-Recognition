$(function () {
    $('#findId').click(function () {
        var url = '../idFindLayout.html';
        $(location).attr('href', url);
    });
});

$(function () {
    $('#findPw').click(function () {
        var url = '../pwFindLayout.html';
        $(location).attr('href', url);
    });
});

$(function () {
    $('#createAccount').click(function () {
        var url = '../createAccount.html';
        $(location).attr('href', url);
    });
});

$(function () {
    $('#next').click(function () {
        var curID = $('#id').val();
        var curPW = $('#pw').val();

        if (curID == "" || curPW == "") {
            alert("공백이 있을 수 없습니다!");
            return;
        }

        $.ajax({
            type: 'post',
            url: '/login',   //데이터를 주고받을 파일 주소
            data: {
                id: curID,
                pw: curPW
            },
            dataType: 'json',

            // 로그인 성공 시 촬영 레이아웃으로 이동
            success: function (data) {
                if (data.ret == 'OK') {

                    var url = '../faceDetection.html'
                    var info = '?id=' + curID;
                    $(location).attr('href', url + info);
                }
                else {
                    alert('아이디 또는 패스워드가 일치하지 않습니다.');
                }
            },
            error: function (err) {
                alert('LOGIN fatal error');
            }
        });
    });
});