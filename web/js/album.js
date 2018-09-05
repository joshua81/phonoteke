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
  console.log("/albums?page=" + page + "&search=" + text);
	req.open("GET", "http://localhost:8180/api/doc/review?page=" + page + "&search=" + text, true);
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
  	html += '<a class="album" href="javascript:loadAlbum(\'' + album.id + '\')"><img class="album" src="' + album.cover + '"/></a>';
  	html += '<h2 class="album">' + album.band + '<br/>' + album.album + '</h2>';
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
  var html = '<h1 class="header">' + album.band + ' - ' + album.album + ' (' + album.year + ')</h1>';
  html += '<div class="album">';
  html += '<div class="vote">';
  if(album.milestone.data == 1)
  {
    html += '<span class="glyphicon glyphicon-star"/>';
  }
  else
  {
    html += album.vote;
  }
  html += '</div>';
	html += '<img class="album" src="' + album.cover + '"/>';
  html += '</div>';
  //html += '<a class="header" href="javascript:loadLinks(\'' + album.id + '\')"/>Recensioni</a>';
  //html += '<a class="header" href="javascript:loadConcerts(\'' + album.bandId + '\')"/>Concerti</a>';
  //html += '</h2>';
	html += '<div id="detail">' + album.content + '</div>';

  var spotifyId = album.albumIdSptf;
  if(spotifyId != null)
  {
    html += '<div id="spotify"><iframe src="https://embed.spotify.com/?uri=https://open.spotify.com/album/' + spotifyId + '" width="100%" frameborder="0" allowtransparency="true"/></div>';
  }

  content.innerHTML = html;

  window.scrollTo(0, 0);
  history.pushState({status: "album", content: html}, null, "/");
}
