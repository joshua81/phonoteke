'use strict';

const express = require('express');
const robots = require('express-robots-txt');
const cookieParser = require('cookie-parser');
const app = express();
const request = require('request');
const MongoClient = require('mongodb').MongoClient;
const PORT = process.env.PORT || 8080;
const PAGE_SIZE = 20;

const uri = "mongodb+srv://mbeats:PwlVOgNqv36lvVXb@hbeats-31tc8.gcp.mongodb.net/test?retryWrites=true&w=majority";
const client_id = 'a6c3686d32cb48d4854d88915d3925be';
const client_secret = '46004c8b1a2b4c778cb9761ace300b6c';
const redirect_uri = 'https://humanbeats.appspot.com/api/login/spotify';
const songkick_id = '1hOiIfT9pFTkyVkg';

var docs = null;
var authors = null;
var stats = null;
const client = new MongoClient(uri, { useNewUrlParser: true, useUnifiedTopology: true });
client.connect(err => {
	docs = client.db("mbeats").collection("docs");
	authors = client.db("mbeats").collection("authors");
	stats = client.db("mbeats").collection("stats");
	console.log("Successfully Connected to MongoDB");
	app.listen(PORT, () => {
		console.log(`App listening on port ${PORT}`);
	});
});

app.set('views', './template');
app.set('view engine', 'ejs');
app.use('/images', express.static('images'));
app.use('/css', express.static('css'));
app.use('/js', express.static('js'));
app.use(express.static('web'));
app.use(cookieParser());
app.use(robots({
	UserAgent: '*',
	Disallow: '/',
	CrawlDelay: '5'
}));

app.get('/api/stats', async(req, res)=>{
	var result = await stats.find({'source': null}).toArray();
	res.send(result[0]);
});

app.get('/api/stats/:source', async(req, res)=>{
	var result = await stats.find({'source': req.params.source}).toArray();
	res.send(result[0]);
});

app.get('/api/albums', async(req, res)=>{
	var result = await findDocs('album', req.query.p, req.query.q, req.query.s);
	res.send(result);
});

app.get('/api/artists', async(req, res)=>{
	var result = await findDocs('artist', req.query.p, req.query.q, req.query.s);
	res.send(result);
});

app.get('/api/concerts', async(req, res)=>{
	var result = await findDocs('concert', req.query.p, req.query.q, req.query.s);
	res.send(result);
});

app.get('/api/interviews', async(req, res)=>{
	var result = await findDocs('interview', req.query.p, req.query.q, req.query.s);
	res.send(result);
});

app.get('/api/podcasts', async(req, res)=>{
	var result = await authors.find().project({source: 1, name: 1, cover: 1, lastEpisodeDate: 1}).sort({"lastEpisodeDate":-1, "name":1}).toArray();
	res.send(result);
});

app.get('/api/podcasts/:source', async(req, res)=>{
	var result = await authors.find({'source': req.params.source}).project({source: 1, name: 1, cover: 1, lastEpisodeDate: 1}).toArray();
	res.send(result);
});

app.get('/api/podcasts/:source/episodes', async(req, res)=>{
	var result = await findDocs('podcast', req.query.p, req.query.q, req.params.source);
	res.send(result);
});

app.get('/api/events/:id', async(req, res)=>{
	const json = await findEvents(req.params.id);
	res.send(json && json.resultsPage.results.event ? json.resultsPage.results.event : []);
});

app.get('/api/login', async(req, res)=>{
	console.log('Login to Spotify');
	res.redirect('https://accounts.spotify.com/authorize?' +
			'response_type=code&' + 
			'scope=user-library-read%20user-library-modify%20user-read-private%20user-read-playback-state%20user-modify-playback-state&' + 
			'client_id=' + client_id + '&' + 
			'redirect_uri=' + redirect_uri);
});

