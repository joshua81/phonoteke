(window["webpackJsonp"] = window["webpackJsonp"] || []).push([["main"],{

/***/ "./$$_lazy_route_resource lazy recursive":
/*!******************************************************!*\
  !*** ./$$_lazy_route_resource lazy namespace object ***!
  \******************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

function webpackEmptyAsyncContext(req) {
	// Here Promise.resolve().then() is used instead of new Promise() to prevent
	// uncaught exception popping up in devtools
	return Promise.resolve().then(function() {
		var e = new Error("Cannot find module '" + req + "'");
		e.code = 'MODULE_NOT_FOUND';
		throw e;
	});
}
webpackEmptyAsyncContext.keys = function() { return []; };
webpackEmptyAsyncContext.resolve = webpackEmptyAsyncContext;
module.exports = webpackEmptyAsyncContext;
webpackEmptyAsyncContext.id = "./$$_lazy_route_resource lazy recursive";

/***/ }),

/***/ "./node_modules/raw-loader/dist/cjs.js!./src/app/app.component.html":
/*!**************************************************************************!*\
  !*** ./node_modules/raw-loader/dist/cjs.js!./src/app/app.component.html ***!
  \**************************************************************************/
/*! exports provided: default */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony default export */ __webpack_exports__["default"] = ("<div class=\"container-fluid\">\n  <router-outlet></router-outlet>\n  <!--footer class=\"page-footer font-small\">\n      <div class=\"footer-copyright text-center\">\n        <div class=\"pb-2\"><small>Per info, segnalazioni e suggerimenti, scrivere ad Andrea Ricci.</small></div>\n        <div class=\"d-flex justify-content-center\">\n          <a class=\"pr-2\" href=\"mailto:andrea.ricci@gmx.com\"><span class=\"mail\"></span></a>\n          <a class=\"pr-2\" href=\"https://github.com/joshua81/\" target=\"_blank\"><span class=\"github\"></span></a>\n          <a class=\"pr-2\" href=\"https://www.linkedin.com/in/ricciandrea/\" target=\"_blank\"><span class=\"linkedin\"></span></a>\n          <a class=\"pr-2\" href=\"https://creativecommons.org/\" target=\"_blank\"><span class=\"cc\"></span></a>\n        </div>\n      </div>\n    </footer-->\n</div>\n");

/***/ }),

/***/ "./node_modules/raw-loader/dist/cjs.js!./src/app/doc/doc.component.html":
/*!******************************************************************************!*\
  !*** ./node_modules/raw-loader/dist/cjs.js!./src/app/doc/doc.component.html ***!
  \******************************************************************************/
/*! exports provided: default */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony default export */ __webpack_exports__["default"] = ("<doc-menu *ngIf=\"doc\"></doc-menu>\n<div *ngIf=\"doc\" class=\"container pt-5 pb-5 w-max\">\n    <div class=\"row pt-2\">\n      <div class=\"cover-m col-auto pl-0 pr-3\">\n        <img class=\"cover-m rounded\" src=\"{{doc.cover}}\"/>\n        <div *ngIf=\"doc.type == 'album' && doc.vote && doc.vote == 10\" class=\"vote\">\n          <span class=\"star\"></span>\n        </div>\n        <div *ngIf=\"doc.type == 'album' && doc.vote && doc.vote != 10\" class=\"vote\">{{doc.vote}}</div>\n      </div>\n\n      <div class=\"col-auto pl-0\">\n        <h1 *ngIf=\"doc.type != 'artist'\" [innerHTML]=\"doc.title\"></h1>\n        <h2 *ngIf=\"doc.type != 'artist'\" [innerHTML]=\"doc.artist\"></h2>\n        <h1 *ngIf=\"doc.type == 'artist'\" [innerHTML]=\"doc.artist\"></h1>\n        <h3 *ngIf=\"doc.type == 'album' && doc.label\">{{doc.label + ' | ' + doc.year + ' | ' + doc.genres}}</h3>\n        <h3 *ngIf=\"doc.authors\">di {{doc.authors}}</h3>\n      </div>\n    </div>\n\n    <div class=\"row pt-3\">\n      <a class=\"pr-2\" href=\"{{doc.url}}\" target=\"_blank\">\n        <img class=\"{{doc.source}}\">\n      </a>\n      <!--a *ngIf=\"doc.type != 'album' && doc.artistid\" class=\"pr-2\" href=\"{{'https://musicbrainz.org/artist/' + doc.artistid}}\" target=\"_blank\">\n        <img class=\"musicbrainz\">\n      </a>\n      <a *ngIf=\"doc.type == 'album' && doc.albumid\" class=\"pr-2\" href=\"{{'https://musicbrainz.org/release-group/' + doc.albumid}}\" target=\"_blank\">\n        <img class=\"musicbrainz\">\n      </a-->\n      <a *ngIf=\"doc.artistid != null\" class=\"pr-2\" (click)=\"service.loadEvents(doc.artistid)\">\n        <!--img class=\"songkick\"-->\n        <div><span class=\"songkickbtn\"></span></div>\n      </a>\n      <a *ngIf=\"doc.type != 'album' && doc.spartistid\" class=\"pr-2\"\n        href=\"{{'https://open.spotify.com/artist/' + doc.spartistid}}\" target=\"_blank\">\n        <!--img class=\"spotify\"-->\n        <div><span class=\"spotifybtn\"></span></div>\n      </a>\n      <a *ngIf=\"doc.type == 'album' && doc.spalbumid\" class=\"pr-2\"\n        href=\"{{'https://open.spotify.com/album/' + doc.spalbumid}}\" target=\"_blank\">\n        <!--img class=\"spotify\"-->\n        <div><span class=\"spotifybtn\"></span></div>\n      </a>\n      <div *ngIf=\"service.audio\" class=\"pr-2\" (click)=\"backward($event)\">\n        <span class=\"backwardbtn\"></span>\n      </div>\n      <div *ngIf=\"service.audio\" class=\"pr-2\" (click)=\"playPause($event)\">\n        <span *ngIf=\"service.audio.paused\" class=\"playbtn\"></span>\n        <span *ngIf=\"!service.audio.paused\" class=\"pausebtn\"></span>\n      </div>\n      <div *ngIf=\"service.audio\" class=\"pr-2\" (click)=\"forward($event)\">\n        <span class=\"forwardbtn\"></span>\n      </div>\n    </div>\n\n    <div class=\"row pt-3\">\n      <div *ngIf=\"doc.review\" class=\"text-justify\" \n        [innerHTML]=\"doc.review\">\n      </div>\n    </div>\n\n    <!--div *ngIf=\"doc.tracks && doc.tracks.length != 0\" class=\"row\">\n      <h2>Video</h2>\n    </div-->\n    <div class=\"row pt-3 d-flex justify-content-center\">\n      <app-youtube [tracks]=\"doc.tracks\"></app-youtube>\n    </div>\n\n    <!--div *ngIf=\"links && links.length != 0\" class=\"row pt-3\">\n      <h2>Link</h2>\n    </div-->\n    <div *ngIf=\"links && links.length != 0\" class=\"row pt-3 d-flex justify-content-center\">\n      <div class=\"list-group w-md\">\n        <div *ngFor=\"let link of links\" class=\"list-group-item d-flex justify-content-center p-1\">\n          <div class=\"w-100\">\n            <a [routerLink]=\"['/docs/' + link.id]\">\n              <div class=\"d-flex align-items-center\">\n                <div class=\"pr-2\">\n                  <img class=\"cover-s\" src=\"{{link.cover}}\">\n                </div>\n                <div class=\"w-100 pr-2\">\n                  <div class=\"font-weight-bold\" [innerHTML]=\"link.title\"></div>\n                  <div [innerHTML]=\"link.artist\"></div>\n                </div>\n                <div *ngIf=\"link.type == 'album' && link.vote && link.vote == 10\" class=\"vote-link\">\n                  <span class=\"star\"></span>\n                </div>\n                <div *ngIf=\"link.type == 'album' && link.vote && link.vote != 10\" class=\"vote-link\">{{link.vote}}</div>\n              </div>\n            </a>\n          </div>\n        </div>\n      </div>\n    </div>\n</div>\n\n<app-events *ngIf=\"service.showEvents == true\" [events]=\"service.events\"></app-events>\n");

