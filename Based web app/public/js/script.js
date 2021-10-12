const video = document.getElementById('video')
const cameraOutput = document.getElementById("camera--output");
const cameraSensor = document.getElementById("camera--sensor");
const cameraTrigger = document.getElementById("camera--trigger")
const savedBtn = document.getElementById('saved_btn');

Promise.all([
    faceapi.nets.tinyFaceDetector.loadFromUri('/js/models'),
    faceapi.nets.faceLandmark68Net.loadFromUri('/js/models'),
    faceapi.nets.faceRecognitionNet.loadFromUri('/js/models'),
    faceapi.nets.faceExpressionNet.loadFromUri('/js/models')
]).then(startVideo)

function startVideo() {
    navigator.getUserMedia(
        { video: {} },
        stream => video.srcObject = stream,
        err => console.error(err)
    )
}

video.addEventListener('play', () => {
    const canvas = faceapi.createCanvasFromMedia(video)
    document.body.append(canvas)
    const displaySize = { width: video.width, height: video.height }
    faceapi.matchDimensions(canvas, displaySize)
    setInterval(async () => {
        const detections = await faceapi.detectAllFaces(video, new faceapi.TinyFaceDetectorOptions()).withFaceLandmarks().withFaceExpressions()
        const resizedDetections = faceapi.resizeResults(detections, displaySize)
        canvas.getContext('2d').clearRect(0, 0, canvas.width, canvas.height)
        faceapi.draw.drawDetections(canvas, resizedDetections)
        faceapi.draw.drawFaceLandmarks(canvas, resizedDetections)
    }, 100)
})

$(function () {
    $('#camera--trigger').click(function () {
        cameraSensor.width = video.videoWidth;
        cameraSensor.height = video.videoHeight;
        cameraSensor.getContext("2d").drawImage(video, 0, 0);
        cameraOutput.src = cameraSensor.toDataURL("image/webp");
        // cameraOutput.src = cameraSensor.toDataURL();     // PNG는 파일이 너무 커서 안됨.
        cameraOutput.classList.add("taken");
    });
});

$(function () {
    $('#store_btn').click(function () {
        // 촬영이 안 되었을 경우, 저장버튼 안 눌리게
        // console.log('output : ' + cameraOutput.src);
        if(cameraOutput.src == 'https://:0/') {
            alert("우선 사진을 촬영해주세요!");
            return;
        }

        var dataURL = cameraOutput.src

        if (confirm("저장하시겠습니까?") == true) {
            $.ajax({
                type:'post',   //post 방식으로 전송
                url: '/upload_images',   //데이터를 주고받을 파일 주소
                data: {
                    img : dataURL,
                    name : "test"
                },
                dataType:'json',   //json 파일 형식으로 값을 담아온다.
                success : function(data){   //파일 주고받기가 성공했을 경우
                    alert("send data !");
                },
                error : function(err) {     // 실패 시
                    alert("failed : " + err);
                }
            });
        }
        else {
            return;
        }
    });
});