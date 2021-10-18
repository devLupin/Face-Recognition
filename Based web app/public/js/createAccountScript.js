$(document).on("keyup", "phoneNum", function () {
    $(this).val($(this).val().replace(/[^0-9]/g, "").replace(/(^02|^0505|^1[0-9]{3}|^0[0-9]{2})([0-9]+)?([0-9]{4})$/, "$1-$2-$3").replace("--", "-"));
});

function isNumber(s) {
    s += ''; // 문자열로 변환
    s = s.replace(/^\s*|\s*$/g, ''); // 좌우 공백 제거
    if (s == '' || isNaN(s)) return false;
    return true;
}

$(function () {
    $('#submit').click(function () {
        var curID, curPW, curPWConfirm, curName, curPhNum, curEmail;
        curID = $('#id').val();
        curPW = $('#pw').val();
        curPWConfirm = $('#pwConfirm').val();
        curName = $('#name').val();
        curPhNum = $('#phoneNum').val();
        curEmail = $('#email').val();

        if(curID == "" || curPW == "" || curPWConfirm == "" ||
            curName == "" || curPhNum == "" || curEmail == ""){
                alert("모든 칸은 공백일 수 없습니다.")
                return;
            }
        if(curPW != curPWConfirm) {
            alert("비밀번호가 일치하지 않습니다.")
            return;
        }

        if(!isNumber(curPhNum)) {
            alert("오직 숫자만 입력가능합니다.")
            return;
        }

        if(curEmail.indexOf("@") == -1 || curEmail.indexOf(".") == -1){
            alert("올바르지 않은 이메일 형식입니다. \n 입력 예시) web@server.com")
            return;
        }

        if (confirm(
            "제출하시겠습니까?\n\n" +
            "아이디: " + curID + '\n' +
            "비밀번호: " + curPW + '\n' +
            "이름: " + curName + '\n' +
            "전화번호: " + curPhNum + '\n' +
            "이메일: " + curEmail + '\n') == true) {

                $.ajax({
                    type:'post',
                    url: '/createAccount',   //데이터를 주고받을 파일 주소
                    data: {
                        id : curID,
                        pw : curPW,
                        name : curName,
                        email : curEmail,
                        phnum : curPhNum
                    },
                    dataType:'json',
        
                    // 로그인 성공 시 촬영 레이아웃으로 이동
                    success : function(data){
                        if(data.status == 'duplicated') {
                            alert('입력된 아이디 또는 이메일로 가입된 계정이 이미 존재합니다.');
                        }
                        else{
                        alert("회원가입 성공!");
        
                        var url = '../loginLayout.html';
                        $(location).attr('href', url);
                        }
                    },
                    error : function(err) {
                        alert('create account fatal error');
                    }
                });
        }
        else {
            return;
        }
    });
});