/***/ }),

/***/ "./node_modules/raw-loader/dist/cjs.js!./src/app/doc/events/events.component.html":
/*!****************************************************************************************!*\
  !*** ./node_modules/raw-loader/dist/cjs.js!./src/app/doc/events/events.component.html ***!
  \****************************************************************************************/
/*! exports provided: default */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony default export */ __webpack_exports__["default"] = ("<div class=\"d-flex justify-content-center modal-bg\" (click)=\"close()\">\n    <div *ngIf=\"events && events.length != 0\" class=\"align-self-center overflow-auto modal-box\">\n        <div class=\"list-group\">\n            <div *ngFor=\"let event of events\" class=\"p-2 list-group-item\">\n                <div class=\"d-flex align-items-center\">\n                    <div class=\"w-100\">\n                        <div class=\"font-weight-bold\">{{event.displayName}}</div>\n                        <div>{{event.start.date}} {{event.location.city}}</div>\n                        <div>{{event.venue.displayName}}</div>\n                    </div>\n                    <a href=\"{{event.uri}}\" target=\"_blank\">\n                        <!--img class=\"songkick\"-->\n                        <div><span class=\"songkickbtn\"></span></div>\n                    </a>\n                </div>\n            </div>\n        </div>\n    </div>\n    <div *ngIf=\"events && events.length == 0\" class=\"align-self-center overflow-auto modal-box\">\n        <div class=\"d-flex justify-content-center align-middle\">\n            <p class=\"font-weight-bold pt-2\">Nessun evento in programma</p>\n        </div>\n    </div>\n</div>");

/***/ }),

/***/ "./node_modules/raw-loader/dist/cjs.js!./src/app/doc/menu/menu.component.html":
/*!************************************************************************************!*\
  !*** ./node_modules/raw-loader/dist/cjs.js!./src/app/doc/menu/menu.component.html ***!
  \************************************************************************************/
/*! exports provided: default */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony default export */ __webpack_exports__["default"] = ("<nav class=\"navbar navbar-expand-lg navbar-light fixed-top justify-content-center\">\n  <div class=\"d-flex align-items-center w-max\">\n    <div class=\"pr-2\">\n      <a [routerLink]=\"['/']\">\n        <img class=\"cover rounded\" src=\"{{component.doc.cover}}\"/>\n      </a>\n    </div>\n\n    <div class=\"text-truncate w-100\">\n      <div class=\"font-weight-bold\" [innerHTML]=\"component.doc.title\"></div>\n      <div [innerHTML]=\"component.doc.artist\"></div>\n    </div>\n  </div>\n</nav>\n");

/***/ }),

/***/ "./node_modules/raw-loader/dist/cjs.js!./src/app/docs/docs.component.html":
/*!********************************************************************************!*\
  !*** ./node_modules/raw-loader/dist/cjs.js!./src/app/docs/docs.component.html ***!
  \********************************************************************************/
/*! exports provided: default */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony default export */ __webpack_exports__["default"] = ("<docs-menu></docs-menu>\n<div class=\"container pt-5 pb-5 w-max\">\n  <div infinite-scroll class=\"row d-flex justify-content-center pt-2\" (scrolled)=\"loadAlbumsScroll()\">\n      <div *ngFor=\"let doc of docs\" class=\"pb-3 pr-2\">\n        <div class=\"cover-m card mx-auto\">\n          <a class=\"cover-m\" [routerLink]=\"['/docs/' + doc.id]\">\n            <img class=\"cover-m card-img-top\" src=\"{{doc.cover}}\"/>\n            <div *ngIf=\"doc.type == 'album' && doc.vote && doc.vote == 10\" class=\"vote\">\n              <span class=\"star\"></span>\n            </div>\n            <div *ngIf=\"doc.type == 'album' && doc.vote && doc.vote != 10\" class=\"vote\">{{doc.vote}}</div>\n          </a>\n          <div class=\"card-body p-0\">\n            <div *ngIf=\"doc.type != 'artist'\" class=\"card-text font-weight-bold\" [innerHTML]=\"doc.title\"></div>\n            <div *ngIf=\"doc.type != 'artist'\" class=\"card-text\" [innerHTML]=\"doc.artist\"></div>\n            <div *ngIf=\"doc.type == 'artist'\" class=\"card-text font-weight-bold\" [innerHTML]=\"doc.artist\"></div>\n            <div *ngIf=\"doc.description\" class=\"card-text pt-2\" [innerHTML]=\"doc.description\"></div>\n            <footer *ngIf=\"doc.authors\" class=\"blockquote-footer pt-2 float-right\">di {{doc.authors}}</footer>\n          </div>\n        </div>\n      </div>\n  </div>\n</div>\n\n");

/***/ }),

/***/ "./node_modules/raw-loader/dist/cjs.js!./src/app/docs/menu/menu.component.html":
/*!*************************************************************************************!*\
  !*** ./node_modules/raw-loader/dist/cjs.js!./src/app/docs/menu/menu.component.html ***!
  \*************************************************************************************/
