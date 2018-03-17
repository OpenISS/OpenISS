'use strict';

const express = require('express');

// Constants
const PORT = 3000;
const HOST = '0.0.0.0';
const soap = require('soap');
const url = 'http://localhost:9090/openiss?wsdl';
const fs = require('fs');
const views = __dirname + '/views/';


// App
const app = express();

app.get('/getFrame/:type', function(req, res, next) {
    if (req.params.type != 'color' && req.params.type != 'depth') {
        res.send("Invalid frame request");
        next();
    }

    soap.createClient(url, function(err, client) {
        let args = {type: req.params.type};
        client.getFrame(args, function(err, result) {
            if(err) {
                console.log("got error");
                console.log(err);
                res.send(err);
                next();
            }
            else {
                var img = new Buffer(result.return, 'base64');
                res.contentType('image/jpeg');
                res.end(img);
                next();
            }
        });
    });
});

// Usage: /mixFrame/yourLocalImage&type&operand
// Example: /mixFrame/example&color&+
// A copyright free image called "example" is provided for testing purposes
app.get('/mixFrame/:image&:type&:op', function(req, res, next) {

    // check parameters validity
    if (req.params.type != 'color' && req.params.type != 'depth') {
        res.send("Invalid frame request");
        next();
    }

    // local read of file to be sent
    var content = fs.readFileSync(req.params.image+".jpg", "base64");

    // create SOAP client and call mixFrame
    soap.createClient(url, function(err, client) {
        let args = {image: content, type: req.params.type, op: req.params.op};
        client.mixFrame(args, function(err, result) {
            if(err) {
                console.log("got error");
                console.log(err);
                res.send(err);
                next();
            }
            else {
                // convert response to jpg
                var img = new Buffer(result.return, 'base64');
                res.contentType('image/jpeg');
                res.end(img);
                next();
            }
        });
    });
});

app.get('/getFileName', function(req, res, next) {

    soap.createClient(url, function(err, client) {

        client.getFileName("", function(err, result) {
            if(err) {
                console.log("got error");
                console.log(err);
                res.send(err);
                next();
            }
            else {
                console.log(result);
                res.end(result.return);
                next();
            }
        });
    });



});

app.get('/doCanny/:filename', function(req, res, next) {

    soap.createClient(url, function(err, client) {
    	let args = {filename: req.params.filename};
        client.doCanny(args, function(err, result) {
            if(err) {
                console.log("got error");
                console.log(err);
                res.send(err);
                next();
            }
            else {
            	var img = new Buffer(result.return, 'base64');
                res.contentType('image/jpeg');
                res.end(img);
                next();
            }
        });
    });



});

app.get('/', (req, res) => {

    res.sendFile(views + "index.html");

});

app.listen(PORT, HOST);
console.log(`SOAP Client HTTP Service Running on http://${HOST}:${PORT}`);


