// import
const path = require("path");
var express = require('express');
var fs = require('fs');
const https = require('https');

var options = require('./openssl/config').options;

// 함수 저장
var app = express();
const port = 16984;

var bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

var publicPath = path.join(__dirname, 'public');
app.use(express.static(publicPath));

const server = https.createServer({
    key: options.key,
    cert: options.cert,
}, app);

// 서버 오픈
server.listen(port, () => console.log('express server running on port ' + port));

// __dirname : 요청하고자 하는 파일의 경로 단축
app.get('/', function (req, res) {
    console.log("start the camera APIs.")
    res.sendFile('public/index.html');
})

function getCurTime() {
    var today = new Date();

    var year = today.getFullYear();
    var month = ('0' + (today.getMonth() + 1)).slice(-2);
    var day = ('0' + today.getDate()).slice(-2);

    var hours = ('0' + today.getHours()).slice(-2);
    var minutes = ('0' + today.getMinutes()).slice(-2);
    var seconds = ('0' + today.getSeconds()).slice(-2);

    return year + month + day + "_" + hours + minutes + seconds;
}

function mkdir(dirPath) {
    const isExists = fs.existsSync( dirPath );
    if( !isExists ) {
        fs.mkdirSync( dirPath, { recursive: true } );
    }
}

var uploadPath = __dirname + '/images/';
// Upload to server
app.post('/upload_images', function (req, res) {

    console.log("upload_images POST");

    var dataUrl = req.body.img;
    var userName = req.body.name;

    mkdir(uploadPath + userName);
    var user_dir = uploadPath + userName + "/";
    var fileName = getCurTime();

    var matches = dataUrl.match(/^data:.+\/(.+);base64,(.*)$/);
    var ext = matches[1];
    var base64_data = matches[2];
    var buffer = new Buffer(base64_data, 'base64');

    fs.writeFile(user_dir + fileName + '.jpg', buffer, function (err) {
        if(err) throw err;
        
        res.status(200).json({status:"ok"})
        console.log('done');
    });
});