/*! exports provided: default */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony default export */ __webpack_exports__["default"] = ("<nav class=\"navbar navbar-expand-lg navbar-light fixed-top justify-content-center\">\n  <div class=\"d-flex align-items-center w-max\">\n    <!--div class=\"pr-2\">\n      <img class=\"menubtn\"/>\n    </div-->\n    \n    <form class=\"w-100\">\n      <input class=\"form-control\" name=\"search\" type=\"text\" placeholder=\"Search\" aria-label=\"Search\" [(ngModel)]=\"searchText\"  (keydown.enter)=\"onSearch()\">\n    </form>\n  </div>\n</nav>\n");

/***/ }),

/***/ "./node_modules/raw-loader/dist/cjs.js!./src/app/youtube/youtube.component.html":
/*!**************************************************************************************!*\
  !*** ./node_modules/raw-loader/dist/cjs.js!./src/app/youtube/youtube.component.html ***!
  \**************************************************************************************/
/*! exports provided: default */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony default export */ __webpack_exports__["default"] = ("<div *ngIf=\"tracks && tracks.length != 0\" class=\"row d-flex justify-content-center\">\n    <div class=\"list-group w-md\">\n        <div *ngFor=\"let track of tracks\" class=\"list-group-item d-flex justify-content-center p-1\" (click)=\"loadVideo(track)\">\n            <div class=\"justify-content-center\">\n                <img *ngIf=\"video != track\" class=\"track\" src=\"{{'https://img.youtube.com/vi/' + track.youtube + '/mqdefault.jpg'}}\">\n                <youtube-player *ngIf=\"video == track\" width=\"320px\" height=\"180px\" suggestedQuality=\"default\" videoId=\"{{track.youtube}}\"\n                    (stateChange)=\"onStateChange($event)\"\n                    (ready)=\"onReady($event)\">\n                    Loading...\n                </youtube-player>\n                <div class=\"track\" [innerHTML]=\"track.title\"></div>\n                <!--div class=\"d-flex align-items-center\"-->\n                    <!--a *ngIf=\"track.albumid\" class=\"pr-2\" href=\"{{'https://musicbrainz.org/release-group/' + track.albumid}}\" target=\"_blank\">\n                        <img class=\"musicbrainz\">\n                    </a-->\n                    <!--a *ngIf=\"track.artistid\" (click)=\"service.loadEvents(track.artistid)\">\n                        <img class=\"songkick\">\n                    </a>\n                </div-->\n            </div>\n        </div>\n    </div>\n</div>");

/***/ }),

/***/ "./node_modules/tslib/tslib.es6.js":
/*!*****************************************!*\
  !*** ./node_modules/tslib/tslib.es6.js ***!
  \*****************************************/
/*! exports provided: __extends, __assign, __rest, __decorate, __param, __metadata, __awaiter, __generator, __exportStar, __values, __read, __spread, __spreadArrays, __await, __asyncGenerator, __asyncDelegator, __asyncValues, __makeTemplateObject, __importStar, __importDefault */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__extends", function() { return __extends; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__assign", function() { return __assign; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__rest", function() { return __rest; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__decorate", function() { return __decorate; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__param", function() { return __param; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__metadata", function() { return __metadata; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__awaiter", function() { return __awaiter; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__generator", function() { return __generator; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__exportStar", function() { return __exportStar; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__values", function() { return __values; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__read", function() { return __read; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__spread", function() { return __spread; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__spreadArrays", function() { return __spreadArrays; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__await", function() { return __await; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__asyncGenerator", function() { return __asyncGenerator; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__asyncDelegator", function() { return __asyncDelegator; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__asyncValues", function() { return __asyncValues; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__makeTemplateObject", function() { return __makeTemplateObject; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__importStar", function() { return __importStar; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "__importDefault", function() { return __importDefault; });
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

var extendStatics = function(d, b) {
    extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return extendStatics(d, b);
};

function __extends(d, b) {
    extendStatics(d, b);
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
}

var __assign = function() {
    __assign = Object.assign || function __assign(t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
            s = arguments[i];
            for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p)) t[p] = s[p];
        }
        return t;
    }
    return __assign.apply(this, arguments);
}

function __rest(s, e) {
    var t = {};
    for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p) && e.indexOf(p) < 0)
        t[p] = s[p];
    if (s != null && typeof Object.getOwnPropertySymbols === "function")
        for (var i = 0, p = Object.getOwnPropertySymbols(s); i < p.length; i++) {
            if (e.indexOf(p[i]) < 0 && Object.prototype.propertyIsEnumerable.call(s, p[i]))
                t[p[i]] = s[p[i]];
        }
    return t;
}

function __decorate(decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
}

function __param(paramIndex, decorator) {
    return function (target, key) { decorator(target, key, paramIndex); }
}

function __metadata(metadataKey, metadataValue) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(metadataKey, metadataValue);
}

function __awaiter(thisArg, _arguments, P, generator) {
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : new P(function (resolve) { resolve(result.value); }).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
}

function __generator(thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
}

function __exportStar(m, exports) {
    for (var p in m) if (!exports.hasOwnProperty(p)) exports[p] = m[p];
}

function __values(o) {
    var m = typeof Symbol === "function" && o[Symbol.iterator], i = 0;
    if (m) return m.call(o);
    return {
        next: function () {
            if (o && i >= o.length) o = void 0;
            return { value: o && o[i++], done: !o };
        }
    };
}

function __read(o, n) {
    var m = typeof Symbol === "function" && o[Symbol.iterator];
    if (!m) return o;
    var i = m.call(o), r, ar = [], e;
    try {
        while ((n === void 0 || n-- > 0) && !(r = i.next()).done) ar.push(r.value);
    }
    catch (error) { e = { error: error }; }
    finally {
        try {
            if (r && !r.done && (m = i["return"])) m.call(i);
        }
        finally { if (e) throw e.error; }
    }
    return ar;
}

function __spread() {
    for (var ar = [], i = 0; i < arguments.length; i++)
        ar = ar.concat(__read(arguments[i]));
    return ar;
}

function __spreadArrays() {
    for (var s = 0, i = 0, il = arguments.length; i < il; i++) s += arguments[i].length;
    for (var r = Array(s), k = 0, i = 0; i < il; i++)
        for (var a = arguments[i], j = 0, jl = a.length; j < jl; j++, k++)
            r[k] = a[j];
    return r;
};

function __await(v) {
    return this instanceof __await ? (this.v = v, this) : new __await(v);
}

function __asyncGenerator(thisArg, _arguments, generator) {
    if (!Symbol.asyncIterator) throw new TypeError("Symbol.asyncIterator is not defined.");
    var g = generator.apply(thisArg, _arguments || []), i, q = [];
    return i = {}, verb("next"), verb("throw"), verb("return"), i[Symbol.asyncIterator] = function () { return this; }, i;
    function verb(n) { if (g[n]) i[n] = function (v) { return new Promise(function (a, b) { q.push([n, v, a, b]) > 1 || resume(n, v); }); }; }
    function resume(n, v) { try { step(g[n](v)); } catch (e) { settle(q[0][3], e); } }
    function step(r) { r.value instanceof __await ? Promise.resolve(r.value.v).then(fulfill, reject) : settle(q[0][2], r); }
    function fulfill(value) { resume("next", value); }
    function reject(value) { resume("throw", value); }
    function settle(f, v) { if (f(v), q.shift(), q.length) resume(q[0][0], q[0][1]); }
}

function __asyncDelegator(o) {
    var i, p;
    return i = {}, verb("next"), verb("throw", function (e) { throw e; }), verb("return"), i[Symbol.iterator] = function () { return this; }, i;
    function verb(n, f) { i[n] = o[n] ? function (v) { return (p = !p) ? { value: __await(o[n](v)), done: n === "return" } : f ? f(v) : v; } : f; }
}

function __asyncValues(o) {
    if (!Symbol.asyncIterator) throw new TypeError("Symbol.asyncIterator is not defined.");
    var m = o[Symbol.asyncIterator], i;
    return m ? m.call(o) : (o = typeof __values === "function" ? __values(o) : o[Symbol.iterator](), i = {}, verb("next"), verb("throw"), verb("return"), i[Symbol.asyncIterator] = function () { return this; }, i);
    function verb(n) { i[n] = o[n] && function (v) { return new Promise(function (resolve, reject) { v = o[n](v), settle(resolve, reject, v.done, v.value); }); }; }
    function settle(resolve, reject, d, v) { Promise.resolve(v).then(function(v) { resolve({ value: v, done: d }); }, reject); }
}

function __makeTemplateObject(cooked, raw) {
    if (Object.defineProperty) { Object.defineProperty(cooked, "raw", { value: raw }); } else { cooked.raw = raw; }
    return cooked;
};

function __importStar(mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (Object.hasOwnProperty.call(mod, k)) result[k] = mod[k];
    result.default = mod;
    return result;
}

function __importDefault(mod) {
    return (mod && mod.__esModule) ? mod : { default: mod };
}


/***/ }),

/***/ "./src/app/app.component.css":
/*!***********************************!*\
  !*** ./src/app/app.component.css ***!
  \***********************************/
/*! exports provided: default */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony default export */ __webpack_exports__["default"] = ("\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IiIsImZpbGUiOiJzcmMvYXBwL2FwcC5jb21wb25lbnQuY3NzIn0= */");

/***/ }),

