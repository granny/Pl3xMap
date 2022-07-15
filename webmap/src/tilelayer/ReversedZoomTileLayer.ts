import {TileLayer} from "leaflet";
import {World} from "../module/World";

export class ReversedZoomTileLayer extends TileLayer {
    declare private _url: string;
    private readonly _world: World;
    private _renderer: string;

    constructor(world: World, renderer: string) {
        super('', {
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

        this._world = world;
        this._renderer = renderer;
        this._url = this.determineUrl();
    }

    private determineUrl() {
        return `tiles/${this._world.name}/{z}/${this._renderer}/{x}_{y}.${this._world.format}`
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

    get renderer(): string {
        return this._renderer;
    }

    set renderer(renderer: string) {
        this._renderer = renderer;
        this._url = this.determineUrl();
        this.redraw();
    }
}
