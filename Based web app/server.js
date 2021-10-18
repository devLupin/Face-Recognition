// import
const path = require("path");
var express = require('express');
var fs = require('fs');
const https = require('https');
var mysql = require('mysql');

var ssl_options = require('./config/ssl_config').options;
var db_options = require('./config/db_config').options;

// 함수 저장
var app = express();
const port = 16984;

var bodyParser = require('body-parser');
const { query } = require("express");
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

var publicPath = path.join(__dirname, 'public');
app.use(express.static(publicPath));

const server = https.createServer({
    key: ssl_options.key,
    cert: ssl_options.cert,
}, app);

var conn = mysql.createConnection({
    host: db_options.host,
    user: db_options.user,
    password: db_options.password,
    database: db_options.database
});

// 서버 오픈
server.listen(port, () => console.log('express server running on port ' + port));

app.get('/', function (req, res) {
    console.log("start layout !");
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
    const isExists = fs.existsSync(dirPath);
    if (!isExists) {
        fs.mkdirSync(dirPath, { recursive: true });
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
        if (err) throw err;

        res.status(200).json({ status: "ok" })
        console.log("[saved image] id: " + userName);
    });
});



/**
 * DB 관련 코드
 * 
 */

//db connect
conn.connect(function (err) {
    if (err)
        console.log("conn connect error: " + err);

    console.log(db_options.database + " connected!");
})

// table create
conn.query(db_options.CREATE, function (err, results, fields) {
    if (err) {
        console.log("CREATE error: " + err);
    }
    console.log("CREATE TABLE MEMBER");
});

// 회원가입
app.post('/createAccount', function (req, res) {
    var id = req.body.id;
    var pw = req.body.pw;
    var name = req.body.name;
    var email = req.body.email;
    var phnum = req.body.phnum;

    var queryStr =
        db_options.INSERT +
        "'" + name + "'," +
        "'" + id + "'," +
        "'" + pw + "'," +
        "'" + phnum + "'," +
        "'" + email + "'" +
        ");";

    conn.query(queryStr, function (err, results) {
        if (err) {
            if (err.code == 'ER_DUP_ENTRY') {
                res.status(200).json({ status: "duplicated" })
            }
            else {
                console.log("create err: " + err);
                res.status(404).json({ status: "fatal error" })
            }
        }

        else {
            res.status(200).json({ status: "ok" })
            console.log("[new member] id: " + id);
        }
    });
});

app.post('/find_id', function (req, res) {
    var email = req.body.email;
    var phnum = req.body.phnum;

    var queryStr =
        db_options.SELECT_WHERE_ID + "EMAIL=" + "'" + email + "'" + " AND PHNUM=" + "'" + phnum + "';";

    conn.query(queryStr, function (err, results, fields) {
        if (err) {
            console.log("FIND_ID error: " + err);
        }

        if (results.length > 0) {
            res.status(200).json({ ret: results[0].ID })
        }
        else {
            res.status(200).json({ ret: "not exist" })
        }
    });
});


app.post('/find_pw', function (req, res) {
    var id = req.body.id;
    var email = req.body.email;
    var phnum = req.body.phnum;

    var queryStr =
        db_options.SELECT_WHERE_PW +
        "ID=" + "'" + id + "'" + " AND " +
        "EMAIL=" + "'" + email + "'" + " AND " +
        "PHNUM=" + "'" + phnum + "';";

    conn.query(queryStr, function (err, results, fields) {
        if (err) {
            console.log("FIND_PW error: " + err);
        }

        if (results.length > 0) {
            res.status(200).json({ ret: results[0].PW })
        }
        else {
            res.status(200).json({ ret: "not exist" })
        }
    });
});

app.post('/login', function (req, res) {
    var id = req.body.id;
    var pw = req.body.pw;

    var queryStr =
        db_options.SELECT_WHERE_LOGIN +
        "ID=" + "'" + id + "'" + " AND " +
        "PW=" + "'" + pw + "';";

    conn.query(queryStr, function (err, results, fields) {
        if (err) {
            console.log("LOGIN error: " + err);
        }

        if (results.length > 0) {
            res.status(200).json({ ret: "OK" })
            console.log("[login member] id: " + id);
        }
        else {
            res.status(200).json({ ret: "not exist" })
        }
    });
});