/***/ "./src/app/app.component.ts":
/*!**********************************!*\
  !*** ./src/app/app.component.ts ***!
  \**********************************/
/*! exports provided: AppComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AppComponent", function() { return AppComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var _app_service__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./app.service */ "./src/app/app.service.ts");



let AppComponent = class AppComponent {
    constructor(service) {
        this.service = service;
    }
};
AppComponent.ctorParameters = () => [
    { type: _app_service__WEBPACK_IMPORTED_MODULE_2__["AppService"] }
];
AppComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
        selector: 'app-root',
        template: tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(/*! raw-loader!./app.component.html */ "./node_modules/raw-loader/dist/cjs.js!./src/app/app.component.html")).default,
        providers: [_app_service__WEBPACK_IMPORTED_MODULE_2__["AppService"]],
        styles: [tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(/*! ./app.component.css */ "./src/app/app.component.css")).default]
    })
], AppComponent);



/***/ }),

/***/ "./src/app/app.module.ts":
/*!*******************************!*\
  !*** ./src/app/app.module.ts ***!
  \*******************************/
/*! exports provided: AppModule */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AppModule", function() { return AppModule; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_platform_browser__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/platform-browser */ "./node_modules/@angular/platform-browser/fesm2015/platform-browser.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm2015/router.js");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/forms */ "./node_modules/@angular/forms/fesm2015/forms.js");
/* harmony import */ var _angular_common_http__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! @angular/common/http */ "./node_modules/@angular/common/fesm2015/http.js");
/* harmony import */ var ngx_infinite_scroll__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ngx-infinite-scroll */ "./node_modules/ngx-infinite-scroll/modules/ngx-infinite-scroll.js");
/* harmony import */ var _app_component__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ./app.component */ "./src/app/app.component.ts");
/* harmony import */ var _docs_menu_menu_component__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ./docs/menu/menu.component */ "./src/app/docs/menu/menu.component.ts");
/* harmony import */ var _docs_docs_component__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ./docs/docs.component */ "./src/app/docs/docs.component.ts");
/* harmony import */ var _doc_menu_menu_component__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ./doc/menu/menu.component */ "./src/app/doc/menu/menu.component.ts");
/* harmony import */ var _doc_doc_component__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ./doc/doc.component */ "./src/app/doc/doc.component.ts");
/* harmony import */ var _doc_events_events_component__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! ./doc/events/events.component */ "./src/app/doc/events/events.component.ts");
/* harmony import */ var _youtube_youtube_module__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! ./youtube/youtube.module */ "./src/app/youtube/youtube.module.ts");














let AppModule = class AppModule {
};
AppModule = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_2__["NgModule"])({
        declarations: [
            _app_component__WEBPACK_IMPORTED_MODULE_7__["AppComponent"],
            _docs_menu_menu_component__WEBPACK_IMPORTED_MODULE_8__["DocsMenuComponent"],
            _docs_docs_component__WEBPACK_IMPORTED_MODULE_9__["DocsComponent"],
            _doc_menu_menu_component__WEBPACK_IMPORTED_MODULE_10__["DocMenuComponent"],
            _doc_doc_component__WEBPACK_IMPORTED_MODULE_11__["DocComponent"],
            _doc_events_events_component__WEBPACK_IMPORTED_MODULE_12__["EventsComponent"]
        ],
        imports: [
            _angular_platform_browser__WEBPACK_IMPORTED_MODULE_1__["BrowserModule"],
            _angular_router__WEBPACK_IMPORTED_MODULE_3__["RouterModule"].forRoot([
                { path: '', component: _docs_docs_component__WEBPACK_IMPORTED_MODULE_9__["DocsComponent"] },
                { path: 'docs', component: _docs_docs_component__WEBPACK_IMPORTED_MODULE_9__["DocsComponent"] },
                { path: 'docs/:docId', component: _doc_doc_component__WEBPACK_IMPORTED_MODULE_11__["DocComponent"] }
            ]),
            _angular_forms__WEBPACK_IMPORTED_MODULE_4__["FormsModule"],
            _angular_common_http__WEBPACK_IMPORTED_MODULE_5__["HttpClientModule"],
            ngx_infinite_scroll__WEBPACK_IMPORTED_MODULE_6__["InfiniteScrollModule"],
            _youtube_youtube_module__WEBPACK_IMPORTED_MODULE_13__["YoutubeModule"]
        ],
        providers: [],
        bootstrap: [_app_component__WEBPACK_IMPORTED_MODULE_7__["AppComponent"]]
    })
], AppModule);



