'use strict';

const express = require('express');
const app = express();
const MongoClient = require('mongodb').MongoClient;
const Https = require('https');
const PORT = process.env.PORT || 8080;

var docs = null;
//const uri = "mongodb://localhost:27017/";
const uri = "mongodb+srv://mbeats:PwlVOgNqv36lvVXb@hbeats-31tc8.gcp.mongodb.net/test?retryWrites=true&w=majority";
const db = new MongoClient(uri, { useNewUrlParser: true, useUnifiedTopology: true });
db.connect(err => {
  docs = db.db("mbeats").collection("docs");
  console.log("Successfully Connected to MongoDB");
});

app.use('/images', express.static('images'));
app.use(express.static('web'));

app.get('/api/albums', async(req, res)=>{
	var result = await findDocs('album', req.query.p, req.query.q);
	res.send(result);
});

app.get('/api/albums/:id', async(req, res)=>{
	var result = await findDoc(req.params.id);
	res.send(result);
});

app.get('/api/albums/:id/links', async(req, res)=>{
	var result = await findLinks(req.params.id);
	res.send(result);
});

app.get('/api/artists', async(req, res)=>{
	var result = await findDocs('artist', req.query.p, req.query.q);
	res.send(result);
});

app.get('/api/artists/:id', async(req, res)=>{
	var result = await findDoc(req.params.id);
	res.send(result);
});

app.get('/api/artists/:id/links', async(req, res)=>{
	var result = await findLinks(req.params.id);
	res.send(result);
});

app.get('/api/artists/:id/events', async(req, res)=>{
	const json = await findEvents(req.params.id);
	res.send(json.resultsPage.results.event ? json.resultsPage.results.event : []);
});

app.get('/api/concerts', async(req, res)=>{
	var result = await findDocs('concert', req.query.p, req.query.q);
	res.send(result);
});

app.get('/api/concerts/:id', async(req, res)=>{
	var result = await findDoc(req.params.id);
	res.send(result);
});

app.get('/api/concerts/:id/links', async(req, res)=>{
	var result = await findLinks(req.params.id);
	res.send(result);
});

app.get('/api/interviews', async(req, res)=>{
	var result = await findDocs('interview', req.query.p, req.query.q);
	res.send(result);
});

app.get('/api/interviews/:id', async(req, res)=>{
	var result = await findDoc(req.params.id);
	res.send(result);
});

app.get('/api/interviews/:id/links', async(req, res)=>{
	var result = await findLinks(req.params.id);
	res.send(result);
});

app.get('/api/podcasts', async(req, res)=>{
	var result = await findDocs('podcast', req.query.p, req.query.q);
	res.send(result);
});

app.get('/api/podcasts/:id', async(req, res)=>{
	var result = await findDoc(req.params.id);
	res.send(result);
});

app.get('/api/podcasts/:id/links', async(req, res)=>{
	var result = await findLinks(req.params.id);
	res.send(result);
});

app.get('/*', (req,res)=>{
	res.sendFile(__dirname + '/web/index.html');});

app.listen(PORT, () => {
  console.log(`App listening on port ${PORT}`);
});
module.exports = app;

//-----------------------------------------------

async function findDoc(id) {
	console.log('Docs: id=' + id);
	var result = await docs.find({'id': id}).toArray();
	if(result.length == 1)
	{
		var doc = result[0];
		if(doc.type == 'album' || doc.type == 'podcast')
		{
			doc.tracks.forEach(function(track) 
			{
				if(track.title == null) {
					track.title = 'Unknown title';
				}
				if(track.youtube && track.youtube == 'UNKNOWN') {
					track.youtube = null;
				}
			});
		}
	}
	return result;
}

async function findDocs(t, p, q) {
	var result = null;
	if(q)
	{
		console.log('Docs: page=' + p + ', query=' + q + ', type=' + t);
		var page = Number(p) > 0 ? Number(p) : 0;
		var query = q;
		query = '.*' + query + '.*';
		query = query.split(' ').join('.*');
		if(t)
		{
			if(t != 'podcast')
			{
				result = await docs.find({$and: [{'type': t}, {$or: [{'artist': {'$regex': query, '$options' : 'i'}}, {'title': {'$regex': query, '$options' : 'i'}}]}]}).project({id: 1, type: 1, artist: 1, title: 1, authors: 1, cover: 1, coverL: 1, coverM: 1, coverS: 1, description: 1, vote: 1}).skip(page*12).limit(12).sort({"date":-1}).toArray();
			}
			else
			{
				result = await docs.find({$and: [{'type': t}, {$or: [{'artist': {'$regex': query, '$options' : 'i'}}, {'title': {'$regex': query, '$options' : 'i'}}, {'tracks.title': {'$regex': query, '$options' : 'i'}}]}]}).project({id: 1, type: 1, artist: 1, title: 1, authors: 1, cover: 1, coverL: 1, coverM: 1, coverS: 1, description: 1, vote: 1}).skip(page*12).limit(12).sort({"date":-1}).toArray();
			}
		}
	}
	else
	{
		console.log('Docs: page=' + p + ', type=' + t);
		var page = Number(p) > 0 ? Number(p) : 0;
		if(t)
		{
			result = await docs.find({'type': t}).project({id: 1, type: 1, artist: 1, title: 1, authors: 1, cover: 1, coverL: 1, coverM: 1, coverS: 1, description: 1, vote: 1}).skip(page*12).limit(12).sort({"date":-1}).toArray();
		}
	}
	return result;
}

async function findEvents(id) {
	console.log('Events: id=' + id);
	var result = await new Promise((resolve, reject) => {
		const skreq = Https.get('https://api.songkick.com/api/3.0/artists/mbid:' + id + '/calendar.json?apikey=1hOiIfT9pFTkyVkg', (skres) => {
			if (skres.statusCode < 200 || skres.statusCode > 299) {
				reject(new Error('Failed to load page, status code: ' + skres.statusCode));
			}
			const body = [];
			skres.on('data', (chunk) => body.push(chunk));
			skres.on('end', () => resolve(body.join('')));
		});
		skreq.on('error', (err) => reject(err))
	});
	return JSON.parse(result);
}

async function findLinks(id) {
	console.log('Links: id=' + id);
	var result = null;
	const doc = await docs.find({'id': id}).toArray();
	if(doc && doc[0])
	{
		var artists = [];
		if(typeof(doc[0].spartistid) != 'undefined' && doc[0].spartistid != null) {
			artists.push(doc[0].spartistid);
		}
		if(doc[0].type == 'podcast')
		{
			doc[0].tracks.forEach(function(track) {
				if(typeof(track.spartistid) != 'undefined' && track.spartistid != null) {
					artists.push(track.spartistid);
				}
			});
		}
		var links = doc[0].links != null ? doc[0].links : [];
		var result = await docs.find({$or: [{'id': {'$in': links}}, {'spartistid': {'$in': artists}}, {'tracks.spartistid': {'$in': artists}}]}).project({id: 1, type: 1, artist: 1, title: 1, cover: 1, coverL: 1, coverM: 1, coverS: 1, vote: 1}).sort({"type":1, "artist":1, "year":-1}).toArray();
		result = result.filter(function(value, index, arr){
			return value.id != doc[0].id;
		});
	}
	return result;
}
