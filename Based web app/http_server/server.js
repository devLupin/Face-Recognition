const path = require("path");

var express = require('express')

var app = express()

var fileUploadPath = path.join(__dirname, 'public/uploadFile/');

// 3000번 포트로 서버 오픈
app.listen(3000, function() {
    console.log("start! express server on port 3000")
})

app.get('/uploadform', function(req, res) {
    console.log("Start the file upload.")
    res.sendFile(fileUploadPath + "index.html")
})