// ARTISTS
function loadArtists(page) {
  console.log("loadArtists");
	currentStatus = 'artists';

	var req = new XMLHttpRequest();
	req.onreadystatechange = function() {
		if (req.readyState == 4 && req.status == 200) {
			var res = JSON.parse(req.responseText);
			loadArtistsHandler(res);
		}
	}
  var text = searchInput.value.trim();
  console.log("/artists?p=" + page + "&q=" + text);
	req.open("GET", "/api/doc/monograph?p=" + page + "&q=" + text, true);
	req.send();
}

function loadArtistsHandler(res) {
	if(res.length == 0)
	{
		return;
	}

  var html = content.innerHTML;
  for(var i = 0; i < res.length; i++) {
    var artist = res[i];
    html += '<div class="col-md-4 col-sm-6">';
  	html += '<a class="artist" href="javascript:loadArtist(\'' + artist.id + '\')"><img class="artist" src="' + artist.cover + '"/>';
  	html += '<h2 class="artist">' + artist.band + '<br/>' + artist.album + '</h2></a>';
  	html += '</div>';
  }
  content.innerHTML = html;
  history.pushState({status: "artists", content: html}, null, "/");
}

function loadArtist(id) {
  console.log("loadArtist");
  currentStatus = 'artist';

	var req = new XMLHttpRequest();
	req.onreadystatechange = function() {
		if (req.readyState == 4 && req.status == 200) {
			var res = JSON.parse(req.responseText);
			loadArtistHandler(res);
		}
	}
	req.open("GET", "/api/doc/monograph/" + id, true);
	req.send();
}

function loadArtistHandler(res) {
	if(res.length == 0)
	{
		return;
	}

	var artist = res[0];
	var html = '<h1 class="header">' + artist.band + ' - ' + artist.album + '</h1>';
	html += '<img class="artist" src="' + artist.cover + '"/>';
	html += '<hr>';
  //html += '<a class="header" href="javascript:loadDocument(\'' + artist.id + '\')"/>' +  + '</a><br/>';
  //html += '<a class="header" href="javascript:loadLinks(\'' + artist.id + '\')"/>Recensioni</a>';
  //html += '<a class="header" href="javascript:loadConcerts(\'' + artist.bandId + '\')"/>Concerti</a>';
	//html += '</h2>';

	html += '<div id="detail">' + artist.content + '</div>';

	if(artist.spotify != null || (artist.youtube != null && artist.youtube.length > 0))
	{
	  html += '<hr>';
	  html += '<div id="media">';
	  if(artist.spotify != null)
	  {
	    html += '<iframe src="https://embed.spotify.com/album/' + artist.spotify + '" width="300" height="400" frameborder="0" allowtransparency="true"></iframe><br/>';
	  }
	  if(artist.youtube.length > 0)
	  {
	    for(var i = 0; i < artist.youtube.length; i++) 
	    {
	      html += '<iframe src="https://www.youtube.com/embed/' + artist.youtube[i] + '" width="300" height="250" allowfullscreen></iframe><br/>';
	    }
	  }
	  html += '</div>';
	}

	content.innerHTML = html;

  window.scrollTo(0, 0);
  history.pushState({status: "artist", content: html}, null, "/");
}
