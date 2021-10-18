const video = document.getElementById('video')
const cameraOutput = document.getElementById("camera--output");
const cameraSensor = document.getElementById("camera--sensor");
const cameraTrigger = document.getElementById("camera--trigger")
const savedBtn = document.getElementById('saved_btn');
const usage = document.getElementById('usage');

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
        if (typeof (resizedDetections[0]) == 'undefined')
            return;

        const curJson = resizedDetections[0];

        curX = curJson.detection._box._x;
        curY = curJson.detection._box._y;
        curWidth = curJson.detection._box._width;
        curHeight = curJson.detection._box._height;

        // console.log(curX + " " + curY + " " + curWidth + " " + curHeight);
    }, 100)
})

// 사진촬영
$(function () {
    $('#camera--trigger').click(function () {
        if (curX == 0 && curY == 0 && curWidth == 0 && curHeight == 0) {
            alert("얼굴이 감지될 때까지 기다려주세요!");
            return;
        }

        cameraSensor.width = curWidth;
        cameraSensor.height = curHeight;
        cameraSensor.getContext("2d").drawImage(video, curX - 50, curY - 50, curWidth, curHeight, 0, 0, curWidth, curHeight);

        cameraOutput.src = cameraSensor.toDataURL("image/webp");
        // cameraOutput.src = cameraSensor.toDataURL();     // PNG는 파일이 너무 커서 안됨.
        cameraOutput.classList.add("taken");

        curX = curY = curWidth = curHeight = 0; // pos init
    });
});

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results == null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

$(function () {
    $('#store_btn').click(function () {
        // 촬영이 안 되었을 경우, 저장버튼 안 눌리게
        if (cameraOutput.src == 'https://:0/') {
            alert("우선 사진을 촬영해주세요!");
            return;
        }

        var userID = getParameterByName('id');

        var dataURL = cameraOutput.src

        if (confirm("저장하시겠습니까?") == true) {
            $.ajax({
                type: 'post',
                url: '/upload_images',   //데이터를 주고받을 파일 주소
                data: {
                    img: dataURL,
                    name: userID
                },
                dataType: 'json',
                success: function (data) {
                    alert("저장이 완료되었습니다 !");
                },
                error: function (err) {
                    alert("failed : " + err);
                }
            });
        }
        else {
            return;
        }
    });
});

$(function () {
    $('#usage').click(function () {
        var str =
            "1. 얼굴이 감지될 때까지 기다려주세요. \n" +
            "2. 정면을 바라보며 '사진촬영' 버튼을 클릭합니다. \n" +
            "3. '저장' 버튼을 클릭하면 해당 영역이 서버에 저장됩니다."

        alert(str);
    });
});