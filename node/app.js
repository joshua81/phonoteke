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
app.get('/api/docs', async(req, res)=>{
	if(req.query.q)
	{
		console.log('Docs: page=' + req.query.p + ', query=' + req.query.q);
		var page = Number(req.query.p) > 0 ? Number(req.query.p) : 0;
		var query = req.query.q;
		query = '.*' + query + '.*';
		query = query.split(' ').join('.*');
		var result = await docs.find({$or: [{'artist': {'$regex': query, '$options' : 'i'}}, {'title': {'$regex': query, '$options' : 'i'}}, {'tracks.title': {'$regex': query, '$options' : 'i'}}]}).project({review: 0}).skip(page*12).limit(12).sort({"date":-1}).toArray();
		res.send(result);
	}
	else
	{
		console.log('Docs: page=' + req.query.p);
		var page = Number(req.query.p) > 0 ? Number(req.query.p) : 0;
		var result = await docs.find().project({review: 0}).skip(page*12).limit(12).sort({"date":-1}).toArray();
		res.send(result);
	}
});
app.get('/api/docs/:id', async(req, res)=>{
	console.log('Docs: id=' + req.params.id);
	const result = await docs.find({'id': req.params.id}).toArray();
	if(result.length == 1)
	{
		var doc = result[0];
		if(doc.type == 'album')
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
	res.send(result);
});
app.get('/api/docs/:id/links', async(req, res)=>{
  	console.log('Links: id=' + req.params.id);
	const doc = await docs.find({'id': req.params.id}).toArray();
	if(doc && doc[0])
	{
		var artists = [];
		if(typeof(doc[0].artistid) != 'undefined' && doc[0].artistid != null) {
			artists.push(doc[0].artistid);
		}
		if(doc[0].type == 'album')
		{
			doc[0].tracks.forEach(function(track) {
				if(typeof(track.artistid) != 'undefined' && track.artistid != null) {
					artists.push(track.artistid);
				}
			});
		}
		var links = doc[0].links != null ? doc[0].links : [];
		var result = await docs.find({$or: [{'id': {'$in': links}}, {'artistid': {'$in': artists}}, {'tracks.artistid': {'$in': artists}}]}).project({review: 0, description: 0, links: 0}).sort({"artist":1, "year":-1}).toArray();
		result = result.filter(function(value, index, arr){
			return value.id != doc[0].id;
		});
		res.send(result);
	}
});
app.get('/api/artists/:id/events', async(req, res)=>{
	console.log('Events: id=' + req.params.id);
	const result = await new Promise((resolve, reject) => {
		const skreq = Https.get('https://api.songkick.com/api/3.0/artists/mbid:' + req.params.id + '/calendar.json?apikey=1hOiIfT9pFTkyVkg', (skres) => {
			if (skres.statusCode < 200 || skres.statusCode > 299) {
				reject(new Error('Failed to load page, status code: ' + skres.statusCode));
			}
			const body = [];
			skres.on('data', (chunk) => body.push(chunk));
			skres.on('end', () => resolve(body.join('')));
		});
		skreq.on('error', (err) => reject(err))
	});
	const json = JSON.parse(result);
	res.send(json.resultsPage.results.event ? json.resultsPage.results.event : []);
});
app.get('/*', (req,res)=>{
	res.sendFile(__dirname + '/web/index.html');});

app.listen(PORT, () => {
  console.log(`App listening on port ${PORT}`);
});
module.exports = app;