/***/ }),

/***/ "./src/app/app.service.ts":
/*!********************************!*\
  !*** ./src/app/app.service.ts ***!
  \********************************/
/*! exports provided: AppService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AppService", function() { return AppService; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var _angular_common_http__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/common/http */ "./node_modules/@angular/common/fesm2015/http.js");



let AppService = class AppService {
    constructor(http) {
        this.http = http;
        this.error = null;
        this.searchText = '';
        this.onSearch = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
        this.audio = null;
        this.showEvents = false;
        this.events = [];
    }
    searchHandler(searchText) {
        this.searchText = searchText;
        this.onSearch.emit(searchText);
    }
    resetEvents() {
        this.showEvents = false;
        this.events.splice(0, this.events.length);
    }
    loadEvents(id) {
        this.showEvents = true;
        this.http.get('/api/artists/' + id + '/events').subscribe((data) => this.setEvents(data), error => this.error = error);
    }
    setEvents(events) {
        this.events.splice(0, this.events.length);
        if (typeof (events) != 'undefined' && events != null) {
            this.events.push.apply(this.events, events);
        }
    }
};
AppService.ctorParameters = () => [
    { type: _angular_common_http__WEBPACK_IMPORTED_MODULE_2__["HttpClient"] }
];
AppService = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Injectable"])({
        providedIn: 'root'
    })
], AppService);



/***/ }),

/***/ "./src/app/doc/doc.component.css":
/*!***************************************!*\
  !*** ./src/app/doc/doc.component.css ***!
  \***************************************/
/*! exports provided: default */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony default export */ __webpack_exports__["default"] = ("div.vote {\n  border-radius: 50%;\n  width: 30px;\n  height: 30px;\n  flex: 0 0 30px;\n  background: #8AC007;\n\n  color: #FFFFFF;\n  text-align: center;\n  padding-top: 5px;\n\n  position: absolute;\n  right: 10px;\n  top: 10px;\n  z-index: 20;\n}\n\ndiv.vote-link {\n  border-radius: 50%;\n  width: 30px;\n  height: 30px;\n  flex: 0 0 30px;\n  background: #8AC007;\n\n  color: #FFFFFF;\n  text-align: center;\n  padding-top: 5px;\n}\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInNyYy9hcHAvZG9jL2RvYy5jb21wb25lbnQuY3NzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUFBO0VBQ0Usa0JBQWtCO0VBQ2xCLFdBQVc7RUFDWCxZQUFZO0VBQ1osY0FBYztFQUNkLG1CQUFtQjs7RUFFbkIsY0FBYztFQUNkLGtCQUFrQjtFQUNsQixnQkFBZ0I7O0VBRWhCLGtCQUFrQjtFQUNsQixXQUFXO0VBQ1gsU0FBUztFQUNULFdBQVc7QUFDYjs7QUFFQTtFQUNFLGtCQUFrQjtFQUNsQixXQUFXO0VBQ1gsWUFBWTtFQUNaLGNBQWM7RUFDZCxtQkFBbUI7O0VBRW5CLGNBQWM7RUFDZCxrQkFBa0I7RUFDbEIsZ0JBQWdCO0FBQ2xCIiwiZmlsZSI6InNyYy9hcHAvZG9jL2RvYy5jb21wb25lbnQuY3NzIiwic291cmNlc0NvbnRlbnQiOlsiZGl2LnZvdGUge1xuICBib3JkZXItcmFkaXVzOiA1MCU7XG4gIHdpZHRoOiAzMHB4O1xuICBoZWlnaHQ6IDMwcHg7XG4gIGZsZXg6IDAgMCAzMHB4O1xuICBiYWNrZ3JvdW5kOiAjOEFDMDA3O1xuXG4gIGNvbG9yOiAjRkZGRkZGO1xuICB0ZXh0LWFsaWduOiBjZW50ZXI7XG4gIHBhZGRpbmctdG9wOiA1cHg7XG5cbiAgcG9zaXRpb246IGFic29sdXRlO1xuICByaWdodDogMTBweDtcbiAgdG9wOiAxMHB4O1xuICB6LWluZGV4OiAyMDtcbn1cblxuZGl2LnZvdGUtbGluayB7XG4gIGJvcmRlci1yYWRpdXM6IDUwJTtcbiAgd2lkdGg6IDMwcHg7XG4gIGhlaWdodDogMzBweDtcbiAgZmxleDogMCAwIDMwcHg7XG4gIGJhY2tncm91bmQ6ICM4QUMwMDc7XG5cbiAgY29sb3I6ICNGRkZGRkY7XG4gIHRleHQtYWxpZ246IGNlbnRlcjtcbiAgcGFkZGluZy10b3A6IDVweDtcbn1cbiJdfQ== */");

/***/ }),

/***/ "./src/app/doc/doc.component.ts":
/*!**************************************!*\
  !*** ./src/app/doc/doc.component.ts ***!
  \**************************************/
/*! exports provided: DocComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DocComponent", function() { return DocComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm2015/router.js");
/* harmony import */ var _angular_common_http__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/common/http */ "./node_modules/@angular/common/fesm2015/http.js");
/* harmony import */ var _angular_platform_browser__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/platform-browser */ "./node_modules/@angular/platform-browser/fesm2015/platform-browser.js");
/* harmony import */ var _app_service__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../app.service */ "./src/app/app.service.ts");






