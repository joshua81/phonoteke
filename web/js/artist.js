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
  console.log("/artists?page=" + page + "&search=" + text);
	req.open("GET", "/artists?page=" + page + "&search=" + text, true);
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
  	html += '<a class="artist" href="javascript:loadArtist(\'' + artist.id + '\')"><img class="artist" src="' + artist.cover + '"/></a>';
  	html += '<h2 class="artist">' + artist.band + ' - ' + artist.album + '</h2>';
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
	req.open("GET", "/artists/" + id, true);
	req.send();
}

function loadArtistHandler(res) {
	if(res.length == 0)
	{
		return;
	}

	var artist = res[0];
	var html = '<img class="artist" src="' + artist.cover + '"/>';

	html += '<h2 class="header">';
  html += '<a class="header" href="javascript:loadDocument(\'' + artist.id + '\')"/>' + artist.band + ' - ' + artist.album + '</a><br/>';
  html += '<a class="header" href="javascript:loadLinks(\'' + artist.id + '\')"/>Recensioni</a>';
  html += '<a class="header" href="javascript:loadConcerts(\'' + album.bandId + '\')"/>Concerti</a>';
	html += '</h2>';

	html += '<div id="detail"><p>' + artist.content + '</p></div>';

	content.innerHTML = html;

  window.scrollTo(0, 0);
  history.pushState({status: "artist", content: html}, null, "/");
}

// CONCERT
function loadConcerts(id) {
  console.log("loadConcerts");

	var req = new XMLHttpRequest();
	req.onreadystatechange = function() {
		if (req.readyState == 4 && req.status == 200) {
			var res = JSON.parse(req.responseText);
			loadConcertsHandler(res);
		}
	}
	req.open("GET", "/concerts/" + id, true);
	req.send();
}

function loadConcertsHandler(res) {
	if(res.length == 0)
	{
		return;
	}

  var html = '<p><ul class="list-group">';
  for(var i = 0; i < res.length; i++) {
    var concert = res[i];
    html += '<li class="list-group-item">' + concert.name + '</li>';
  }
  html += '</ul></p>';

  document.getElementById("detail").innerHTML = html;
}
