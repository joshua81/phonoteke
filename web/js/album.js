// ALBUMS
function loadAlbums(page) {
  console.log("loadAlbums");
	currentStatus = 'albums';

	var req = new XMLHttpRequest();
	req.onreadystatechange = function() {
		if (req.readyState == 4 && req.status == 200) {
			var res = JSON.parse(req.responseText);
			loadAlbumsHandler(res);
		}
	}
  var text = searchInput.value.trim();
  console.log("/albums?p=" + page + "&q=" + text);
	req.open("GET", "http://localhost:8180/api/doc/review?p=" + page + "&q=" + text, true);
	req.send();
}

function loadAlbumsHandler(res) {
	if(res.length == 0)
	{
		return;
	}

  var html = content.innerHTML;
  for(var i = 0; i < res.length; i++) {
    var album = res[i];
  	html += '<div class="col-md-4 col-sm-6">';
  	html += '<a class="album" href="javascript:loadAlbum(\'' + album.id + '\')"><img class="album" src="' + album.cover + '"/>';
  	html += '<h2 class="album">' + album.band + '<br/>' + album.album + '</h2></a>';
    html += '</div>';
  }
  content.innerHTML = html;

  history.pushState({status: "albums", content: html}, null, "/");
}

function loadAlbum(id) {
  console.log("loadAlbum");
  currentStatus = 'album';

	var req = new XMLHttpRequest();
	req.onreadystatechange = function() {
		if (req.readyState == 4 && req.status == 200) {
			var res = JSON.parse(req.responseText);
			loadAlbumHandler(res);
		}
	}
	req.open("GET", "/api/doc/review/" + id, true);
	req.send();
}

function loadAlbumHandler(res) {
	if(res.length == 0)
	{
		return;
	}

	var album = res[0];
  var html = '<h1>' + album.band + ' | ' + album.album + '</h1>';
  html += '<h2>' + album.label + ' | ' + album.year + ' | ' + album.genres + ' <br/>' + album.authors + ' | ' + date2str(album.creationDate) + '</h2>';
  html += '<div class="album">';
  if(album.milestone)
  {
    html += '<p id="vote"><span class="glyphicon glyphicon-star"/></p>';
  }
  else
  {
    html += '<p id="vote">' + album.vote + '</p>';
  }
  html += '<img class="album" src="' + album.cover + '"/></div>';
  html += '<hr>';
  //html += '<a class="header" href="javascript:loadLinks(\'' + album.id + '\')"/>Recensioni</a>';
  //html += '<a class="header" href="javascript:loadConcerts(\'' + album.bandId + '\')"/>Concerti</a>';
  //html += '</h2>';
	html += '<div id="detail">' + album.content + '</div>';

  if(album.spotify != null || (album.youtube != null && album.youtube.length > 0))
  {
    html += '<hr>';
    html += '<div id="media">';
    if(album.spotify != null)
    {
      html += '<iframe src="https://embed.spotify.com/album/' + album.spotify + '" width="300" height="400" frameborder="0" allowtransparency="true"></iframe><br/>';
    }
    if(album.youtube.length > 0)
    {
      for(var i = 0; i < album.youtube.length; i++) 
      {
        html += '<iframe src="https://www.youtube.com/embed/' + album.youtube[i] + '" width="300" height="250" allowfullscreen></iframe><br/>';
      }
    }
    html += '</div>';
  }

  content.innerHTML = html;

  window.scrollTo(0, 0);
  history.pushState({status: "album", content: html}, null, "/");
}

function date2str(date) {
  return date.substring(0,10);
}
