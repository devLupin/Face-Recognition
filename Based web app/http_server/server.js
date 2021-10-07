const path = require("path");
var express = require('express');

var app = express()

// 아래부터 세줄 json데이터 참조를 위해 필요
var bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

var fileUploadPath = path.join(__dirname, '/public/uploadFile/');
app.use(express.static(fileUploadPath));
const DIR = path.join(__dirname, '/public/');
app.use(express.static(DIR));

// 3000번 포트로 서버 오픈
app.listen(3000, function () {
    console.log("start! express server on port 3000")
})

app.get('/uploadform', function (req, res) {
    console.log("Start the file upload.")
    res.sendFile(fileUploadPath + "index.html")
})

app.post('/test', function(req, res) {
    var name = req.body.name;
    var price = req.body.price;
    console.log("Is send it?");
    console.log(name + " " + price);
})