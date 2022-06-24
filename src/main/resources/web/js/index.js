let map;
let world = `world`; // temp
let format = "png";
let zoom = 3; // current zoom
let maxZoom = 3;
let extraZoomIn = 2;
let scale = (1 / Math.pow(2, zoom));

window.onload = function () {
    map = L.map(`map`, {
        // simple crs for custom map tiles
        crs: L.Util.extend(L.CRS.Simple, {
            // we need to flip the y-axis correctly
            // https://stackoverflow.com/a/62320569/3530727
            transformation: new L.Transformation(1, 0, 1, 0)
        }),
        // always 0,0 center
        center: [0, 0],
        // hides the leaflet footer
        attributionControl: false,
        // canvas is faster than default svg
        preferCanvas: true
    });

    // start at spawn point with default zoom
    map.setView(toLatLng(0, 0), zoom);
    //map.setView(toLatLng(18880, 6321), zoom); // earth world spawn

    // set up layer controls
    let layerControls = L.control.layers({}, {}, {position: 'topleft'});

    // add layer controls to map
    layerControls.addTo(map);

    // create layers
    let blocks = L.tileLayer.reversedZoom(`tiles/${world}/{z}/blocks/{x}_{y}.${format}`).setZIndex(0);
    let biomes = L.tileLayer.reversedZoom(`tiles/${world}/{z}/biomes/{x}_{y}.${format}`).setZIndex(0);
    let heights = L.tileLayer.reversedZoom(`tiles/${world}/{z}/heights/{x}_{y}.${format}`).setZIndex(1);
    let fluids = L.tileLayer.reversedZoom(`tiles/${world}/{z}/fluids/{x}_{y}.${format}`).setZIndex(2);
    let players = new L.layerGroup().setZIndex(100);

    // add layers
    layerControls.addBaseLayer(blocks, "Blocks");
    layerControls.addBaseLayer(biomes, "Biomes");
    layerControls.addOverlay(heights, "Heightmap");
    layerControls.addOverlay(fluids, "Fluids");
    layerControls.addOverlay(players, "Players");

    // select default layers to show
    blocks.addTo(map);
    heights.addTo(map);
    fluids.addTo(map);
    players.addTo(map);
};

function toLatLng(x, z) {
    return L.latLng(pixelsToMeters(z), pixelsToMeters(x));
}

function pixelsToMeters(num) {
    return num * scale;
}

L.TileLayer.ReversedZoom = L.TileLayer.extend({
    // copied from leaflet source for modification <https://github.com/Leaflet/Leaflet/blob/main/src/layer/tile/TileLayer.js#L220-L231>
    _getZoomForUrl: function () {
        let zoom = this._tileZoom,
            maxZoom = this.options.maxZoom,
            //zoomReverse = this.options.zoomReverse,
            zoomOffset = this.options.zoomOffset;
        // always reverse the zoom here, but not in the options
        // reversing zoom in the options makes other areas of
        // the map screw up from what we want/expect.
        //if (zoomReverse) {
            zoom = maxZoom - zoom;
        //}
        return zoom + zoomOffset;
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
        maxNativeZoom: maxZoom,
        // for extra zoom in, make higher than maxNativeZoom
        // this is the stretched tiles to zoom in further
        maxZoom: maxZoom + extraZoomIn,
        // we need to counter effect the higher maxZoom here
        // maxZoom + zoomOffset = maxNativeZoom
        zoomOffset: -extraZoomIn
    });
}
