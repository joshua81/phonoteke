.limit(50).sort({"_id":-1});
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
