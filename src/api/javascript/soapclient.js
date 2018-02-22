const soap = require('soap');

const url = 'http://localhost:9090/openiss?wsdl';
let args = {type: 'color'};

soap.createClient(url, function(err, client) {
    client.getFrame(args, function(err, result) {
        if(err) {
            console.log("got error");
            console.log(err);
        }
        else {
            var img = new Buffer(result.return, 'base64');
            console.log(result.return);
            console.log(img.toString());
        }
    });
});