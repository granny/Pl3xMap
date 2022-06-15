let map;
let world = `world`; // temp

window.onload = function () {
    map = L.map(`map`, {
        // simple crs for custom map tiles
        crs: L.Util.extend(L.CRS.Simple, {
            // we need to flip the y-axis correctly
            // https://stackoverflow.com/a/62320569/3530727
            transformation: new L.Transformation(1,0,1,0)
        }),
        // always 0,0 center
        center: [0, 0],
        // hides the leaflet footer
        attributionControl: false,
        // canvas is faster than default svg
        preferCanvas: true
    });

    // start at spawn point with default zoom
    map.setView([0, 0], 0);

    // {z} is zoom level
    // {x}{y} is tile/region coordinates
    L.tileLayer(`tiles/${world}/{z}/{x}_{y}.png`, {
        // tile sizes match regions sizes (512 blocks x 512 blocks)
        tileSize: 512,
        // tiles farthest zoomed out (non-stretched tiles)
        // always 0. the farthest possible (negatives don't work)
        minNativeZoom: 0,
        // tiles farthest zoomed in (non-stretched tiles)
        // native size (1 pixel = 1 block)
        maxNativeZoom: 0,
        // if higher than maxNativeZoom then extra zoom in
        // is possible with stretched tiles
        maxZoom: 2,
        // dont wrap tiles at edges
        noWrap: true
    }).addTo(map);
};
