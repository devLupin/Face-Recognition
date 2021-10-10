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
        cameraOutput.classList.add("taken");
    });
});

$(function () {
    $('#store_btn').click(function () {
        // 촬영이 안 되었을 경우, 저장버튼 안 눌리게 하는 예외 처리 필요

        if (confirm("저장하시겠습니까?") == true) {
            $.ajax({
                type: 'POST',
                url: "./image-sender.php",
                data: { imgBase64: cameraOutput.src }
            }).done(function (response) {
                console.log('saved: ' + response);
            });
        }
        else {
            return;
        }
    });
});