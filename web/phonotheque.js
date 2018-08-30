'use strict';

const Hapi = require('hapi');
const Types = require('hapi').Types;
const MongoClient = require('mongodb').MongoClient;
const MongoServer = require('mongodb').Server;

//HTTP server
const Server = new Hapi.Server({host:'localhost', port:8180});
const init = async () => {
	await Server.register(require('inert'));

	Server.route({path:'/', method:'GET', handler: (request, h) => {return h.file('./index.html');}});
	Server.route({path:'/{file}', method:'GET', handler:(request, h) => {return h.file('./{file}');}});
	Server.route({path:'/css/{file}', method:'GET', handler:(request, h) => {return h.file('./css/{file}');}});
	Server.route({path:'/js/{file}', method:'GET', handler:(request, h) => {return h.file('./js/{file}');}});
	Server.route({path:'/fonts/{file}', method:'GET', handler:(request, h) => {return h.file('./fonts/{file}');}});
	Server.route({path:'/images/{file}', method:'GET', handler:(request, h) => {return h.file('./images/{file}');}});

	Server.route({path:'/api/doc', method:'GET', config:{handler:getDocument}});
	Server.route({path:'/api/doc/{id}', method:'GET', config:{handler:getDocument}});
	Server.route({path:'/api/doc/review', method:'GET', config:{handler:getReview}});
	Server.route({path:'/api/doc/review/{id}', method:'GET', config:{handler:getReview}});
	Server.route({path:'/api/doc/monograph', method:'GET', config:{handler:getMonograph}});
	Server.route({path:'/api/doc/monograph/{id}', method:'GET', config:{handler:getMonograph}});

	await Server.start();
	console.log(`Server running at: ${Server.info.uri}`);
};
process.on('unhandledRejection', (err) => {
    console.log(err);
    process.exit(1);
});
init();

// Mongo DB
var articles = null;
MongoClient.connect('mongodb://localhost:27017/phonoteke', function(err, db) {
	console.log("MongoDB: Connected successfully to Phonoteke");
	articles = db.db('articles');
});

function getDocument(request, reply)
{
	if(request.params.id)
	{
		console.log('Document: document with id ' + request.params.id);
		articles.find({'id': request.params.id}).toArray(function(err, docs) {
			reply(docs);
		});
	}
	else
	{
		var page = Number(request.query.page) > 0 ? Number(request.query.page) : 0;
		articles.find().skip(page*10).limit(10).sort({"creationDate":-1}).toArray(function(err, docs) {
			reply(docs);
		});
	}
}

function getReview(request, reply)
{
	if(request.params.id)
	{
		console.log('Review: document with id ' + request.params.id);
		articles.find({'id': request.params.id}).toArray(function(err, docs) {
			reply(docs);
		});
	}
	else
	{
		var page = Number(request.query.page) > 0 ? Number(request.query.page) : 0;
		articles.find({'type': 'REVIEW'}).skip(page*10).limit(10).sort({"creationDate":-1}).toArray(function(err, docs) {
			reply(docs);
		});
	}
}

function getMonograph(request, reply)
{
	if(request.params.id)
	{
		console.log('Monograph: document with id ' + request.params.id);
		articles.find({'id': request.params.id}).toArray(function(err, docs) {
			reply(docs);
		});
	}
	else
	{
		var page = Number(request.query.page) > 0 ? Number(request.query.page) : 0;
		articles.find({'type': 'MONOGRAPH'}).skip(page*10).limit(10).sort({"creationDate":-1}).toArray(function(err, docs) {
			reply(docs);
		});
	}
}