app.get('/api/login/spotify', async(req,res)=>{
	if(req.query.error) {
		console.log('Login to Spotify failed: ' + req.query.error);
		res.send(req.query.error);
	}
	else if(req.query.code) {
		console.log('Login to Spotify done: ' + req.query.code);
		var options = {
				url: 'https://accounts.spotify.com/api/token',
				form: {
					code: req.query.code,
					redirect_uri: redirect_uri,
					grant_type: 'authorization_code'
				},
				headers: {
					'Authorization': 'Basic ' + Buffer.from(client_id + ':' + client_secret).toString('base64')
				},
				json: true
		};
		request.post(options, function(error, response, body) {
			if (!error && response.statusCode === 200) {
				var access_token = body.access_token;
				var refresh_token = body.refresh_token;
				res.cookie('spotify-token', access_token);
				res.cookie('spotify-refresh-token', refresh_token);
			}
			res.redirect('/');
		});
	}
});

app.get('/api/login/refresh', async(req,res)=>{
	var options = {
		url: 'https://accounts.spotify.com/api/token?response_type=code',
		form: {
			refresh_token: req.cookies['spotify-refresh-token'],
			grant_type: 'refresh_token'
		},
		headers: {
			'Authorization': 'Basic ' + Buffer.from(client_id + ':' + client_secret).toString('base64')
		},
		json: true
	};
	request.post(options, function(error, response, body) {
		console.log('Refresh: %j', body);
		if (!error && response.statusCode === 200) {
			var access_token = body.access_token;
			res.cookie('spotify-token', access_token);
			res.send();
		}
		else {
			res.status(response.statusCode).send(body);
		}
	});
});

app.get('/api/:id', async(req, res)=>{
	var result = await findDoc(req.params.id);
	res.send(result);
});

app.get('/api/:id/links', async(req, res)=>{
	var result = await findLinks(req.params.id);
	res.send(result);
});

app.get('/episodes/:id', async(req,res)=>{
	var doc = await docs.find({'id': req.params.id}).project({artist: 1, title: 1, type: 1, cover: 1, coverM: 1, description: 1}).toArray();
	if(doc && doc[0]) {
		res.render('index', { 
			title: doc[0].artist + ' - ' + doc[0].title,
			type: 'music:' + doc[0].type,
			url: 'https://humanbeats.appspot.com/episodes/' + req.params.id,
			cover: doc[0].coverM == null ? doc[0].cover : doc[0].coverM,
			description: doc[0].description });
	}
});

app.get('/:source', async(req,res)=>{
	if(req.params.source.endsWith('.css') || req.params.source.endsWith('.js')) {
		res.render('index', { 
			title: 'Human Beats',
			type: 'music',
			url: 'https://humanbeats.appspot.com/',
			cover: 'https://humanbeats.appspot.com/favicon.ico',
			description: 'Music designed by humans, assembled by robots' });
	}
	else {
		var doc = await authors.find({'source': req.params.source}).project({source: 1, name: 1, cover: 1}).toArray();
		if(doc && doc[0]) {
			res.render('index', { 
				title: 'Human Beats - ' + doc[0].name,
				type: 'music:podcast',
				url: 'https://humanbeats.appspot.com/' + req.params.source,
				cover: doc[0].cover,
				description: doc[0].name + ' podcasts'});
		}
	}
});

app.get('/*', (req,res)=>{
	res.render('index', { 
		title: 'Human Beats',
		type: 'music',
		url: 'https://humanbeats.appspot.com/',
		cover: 'https://humanbeats.appspot.com/favicon.ico',
		description: 'Music designed by humans, assembled by robots' });
});
module.exports = app;

//-----------------------------------------------

async function findDoc(id) {
	console.log('Docs: id=' + id);
	var result = await docs.find({'id': id}).toArray();
	if(result && result[0]) {
		// reset 'na' values
		const doc = result[0];
		if(doc.artistid == 'na') {
			doc.artistid = null;
		}
		if(doc.spartistid == 'na') {
			doc.spartistid = null;
		}
		if(doc.spalbumid == 'na') {
			doc.spalbumid = null;
		}
		if(doc.tracks) {
			doc.tracks.forEach(function(track) {
				if(track.spotify == 'na') {
					track.spotify = null;
					track.spartistid = null;
					track.spalbumid = null;
				}
				if(track.artistid == 'na') {
					track.artistid = null;
				}
				if(track.youtube == 'na') {
					track.youtube = null;
				}
			});
		}
	}
	return result;
}

