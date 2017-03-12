var app = angular.module('phonoteque', ['ngSanitize']);

app.controller('album', function($scope) {
  $scope.albums=[];
  $scope.selectedAlbum=null;

  $scope.findAlbums = function (text, page) {
    	var req = new XMLHttpRequest();
    	req.onreadystatechange = function() {
    		if (req.readyState == 4 && req.status == 200) {
          console.log(req.responseText);
          var res = JSON.parse(req.responseText);
    			$scope.albums.push.apply($scope.albums, res);
          $scope.$apply();
    		}
    	}
    	req.open("GET", "/albums?search=" + text + "&page=" + page, true);
    	req.send();
    };

  $scope.loadAlbum = function (id) {
    var req = new XMLHttpRequest();
    req.onreadystatechange = function() {
      if (req.readyState == 4 && req.status == 200) {
        var res = JSON.parse(req.responseText);
        $scope.selectedAlbum = res[0];
        $scope.$apply();
      }
    }
    req.open("GET", "/albums/" + id, true);
    req.send();
  };

  $scope.resetAlbum = function () {
    $scope.selectedAlbum=null;
    $scope.$apply();
  };
});
