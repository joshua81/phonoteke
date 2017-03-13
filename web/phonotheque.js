var Hapi = require('hapi');
var MongoClient = require('mongodb').MongoClient;
var Types = require('hapi').Types;
var server = new Hapi.Server();
var httpRequest = require('request');

// Use connect method to connect to the server
var articles = null;
MongoClient.connect('mongodb://localhost:27017/phonoteke', function(err, db) {
	console.log("MongoDB: Connected successfully to Phonoteke");
	articles = db.collection('articles');
});

server.connection({
	host:'localhost',
	port:8180
});

server.route({path:'/', method:'GET', handler:{file:'index.html'}});
server.route({path:'/doc', method:'GET', config:{handler:getDocument}});
server.route({path:'/doc/{id}', method:'GET', config:{handler:getDocument}});
/**server.route({path:'/doc/review', method:'GET', config:{handler:getReview}});
server.route({path:'/doc/review/{id}', method:'GET', config:{handler:getReview}});
server.route({path:'/doc/monograph', method:'GET', config:{handler:getMonograph}});
server.route({path:'/doc/monograph/{id}', method:'GET', config:{handler:getMonograph}});*/

server.route({path:'/{file}', method:'GET', handler:{directory:{path:'./'}}});
server.route({path:'/css/{file}', method:'GET', handler:{directory:{path:'./css'}}});
server.route({path:'/js/{file}', method:'GET', handler:{directory:{path:'./js'}}});
server.route({path:'/fonts/{file}', method:'GET', handler:{directory:{path:'./fonts'}}});
server.route({path:'/images/{file}', method:'GET', handler:{directory:{path:'./images'}}});

server.start((err) => {
    if (err) {throw err;}
    console.log('Server running at:', server.info.uri);
});

function getDocument(request, reply)
{
	if(request.params.id)
	{
		console.log('Finding document with id ' + request.params.id);
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
