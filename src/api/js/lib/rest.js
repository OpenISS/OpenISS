'use strict';

const request = require('request');
const baseUrl = 'http://localhost:8080';

exports.getFrame = function(args, cb) {

    var requestSettings = {
        url: baseUrl + '/rest/openiss/' + args.type,
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
        url: baseUrl + '/rest/openiss/' + args.type,
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


// PATCH call for enabling flags on the server
exports.enableFlag = function(args, cb) {
    console.log("Enabling flag: " + args.flag)
    var requestSettings = {
        url: baseUrl + '/rest/openiss' + args.flag,
        method: 'PATCH'
    };
    request(requestSettings, function (error, response, body) {
        cb(error, response, body);
    });
}

// DELETE call for disabling flags on the server
exports.disableFlag = function(args, cb) {
    console.log("Disabling flag: " + args.flag)

    var requestSettings = {
        url: baseUrl + '/rest/openiss' + args.flag,
        method: 'DELETE'
    };
    request(requestSettings, function (error, response, body) {
        cb(error, response, body);
    });
}