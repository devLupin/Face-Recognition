$(function(){
	$('#submit').click(function(){   //submit 버튼을 클릭하였을 때
        let sendData = "message";
		$.ajax({
			type:'post',   //post 방식으로 전송
			url:'/test',   //데이터를 주고받을 파일 주소
			data: {
                name : "name",
                price : "price"
            },
			dataType:'json',   //html 파일 형식으로 값을 담아온다.
			success : function(data){   //파일 주고받기가 성공했을 경우. data 변수 안에 값을 담아온다.
                alert("send data !");
            },
            error : function(err) {
                alert("failed : " + err);
            }
		});
	});
});

