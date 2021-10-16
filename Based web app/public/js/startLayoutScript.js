$(function () {
    $('#agree').click(function () {
        var url = '../loginLayout.html';
        $(location).attr('href', url);
    });
});

$(function () {
    $('#disagree').click(function () {
        alert("미동의 시 서비스 이용이 불가합니다.");
        return;
    });
});

$(function () {
    $('#github').click(function () {
        var url = 'https://github.com/devLupin/Face-Recognition-using-JS-document/blob/master/README.md';
        $(location).attr('href', url);
    });
});