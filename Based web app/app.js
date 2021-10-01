// import
var express = require('express')

// 함수 저장
var app = express()

// 아래부터 3줄 폴더 채로 sendFile
var path = require('path');
var faceDetectionPath = path.join(__dirname, 'Face-Detection');
app.use(express.static(faceDetectionPath));

// 3000번 포트로 서버 오픈
app.listen(3000, function() {
    console.log("start! express server on port 3000")
})

// __dirname : 요청하고자 하는 파일의 경로 단축
app.get('/', function(req, res) {
    console.log("start the camera APIs.")
    res.sendFile(__dirname + "/Face-Detection/index.html")
})

app.get('/main', function(req, res) {
    res.sendFile(__dirname + "/public/main.html")
})

// public 디렉토리를 static으로 기억
// public 내부의 파일들을 localhost:3000/파일명으로 브라우저에서 불러옴
app.use(express.static('public'));