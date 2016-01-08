// DOCUMENTS
function loadDocument(id) {
  console.log("loadDocument");

	var req = new XMLHttpRequest();
	req.onreadystatechange = function() {
		if (req.readyState == 4 && req.status == 200) {
			var res = JSON.parse(req.responseText);
			loadDocumentHandler(res);
		}
	}
	req.open("GET", "/documents/" + id, true);
	req.send();
}

function loadDocumentHandler(res) {
	if(res.length == 0)
	{
		return;
	}

	if(res[0].type == 'REVIEW')
	{
		loadAlbumHandler(res);
	}
	else if(res[0].type == 'MONOGRAPH')
	{
		loadArtistHandler(res);
	}
}

// LINKS
function loadLinks(id) {
  console.log("loadLinks");

  var req = new XMLHttpRequest();
  req.onreadystatechange = function() {
    if (req.readyState == 4 && req.status == 200) {
      var res = JSON.parse(req.responseText);
      loadLinksHandler(res);
    }
  }
  req.open("GET", "/links?id=" + id, true);
  req.send();
}

function loadLinksHandler(res) {
	if(res.length == 0)
	{
		return;
	}

  var html = '<p><ul class="list-group">';
  for(var i = 0; i < res.length; i++) {
    var link = res[i];
    html += '<li class="list-group-item"><a href="javascript:loadDocument(\'' + link.id + '\')">' + link.band + ' - ' + link.album + '</a></li>';
  }
  html += '</ul></p>';

  document.getElementById("detail").innerHTML = html;
}
