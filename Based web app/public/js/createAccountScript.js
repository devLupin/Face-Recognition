$(document).on("keyup", "phoneNum", function () {
    $(this).val($(this).val().replace(/[^0-9]/g, "").replace(/(^02|^0505|^1[0-9]{3}|^0[0-9]{2})([0-9]+)?([0-9]{4})$/, "$1-$2-$3").replace("--", "-"));
});

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

        if (confirm(
            "제출하시겠습니까?\n\n" +
            "아이디: " + curID + '\n' +
            "비밀번호: " + curPW + '\n' +
            "이름: " + curName + '\n' +
            "전화번호: " + curPhNum + '\n' +
            "이메일: " + curEmail + '\n') == true) {

        }
        else {
            return;
        }
    });
});