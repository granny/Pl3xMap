let P = {
    map: null,
    renderer: `basic`,
    format: `png`,
    world: `world`,
    options: {
        zoom: 3, // current zoom
        maxZoom: 3,
        extraZoomIn: 2,
    },
    scale: function () {
        return 1 / Math.pow(2, this.options.zoom)
    },
    toLatLng: function (x, z) {
        return L.latLng(this.pixelsToMeters(z), this.pixelsToMeters(x));
    },
    pixelsToMeters: function (num) {
        return num * this.scale();
    },
    metersToPixels(num) {
        return num / this.scale();
    },
    toPoint: function (latlng) {
        return L.point(this.metersToPixels(latlng.lng), this.metersToPixels(-latlng.lat));
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

    // start at spawn point with default zoom
    P.map.setView(P.toLatLng(0, 0), P.options.zoom);
    //map.setView(toLatLng(18880, 6321), zoom); // earth world spawn

    // the base layer for tiles
    L.tileLayer.reversedZoom(`tiles/${P.world}/{z}/${P.renderer}/{x}_{y}.${P.format}`).setZIndex(0).addTo(P.map);

    // player tracker layer
    let players = new L.layerGroup().setZIndex(100);
    players.addTo(P.map);

    // set up layer controls
    let layerControls = L.control.layers({}, {}, {position: `topleft`});
    layerControls.addOverlay(players, `Players`);
    layerControls.addTo(P.map);

    L.control.coords().addTo(P.map);
};

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
        position: `bottomleft`,
        html: `Coordinates<br/>{x}, {z}`
    },
    onAdd: function () {
        this._coords = L.DomUtil.create(`div`, `leaflet-control-layers coordinates`);
        P.map.addEventListener(`mousemove`, (event) => {
            this.update(P.toPoint(event.latlng));
        });
        this.update(null);
        return this._coords;
    },
    update: function (point) {
        this.x = point == null ? `---` : Math.round(point.x);
        this.z = point == null ? `---` : Math.round(point.y);
        this._coords.innerHTML = this.options.html
            .replace(/{x}/g, this.x)
            .replace(/{z}/g, this.z);
    }
});

L.control.coords = function () {
    return new L.Control.Coords();
};
