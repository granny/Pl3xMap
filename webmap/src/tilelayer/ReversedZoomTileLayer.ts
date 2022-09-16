import * as L from "leaflet";
import {Pl3xMap} from "../Pl3xMap";
import {Label} from "../settings/Lang";
import {World} from "../world/World";

export class ReversedZoomTileLayer extends L.TileLayer {
    private readonly _world: World;
    private readonly _renderer: Label;

    constructor(pl3xmap: Pl3xMap, world: World, renderer: Label) {
        super(`tiles/${world.name}/{z}/${renderer.label}/{x}_{y}.${Pl3xMap.instance.settings?.format}`, {
            // tile sizes match regions sizes (512 blocks x 512 blocks)
            tileSize: 512,
            // dont wrap tiles at edges
            noWrap: true,
            // the closest zoomed in possible (without stretching)
            // this is always 0. no exceptions!
            minNativeZoom: 0,
            // the farthest possible zoom out possible
            maxNativeZoom: world.zoom.maxOut,
            // for extra zoom in, make higher than maxNativeZoom
            // this is the stretched tiles to zoom in further
            maxZoom: world.zoom.maxOut + world.zoom.maxIn,
            // we need to counter effect the higher maxZoom here
            // maxZoom + zoomOffset = maxNativeZoom
            zoomOffset: -world.zoom.maxIn
        });

        this._world = world!;
        this._renderer = renderer;

        // when tiles load we need to load extra block info
        this.addEventListener("tileload", (event) => {
            const zoom: number = world.settings.zoom.maxOut - event.coords.z;
            world.loadBlockInfo(zoom, event.coords.x, event.coords.y);
        });

        // when tiles unload we need to remove the extra block info from memory
        this.addEventListener("tileunload", (event) => {
            const zoom: number = world.settings.zoom.maxOut - event.coords.z;
            world.unsetBlockInfo(zoom, event.coords.x, event.coords.y);
        });

        // push this layer to the back (leaflet defaults it to 1)
        this.setZIndex(0);
    }

    _getZoomForUrl(): number {
        const zoom = this._tileZoom!,
            maxZoom = this.options.maxZoom!,
            offset = this.options.zoomOffset!;
        return (maxZoom - zoom) + offset;
    }

    get world(): World {
        return this._world;
    }

    get renderer(): Label {
        return this._renderer;
    }
}
