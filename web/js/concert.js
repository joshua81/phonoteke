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