let DocComponent = class DocComponent {
    constructor(http, route, meta, service) {
        this.http = http;
        this.route = route;
        this.meta = meta;
        this.service = service;
        this.error = null;
        this.docId = null;
        this.doc = null;
        this.links = [];
    }
    ngOnInit() {
        this.route.paramMap.subscribe(params => {
            this.docId = params.get('docId');
            this.loadDoc(this.docId);
            this.loadLinks(this.docId);
        });
    }
    loadDoc(id) {
        this.service.resetEvents();
        this.http.get('/api/docs/' + id).subscribe((data) => this.setDoc(data[0]), error => this.error = error);
    }
    loadLinks(id) {
        this.links = [];
        this.http.get('/api/docs/' + id + '/links').subscribe((data) => this.setLinks(data), error => this.error = error);
    }
    setDoc(doc) {
        this.doc = doc;
        this.meta.updateTag({ name: 'og:site_name', content: 'Human Beats' });
        this.meta.updateTag({ name: 'og:title', content: this.doc.artist + ' - ' + this.doc.title + ' :: Human Beats' });
        this.meta.updateTag({ name: 'og:type', content: 'music.album' });
        //this.meta.updateTag({ name: 'og:type', content: 'music.playlist' });
        this.meta.updateTag({ name: 'og:image', content: this.doc.cover });
        this.meta.updateTag({ name: 'og:url', content: 'https://humanbeats.appspot.com/docs/' + this.doc.id });
        this.meta.updateTag({ name: 'og:locale', content: 'it_IT' });
        this.meta.updateTag({ name: 'og:description', content: this.doc.description });
        this.meta.updateTag({ name: 'music:musician', content: this.doc.artist });
        this.meta.updateTag({ name: 'music:release_date', content: this.doc.year + '-01-01' });
        this.meta.updateTag({ name: 'music:album', content: this.doc.title });
        //this.meta.updateTag({ name: 'music:creator', content: this.doc.title });
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
    setLinks(links) {
        this.links.splice(0, this.links.length);
        this.links.push.apply(this.links, links);
        var id = this.doc.id;
        this.links = this.links.filter(function (value, index, arr) {
            return value.id != id;
        });
    }
    playPause(event) {
        if (this.service.audio.paused) {
            this.service.audio.play();
        }
        else {
            this.service.audio.pause();
        }
    }
    forward(event) {
        if (!this.service.audio.paused) {
            this.service.audio.currentTime += 60.0;
        }
    }
    backward(event) {
        if (!this.service.audio.paused) {
            this.service.audio.currentTime -= 60.0;
        }
    }
};
DocComponent.ctorParameters = () => [
    { type: _angular_common_http__WEBPACK_IMPORTED_MODULE_3__["HttpClient"] },
    { type: _angular_router__WEBPACK_IMPORTED_MODULE_2__["ActivatedRoute"] },
    { type: _angular_platform_browser__WEBPACK_IMPORTED_MODULE_4__["Meta"] },
    { type: _app_service__WEBPACK_IMPORTED_MODULE_5__["AppService"] }
];
DocComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
        selector: 'app-doc',
        template: tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(/*! raw-loader!./doc.component.html */ "./node_modules/raw-loader/dist/cjs.js!./src/app/doc/doc.component.html")).default,
        styles: [tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(/*! ./doc.component.css */ "./src/app/doc/doc.component.css")).default]
    })
], DocComponent);



/***/ }),

/***/ "./src/app/doc/events/events.component.css":
/*!*************************************************!*\
  !*** ./src/app/doc/events/events.component.css ***!
  \*************************************************/
/*! exports provided: default */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony default export */ __webpack_exports__["default"] = ("\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IiIsImZpbGUiOiJzcmMvYXBwL2RvYy9ldmVudHMvZXZlbnRzLmNvbXBvbmVudC5jc3MifQ== */");

/***/ }),

/***/ "./src/app/doc/events/events.component.ts":
/*!************************************************!*\
  !*** ./src/app/doc/events/events.component.ts ***!
  \************************************************/
/*! exports provided: EventsComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "EventsComponent", function() { return EventsComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var _app_service__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../app.service */ "./src/app/app.service.ts");



let EventsComponent = class EventsComponent {
    constructor(service) {
        this.service = service;
        this.events = [];
    }
    ngOnInit() { }
    close() {
        this.service.showEvents = false;
    }
};
EventsComponent.ctorParameters = () => [
    { type: _app_service__WEBPACK_IMPORTED_MODULE_2__["AppService"] }
];
tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])()
], EventsComponent.prototype, "events", void 0);
EventsComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
        selector: 'app-events',
        template: tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(/*! raw-loader!./events.component.html */ "./node_modules/raw-loader/dist/cjs.js!./src/app/doc/events/events.component.html")).default,
        styles: [tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(/*! ./events.component.css */ "./src/app/doc/events/events.component.css")).default]
    })
], EventsComponent);



/***/ }),

/***/ "./src/app/doc/menu/menu.component.css":
/*!*********************************************!*\
  !*** ./src/app/doc/menu/menu.component.css ***!
  \*********************************************/
/*! exports provided: default */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony default export */ __webpack_exports__["default"] = ("img.cover {\n    width: 40px;\n    height: 40px;\n    -o-object-fit:cover;\n       object-fit:cover;\n}\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInNyYy9hcHAvZG9jL21lbnUvbWVudS5jb21wb25lbnQuY3NzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUFBO0lBQ0ksV0FBVztJQUNYLFlBQVk7SUFDWixtQkFBZ0I7T0FBaEIsZ0JBQWdCO0FBQ3BCIiwiZmlsZSI6InNyYy9hcHAvZG9jL21lbnUvbWVudS5jb21wb25lbnQuY3NzIiwic291cmNlc0NvbnRlbnQiOlsiaW1nLmNvdmVyIHtcbiAgICB3aWR0aDogNDBweDtcbiAgICBoZWlnaHQ6IDQwcHg7XG4gICAgb2JqZWN0LWZpdDpjb3Zlcjtcbn0iXX0= */");

/***/ }),

/***/ "./src/app/doc/menu/menu.component.ts":
/*!********************************************!*\
  !*** ./src/app/doc/menu/menu.component.ts ***!
  \********************************************/
/*! exports provided: DocMenuComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DocMenuComponent", function() { return DocMenuComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var _doc_component__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../doc.component */ "./src/app/doc/doc.component.ts");



let DocMenuComponent = class DocMenuComponent {
    constructor(component) {
        this.component = component;
    }
    ngOnInit() { }
};
DocMenuComponent.ctorParameters = () => [
    { type: _doc_component__WEBPACK_IMPORTED_MODULE_2__["DocComponent"] }
];
DocMenuComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
        selector: 'doc-menu',
        template: tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(/*! raw-loader!./menu.component.html */ "./node_modules/raw-loader/dist/cjs.js!./src/app/doc/menu/menu.component.html")).default,
        styles: [tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(/*! ./menu.component.css */ "./src/app/doc/menu/menu.component.css")).default]
    })
], DocMenuComponent);



/***/ }),

/***/ "./src/app/docs/docs.component.css":
/*!*****************************************!*\
  !*** ./src/app/docs/docs.component.css ***!
  \*****************************************/