async function findDocs(t, p, q, s) {
	var result = null;
	var nql = null;

	console.log('Docs: page=' + p + ', query=' + q + ', type=' + t + ', source=' + s);
	var page = Number(p) > 0 ? Number(p) : 0;
	if(q != null && t != null) {
		q = '.*' + q + '.*';
		q = q.split(' ').join('.*');
		if(s == null) {
			nql = {$and: [{'type': t}, {$or: [{'artist': {'$regex': q, '$options' : 'i'}}, {'title': {'$regex': q, '$options' : 'i'}}, {'tracks.title': {'$regex': q, '$options' : 'i'}}]}]};
		}
		else {
			nql = {$and: [{'type': t}, {'source': s}, {$or: [{'artist': {'$regex': q, '$options' : 'i'}}, {'title': {'$regex': q, '$options' : 'i'}}, {'tracks.title': {'$regex': q, '$options' : 'i'}}]}]};
		}
	}
	else if(q != null && t == null) {
		q = '.*' + q + '.*';
		q = q.split(' ').join('.*');
		nql = {$and: [{$or: [{'artist': {'$regex': q, '$options' : 'i'}}, {'title': {'$regex': q, '$options' : 'i'}}, {'tracks.title': {'$regex': q, '$options' : 'i'}}]}]};
	}
	else if(q == null && t != null) {
		if(s == null) {
			nql = {'type': t};
		}
		else {
			nql = {$and: [{'type': t}, {'source': s}]};
		}
	}
	
	result = await docs.find(nql).project({id: 1, type: 1, artist: 1, title: 1, cover: 1, coverL: 1, coverM: 1, coverS: 1, description: 1, date: 1}).skip(page*PAGE_SIZE).limit(PAGE_SIZE).sort({"date":-1}).toArray();
	return result;
}

async function findEvents(id) {
	console.log('Events: id=' + id);
	var result = null;
	if(id != null && id != 'na') {
		result = await new Promise((resolve, reject) => {
			var options = {
					url: 'https://api.songkick.com/api/3.0/artists/mbid:' + id + '/calendar.json?apikey=' + songkick_id,
					json: true
			};
			request.get(options, function(error, response, body) {
				if (!error && response.statusCode === 200) {
					resolve(body);
				}
				else {
					console.log('Error while executing findEvents()');
					resolve(null);
				}
			});
		});
	}
	return result;
}

async function findLinks(id) {
	console.log('Links: id=' + id);
	const doc = await docs.find({'id': id}).toArray();
	if(doc && doc[0]) {
		var artists = [];
		if(typeof(doc[0].spartistid) != 'undefined' && doc[0].spartistid != null && doc[0].spartistid != 'na') {
			artists.push(doc[0].spartistid);
		}
		if(doc[0].type == 'podcast') {
			doc[0].tracks.forEach(function(track) {
				if(typeof(track.spartistid) != 'undefined' && track.spartistid != null && track.spartistid != 'na') {
					artists.push(track.spartistid);
				}
			});
		}
		var links = doc[0].links != null ? doc[0].links : [];
		var albums = await docs.find({$and: [{'type': 'album'}, {$or: [{'id': {'$in': links}}, {'spartistid': {'$in': artists}}]}]}).project({id: 1, type: 1, artist: 1, title: 1, cover: 1, coverL: 1, coverM: 1, coverS: 1, date: 1, year: 1}).sort({"year":-1, "artist": 1}).toArray();
		albums = albums.filter(function(value, index, arr){
			return value.id != doc[0].id;
		});
		var podcasts = await docs.find({$and: [{'type': 'podcast'}, {'tracks.spartistid': {'$in': artists}}]}).project({id: 1, type: 1, artist: 1, title: 1, cover: 1, coverL: 1, coverM: 1, coverS: 1, date: 1, year: 1}).sort({"date":-1, "artist": 1}).limit(PAGE_SIZE).toArray();
		podcasts = podcasts.filter(function(value, index, arr){
			return value.id != doc[0].id;
		});
		return {albums: albums, podcasts: podcasts};
	}
	return {albums: [], podcasts: []};
}
