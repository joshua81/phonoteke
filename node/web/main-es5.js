function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

(window["webpackJsonp"] = window["webpackJsonp"] || []).push([["main"], {
  /***/
  "./$$_lazy_route_resource lazy recursive":
  /*!******************************************************!*\
    !*** ./$$_lazy_route_resource lazy namespace object ***!
    \******************************************************/

  /*! no static exports found */

  /***/
  function $$_lazy_route_resourceLazyRecursive(module, exports) {
    function webpackEmptyAsyncContext(req) {
      // Here Promise.resolve().then() is used instead of new Promise() to prevent
      // uncaught exception popping up in devtools
      return Promise.resolve().then(function () {
        var e = new Error("Cannot find module '" + req + "'");
        e.code = 'MODULE_NOT_FOUND';
        throw e;
      });
    }

    webpackEmptyAsyncContext.keys = function () {
      return [];
    };

    webpackEmptyAsyncContext.resolve = webpackEmptyAsyncContext;
    module.exports = webpackEmptyAsyncContext;
    webpackEmptyAsyncContext.id = "./$$_lazy_route_resource lazy recursive";
    /***/
  },

  /***/
  "./node_modules/raw-loader/dist/cjs.js!./src/app/app.component.html":
  /*!**************************************************************************!*\
    !*** ./node_modules/raw-loader/dist/cjs.js!./src/app/app.component.html ***!
    \**************************************************************************/

  /*! exports provided: default */

  /***/
  function node_modulesRawLoaderDistCjsJsSrcAppAppComponentHtml(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = "<div class=\"container-fluid\">\n  <router-outlet></router-outlet>\n  <app-footer></app-footer>\n</div>\n";
    /***/
  },

  /***/
  "./node_modules/raw-loader/dist/cjs.js!./src/app/doc/doc.component.html":
  /*!******************************************************************************!*\
    !*** ./node_modules/raw-loader/dist/cjs.js!./src/app/doc/doc.component.html ***!
    \******************************************************************************/

  /*! exports provided: default */

  /***/
  function node_modulesRawLoaderDistCjsJsSrcAppDocDocComponentHtml(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = "<doc-menu *ngIf=\"doc\"></doc-menu>\n<div *ngIf=\"doc\" class=\"container w-800 pl-1 pr-1 mt-3\">\n  <div class=\"d-flex flex-column pl-0 pr-0\">\n    <!-- cover -->\n    <div class=\"cover mx-auto\">\n      <a *ngIf=\"doc.type != 'podcast' && doc.spalbumid != null\" \n        href=\"{{'https://open.spotify.com/album/' + doc.spalbumid}}\" target=\"_blank\">\n        <img *ngIf=\"doc.coverM == null\" class=\"cover\" src=\"{{doc.cover}}\"/>\n        <img *ngIf=\"doc.coverM != null\" class=\"cover\" src=\"{{doc.coverM}}\"/>\n        <span class=\"play\"></span>\n      </a>\n      <a *ngIf=\"doc.type != 'podcast' && doc.spalbumid == null && doc.spartistid != null\" \n        href=\"{{'https://open.spotify.com/artist/' + doc.spartistid}}\" target=\"_blank\">\n        <img *ngIf=\"doc.coverM == null\" class=\"cover\" src=\"{{doc.cover}}\"/>\n        <img *ngIf=\"doc.coverM != null\" class=\"cover\" src=\"{{doc.coverM}}\"/>\n        <span class=\"play\"></span>\n      </a>\n      <div *ngIf=\"doc.type == 'podcast' || (doc.spalbumid == null && doc.spartistid == null)\" (click)=\"loadAlbum()\">\n        <img *ngIf=\"doc.coverM == null\" class=\"cover\" src=\"{{doc.cover}}\"/>\n        <img *ngIf=\"doc.coverM != null\" class=\"cover\" src=\"{{doc.coverM}}\"/>\n        <span *ngIf=\"doc.audio\" class=\"play\"></span>\n      </div>\n      <h2 *ngIf=\"doc.type == 'album' && doc.vote && doc.vote != 10\" class=\"vote\">{{doc.vote}}</h2>\n      <div *ngIf=\"doc.type == 'album' && doc.vote && doc.vote == 10\" class=\"vote\">\n        <span class=\"star\"></span>\n      </div>\n    </div>\n\n    <!-- album -->\n    <div class=\"d-flex flex-column bg-white mt-3\">\n      <div class=\"mx-auto\">\n        <a *ngIf=\"doc.spalbumid && doc.type != 'podcast'\" class=\"font-weight-bold\"\n          [innerHTML]=\"doc.title\" href=\"{{'https://open.spotify.com/album/' + doc.spalbumid}}\" target=\"_blank\">\n        </a>\n        <a *ngIf=\"doc.spalbumid && doc.type == 'podcast'\" class=\"font-weight-bold\"\n          [innerHTML]=\"doc.title\" href=\"{{'https://open.spotify.com/playlist/' + doc.spalbumid}}\" target=\"_blank\">\n        </a>\n        <h2 *ngIf=\"!doc.spalbumid\" [innerHTML]=\"doc.title\"></h2>\n      </div>\n\n      <!-- artist -->\n      <div class=\"mx-auto\">\n        <a *ngIf=\"doc.spartistid\"\n          [innerHTML]=\"doc.artist\" href=\"{{'https://open.spotify.com/artist/' + doc.spartistid}}\" target=\"_blank\">\n        </a>\n        <h3 *ngIf=\"!doc.spartistid\" [innerHTML]=\"doc.artist\"></h3>\n      </div>\n      \n      <!-- label + authors -->\n      <h3 *ngIf=\"doc.type == 'album' && doc.label\" class=\"mx-auto\">{{doc.label + ' | ' + doc.year + ' | ' + doc.genres}}</h3>\n      <h3 *ngIf=\"doc.authors\" class=\"text-muted mx-auto mb-2\">di {{doc.authors}}</h3>\n\n      <!-- source + events -->\n      <div class=\"d-flex flex-row align-items-center mx-auto\">\n        <a href=\"{{doc.url}}\" target=\"_blank\"><img class=\"{{doc.source}}\"></a>\n        <div *ngIf=\"doc.artistid\" class=\"icon ml-2\" (click)=\"loadEvents(doc.artistid)\">\n          <span class=\"songkick\"></span>\n        </div>\n      </div>\n\n      <!-- content -->\n      <p *ngIf=\"doc.review\" class=\"text-justify mt-2 p-2\" [innerHTML]=\"doc.review\"></p>\n      <app-tracks *ngIf=\"doc.type == 'podcast'\" [tracks]=\"doc.tracks\"></app-tracks>\n    </div>\n\n    <!-- videos + links -->\n    <app-video *ngIf=\"doc.type != 'podcast'\" [tracks]=\"doc.tracks\" type=\"videos\" label=\"Video\"></app-video>\n    <app-link [links]=\"links\" type=\"links\" label=\"Link\"></app-link>\n    <app-link [links]=\"otherLinks\" type=\"otherlinks\" label=\"Altri Link\"></app-link>\n  </div>\n</div>\n\n<!-- Spotify player-->\n<!--div *ngIf=\"spotify\" class=\"d-flex flex-row fixed-bottom\" [ngStyle]=\"{'width':'380px'}\">\n  <iframe [src]='spotifyURL()' width=\"350\" height=\"80\" frameborder=\"0\" allowtransparency=\"true\" allow=\"encrypted-media\">\n  </iframe>\n  <div class=\"close\" (click)=\"close($event)\">\n      <span class=\"close\"></span>\n  </div>\n</div-->\n\n<!-- Podcast player-->\n<div *ngIf=\"audio\" class=\"player d-flex flex-row fixed-bottom w-100\">\n  <img *ngIf=\"doc.coverM == null\" class=\"cover-player\" src=\"{{doc.cover}}\"/>\n  <img *ngIf=\"doc.coverM != null\" class=\"cover-player\" src=\"{{doc.coverM}}\"/>\n  <div class=\"ml-2 w-100\">\n    <div class=\"text-truncate-1 text-white font-weight-bold\" [innerHTML]=\"doc.title\"></div>\n    <div class=\"text-truncate-1 text-white mb-2\" [innerHTML]=\"doc.artist\"></div>\n    <div class=\"d-flex flex-row justify-content-center align-items-center w-100\">\n      <div class=\"player-time text-white text-left mr-2\" [innerHTML]=\"audioCurrentTime\"></div>\n      <div (click)=\"backward($event)\">\n        <span class=\"player-backward\"></span>\n      </div>\n      <div class=\"ml-2 mr-2\" (click)=\"playPause($event)\">\n        <span *ngIf=\"audio.paused\" class=\"player-play\"></span>\n        <span *ngIf=\"!audio.paused\" class=\"player-pause\"></span>\n      </div>\n      <div (click)=\"forward($event)\">\n        <span class=\"player-forward\"></span>\n      </div>\n      <div class=\"player-time text-white text-right ml-2\" [innerHTML]=\"audioDuration\"></div>\n    </div>\n  </div>\n  <div class=\"close\" (click)=\"close($event)\">\n    <span class=\"close\"></span>\n  </div>\n</div>\n\n<!-- Events -->\n<app-events [artist]=\"songkick\"></app-events>\n";
    /***/
  },

  /***/
  "./node_modules/raw-loader/dist/cjs.js!./src/app/doc/menu/menu.component.html":
  /*!************************************************************************************!*\
    !*** ./node_modules/raw-loader/dist/cjs.js!./src/app/doc/menu/menu.component.html ***!
    \************************************************************************************/

  /*! exports provided: default */

  /***/
  function node_modulesRawLoaderDistCjsJsSrcAppDocMenuMenuComponentHtml(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = "<nav class=\"navbar navbar-expand-lg navbar-light fixed-top justify-content-center hscroll\">\n  <div class=\"d-flex flex-row align-items-center w-100\">\n    <a class=\"ml-2 mr-2\" (click)=\"back()\"><img class=\"logo\"></a>\n    <!--div class=\"ml-2 mr-2\">\n      <a>\n        <img *ngIf=\"component.doc.coverM == null\" class=\"cover\" src=\"{{component.doc.cover}}\"/>\n        <img *ngIf=\"component.doc.coverM != null\" class=\"cover\" src=\"{{component.doc.coverM}}\"/>\n      </a>\n    </div-->\n    <div class=\"title\">\n      <h2 class=\"text-center text-truncate-1\" [innerHTML]=\"component.doc.title\"></h2>\n      <h3 class=\"text-center text-truncate-1\" [innerHTML]=\"component.doc.artist\"></h3>\n    </div>\n  </div>\n</nav>\n";
    /***/
  },

  /***/
  "./node_modules/raw-loader/dist/cjs.js!./src/app/docs/docs.component.html":
  /*!********************************************************************************!*\
    !*** ./node_modules/raw-loader/dist/cjs.js!./src/app/docs/docs.component.html ***!
    \********************************************************************************/

  /*! exports provided: default */

  /***/
  function node_modulesRawLoaderDistCjsJsSrcAppDocsDocsComponentHtml(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = "<docs-menu></docs-menu>\n<div *ngIf=\"albums.length != 0 || podcasts.length != 0 || interviews.length != 0 || concerts.length != 0 || artists.length != 0\" \n    class=\"fill\">\n    <app-section type=\"albums\" label=\"Album\" [docs]=\"albums\"></app-section>\n    <app-section type=\"podcasts\" label=\"Podcast\" [docs]=\"podcasts\"></app-section>\n    <app-section type=\"interviews\" label=\"Interviste\" [docs]=\"interviews\"></app-section>\n    <app-section type=\"concerts\" label=\"Concerti\" [docs]=\"concerts\"></app-section>\n    <app-section type=\"artists\" label=\"Artisti\" [docs]=\"artists\"></app-section>\n</div>\n<div *ngIf=\"albums.length == 0 && podcasts.length == 0 && interviews.length == 0 && concerts.length == 0 && artists.length == 0\" \n    class=\"d-flex justify-content-center align-items-center fill\">\n    <div class=\"font-weight-bold\">Nessun risultato trovato</div>\n</div>\n";
    /***/
  },

  /***/
  "./node_modules/raw-loader/dist/cjs.js!./src/app/docs/menu/menu.component.html":
  /*!*************************************************************************************!*\
    !*** ./node_modules/raw-loader/dist/cjs.js!./src/app/docs/menu/menu.component.html ***!
    \*************************************************************************************/

  /*! exports provided: default */

  /***/
  function node_modulesRawLoaderDistCjsJsSrcAppDocsMenuMenuComponentHtml(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = "<nav class=\"navbar navbar-expand-lg navbar-light fixed-top justify-content-center hscroll\">\n  <div class=\"d-flex flex-row align-items-center w-100\">\n    <a [routerLink]=\"['/']\" class=\"ml-2 mr-2\"><img class=\"logo\"></a>\n    <form class=\"search ml-2 mr-2\">\n      <input class=\"form-control\" name=\"search\" type=\"search\" placeholder=\"Cerca\" aria-label=\"Cerca\" [(ngModel)]=\"docs.searchText\" (search)=\"docs.loadDocsAll()\">\n    </form>\n    <div class=\"icon ml-2 mr-2\">\n      <a *ngIf=\"docs.user == null\" href=\"/api/login\">\n        <span class=\"spotify\"></span>\n      </a>\n      <a *ngIf=\"docs.user != null\" [routerLink]=\"['/starred']\">\n        <img class=\"spotify\" src=\"{{docs.user}}\">\n      </a>\n    </div>\n  </div>\n</nav>\n";
    /***/
  },

  /***/
  "./node_modules/raw-loader/dist/cjs.js!./src/app/events/events.component.html":
  /*!************************************************************************************!*\
    !*** ./node_modules/raw-loader/dist/cjs.js!./src/app/events/events.component.html ***!
    \************************************************************************************/

  /*! exports provided: default */

  /***/
  function node_modulesRawLoaderDistCjsJsSrcAppEventsEventsComponentHtml(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = "<div *ngIf=\"artist != null\">\n    <div class=\"d-flex justify-content-center modal-bg\" (click)=\"close()\">\n        <div *ngIf=\"events && events.length != 0\" class=\"align-self-center overflow-auto modal-box\">\n            <div class=\"list-group\">\n                <div *ngFor=\"let event of events\" class=\"p-2 list-group-item\">\n                    <div class=\"d-flex align-items-center\">\n                        <div class=\"w-100\">\n                            <div class=\"text-white font-weight-bold\">{{event.displayName}}</div>\n                            <div class=\"text-white\">{{event.start.date}} {{event.location.city}}</div>\n                            <div class=\"text-white\">{{event.venue.displayName}}</div>\n                        </div>\n                    </div>\n                </div>\n            </div>\n        </div>\n        <div *ngIf=\"events && events.length == 0\" class=\"align-self-center overflow-auto modal-box\">\n            <div class=\"d-flex justify-content-center align-middle\">\n                <p class=\"text-white font-weight-bold pt-2\">Nessun evento in programma</p>\n            </div>\n        </div>\n    </div>\n</div>";
    /***/
  },

  /***/
  "./node_modules/raw-loader/dist/cjs.js!./src/app/footer/footer.component.html":
  /*!************************************************************************************!*\
    !*** ./node_modules/raw-loader/dist/cjs.js!./src/app/footer/footer.component.html ***!
    \************************************************************************************/

  /*! exports provided: default */

  /***/
  function node_modulesRawLoaderDistCjsJsSrcAppFooterFooterComponentHtml(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = "<footer class=\"page-footer font-small mt-3\">\n  <div class=\"footer-copyright bg-info d-flex flex-row align-items-center justify-content-center\">\n    <!--div class=\"icon mr-2\">\n      <a href=\"mailto:andrea.ricci@gmx.com\"><span class=\"mail\"></span></a>\n    </div-->\n    <div class=\"icon mr-2\">\n      <a href=\"https://github.com/joshua81/\" target=\"_blank\"><span class=\"github\"></span></a>\n    </div>\n    <div class=\"icon mr-2\">\n      <a href=\"https://www.linkedin.com/in/ricciandrea/\" target=\"_blank\"><span class=\"linkedin\"></span></a>\n    </div>\n    <div class=\"icon mr-2\">\n      <a href=\"/html/api.html\" target=\"_blank\"><h3 class=\"api text-white\">&#123;api&#125;</h3></a>\n    </div>\n    <div class=\"icon\">\n      <a href=\"https://creativecommons.org/\" target=\"_blank\"><span class=\"cc\"></span></a>\n    </div>\n  </div>\n</footer>";
    /***/
  },

  /***/
  "./node_modules/raw-loader/dist/cjs.js!./src/app/link/link.component.html":
  /*!********************************************************************************!*\
    !*** ./node_modules/raw-loader/dist/cjs.js!./src/app/link/link.component.html ***!
    \********************************************************************************/

  /*! exports provided: default */

  /***/
  function node_modulesRawLoaderDistCjsJsSrcAppLinkLinkComponentHtml(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = "<div *ngIf=\"links && links.length != 0\" class=\"mt-3\">\n  <div class=\"d-flex flex-row align-items-center pl-0 pr-0\">\n    <h1 class=\"text-uppercase mt-0 mb-0 mr-auto\">{{label}}</h1>\n    <div *ngIf=\"component.isDesktop()\"\n      class=\"backward\" (click)=\"previous()\"><span class=\"previous\"></span></div>\n    <div *ngIf=\"component.isDesktop()\"\n      class=\"forward\" (click)=\"next()\"><span class=\"next\"></span></div>\n  </div>\n  <div id=\"{{type}}\" class=\"d-flex flex-row flex-nowrap hscroll pt-2\">\n    <div *ngFor=\"let link of links\" class=\"pr-2\">\n      <div class=\"cover card\">\n        <a class=\"cover\" [routerLink]=\"['/docs/' + link.id]\">\n          <img *ngIf=\"link.coverM == null\" class=\"cover card-img-top\" src=\"{{link.cover}}\"/>\n          <img *ngIf=\"link.coverM != null\" class=\"cover card-img-top\" src=\"{{link.coverM}}\"/>\n        </a>\n        <div class=\"card-body p-1\">\n          <h2 class=\"text-truncate-2\" [innerHTML]=\"link.title\"></h2>\n          <h3 class=\"text-truncate-2\" [innerHTML]=\"link.artist\"></h3>\n          <h3 class=\"card-footer text-muted text-uppercase p-0\">{{link.type}}</h3>\n        </div>\n      </div>\n    </div>\n  </div>\n</div>\n";
    /***/
  },

  /***/
  "./node_modules/raw-loader/dist/cjs.js!./src/app/podcasts/menu/menu.component.html":
  /*!*****************************************************************************************!*\
    !*** ./node_modules/raw-loader/dist/cjs.js!./src/app/podcasts/menu/menu.component.html ***!
    \*****************************************************************************************/

  /*! exports provided: default */

  /***/
  function node_modulesRawLoaderDistCjsJsSrcAppPodcastsMenuMenuComponentHtml(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = "<nav class=\"navbar navbar-expand-lg navbar-light fixed-top justify-content-center hscroll\">\n  <div class=\"d-flex flex-row align-items-center w-100\">\n    <a [routerLink]=\"['/']\" class=\"ml-2 mr-2\"><img class=\"logo\"></a>\n    <form class=\"search ml-2 mr-2\">\n      <input class=\"form-control\" name=\"search\" type=\"search\" placeholder=\"Cerca\" aria-label=\"Cerca\" [(ngModel)]=\"docs.searchText\" (search)=\"docs.loadDocs()\">\n    </form>\n    <!--div class=\"icon ml-2 mr-2\">\n      <a *ngIf=\"docs.user == null\" href=\"/api/login\">\n        <span class=\"spotify\"></span>\n      </a>\n      <a *ngIf=\"docs.user != null\" [routerLink]=\"['/starred']\">\n        <img class=\"spotify\" src=\"{{docs.user}}\">\n      </a>\n    </div-->\n  </div>\n</nav>\n";
    /***/
  },

  /***/
  "./node_modules/raw-loader/dist/cjs.js!./src/app/podcasts/podcasts.component.html":
  /*!****************************************************************************************!*\
    !*** ./node_modules/raw-loader/dist/cjs.js!./src/app/podcasts/podcasts.component.html ***!
    \****************************************************************************************/

  /*! exports provided: default */

  /***/
  function node_modulesRawLoaderDistCjsJsSrcAppPodcastsPodcastsComponentHtml(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = "<podcasts-menu></podcasts-menu>\n<div *ngIf=\"podcasts.length != 0\" class=\"fill\">\n    <app-section type=\"podcasts\" label=\"Podcast\" [docs]=\"podcasts\"></app-section>\n</div>\n<div *ngIf=\"podcasts.length == 0\" \n    class=\"d-flex justify-content-center align-items-center fill\">\n    <div class=\"font-weight-bold\">Nessun risultato trovato</div>\n</div>\n";
    /***/
  },

  /***/
  "./node_modules/raw-loader/dist/cjs.js!./src/app/section/section.component.html":
  /*!**************************************************************************************!*\
    !*** ./node_modules/raw-loader/dist/cjs.js!./src/app/section/section.component.html ***!
    \**************************************************************************************/

  /*! exports provided: default */

  /***/
  function node_modulesRawLoaderDistCjsJsSrcAppSectionSectionComponentHtml(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = "<div *ngIf=\"docs && docs.length != 0\" class=\"container mt-2\">\n  <div id=\"podcast\" infinite-scroll (scrolled)=\"scrollDocs()\">\n      <ul class=\"list-group\">\n        <li *ngFor=\"let doc of docs\" class=\"list-group-item p-2\">\n          <div class=\"card\">\n            <div class=\"row no-gutters\">\n              <a class=\"cover\" [routerLink]=\"['/docs/' + doc.id]\">\n                <img *ngIf=\"doc.coverM == null\" class=\"cover card-img-top\" src=\"{{doc.cover}}\"/>\n                <img *ngIf=\"doc.coverM != null\" class=\"cover card-img-top\" src=\"{{doc.coverM}}\"/>\n              </a>\n              <div class=\"col-8 ml-2\">\n                <div class=\"card-body p-0\">\n                  <h3 class=\"text-truncate-2\" [innerHTML]=\"doc.artist\"></h3>\n                  <h2 class=\"text-truncate-2\" [innerHTML]=\"doc.title\"></h2>\n                  <p *ngIf=\"doc.description != null\" class=\"text-truncate-3 mt-2 mb-0\" [innerHTML]=\"doc.description\"></p>\n                </div>\n              </div>\n            </div>\n          </div>\n        </li>\n      </ul>\n  </div>\n</div>\n";
    /***/
  },

  /***/
  "./node_modules/raw-loader/dist/cjs.js!./src/app/tracks/tracks.component.html":
  /*!************************************************************************************!*\
    !*** ./node_modules/raw-loader/dist/cjs.js!./src/app/tracks/tracks.component.html ***!
    \************************************************************************************/

  /*! exports provided: default */

  /***/
  function node_modulesRawLoaderDistCjsJsSrcAppTracksTracksComponentHtml(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = "<div *ngIf=\"tracks && tracks.length > 0\" class=\"d-flex flex-column justify-content-center w-100 mt-2 p-2\">\n    <div *ngFor=\"let track of tracks\" class=\"mb-2\">\n        <div class=\"d-flex flex-row align-items-center m-0\">\n            <div class=\"cover\">\n                <a *ngIf=\"track.spotify\" href=\"{{'https://open.spotify.com/track/' + track.spotify}}\" target=\"_blank\">\n                    <img class=\"cover\" src=\"{{track.coverM}}\"/>\n                </a>\n                <!--span *ngIf=\"track.spotify\" class=\"play\"></span-->\n                <h1 *ngIf=\"track.spotify == null\" class=\"cover text-uppercase text-white font-weight-bold\">{{track.title.charAt(0)}}</h1>\n            </div>\n            <div class=\"ml-2 mr-auto\">\n                <div *ngIf=\"track.spotify == null\" [innerHTML]=\"track.title\"></div>\n                <div *ngIf=\"track.spotify\" class=\"text-truncate-1\">\n                    <a [innerHTML]=\"track.title\" href=\"{{'https://open.spotify.com/track/' + track.spotify}}\" target=\"_blank\"></a>\n                </div>\n                <div *ngIf=\"track.spotify\" class=\"text-truncate-1\">\n                    <a class=\"font-weight-bold\" [innerHTML]=\"track.album\" href=\"{{'https://open.spotify.com/album/' + track.spalbumid}}\" target=\"_blank\"></a>\n                </div>\n                <div *ngIf=\"track.spotify\" class=\"text-truncate-1\">\n                    <a [innerHTML]=\"track.artist\" href=\"{{'https://open.spotify.com/artist/' + track.spartistid}}\" target=\"_blank\"></a>\n                </div>\n            </div>\n            <div *ngIf=\"track.artistid\" class=\"icon ml-2\" (click)=\"component.loadEvents(track.artistid)\">\n                <span class=\"songkick\"></span>\n            </div>\n        </div>\n    </div>\n</div>\n";
    /***/
  },

  /***/
  "./node_modules/raw-loader/dist/cjs.js!./src/app/video/video.component.html":
  /*!**********************************************************************************!*\
    !*** ./node_modules/raw-loader/dist/cjs.js!./src/app/video/video.component.html ***!
    \**********************************************************************************/

  /*! exports provided: default */

  /***/
  function node_modulesRawLoaderDistCjsJsSrcAppVideoVideoComponentHtml(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = "<div *ngIf=\"tracks && tracks.length != 0\" class=\"mt-3\">\n  <div class=\"d-flex flex-row align-items-center pl-0 pr-0\">\n    <h1 class=\"text-uppercase mt-0 mb-0 mr-auto\">{{label}}</h1>\n    <div *ngIf=\"component.isDesktop()\"\n      class=\"backward\" (click)=\"previous()\"><span class=\"previous\"></span></div>\n    <div *ngIf=\"component.isDesktop()\"\n      class=\"forward\" (click)=\"next()\"><span class=\"next\"></span></div>\n  </div>\n  <div id=\"{{type}}\" class=\"d-flex flex-row flex-nowrap hscroll pt-2\">\n    <div *ngFor=\"let track of tracks\" class=\"pr-2\">\n      <div *ngIf=\"track.youtube\" class=\"cover\">\n        <a href=\"{{'https://www.youtube.com/watch?v=' + track.youtube}}\" target=\"_blank\">\n          <img class=\"cover\" src=\"{{'https://img.youtube.com/vi/' + track.youtube + '/default.jpg'}}\"/>\n          <span class=\"youtube\"></span>\n        </a>\n      </div>\n    </div>\n  </div>\n</div>\n\n<!-- player-->\n<!--div *ngIf=\"video\" class=\"d-flex flex-row fixed-bottom\" [ngStyle]=\"{'width':'380px'}\">\n  <iframe [src]='youtubeURL()' type=\"text/html\" width=\"350\" height=\"200\" frameborder=\"0\">\n  </iframe>\n  <div class=\"close\" (click)=\"close($event)\">\n    <span class=\"close\"></span>\n  </div>\n</div-->";
    /***/
  },

  /***/
  "./node_modules/tslib/tslib.es6.js":
  /*!*****************************************!*\
    !*** ./node_modules/tslib/tslib.es6.js ***!
    \*****************************************/

  /*! exports provided: __extends, __assign, __rest, __decorate, __param, __metadata, __awaiter, __generator, __exportStar, __values, __read, __spread, __spreadArrays, __await, __asyncGenerator, __asyncDelegator, __asyncValues, __makeTemplateObject, __importStar, __importDefault */

  /***/
  function node_modulesTslibTslibEs6Js(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "__extends", function () {
      return __extends;
    });
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "__assign", function () {
      return _assign;
    });
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "__rest", function () {
      return __rest;
    });
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "__decorate", function () {
      return __decorate;
    });
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "__param", function () {
      return __param;
    });
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "__metadata", function () {
      return __metadata;
    });
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "__awaiter", function () {
      return __awaiter;
    });
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "__generator", function () {
      return __generator;
    });
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "__exportStar", function () {
      return __exportStar;
    });
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "__values", function () {
      return __values;
    });
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "__read", function () {
      return __read;
    });
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "__spread", function () {
      return __spread;
    });
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "__spreadArrays", function () {
      return __spreadArrays;
    });
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "__await", function () {
      return __await;
    });
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "__asyncGenerator", function () {
      return __asyncGenerator;
    });
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "__asyncDelegator", function () {
      return __asyncDelegator;
    });
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "__asyncValues", function () {
      return __asyncValues;
    });
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "__makeTemplateObject", function () {
      return __makeTemplateObject;
    });
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "__importStar", function () {
      return __importStar;
    });
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "__importDefault", function () {
      return __importDefault;
    });
    /*! *****************************************************************************
    Copyright (c) Microsoft Corporation. All rights reserved.
    Licensed under the Apache License, Version 2.0 (the "License"); you may not use
    this file except in compliance with the License. You may obtain a copy of the
    License at http://www.apache.org/licenses/LICENSE-2.0
    
    THIS CODE IS PROVIDED ON AN *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION ANY IMPLIED
    WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A PARTICULAR PURPOSE,
    MERCHANTABLITY OR NON-INFRINGEMENT.
    
    See the Apache Version 2.0 License for specific language governing permissions
    and limitations under the License.
    ***************************************************************************** */

    /* global Reflect, Promise */


    var _extendStatics = function extendStatics(d, b) {
      _extendStatics = Object.setPrototypeOf || {
        __proto__: []
      } instanceof Array && function (d, b) {
        d.__proto__ = b;
      } || function (d, b) {
        for (var p in b) {
          if (b.hasOwnProperty(p)) d[p] = b[p];
        }
      };

      return _extendStatics(d, b);
    };

    function __extends(d, b) {
      _extendStatics(d, b);

      function __() {
        this.constructor = d;
      }

      d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    }

    var _assign = function __assign() {
      _assign = Object.assign || function __assign(t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
          s = arguments[i];

          for (var p in s) {
            if (Object.prototype.hasOwnProperty.call(s, p)) t[p] = s[p];
          }
        }

        return t;
      };

      return _assign.apply(this, arguments);
    };

    function __rest(s, e) {
      var t = {};

      for (var p in s) {
        if (Object.prototype.hasOwnProperty.call(s, p) && e.indexOf(p) < 0) t[p] = s[p];
      }

      if (s != null && typeof Object.getOwnPropertySymbols === "function") for (var i = 0, p = Object.getOwnPropertySymbols(s); i < p.length; i++) {
        if (e.indexOf(p[i]) < 0 && Object.prototype.propertyIsEnumerable.call(s, p[i])) t[p[i]] = s[p[i]];
      }
      return t;
    }

    function __decorate(decorators, target, key, desc) {
      var c = arguments.length,
          r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc,
          d;
      if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);else for (var i = decorators.length - 1; i >= 0; i--) {
        if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
      }
      return c > 3 && r && Object.defineProperty(target, key, r), r;
    }

    function __param(paramIndex, decorator) {
      return function (target, key) {
        decorator(target, key, paramIndex);
      };
    }

    function __metadata(metadataKey, metadataValue) {
      if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(metadataKey, metadataValue);
    }

    function __awaiter(thisArg, _arguments, P, generator) {
      return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) {
          try {
            step(generator.next(value));
          } catch (e) {
            reject(e);
          }
        }

        function rejected(value) {
          try {
            step(generator["throw"](value));
          } catch (e) {
            reject(e);
          }
        }

        function step(result) {
          result.done ? resolve(result.value) : new P(function (resolve) {
            resolve(result.value);
          }).then(fulfilled, rejected);
        }

        step((generator = generator.apply(thisArg, _arguments || [])).next());
      });
    }

    function __generator(thisArg, body) {
      var _ = {
        label: 0,
        sent: function sent() {
          if (t[0] & 1) throw t[1];
          return t[1];
        },
        trys: [],
        ops: []
      },
          f,
          y,
          t,
          g;
      return g = {
        next: verb(0),
        "throw": verb(1),
        "return": verb(2)
      }, typeof Symbol === "function" && (g[Symbol.iterator] = function () {
        return this;
      }), g;

      function verb(n) {
        return function (v) {
          return step([n, v]);
        };
      }

      function step(op) {
        if (f) throw new TypeError("Generator is already executing.");

        while (_) {
          try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];

            switch (op[0]) {
              case 0:
              case 1:
                t = op;
                break;

              case 4:
                _.label++;
                return {
                  value: op[1],
                  done: false
                };

              case 5:
                _.label++;
                y = op[1];
                op = [0];
                continue;

              case 7:
                op = _.ops.pop();

                _.trys.pop();

                continue;

              default:
                if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) {
                  _ = 0;
                  continue;
                }

                if (op[0] === 3 && (!t || op[1] > t[0] && op[1] < t[3])) {
                  _.label = op[1];
                  break;
                }

                if (op[0] === 6 && _.label < t[1]) {
                  _.label = t[1];
                  t = op;
                  break;
                }

                if (t && _.label < t[2]) {
                  _.label = t[2];

                  _.ops.push(op);

                  break;
                }

                if (t[2]) _.ops.pop();

                _.trys.pop();

                continue;
            }

            op = body.call(thisArg, _);
          } catch (e) {
            op = [6, e];
            y = 0;
          } finally {
            f = t = 0;
          }
        }

        if (op[0] & 5) throw op[1];
        return {
          value: op[0] ? op[1] : void 0,
          done: true
        };
      }
    }

    function __exportStar(m, exports) {
      for (var p in m) {
        if (!exports.hasOwnProperty(p)) exports[p] = m[p];
      }
    }

    function __values(o) {
      var m = typeof Symbol === "function" && o[Symbol.iterator],
          i = 0;
      if (m) return m.call(o);
      return {
        next: function next() {
          if (o && i >= o.length) o = void 0;
          return {
            value: o && o[i++],
            done: !o
          };
        }
      };
    }

    function __read(o, n) {
      var m = typeof Symbol === "function" && o[Symbol.iterator];
      if (!m) return o;
      var i = m.call(o),
          r,
          ar = [],
          e;

      try {
        while ((n === void 0 || n-- > 0) && !(r = i.next()).done) {
          ar.push(r.value);
        }
      } catch (error) {
        e = {
          error: error
        };
      } finally {
        try {
          if (r && !r.done && (m = i["return"])) m.call(i);
        } finally {
          if (e) throw e.error;
        }
      }

      return ar;
    }

    function __spread() {
      for (var ar = [], i = 0; i < arguments.length; i++) {
        ar = ar.concat(__read(arguments[i]));
      }

      return ar;
    }

    function __spreadArrays() {
      for (var s = 0, i = 0, il = arguments.length; i < il; i++) {
        s += arguments[i].length;
      }

      for (var r = Array(s), k = 0, i = 0; i < il; i++) {
        for (var a = arguments[i], j = 0, jl = a.length; j < jl; j++, k++) {
          r[k] = a[j];
        }
      }

      return r;
    }

    ;

    function __await(v) {
      return this instanceof __await ? (this.v = v, this) : new __await(v);
    }

    function __asyncGenerator(thisArg, _arguments, generator) {
      if (!Symbol.asyncIterator) throw new TypeError("Symbol.asyncIterator is not defined.");
      var g = generator.apply(thisArg, _arguments || []),
          i,
          q = [];
      return i = {}, verb("next"), verb("throw"), verb("return"), i[Symbol.asyncIterator] = function () {
        return this;
      }, i;

      function verb(n) {
        if (g[n]) i[n] = function (v) {
          return new Promise(function (a, b) {
            q.push([n, v, a, b]) > 1 || resume(n, v);
          });
        };
      }

      function resume(n, v) {
        try {
          step(g[n](v));
        } catch (e) {
          settle(q[0][3], e);
        }
      }

      function step(r) {
        r.value instanceof __await ? Promise.resolve(r.value.v).then(fulfill, reject) : settle(q[0][2], r);
      }

      function fulfill(value) {
        resume("next", value);
      }

      function reject(value) {
        resume("throw", value);
      }

      function settle(f, v) {
        if (f(v), q.shift(), q.length) resume(q[0][0], q[0][1]);
      }
    }

    function __asyncDelegator(o) {
      var i, p;
      return i = {}, verb("next"), verb("throw", function (e) {
        throw e;
      }), verb("return"), i[Symbol.iterator] = function () {
        return this;
      }, i;

      function verb(n, f) {
        i[n] = o[n] ? function (v) {
          return (p = !p) ? {
            value: __await(o[n](v)),
            done: n === "return"
          } : f ? f(v) : v;
        } : f;
      }
    }

    function __asyncValues(o) {
      if (!Symbol.asyncIterator) throw new TypeError("Symbol.asyncIterator is not defined.");
      var m = o[Symbol.asyncIterator],
          i;
      return m ? m.call(o) : (o = typeof __values === "function" ? __values(o) : o[Symbol.iterator](), i = {}, verb("next"), verb("throw"), verb("return"), i[Symbol.asyncIterator] = function () {
        return this;
      }, i);

      function verb(n) {
        i[n] = o[n] && function (v) {
          return new Promise(function (resolve, reject) {
            v = o[n](v), settle(resolve, reject, v.done, v.value);
          });
        };
      }

      function settle(resolve, reject, d, v) {
        Promise.resolve(v).then(function (v) {
          resolve({
            value: v,
            done: d
          });
        }, reject);
      }
    }

    function __makeTemplateObject(cooked, raw) {
      if (Object.defineProperty) {
        Object.defineProperty(cooked, "raw", {
          value: raw
        });
      } else {
        cooked.raw = raw;
      }

      return cooked;
    }

    ;

    function __importStar(mod) {
      if (mod && mod.__esModule) return mod;
      var result = {};
      if (mod != null) for (var k in mod) {
        if (Object.hasOwnProperty.call(mod, k)) result[k] = mod[k];
      }
      result.default = mod;
      return result;
    }

    function __importDefault(mod) {
      return mod && mod.__esModule ? mod : {
        default: mod
      };
    }
    /***/

  },

  /***/
  "./src/app/app.component.css":
  /*!***********************************!*\
    !*** ./src/app/app.component.css ***!
    \***********************************/

  /*! exports provided: default */

  /***/
  function srcAppAppComponentCss(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = "\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IiIsImZpbGUiOiJzcmMvYXBwL2FwcC5jb21wb25lbnQuY3NzIn0= */";
    /***/
  },

  /***/
  "./src/app/app.component.ts":
  /*!**********************************!*\
    !*** ./src/app/app.component.ts ***!
    \**********************************/

  /*! exports provided: AppComponent */

  /***/
  function srcAppAppComponentTs(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "AppComponent", function () {
      return AppComponent;
    });
    /* harmony import */


    var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(
    /*! tslib */
    "./node_modules/tslib/tslib.es6.js");
    /* harmony import */


    var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(
    /*! @angular/core */
    "./node_modules/@angular/core/fesm2015/core.js");
    /* harmony import */


    var _app_service__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(
    /*! ./app.service */
    "./src/app/app.service.ts");

    var AppComponent = function AppComponent(service) {
      _classCallCheck(this, AppComponent);

      this.service = service;
    };

    AppComponent.ctorParameters = function () {
      return [{
        type: _app_service__WEBPACK_IMPORTED_MODULE_2__["AppService"]
      }];
    };

    AppComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
      selector: 'app-root',
      template: tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! raw-loader!./app.component.html */
      "./node_modules/raw-loader/dist/cjs.js!./src/app/app.component.html")).default,
      providers: [_app_service__WEBPACK_IMPORTED_MODULE_2__["AppService"]],
      styles: [tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! ./app.component.css */
      "./src/app/app.component.css")).default]
    })], AppComponent);
    /***/
  },

  /***/
  "./src/app/app.module.ts":
  /*!*******************************!*\
    !*** ./src/app/app.module.ts ***!
    \*******************************/

  /*! exports provided: AppModule */

  /***/
  function srcAppAppModuleTs(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "AppModule", function () {
      return AppModule;
    });
    /* harmony import */


    var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(
    /*! tslib */
    "./node_modules/tslib/tslib.es6.js");
    /* harmony import */


    var _angular_platform_browser__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(
    /*! @angular/platform-browser */
    "./node_modules/@angular/platform-browser/fesm2015/platform-browser.js");
    /* harmony import */


    var _angular_core__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(
    /*! @angular/core */
    "./node_modules/@angular/core/fesm2015/core.js");
    /* harmony import */


    var _angular_router__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(
    /*! @angular/router */
    "./node_modules/@angular/router/fesm2015/router.js");
    /* harmony import */


    var _angular_forms__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(
    /*! @angular/forms */
    "./node_modules/@angular/forms/fesm2015/forms.js");
    /* harmony import */


    var _angular_common_http__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(
    /*! @angular/common/http */
    "./node_modules/@angular/common/fesm2015/http.js");
    /* harmony import */


    var ngx_infinite_scroll__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(
    /*! ngx-infinite-scroll */
    "./node_modules/ngx-infinite-scroll/modules/ngx-infinite-scroll.js");
    /* harmony import */


    var _angular_common__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(
    /*! @angular/common */
    "./node_modules/@angular/common/fesm2015/common.js");
    /* harmony import */


    var _angular_youtube_player__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(
    /*! @angular/youtube-player */
    "./node_modules/@angular/youtube-player/esm2015/youtube-player.js");
    /* harmony import */


    var _app_component__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(
    /*! ./app.component */
    "./src/app/app.component.ts");
    /* harmony import */


    var _docs_menu_menu_component__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(
    /*! ./docs/menu/menu.component */
    "./src/app/docs/menu/menu.component.ts");
    /* harmony import */


    var _docs_docs_component__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(
    /*! ./docs/docs.component */
    "./src/app/docs/docs.component.ts");
    /* harmony import */


    var _podcasts_menu_menu_component__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(
    /*! ./podcasts/menu/menu.component */
    "./src/app/podcasts/menu/menu.component.ts");
    /* harmony import */


    var _podcasts_podcasts_component__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(
    /*! ./podcasts/podcasts.component */
    "./src/app/podcasts/podcasts.component.ts");
    /* harmony import */


    var _doc_menu_menu_component__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(
    /*! ./doc/menu/menu.component */
    "./src/app/doc/menu/menu.component.ts");
    /* harmony import */


    var _doc_doc_component__WEBPACK_IMPORTED_MODULE_15__ = __webpack_require__(
    /*! ./doc/doc.component */
    "./src/app/doc/doc.component.ts");
    /* harmony import */


    var _tracks_tracks_component__WEBPACK_IMPORTED_MODULE_16__ = __webpack_require__(
    /*! ./tracks/tracks.component */
    "./src/app/tracks/tracks.component.ts");
    /* harmony import */


    var _events_events_component__WEBPACK_IMPORTED_MODULE_17__ = __webpack_require__(
    /*! ./events/events.component */
    "./src/app/events/events.component.ts");
    /* harmony import */


    var _section_section_component__WEBPACK_IMPORTED_MODULE_18__ = __webpack_require__(
    /*! ./section/section.component */
    "./src/app/section/section.component.ts");
    /* harmony import */


    var _video_video_component__WEBPACK_IMPORTED_MODULE_19__ = __webpack_require__(
    /*! ./video/video.component */
    "./src/app/video/video.component.ts");
    /* harmony import */


    var _link_link_component__WEBPACK_IMPORTED_MODULE_20__ = __webpack_require__(
    /*! ./link/link.component */
    "./src/app/link/link.component.ts");
    /* harmony import */


    var _footer_footer_component__WEBPACK_IMPORTED_MODULE_21__ = __webpack_require__(
    /*! ./footer/footer.component */
    "./src/app/footer/footer.component.ts");

    var AppModule = function AppModule() {
      _classCallCheck(this, AppModule);
    };

    AppModule = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_2__["NgModule"])({
      declarations: [_app_component__WEBPACK_IMPORTED_MODULE_9__["AppComponent"], _docs_menu_menu_component__WEBPACK_IMPORTED_MODULE_10__["DocsMenuComponent"], _docs_docs_component__WEBPACK_IMPORTED_MODULE_11__["DocsComponent"], _podcasts_menu_menu_component__WEBPACK_IMPORTED_MODULE_12__["PodcastsMenuComponent"], _podcasts_podcasts_component__WEBPACK_IMPORTED_MODULE_13__["PodcastsComponent"], _doc_menu_menu_component__WEBPACK_IMPORTED_MODULE_14__["DocMenuComponent"], _doc_doc_component__WEBPACK_IMPORTED_MODULE_15__["DocComponent"], _tracks_tracks_component__WEBPACK_IMPORTED_MODULE_16__["TracksComponent"], _section_section_component__WEBPACK_IMPORTED_MODULE_18__["SectionComponent"], _video_video_component__WEBPACK_IMPORTED_MODULE_19__["VideoComponent"], _link_link_component__WEBPACK_IMPORTED_MODULE_20__["LinkComponent"], _footer_footer_component__WEBPACK_IMPORTED_MODULE_21__["FooterComponent"], _events_events_component__WEBPACK_IMPORTED_MODULE_17__["EventsComponent"]],
      imports: [_angular_platform_browser__WEBPACK_IMPORTED_MODULE_1__["BrowserModule"], _angular_router__WEBPACK_IMPORTED_MODULE_3__["RouterModule"].forRoot([{
        path: '',
        component: _podcasts_podcasts_component__WEBPACK_IMPORTED_MODULE_13__["PodcastsComponent"]
      }, {
        path: ':type',
        component: _docs_docs_component__WEBPACK_IMPORTED_MODULE_11__["DocsComponent"]
      }, {
        path: 'docs/:id',
        component: _doc_doc_component__WEBPACK_IMPORTED_MODULE_15__["DocComponent"]
      }]), _angular_forms__WEBPACK_IMPORTED_MODULE_4__["FormsModule"], _angular_common_http__WEBPACK_IMPORTED_MODULE_5__["HttpClientModule"], ngx_infinite_scroll__WEBPACK_IMPORTED_MODULE_6__["InfiniteScrollModule"], _angular_youtube_player__WEBPACK_IMPORTED_MODULE_8__["YouTubePlayerModule"], _angular_common__WEBPACK_IMPORTED_MODULE_7__["CommonModule"]],
      providers: [],
      bootstrap: [_app_component__WEBPACK_IMPORTED_MODULE_9__["AppComponent"]]
    })], AppModule);
    /***/
  },

  /***/
  "./src/app/app.service.ts":
  /*!********************************!*\
    !*** ./src/app/app.service.ts ***!
    \********************************/

  /*! exports provided: AppService */

  /***/
  function srcAppAppServiceTs(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "AppService", function () {
      return AppService;
    });
    /* harmony import */


    var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(
    /*! tslib */
    "./node_modules/tslib/tslib.es6.js");
    /* harmony import */


    var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(
    /*! @angular/core */
    "./node_modules/@angular/core/fesm2015/core.js");

    var AppService = function AppService() {
      _classCallCheck(this, AppService);
    };

    AppService = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Injectable"])({
      providedIn: 'root'
    })], AppService);
    /***/
  },

  /***/
  "./src/app/doc/doc.component.css":
  /*!***************************************!*\
    !*** ./src/app/doc/doc.component.css ***!
    \***************************************/

  /*! exports provided: default */

  /***/
  function srcAppDocDocComponentCss(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = ".close {\n  bottom: 5px;\n  right: 5px;\n  position: absolute;\n}\n\n.icon {\n  border-radius: 50%;\n  background: #FFFFFF;\n  width: 30px;\n  min-width: 30px;\n  height: 30px;\n  min-height: 30px;\n  cursor: pointer;\n}\n\n.songkick {\n  display: inline-block;\n  -webkit-mask: url(\"/images/songkick.svg\");\n          mask: url(\"/images/songkick.svg\");\n  width: 30px;\n  height: 30px;\n  background: #F80046; \n  cursor: pointer;\n}\n\nh2.vote {\n  border-radius: 50%;\n  width: 30px;\n  height: 30px;\n  background: #18A2B8;\n\n  color: #FFFFFF;\n  text-align: center;\n  padding-top: 8px;\n\n  position: absolute;\n  right: 10px;\n  top: 10px;\n  z-index: 20;\n}\n\ndiv.vote {\n  border-radius: 50%;\n  width: 30px;\n  height: 30px;\n  background: #18A2B8;\n\n  color: #FFFFFF;\n  text-align: center;\n  padding-top: 6px;\n\n  position: absolute;\n  right: 10px;\n  top: 10px;\n  z-index: 20;\n}\n\n.star {\n  display: inline-block;\n  -webkit-mask: url(\"/images/star.svg\");\n          mask: url(\"/images/star.svg\");\n  width: 20px;\n  height: 18px;\n  background: #FFFFFF;\n}\n\ndiv.cover {\n  width: 250px;\n  position: relative;\n  cursor: pointer;\n}\n\na.cover {\n  width: 250px;\n  height: 250px;\n  cursor: pointer;\n}\n\nimg.cover {\n  width: 250px;\n  height: 250px;\n  -o-object-fit:cover;\n     object-fit:cover;\n  cursor: pointer;\n}\n\n.play {\n  position: absolute;\n  display: inline-block;\n  -webkit-mask: url(\"/images/play.svg\");\n          mask: url(\"/images/play.svg\");\n  width: 30px;\n  height: 30px;\n  margin: 0px;\n  padding: 0px;\n  left: 110px;\n  top: 110px;\n  background: #FFFFFF;\n  opacity: 0.6;\n  cursor: pointer;\n}\n\n.player {\n  width: 100%;\n  height: 80px;\n  background: #193541;\n}\n\nimg.cover-player {\n  width: 80px;\n  height: 80px;\n  -o-object-fit:cover;\n     object-fit:cover;\n}\n\n.player-play {\n  display: inline-block;\n  -webkit-mask: url(\"/images/play.svg\");\n          mask: url(\"/images/play.svg\");\n  width: 30px;\n  height: 30px;\n  margin: 0px;\n  padding: 0px;\n  background: #FFFFFF;\n  cursor: pointer;\n}\n\n.player-pause {\n  display: inline-block;\n  -webkit-mask: url(\"/images/pause.svg\");\n          mask: url(\"/images/pause.svg\");\n  width: 30px;\n  height: 30px;\n  margin: 0px;\n  padding: 0px;\n  background: #FFFFFF;\n  cursor: pointer;\n}\n\n.player-forward {\n  display: inline-block;\n  -webkit-mask: url(\"/images/forward.svg\");\n          mask: url(\"/images/forward.svg\");\n  width: 30px;\n  height: 30px;\n  margin: 0px;\n  padding: 0px;\n  background: #FFFFFF;\n  cursor: pointer;\n}\n\n.player-backward {\n  display: inline-block;\n  -webkit-mask: url(\"/images/backward.svg\");\n          mask: url(\"/images/backward.svg\");\n  width: 30px;\n  height: 30px;\n  margin: 0px;\n  padding: 0px;\n  background: #FFFFFF;\n  cursor: pointer;\n}\n\n.player-time {\n  width: 50px;\n}\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInNyYy9hcHAvZG9jL2RvYy5jb21wb25lbnQuY3NzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUFBO0VBQ0UsV0FBVztFQUNYLFVBQVU7RUFDVixrQkFBa0I7QUFDcEI7O0FBRUE7RUFDRSxrQkFBa0I7RUFDbEIsbUJBQW1CO0VBQ25CLFdBQVc7RUFDWCxlQUFlO0VBQ2YsWUFBWTtFQUNaLGdCQUFnQjtFQUNoQixlQUFlO0FBQ2pCOztBQUVBO0VBQ0UscUJBQXFCO0VBQ3JCLHlDQUFpQztVQUFqQyxpQ0FBaUM7RUFDakMsV0FBVztFQUNYLFlBQVk7RUFDWixtQkFBbUI7RUFDbkIsZUFBZTtBQUNqQjs7QUFFQTtFQUNFLGtCQUFrQjtFQUNsQixXQUFXO0VBQ1gsWUFBWTtFQUNaLG1CQUFtQjs7RUFFbkIsY0FBYztFQUNkLGtCQUFrQjtFQUNsQixnQkFBZ0I7O0VBRWhCLGtCQUFrQjtFQUNsQixXQUFXO0VBQ1gsU0FBUztFQUNULFdBQVc7QUFDYjs7QUFFQTtFQUNFLGtCQUFrQjtFQUNsQixXQUFXO0VBQ1gsWUFBWTtFQUNaLG1CQUFtQjs7RUFFbkIsY0FBYztFQUNkLGtCQUFrQjtFQUNsQixnQkFBZ0I7O0VBRWhCLGtCQUFrQjtFQUNsQixXQUFXO0VBQ1gsU0FBUztFQUNULFdBQVc7QUFDYjs7QUFFQTtFQUNFLHFCQUFxQjtFQUNyQixxQ0FBNkI7VUFBN0IsNkJBQTZCO0VBQzdCLFdBQVc7RUFDWCxZQUFZO0VBQ1osbUJBQW1CO0FBQ3JCOztBQUVBO0VBQ0UsWUFBWTtFQUNaLGtCQUFrQjtFQUNsQixlQUFlO0FBQ2pCOztBQUVBO0VBQ0UsWUFBWTtFQUNaLGFBQWE7RUFDYixlQUFlO0FBQ2pCOztBQUVBO0VBQ0UsWUFBWTtFQUNaLGFBQWE7RUFDYixtQkFBZ0I7S0FBaEIsZ0JBQWdCO0VBQ2hCLGVBQWU7QUFDakI7O0FBRUE7RUFDRSxrQkFBa0I7RUFDbEIscUJBQXFCO0VBQ3JCLHFDQUE2QjtVQUE3Qiw2QkFBNkI7RUFDN0IsV0FBVztFQUNYLFlBQVk7RUFDWixXQUFXO0VBQ1gsWUFBWTtFQUNaLFdBQVc7RUFDWCxVQUFVO0VBQ1YsbUJBQW1CO0VBQ25CLFlBQVk7RUFDWixlQUFlO0FBQ2pCOztBQUVBO0VBQ0UsV0FBVztFQUNYLFlBQVk7RUFDWixtQkFBbUI7QUFDckI7O0FBRUE7RUFDRSxXQUFXO0VBQ1gsWUFBWTtFQUNaLG1CQUFnQjtLQUFoQixnQkFBZ0I7QUFDbEI7O0FBRUE7RUFDRSxxQkFBcUI7RUFDckIscUNBQTZCO1VBQTdCLDZCQUE2QjtFQUM3QixXQUFXO0VBQ1gsWUFBWTtFQUNaLFdBQVc7RUFDWCxZQUFZO0VBQ1osbUJBQW1CO0VBQ25CLGVBQWU7QUFDakI7O0FBRUE7RUFDRSxxQkFBcUI7RUFDckIsc0NBQThCO1VBQTlCLDhCQUE4QjtFQUM5QixXQUFXO0VBQ1gsWUFBWTtFQUNaLFdBQVc7RUFDWCxZQUFZO0VBQ1osbUJBQW1CO0VBQ25CLGVBQWU7QUFDakI7O0FBRUE7RUFDRSxxQkFBcUI7RUFDckIsd0NBQWdDO1VBQWhDLGdDQUFnQztFQUNoQyxXQUFXO0VBQ1gsWUFBWTtFQUNaLFdBQVc7RUFDWCxZQUFZO0VBQ1osbUJBQW1CO0VBQ25CLGVBQWU7QUFDakI7O0FBRUE7RUFDRSxxQkFBcUI7RUFDckIseUNBQWlDO1VBQWpDLGlDQUFpQztFQUNqQyxXQUFXO0VBQ1gsWUFBWTtFQUNaLFdBQVc7RUFDWCxZQUFZO0VBQ1osbUJBQW1CO0VBQ25CLGVBQWU7QUFDakI7O0FBRUE7RUFDRSxXQUFXO0FBQ2IiLCJmaWxlIjoic3JjL2FwcC9kb2MvZG9jLmNvbXBvbmVudC5jc3MiLCJzb3VyY2VzQ29udGVudCI6WyIuY2xvc2Uge1xuICBib3R0b206IDVweDtcbiAgcmlnaHQ6IDVweDtcbiAgcG9zaXRpb246IGFic29sdXRlO1xufVxuXG4uaWNvbiB7XG4gIGJvcmRlci1yYWRpdXM6IDUwJTtcbiAgYmFja2dyb3VuZDogI0ZGRkZGRjtcbiAgd2lkdGg6IDMwcHg7XG4gIG1pbi13aWR0aDogMzBweDtcbiAgaGVpZ2h0OiAzMHB4O1xuICBtaW4taGVpZ2h0OiAzMHB4O1xuICBjdXJzb3I6IHBvaW50ZXI7XG59XG5cbi5zb25na2ljayB7XG4gIGRpc3BsYXk6IGlubGluZS1ibG9jaztcbiAgbWFzazogdXJsKFwiL2ltYWdlcy9zb25na2ljay5zdmdcIik7XG4gIHdpZHRoOiAzMHB4O1xuICBoZWlnaHQ6IDMwcHg7XG4gIGJhY2tncm91bmQ6ICNGODAwNDY7IFxuICBjdXJzb3I6IHBvaW50ZXI7XG59XG5cbmgyLnZvdGUge1xuICBib3JkZXItcmFkaXVzOiA1MCU7XG4gIHdpZHRoOiAzMHB4O1xuICBoZWlnaHQ6IDMwcHg7XG4gIGJhY2tncm91bmQ6ICMxOEEyQjg7XG5cbiAgY29sb3I6ICNGRkZGRkY7XG4gIHRleHQtYWxpZ246IGNlbnRlcjtcbiAgcGFkZGluZy10b3A6IDhweDtcblxuICBwb3NpdGlvbjogYWJzb2x1dGU7XG4gIHJpZ2h0OiAxMHB4O1xuICB0b3A6IDEwcHg7XG4gIHotaW5kZXg6IDIwO1xufVxuXG5kaXYudm90ZSB7XG4gIGJvcmRlci1yYWRpdXM6IDUwJTtcbiAgd2lkdGg6IDMwcHg7XG4gIGhlaWdodDogMzBweDtcbiAgYmFja2dyb3VuZDogIzE4QTJCODtcblxuICBjb2xvcjogI0ZGRkZGRjtcbiAgdGV4dC1hbGlnbjogY2VudGVyO1xuICBwYWRkaW5nLXRvcDogNnB4O1xuXG4gIHBvc2l0aW9uOiBhYnNvbHV0ZTtcbiAgcmlnaHQ6IDEwcHg7XG4gIHRvcDogMTBweDtcbiAgei1pbmRleDogMjA7XG59XG5cbi5zdGFyIHtcbiAgZGlzcGxheTogaW5saW5lLWJsb2NrO1xuICBtYXNrOiB1cmwoXCIvaW1hZ2VzL3N0YXIuc3ZnXCIpO1xuICB3aWR0aDogMjBweDtcbiAgaGVpZ2h0OiAxOHB4O1xuICBiYWNrZ3JvdW5kOiAjRkZGRkZGO1xufVxuXG5kaXYuY292ZXIge1xuICB3aWR0aDogMjUwcHg7XG4gIHBvc2l0aW9uOiByZWxhdGl2ZTtcbiAgY3Vyc29yOiBwb2ludGVyO1xufVxuXG5hLmNvdmVyIHtcbiAgd2lkdGg6IDI1MHB4O1xuICBoZWlnaHQ6IDI1MHB4O1xuICBjdXJzb3I6IHBvaW50ZXI7XG59XG5cbmltZy5jb3ZlciB7XG4gIHdpZHRoOiAyNTBweDtcbiAgaGVpZ2h0OiAyNTBweDtcbiAgb2JqZWN0LWZpdDpjb3ZlcjtcbiAgY3Vyc29yOiBwb2ludGVyO1xufVxuXG4ucGxheSB7XG4gIHBvc2l0aW9uOiBhYnNvbHV0ZTtcbiAgZGlzcGxheTogaW5saW5lLWJsb2NrO1xuICBtYXNrOiB1cmwoXCIvaW1hZ2VzL3BsYXkuc3ZnXCIpO1xuICB3aWR0aDogMzBweDtcbiAgaGVpZ2h0OiAzMHB4O1xuICBtYXJnaW46IDBweDtcbiAgcGFkZGluZzogMHB4O1xuICBsZWZ0OiAxMTBweDtcbiAgdG9wOiAxMTBweDtcbiAgYmFja2dyb3VuZDogI0ZGRkZGRjtcbiAgb3BhY2l0eTogMC42O1xuICBjdXJzb3I6IHBvaW50ZXI7XG59XG5cbi5wbGF5ZXIge1xuICB3aWR0aDogMTAwJTtcbiAgaGVpZ2h0OiA4MHB4O1xuICBiYWNrZ3JvdW5kOiAjMTkzNTQxO1xufVxuXG5pbWcuY292ZXItcGxheWVyIHtcbiAgd2lkdGg6IDgwcHg7XG4gIGhlaWdodDogODBweDtcbiAgb2JqZWN0LWZpdDpjb3Zlcjtcbn1cblxuLnBsYXllci1wbGF5IHtcbiAgZGlzcGxheTogaW5saW5lLWJsb2NrO1xuICBtYXNrOiB1cmwoXCIvaW1hZ2VzL3BsYXkuc3ZnXCIpO1xuICB3aWR0aDogMzBweDtcbiAgaGVpZ2h0OiAzMHB4O1xuICBtYXJnaW46IDBweDtcbiAgcGFkZGluZzogMHB4O1xuICBiYWNrZ3JvdW5kOiAjRkZGRkZGO1xuICBjdXJzb3I6IHBvaW50ZXI7XG59XG5cbi5wbGF5ZXItcGF1c2Uge1xuICBkaXNwbGF5OiBpbmxpbmUtYmxvY2s7XG4gIG1hc2s6IHVybChcIi9pbWFnZXMvcGF1c2Uuc3ZnXCIpO1xuICB3aWR0aDogMzBweDtcbiAgaGVpZ2h0OiAzMHB4O1xuICBtYXJnaW46IDBweDtcbiAgcGFkZGluZzogMHB4O1xuICBiYWNrZ3JvdW5kOiAjRkZGRkZGO1xuICBjdXJzb3I6IHBvaW50ZXI7XG59XG5cbi5wbGF5ZXItZm9yd2FyZCB7XG4gIGRpc3BsYXk6IGlubGluZS1ibG9jaztcbiAgbWFzazogdXJsKFwiL2ltYWdlcy9mb3J3YXJkLnN2Z1wiKTtcbiAgd2lkdGg6IDMwcHg7XG4gIGhlaWdodDogMzBweDtcbiAgbWFyZ2luOiAwcHg7XG4gIHBhZGRpbmc6IDBweDtcbiAgYmFja2dyb3VuZDogI0ZGRkZGRjtcbiAgY3Vyc29yOiBwb2ludGVyO1xufVxuXG4ucGxheWVyLWJhY2t3YXJkIHtcbiAgZGlzcGxheTogaW5saW5lLWJsb2NrO1xuICBtYXNrOiB1cmwoXCIvaW1hZ2VzL2JhY2t3YXJkLnN2Z1wiKTtcbiAgd2lkdGg6IDMwcHg7XG4gIGhlaWdodDogMzBweDtcbiAgbWFyZ2luOiAwcHg7XG4gIHBhZGRpbmc6IDBweDtcbiAgYmFja2dyb3VuZDogI0ZGRkZGRjtcbiAgY3Vyc29yOiBwb2ludGVyO1xufVxuXG4ucGxheWVyLXRpbWUge1xuICB3aWR0aDogNTBweDtcbn1cbiJdfQ== */";
    /***/
  },

  /***/
  "./src/app/doc/doc.component.ts":
  /*!**************************************!*\
    !*** ./src/app/doc/doc.component.ts ***!
    \**************************************/

  /*! exports provided: DocComponent */

  /***/
  function srcAppDocDocComponentTs(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "DocComponent", function () {
      return DocComponent;
    });
    /* harmony import */


    var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(
    /*! tslib */
    "./node_modules/tslib/tslib.es6.js");
    /* harmony import */


    var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(
    /*! @angular/core */
    "./node_modules/@angular/core/fesm2015/core.js");
    /* harmony import */


    var _angular_router__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(
    /*! @angular/router */
    "./node_modules/@angular/router/fesm2015/router.js");
    /* harmony import */


    var _angular_common_http__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(
    /*! @angular/common/http */
    "./node_modules/@angular/common/fesm2015/http.js");
    /* harmony import */


    var _angular_platform_browser__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(
    /*! @angular/platform-browser */
    "./node_modules/@angular/platform-browser/fesm2015/platform-browser.js");

    var DocComponent =
    /*#__PURE__*/
    function () {
      function DocComponent(http, route, sanitizer) {
        _classCallCheck(this, DocComponent);

        this.http = http;
        this.route = route;
        this.sanitizer = sanitizer;
        this.error = null;
        this.id = null;
        this.doc = null;
        this.links = [];
        this.otherLinks = [];
        this.spotify = null;
        this.songkick = null;
        this.audio = null;
        this.audioCurrentTime = null;
        this.audioDuration = null;
      }

      _createClass(DocComponent, [{
        key: "ngOnInit",
        value: function ngOnInit() {
          var _this = this;

          this.route.paramMap.subscribe(function (params) {
            window.scrollTo(0, 0);
            _this.id = params.get('id');

            _this.loadDoc();
          });
        }
      }, {
        key: "loadDoc",
        value: function loadDoc() {
          var _this2 = this;

          this.http.get('/api/docs/' + this.id).subscribe(function (data) {
            return _this2.setDoc(data[0]);
          }, function (error) {
            return _this2.error = error;
          });
        }
      }, {
        key: "setDoc",
        value: function setDoc(doc) {
          this.doc = doc;
          this.links = [];
          this.otherLinks = [];
          this.spotify = null;
          this.songkick = null;

          if (this.audio) {
            this.audio.pause();
            this.audio = null;
            this.audioCurrentTime = null;
            this.audioDuration = null;
          }

          this.loadLinks();
        }
      }, {
        key: "loadLinks",
        value: function loadLinks() {
          var _this3 = this;

          this.http.get('/api/docs/' + this.id + '/links').subscribe(function (data) {
            return _this3.setLinks(data);
          }, function (error) {
            return _this3.error = error;
          });
        }
      }, {
        key: "setLinks",
        value: function setLinks(links) {
          var doc = this.doc;
          this.links = links.filter(function (link) {
            return doc.spartistid != null && doc.spartistid == link.spartistid;
          });
          this.otherLinks = links.filter(function (link) {
            return doc.spartistid == null || doc.spartistid != link.spartistid;
          });
        }
      }, {
        key: "spotifyURL",
        value: function spotifyURL() {
          return this.doc.type == 'podcast' ? this.sanitizer.bypassSecurityTrustResourceUrl('https://open.spotify.com/embed/track/' + this.spotify) : this.doc.spalbumid != null ? this.sanitizer.bypassSecurityTrustResourceUrl('https://open.spotify.com/embed/album/' + this.spotify) : this.sanitizer.bypassSecurityTrustResourceUrl('https://open.spotify.com/embed/artist/' + this.spotify);
        }
      }, {
        key: "loadAlbum",
        value: function loadAlbum() {
          var _this4 = this;

          if (this.doc.spartistid || this.doc.spalbumid) {
            this.spotify = this.doc.spalbumid != null ? this.doc.spalbumid : this.doc.spartistid;
          }

          if (this.doc.audio != null && this.audio == null) {
            this.audio = new Audio();
            this.audio.src = this.doc.audio;

            this.audio.ontimeupdate = function () {
              _this4.audioDuration = _this4.formatTime(_this4.audio.duration);
              _this4.audioCurrentTime = _this4.formatTime(_this4.audio.currentTime);
            };

            this.audio.load();
            this.audio.play();
          }
        }
      }, {
        key: "loadTrack",
        value: function loadTrack(track) {
          this.spotify = track.spotify;
        }
      }, {
        key: "close",
        value: function close(event) {
          this.spotify = null;

          if (this.audio) {
            this.audio.pause();
            this.audio = null;
            this.audioCurrentTime = null;
            this.audioDuration = null;
          }
        }
      }, {
        key: "playPause",
        value: function playPause(event) {
          if (this.audio.paused) {
            this.audio.play();
          } else {
            this.audio.pause();
          }
        }
      }, {
        key: "forward",
        value: function forward(event) {
          if (!this.audio.paused) {
            this.audio.currentTime += 60.0;
          }
        }
      }, {
        key: "backward",
        value: function backward(event) {
          if (!this.audio.paused) {
            this.audio.currentTime -= 60.0;
          }
        }
      }, {
        key: "isDesktop",
        value: function isDesktop() {
          var hasTouchScreen = false;

          if (window.navigator.maxTouchPoints > 0) {
            hasTouchScreen = true;
          } else if (window.navigator.msMaxTouchPoints > 0) {
            hasTouchScreen = true;
          } else {
            var mQ = window.matchMedia && matchMedia("(pointer:coarse)");

            if (mQ && mQ.media === "(pointer:coarse)") {
              hasTouchScreen = !!mQ.matches;
            } else {
              // Only as a last resort, fall back to user agent sniffing
              var ua = window.navigator.userAgent;
              hasTouchScreen = /\b(BlackBerry|webOS|iPhone|IEMobile)\b/i.test(ua) || /\b(Android|Windows Phone|iPad|iPod)\b/i.test(ua);
            }
          }

          return !hasTouchScreen;
        }
      }, {
        key: "loadEvents",
        value: function loadEvents(artistid) {
          this.songkick = artistid;
        }
      }, {
        key: "formatTime",
        value: function formatTime(seconds) {
          if (!isNaN(seconds)) {
            var minutes = Math.floor(seconds / 60);
            var mins = minutes >= 10 ? minutes : "0" + minutes;
            seconds = Math.floor(seconds % 60);
            var secs = seconds >= 10 ? seconds : "0" + seconds;
            return mins + ":" + secs;
          }
        }
      }]);

      return DocComponent;
    }();

    DocComponent.ctorParameters = function () {
      return [{
        type: _angular_common_http__WEBPACK_IMPORTED_MODULE_3__["HttpClient"]
      }, {
        type: _angular_router__WEBPACK_IMPORTED_MODULE_2__["ActivatedRoute"]
      }, {
        type: _angular_platform_browser__WEBPACK_IMPORTED_MODULE_4__["DomSanitizer"]
      }];
    };

    DocComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
      selector: 'app-doc',
      template: tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! raw-loader!./doc.component.html */
      "./node_modules/raw-loader/dist/cjs.js!./src/app/doc/doc.component.html")).default,
      styles: [tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! ./doc.component.css */
      "./src/app/doc/doc.component.css")).default]
    })], DocComponent);
    /***/
  },

  /***/
  "./src/app/doc/menu/menu.component.css":
  /*!*********************************************!*\
    !*** ./src/app/doc/menu/menu.component.css ***!
    \*********************************************/

  /*! exports provided: default */

  /***/
  function srcAppDocMenuMenuComponentCss(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = "img.cover {\n    width: 40px;\n    height: 40px;\n    -o-object-fit:cover;\n       object-fit:cover;\n}\n\n.title {\n    width: 100%;\n    margin-right: 42px;\n    margin-left: 0px;\n}\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInNyYy9hcHAvZG9jL21lbnUvbWVudS5jb21wb25lbnQuY3NzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUFBO0lBQ0ksV0FBVztJQUNYLFlBQVk7SUFDWixtQkFBZ0I7T0FBaEIsZ0JBQWdCO0FBQ3BCOztBQUVBO0lBQ0ksV0FBVztJQUNYLGtCQUFrQjtJQUNsQixnQkFBZ0I7QUFDcEIiLCJmaWxlIjoic3JjL2FwcC9kb2MvbWVudS9tZW51LmNvbXBvbmVudC5jc3MiLCJzb3VyY2VzQ29udGVudCI6WyJpbWcuY292ZXIge1xuICAgIHdpZHRoOiA0MHB4O1xuICAgIGhlaWdodDogNDBweDtcbiAgICBvYmplY3QtZml0OmNvdmVyO1xufVxuXG4udGl0bGUge1xuICAgIHdpZHRoOiAxMDAlO1xuICAgIG1hcmdpbi1yaWdodDogNDJweDtcbiAgICBtYXJnaW4tbGVmdDogMHB4O1xufSJdfQ== */";
    /***/
  },

  /***/
  "./src/app/doc/menu/menu.component.ts":
  /*!********************************************!*\
    !*** ./src/app/doc/menu/menu.component.ts ***!
    \********************************************/

  /*! exports provided: DocMenuComponent */

  /***/
  function srcAppDocMenuMenuComponentTs(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "DocMenuComponent", function () {
      return DocMenuComponent;
    });
    /* harmony import */


    var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(
    /*! tslib */
    "./node_modules/tslib/tslib.es6.js");
    /* harmony import */


    var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(
    /*! @angular/core */
    "./node_modules/@angular/core/fesm2015/core.js");
    /* harmony import */


    var _angular_common__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(
    /*! @angular/common */
    "./node_modules/@angular/common/fesm2015/common.js");
    /* harmony import */


    var _doc_component__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(
    /*! ../doc.component */
    "./src/app/doc/doc.component.ts");

    var DocMenuComponent =
    /*#__PURE__*/
    function () {
      function DocMenuComponent(component, location) {
        _classCallCheck(this, DocMenuComponent);

        this.component = component;
        this.location = location;
      }

      _createClass(DocMenuComponent, [{
        key: "ngOnInit",
        value: function ngOnInit() {}
      }, {
        key: "back",
        value: function back() {
          this.location.back();
        }
      }]);

      return DocMenuComponent;
    }();

    DocMenuComponent.ctorParameters = function () {
      return [{
        type: _doc_component__WEBPACK_IMPORTED_MODULE_3__["DocComponent"]
      }, {
        type: _angular_common__WEBPACK_IMPORTED_MODULE_2__["Location"]
      }];
    };

    DocMenuComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
      selector: 'doc-menu',
      template: tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! raw-loader!./menu.component.html */
      "./node_modules/raw-loader/dist/cjs.js!./src/app/doc/menu/menu.component.html")).default,
      styles: [tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! ./menu.component.css */
      "./src/app/doc/menu/menu.component.css")).default]
    })], DocMenuComponent);
    /***/
  },

  /***/
  "./src/app/docs/docs.component.css":
  /*!*****************************************!*\
    !*** ./src/app/docs/docs.component.css ***!
    \*****************************************/

  /*! exports provided: default */

  /***/
  function srcAppDocsDocsComponentCss(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = ".fill { \n    min-height: calc(100vh - 135px);\n}\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInNyYy9hcHAvZG9jcy9kb2NzLmNvbXBvbmVudC5jc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUE7SUFDSSwrQkFBK0I7QUFDbkMiLCJmaWxlIjoic3JjL2FwcC9kb2NzL2RvY3MuY29tcG9uZW50LmNzcyIsInNvdXJjZXNDb250ZW50IjpbIi5maWxsIHsgXG4gICAgbWluLWhlaWdodDogY2FsYygxMDB2aCAtIDEzNXB4KTtcbn0iXX0= */";
    /***/
  },

  /***/
  "./src/app/docs/docs.component.ts":
  /*!****************************************!*\
    !*** ./src/app/docs/docs.component.ts ***!
    \****************************************/

  /*! exports provided: DocsComponent */

  /***/
  function srcAppDocsDocsComponentTs(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "DocsComponent", function () {
      return DocsComponent;
    });
    /* harmony import */


    var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(
    /*! tslib */
    "./node_modules/tslib/tslib.es6.js");
    /* harmony import */


    var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(
    /*! @angular/core */
    "./node_modules/@angular/core/fesm2015/core.js");
    /* harmony import */


    var _angular_common_http__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(
    /*! @angular/common/http */
    "./node_modules/@angular/common/fesm2015/http.js");
    /* harmony import */


    var _angular_router__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(
    /*! @angular/router */
    "./node_modules/@angular/router/fesm2015/router.js");

    var DocsComponent =
    /*#__PURE__*/
    function () {
      function DocsComponent(http, route) {
        _classCallCheck(this, DocsComponent);

        this.http = http;
        this.route = route;
        this.error = null;
        this.searchText = '';
        this.user = null;
        this.isStarred = false;
        this.albums = [];
        this.albumsPage = 0;
        this.interviews = [];
        this.interviewsPage = 0;
        this.podcasts = [];
        this.podcastsPage = 0;
        this.artists = [];
        this.artistsPage = 0;
        this.concerts = [];
        this.concertsPage = 0;
      }

      _createClass(DocsComponent, [{
        key: "ngOnInit",
        value: function ngOnInit() {
          var _this5 = this;

          this.loadUser();
          this.route.paramMap.subscribe(function (params) {
            window.scrollTo(0, 0);
            _this5.searchText = '';

            if (params.get('type') == 'starred') {
              _this5.isStarred = true;

              _this5.loadStarred();
            } else {
              _this5.isStarred = false;

              _this5.loadDocsAll();
            }
          });
        }
      }, {
        key: "loadDocsAll",
        value: function loadDocsAll() {
          this.loadDocs('albums');
          this.loadDocs('interviews');
          this.loadDocs('podcasts');
          this.loadDocs('artists');
          this.loadDocs('concerts');
        }
      }, {
        key: "scrollDocs",
        value: function scrollDocs(type) {
          var _this6 = this;

          if (!this.isStarred) {
            var page = 0;

            if (type == 'albums') {
              this.albumsPage++;
              page = this.albumsPage;
            } else if (type == 'interviews') {
              this.interviewsPage++;
              page = this.interviewsPage;
            } else if (type == 'podcasts') {
              this.podcastsPage++;
              page = this.podcastsPage;
            } else if (type == 'artists') {
              this.artistsPage++;
              page = this.artistsPage;
            } else if (type == 'concerts') {
              this.concertsPage++;
              page = this.concertsPage;
            }

            this.http.get('/api/docs/' + type + '?p=' + page + '&q=' + this.searchText).subscribe(function (data) {
              return _this6.docsLoaded(type, data);
            }, function (error) {
              return _this6.error = error;
            });
          }
        }
      }, {
        key: "loadDocs",
        value: function loadDocs(type) {
          var _this7 = this;

          var page = 0;

          if (type == 'albums') {
            this.albumsPage = 0;
            this.albums = [];
          } else if (type == 'interviews') {
            this.interviewsPage = 0;
            this.interviews = [];
          } else if (type == 'podcasts') {
            this.podcastsPage = 0;
            this.podcasts = [];
          } else if (type == 'artists') {
            this.artistsPage = 0;
            this.artists = [];
          } else if (type == 'concerts') {
            this.concertsPage = 0;
            this.concerts = [];
          }

          this.http.get('/api/docs/' + type + '?p=' + page + '&q=' + this.searchText).subscribe(function (data) {
            return _this7.docsLoaded(type, data);
          }, function (error) {
            return _this7.error = error;
          });
        }
      }, {
        key: "loadStarred",
        value: function loadStarred() {
          var _this8 = this;

          this.albums = [];
          this.interviews = [];
          this.podcasts = [];
          this.artists = [];
          this.concerts = [];
          this.http.get('/api/docs/starred').subscribe(function (data) {
            return _this8.docsLoaded('starred', data);
          }, function (error) {
            return _this8.error = error;
          });
        }
      }, {
        key: "docsLoaded",
        value: function docsLoaded(type, data) {
          if (type == 'albums') {
            this.albums.push.apply(this.albums, data);
          } else if (type == 'interviews') {
            this.interviews.push.apply(this.interviews, data);
          } else if (type == 'podcasts') {
            this.podcasts.push.apply(this.podcasts, data);
          } else if (type == 'artists') {
            this.artists.push.apply(this.artists, data);
          } else if (type == 'concerts') {
            this.concerts.push.apply(this.concerts, data);
          } else if (type == 'starred') {
            var albums = [];
            var interviews = [];
            var podcasts = [];
            var artists = [];
            var concerts = [];
            data.forEach(function (doc) {
              if (doc.type == 'album') {
                albums.push(doc);
              } else if (doc.type == 'interview') {
                interviews.push(doc);
              } else if (doc.type == 'podcast') {
                podcasts.push(doc);
              } else if (doc.type == 'artist') {
                artists.push(doc);
              } else if (doc.type == 'concert') {
                concerts.push(doc);
              }
            });
            this.albums.push.apply(this.albums, albums);
            this.interviews.push.apply(this.interviews, interviews);
            this.podcasts.push.apply(this.podcasts, podcasts);
            this.artists.push.apply(this.artists, artists);
            this.concerts.push.apply(this.concerts, concerts);
          }
        }
      }, {
        key: "loadUser",
        value: function loadUser() {
          var _this9 = this;

          if (this.user == null) {
            this.http.get('/api/user').subscribe(function (data) {
              return _this9.userLoaded(data);
            }, function (error) {
              return _this9.error = error;
            });
          }
        }
      }, {
        key: "userLoaded",
        value: function userLoaded(data) {
          if (data) {
            this.user = data.images[0].url;
          }
        }
      }, {
        key: "isDesktop",
        value: function isDesktop() {
          var hasTouchScreen = false;

          if (window.navigator.maxTouchPoints > 0) {
            hasTouchScreen = true;
          } else if (window.navigator.msMaxTouchPoints > 0) {
            hasTouchScreen = true;
          } else {
            var mQ = window.matchMedia && matchMedia("(pointer:coarse)");

            if (mQ && mQ.media === "(pointer:coarse)") {
              hasTouchScreen = !!mQ.matches;
            } else {
              // Only as a last resort, fall back to user agent sniffing
              var ua = window.navigator.userAgent;
              hasTouchScreen = /\b(BlackBerry|webOS|iPhone|IEMobile)\b/i.test(ua) || /\b(Android|Windows Phone|iPad|iPod)\b/i.test(ua);
            }
          }

          return !hasTouchScreen;
        }
      }]);

      return DocsComponent;
    }();

    DocsComponent.ctorParameters = function () {
      return [{
        type: _angular_common_http__WEBPACK_IMPORTED_MODULE_2__["HttpClient"]
      }, {
        type: _angular_router__WEBPACK_IMPORTED_MODULE_3__["ActivatedRoute"]
      }];
    };

    DocsComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
      selector: 'app-docs',
      template: tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! raw-loader!./docs.component.html */
      "./node_modules/raw-loader/dist/cjs.js!./src/app/docs/docs.component.html")).default,
      styles: [tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! ./docs.component.css */
      "./src/app/docs/docs.component.css")).default]
    })], DocsComponent);
    /***/
  },

  /***/
  "./src/app/docs/menu/menu.component.css":
  /*!**********************************************!*\
    !*** ./src/app/docs/menu/menu.component.css ***!
    \**********************************************/

  /*! exports provided: default */

  /***/
  function srcAppDocsMenuMenuComponentCss(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = ".menu-item {\n    height: 40px;\n    padding-top: 8px;\n    cursor: pointer;\n}\n\n.search {\n    width: 100%;\n}\n\n.unselected {\n    border-bottom: 4px solid #18A2B8;\n}\n\n.selected {\n    border-bottom: 4px solid #000000;\n}\n\n.icon {\n    border-radius: 50%;\n    background: #FFFFFF;\n    width: 30px;\n    height: 30px;\n    cursor: pointer;\n}\n\nspan.spotify {\n    display: inline-block;\n    -webkit-mask: url(\"/images/spotify.svg\");\n            mask: url(\"/images/spotify.svg\");\n    width: 30px;\n    height: 30px;\n    background: #18A2B8;\n    cursor: pointer;\n}\n\nimg.spotify {\n    -o-object-fit: cover;\n       object-fit: cover;\n    border-radius: 50%;\n    width: 30px;\n    height: 30px;\n}\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInNyYy9hcHAvZG9jcy9tZW51L21lbnUuY29tcG9uZW50LmNzcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQTtJQUNJLFlBQVk7SUFDWixnQkFBZ0I7SUFDaEIsZUFBZTtBQUNuQjs7QUFFQTtJQUNJLFdBQVc7QUFDZjs7QUFFQTtJQUNJLGdDQUFnQztBQUNwQzs7QUFFQTtJQUNJLGdDQUFnQztBQUNwQzs7QUFFQTtJQUNJLGtCQUFrQjtJQUNsQixtQkFBbUI7SUFDbkIsV0FBVztJQUNYLFlBQVk7SUFDWixlQUFlO0FBQ25COztBQUVBO0lBQ0kscUJBQXFCO0lBQ3JCLHdDQUFnQztZQUFoQyxnQ0FBZ0M7SUFDaEMsV0FBVztJQUNYLFlBQVk7SUFDWixtQkFBbUI7SUFDbkIsZUFBZTtBQUNuQjs7QUFFQTtJQUNJLG9CQUFpQjtPQUFqQixpQkFBaUI7SUFDakIsa0JBQWtCO0lBQ2xCLFdBQVc7SUFDWCxZQUFZO0FBQ2hCIiwiZmlsZSI6InNyYy9hcHAvZG9jcy9tZW51L21lbnUuY29tcG9uZW50LmNzcyIsInNvdXJjZXNDb250ZW50IjpbIi5tZW51LWl0ZW0ge1xuICAgIGhlaWdodDogNDBweDtcbiAgICBwYWRkaW5nLXRvcDogOHB4O1xuICAgIGN1cnNvcjogcG9pbnRlcjtcbn1cblxuLnNlYXJjaCB7XG4gICAgd2lkdGg6IDEwMCU7XG59XG5cbi51bnNlbGVjdGVkIHtcbiAgICBib3JkZXItYm90dG9tOiA0cHggc29saWQgIzE4QTJCODtcbn1cblxuLnNlbGVjdGVkIHtcbiAgICBib3JkZXItYm90dG9tOiA0cHggc29saWQgIzAwMDAwMDtcbn1cblxuLmljb24ge1xuICAgIGJvcmRlci1yYWRpdXM6IDUwJTtcbiAgICBiYWNrZ3JvdW5kOiAjRkZGRkZGO1xuICAgIHdpZHRoOiAzMHB4O1xuICAgIGhlaWdodDogMzBweDtcbiAgICBjdXJzb3I6IHBvaW50ZXI7XG59XG5cbnNwYW4uc3BvdGlmeSB7XG4gICAgZGlzcGxheTogaW5saW5lLWJsb2NrO1xuICAgIG1hc2s6IHVybChcIi9pbWFnZXMvc3BvdGlmeS5zdmdcIik7XG4gICAgd2lkdGg6IDMwcHg7XG4gICAgaGVpZ2h0OiAzMHB4O1xuICAgIGJhY2tncm91bmQ6ICMxOEEyQjg7XG4gICAgY3Vyc29yOiBwb2ludGVyO1xufVxuXG5pbWcuc3BvdGlmeSB7XG4gICAgb2JqZWN0LWZpdDogY292ZXI7XG4gICAgYm9yZGVyLXJhZGl1czogNTAlO1xuICAgIHdpZHRoOiAzMHB4O1xuICAgIGhlaWdodDogMzBweDtcbn1cbiJdfQ== */";
    /***/
  },

  /***/
  "./src/app/docs/menu/menu.component.ts":
  /*!*********************************************!*\
    !*** ./src/app/docs/menu/menu.component.ts ***!
    \*********************************************/

  /*! exports provided: DocsMenuComponent */

  /***/
  function srcAppDocsMenuMenuComponentTs(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "DocsMenuComponent", function () {
      return DocsMenuComponent;
    });
    /* harmony import */


    var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(
    /*! tslib */
    "./node_modules/tslib/tslib.es6.js");
    /* harmony import */


    var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(
    /*! @angular/core */
    "./node_modules/@angular/core/fesm2015/core.js");
    /* harmony import */


    var _docs_component__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(
    /*! ../docs.component */
    "./src/app/docs/docs.component.ts");

    var DocsMenuComponent =
    /*#__PURE__*/
    function () {
      function DocsMenuComponent(docs) {
        _classCallCheck(this, DocsMenuComponent);

        this.docs = docs;
      }

      _createClass(DocsMenuComponent, [{
        key: "ngOnInit",
        value: function ngOnInit() {}
      }]);

      return DocsMenuComponent;
    }();

    DocsMenuComponent.ctorParameters = function () {
      return [{
        type: _docs_component__WEBPACK_IMPORTED_MODULE_2__["DocsComponent"]
      }];
    };

    DocsMenuComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
      selector: 'docs-menu',
      template: tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! raw-loader!./menu.component.html */
      "./node_modules/raw-loader/dist/cjs.js!./src/app/docs/menu/menu.component.html")).default,
      styles: [tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! ./menu.component.css */
      "./src/app/docs/menu/menu.component.css")).default]
    })], DocsMenuComponent);
    /***/
  },

  /***/
  "./src/app/events/events.component.css":
  /*!*********************************************!*\
    !*** ./src/app/events/events.component.css ***!
    \*********************************************/

  /*! exports provided: default */

  /***/
  function srcAppEventsEventsComponentCss(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = ".modal-bg {\n    position: fixed;\n    top: 0;\n    left: 0;\n    width: 100vw;\n    height: 100vh;\n    z-index: 30;\n    background: rgba(0, 0, 0, 0.75);\n    cursor: pointer;\n}\n\n.modal-box {\n    width: 80vw;\n    max-width: 600px;\n    max-height: 80vh;\n    z-index: 40;\n    border-style: solid;\n    border-width: 1px;\n    border-color: white;\n}\n\n.list-group-item {\n    background-color: transparent;\n    border-style: none;\n    cursor: pointer;\n}\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInNyYy9hcHAvZXZlbnRzL2V2ZW50cy5jb21wb25lbnQuY3NzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUFBO0lBQ0ksZUFBZTtJQUNmLE1BQU07SUFDTixPQUFPO0lBQ1AsWUFBWTtJQUNaLGFBQWE7SUFDYixXQUFXO0lBQ1gsK0JBQStCO0lBQy9CLGVBQWU7QUFDbkI7O0FBRUE7SUFDSSxXQUFXO0lBQ1gsZ0JBQWdCO0lBQ2hCLGdCQUFnQjtJQUNoQixXQUFXO0lBQ1gsbUJBQW1CO0lBQ25CLGlCQUFpQjtJQUNqQixtQkFBbUI7QUFDdkI7O0FBRUE7SUFDSSw2QkFBNkI7SUFDN0Isa0JBQWtCO0lBQ2xCLGVBQWU7QUFDbkIiLCJmaWxlIjoic3JjL2FwcC9ldmVudHMvZXZlbnRzLmNvbXBvbmVudC5jc3MiLCJzb3VyY2VzQ29udGVudCI6WyIubW9kYWwtYmcge1xuICAgIHBvc2l0aW9uOiBmaXhlZDtcbiAgICB0b3A6IDA7XG4gICAgbGVmdDogMDtcbiAgICB3aWR0aDogMTAwdnc7XG4gICAgaGVpZ2h0OiAxMDB2aDtcbiAgICB6LWluZGV4OiAzMDtcbiAgICBiYWNrZ3JvdW5kOiByZ2JhKDAsIDAsIDAsIDAuNzUpO1xuICAgIGN1cnNvcjogcG9pbnRlcjtcbn1cblxuLm1vZGFsLWJveCB7XG4gICAgd2lkdGg6IDgwdnc7XG4gICAgbWF4LXdpZHRoOiA2MDBweDtcbiAgICBtYXgtaGVpZ2h0OiA4MHZoO1xuICAgIHotaW5kZXg6IDQwO1xuICAgIGJvcmRlci1zdHlsZTogc29saWQ7XG4gICAgYm9yZGVyLXdpZHRoOiAxcHg7XG4gICAgYm9yZGVyLWNvbG9yOiB3aGl0ZTtcbn1cblxuLmxpc3QtZ3JvdXAtaXRlbSB7XG4gICAgYmFja2dyb3VuZC1jb2xvcjogdHJhbnNwYXJlbnQ7XG4gICAgYm9yZGVyLXN0eWxlOiBub25lO1xuICAgIGN1cnNvcjogcG9pbnRlcjtcbn0iXX0= */";
    /***/
  },

  /***/
  "./src/app/events/events.component.ts":
  /*!********************************************!*\
    !*** ./src/app/events/events.component.ts ***!
    \********************************************/

  /*! exports provided: EventsComponent */

  /***/
  function srcAppEventsEventsComponentTs(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "EventsComponent", function () {
      return EventsComponent;
    });
    /* harmony import */


    var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(
    /*! tslib */
    "./node_modules/tslib/tslib.es6.js");
    /* harmony import */


    var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(
    /*! @angular/core */
    "./node_modules/@angular/core/fesm2015/core.js");
    /* harmony import */


    var _angular_common_http__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(
    /*! @angular/common/http */
    "./node_modules/@angular/common/fesm2015/http.js");

    var EventsComponent =
    /*#__PURE__*/
    function () {
      function EventsComponent(http) {
        _classCallCheck(this, EventsComponent);

        this.http = http;
        this._artist = null;
        this.events = [];
        this.error = null;
      }

      _createClass(EventsComponent, [{
        key: "ngOnInit",
        value: function ngOnInit() {}
      }, {
        key: "close",
        value: function close() {
          this.artist = null;
          this.events = [];
        }
      }, {
        key: "loadEvents",
        value: function loadEvents() {
          var _this10 = this;

          if (this.artist != null) {
            this.http.get('/api/events/' + this.artist).subscribe(function (data) {
              return _this10.setEvents(data);
            }, function (error) {
              return _this10.error = error;
            });
          }
        }
      }, {
        key: "setEvents",
        value: function setEvents(events) {
          if (typeof events != 'undefined' && events != null) {
            this.events.push.apply(this.events, events);
          }
        }
      }, {
        key: "artist",
        get: function get() {
          return this._artist;
        },
        set: function set(value) {
          this._artist = value;
          this.loadEvents();
        }
      }]);

      return EventsComponent;
    }();

    EventsComponent.ctorParameters = function () {
      return [{
        type: _angular_common_http__WEBPACK_IMPORTED_MODULE_2__["HttpClient"]
      }];
    };

    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])()], EventsComponent.prototype, "artist", null);
    EventsComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
      selector: 'app-events',
      template: tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! raw-loader!./events.component.html */
      "./node_modules/raw-loader/dist/cjs.js!./src/app/events/events.component.html")).default,
      styles: [tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! ./events.component.css */
      "./src/app/events/events.component.css")).default]
    })], EventsComponent);
    /***/
  },

  /***/
  "./src/app/footer/footer.component.css":
  /*!*********************************************!*\
    !*** ./src/app/footer/footer.component.css ***!
    \*********************************************/

  /*! exports provided: default */

  /***/
  function srcAppFooterFooterComponentCss(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = ".footer-copyright {\n  height: 80px;\n}\n\n.icon {\n  border-radius: 50%;\n  background: #FFFFFF;\n  width: 30px;\n  height: 30px;\n  cursor: pointer;\n}\n\n.cc {\n  display: inline-block;\n  -webkit-mask: url(\"/images/cc.svg\");\n          mask: url(\"/images/cc.svg\");\n  width: 20px;\n  height: 20px;\n  margin-top: 5px;\n  margin-left: 5px;\n  background: #18A2B8;\n  cursor: pointer;\n}\n\n.mail {\n  display: inline-block;\n  -webkit-mask: url(\"/images/mail.svg\");\n          mask: url(\"/images/mail.svg\");\n  width: 20px;\n  height: 20px;\n  margin-top: 5px;\n  margin-left: 5px;\n  background: #18A2B8;\n  cursor: pointer;\n}\n\n.linkedin {\n  display: inline-block;\n  -webkit-mask: url(\"/images/linkedin.svg\");\n          mask: url(\"/images/linkedin.svg\");\n  width: 17px;\n  height: 20px;\n  margin-top: 5px;\n  margin-left: 6px;\n  background: #18A2B8;\n  cursor: pointer;\n}\n\n.github {\n  display: inline-block;\n  -webkit-mask: url(\"/images/github.svg\");\n          mask: url(\"/images/github.svg\");\n  width: 20px;\n  height: 20px;\n  margin-top: 5px;\n  margin-left: 5px;\n  background: #18A2B8;\n  cursor: pointer;\n}\n\n.api {\n  border-radius: 50%;\n  background: #18A2B8;\n  width: 24px;\n  height: 24px;\n  padding-top: 7px;\n  margin-top: 3px;\n  margin-left: 3px;\n\n  text-align: center;\n  font-size: 9px;\n  cursor: pointer;\n}\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInNyYy9hcHAvZm9vdGVyL2Zvb3Rlci5jb21wb25lbnQuY3NzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUFBO0VBQ0UsWUFBWTtBQUNkOztBQUVBO0VBQ0Usa0JBQWtCO0VBQ2xCLG1CQUFtQjtFQUNuQixXQUFXO0VBQ1gsWUFBWTtFQUNaLGVBQWU7QUFDakI7O0FBRUE7RUFDRSxxQkFBcUI7RUFDckIsbUNBQTJCO1VBQTNCLDJCQUEyQjtFQUMzQixXQUFXO0VBQ1gsWUFBWTtFQUNaLGVBQWU7RUFDZixnQkFBZ0I7RUFDaEIsbUJBQW1CO0VBQ25CLGVBQWU7QUFDakI7O0FBRUE7RUFDRSxxQkFBcUI7RUFDckIscUNBQTZCO1VBQTdCLDZCQUE2QjtFQUM3QixXQUFXO0VBQ1gsWUFBWTtFQUNaLGVBQWU7RUFDZixnQkFBZ0I7RUFDaEIsbUJBQW1CO0VBQ25CLGVBQWU7QUFDakI7O0FBRUE7RUFDRSxxQkFBcUI7RUFDckIseUNBQWlDO1VBQWpDLGlDQUFpQztFQUNqQyxXQUFXO0VBQ1gsWUFBWTtFQUNaLGVBQWU7RUFDZixnQkFBZ0I7RUFDaEIsbUJBQW1CO0VBQ25CLGVBQWU7QUFDakI7O0FBRUE7RUFDRSxxQkFBcUI7RUFDckIsdUNBQStCO1VBQS9CLCtCQUErQjtFQUMvQixXQUFXO0VBQ1gsWUFBWTtFQUNaLGVBQWU7RUFDZixnQkFBZ0I7RUFDaEIsbUJBQW1CO0VBQ25CLGVBQWU7QUFDakI7O0FBRUE7RUFDRSxrQkFBa0I7RUFDbEIsbUJBQW1CO0VBQ25CLFdBQVc7RUFDWCxZQUFZO0VBQ1osZ0JBQWdCO0VBQ2hCLGVBQWU7RUFDZixnQkFBZ0I7O0VBRWhCLGtCQUFrQjtFQUNsQixjQUFjO0VBQ2QsZUFBZTtBQUNqQiIsImZpbGUiOiJzcmMvYXBwL2Zvb3Rlci9mb290ZXIuY29tcG9uZW50LmNzcyIsInNvdXJjZXNDb250ZW50IjpbIi5mb290ZXItY29weXJpZ2h0IHtcbiAgaGVpZ2h0OiA4MHB4O1xufVxuXG4uaWNvbiB7XG4gIGJvcmRlci1yYWRpdXM6IDUwJTtcbiAgYmFja2dyb3VuZDogI0ZGRkZGRjtcbiAgd2lkdGg6IDMwcHg7XG4gIGhlaWdodDogMzBweDtcbiAgY3Vyc29yOiBwb2ludGVyO1xufVxuXG4uY2Mge1xuICBkaXNwbGF5OiBpbmxpbmUtYmxvY2s7XG4gIG1hc2s6IHVybChcIi9pbWFnZXMvY2Muc3ZnXCIpO1xuICB3aWR0aDogMjBweDtcbiAgaGVpZ2h0OiAyMHB4O1xuICBtYXJnaW4tdG9wOiA1cHg7XG4gIG1hcmdpbi1sZWZ0OiA1cHg7XG4gIGJhY2tncm91bmQ6ICMxOEEyQjg7XG4gIGN1cnNvcjogcG9pbnRlcjtcbn1cblxuLm1haWwge1xuICBkaXNwbGF5OiBpbmxpbmUtYmxvY2s7XG4gIG1hc2s6IHVybChcIi9pbWFnZXMvbWFpbC5zdmdcIik7XG4gIHdpZHRoOiAyMHB4O1xuICBoZWlnaHQ6IDIwcHg7XG4gIG1hcmdpbi10b3A6IDVweDtcbiAgbWFyZ2luLWxlZnQ6IDVweDtcbiAgYmFja2dyb3VuZDogIzE4QTJCODtcbiAgY3Vyc29yOiBwb2ludGVyO1xufVxuXG4ubGlua2VkaW4ge1xuICBkaXNwbGF5OiBpbmxpbmUtYmxvY2s7XG4gIG1hc2s6IHVybChcIi9pbWFnZXMvbGlua2VkaW4uc3ZnXCIpO1xuICB3aWR0aDogMTdweDtcbiAgaGVpZ2h0OiAyMHB4O1xuICBtYXJnaW4tdG9wOiA1cHg7XG4gIG1hcmdpbi1sZWZ0OiA2cHg7XG4gIGJhY2tncm91bmQ6ICMxOEEyQjg7XG4gIGN1cnNvcjogcG9pbnRlcjtcbn1cblxuLmdpdGh1YiB7XG4gIGRpc3BsYXk6IGlubGluZS1ibG9jaztcbiAgbWFzazogdXJsKFwiL2ltYWdlcy9naXRodWIuc3ZnXCIpO1xuICB3aWR0aDogMjBweDtcbiAgaGVpZ2h0OiAyMHB4O1xuICBtYXJnaW4tdG9wOiA1cHg7XG4gIG1hcmdpbi1sZWZ0OiA1cHg7XG4gIGJhY2tncm91bmQ6ICMxOEEyQjg7XG4gIGN1cnNvcjogcG9pbnRlcjtcbn1cblxuLmFwaSB7XG4gIGJvcmRlci1yYWRpdXM6IDUwJTtcbiAgYmFja2dyb3VuZDogIzE4QTJCODtcbiAgd2lkdGg6IDI0cHg7XG4gIGhlaWdodDogMjRweDtcbiAgcGFkZGluZy10b3A6IDdweDtcbiAgbWFyZ2luLXRvcDogM3B4O1xuICBtYXJnaW4tbGVmdDogM3B4O1xuXG4gIHRleHQtYWxpZ246IGNlbnRlcjtcbiAgZm9udC1zaXplOiA5cHg7XG4gIGN1cnNvcjogcG9pbnRlcjtcbn0iXX0= */";
    /***/
  },

  /***/
  "./src/app/footer/footer.component.ts":
  /*!********************************************!*\
    !*** ./src/app/footer/footer.component.ts ***!
    \********************************************/

  /*! exports provided: FooterComponent */

  /***/
  function srcAppFooterFooterComponentTs(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "FooterComponent", function () {
      return FooterComponent;
    });
    /* harmony import */


    var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(
    /*! tslib */
    "./node_modules/tslib/tslib.es6.js");
    /* harmony import */


    var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(
    /*! @angular/core */
    "./node_modules/@angular/core/fesm2015/core.js");

    var FooterComponent =
    /*#__PURE__*/
    function () {
      function FooterComponent() {
        _classCallCheck(this, FooterComponent);
      }

      _createClass(FooterComponent, [{
        key: "ngOnInit",
        value: function ngOnInit() {}
      }]);

      return FooterComponent;
    }();

    FooterComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
      selector: 'app-footer',
      template: tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! raw-loader!./footer.component.html */
      "./node_modules/raw-loader/dist/cjs.js!./src/app/footer/footer.component.html")).default,
      styles: [tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! ./footer.component.css */
      "./src/app/footer/footer.component.css")).default]
    })], FooterComponent);
    /***/
  },

  /***/
  "./src/app/link/link.component.css":
  /*!*****************************************!*\
    !*** ./src/app/link/link.component.css ***!
    \*****************************************/

  /*! exports provided: default */

  /***/
  function srcAppLinkLinkComponentCss(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = ".backward {\n  width: 40px;\n  height: 20px;\n  background: none;\n  cursor: pointer;\n}\n\n.previous {\n  display: inline-block;\n  -webkit-mask: url(\"/images/previous.svg\");\n          mask: url(\"/images/previous.svg\");\n  width: 10px;\n  height: 15px;\n  margin-top: 2px;\n  margin-left: 20px;\n  background: #18A2B8;\n  cursor: pointer;\n}\n\n.forward {\n  width: 40px;\n  height: 20px;\n  background: none;\n  cursor: pointer;\n}\n\n.next {\n  display: inline-block;\n  -webkit-mask: url(\"/images/next.svg\");\n          mask: url(\"/images/next.svg\");\n  width: 10px;\n  height: 15px;\n  margin-top: 2px;\n  margin-left: 20px;\n  background: #18A2B8;\n  cursor: pointer;\n}\n\ndiv.cover {\n  width: 120px;\n  position: relative;\n}\n\na.cover {\n  width: 120px;\n  height: 120px;\n}\n\nimg.cover {\n  width: 120px;\n  height: 120px;\n  -o-object-fit:cover;\n     object-fit:cover;\n}\n\n.card {\n  border-style: none;\n}\n\n.card-body {\n  position: relative;\n  width: 120px;\n  height: 100px;\n}\n\n.card-footer {\n  border-style: none;\n  background: none;\n  position: absolute;\n  bottom: 0px;\n}\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInNyYy9hcHAvbGluay9saW5rLmNvbXBvbmVudC5jc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUE7RUFDRSxXQUFXO0VBQ1gsWUFBWTtFQUNaLGdCQUFnQjtFQUNoQixlQUFlO0FBQ2pCOztBQUVBO0VBQ0UscUJBQXFCO0VBQ3JCLHlDQUFpQztVQUFqQyxpQ0FBaUM7RUFDakMsV0FBVztFQUNYLFlBQVk7RUFDWixlQUFlO0VBQ2YsaUJBQWlCO0VBQ2pCLG1CQUFtQjtFQUNuQixlQUFlO0FBQ2pCOztBQUVBO0VBQ0UsV0FBVztFQUNYLFlBQVk7RUFDWixnQkFBZ0I7RUFDaEIsZUFBZTtBQUNqQjs7QUFFQTtFQUNFLHFCQUFxQjtFQUNyQixxQ0FBNkI7VUFBN0IsNkJBQTZCO0VBQzdCLFdBQVc7RUFDWCxZQUFZO0VBQ1osZUFBZTtFQUNmLGlCQUFpQjtFQUNqQixtQkFBbUI7RUFDbkIsZUFBZTtBQUNqQjs7QUFFQTtFQUNFLFlBQVk7RUFDWixrQkFBa0I7QUFDcEI7O0FBRUE7RUFDRSxZQUFZO0VBQ1osYUFBYTtBQUNmOztBQUVBO0VBQ0UsWUFBWTtFQUNaLGFBQWE7RUFDYixtQkFBZ0I7S0FBaEIsZ0JBQWdCO0FBQ2xCOztBQUVBO0VBQ0Usa0JBQWtCO0FBQ3BCOztBQUVBO0VBQ0Usa0JBQWtCO0VBQ2xCLFlBQVk7RUFDWixhQUFhO0FBQ2Y7O0FBRUE7RUFDRSxrQkFBa0I7RUFDbEIsZ0JBQWdCO0VBQ2hCLGtCQUFrQjtFQUNsQixXQUFXO0FBQ2IiLCJmaWxlIjoic3JjL2FwcC9saW5rL2xpbmsuY29tcG9uZW50LmNzcyIsInNvdXJjZXNDb250ZW50IjpbIi5iYWNrd2FyZCB7XG4gIHdpZHRoOiA0MHB4O1xuICBoZWlnaHQ6IDIwcHg7XG4gIGJhY2tncm91bmQ6IG5vbmU7XG4gIGN1cnNvcjogcG9pbnRlcjtcbn1cblxuLnByZXZpb3VzIHtcbiAgZGlzcGxheTogaW5saW5lLWJsb2NrO1xuICBtYXNrOiB1cmwoXCIvaW1hZ2VzL3ByZXZpb3VzLnN2Z1wiKTtcbiAgd2lkdGg6IDEwcHg7XG4gIGhlaWdodDogMTVweDtcbiAgbWFyZ2luLXRvcDogMnB4O1xuICBtYXJnaW4tbGVmdDogMjBweDtcbiAgYmFja2dyb3VuZDogIzE4QTJCODtcbiAgY3Vyc29yOiBwb2ludGVyO1xufVxuXG4uZm9yd2FyZCB7XG4gIHdpZHRoOiA0MHB4O1xuICBoZWlnaHQ6IDIwcHg7XG4gIGJhY2tncm91bmQ6IG5vbmU7XG4gIGN1cnNvcjogcG9pbnRlcjtcbn1cblxuLm5leHQge1xuICBkaXNwbGF5OiBpbmxpbmUtYmxvY2s7XG4gIG1hc2s6IHVybChcIi9pbWFnZXMvbmV4dC5zdmdcIik7XG4gIHdpZHRoOiAxMHB4O1xuICBoZWlnaHQ6IDE1cHg7XG4gIG1hcmdpbi10b3A6IDJweDtcbiAgbWFyZ2luLWxlZnQ6IDIwcHg7XG4gIGJhY2tncm91bmQ6ICMxOEEyQjg7XG4gIGN1cnNvcjogcG9pbnRlcjtcbn1cblxuZGl2LmNvdmVyIHtcbiAgd2lkdGg6IDEyMHB4O1xuICBwb3NpdGlvbjogcmVsYXRpdmU7XG59XG5cbmEuY292ZXIge1xuICB3aWR0aDogMTIwcHg7XG4gIGhlaWdodDogMTIwcHg7XG59XG5cbmltZy5jb3ZlciB7XG4gIHdpZHRoOiAxMjBweDtcbiAgaGVpZ2h0OiAxMjBweDtcbiAgb2JqZWN0LWZpdDpjb3Zlcjtcbn1cblxuLmNhcmQge1xuICBib3JkZXItc3R5bGU6IG5vbmU7XG59XG5cbi5jYXJkLWJvZHkge1xuICBwb3NpdGlvbjogcmVsYXRpdmU7XG4gIHdpZHRoOiAxMjBweDtcbiAgaGVpZ2h0OiAxMDBweDtcbn1cblxuLmNhcmQtZm9vdGVyIHtcbiAgYm9yZGVyLXN0eWxlOiBub25lO1xuICBiYWNrZ3JvdW5kOiBub25lO1xuICBwb3NpdGlvbjogYWJzb2x1dGU7XG4gIGJvdHRvbTogMHB4O1xufSJdfQ== */";
    /***/
  },

  /***/
  "./src/app/link/link.component.ts":
  /*!****************************************!*\
    !*** ./src/app/link/link.component.ts ***!
    \****************************************/

  /*! exports provided: LinkComponent */

  /***/
  function srcAppLinkLinkComponentTs(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "LinkComponent", function () {
      return LinkComponent;
    });
    /* harmony import */


    var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(
    /*! tslib */
    "./node_modules/tslib/tslib.es6.js");
    /* harmony import */


    var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(
    /*! @angular/core */
    "./node_modules/@angular/core/fesm2015/core.js");
    /* harmony import */


    var _angular_platform_browser__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(
    /*! @angular/platform-browser */
    "./node_modules/@angular/platform-browser/fesm2015/platform-browser.js");
    /* harmony import */


    var _doc_doc_component__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(
    /*! ../doc/doc.component */
    "./src/app/doc/doc.component.ts");

    var LinkComponent =
    /*#__PURE__*/
    function () {
      function LinkComponent(component, sanitizer) {
        _classCallCheck(this, LinkComponent);

        this.component = component;
        this.sanitizer = sanitizer;
        this.links = [];
        this.scrollLinks = 0;
      }

      _createClass(LinkComponent, [{
        key: "ngOnInit",
        value: function ngOnInit() {}
      }, {
        key: "next",
        value: function next() {
          this.scrollLinks++;
          var slider = document.querySelector('#' + this.type);
          slider.scrollTo(126 * Math.floor(slider.clientWidth / 126) * this.scrollLinks, 0);
        }
      }, {
        key: "previous",
        value: function previous() {
          if (this.scrollLinks > 0) {
            this.scrollLinks--;
            var slider = document.querySelector('#' + this.type);
            slider.scrollTo(126 * Math.floor(slider.clientWidth / 126) * this.scrollLinks, 0);
          }
        }
      }]);

      return LinkComponent;
    }();

    LinkComponent.ctorParameters = function () {
      return [{
        type: _doc_doc_component__WEBPACK_IMPORTED_MODULE_3__["DocComponent"]
      }, {
        type: _angular_platform_browser__WEBPACK_IMPORTED_MODULE_2__["DomSanitizer"]
      }];
    };

    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])()], LinkComponent.prototype, "type", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])()], LinkComponent.prototype, "label", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])()], LinkComponent.prototype, "links", void 0);
    LinkComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
      selector: 'app-link',
      template: tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! raw-loader!./link.component.html */
      "./node_modules/raw-loader/dist/cjs.js!./src/app/link/link.component.html")).default,
      styles: [tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! ./link.component.css */
      "./src/app/link/link.component.css")).default]
    })], LinkComponent);
    /***/
  },

  /***/
  "./src/app/podcasts/menu/menu.component.css":
  /*!**************************************************!*\
    !*** ./src/app/podcasts/menu/menu.component.css ***!
    \**************************************************/

  /*! exports provided: default */

  /***/
  function srcAppPodcastsMenuMenuComponentCss(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = ".menu-item {\n    height: 40px;\n    padding-top: 8px;\n    cursor: pointer;\n}\n\n.search {\n    width: 100%;\n}\n\n.unselected {\n    border-bottom: 4px solid #18A2B8;\n}\n\n.selected {\n    border-bottom: 4px solid #000000;\n}\n\n.icon {\n    border-radius: 50%;\n    background: #FFFFFF;\n    width: 30px;\n    height: 30px;\n    cursor: pointer;\n}\n\nspan.spotify {\n    display: inline-block;\n    -webkit-mask: url(\"/images/spotify.svg\");\n            mask: url(\"/images/spotify.svg\");\n    width: 30px;\n    height: 30px;\n    background: #18A2B8;\n    cursor: pointer;\n}\n\nimg.spotify {\n    -o-object-fit: cover;\n       object-fit: cover;\n    border-radius: 50%;\n    width: 30px;\n    height: 30px;\n}\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInNyYy9hcHAvcG9kY2FzdHMvbWVudS9tZW51LmNvbXBvbmVudC5jc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUE7SUFDSSxZQUFZO0lBQ1osZ0JBQWdCO0lBQ2hCLGVBQWU7QUFDbkI7O0FBRUE7SUFDSSxXQUFXO0FBQ2Y7O0FBRUE7SUFDSSxnQ0FBZ0M7QUFDcEM7O0FBRUE7SUFDSSxnQ0FBZ0M7QUFDcEM7O0FBRUE7SUFDSSxrQkFBa0I7SUFDbEIsbUJBQW1CO0lBQ25CLFdBQVc7SUFDWCxZQUFZO0lBQ1osZUFBZTtBQUNuQjs7QUFFQTtJQUNJLHFCQUFxQjtJQUNyQix3Q0FBZ0M7WUFBaEMsZ0NBQWdDO0lBQ2hDLFdBQVc7SUFDWCxZQUFZO0lBQ1osbUJBQW1CO0lBQ25CLGVBQWU7QUFDbkI7O0FBRUE7SUFDSSxvQkFBaUI7T0FBakIsaUJBQWlCO0lBQ2pCLGtCQUFrQjtJQUNsQixXQUFXO0lBQ1gsWUFBWTtBQUNoQiIsImZpbGUiOiJzcmMvYXBwL3BvZGNhc3RzL21lbnUvbWVudS5jb21wb25lbnQuY3NzIiwic291cmNlc0NvbnRlbnQiOlsiLm1lbnUtaXRlbSB7XG4gICAgaGVpZ2h0OiA0MHB4O1xuICAgIHBhZGRpbmctdG9wOiA4cHg7XG4gICAgY3Vyc29yOiBwb2ludGVyO1xufVxuXG4uc2VhcmNoIHtcbiAgICB3aWR0aDogMTAwJTtcbn1cblxuLnVuc2VsZWN0ZWQge1xuICAgIGJvcmRlci1ib3R0b206IDRweCBzb2xpZCAjMThBMkI4O1xufVxuXG4uc2VsZWN0ZWQge1xuICAgIGJvcmRlci1ib3R0b206IDRweCBzb2xpZCAjMDAwMDAwO1xufVxuXG4uaWNvbiB7XG4gICAgYm9yZGVyLXJhZGl1czogNTAlO1xuICAgIGJhY2tncm91bmQ6ICNGRkZGRkY7XG4gICAgd2lkdGg6IDMwcHg7XG4gICAgaGVpZ2h0OiAzMHB4O1xuICAgIGN1cnNvcjogcG9pbnRlcjtcbn1cblxuc3Bhbi5zcG90aWZ5IHtcbiAgICBkaXNwbGF5OiBpbmxpbmUtYmxvY2s7XG4gICAgbWFzazogdXJsKFwiL2ltYWdlcy9zcG90aWZ5LnN2Z1wiKTtcbiAgICB3aWR0aDogMzBweDtcbiAgICBoZWlnaHQ6IDMwcHg7XG4gICAgYmFja2dyb3VuZDogIzE4QTJCODtcbiAgICBjdXJzb3I6IHBvaW50ZXI7XG59XG5cbmltZy5zcG90aWZ5IHtcbiAgICBvYmplY3QtZml0OiBjb3ZlcjtcbiAgICBib3JkZXItcmFkaXVzOiA1MCU7XG4gICAgd2lkdGg6IDMwcHg7XG4gICAgaGVpZ2h0OiAzMHB4O1xufVxuIl19 */";
    /***/
  },

  /***/
  "./src/app/podcasts/menu/menu.component.ts":
  /*!*************************************************!*\
    !*** ./src/app/podcasts/menu/menu.component.ts ***!
    \*************************************************/

  /*! exports provided: PodcastsMenuComponent */

  /***/
  function srcAppPodcastsMenuMenuComponentTs(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "PodcastsMenuComponent", function () {
      return PodcastsMenuComponent;
    });
    /* harmony import */


    var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(
    /*! tslib */
    "./node_modules/tslib/tslib.es6.js");
    /* harmony import */


    var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(
    /*! @angular/core */
    "./node_modules/@angular/core/fesm2015/core.js");
    /* harmony import */


    var _podcasts_component__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(
    /*! ../podcasts.component */
    "./src/app/podcasts/podcasts.component.ts");

    var PodcastsMenuComponent =
    /*#__PURE__*/
    function () {
      function PodcastsMenuComponent(docs) {
        _classCallCheck(this, PodcastsMenuComponent);

        this.docs = docs;
      }

      _createClass(PodcastsMenuComponent, [{
        key: "ngOnInit",
        value: function ngOnInit() {}
      }]);

      return PodcastsMenuComponent;
    }();

    PodcastsMenuComponent.ctorParameters = function () {
      return [{
        type: _podcasts_component__WEBPACK_IMPORTED_MODULE_2__["PodcastsComponent"]
      }];
    };

    PodcastsMenuComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
      selector: 'podcasts-menu',
      template: tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! raw-loader!./menu.component.html */
      "./node_modules/raw-loader/dist/cjs.js!./src/app/podcasts/menu/menu.component.html")).default,
      styles: [tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! ./menu.component.css */
      "./src/app/podcasts/menu/menu.component.css")).default]
    })], PodcastsMenuComponent);
    /***/
  },

  /***/
  "./src/app/podcasts/podcasts.component.css":
  /*!*************************************************!*\
    !*** ./src/app/podcasts/podcasts.component.css ***!
    \*************************************************/

  /*! exports provided: default */

  /***/
  function srcAppPodcastsPodcastsComponentCss(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = ".fill { \n    min-height: calc(100vh - 135px);\n}\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInNyYy9hcHAvcG9kY2FzdHMvcG9kY2FzdHMuY29tcG9uZW50LmNzcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQTtJQUNJLCtCQUErQjtBQUNuQyIsImZpbGUiOiJzcmMvYXBwL3BvZGNhc3RzL3BvZGNhc3RzLmNvbXBvbmVudC5jc3MiLCJzb3VyY2VzQ29udGVudCI6WyIuZmlsbCB7IFxuICAgIG1pbi1oZWlnaHQ6IGNhbGMoMTAwdmggLSAxMzVweCk7XG59Il19 */";
    /***/
  },

  /***/
  "./src/app/podcasts/podcasts.component.ts":
  /*!************************************************!*\
    !*** ./src/app/podcasts/podcasts.component.ts ***!
    \************************************************/

  /*! exports provided: PodcastsComponent */

  /***/
  function srcAppPodcastsPodcastsComponentTs(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "PodcastsComponent", function () {
      return PodcastsComponent;
    });
    /* harmony import */


    var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(
    /*! tslib */
    "./node_modules/tslib/tslib.es6.js");
    /* harmony import */


    var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(
    /*! @angular/core */
    "./node_modules/@angular/core/fesm2015/core.js");
    /* harmony import */


    var _angular_common_http__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(
    /*! @angular/common/http */
    "./node_modules/@angular/common/fesm2015/http.js");
    /* harmony import */


    var _angular_router__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(
    /*! @angular/router */
    "./node_modules/@angular/router/fesm2015/router.js");

    var PodcastsComponent =
    /*#__PURE__*/
    function () {
      function PodcastsComponent(http, route) {
        _classCallCheck(this, PodcastsComponent);

        this.http = http;
        this.route = route;
        this.error = null;
        this.searchText = '';
        this.user = null;
        this.isStarred = false;
        this.podcasts = [];
        this.podcastsPage = 0;
      }

      _createClass(PodcastsComponent, [{
        key: "ngOnInit",
        value: function ngOnInit() {
          var _this11 = this;

          this.loadUser();
          this.route.paramMap.subscribe(function (params) {
            window.scrollTo(0, 0);
            _this11.searchText = '';

            if (params.get('type') == 'starred') {
              _this11.isStarred = true;

              _this11.loadStarred();
            } else {
              _this11.isStarred = false;

              _this11.loadDocs();
            }
          });
        }
      }, {
        key: "scrollDocs",
        value: function scrollDocs(type) {
          var _this12 = this;

          if (!this.isStarred) {
            var page = 0;
            this.podcastsPage++;
            page = this.podcastsPage;
            this.http.get('/api/docs/podcasts?p=' + page + '&q=' + this.searchText).subscribe(function (data) {
              return _this12.docsLoaded('podcasts', data);
            }, function (error) {
              return _this12.error = error;
            });
          }
        }
      }, {
        key: "loadDocs",
        value: function loadDocs() {
          var _this13 = this;

          var page = 0;
          this.podcastsPage = 0;
          this.podcasts = [];
          this.http.get('/api/docs/podcasts?p=' + page + '&q=' + this.searchText).subscribe(function (data) {
            return _this13.docsLoaded('podcasts', data);
          }, function (error) {
            return _this13.error = error;
          });
        }
      }, {
        key: "loadStarred",
        value: function loadStarred() {
          var _this14 = this;

          this.podcasts = [];
          this.http.get('/api/docs/starred').subscribe(function (data) {
            return _this14.docsLoaded('starred', data);
          }, function (error) {
            return _this14.error = error;
          });
        }
      }, {
        key: "docsLoaded",
        value: function docsLoaded(type, data) {
          if (type == 'podcasts') {
            this.podcasts.push.apply(this.podcasts, data);
          } else if (type == 'starred') {
            var podcasts = [];
            data.forEach(function (doc) {
              if (doc.type == 'podcast') {
                podcasts.push(doc);
              }
            });
            this.podcasts.push.apply(this.podcasts, podcasts);
          }
        }
      }, {
        key: "loadUser",
        value: function loadUser() {
          var _this15 = this;

          if (this.user == null) {
            this.http.get('/api/user').subscribe(function (data) {
              return _this15.userLoaded(data);
            }, function (error) {
              return _this15.error = error;
            });
          }
        }
      }, {
        key: "userLoaded",
        value: function userLoaded(data) {
          if (data) {
            this.user = data.images[0].url;
          }
        }
      }, {
        key: "isDesktop",
        value: function isDesktop() {
          var hasTouchScreen = false;

          if (window.navigator.maxTouchPoints > 0) {
            hasTouchScreen = true;
          } else if (window.navigator.msMaxTouchPoints > 0) {
            hasTouchScreen = true;
          } else {
            var mQ = window.matchMedia && matchMedia("(pointer:coarse)");

            if (mQ && mQ.media === "(pointer:coarse)") {
              hasTouchScreen = !!mQ.matches;
            } else {
              // Only as a last resort, fall back to user agent sniffing
              var ua = window.navigator.userAgent;
              hasTouchScreen = /\b(BlackBerry|webOS|iPhone|IEMobile)\b/i.test(ua) || /\b(Android|Windows Phone|iPad|iPod)\b/i.test(ua);
            }
          }

          return !hasTouchScreen;
        }
      }]);

      return PodcastsComponent;
    }();

    PodcastsComponent.ctorParameters = function () {
      return [{
        type: _angular_common_http__WEBPACK_IMPORTED_MODULE_2__["HttpClient"]
      }, {
        type: _angular_router__WEBPACK_IMPORTED_MODULE_3__["ActivatedRoute"]
      }];
    };

    PodcastsComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
      selector: 'app-podcasts',
      template: tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! raw-loader!./podcasts.component.html */
      "./node_modules/raw-loader/dist/cjs.js!./src/app/podcasts/podcasts.component.html")).default,
      styles: [tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! ./podcasts.component.css */
      "./src/app/podcasts/podcasts.component.css")).default]
    })], PodcastsComponent);
    /***/
  },

  /***/
  "./src/app/section/section.component.css":
  /*!***********************************************!*\
    !*** ./src/app/section/section.component.css ***!
    \***********************************************/

  /*! exports provided: default */

  /***/
  function srcAppSectionSectionComponentCss(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = ".backward {\n  width: 40px;\n  height: 20px;\n  cursor: pointer;\n}\n\n.previous {\n  display: inline-block;\n  -webkit-mask: url(\"/images/previous.svg\");\n          mask: url(\"/images/previous.svg\");\n  width: 10px;\n  height: 15px;\n  margin-top: 2px;\n  margin-left: 20px;\n  background: #18A2B8;\n  cursor: pointer;\n}\n\n.forward {\n  width: 40px;\n  height: 20px;\n  background: none;\n  cursor: pointer;\n}\n\n.next {\n  display: inline-block;\n  -webkit-mask: url(\"/images/next.svg\");\n          mask: url(\"/images/next.svg\");\n  width: 10px;\n  height: 15px;\n  margin-top: 2px;\n  margin-left: 20px;\n  background: #18A2B8;\n  cursor: pointer;\n}\n\ndiv.cover {\n  width: 100px;\n  position: relative;\n}\n\na.cover {\n  width: 100px;\n  height: 100px;\n}\n\nimg.cover {\n  width: 100px;\n  height: 100px;\n  -o-object-fit:cover;\n     object-fit:cover;\n}\n\n.card {\n  border-style: none;\n}\n\n.card-body-sm {\n  position: relative;\n  width: 200px;\n  height: 80px;\n}\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInNyYy9hcHAvc2VjdGlvbi9zZWN0aW9uLmNvbXBvbmVudC5jc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUE7RUFDRSxXQUFXO0VBQ1gsWUFBWTtFQUNaLGVBQWU7QUFDakI7O0FBRUE7RUFDRSxxQkFBcUI7RUFDckIseUNBQWlDO1VBQWpDLGlDQUFpQztFQUNqQyxXQUFXO0VBQ1gsWUFBWTtFQUNaLGVBQWU7RUFDZixpQkFBaUI7RUFDakIsbUJBQW1CO0VBQ25CLGVBQWU7QUFDakI7O0FBRUE7RUFDRSxXQUFXO0VBQ1gsWUFBWTtFQUNaLGdCQUFnQjtFQUNoQixlQUFlO0FBQ2pCOztBQUVBO0VBQ0UscUJBQXFCO0VBQ3JCLHFDQUE2QjtVQUE3Qiw2QkFBNkI7RUFDN0IsV0FBVztFQUNYLFlBQVk7RUFDWixlQUFlO0VBQ2YsaUJBQWlCO0VBQ2pCLG1CQUFtQjtFQUNuQixlQUFlO0FBQ2pCOztBQUVBO0VBQ0UsWUFBWTtFQUNaLGtCQUFrQjtBQUNwQjs7QUFFQTtFQUNFLFlBQVk7RUFDWixhQUFhO0FBQ2Y7O0FBRUE7RUFDRSxZQUFZO0VBQ1osYUFBYTtFQUNiLG1CQUFnQjtLQUFoQixnQkFBZ0I7QUFDbEI7O0FBRUE7RUFDRSxrQkFBa0I7QUFDcEI7O0FBRUE7RUFDRSxrQkFBa0I7RUFDbEIsWUFBWTtFQUNaLFlBQVk7QUFDZCIsImZpbGUiOiJzcmMvYXBwL3NlY3Rpb24vc2VjdGlvbi5jb21wb25lbnQuY3NzIiwic291cmNlc0NvbnRlbnQiOlsiLmJhY2t3YXJkIHtcbiAgd2lkdGg6IDQwcHg7XG4gIGhlaWdodDogMjBweDtcbiAgY3Vyc29yOiBwb2ludGVyO1xufVxuXG4ucHJldmlvdXMge1xuICBkaXNwbGF5OiBpbmxpbmUtYmxvY2s7XG4gIG1hc2s6IHVybChcIi9pbWFnZXMvcHJldmlvdXMuc3ZnXCIpO1xuICB3aWR0aDogMTBweDtcbiAgaGVpZ2h0OiAxNXB4O1xuICBtYXJnaW4tdG9wOiAycHg7XG4gIG1hcmdpbi1sZWZ0OiAyMHB4O1xuICBiYWNrZ3JvdW5kOiAjMThBMkI4O1xuICBjdXJzb3I6IHBvaW50ZXI7XG59XG5cbi5mb3J3YXJkIHtcbiAgd2lkdGg6IDQwcHg7XG4gIGhlaWdodDogMjBweDtcbiAgYmFja2dyb3VuZDogbm9uZTtcbiAgY3Vyc29yOiBwb2ludGVyO1xufVxuXG4ubmV4dCB7XG4gIGRpc3BsYXk6IGlubGluZS1ibG9jaztcbiAgbWFzazogdXJsKFwiL2ltYWdlcy9uZXh0LnN2Z1wiKTtcbiAgd2lkdGg6IDEwcHg7XG4gIGhlaWdodDogMTVweDtcbiAgbWFyZ2luLXRvcDogMnB4O1xuICBtYXJnaW4tbGVmdDogMjBweDtcbiAgYmFja2dyb3VuZDogIzE4QTJCODtcbiAgY3Vyc29yOiBwb2ludGVyO1xufVxuXG5kaXYuY292ZXIge1xuICB3aWR0aDogMTAwcHg7XG4gIHBvc2l0aW9uOiByZWxhdGl2ZTtcbn1cblxuYS5jb3ZlciB7XG4gIHdpZHRoOiAxMDBweDtcbiAgaGVpZ2h0OiAxMDBweDtcbn1cblxuaW1nLmNvdmVyIHtcbiAgd2lkdGg6IDEwMHB4O1xuICBoZWlnaHQ6IDEwMHB4O1xuICBvYmplY3QtZml0OmNvdmVyO1xufVxuXG4uY2FyZCB7XG4gIGJvcmRlci1zdHlsZTogbm9uZTtcbn1cblxuLmNhcmQtYm9keS1zbSB7XG4gIHBvc2l0aW9uOiByZWxhdGl2ZTtcbiAgd2lkdGg6IDIwMHB4O1xuICBoZWlnaHQ6IDgwcHg7XG59XG4iXX0= */";
    /***/
  },

  /***/
  "./src/app/section/section.component.ts":
  /*!**********************************************!*\
    !*** ./src/app/section/section.component.ts ***!
    \**********************************************/

  /*! exports provided: SectionComponent */

  /***/
  function srcAppSectionSectionComponentTs(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "SectionComponent", function () {
      return SectionComponent;
    });
    /* harmony import */


    var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(
    /*! tslib */
    "./node_modules/tslib/tslib.es6.js");
    /* harmony import */


    var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(
    /*! @angular/core */
    "./node_modules/@angular/core/fesm2015/core.js");
    /* harmony import */


    var _podcasts_podcasts_component__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(
    /*! ../podcasts/podcasts.component */
    "./src/app/podcasts/podcasts.component.ts");

    var SectionComponent =
    /*#__PURE__*/
    function () {
      function SectionComponent(component) {
        _classCallCheck(this, SectionComponent);

        this.component = component;
        this.type = null;
        this.label = null;
        this.docs = [];
        this.scroll = 0;
      }

      _createClass(SectionComponent, [{
        key: "ngOnInit",
        value: function ngOnInit() {}
      }, {
        key: "scrollDocs",
        value: function scrollDocs() {
          this.component.scrollDocs(this.type);
        }
      }, {
        key: "next",
        value: function next() {
          this.scroll++;
          var slider = document.querySelector('#' + this.type);
          slider.scrollTo(206 * Math.floor(slider.clientWidth / 206) * this.scroll, 0);
        }
      }, {
        key: "previous",
        value: function previous() {
          if (this.scroll > 0) {
            this.scroll--;
            var slider = document.querySelector('#' + this.type);
            slider.scrollTo(206 * Math.floor(slider.clientWidth / 206) * this.scroll, 0);
          }
        }
      }]);

      return SectionComponent;
    }();

    SectionComponent.ctorParameters = function () {
      return [{
        type: _podcasts_podcasts_component__WEBPACK_IMPORTED_MODULE_2__["PodcastsComponent"]
      }];
    };

    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])()], SectionComponent.prototype, "type", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])()], SectionComponent.prototype, "label", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])()], SectionComponent.prototype, "docs", void 0);
    SectionComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
      selector: 'app-section',
      template: tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! raw-loader!./section.component.html */
      "./node_modules/raw-loader/dist/cjs.js!./src/app/section/section.component.html")).default,
      styles: [tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! ./section.component.css */
      "./src/app/section/section.component.css")).default]
    })], SectionComponent);
    /***/
  },

  /***/
  "./src/app/tracks/tracks.component.css":
  /*!*********************************************!*\
    !*** ./src/app/tracks/tracks.component.css ***!
    \*********************************************/

  /*! exports provided: default */

  /***/
  function srcAppTracksTracksComponentCss(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = ".close {\n    bottom: 5px;\n    right: 5px;\n    position: absolute;\n}\n\n.icon {\n    border-radius: 50%;\n    background: #FFFFFF;\n    width: 20px;\n    min-width: 20px;\n    height: 20px;\n    min-height: 20px;\n    cursor: pointer;\n}\n\n.songkick {\n    display: inline-block;\n    -webkit-mask: url(\"/images/songkick.svg\");\n            mask: url(\"/images/songkick.svg\");\n    width: 20px;\n    height: 20px;\n    background: #F80046;\n    cursor: pointer;\n}\n\ndiv.cover {\n    position: relative;\n    width: 62px;\n    min-width: 62px;\n    height: 62px;\n    min-height: 62px;\n    background: #18A2B8;\n    border-width: 1px;\n    border-color: #9c9c9c;\n    border-style: solid;\n    -o-object-fit:cover;\n       object-fit:cover;\n    cursor: pointer;\n}\n\nimg.cover {\n    width: 60px;\n    height: 60px;\n}\n\nh1.cover {\n    width: 60px;\n    height: 60px;\n    text-align: center;\n    margin-top: 15px;\n}\n\n.play {\n    position: absolute;\n    display: inline-block;\n    -webkit-mask: url(\"/images/play.svg\");\n            mask: url(\"/images/play.svg\");\n    width: 30px;\n    height: 30px;\n    left: 15px;\n    top: 15px;\n    background: #FFFFFF;\n    opacity: 0.6;\n    cursor: pointer;\n}\n\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInNyYy9hcHAvdHJhY2tzL3RyYWNrcy5jb21wb25lbnQuY3NzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUFBO0lBQ0ksV0FBVztJQUNYLFVBQVU7SUFDVixrQkFBa0I7QUFDdEI7O0FBRUE7SUFDSSxrQkFBa0I7SUFDbEIsbUJBQW1CO0lBQ25CLFdBQVc7SUFDWCxlQUFlO0lBQ2YsWUFBWTtJQUNaLGdCQUFnQjtJQUNoQixlQUFlO0FBQ25COztBQUVBO0lBQ0kscUJBQXFCO0lBQ3JCLHlDQUFpQztZQUFqQyxpQ0FBaUM7SUFDakMsV0FBVztJQUNYLFlBQVk7SUFDWixtQkFBbUI7SUFDbkIsZUFBZTtBQUNuQjs7QUFFQTtJQUNJLGtCQUFrQjtJQUNsQixXQUFXO0lBQ1gsZUFBZTtJQUNmLFlBQVk7SUFDWixnQkFBZ0I7SUFDaEIsbUJBQW1CO0lBQ25CLGlCQUFpQjtJQUNqQixxQkFBcUI7SUFDckIsbUJBQW1CO0lBQ25CLG1CQUFnQjtPQUFoQixnQkFBZ0I7SUFDaEIsZUFBZTtBQUNuQjs7QUFFQTtJQUNJLFdBQVc7SUFDWCxZQUFZO0FBQ2hCOztBQUVBO0lBQ0ksV0FBVztJQUNYLFlBQVk7SUFDWixrQkFBa0I7SUFDbEIsZ0JBQWdCO0FBQ3BCOztBQUVBO0lBQ0ksa0JBQWtCO0lBQ2xCLHFCQUFxQjtJQUNyQixxQ0FBNkI7WUFBN0IsNkJBQTZCO0lBQzdCLFdBQVc7SUFDWCxZQUFZO0lBQ1osVUFBVTtJQUNWLFNBQVM7SUFDVCxtQkFBbUI7SUFDbkIsWUFBWTtJQUNaLGVBQWU7QUFDbkIiLCJmaWxlIjoic3JjL2FwcC90cmFja3MvdHJhY2tzLmNvbXBvbmVudC5jc3MiLCJzb3VyY2VzQ29udGVudCI6WyIuY2xvc2Uge1xuICAgIGJvdHRvbTogNXB4O1xuICAgIHJpZ2h0OiA1cHg7XG4gICAgcG9zaXRpb246IGFic29sdXRlO1xufVxuXG4uaWNvbiB7XG4gICAgYm9yZGVyLXJhZGl1czogNTAlO1xuICAgIGJhY2tncm91bmQ6ICNGRkZGRkY7XG4gICAgd2lkdGg6IDIwcHg7XG4gICAgbWluLXdpZHRoOiAyMHB4O1xuICAgIGhlaWdodDogMjBweDtcbiAgICBtaW4taGVpZ2h0OiAyMHB4O1xuICAgIGN1cnNvcjogcG9pbnRlcjtcbn1cblxuLnNvbmdraWNrIHtcbiAgICBkaXNwbGF5OiBpbmxpbmUtYmxvY2s7XG4gICAgbWFzazogdXJsKFwiL2ltYWdlcy9zb25na2ljay5zdmdcIik7XG4gICAgd2lkdGg6IDIwcHg7XG4gICAgaGVpZ2h0OiAyMHB4O1xuICAgIGJhY2tncm91bmQ6ICNGODAwNDY7XG4gICAgY3Vyc29yOiBwb2ludGVyO1xufVxuXG5kaXYuY292ZXIge1xuICAgIHBvc2l0aW9uOiByZWxhdGl2ZTtcbiAgICB3aWR0aDogNjJweDtcbiAgICBtaW4td2lkdGg6IDYycHg7XG4gICAgaGVpZ2h0OiA2MnB4O1xuICAgIG1pbi1oZWlnaHQ6IDYycHg7XG4gICAgYmFja2dyb3VuZDogIzE4QTJCODtcbiAgICBib3JkZXItd2lkdGg6IDFweDtcbiAgICBib3JkZXItY29sb3I6ICM5YzljOWM7XG4gICAgYm9yZGVyLXN0eWxlOiBzb2xpZDtcbiAgICBvYmplY3QtZml0OmNvdmVyO1xuICAgIGN1cnNvcjogcG9pbnRlcjtcbn1cblxuaW1nLmNvdmVyIHtcbiAgICB3aWR0aDogNjBweDtcbiAgICBoZWlnaHQ6IDYwcHg7XG59XG5cbmgxLmNvdmVyIHtcbiAgICB3aWR0aDogNjBweDtcbiAgICBoZWlnaHQ6IDYwcHg7XG4gICAgdGV4dC1hbGlnbjogY2VudGVyO1xuICAgIG1hcmdpbi10b3A6IDE1cHg7XG59XG5cbi5wbGF5IHtcbiAgICBwb3NpdGlvbjogYWJzb2x1dGU7XG4gICAgZGlzcGxheTogaW5saW5lLWJsb2NrO1xuICAgIG1hc2s6IHVybChcIi9pbWFnZXMvcGxheS5zdmdcIik7XG4gICAgd2lkdGg6IDMwcHg7XG4gICAgaGVpZ2h0OiAzMHB4O1xuICAgIGxlZnQ6IDE1cHg7XG4gICAgdG9wOiAxNXB4O1xuICAgIGJhY2tncm91bmQ6ICNGRkZGRkY7XG4gICAgb3BhY2l0eTogMC42O1xuICAgIGN1cnNvcjogcG9pbnRlcjtcbn1cblxuIl19 */";
    /***/
  },

  /***/
  "./src/app/tracks/tracks.component.ts":
  /*!********************************************!*\
    !*** ./src/app/tracks/tracks.component.ts ***!
    \********************************************/

  /*! exports provided: TracksComponent */

  /***/
  function srcAppTracksTracksComponentTs(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "TracksComponent", function () {
      return TracksComponent;
    });
    /* harmony import */


    var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(
    /*! tslib */
    "./node_modules/tslib/tslib.es6.js");
    /* harmony import */


    var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(
    /*! @angular/core */
    "./node_modules/@angular/core/fesm2015/core.js");
    /* harmony import */


    var _doc_doc_component__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(
    /*! ../doc/doc.component */
    "./src/app/doc/doc.component.ts");

    var TracksComponent =
    /*#__PURE__*/
    function () {
      function TracksComponent(component) {
        _classCallCheck(this, TracksComponent);

        this.component = component;
        this.tracks = null;
      }

      _createClass(TracksComponent, [{
        key: "ngOnInit",
        value: function ngOnInit() {}
      }]);

      return TracksComponent;
    }();

    TracksComponent.ctorParameters = function () {
      return [{
        type: _doc_doc_component__WEBPACK_IMPORTED_MODULE_2__["DocComponent"]
      }];
    };

    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])()], TracksComponent.prototype, "tracks", void 0);
    TracksComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
      selector: 'app-tracks',
      template: tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! raw-loader!./tracks.component.html */
      "./node_modules/raw-loader/dist/cjs.js!./src/app/tracks/tracks.component.html")).default,
      styles: [tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! ./tracks.component.css */
      "./src/app/tracks/tracks.component.css")).default]
    })], TracksComponent);
    /***/
  },

  /***/
  "./src/app/video/video.component.css":
  /*!*******************************************!*\
    !*** ./src/app/video/video.component.css ***!
    \*******************************************/

  /*! exports provided: default */

  /***/
  function srcAppVideoVideoComponentCss(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = ".backward {\n  width: 40px;\n  height: 20px;\n  background: none;\n  cursor: pointer;\n}\n\n.previous {\n  display: inline-block;\n  -webkit-mask: url(\"/images/previous.svg\");\n          mask: url(\"/images/previous.svg\");\n  width: 10px;\n  height: 15px;\n  margin-top: 2px;\n  margin-left: 20px;\n  background: #18A2B8;\n  cursor: pointer;\n}\n\n.forward {\n  width: 40px;\n  height: 20px;\n  background: none;\n  cursor: pointer;\n}\n\n.next {\n  display: inline-block;\n  -webkit-mask: url(\"/images/next.svg\");\n          mask: url(\"/images/next.svg\");\n  width: 10px;\n  height: 15px;\n  margin-top: 2px;\n  margin-left: 20px;\n  background: #18A2B8;\n  cursor: pointer;\n}\n\ndiv.cover {\n  position: relative;\n  width: 120px;\n}\n\nimg.cover {\n  width: 120px;\n  -o-object-fit:cover;\n     object-fit:cover;\n  cursor: pointer;\n}\n\n.youtube {\n  position: absolute;\n  display: inline-block;\n  -webkit-mask: url(\"/images/youtube.svg\");\n          mask: url(\"/images/youtube.svg\");\n  width: 30px;\n  height: 30px;\n  margin: 0px;\n  padding: 0px;\n  left: 45px;\n  top: 30px;\n  background: #FFFFFF;\n  opacity: 0.6;\n  cursor: pointer;\n}\n\n.close {\n  bottom: 5px;\n  right: 5px;\n  position: absolute;\n}\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInNyYy9hcHAvdmlkZW8vdmlkZW8uY29tcG9uZW50LmNzcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQTtFQUNFLFdBQVc7RUFDWCxZQUFZO0VBQ1osZ0JBQWdCO0VBQ2hCLGVBQWU7QUFDakI7O0FBRUE7RUFDRSxxQkFBcUI7RUFDckIseUNBQWlDO1VBQWpDLGlDQUFpQztFQUNqQyxXQUFXO0VBQ1gsWUFBWTtFQUNaLGVBQWU7RUFDZixpQkFBaUI7RUFDakIsbUJBQW1CO0VBQ25CLGVBQWU7QUFDakI7O0FBRUE7RUFDRSxXQUFXO0VBQ1gsWUFBWTtFQUNaLGdCQUFnQjtFQUNoQixlQUFlO0FBQ2pCOztBQUVBO0VBQ0UscUJBQXFCO0VBQ3JCLHFDQUE2QjtVQUE3Qiw2QkFBNkI7RUFDN0IsV0FBVztFQUNYLFlBQVk7RUFDWixlQUFlO0VBQ2YsaUJBQWlCO0VBQ2pCLG1CQUFtQjtFQUNuQixlQUFlO0FBQ2pCOztBQUVBO0VBQ0Usa0JBQWtCO0VBQ2xCLFlBQVk7QUFDZDs7QUFFQTtFQUNFLFlBQVk7RUFDWixtQkFBZ0I7S0FBaEIsZ0JBQWdCO0VBQ2hCLGVBQWU7QUFDakI7O0FBRUE7RUFDRSxrQkFBa0I7RUFDbEIscUJBQXFCO0VBQ3JCLHdDQUFnQztVQUFoQyxnQ0FBZ0M7RUFDaEMsV0FBVztFQUNYLFlBQVk7RUFDWixXQUFXO0VBQ1gsWUFBWTtFQUNaLFVBQVU7RUFDVixTQUFTO0VBQ1QsbUJBQW1CO0VBQ25CLFlBQVk7RUFDWixlQUFlO0FBQ2pCOztBQUVBO0VBQ0UsV0FBVztFQUNYLFVBQVU7RUFDVixrQkFBa0I7QUFDcEIiLCJmaWxlIjoic3JjL2FwcC92aWRlby92aWRlby5jb21wb25lbnQuY3NzIiwic291cmNlc0NvbnRlbnQiOlsiLmJhY2t3YXJkIHtcbiAgd2lkdGg6IDQwcHg7XG4gIGhlaWdodDogMjBweDtcbiAgYmFja2dyb3VuZDogbm9uZTtcbiAgY3Vyc29yOiBwb2ludGVyO1xufVxuXG4ucHJldmlvdXMge1xuICBkaXNwbGF5OiBpbmxpbmUtYmxvY2s7XG4gIG1hc2s6IHVybChcIi9pbWFnZXMvcHJldmlvdXMuc3ZnXCIpO1xuICB3aWR0aDogMTBweDtcbiAgaGVpZ2h0OiAxNXB4O1xuICBtYXJnaW4tdG9wOiAycHg7XG4gIG1hcmdpbi1sZWZ0OiAyMHB4O1xuICBiYWNrZ3JvdW5kOiAjMThBMkI4O1xuICBjdXJzb3I6IHBvaW50ZXI7XG59XG5cbi5mb3J3YXJkIHtcbiAgd2lkdGg6IDQwcHg7XG4gIGhlaWdodDogMjBweDtcbiAgYmFja2dyb3VuZDogbm9uZTtcbiAgY3Vyc29yOiBwb2ludGVyO1xufVxuXG4ubmV4dCB7XG4gIGRpc3BsYXk6IGlubGluZS1ibG9jaztcbiAgbWFzazogdXJsKFwiL2ltYWdlcy9uZXh0LnN2Z1wiKTtcbiAgd2lkdGg6IDEwcHg7XG4gIGhlaWdodDogMTVweDtcbiAgbWFyZ2luLXRvcDogMnB4O1xuICBtYXJnaW4tbGVmdDogMjBweDtcbiAgYmFja2dyb3VuZDogIzE4QTJCODtcbiAgY3Vyc29yOiBwb2ludGVyO1xufVxuXG5kaXYuY292ZXIge1xuICBwb3NpdGlvbjogcmVsYXRpdmU7XG4gIHdpZHRoOiAxMjBweDtcbn1cblxuaW1nLmNvdmVyIHtcbiAgd2lkdGg6IDEyMHB4O1xuICBvYmplY3QtZml0OmNvdmVyO1xuICBjdXJzb3I6IHBvaW50ZXI7XG59XG5cbi55b3V0dWJlIHtcbiAgcG9zaXRpb246IGFic29sdXRlO1xuICBkaXNwbGF5OiBpbmxpbmUtYmxvY2s7XG4gIG1hc2s6IHVybChcIi9pbWFnZXMveW91dHViZS5zdmdcIik7XG4gIHdpZHRoOiAzMHB4O1xuICBoZWlnaHQ6IDMwcHg7XG4gIG1hcmdpbjogMHB4O1xuICBwYWRkaW5nOiAwcHg7XG4gIGxlZnQ6IDQ1cHg7XG4gIHRvcDogMzBweDtcbiAgYmFja2dyb3VuZDogI0ZGRkZGRjtcbiAgb3BhY2l0eTogMC42O1xuICBjdXJzb3I6IHBvaW50ZXI7XG59XG5cbi5jbG9zZSB7XG4gIGJvdHRvbTogNXB4O1xuICByaWdodDogNXB4O1xuICBwb3NpdGlvbjogYWJzb2x1dGU7XG59Il19 */";
    /***/
  },

  /***/
  "./src/app/video/video.component.ts":
  /*!******************************************!*\
    !*** ./src/app/video/video.component.ts ***!
    \******************************************/

  /*! exports provided: VideoComponent */

  /***/
  function srcAppVideoVideoComponentTs(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "VideoComponent", function () {
      return VideoComponent;
    });
    /* harmony import */


    var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(
    /*! tslib */
    "./node_modules/tslib/tslib.es6.js");
    /* harmony import */


    var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(
    /*! @angular/core */
    "./node_modules/@angular/core/fesm2015/core.js");
    /* harmony import */


    var _angular_platform_browser__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(
    /*! @angular/platform-browser */
    "./node_modules/@angular/platform-browser/fesm2015/platform-browser.js");
    /* harmony import */


    var _doc_doc_component__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(
    /*! ../doc/doc.component */
    "./src/app/doc/doc.component.ts");

    var VideoComponent =
    /*#__PURE__*/
    function () {
      function VideoComponent(component, sanitizer) {
        _classCallCheck(this, VideoComponent);

        this.component = component;
        this.sanitizer = sanitizer;
        this.tracks = [];
        this.video = null;
        this.scroll = 0;
      }

      _createClass(VideoComponent, [{
        key: "ngOnInit",
        value: function ngOnInit() {}
      }, {
        key: "youtubeURL",
        value: function youtubeURL() {
          return this.sanitizer.bypassSecurityTrustResourceUrl('https://www.youtube.com/embed/' + this.video.youtube + '?autoplay=1');
        }
      }, {
        key: "loadVideo",
        value: function loadVideo(track) {
          this.video = track.youtube ? track : null;
        }
      }, {
        key: "close",
        value: function close(event) {
          this.video = null;
        }
      }, {
        key: "next",
        value: function next() {
          this.scroll++;
          var slider = document.querySelector('#videos');
          slider.scrollTo(126 * Math.floor(slider.clientWidth / 126) * this.scroll, 0);
        }
      }, {
        key: "previous",
        value: function previous() {
          if (this.scroll > 0) {
            this.scroll--;
            var slider = document.querySelector('#videos');
            slider.scrollTo(126 * Math.floor(slider.clientWidth / 126) * this.scroll, 0);
          }
        }
      }]);

      return VideoComponent;
    }();

    VideoComponent.ctorParameters = function () {
      return [{
        type: _doc_doc_component__WEBPACK_IMPORTED_MODULE_3__["DocComponent"]
      }, {
        type: _angular_platform_browser__WEBPACK_IMPORTED_MODULE_2__["DomSanitizer"]
      }];
    };

    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])()], VideoComponent.prototype, "type", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])()], VideoComponent.prototype, "label", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])()], VideoComponent.prototype, "tracks", void 0);
    VideoComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
      selector: 'app-video',
      template: tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! raw-loader!./video.component.html */
      "./node_modules/raw-loader/dist/cjs.js!./src/app/video/video.component.html")).default,
      styles: [tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! ./video.component.css */
      "./src/app/video/video.component.css")).default]
    })], VideoComponent);
    /***/
  },

  /***/
  "./src/environments/environment.ts":
  /*!*****************************************!*\
    !*** ./src/environments/environment.ts ***!
    \*****************************************/

  /*! exports provided: environment */

  /***/
  function srcEnvironmentsEnvironmentTs(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "environment", function () {
      return environment;
    });
    /* harmony import */


    var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(
    /*! tslib */
    "./node_modules/tslib/tslib.es6.js"); // This file can be replaced during build by using the `fileReplacements` array.
    // `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
    // The list of file replacements can be found in `angular.json`.


    var environment = {
      production: false
    };
    /*
     * For easier debugging in development mode, you can import the following file
     * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
     *
     * This import should be commented out in production mode because it will have a negative impact
     * on performance if an error is thrown.
     */
    // import 'zone.js/dist/zone-error';  // Included with Angular CLI.

    /***/
  },

  /***/
  "./src/main.ts":
  /*!*********************!*\
    !*** ./src/main.ts ***!
    \*********************/

  /*! no exports provided */

  /***/
  function srcMainTs(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony import */


    var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(
    /*! tslib */
    "./node_modules/tslib/tslib.es6.js");
    /* harmony import */


    var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(
    /*! @angular/core */
    "./node_modules/@angular/core/fesm2015/core.js");
    /* harmony import */


    var _angular_platform_browser_dynamic__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(
    /*! @angular/platform-browser-dynamic */
    "./node_modules/@angular/platform-browser-dynamic/fesm2015/platform-browser-dynamic.js");
    /* harmony import */


    var _app_app_module__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(
    /*! ./app/app.module */
    "./src/app/app.module.ts");
    /* harmony import */


    var _environments_environment__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(
    /*! ./environments/environment */
    "./src/environments/environment.ts");

    if (_environments_environment__WEBPACK_IMPORTED_MODULE_4__["environment"].production) {
      Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["enableProdMode"])();
    }

    Object(_angular_platform_browser_dynamic__WEBPACK_IMPORTED_MODULE_2__["platformBrowserDynamic"])().bootstrapModule(_app_app_module__WEBPACK_IMPORTED_MODULE_3__["AppModule"]).catch(function (err) {
      return console.error(err);
    });
    /***/
  },

  /***/
  0:
  /*!***************************!*\
    !*** multi ./src/main.ts ***!
    \***************************/

  /*! no static exports found */

  /***/
  function _(module, exports, __webpack_require__) {
    module.exports = __webpack_require__(
    /*! /Users/riccia/workspace/phonoteke/web/src/main.ts */
    "./src/main.ts");
    /***/
  }
}, [[0, "runtime", "vendor"]]]);
//# sourceMappingURL=main-es5.js.map