// import
const path = require("path");
var express = require('express');

// 함수 저장
var app = express()

var publicPath = path.join(__dirname, 'public');
app.use(express.static(publicPath));

// 3000번 포트로 서버 오픈
app.listen(3000, function() {
    console.log("start! express server on port 3000")
})

// __dirname : 요청하고자 하는 파일의 경로 단축
app.get('/', function(req, res) {
    console.log("start the camera APIs.")
    // res.sendFile(__dirname + "/public/index.html")
    res.sendFile('public/index.html');
})