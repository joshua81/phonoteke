'use strict';

const Https = require('https')
const Hapi = require('hapi');
const Types = require('hapi').Types;
const MongoClient = require('mongodb').MongoClient;
const MongoServer = require('mongodb').Server;

//HTTP server
const Server = new Hapi.Server({host:'0.0.0.0', port:8180});
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
		https://api.songkick.com/api/3.0/artists/mbid:b7539c32-53e7-4908-bda3-81449c367da6/calendar.json?apikey=1hOiIfT9pFTkyVkg*/
		{method:'GET', path:'/images/{file}',
		config: {cors: {origin: ['*'], additionalHeaders: ['cache-control', 'x-requested-with']}},
		handler:(request, h) => {return h.file('./images/'+request.params.file);}},
		{method:'GET', path:'/api/docs',
		config: {cors: {origin: ['*'], additionalHeaders: ['cache-control', 'x-requested-with']}},
		handler:getDocs},
		{method:'GET', path:'/api/docs/{id}',
		config: {cors: {origin: ['*'], additionalHeaders: ['cache-control', 'x-requested-with']}},
		handler:getDocs},
		{method:'GET', path:'/api/docs/{id}/events',
		config: {cors: {origin: ['*'], additionalHeaders: ['cache-control', 'x-requested-with']}},
		handler:getEvents},
		{method:'GET', path:'/api/docs/{id}/links',
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
var docs = null;
MongoClient.connect('mongodb://localhost:27017/', function(err, db) {
	console.log("Connected successfully to MongoDB");
	docs = db.db('phonoteke').collection('docs');
});

async function getDocs(request, h)
{
	if(request.params.id)
	{
		console.log('Docs: id ' + request.params.id);
		const result = await docs.find({'id': request.params.id}).toArray();
		return result;
	}
	else if(request.query.q)
	{
		console.log('Docs: page ' + request.query.p + ', query ' + request.query.q);
		var page = Number(request.query.p) > 0 ? Number(request.query.p) : 0;
		var query = request.query.q;
		query = '.*' + query + '.*';
		query = query.split(' ').join('.*');
		const result = await docs.find({'artist': {'$regex': query, '$options' : 'i'}}).project({review: 0}).skip(page*12).limit(12).sort({"date":-1}).toArray();
		return result;
	}
	else
	{
		console.log('Docs: page ' + request.query.p);
		var page = Number(request.query.p) > 0 ? Number(request.query.p) : 0;
		const result = await docs.find().project({review: 0}).skip(page*12).limit(12).sort({"date":-1}).toArray();
		return result;
	}
}

async function getEvents(request, h)
{
	if(request.params.id)
	{
		console.log('Events: id ' + request.params.id);
		const doc = await docs.find({'id': request.params.id}).toArray();
		if(doc && doc[0] && doc[0].artistid)
		{
			const result = await new Promise((resolve, reject) => {
				const req = Https.get('https://api.songkick.com/api/3.0/artists/mbid:' + doc[0].artistid + '/calendar.json?apikey=1hOiIfT9pFTkyVkg', (res) => {
					if (res.statusCode < 200 || res.statusCode > 299) {
						reject(new Error('Failed to load page, status code: ' + res.statusCode));
					}
					const body = [];
					res.on('data', (chunk) => body.push(chunk));
					res.on('end', () => resolve(body.join('')));
				});
				req.on('error', (err) => reject(err))
			});
			var json = JSON.parse(result);
			if(json && json.resultsPage && json.resultsPage.results) {
				return json.resultsPage.results.event;
			}
		}
	}
	return [];
}

async function getLinks(request, h)
{
	if(request.params.id)
	{
		console.log('Links: id ' + request.params.id);
		const doc = await docs.find({'id': request.params.id}).toArray();
		if(doc && doc[0] && doc[0].links)
		{
			//const result = await docs.find({'id': {'$in': doc[0].links}}).project({review: 0, description: 0, links: 0}).sort({"type":1, "artist":1, "title":1}).toArray();
			const result = await docs.find({$or: [{'artistid': doc[0].artistid}, {'tracks.artistid': doc[0].artistid}]}).project({review: 0, description: 0, links: 0}).sort({"type":1, "artist":1, "title":1}).toArray();
			return result;
		}
		return [];
	}
}
