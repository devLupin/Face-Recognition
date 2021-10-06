// for uploadFile
var http = require('http');
var fs = require('fs');
var formidable = require('formidable');
const path = require("path");

// replace this with the location to save uploaded files
const upload_path = path.resolve(__dirname, "./img/");

async function uploadFile() {
    var form = new formidable.IncomingForm();
    form.parse(req, function (err, fields, files) {
        // oldpath : temporary folder to which file is saved to
        var oldpath = files.filetoupload.path;
        var newpath = upload_path + files.filetoupload.name;
        // copy the file to a new location
        fs.rename(oldpath, newpath, function (err) {
            if (err) throw err;

            alert('The file has been uploaded successfully.');
        });
    });
}