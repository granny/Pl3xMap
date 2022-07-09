let P = {
    map: null,
    renderer: `basic`,
    format: `png`,
    world: `world`,
    options: {
        defZoom: 0,
        maxZoom: 3,
        extraZoomIn: 2,
        link: false,
        coords: false
    },
    lang: {
        coords: `Coordinates<br/><x>, <z>`,
        players: `Players (<online>/<max>)`,
        worlds: `Worlds`
    },
    toLatLng: function (x, z) {
        return L.latLng(P.pixelsToMeters(z), P.pixelsToMeters(x));
    },
    toPoint: function (latlng) {
        return L.point(P.metersToPixels(latlng.lng), P.metersToPixels(-latlng.lat));
    },
    pixelsToMeters: function (num) {
        return num * P.scale();
    },
    metersToPixels(num) {
        return num / P.scale();
    },
    scale: function () {
        return 1 / Math.pow(2, P.options.maxZoom)
    },
    centerOn(x, z, zoom) {
        P.map.setView(P.toLatLng(x, z), P.options.maxZoom - zoom);
        return P.map;
    },
    getUrlParam(query, def) {
        const url = window.location.search.substring(1);
        const vars = url.split('&');
        for (let i = 0; i < vars.length; i++) {
            const param = vars[i].split('=');
            if (param[0] === query) {
                const value = param[1] === undefined ? '' : decodeURIComponent(param[1]);
                return value === '' ? def : value;
            }
        }
        return def;
    },
    getUrlFromView() {
        const center = P.toPoint(P.map.getCenter());
        const zoom = P.options.maxZoom - P.map.getZoom();
        const x = Math.floor(center.x);
        const z = Math.floor(-center.y);
        return `?world=${P.world}&zoom=${zoom}&x=${x}&z=${z}`;
    },
    getJSON(url, fn) {
        fetch(url, {cache: "no-store"})
            .then(async res => {
                if (res.ok) {
                    fn(await res.json());
                }
            });
    }
}

window.onload = function () {
    P.map = L.map(`map`, {
        // simple crs for custom map tiles
        crs: L.Util.extend(L.CRS.Simple, {
            // we need to flip the y-axis correctly
            // https://stackoverflow.com/a/62320569/3530727
            transformation: new L.Transformation(1, 0, 1, 0)
        }),
        // always 0,0 center
        center: [0, 0],
        // hides the leaflet attribution footer
        attributionControl: false,
        // canvas is faster than default svg
        preferCanvas: true
    });

    P.getJSON("tiles/settings.json", (json) => init(json));
}

/**
 * @param json.ui
 * @param json.worlds
 */
function init(json) {
    document.title = json.ui.lang.title;

    P.lang.coords = json.ui.lang.coords;
    P.lang.players = json.ui.lang.players;
    P.lang.worlds = json.ui.lang.worlds;

    P.options.link = json.ui.link;
    P.options.coords = json.ui.coords;

    // get world from url, or first world from json
    P.world = P.getUrlParam("world", json.worlds[0].name)

    // center map on coords at zoom from url, or from json
    P.getJSON(`tiles/${P.world}/settings.json`,
        /**
         * @param world
         * @param world.spawn
         * @param world.zoom
         */
        (world) => {
            P.centerOn(
                P.getUrlParam("x", world.spawn.x),
                P.getUrlParam("z", world.spawn.z),
                P.getUrlParam("zoom", world.zoom.default)
            );
        });

    // the base layer for tiles
    L.tileLayer.reversedZoom(`tiles/${P.world}/{z}/${P.renderer}/{x}_{y}.${P.format}`).setZIndex(0).addTo(P.map);

    // player tracker layer
    let players = new L.layerGroup();
    players.setZIndex(100);
    players.addTo(P.map);

    // set up layer controls
    let layerControls = L.control.layers({}, {}, {position: `topleft`});
    layerControls.addOverlay(players, `Players`);
    layerControls.addTo(P.map);

    // add the coords ui control box
    if (P.options.coords) {
        L.control.coords().addTo(P.map);
    }

    // add the link ui control box
    if (P.options.link) {
        L.control.link().addTo(P.map);
    }
}

L.TileLayer.ReversedZoom = L.TileLayer.extend({
    // <https://github.com/Leaflet/Leaflet/blob/main/src/layer/tile/TileLayer.js#L220-L231>
    _getZoomForUrl: function () {
        // fix zoom to work how we intuitively expect it to
        return (this.options.maxZoom - this._tileZoom) + this.options.zoomOffset;
    }
});

L.tileLayer.reversedZoom = function (url) {
    return new L.TileLayer.ReversedZoom(url, {
        // tile sizes match regions sizes (512 blocks x 512 blocks)
        tileSize: 512,
        // dont wrap tiles at edges
        noWrap: true,
        // the closest zoomed in possible (without stretching)
        // this is always 0. no exceptions!
        minNativeZoom: 0,
        // the farthest possible out possible
        maxNativeZoom: P.options.maxZoom,
        // for extra zoom in, make higher than maxNativeZoom
        // this is the stretched tiles to zoom in further
        maxZoom: P.options.maxZoom + P.options.extraZoomIn,
        // we need to counter effect the higher maxZoom here
        // maxZoom + zoomOffset = maxNativeZoom
        zoomOffset: -P.options.extraZoomIn
    });
}

L.Control.Coords = L.Control.extend({
    _container: null,
    options: {
        position: `bottomleft`
    },
    onAdd: function () {
        this._coords = L.DomUtil.create(`div`, `leaflet-control-layers coordinates`);
        P.map.addEventListener(`mousemove`, (event) => this.update(P.toPoint(event.latlng)));
        this.update(null);
        return this._coords;
    },
    update: function (point) {
        this.x = point == null ? `---` : Math.round(point.x);
        this.z = point == null ? `---` : Math.round(point.y);
        this._coords.innerHTML = P.lang.coords
            .replace(/<x>/g, this.x)
            .replace(/<z>/g, this.z);
    }
});

L.control.coords = function () {
    return new L.Control.Coords();
};

L.Control.Link = L.Control.extend({
    _container: null,
    options: {
        position: `bottomleft`
    },
    onAdd: function () {
        this._link = L.DomUtil.create(`div`, `leaflet-control-layers link`);
        P.map.addEventListener(`move`, () => this.update());
        P.map.addEventListener(`zoom`, () => this.update());
        this.update();
        return this._link;
    },
    update: function () {
        const url = P.world == null ? `` : P.getUrlFromView();
        //P.updateBrowserUrl(url); // this spams browser history
        this._link.innerHTML = `<a href='${url}'><img src='images/clear.png' alt=''/></a>`;
    }
});

L.control.link = function () {
    return new L.Control.Link();
};
