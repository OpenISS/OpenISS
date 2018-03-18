'use strict';

const request = require('request');
const baseUrl = 'http://localhost:8080';

exports.getFrame = function(args, cb) {

    var requestSettings = {
        url: baseUrl + '/rest/openiss/getImage/' + args.type,
        method: 'GET',
        encoding: null
    };
    request(requestSettings, function (error, response, body) {
        cb(error, response, body);
    });

}

exports.mixFrame = function(args, cb) {
    //@TODO not implemented yet
    var requestSettings = {
        url: baseUrl + '/rest/openiss/getImage/' + args.type,
        method: 'GET',
        encoding: null
    };
    request(requestSettings, function (error, response, body) {
        cb(error, response, body);
    });
}

exports.apiCall = function(args, cb) {
    var requestSettings = {
        url: baseUrl + '/rest/openiss/' + args.apiCall,
        method: 'GET'
    };
    request(requestSettings, function (error, response, body) {
        cb(error, response, body);
    });
}