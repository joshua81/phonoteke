'use strict';

const express = require('express');
const cookieParser = require('cookie-parser');
const app = express();
var fs = require('fs');
const request = require('request');
const MongoClient = require('mongodb').MongoClient;
const PORT = process.env.PORT || 8080;

var docs = null;
//const uri = "mongodb://localhost:27017/";
const uri = "mongodb+srv://mbeats:PwlVOgNqv36lvVXb@hbeats-31tc8.gcp.mongodb.net/test?retryWrites=true&w=majority";
const client_id = 'a6c3686d32cb48d4854d88915d3925be';
const client_secret = '46004c8b1a2b4c778cb9761ace300b6c';
const redirect_uri = 'https://humanbeats.appspot.com/api/login/spotify';
const songkick_id = '1hOiIfT9pFTkyVkg';

const db = new MongoClient(uri, { useNewUrlParser: true, useUnifiedTopology: true });
db.connect(err => {
	docs = db.db("mbeats").collection("docs");
	console.log("Successfully Connected to MongoDB");
});

app.engine('ntl', function (filePath, options, callback) {
	fs.readFile(filePath, function (err, content) {
		if (err) return callback(err);
		var rendered = content.toString()
		.replace('#title#', options.title)
		.replace('#title#', options.title)
		.replace('#type#', options.type)
		.replace('#url#', options.url)
		.replace('#cover#', options.cover)
		.replace('#description#', options.description);
		return callback(null, rendered);
	});
})
app.set('views', './template')
app.set('view engine', 'ntl')
app.use('/images', express.static('images'));
app.use('/css', express.static('css'));
app.use('/js', express.static('js'));
app.use('/html', express.static('html'));
app.use(express.static('web'));
app.use(cookieParser());

app.get('/api/docs', async(req, res)=>{
	var result = await findDocs(req.query.t, req.query.p, req.query.q);
	res.send(result);
});

app.get('/api/docs/albums', async(req, res)=>{
	var result = await findDocs('album', req.query.p, req.query.q);
	res.send(result);
});

app.get('/api/docs/artists', async(req, res)=>{
	var result = await findDocs('artist', req.query.p, req.query.q);
	res.send(result);
});

app.get('/api/docs/concerts', async(req, res)=>{
	var result = await findDocs('concert', req.query.p, req.query.q);
	res.send(result);
});

app.get('/api/docs/interviews', async(req, res)=>{
	var result = await findDocs('interview', req.query.p, req.query.q);
	res.send(result);
});

app.get('/api/docs/podcasts', async(req, res)=>{
	var result = await findDocs('podcast', req.query.p, req.query.q);
	res.send(result);
});

app.get('/api/docs/:id', async(req, res)=>{
	var result = await findDoc(req.params.id);
	res.send(result);
});

app.get('/api/docs/:id/links', async(req, res)=>{
	var result = await findLinks(req.params.id);
	res.send(result);
});

app.get('/api/events/:id', async(req, res)=>{
	const json = await findEvents(req.params.id);
	res.send(json.resultsPage.results.event ? json.resultsPage.results.event : []);
});

app.get('/api/login', async(req, res)=>{
	console.log('Login to Spotify');
	res.redirect('https://accounts.spotify.com/authorize?' +
			'response_type=code&' + 
			'scope=user-read-private%20user-read-playback-state%20user-modify-playback-state&' + 
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

app.get('/docs/:id', async(req,res)=>{
	var docs = await findDocSnippet(req.params.id);
	if(docs && docs[0]) {
		res.render('index', { 
			title: docs[0].artist + ' - ' + docs[0].title,
			type: 'music:' + docs[0].type,
			url: 'https://humanbeats.appspot.com/docs/' + req.params.id,
			cover: docs[0].coverM == null ? docs[0].cover : docs[0].coverM,
					description: docs[0].description });
	}
	else {
		res.render('index', { 
			title: 'Human Beats',
			type: '',
			url: 'https://humanbeats.appspot.com/',
			cover: 'https://humanbeats.appspot.com/images/logo.png',
			description: 'Human Beats' });
	}
})

app.get('/*', (req,res)=>{
	res.render('index', { title: 'Human Beats' });
})

app.listen(PORT, () => {
	console.log(`App listening on port ${PORT}`);
});
module.exports = app;

//-----------------------------------------------

async function findDocSnippet(id) {
	console.log('Docs: id=' + id);
	var result = await docs.find({'id': id}).project({id: 1, type: 1, artist: 1, title: 1, cover: 1, coverL: 1, coverM: 1, coverS: 1, description: 1}).toArray();
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
			});
		}
	}
	return result;
}

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
			});
		}
	}
	return result;
}

async function findDocs(t, p, q) {
	var result = null;
	if(q) {
		console.log('Docs: page=' + p + ', query=' + q + ', type=' + t);
		var page = Number(p) > 0 ? Number(p) : 0;
		var query = q;
		query = '.*' + query + '.*';
		query = query.split(' ').join('.*');
		if(t) {
			result = await docs.find({$and: [{'type': t}, {$or: [{'artist': {'$regex': query, '$options' : 'i'}}, {'title': {'$regex': query, '$options' : 'i'}}, {'tracks.title': {'$regex': query, '$options' : 'i'}}]}]}).project({id: 1, type: 1, artist: 1, title: 1, cover: 1, coverL: 1, coverM: 1, coverS: 1, description: 1}).skip(page*12).limit(12).sort({"date":-1}).toArray();
		}
		else {
			result = await docs.find({$and: [{$or: [{'artist': {'$regex': query, '$options' : 'i'}}, {'title': {'$regex': query, '$options' : 'i'}}, {'tracks.title': {'$regex': query, '$options' : 'i'}}]}]}).project({id: 1, type: 1, artist: 1, title: 1, cover: 1, coverL: 1, coverM: 1, coverS: 1, description: 1}).skip(page*12).limit(12).sort({"date":-1}).toArray();
		}
	}
	else {
		console.log('Docs: page=' + p + ', type=' + t);
		var page = Number(p) > 0 ? Number(p) : 0;
		if(t) {
			result = await docs.find({'type': t}).project({id: 1, type: 1, artist: 1, title: 1, cover: 1, coverL: 1, coverM: 1, coverS: 1, description: 1}).skip(page*12).limit(12).sort({"date":-1}).toArray();
		}
		else {
			result = await docs.find().project({id: 1, type: 1, artist: 1, title: 1, cover: 1, coverL: 1, coverM: 1, coverS: 1, description: 1}).skip(page*12).limit(12).sort({"date":-1}).toArray();
		}
	}
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
					console.log("Error while executing findEvents()");
					return null;
				}
			});
		});
	}
	return result;
}

async function findLinks(id) {
	console.log('Links: id=' + id);
	var result = null;
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
		result = await docs.find({$or: [{'id': {'$in': links}}, {'spartistid': {'$in': artists}}, {'tracks.spartistid': {'$in': artists}}]}).project({id: 1, type: 1, artist: 1, title: 1, cover: 1, coverL: 1, coverM: 1, coverS: 1, spartistid: 1}).sort({"type":1, "date":-1}).toArray();
		result = result.filter(function(value, index, arr){
			return value.id != doc[0].id;
		});
	}
	return result;
}
