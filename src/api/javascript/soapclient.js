'use strict';

const express = require('express');

// Constants
const PORT = 8080;
const HOST = '0.0.0.0';
const soap = require('soap');
const url = 'http://localhost:9090/openiss?wsdl';

// App
const app = express();

app.get('/getFrame/:type', function(req, res) {
    if (req.params.type != 'color' && req.params.type != 'depth') {
        res.send("Invalid frame request");
        return;
    }

    soap.createClient(url, function(err, client) {
        let args = {type: req.params.type};
        client.getFrame(args, function(err, result) {
            if(err) {
                console.log("got error");
                console.log(err);
                res.send(err);
            }
            else {
                var img = new Buffer(result.return, 'base64');
//                console.log(img.toString());
                res.contentType('image/jpeg');
                res.end(img);
            }
        });
    });



});

app.get('/', (req, res) => {

    res.send('Hello World ');

});

app.listen(PORT, HOST);
console.log(`SOAP Client HTTP Service Running on http://${HOST}:${PORT}`);


