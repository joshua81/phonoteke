'use strict';

const Hapi = require('hapi');
const Types = require('hapi').Types;
const MongoClient = require('mongodb').MongoClient;
const MongoServer = require('mongodb').Server;

//HTTP server
const Server = new Hapi.Server({host:'localhost', port:8180});
const init = async () => {
	await Server.register(require('inert'));
	Server.route([
		{method:'GET', path:'/', handler:(request, h) => {return h.file('./index.html');}},
		{method:'GET', path:'/css/{file}', handler:(request, h) => {return h.file('./css/'+request.params.file);}},
		{method:'GET', path:'/js/{file}', handler:(request, h) => {return h.file('./js/'+request.params.file);}},
		{method:'GET', path:'/fonts/{file}', handler:(request, h) => {return h.file('./fonts/'+request.params.file);}},
		{method:'GET', path:'/images/{file}', handler:(request, h) => {return h.file('./images/'+request.params.file);}},
		{method:'GET', path:'/api/doc', handler:getDocument},
		{method:'GET', path:'/api/doc/{id}', handler:getDocument},
		{method:'GET', path:'/api/doc/review', handler:getReview},
		{method:'GET', path:'/api/doc/review/{id}', handler:getReview},
		{method:'GET', path:'/api/doc/monograph', handler:getMonograph},
		{method:'GET', path:'/api/doc/monograph/{id}', handler:getMonograph}]);
	await Server.start();
	console.log('Server running at: ${Server.info.uri}');
};
process.on('unhandledRejection', (err) => {
    console.log(err);
    process.exit(1);
});
init();

// Mongo DB
MongoClient.connect('mongodb://localhost:27017/phonoteke', function(err, db) {
	console.log("MongoDB: Connected successfully to Phonoteke");
	const articles = db.db('articles');
});
init();

function getDocument(request, h)
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

function getReview(request, h)
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

function getMonograph(request, h)
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
