function isNumber(s) {
    s += ''; // 문자열로 변환
    s = s.replace(/^\s*|\s*$/g, ''); // 좌우 공백 제거
    if (s == '' || isNaN(s)) return false;
    return true;
}

$(function () {
    $('#next').click(function () {
        var curID = $('#id').val();
        var curPhNum = $('#phoneNum').val();
        var curEmail = $('#email').val();

        if (curID == "" || curPhNum == "" || curEmail == "") {
            alert("모든 칸은 공백일 수 없습니다.")
            return;
        }

        if (!isNumber(curPhNum)) {
            alert("오직 숫자만 입력가능합니다.")
            return;
        }

        if (curEmail.indexOf("@") == -1 || curEmail.indexOf(".") == -1) {
            alert("올바르지 않은 이메일 형식입니다. \n 입력 예시) web@server.com")
            return;
        }

        $.ajax({
            type: 'post',
            url: '/find_pw',   //데이터를 주고받을 파일 주소
            data: {
                id: curID,
                email: curEmail,
                phnum: curPhNum
            },
            dataType: 'json',

            // 로그인 성공 시 촬영 레이아웃으로 이동
            success: function (data) {
                if (data.ret == 'not exist') {
                    alert("일치하는 계정이 없습니다.");
                }
                else {
                    alert("비밀번호: " + data.ret);

                    var url = '../loginLayout.html';
                    $(location).attr('href', url);
                }
            },
            error: function (err) {
                alert('find id fatal error');
            }
        });
    });
});