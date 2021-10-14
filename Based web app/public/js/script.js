const video = document.getElementById('video')
const cameraOutput = document.getElementById("camera--output");
const cameraSensor = document.getElementById("camera--sensor");
const cameraTrigger = document.getElementById("camera--trigger")
const savedBtn = document.getElementById('saved_btn');

var curX = 0;
var curY = 0;
var curWidth = 0;
var curHeight = 0;

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

        // 얼굴이 감지되지 않음.
        if(typeof(resizedDetections[0]) == 'undefined')
            return;

        const curJson = resizedDetections[0];

        curX = curJson.alignedRect._box._x;
        curY = curJson.alignedRect._box._y;
        curWidth = curJson.alignedRect._box._width;
        curHeight = curJson.alignedRect._box._height;

        // console.log(curX + " " + curY + " " + curWidth + " " + curHeight);
    }, 100)
})

$(function () {
    $('#camera--trigger').click(function () {
        if(curX == 0 && curY == 0 && curWidth == 0 && curHeight == 0){
            alert("얼굴이 감지될 때까지 기다려주세요!");
            return;
        }

        cameraSensor.width = video.videoWidth;
        cameraSensor.height = video.videoHeight;
        cameraSensor.getContext("2d").drawImage(video, 0, 0);

        cameraOutput.src = cameraSensor.toDataURL("image/webp");
        // cameraOutput.src = cameraSensor.toDataURL();     // PNG는 파일이 너무 커서 안됨.
        cameraOutput.classList.add("taken");

        curX = curY = curWidth = curHeight = 0; // pos init
    });
});

$(function () {
    $('#store_btn').click(function () {
        // 촬영이 안 되었을 경우, 저장버튼 안 눌리게
        if(cameraOutput.src == 'https://:0/') {
            alert("우선 사진을 촬영해주세요!");
            return;
        }

        var dataURL = cameraOutput.src

        if (confirm("저장하시겠습니까?") == true) {
            $.ajax({
                type:'post',
                url: '/upload_images',   //데이터를 주고받을 파일 주소
                data: {
                    img : dataURL,
                    name : "test"
                },
                dataType:'json',
                success : function(data){
                    alert("send data !");
                },
                error : function(err) {
                    alert("failed : " + err);
                }
            });
        }
        else {
            return;
        }
    });
});