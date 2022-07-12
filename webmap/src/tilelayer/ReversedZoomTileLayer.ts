import {TileLayer} from "leaflet";
import {Pl3xMap} from "../Pl3xMap";

export class ReversedZoomTileLayer extends TileLayer {
    private pl3xmap: Pl3xMap;

    constructor(pl3xmap: Pl3xMap) {
        super(`tiles/${pl3xmap.world?.name}/{z}/${pl3xmap.world?.renderer}/{x}_{y}.${pl3xmap.options.format}`, {
            // tile sizes match regions sizes (512 blocks x 512 blocks)
            tileSize: 512,
            // dont wrap tiles at edges
            noWrap: true,
            // the closest zoomed in possible (without stretching)
            // this is always 0. no exceptions!
            minNativeZoom: 0,
            // the farthest possible out possible
            maxNativeZoom: pl3xmap.options.zoom.maxZoom,
            // for extra zoom in, make higher than maxNativeZoom
            // this is the stretched tiles to zoom in further
            maxZoom: pl3xmap.options.zoom.maxZoom + pl3xmap.options.zoom.extraZoomIn,
            // we need to counter effect the higher maxZoom here
            // maxZoom + zoomOffset = maxNativeZoom
            zoomOffset: -pl3xmap.options.zoom.extraZoomIn
        });
        this.pl3xmap = pl3xmap;
    }

    _getZoomForUrl(): number {
        let zoom = this._tileZoom!,
            maxZoom = this.options.maxZoom!,
            offset = this.options.zoomOffset!;
        return (maxZoom - zoom) + offset;
    }

    static create(pl3xmap: Pl3xMap): ReversedZoomTileLayer {
        return new ReversedZoomTileLayer(pl3xmap);
    }
}