/*! exports provided: default */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony default export */ __webpack_exports__["default"] = ("div.vote {\n  border-radius: 50%;\n  width: 30px;\n  height: 30px;\n  flex: 0 0 30px;\n  background: #8AC007;\n\n  text-align: center;\n  padding-top: 5px;\n\n  position: absolute;\n  right: 10px;\n  top: 10px;\n  z-index: 20;\n}\n\n.card {\n  border-style: none;\n}\n\n.card-text {\n  overflow: hidden;\n  display: -webkit-box;\n  -webkit-line-clamp: 3;\n  -webkit-box-orient: vertical;\n}\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInNyYy9hcHAvZG9jcy9kb2NzLmNvbXBvbmVudC5jc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUE7RUFDRSxrQkFBa0I7RUFDbEIsV0FBVztFQUNYLFlBQVk7RUFDWixjQUFjO0VBQ2QsbUJBQW1COztFQUVuQixrQkFBa0I7RUFDbEIsZ0JBQWdCOztFQUVoQixrQkFBa0I7RUFDbEIsV0FBVztFQUNYLFNBQVM7RUFDVCxXQUFXO0FBQ2I7O0FBRUE7RUFDRSxrQkFBa0I7QUFDcEI7O0FBRUE7RUFDRSxnQkFBZ0I7RUFDaEIsb0JBQW9CO0VBQ3BCLHFCQUFxQjtFQUNyQiw0QkFBNEI7QUFDOUIiLCJmaWxlIjoic3JjL2FwcC9kb2NzL2RvY3MuY29tcG9uZW50LmNzcyIsInNvdXJjZXNDb250ZW50IjpbImRpdi52b3RlIHtcbiAgYm9yZGVyLXJhZGl1czogNTAlO1xuICB3aWR0aDogMzBweDtcbiAgaGVpZ2h0OiAzMHB4O1xuICBmbGV4OiAwIDAgMzBweDtcbiAgYmFja2dyb3VuZDogIzhBQzAwNztcblxuICB0ZXh0LWFsaWduOiBjZW50ZXI7XG4gIHBhZGRpbmctdG9wOiA1cHg7XG5cbiAgcG9zaXRpb246IGFic29sdXRlO1xuICByaWdodDogMTBweDtcbiAgdG9wOiAxMHB4O1xuICB6LWluZGV4OiAyMDtcbn1cblxuLmNhcmQge1xuICBib3JkZXItc3R5bGU6IG5vbmU7XG59XG5cbi5jYXJkLXRleHQge1xuICBvdmVyZmxvdzogaGlkZGVuO1xuICBkaXNwbGF5OiAtd2Via2l0LWJveDtcbiAgLXdlYmtpdC1saW5lLWNsYW1wOiAzO1xuICAtd2Via2l0LWJveC1vcmllbnQ6IHZlcnRpY2FsO1xufSJdfQ== */");

/***/ }),

/***/ "./src/app/docs/docs.component.ts":
/*!****************************************!*\
  !*** ./src/app/docs/docs.component.ts ***!
  \****************************************/
/*! exports provided: DocsComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DocsComponent", function() { return DocsComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var _angular_common_http__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/common/http */ "./node_modules/@angular/common/fesm2015/http.js");
/* harmony import */ var _app_service__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../app.service */ "./src/app/app.service.ts");




let DocsComponent = class DocsComponent {
    constructor(http, service) {
        this.http = http;
        this.service = service;
        this.error = null;
        this.page = 0;
        this.docs = [];
        this.tracks = [];
    }
    ngOnInit() {
        this.service.onSearch.subscribe((searchText) => this.onSearch(searchText));
        this.loadAlbums(0);
        this.loadTracks(0);
    }
    onSearch(searchText) {
        this.loadAlbums(0);
    }
    loadAlbumsScroll() {
        this.page++;
        this.loadAlbums(this.page);
        this.loadTracks(this.page);
    }
    loadAlbums(page) {
        this.page = page;
        if (this.page == 0) {
            this.docs.splice(0, this.docs.length);
        }
        this.http.get('/api/docs?p=' + this.page + '&q=' + this.service.searchText).subscribe((data) => this.albumsLoaded(data), error => this.error = error);
    }
    albumsLoaded(data) {
        this.docs.push.apply(this.docs, data);
    }
    loadTracks(page) {
        this.page = page;
        if (this.page == 0) {
            this.tracks.splice(0, this.tracks.length);
        }
        this.http.get('/api/tracks?p=' + this.page).subscribe((data) => this.tracksLoaded(data), error => this.error = error);
    }
    tracksLoaded(data) {
        this.tracks.push.apply(this.tracks, data);
    }
};
DocsComponent.ctorParameters = () => [
    { type: _angular_common_http__WEBPACK_IMPORTED_MODULE_2__["HttpClient"] },
    { type: _app_service__WEBPACK_IMPORTED_MODULE_3__["AppService"] }
];
DocsComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
        selector: 'app-docs',
        template: tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(/*! raw-loader!./docs.component.html */ "./node_modules/raw-loader/dist/cjs.js!./src/app/docs/docs.component.html")).default,
        styles: [tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(/*! ./docs.component.css */ "./src/app/docs/docs.component.css")).default]
    })
], DocsComponent);



/***/ }),

/***/ "./src/app/docs/menu/menu.component.css":
/*!**********************************************!*\
  !*** ./src/app/docs/menu/menu.component.css ***!
  \**********************************************/
/*! exports provided: default */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony default export */ __webpack_exports__["default"] = ("\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IiIsImZpbGUiOiJzcmMvYXBwL2RvY3MvbWVudS9tZW51LmNvbXBvbmVudC5jc3MifQ== */");

/***/ }),

/***/ "./src/app/docs/menu/menu.component.ts":
/*!*********************************************!*\
  !*** ./src/app/docs/menu/menu.component.ts ***!
  \*********************************************/
/*! exports provided: DocsMenuComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DocsMenuComponent", function() { return DocsMenuComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var _app_service__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../app.service */ "./src/app/app.service.ts");



let DocsMenuComponent = class DocsMenuComponent {
    constructor(service) {
        this.service = service;
        this.searchText = '';
    }
    ngOnInit() { }
    onSearch() {
        this.service.searchHandler(this.searchText);
    }
};
DocsMenuComponent.ctorParameters = () => [
    { type: _app_service__WEBPACK_IMPORTED_MODULE_2__["AppService"] }
];
DocsMenuComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
        selector: 'docs-menu',
        template: tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(/*! raw-loader!./menu.component.html */ "./node_modules/raw-loader/dist/cjs.js!./src/app/docs/menu/menu.component.html")).default,
        styles: [tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(/*! ./menu.component.css */ "./src/app/docs/menu/menu.component.css")).default]
    })
], DocsMenuComponent);



