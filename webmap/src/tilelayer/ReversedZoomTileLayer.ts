import {TileLayer} from "leaflet";
import {World} from "../module/World";

export class ReversedZoomTileLayer extends TileLayer {
    constructor(world: World) {
        super(`tiles/${world.name}/{z}/${world.renderer}/{x}_{y}.${world.format}`, {
            // tile sizes match regions sizes (512 blocks x 512 blocks)
            tileSize: 512,
            // dont wrap tiles at edges
            noWrap: true,
            // the closest zoomed in possible (without stretching)
            // this is always 0. no exceptions!
            minNativeZoom: 0,
            // the farthest possible out possible
            maxNativeZoom: world.zoom.maxOut,
            // for extra zoom in, make higher than maxNativeZoom
            // this is the stretched tiles to zoom in further
            maxZoom: world.zoom.maxOut + world.zoom.maxIn,
            // we need to counter effect the higher maxZoom here
            // maxZoom + zoomOffset = maxNativeZoom
            zoomOffset: -world.zoom.maxIn
        });
    }

    _getZoomForUrl(): number {
        const zoom = this._tileZoom!,
            maxZoom = this.options.maxZoom!,
            offset = this.options.zoomOffset!;
        return (maxZoom - zoom) + offset;
    }
}
