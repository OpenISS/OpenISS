const soap = require('soap');
const url = 'http://localhost:9090/openiss?wsdl';
const args = {};

soap.createClient(url, function(err, client) {
    client.getFrame(args, function(err, result) {
        if(err) {
            console.log("got error");
            console.log(err);
        }
        else {
            console.log('got result');
            console.log(result);
        }
    });
});