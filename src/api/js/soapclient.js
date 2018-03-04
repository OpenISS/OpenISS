'use strict';

const express = require('express');

// Constants
const PORT = 8080;
const HOST = '0.0.0.0';
const soap = require('soap');
const url = 'http://localhost:9090/openiss?wsdl';
const fs = require('fs');


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

app.get('/mixFrame/:image&:type&:op', function(req, res, next) {
    if (req.params.type != 'color' && req.params.type != 'depth') {
        res.send("Invalid frame request");
        next();
    }

    var content = fs.readFileSync(req.params.image+".jpg", "base64");


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
                var img = new Buffer(result.return, 'base64');
//                console.log(img.toString());
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

app.get('/', (req, res) => {

    res.send('Hello World ');

});

app.listen(PORT, HOST);
console.log(`SOAP Client HTTP Service Running on http://${HOST}:${PORT}`);


