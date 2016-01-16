var Hapi = require('hapi');
var Types = require('hapi').Types;
var server = new Hapi.Server();
var mysql = require('mysql');
var httpRequest = require('request');

server.connection({
	host:'0.0.0.0',
	port:Number(process.argv[2]||8180)
});

server.route({path:'/', method:'GET', handler:{file:'index.html'}});
server.route({path:'/documents', method:'GET', config:{handler:getDocuments}});
server.route({path:'/documents/{id}', method:'GET', config:{handler:getDocuments}});
server.route({path:'/albums', method:'GET', config:{handler:getAlbums}});
server.route({path:'/albums/{id}', method:'GET', config:{handler:getAlbums}});
server.route({path:'/artists', method:'GET', config:{handler:getArtists}});
server.route({path:'/artists/{id}', method:'GET', config:{handler:getArtists}});
server.route({path:'/interviews', method:'GET', config:{handler:getInterviews}});
server.route({path:'/interviews/{id}', method:'GET', config:{handler:getInterviews}});
server.route({path:'/concerts', method:'GET', config:{handler:getConcerts}});
server.route({path:'/concerts/{id}', method:'GET', config:{handler:getConcerts}});
server.route({path:'/news', method:'GET', config:{handler:getNews}});
server.route({path:'/news/{id}', method:'GET', config:{handler:getNews}});
server.route({path:'/links', method:'GET', config:{handler:getLinks}});
server.route({path:'/links/{id}', method:'GET', config:{handler:getLinks}});
server.route({path:'/similar', method:'GET', config:{handler:getSimilar}});
server.route({path:'/similar/{id}', method:'GET', config:{handler:getSimilar}});

server.route({path:'/{file}', method:'GET', handler:{directory:{path:'./'}}});
server.route({path:'/css/{file}', method:'GET', handler:{directory:{path:'./css'}}});
server.route({path:'/js/{file}', method:'GET', handler:{directory:{path:'./js'}}});
server.route({path:'/fonts/{file}', method:'GET', handler:{directory:{path:'./fonts'}}});
server.route({path:'/images/{file}', method:'GET', handler:{directory:{path:'./images'}}});

server.start();

function getDBConnection() {
	var connection = mysql.createConnection({
		host:'localhost',
		user:'musicdb',
		password:'musicdb'
	});
	connection.connect();
	return connection;
}
var connection = getDBConnection();

function getInterviews(request, reply)
{
	reply('UNDER CONSTRUCTION!!');
}

function getConcerts(request, reply)
{
	var concerts = [];
	var queryStr = 'SELECT * FROM musicdb.event WHERE date > sysdate()';
	if (request.params.id)
	{
		queryStr += ' AND bandId = "' + request.params.id + '"';
	}
	queryStr += ' ORDER BY date ASC';
	if(request.query.page && Number(request.query.page) >= 0)
	{
		queryStr += ' LIMIT ' + (Number(request.query.page) * 12) + ', 12';
	}
	else
	{
		queryStr += ' LIMIT 0, 12';
	}

	console.log('Executing query: ' + queryStr);
	var query = connection.query(queryStr);
	query.on('error', function(err)
	{
		console.log('Error executing query: ' + queryStr + ' [' + err + ']');
	})
	.on('result', function(row)
	{
		var concert = JSON.parse(JSON.stringify(row));
		concerts.push(concert);
	})
	.on('end', function()
	{
		reply(concerts);
	});
}

function getNews(request, reply)
{
	reply('UNDER CONSTRUCTION!!');
}

// ARTISTS
function getArtists(request, reply)
{
	getDocumentsInternal(request, reply, 'MONOGRAPH');
}

// ALBUMS
function getAlbums(request, reply)
{
	getDocumentsInternal(request, reply, 'REVIEW');
}

// DOCUMENTS
function getDocuments(request, reply, type)
{
	getDocumentsInternal(request, reply, null);
}

function getDocumentsInternal(request, reply, type)
{
	var documents = [];
	var queryStr = 'SELECT * FROM musicdb.document WHERE 1=1';
	if(type)
	{
		queryStr += ' AND type = "' + type +'"';
	}
	if (request.params.id)
	{
		queryStr += ' AND id = "' + request.params.id + '"';
	}
	else if (request.query.search && request.query.search.trim().length > 3)
	{
		queryStr += ' AND (UPPER(band) LIKE UPPER("%' + request.query.search + '%") OR UPPER(album) LIKE UPPER("%' + request.query.search + '%"))';
	}
	else
	{
		if (request.query.band)
		{
			queryStr += ' AND UPPER(band) LIKE UPPER("%' + request.query.band + '%")';
		}
		if(request.query.album)
		{
			queryStr += ' AND UPPER(album) LIKE UPPER("%' + request.query.album + '%")'
		}
	}
	queryStr += ' ORDER BY creation_date DESC';
	if(request.query.page && Number(request.query.page) >= 0)
	{
		queryStr += ' LIMIT ' + (Number(request.query.page) * 12) + ', 12';
	}
	else
	{
		queryStr += ' LIMIT 0, 12';
	}

	console.log('Executing query: ' + queryStr);
	var query = connection.query(queryStr);
	query.on('error', function(err)
	{
		console.log('Error executing query: ' + queryStr + ' [' + err + ']');
	})
	.on('result', function(row)
	{
		var document = JSON.parse(JSON.stringify(row));
		// prevents to send unnecessary info to the client
		if (request.query.page)
		{
			document.content = null;
		}
		documents.push(document);
	})
	.on('end', function()
	{
		reply(documents);
	});
}

// LINKS
function getLinks(request, reply)
{
	if (request.query.id)
	{
		var links = [];
		var queryStr = 'SELECT * FROM musicdb.document d WHERE UPPER(d.band) = (SELECT UPPER(band) FROM musicdb.document WHERE id = "' + request.query.id + '") ORDER BY d.type';
		console.log('Executing query: ' + queryStr);
		var query = connection.query(queryStr);
		query.on('error', function(err)
		{
			console.log('Error executing query: ' + queryStr + ' [' + err + ']');
		})
		.on('result', function(row)
		{
			var link = JSON.parse(JSON.stringify(row));
			links.push(link);
		})
		.on('end', function()
		{
			reply(links);
		});
	}
}

// SIMILAR ARTISTS
function getSimilar(request, reply)
{
	if (request.query.band)
	{
		var similars = [];
		var url = 'http://developer.echonest.com/api/v4/artist/similar?api_key=EQL6WLZBL3NQ7VXRQ&name=' + request.query.band + '&format=json&results=100&start=0';
		console.log('Invoking url: ' + url);
		httpRequest(url, function (error, response, body) {
			if (!error && response.statusCode == 200) {
				var json = JSON.parse(body);
				var artists = json.response.artists;

				var inClause = "(";
				for(var i = 0; i < artists.length; i++) {
					inClause += (i == 0) ? 'UPPER("' + artists[i].name + '")' : ', UPPER("' + artists[i].name + '")';
				}
				inClause += ")";

				var queryStr = 'SELECT * FROM musicdb.document d WHERE UPPER(d.band) in ' + inClause + ' order by d.band, d.album';
				console.log('Executing query: ' + queryStr);
				var query = connection.query(queryStr);
				query.on('error', function(err)
				{
					console.log('Error executing query: ' + queryStr + ' [' + err + ']');
				})
				.on('result', function(row)
				{
					var similar = JSON.parse(JSON.stringify(row));
					similars.push(similar);
				})
				.on('end', function()
				{
					console.log('Found ' + similars.length + ' similar artists');
					reply(similars);
				});
			}
		})
	}
}
