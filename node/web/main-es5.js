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


    __webpack_exports__["default"] = "<div class=\"container-fluid\">\n  <router-outlet></router-outlet>\n  <footer class=\"page-footer font-small pt-2 pb-2\">\n    <div class=\"footer-copyright\">\n      <div class=\"d-flex row justify-content-center\"><small>Ideato, progettato e realizzato da Andrea Ricci</small></div>\n      <div class=\"d-flex row align-items-center justify-content-center\">\n        <a class=\"pr-2\" href=\"mailto:andrea.ricci@gmx.com\"><span class=\"mail\"></span></a>\n        <a class=\"pr-2\" href=\"https://github.com/joshua81/\" target=\"_blank\"><span class=\"github\"></span></a>\n        <a class=\"pr-2\" href=\"https://www.linkedin.com/in/ricciandrea/\" target=\"_blank\"><span class=\"linkedin\"></span></a>\n        <a href=\"https://creativecommons.org/\" target=\"_blank\"><span class=\"cc\"></span></a>\n      </div>\n    </div>\n  </footer>\n</div>\n";
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


    __webpack_exports__["default"] = "<doc-menu *ngIf=\"doc\"></doc-menu>\n<div *ngIf=\"doc\" class=\"container pt-5 w-max\">\n    <div class=\"row d-flex justify-content-center\" [ngStyle]=\"{'top':'6px'}\">\n      <img class=\"bg-image\" [ngStyle]=\"{'background-image':'url('+doc.cover+')'}\"/>\n      <div class=\"cover-m\">\n        <img class=\"cover-m\" src=\"{{doc.cover}}\"/>\n        <div *ngIf=\"doc.type == 'album' && doc.vote && doc.vote == 10\" class=\"vote\">\n          <span class=\"star\"></span>\n        </div>\n        <h3 *ngIf=\"doc.type == 'album' && doc.vote && doc.vote != 10\" class=\"vote\">{{doc.vote}}</h3>\n      </div>\n    </div>\n\n    <div class=\"row d-flex justify-content-center bg-white\">\n      <div class=\"text-center\">\n        <h1 *ngIf=\"doc.type != 'artist'\" [innerHTML]=\"doc.title\"></h1>\n        <h2 *ngIf=\"doc.type != 'artist'\" [innerHTML]=\"doc.artist\"></h2>\n        <h1 *ngIf=\"doc.type == 'artist'\" [innerHTML]=\"doc.artist\"></h1>\n        <h3 *ngIf=\"doc.type == 'album' && doc.label\">{{doc.label + ' | ' + doc.year + ' | ' + doc.genres}}</h3>\n        <h3 *ngIf=\"doc.authors\" class=\"text-muted\">di {{doc.authors}}</h3>\n      </div>\n    </div>\n\n    <div class=\"row d-flex align-items-center justify-content-center bg-white\">\n      <a class=\"pl-2 pr-2\" href=\"{{doc.url}}\" target=\"_blank\">\n        <img class=\"{{doc.source}}\">\n      </a>\n      <!--a *ngIf=\"doc.type != 'album' && doc.artistid\" class=\"pr-2\" href=\"{{'https://musicbrainz.org/artist/' + doc.artistid}}\" target=\"_blank\">\n        <img class=\"musicbrainz\">\n      </a>\n      <a *ngIf=\"doc.type == 'album' && doc.albumid\" class=\"pr-2\" href=\"{{'https://musicbrainz.org/release-group/' + doc.albumid}}\" target=\"_blank\">\n        <img class=\"musicbrainz\">\n      </a-->\n      <a *ngIf=\"doc.artistid != null\" class=\"pl-2 pr-2\" (click)=\"service.loadEvents(doc.artistid, $event)\">\n        <img class=\"songkick\">\n      </a>\n      <!--div *ngIf=\"doc.artistid != null\" class=\"songkick pl-2 pr-2\" (click)=\"service.loadEvents(doc.artistid)\">\n        <span class=\"songkick\"></span>\n      </div-->\n      <a *ngIf=\"doc.type != 'album' && doc.spartistid\" class=\"pl-2 pr-2\" href=\"{{'https://open.spotify.com/artist/' + doc.spartistid}}\" target=\"_blank\">\n        <img class=\"spotify\">\n      </a>\n      <a *ngIf=\"doc.type == 'album' && doc.spalbumid\" class=\"pl-2 pr-2\" href=\"{{'https://open.spotify.com/album/' + doc.spalbumid}}\" target=\"_blank\">\n        <img class=\"spotify\">\n      </a>\n      <div *ngIf=\"service.audio\" class=\"pl-2 pr-2\" (click)=\"backward($event)\">\n        <span class=\"backward\"></span>\n      </div>\n      <div *ngIf=\"service.audio\" class=\"pl-2 pr-2\" (click)=\"playPause($event)\">\n        <span *ngIf=\"service.audio.paused\" class=\"play\"></span>\n        <span *ngIf=\"!service.audio.paused\" class=\"pause\"></span>\n      </div>\n      <div *ngIf=\"service.audio\" class=\"pl-2 pr-2\" (click)=\"forward($event)\">\n        <span class=\"forward\"></span>\n      </div>\n    </div>\n\n    <div class=\"row p-2 bg-white\">\n      <div *ngIf=\"doc.review\" class=\"text-justify\" [innerHTML]=\"doc.review\"></div>\n    </div>\n\n    <app-youtube [tracks]=\"doc.tracks\"></app-youtube>\n\n    <div *ngIf=\"links && links.length != 0\" class=\"row d-flex justify-content-center pt-3\">\n      <div *ngFor=\"let link of links\" class=\"pb-2 pl-1 pr-1\">\n        <div class=\"cover-s card mx-auto\">\n          <a class=\"cover-s\" [routerLink]=\"['/docs/' + link.id]\">\n            <img class=\"cover-s card-img-top\" src=\"{{link.cover}}\"/>\n          </a>\n          <div class=\"card-body p-1\">\n            <div class=\"card-text font-weight-bold\" [innerHTML]=\"link.title\"></div>\n            <div class=\"card-text\" [innerHTML]=\"link.artist\"></div>\n            <div class=\"card-footer text-muted p-0\">{{link.type.toUpperCase()}}</div>\n          </div>\n        </div>\n      </div>\n  </div>\n</div>\n\n<app-events *ngIf=\"service.showEvents == true\" [events]=\"service.events\"></app-events>\n";
    /***/
  },

  /***/
  "./node_modules/raw-loader/dist/cjs.js!./src/app/doc/events/events.component.html":
  /*!****************************************************************************************!*\
    !*** ./node_modules/raw-loader/dist/cjs.js!./src/app/doc/events/events.component.html ***!
    \****************************************************************************************/

  /*! exports provided: default */

  /***/
  function node_modulesRawLoaderDistCjsJsSrcAppDocEventsEventsComponentHtml(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = "<div class=\"d-flex justify-content-center modal-bg\" (click)=\"close()\">\n    <div *ngIf=\"events && events.length != 0\" class=\"align-self-center overflow-auto modal-box\">\n        <div class=\"list-group\">\n            <div *ngFor=\"let event of events\" class=\"p-2 list-group-item\">\n                <div class=\"d-flex align-items-center\">\n                    <div class=\"w-100\">\n                        <div class=\"text-white font-weight-bold\">{{event.displayName}}</div>\n                        <div class=\"text-white\">{{event.start.date}} {{event.location.city}}</div>\n                        <div class=\"text-white\">{{event.venue.displayName}}</div>\n                    </div>\n                    <a href=\"{{event.uri}}\" target=\"_blank\">\n                        <img class=\"songkick\">\n                    </a>\n                </div>\n            </div>\n        </div>\n    </div>\n    <div *ngIf=\"events && events.length == 0\" class=\"align-self-center overflow-auto modal-box\">\n        <div class=\"d-flex justify-content-center align-middle\">\n            <p class=\"text-white font-weight-bold pt-2\">Nessun evento in programma</p>\n        </div>\n    </div>\n</div>";
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


    __webpack_exports__["default"] = "<nav class=\"navbar navbar-expand-lg navbar-light fixed-top justify-content-center bg-info\">\n  <div class=\"d-flex flex-row align-items-center w-max\">\n    <!--div class=\"pr-2\">\n      <a [routerLink]=\"['/']\">\n        <span class=\"back\"></span>\n      </a>\n    </div-->\n    <div class=\"pr-2\">\n      <a [routerLink]=\"['/']\">\n        <img class=\"cover\" src=\"{{component.doc.cover}}\"/>\n      </a>\n    </div>\n    <div class=\"text-truncate w-100\">\n      <div class=\"text-white font-weight-bold\" [innerHTML]=\"component.doc.title\"></div>\n      <div class=\"text-white\" [innerHTML]=\"component.doc.artist\"></div>\n    </div>\n  </div>\n</nav>\n";
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


    __webpack_exports__["default"] = "<docs-menu></docs-menu>\n<div class=\"container pt-5 w-max\">\n  <div infinite-scroll *ngIf=\"docs.length > 0\" class=\"row d-flex justify-content-center pt-2\" (scrolled)=\"scrollDocs()\">\n      <div *ngFor=\"let doc of docs\" class=\"pb-2 pl-1 pr-1\">\n        <div class=\"cover-m card mx-auto\">\n          <a class=\"cover-m\" [routerLink]=\"['/docs/' + doc.id]\">\n            <img class=\"cover-m card-img-top\" src=\"{{doc.cover}}\"/>\n          </a>\n          <div class=\"card-body p-1\">\n            <div *ngIf=\"doc.type != 'artist'\" class=\"card-text font-weight-bold\" [innerHTML]=\"doc.title\"></div>\n            <div *ngIf=\"doc.type != 'artist'\" class=\"card-text\" [innerHTML]=\"doc.artist\"></div>\n            <div *ngIf=\"doc.type == 'artist'\" class=\"card-text font-weight-bold\" [innerHTML]=\"doc.artist\"></div>\n            <div *ngIf=\"doc.description\" class=\"card-text pt-2\" [innerHTML]=\"doc.description\"></div>\n            <div class=\"card-footer text-muted p-0\">{{doc.type.toUpperCase()}}</div>\n          </div>\n        </div>\n      </div>\n  </div>\n\n  <div infinite-scroll *ngIf=\"tracks.length > 0\" class=\"row d-flex justify-content-center pt-2\" (scrolled)=\"scrollTracks()\">\n    <app-youtube [tracks]=\"tracks\"></app-youtube>\n  </div>\n</div>\n";
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


    __webpack_exports__["default"] = "<nav class=\"navbar navbar-expand-lg navbar-light fixed-top justify-content-center bg-info\">\n  <div class=\"d-flex flex-row align-items-center w-max\">\n    <div class=\"menu-item pt-1 pb-1 pl-3 pr-3\" [ngClass]=\"component.docType == 'album' ? 'selected' : ''\" (click)=\"loadAlbums()\">\n      <h3 class=\"text-white font-weight-bold mt-1\">album</h3>\n    </div>\n    <div class=\"menu-item pt-1 pb-1 pl-3 pr-3\" [ngClass]=\"component.docType == 'artist' ? 'selected' : ''\" (click)=\"loadArtists()\">\n      <h3 class=\"text-white font-weight-bold mt-1\">artisti</h3>\n    </div>\n    <div class=\"menu-item pt-1 pb-1 pl-3 pr-3\" [ngClass]=\"component.docType == 'interview' ? 'selected' : ''\" (click)=\"loadInterviews()\">\n      <h3 class=\"text-white font-weight-bold mt-1\">interviste</h3>\n    </div>\n    <div class=\"menu-item pt-1 pb-1 pl-3 pr-3\" [ngClass]=\"component.docType == 'concert' ? 'selected' : ''\" (click)=\"loadConcerts()\">\n      <h3 class=\"text-white font-weight-bold mt-1\">concerti</h3>\n    </div>\n    <div class=\"menu-item pt-1 pb-1 pl-3 pr-3\" [ngClass]=\"component.docType == 'podcast' ? 'selected' : ''\" (click)=\"loadPodcasts()\">\n      <h3 class=\"text-white font-weight-bold mt-1\">podcast</h3>\n    </div>\n    <!--div class=\"menu-item pt-1 pb-1 pl-3 pr-3\" [ngClass]=\"component.docType == 'video' ? 'selected' : ''\" (click)=\"loadVideos()\">\n      <h3 class=\"text-white font-weight-bold mt-1\">video</h3>\n    </div-->\n    <form class=\"search ml-auto pl-3 pr-3\">\n      <input class=\"form-control\" name=\"search\" type=\"text\" placeholder=\"Cerca\" aria-label=\"Cerca\" [(ngModel)]=\"component.searchText\"  (keydown.enter)=\"onSearch()\">\n    </form>\n  </div>\n</nav>\n";
    /***/
  },

  /***/
  "./node_modules/raw-loader/dist/cjs.js!./src/app/youtube/youtube.component.html":
  /*!**************************************************************************************!*\
    !*** ./node_modules/raw-loader/dist/cjs.js!./src/app/youtube/youtube.component.html ***!
    \**************************************************************************************/

  /*! exports provided: default */

  /***/
  function node_modulesRawLoaderDistCjsJsSrcAppYoutubeYoutubeComponentHtml(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = "<div *ngIf=\"tracks && tracks.length > 0\" class=\"row d-flex justify-content-center bg-white\">\n    <div class=\"list-group w-med\">\n        <a *ngFor=\"let track of tracks\" class=\"pt-2\" (click)=\"loadVideo(track)\">\n            <div class=\"row d-flex align-items-center m-0\">\n                <div><span class=\"youtube\" [ngClass]=\"track == video ? 'youtube-selected' : ''\"></span></div>\n                <div class=\"flex-grow-1 pl-1 pr-1\" [ngStyle]=\"{'max-width':'300px'}\" [innerHTML]=\"track.title\"></div>\n                <div *ngIf=\"track.artistid\" (click)=\"service.loadEvents(track.artistid, $event)\">\n                    <span class=\"songkickbtn\" [ngClass]=\"track == video ? 'songkick-selected' : ''\"></span>\n                </div>\n            </div>\n        </a>\n    </div>\n</div>\n\n<div *ngIf=\"video != null\" class=\"player fixed-bottom bg-white\">\n    <youtube-player width=\"350px\" height=\"200px\" suggestedQuality=\"default\" videoId=\"{{video.youtube}}\"\n    (stateChange)=\"onStateChange($event)\" (ready)=\"onReady($event)\">\n    </youtube-player>\n    <div class=\"p-1\">\n        <div [innerHTML]=\"video.title\"></div>\n        <!--div *ngIf=\"video.album\" class=\"row d-flex align-items-center m-0 pt-2\">\n            <a class=\"cover\" [routerLink]=\"['/docs/' + video.id]\">\n                <img class=\"cover\" src=\"{{video.cover}}\"/>\n            </a>\n            <div class=\"pl-2 pr-2 flex-grow-1\">\n                <div class=\"text-truncate font-weight-bold\" [innerHTML]=\"video.album\"></div>\n                <div class=\"text-truncate\" [innerHTML]=\"video.artist\"></div>\n            </div>\n        </div-->\n        <div class=\"row d-flex align-items-center justify-content-center m-0\">\n            <div class=\"pr-2\" (click)=\"backward($event)\">\n                <span class=\"backward\"></span>\n            </div>\n            <div class=\"pl-2 pr-2\" (click)=\"playPause($event)\">\n                <span *ngIf=\"status != 1\" class=\"play\"></span>\n                <span *ngIf=\"status == 1\" class=\"pause\"></span>\n              </div>\n            <div class=\"pl-2\" (click)=\"forward($event)\">\n                <span class=\"forward\"></span>\n            </div>\n        </div>\n    </div>\n    <div class=\"close\" (click)=\"close($event)\">\n        <span class=\"close\"></span>\n    </div>\n</div>";
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


    var _app_component__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(
    /*! ./app.component */
    "./src/app/app.component.ts");
    /* harmony import */


    var _docs_menu_menu_component__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(
    /*! ./docs/menu/menu.component */
    "./src/app/docs/menu/menu.component.ts");
    /* harmony import */


    var _docs_docs_component__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(
    /*! ./docs/docs.component */
    "./src/app/docs/docs.component.ts");
    /* harmony import */


    var _doc_menu_menu_component__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(
    /*! ./doc/menu/menu.component */
    "./src/app/doc/menu/menu.component.ts");
    /* harmony import */


    var _doc_doc_component__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(
    /*! ./doc/doc.component */
    "./src/app/doc/doc.component.ts");
    /* harmony import */


    var _youtube_youtube_component__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(
    /*! ./youtube/youtube.component */
    "./src/app/youtube/youtube.component.ts");
    /* harmony import */


    var _doc_events_events_component__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(
    /*! ./doc/events/events.component */
    "./src/app/doc/events/events.component.ts");
    /* harmony import */


    var _angular_youtube_player__WEBPACK_IMPORTED_MODULE_15__ = __webpack_require__(
    /*! @angular/youtube-player */
    "./node_modules/@angular/youtube-player/esm2015/youtube-player.js");

    var AppModule = function AppModule() {
      _classCallCheck(this, AppModule);
    };

    AppModule = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_2__["NgModule"])({
      declarations: [_app_component__WEBPACK_IMPORTED_MODULE_8__["AppComponent"], _docs_menu_menu_component__WEBPACK_IMPORTED_MODULE_9__["DocsMenuComponent"], _docs_docs_component__WEBPACK_IMPORTED_MODULE_10__["DocsComponent"], _doc_menu_menu_component__WEBPACK_IMPORTED_MODULE_11__["DocMenuComponent"], _doc_doc_component__WEBPACK_IMPORTED_MODULE_12__["DocComponent"], _youtube_youtube_component__WEBPACK_IMPORTED_MODULE_13__["YoutubeComponent"], _doc_events_events_component__WEBPACK_IMPORTED_MODULE_14__["EventsComponent"]],
      imports: [_angular_platform_browser__WEBPACK_IMPORTED_MODULE_1__["BrowserModule"], _angular_router__WEBPACK_IMPORTED_MODULE_3__["RouterModule"].forRoot([{
        path: '',
        component: _docs_docs_component__WEBPACK_IMPORTED_MODULE_10__["DocsComponent"]
      }, {
        path: 'docs',
        component: _docs_docs_component__WEBPACK_IMPORTED_MODULE_10__["DocsComponent"]
      }, {
        path: 'docs/:docId',
        component: _doc_doc_component__WEBPACK_IMPORTED_MODULE_12__["DocComponent"]
      }]), _angular_forms__WEBPACK_IMPORTED_MODULE_4__["FormsModule"], _angular_common_http__WEBPACK_IMPORTED_MODULE_5__["HttpClientModule"], ngx_infinite_scroll__WEBPACK_IMPORTED_MODULE_6__["InfiniteScrollModule"], _angular_youtube_player__WEBPACK_IMPORTED_MODULE_15__["YouTubePlayerModule"], _angular_common__WEBPACK_IMPORTED_MODULE_7__["CommonModule"]],
      providers: [],
      bootstrap: [_app_component__WEBPACK_IMPORTED_MODULE_8__["AppComponent"]]
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
    /* harmony import */


    var _angular_common_http__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(
    /*! @angular/common/http */
    "./node_modules/@angular/common/fesm2015/http.js");

    var AppService =
    /*#__PURE__*/
    function () {
      function AppService(http) {
        _classCallCheck(this, AppService);

        this.http = http;
        this.error = null;
        this.audio = null;
        this.showEvents = false;
        this.events = [];
      }

      _createClass(AppService, [{
        key: "resetEvents",
        value: function resetEvents() {
          this.showEvents = false;
          this.events.splice(0, this.events.length);
        }
      }, {
        key: "loadEvents",
        value: function loadEvents(id, event) {
          var _this = this;

          event.stopPropagation();
          this.showEvents = true;
          this.http.get('/api/artists/' + id + '/events').subscribe(function (data) {
            return _this.setEvents(data);
          }, function (error) {
            return _this.error = error;
          });
        }
      }, {
        key: "setEvents",
        value: function setEvents(events) {
          this.events.splice(0, this.events.length);

          if (typeof events != 'undefined' && events != null) {
            this.events.push.apply(this.events, events);
          }
        }
      }]);

      return AppService;
    }();

    AppService.ctorParameters = function () {
      return [{
        type: _angular_common_http__WEBPACK_IMPORTED_MODULE_2__["HttpClient"]
      }];
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


    __webpack_exports__["default"] = ".row {\n  position: relative;\n}\n\n.bg-image {\n  position: absolute;\n  top: 0px;\n  left: 0px;\n  opacity: 0.6;\n  width: 100%;\n  height: 100%;\n\n  filter: blur(4px);\n  -webkit-filter: blur(4px);\n  background-position: center;\n  background-repeat: no-repeat;\n  background-size: cover;\n  -webkit-clip-path: polygon(0 0, 100% 0, 100% 100%, 0 100%);\n          clip-path: polygon(0 0, 100% 0, 100% 100%, 0 100%);\n}\n\n.vote {\n  border-radius: 50%;\n  width: 30px;\n  height: 30px;\n  flex: 0 0 30px;\n  background: #8AC007;\n\n  color: #FFFFFF;\n  text-align: center;\n  padding-top: 8px;\n\n  position: absolute;\n  right: 10px;\n  top: 10px;\n  z-index: 20;\n}\n\ndiv.cover-s {\n  width: 120px;\n  position: relative;\n}\n\na.cover-s {\n  width: 120px;\n  height: 120px;\n}\n\nimg.cover-s {\n  width: 120px;\n  height: 120px;\n  -o-object-fit:cover;\n     object-fit:cover;\n}\n\n.card {\n  border-style: none;\n}\n\n.card-body {\n  position: relative;\n  width: 120px;\n  height: 100px;\n}\n\n.card-text {\n  overflow: hidden;\n  display: -webkit-box;\n  -webkit-line-clamp: 3;\n  -webkit-box-orient: vertical;\n}\n\n.card-footer {\n  border-style: none;\n  background: none;\n  position: absolute;\n  bottom: 0px;\n}\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInNyYy9hcHAvZG9jL2RvYy5jb21wb25lbnQuY3NzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUFBO0VBQ0Usa0JBQWtCO0FBQ3BCOztBQUVBO0VBQ0Usa0JBQWtCO0VBQ2xCLFFBQVE7RUFDUixTQUFTO0VBQ1QsWUFBWTtFQUNaLFdBQVc7RUFDWCxZQUFZOztFQUVaLGlCQUFpQjtFQUNqQix5QkFBeUI7RUFDekIsMkJBQTJCO0VBQzNCLDRCQUE0QjtFQUM1QixzQkFBc0I7RUFDdEIsMERBQWtEO1VBQWxELGtEQUFrRDtBQUNwRDs7QUFFQTtFQUNFLGtCQUFrQjtFQUNsQixXQUFXO0VBQ1gsWUFBWTtFQUNaLGNBQWM7RUFDZCxtQkFBbUI7O0VBRW5CLGNBQWM7RUFDZCxrQkFBa0I7RUFDbEIsZ0JBQWdCOztFQUVoQixrQkFBa0I7RUFDbEIsV0FBVztFQUNYLFNBQVM7RUFDVCxXQUFXO0FBQ2I7O0FBRUE7RUFDRSxZQUFZO0VBQ1osa0JBQWtCO0FBQ3BCOztBQUVBO0VBQ0UsWUFBWTtFQUNaLGFBQWE7QUFDZjs7QUFFQTtFQUNFLFlBQVk7RUFDWixhQUFhO0VBQ2IsbUJBQWdCO0tBQWhCLGdCQUFnQjtBQUNsQjs7QUFFQTtFQUNFLGtCQUFrQjtBQUNwQjs7QUFFQTtFQUNFLGtCQUFrQjtFQUNsQixZQUFZO0VBQ1osYUFBYTtBQUNmOztBQUVBO0VBQ0UsZ0JBQWdCO0VBQ2hCLG9CQUFvQjtFQUNwQixxQkFBcUI7RUFDckIsNEJBQTRCO0FBQzlCOztBQUVBO0VBQ0Usa0JBQWtCO0VBQ2xCLGdCQUFnQjtFQUNoQixrQkFBa0I7RUFDbEIsV0FBVztBQUNiIiwiZmlsZSI6InNyYy9hcHAvZG9jL2RvYy5jb21wb25lbnQuY3NzIiwic291cmNlc0NvbnRlbnQiOlsiLnJvdyB7XG4gIHBvc2l0aW9uOiByZWxhdGl2ZTtcbn1cblxuLmJnLWltYWdlIHtcbiAgcG9zaXRpb246IGFic29sdXRlO1xuICB0b3A6IDBweDtcbiAgbGVmdDogMHB4O1xuICBvcGFjaXR5OiAwLjY7XG4gIHdpZHRoOiAxMDAlO1xuICBoZWlnaHQ6IDEwMCU7XG5cbiAgZmlsdGVyOiBibHVyKDRweCk7XG4gIC13ZWJraXQtZmlsdGVyOiBibHVyKDRweCk7XG4gIGJhY2tncm91bmQtcG9zaXRpb246IGNlbnRlcjtcbiAgYmFja2dyb3VuZC1yZXBlYXQ6IG5vLXJlcGVhdDtcbiAgYmFja2dyb3VuZC1zaXplOiBjb3ZlcjtcbiAgY2xpcC1wYXRoOiBwb2x5Z29uKDAgMCwgMTAwJSAwLCAxMDAlIDEwMCUsIDAgMTAwJSk7XG59XG5cbi52b3RlIHtcbiAgYm9yZGVyLXJhZGl1czogNTAlO1xuICB3aWR0aDogMzBweDtcbiAgaGVpZ2h0OiAzMHB4O1xuICBmbGV4OiAwIDAgMzBweDtcbiAgYmFja2dyb3VuZDogIzhBQzAwNztcblxuICBjb2xvcjogI0ZGRkZGRjtcbiAgdGV4dC1hbGlnbjogY2VudGVyO1xuICBwYWRkaW5nLXRvcDogOHB4O1xuXG4gIHBvc2l0aW9uOiBhYnNvbHV0ZTtcbiAgcmlnaHQ6IDEwcHg7XG4gIHRvcDogMTBweDtcbiAgei1pbmRleDogMjA7XG59XG5cbmRpdi5jb3Zlci1zIHtcbiAgd2lkdGg6IDEyMHB4O1xuICBwb3NpdGlvbjogcmVsYXRpdmU7XG59XG5cbmEuY292ZXItcyB7XG4gIHdpZHRoOiAxMjBweDtcbiAgaGVpZ2h0OiAxMjBweDtcbn1cblxuaW1nLmNvdmVyLXMge1xuICB3aWR0aDogMTIwcHg7XG4gIGhlaWdodDogMTIwcHg7XG4gIG9iamVjdC1maXQ6Y292ZXI7XG59XG5cbi5jYXJkIHtcbiAgYm9yZGVyLXN0eWxlOiBub25lO1xufVxuXG4uY2FyZC1ib2R5IHtcbiAgcG9zaXRpb246IHJlbGF0aXZlO1xuICB3aWR0aDogMTIwcHg7XG4gIGhlaWdodDogMTAwcHg7XG59XG5cbi5jYXJkLXRleHQge1xuICBvdmVyZmxvdzogaGlkZGVuO1xuICBkaXNwbGF5OiAtd2Via2l0LWJveDtcbiAgLXdlYmtpdC1saW5lLWNsYW1wOiAzO1xuICAtd2Via2l0LWJveC1vcmllbnQ6IHZlcnRpY2FsO1xufVxuXG4uY2FyZC1mb290ZXIge1xuICBib3JkZXItc3R5bGU6IG5vbmU7XG4gIGJhY2tncm91bmQ6IG5vbmU7XG4gIHBvc2l0aW9uOiBhYnNvbHV0ZTtcbiAgYm90dG9tOiAwcHg7XG59XG4iXX0= */";
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
    /* harmony import */


    var _app_service__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(
    /*! ../app.service */
    "./src/app/app.service.ts");

    var DocComponent =
    /*#__PURE__*/
    function () {
      function DocComponent(http, route, meta, service) {
        _classCallCheck(this, DocComponent);

        this.http = http;
        this.route = route;
        this.meta = meta;
        this.service = service;
        this.error = null;
        this.docId = null;
        this.doc = null;
        this.links = [];
      }

      _createClass(DocComponent, [{
        key: "ngOnInit",
        value: function ngOnInit() {
          var _this2 = this;

          this.route.paramMap.subscribe(function (params) {
            _this2.docId = params.get('docId');

            _this2.loadDoc(_this2.docId);

            _this2.loadLinks(_this2.docId);
          });
        }
      }, {
        key: "loadDoc",
        value: function loadDoc(id) {
          var _this3 = this;

          this.service.resetEvents();
          this.http.get('/api/docs/' + id).subscribe(function (data) {
            return _this3.setDoc(data[0]);
          }, function (error) {
            return _this3.error = error;
          });
        }
      }, {
        key: "loadLinks",
        value: function loadLinks(id) {
          var _this4 = this;

          this.links = [];
          this.http.get('/api/docs/' + id + '/links').subscribe(function (data) {
            return _this4.setLinks(data);
          }, function (error) {
            return _this4.error = error;
          });
        }
      }, {
        key: "setDoc",
        value: function setDoc(doc) {
          this.doc = doc;
          this.meta.updateTag({
            name: 'og:site_name',
            content: 'Human Beats'
          });
          this.meta.updateTag({
            name: 'og:title',
            content: this.doc.artist + ' - ' + this.doc.title + ' :: Human Beats'
          });
          this.meta.updateTag({
            name: 'og:type',
            content: 'music.album'
          }); //this.meta.updateTag({ name: 'og:type', content: 'music.playlist' });

          this.meta.updateTag({
            name: 'og:image',
            content: this.doc.cover
          });
          this.meta.updateTag({
            name: 'og:url',
            content: 'https://humanbeats.appspot.com/docs/' + this.doc.id
          });
          this.meta.updateTag({
            name: 'og:locale',
            content: 'it_IT'
          });
          this.meta.updateTag({
            name: 'og:description',
            content: this.doc.description
          });
          this.meta.updateTag({
            name: 'music:musician',
            content: this.doc.artist
          });
          this.meta.updateTag({
            name: 'music:release_date',
            content: this.doc.year + '-01-01'
          });
          this.meta.updateTag({
            name: 'music:album',
            content: this.doc.title
          }); //this.meta.updateTag({ name: 'music:creator', content: this.doc.title });

          if (this.service.audio) {
            this.service.audio.pause();
            this.service.audio = null;
          }

          if (this.doc.audio) {
            this.service.audio = new Audio();
            this.service.audio.src = this.doc.audio;
            this.service.audio.load();
          }

          this.service.resetEvents();
          this.links.splice(0, this.links.length);

          if (this.doc.artistid) {
            this.loadLinks(this.doc.artistid);
          }
        }
      }, {
        key: "setLinks",
        value: function setLinks(links) {
          this.links.splice(0, this.links.length);
          this.links.push.apply(this.links, links);
          var id = this.doc.id;
          this.links = this.links.filter(function (value, index, arr) {
            return value.id != id;
          });
        }
      }, {
        key: "playPause",
        value: function playPause(event) {
          if (this.service.audio.paused) {
            this.service.audio.play();
          } else {
            this.service.audio.pause();
          }
        }
      }, {
        key: "forward",
        value: function forward(event) {
          if (!this.service.audio.paused) {
            this.service.audio.currentTime += 60.0;
          }
        }
      }, {
        key: "backward",
        value: function backward(event) {
          if (!this.service.audio.paused) {
            this.service.audio.currentTime -= 60.0;
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
        type: _angular_platform_browser__WEBPACK_IMPORTED_MODULE_4__["Meta"]
      }, {
        type: _app_service__WEBPACK_IMPORTED_MODULE_5__["AppService"]
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
  "./src/app/doc/events/events.component.css":
  /*!*************************************************!*\
    !*** ./src/app/doc/events/events.component.css ***!
    \*************************************************/

  /*! exports provided: default */

  /***/
  function srcAppDocEventsEventsComponentCss(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = ".modal-bg {\n    position: fixed;\n    top: 0;\n    left: 0;\n    width: 100vw;\n    height: 100vh;\n    z-index: 30;\n    background: rgba(0, 0, 0, 0.75);\n    cursor: pointer;\n}\n\n.modal-box {\n    width: 80vw;\n    max-width: 600px;\n    max-height: 80vh;\n    z-index: 40;\n    border-style: solid;\n    border-width: 1px;\n    border-color: white;\n}\n\n.list-group-item {\n    background-color: transparent;\n    border-style: none;\n    cursor: pointer;\n}\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInNyYy9hcHAvZG9jL2V2ZW50cy9ldmVudHMuY29tcG9uZW50LmNzcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQTtJQUNJLGVBQWU7SUFDZixNQUFNO0lBQ04sT0FBTztJQUNQLFlBQVk7SUFDWixhQUFhO0lBQ2IsV0FBVztJQUNYLCtCQUErQjtJQUMvQixlQUFlO0FBQ25COztBQUVBO0lBQ0ksV0FBVztJQUNYLGdCQUFnQjtJQUNoQixnQkFBZ0I7SUFDaEIsV0FBVztJQUNYLG1CQUFtQjtJQUNuQixpQkFBaUI7SUFDakIsbUJBQW1CO0FBQ3ZCOztBQUVBO0lBQ0ksNkJBQTZCO0lBQzdCLGtCQUFrQjtJQUNsQixlQUFlO0FBQ25CIiwiZmlsZSI6InNyYy9hcHAvZG9jL2V2ZW50cy9ldmVudHMuY29tcG9uZW50LmNzcyIsInNvdXJjZXNDb250ZW50IjpbIi5tb2RhbC1iZyB7XG4gICAgcG9zaXRpb246IGZpeGVkO1xuICAgIHRvcDogMDtcbiAgICBsZWZ0OiAwO1xuICAgIHdpZHRoOiAxMDB2dztcbiAgICBoZWlnaHQ6IDEwMHZoO1xuICAgIHotaW5kZXg6IDMwO1xuICAgIGJhY2tncm91bmQ6IHJnYmEoMCwgMCwgMCwgMC43NSk7XG4gICAgY3Vyc29yOiBwb2ludGVyO1xufVxuXG4ubW9kYWwtYm94IHtcbiAgICB3aWR0aDogODB2dztcbiAgICBtYXgtd2lkdGg6IDYwMHB4O1xuICAgIG1heC1oZWlnaHQ6IDgwdmg7XG4gICAgei1pbmRleDogNDA7XG4gICAgYm9yZGVyLXN0eWxlOiBzb2xpZDtcbiAgICBib3JkZXItd2lkdGg6IDFweDtcbiAgICBib3JkZXItY29sb3I6IHdoaXRlO1xufVxuXG4ubGlzdC1ncm91cC1pdGVtIHtcbiAgICBiYWNrZ3JvdW5kLWNvbG9yOiB0cmFuc3BhcmVudDtcbiAgICBib3JkZXItc3R5bGU6IG5vbmU7XG4gICAgY3Vyc29yOiBwb2ludGVyO1xufSJdfQ== */";
    /***/
  },

  /***/
  "./src/app/doc/events/events.component.ts":
  /*!************************************************!*\
    !*** ./src/app/doc/events/events.component.ts ***!
    \************************************************/

  /*! exports provided: EventsComponent */

  /***/
  function srcAppDocEventsEventsComponentTs(module, __webpack_exports__, __webpack_require__) {
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


    var _app_service__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(
    /*! ../../app.service */
    "./src/app/app.service.ts");

    var EventsComponent =
    /*#__PURE__*/
    function () {
      function EventsComponent(service) {
        _classCallCheck(this, EventsComponent);

        this.service = service;
        this.events = [];
      }

      _createClass(EventsComponent, [{
        key: "ngOnInit",
        value: function ngOnInit() {}
      }, {
        key: "close",
        value: function close() {
          this.service.showEvents = false;
        }
      }]);

      return EventsComponent;
    }();

    EventsComponent.ctorParameters = function () {
      return [{
        type: _app_service__WEBPACK_IMPORTED_MODULE_2__["AppService"]
      }];
    };

    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])()], EventsComponent.prototype, "events", void 0);
    EventsComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
      selector: 'app-events',
      template: tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! raw-loader!./events.component.html */
      "./node_modules/raw-loader/dist/cjs.js!./src/app/doc/events/events.component.html")).default,
      styles: [tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! ./events.component.css */
      "./src/app/doc/events/events.component.css")).default]
    })], EventsComponent);
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


    __webpack_exports__["default"] = "img.cover {\n    width: 40px;\n    height: 40px;\n    -o-object-fit:cover;\n       object-fit:cover;\n}\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInNyYy9hcHAvZG9jL21lbnUvbWVudS5jb21wb25lbnQuY3NzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUFBO0lBQ0ksV0FBVztJQUNYLFlBQVk7SUFDWixtQkFBZ0I7T0FBaEIsZ0JBQWdCO0FBQ3BCIiwiZmlsZSI6InNyYy9hcHAvZG9jL21lbnUvbWVudS5jb21wb25lbnQuY3NzIiwic291cmNlc0NvbnRlbnQiOlsiaW1nLmNvdmVyIHtcbiAgICB3aWR0aDogNDBweDtcbiAgICBoZWlnaHQ6IDQwcHg7XG4gICAgb2JqZWN0LWZpdDpjb3Zlcjtcbn0iXX0= */";
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


    var _doc_component__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(
    /*! ../doc.component */
    "./src/app/doc/doc.component.ts");

    var DocMenuComponent =
    /*#__PURE__*/
    function () {
      function DocMenuComponent(component) {
        _classCallCheck(this, DocMenuComponent);

        this.component = component;
      }

      _createClass(DocMenuComponent, [{
        key: "ngOnInit",
        value: function ngOnInit() {}
      }]);

      return DocMenuComponent;
    }();

    DocMenuComponent.ctorParameters = function () {
      return [{
        type: _doc_component__WEBPACK_IMPORTED_MODULE_2__["DocComponent"]
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


    __webpack_exports__["default"] = ".card {\n  border-style: none;\n}\n\n.card-body {\n  position: relative;\n  width: 250px;\n  height: 150px;\n}\n\n.card-text {\n  overflow: hidden;\n  display: -webkit-box;\n  -webkit-line-clamp: 3;\n  -webkit-box-orient: vertical;\n}\n\n.card-footer {\n  border-style: none;\n  background: none;\n  position: absolute;\n  bottom: 0px;\n}\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInNyYy9hcHAvZG9jcy9kb2NzLmNvbXBvbmVudC5jc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUE7RUFDRSxrQkFBa0I7QUFDcEI7O0FBRUE7RUFDRSxrQkFBa0I7RUFDbEIsWUFBWTtFQUNaLGFBQWE7QUFDZjs7QUFFQTtFQUNFLGdCQUFnQjtFQUNoQixvQkFBb0I7RUFDcEIscUJBQXFCO0VBQ3JCLDRCQUE0QjtBQUM5Qjs7QUFFQTtFQUNFLGtCQUFrQjtFQUNsQixnQkFBZ0I7RUFDaEIsa0JBQWtCO0VBQ2xCLFdBQVc7QUFDYiIsImZpbGUiOiJzcmMvYXBwL2RvY3MvZG9jcy5jb21wb25lbnQuY3NzIiwic291cmNlc0NvbnRlbnQiOlsiLmNhcmQge1xuICBib3JkZXItc3R5bGU6IG5vbmU7XG59XG5cbi5jYXJkLWJvZHkge1xuICBwb3NpdGlvbjogcmVsYXRpdmU7XG4gIHdpZHRoOiAyNTBweDtcbiAgaGVpZ2h0OiAxNTBweDtcbn1cblxuLmNhcmQtdGV4dCB7XG4gIG92ZXJmbG93OiBoaWRkZW47XG4gIGRpc3BsYXk6IC13ZWJraXQtYm94O1xuICAtd2Via2l0LWxpbmUtY2xhbXA6IDM7XG4gIC13ZWJraXQtYm94LW9yaWVudDogdmVydGljYWw7XG59XG5cbi5jYXJkLWZvb3RlciB7XG4gIGJvcmRlci1zdHlsZTogbm9uZTtcbiAgYmFja2dyb3VuZDogbm9uZTtcbiAgcG9zaXRpb246IGFic29sdXRlO1xuICBib3R0b206IDBweDtcbn0iXX0= */";
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

    var DocsComponent =
    /*#__PURE__*/
    function () {
      function DocsComponent(http) {
        _classCallCheck(this, DocsComponent);

        this.http = http;
        this.error = null;
        this.searchText = '';
        this.docType = 'album';
        this.page = 0;
        this.docs = [];
        this.tracks = [];
      }

      _createClass(DocsComponent, [{
        key: "ngOnInit",
        value: function ngOnInit() {
          this.searchText = '';
          this.loadDocs(0, 'album');
        }
      }, {
        key: "onSearch",
        value: function onSearch() {
          if (this.docType == 'video') {
            this.loadTracks(0);
          } else {
            this.loadDocs(0, this.docType);
          }
        }
      }, {
        key: "scrollDocs",
        value: function scrollDocs() {
          this.loadDocs(this.page + 1, this.docType);
        }
      }, {
        key: "scrollTracks",
        value: function scrollTracks() {
          this.loadTracks(this.page + 1);
        }
      }, {
        key: "loadDocs",
        value: function loadDocs(page, docType) {
          var _this5 = this;

          this.page = page;
          this.docType = docType;

          if (this.page == 0) {
            this.docs.splice(0, this.docs.length);
            this.tracks.splice(0, this.tracks.length);
          }

          this.http.get('/api/docs?p=' + this.page + '&q=' + this.searchText + '&t=' + this.docType).subscribe(function (data) {
            return _this5.docsLoaded(data);
          }, function (error) {
            return _this5.error = error;
          });
        }
      }, {
        key: "docsLoaded",
        value: function docsLoaded(data) {
          this.docs.push.apply(this.docs, data);
        }
      }, {
        key: "loadTracks",
        value: function loadTracks(page) {
          var _this6 = this;

          this.page = page;
          this.docType = 'video';

          if (this.page == 0) {
            this.docs.splice(0, this.docs.length);
            this.tracks.splice(0, this.tracks.length);
          }

          this.http.get('/api/tracks?p=' + this.page + '&q=' + this.searchText).subscribe(function (data) {
            return _this6.tracksLoaded(data);
          }, function (error) {
            return _this6.error = error;
          });
        }
      }, {
        key: "tracksLoaded",
        value: function tracksLoaded(data) {
          this.tracks.push.apply(this.tracks, data);
        }
      }]);

      return DocsComponent;
    }();

    DocsComponent.ctorParameters = function () {
      return [{
        type: _angular_common_http__WEBPACK_IMPORTED_MODULE_2__["HttpClient"]
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


    __webpack_exports__["default"] = ".menu-item {\n    cursor: pointer;\n}\n\n.search {\n    width: 250px;\n    min-width: 250px;\n}\n\n.selected {\n    border-radius: 20px;\n    background-color: #000000;\n}\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInNyYy9hcHAvZG9jcy9tZW51L21lbnUuY29tcG9uZW50LmNzcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQTtJQUNJLGVBQWU7QUFDbkI7O0FBRUE7SUFDSSxZQUFZO0lBQ1osZ0JBQWdCO0FBQ3BCOztBQUVBO0lBQ0ksbUJBQW1CO0lBQ25CLHlCQUF5QjtBQUM3QiIsImZpbGUiOiJzcmMvYXBwL2RvY3MvbWVudS9tZW51LmNvbXBvbmVudC5jc3MiLCJzb3VyY2VzQ29udGVudCI6WyIubWVudS1pdGVtIHtcbiAgICBjdXJzb3I6IHBvaW50ZXI7XG59XG5cbi5zZWFyY2gge1xuICAgIHdpZHRoOiAyNTBweDtcbiAgICBtaW4td2lkdGg6IDI1MHB4O1xufVxuXG4uc2VsZWN0ZWQge1xuICAgIGJvcmRlci1yYWRpdXM6IDIwcHg7XG4gICAgYmFja2dyb3VuZC1jb2xvcjogIzAwMDAwMDtcbn1cbiJdfQ== */";
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
      function DocsMenuComponent(component) {
        _classCallCheck(this, DocsMenuComponent);

        this.component = component;
      }

      _createClass(DocsMenuComponent, [{
        key: "ngOnInit",
        value: function ngOnInit() {}
      }, {
        key: "onSearch",
        value: function onSearch() {
          this.component.onSearch();
        }
      }, {
        key: "resetSearch",
        value: function resetSearch() {
          this.component.ngOnInit();
        }
      }, {
        key: "loadAlbums",
        value: function loadAlbums() {
          this.component.loadDocs(0, 'album');
        }
      }, {
        key: "loadArtists",
        value: function loadArtists() {
          this.component.loadDocs(0, 'artist');
        }
      }, {
        key: "loadPodcasts",
        value: function loadPodcasts() {
          this.component.loadDocs(0, 'podcast');
        }
      }, {
        key: "loadConcerts",
        value: function loadConcerts() {
          this.component.loadDocs(0, 'concert');
        }
      }, {
        key: "loadInterviews",
        value: function loadInterviews() {
          this.component.loadDocs(0, 'interview');
        }
      }, {
        key: "loadVideos",
        value: function loadVideos() {
          this.component.loadTracks(0);
        }
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
  "./src/app/youtube/youtube.component.css":
  /*!***********************************************!*\
    !*** ./src/app/youtube/youtube.component.css ***!
    \***********************************************/

  /*! exports provided: default */

  /***/
  function srcAppYoutubeYoutubeComponentCss(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony default export */


    __webpack_exports__["default"] = ".player {\n    width: 350px;\n}\n\n.close {\n    bottom: 5px;\n    right: 5px;\n    position: absolute;\n}\n\n.youtube-selected {\n    background:#FF0000;\n}\n\n.songkick-selected {\n    background:#F80046;\n}\n\na.cover {\n    width: 40px;\n    height: 40px;\n}\n\nimg.cover {\n    width: 40px;\n    height: 40px;\n    -o-object-fit:cover;\n       object-fit:cover;\n}\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInNyYy9hcHAveW91dHViZS95b3V0dWJlLmNvbXBvbmVudC5jc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUE7SUFDSSxZQUFZO0FBQ2hCOztBQUVBO0lBQ0ksV0FBVztJQUNYLFVBQVU7SUFDVixrQkFBa0I7QUFDdEI7O0FBRUE7SUFDSSxrQkFBa0I7QUFDdEI7O0FBRUE7SUFDSSxrQkFBa0I7QUFDdEI7O0FBRUE7SUFDSSxXQUFXO0lBQ1gsWUFBWTtBQUNoQjs7QUFFQTtJQUNJLFdBQVc7SUFDWCxZQUFZO0lBQ1osbUJBQWdCO09BQWhCLGdCQUFnQjtBQUNwQiIsImZpbGUiOiJzcmMvYXBwL3lvdXR1YmUveW91dHViZS5jb21wb25lbnQuY3NzIiwic291cmNlc0NvbnRlbnQiOlsiLnBsYXllciB7XG4gICAgd2lkdGg6IDM1MHB4O1xufVxuXG4uY2xvc2Uge1xuICAgIGJvdHRvbTogNXB4O1xuICAgIHJpZ2h0OiA1cHg7XG4gICAgcG9zaXRpb246IGFic29sdXRlO1xufVxuXG4ueW91dHViZS1zZWxlY3RlZCB7XG4gICAgYmFja2dyb3VuZDojRkYwMDAwO1xufVxuXG4uc29uZ2tpY2stc2VsZWN0ZWQge1xuICAgIGJhY2tncm91bmQ6I0Y4MDA0Njtcbn1cblxuYS5jb3ZlciB7XG4gICAgd2lkdGg6IDQwcHg7XG4gICAgaGVpZ2h0OiA0MHB4O1xufVxuXG5pbWcuY292ZXIge1xuICAgIHdpZHRoOiA0MHB4O1xuICAgIGhlaWdodDogNDBweDtcbiAgICBvYmplY3QtZml0OmNvdmVyO1xufVxuIl19 */";
    /***/
  },

  /***/
  "./src/app/youtube/youtube.component.ts":
  /*!**********************************************!*\
    !*** ./src/app/youtube/youtube.component.ts ***!
    \**********************************************/

  /*! exports provided: YoutubeComponent */

  /***/
  function srcAppYoutubeYoutubeComponentTs(module, __webpack_exports__, __webpack_require__) {
    "use strict";

    __webpack_require__.r(__webpack_exports__);
    /* harmony export (binding) */


    __webpack_require__.d(__webpack_exports__, "YoutubeComponent", function () {
      return YoutubeComponent;
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
    /*! ../app.service */
    "./src/app/app.service.ts");

    var YoutubeComponent =
    /*#__PURE__*/
    function () {
      function YoutubeComponent(service) {
        _classCallCheck(this, YoutubeComponent);

        this.service = service;
        this.player = null;
        this.status = null;
        this.video = null;
        this.tracks = null;
      }

      _createClass(YoutubeComponent, [{
        key: "ngOnInit",
        value: function ngOnInit() {
          var tag = document.createElement('script');
          tag.src = 'https://www.youtube.com/iframe_api';
          document.body.appendChild(tag);
        }
      }, {
        key: "onReady",
        value: function onReady(event) {
          this.player = event.target;
          this.player.playVideo();
        }
      }, {
        key: "onStateChange",
        value: function onStateChange(event) {
          this.status = event.data;

          switch (this.status) {
            case 0:
              console.log('video ended');
              this.forward(null);
              break;

            case 1:
              console.log('video playing');
              break;

            case 2:
              console.log('video paused');
              break;

            case 5:
              console.log('video cued');
              this.player.playVideo();
              break;
          }
        }
      }, {
        key: "loadVideo",
        value: function loadVideo(track) {
          this.video = track;
        }
      }, {
        key: "play",
        value: function play(event) {
          this.player.playVideo();
        }
      }, {
        key: "pause",
        value: function pause(event) {
          this.player.pauseVideo();
        }
      }, {
        key: "playPause",
        value: function playPause(event) {
          if (this.status == 1) {
            this.player.pauseVideo();
          } else {
            this.player.playVideo();
          }
        }
      }, {
        key: "forward",
        value: function forward(event) {
          if (this.tracks.indexOf(this.video) < this.tracks.length - 1) {
            this.video = this.tracks[this.tracks.indexOf(this.video) + 1];
          }
        }
      }, {
        key: "backward",
        value: function backward(event) {
          if (this.tracks.indexOf(this.video) > 0) {
            this.video = this.tracks[this.tracks.indexOf(this.video) - 1];
          }
        }
      }, {
        key: "close",
        value: function close(event) {
          this.video = null;
        }
      }]);

      return YoutubeComponent;
    }();

    YoutubeComponent.ctorParameters = function () {
      return [{
        type: _app_service__WEBPACK_IMPORTED_MODULE_2__["AppService"]
      }];
    };

    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])()], YoutubeComponent.prototype, "tracks", void 0);
    YoutubeComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
      selector: 'app-youtube',
      template: tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! raw-loader!./youtube.component.html */
      "./node_modules/raw-loader/dist/cjs.js!./src/app/youtube/youtube.component.html")).default,
      styles: [tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(
      /*! ./youtube.component.css */
      "./src/app/youtube/youtube.component.css")).default]
    })], YoutubeComponent);
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