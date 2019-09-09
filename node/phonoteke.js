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
		/*{method:'GET', path:'/',
		config: {cors: {origin: ['*'], additionalHeaders: ['cache-control', 'x-requested-with']}},
		handler:(request, h) => {return h.file('./index.html');}},
		{method:'GET', path:'/css/{file}',
		config: {cors: {origin: ['*'], additionalHeaders: ['cache-control', 'x-requested-with']}},
		handler:(request, h) => {return h.file('./css/'+request.params.file);}},
		{method:'GET', path:'/js/{file}',
		config: {cors: {origin: ['*'], additionalHeaders: ['cache-control', 'x-requested-with']}},
		handler:(request, h) => {return h.file('./js/'+request.params.file);}},
		{method:'GET', path:'/fonts/{file}',
		config: {cors: {origin: ['*'], additionalHeaders: ['cache-control', 'x-requested-with']}},
		handler:(request, h) => {return h.file('./fonts/'+request.params.file);}},
		{method:'GET', path:'/images/{file}',
		config: {cors: {origin: ['*'], additionalHeaders: ['cache-control', 'x-requested-with']}},
		handler:(request, h) => {return h.file('./images/'+request.params.file);}},*/
		{method:'GET', path:'/api/artists',
		config: {cors: {origin: ['*'], additionalHeaders: ['cache-control', 'x-requested-with']}},
		handler:getArtists},
		{method:'GET', path:'/api/artists/{id}',
		config: {cors: {origin: ['*'], additionalHeaders: ['cache-control', 'x-requested-with']}},
		handler:getArtists},
		{method:'GET', path:'/api/albums',
		config: {cors: {origin: ['*'], additionalHeaders: ['cache-control', 'x-requested-with']}},
		handler:getAlbums},
		{method:'GET', path:'/api/albums/{id}',
		config: {cors: {origin: ['*'], additionalHeaders: ['cache-control', 'x-requested-with']}},
		handler:getAlbums},
		{method:'GET', path:'/api/tracks/{id}',
		config: {cors: {origin: ['*'], additionalHeaders: ['cache-control', 'x-requested-with']}},
		handler:getTracks},
		{method:'GET', path:'/api/links/{id}',
		config: {cors: {origin: ['*'], additionalHeaders: ['cache-control', 'x-requested-with']}},
		handler:getLinks}]);
	await Server.start();
	console.log('Server running at: ${Server.info.uri}');
};
process.on('unhandledRejection', (err) => {
    console.log(err);
    process.exit(1);
});
init();

// Mongo DB
var artists = null;
var albums = null;
var tracks = null;
var links = null;
MongoClient.connect('mongodb://localhost:27017/', function(err, db) {
	console.log("Connected successfully to MongoDB");
	artists = db.db('phonoteke').collection('artists');
	albums = db.db('phonoteke').collection('albums');
	tracks = db.db('phonoteke').collection('tracks');
	links = db.db('phonoteke').collection('links');
});

async function getAlbums(request, h)
{
	if(request.params.id)
	{
		console.log('Album: id ' + request.params.id);
		const result = await albums.find({'id': request.params.id}).toArray();
		return result;
	}
	else if(request.query.q)
	{
		console.log('Album: page ' + request.query.p + ', query ' + request.query.q);
		var page = Number(request.query.p) > 0 ? Number(request.query.p) : 0;
		var query = request.query.q;
		const result = await albums.find({'artist': query}).skip(page*12).limit(12).sort({"date":-1}).toArray();
		return result;
	}
	else
	{
		console.log('Album: page ' + request.query.p);
		var page = Number(request.query.p) > 0 ? Number(request.query.p) : 0;
		const result = await albums.find({'date': {'$ne': 'null'}}).skip(page*12).limit(12).sort({"date":-1}).toArray();
		return result;
	}
}

async function getArtists(request, h)
{
	if(request.params.id)
	{
		console.log('Artist: id ' + request.params.id);
		const result = await artists.find({'id': request.params.id}).toArray();
		return result;
	}
	else if(request.query.q)
	{
		console.log('Artist: page ' + request.query.p + ', query ' + request.query.q);
		var page = Number(request.query.p) > 0 ? Number(request.query.p) : 0;
		var query = request.query.q;
		const result = await artists.find({'artist': query}).skip(page*12).limit(12).sort({"date":-1}).toArray();
		return result;
	}
	else
	{
		console.log('Artist: page ' + request.query.p);
		var page = Number(request.query.p) > 0 ? Number(request.query.p) : 0;
		const result = await artists.find().skip(page*12).limit(12).sort({"date":-1}).toArray();
		return result;
	}
}

async function getTracks(request, h)
{
	if(request.params.id)
	{
		console.log('Tracks: id ' + request.params.id);
		const result = await tracks.find({'id': request.params.id}).toArray();
		return result;
	}
}

async function getLinks(request, h)
{
	if(request.params.id)
	{
		console.log('Links: id ' + request.params.id);
		const result = await links.find({'id': request.params.id}).toArray();
		return result;
	}
}