/***/ }),

/***/ "./src/app/youtube/youtube.component.css":
/*!***********************************************!*\
  !*** ./src/app/youtube/youtube.component.css ***!
  \***********************************************/
/*! exports provided: default */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony default export */ __webpack_exports__["default"] = ("div.track {\n    width: 320px;\n}\n\nimg.track {\n    width: 320px;\n    height: 180px;\n    -o-object-fit:cover;\n       object-fit:cover;\n}\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInNyYy9hcHAveW91dHViZS95b3V0dWJlLmNvbXBvbmVudC5jc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUE7SUFDSSxZQUFZO0FBQ2hCOztBQUVBO0lBQ0ksWUFBWTtJQUNaLGFBQWE7SUFDYixtQkFBZ0I7T0FBaEIsZ0JBQWdCO0FBQ3BCIiwiZmlsZSI6InNyYy9hcHAveW91dHViZS95b3V0dWJlLmNvbXBvbmVudC5jc3MiLCJzb3VyY2VzQ29udGVudCI6WyJkaXYudHJhY2sge1xuICAgIHdpZHRoOiAzMjBweDtcbn1cblxuaW1nLnRyYWNrIHtcbiAgICB3aWR0aDogMzIwcHg7XG4gICAgaGVpZ2h0OiAxODBweDtcbiAgICBvYmplY3QtZml0OmNvdmVyO1xufVxuIl19 */");

/***/ }),

/***/ "./src/app/youtube/youtube.component.ts":
/*!**********************************************!*\
  !*** ./src/app/youtube/youtube.component.ts ***!
  \**********************************************/
/*! exports provided: YoutubeComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "YoutubeComponent", function() { return YoutubeComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var _app_service__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../app.service */ "./src/app/app.service.ts");



let YoutubeComponent = class YoutubeComponent {
    constructor(service) {
        this.service = service;
        this.player = null;
        this.status = null;
        this.video = null;
        this.videos = [];
        this._tracks = [];
    }
    get tracks() {
        return this._tracks;
    }
    set tracks(val) {
        this._tracks = val;
        this.videos = [];
        if (this.tracks) {
            this.tracks.forEach(function (track) {
                if (track.youtube) {
                    this.videos.push(track);
                }
            }, this);
        }
        this.video = this.videos.length > 0 ? this.videos[0] : null;
    }
    ngOnInit() {
        const tag = document.createElement('script');
        tag.src = 'https://www.youtube.com/iframe_api';
        document.body.appendChild(tag);
    }
    onReady(event) {
        this.player = event.target;
        this.player.playVideo();
    }
    onStateChange(event) {
        this.status = event.data;
        switch (this.status) {
            case 0:
                console.log('video ended');
                this.next(null);
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
    loadVideo(video) {
        this.video = video;
    }
    play(event) {
        this.player.playVideo();
    }
    pause(event) {
        this.player.pauseVideo();
    }
    next(event) {
        if (this.videos.indexOf(this.video) < this.videos.length - 1) {
            this.video = this.videos[this.videos.indexOf(this.video) + 1];
        }
    }
    previous(event) {
        if (this.videos.indexOf(this.video) > 0) {
            this.video = this.videos[this.videos.indexOf(this.video) - 1];
        }
    }
};
YoutubeComponent.ctorParameters = () => [
    { type: _app_service__WEBPACK_IMPORTED_MODULE_2__["AppService"] }
];
tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])()
], YoutubeComponent.prototype, "tracks", null);
YoutubeComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
        selector: 'app-youtube',
        template: tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(/*! raw-loader!./youtube.component.html */ "./node_modules/raw-loader/dist/cjs.js!./src/app/youtube/youtube.component.html")).default,
        styles: [tslib__WEBPACK_IMPORTED_MODULE_0__["__importDefault"](__webpack_require__(/*! ./youtube.component.css */ "./src/app/youtube/youtube.component.css")).default]
    })
], YoutubeComponent);



/***/ }),

/***/ "./src/app/youtube/youtube.module.ts":
/*!*******************************************!*\
  !*** ./src/app/youtube/youtube.module.ts ***!
  \*******************************************/
/*! exports provided: YoutubeModule */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "YoutubeModule", function() { return YoutubeModule; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/common */ "./node_modules/@angular/common/fesm2015/common.js");
/* harmony import */ var _angular_youtube_player__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/youtube-player */ "./node_modules/@angular/youtube-player/esm2015/youtube-player.js");
/* harmony import */ var _youtube_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./youtube.component */ "./src/app/youtube/youtube.component.ts");





let YoutubeModule = class YoutubeModule {
};
YoutubeModule = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["NgModule"])({
        imports: [_angular_youtube_player__WEBPACK_IMPORTED_MODULE_3__["YouTubePlayerModule"], _angular_common__WEBPACK_IMPORTED_MODULE_2__["CommonModule"]],
        declarations: [_youtube_component__WEBPACK_IMPORTED_MODULE_4__["YoutubeComponent"]],
        exports: [_youtube_component__WEBPACK_IMPORTED_MODULE_4__["YoutubeComponent"]]
    })
], YoutubeModule);



/***/ }),

/***/ "./src/environments/environment.ts":
/*!*****************************************!*\
  !*** ./src/environments/environment.ts ***!
  \*****************************************/
/*! exports provided: environment */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "environment", function() { return environment; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

const environment = {
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


/***/ }),

/***/ "./src/main.ts":
/*!*********************!*\
  !*** ./src/main.ts ***!
  \*********************/
/*! no exports provided */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var _angular_platform_browser_dynamic__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/platform-browser-dynamic */ "./node_modules/@angular/platform-browser-dynamic/fesm2015/platform-browser-dynamic.js");
/* harmony import */ var _app_app_module__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./app/app.module */ "./src/app/app.module.ts");
/* harmony import */ var _environments_environment__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./environments/environment */ "./src/environments/environment.ts");





if (_environments_environment__WEBPACK_IMPORTED_MODULE_4__["environment"].production) {
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["enableProdMode"])();
}
Object(_angular_platform_browser_dynamic__WEBPACK_IMPORTED_MODULE_2__["platformBrowserDynamic"])().bootstrapModule(_app_app_module__WEBPACK_IMPORTED_MODULE_3__["AppModule"])
    .catch(err => console.error(err));


/***/ }),

/***/ 0:
/*!***************************!*\
  !*** multi ./src/main.ts ***!
  \***************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

module.exports = __webpack_require__(/*! /Users/riccia/workspace/phonoteke/web/src/main.ts */"./src/main.ts");


/***/ })

},[[0,"runtime","vendor"]]]);