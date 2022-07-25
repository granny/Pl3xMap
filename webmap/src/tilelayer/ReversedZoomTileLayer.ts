import {TileLayer} from "leaflet";
import {World} from "../module/World";
import {getJSON} from "../Util";
import {BlockInfo} from "../types/Json";

export class ReversedZoomTileLayer extends TileLayer {
    declare private _url: string;
    private readonly _world: World;
    private readonly _renderer: string;

    // temporary storage for tile block info
    private blockInfos: Map<string, BlockInfo> = new Map();

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
            maxNativeZoom: world!.zoom.maxOut,
            // for extra zoom in, make higher than maxNativeZoom
            // this is the stretched tiles to zoom in further
            maxZoom: world!.zoom.maxOut + world!.zoom.maxIn,
            // we need to counter effect the higher maxZoom here
            // maxZoom + zoomOffset = maxNativeZoom
            zoomOffset: -world!.zoom.maxIn
        });

        this._world = world!;
        this._renderer = renderer;
        this._url = this.determineUrl();

        this.addEventListener("tileload", (event) => {
            const x: number = event.coords.x;
            const z: number = event.coords.y;
            getJSON(`tiles/${this._world.name}/${this._getZoomForUrl()}/blockinfo/${x}_${z}.gz`).then((json: BlockInfo) => {
                this.blockInfos.set(`${x}_${z}`, json);
            });
        });
        this.addEventListener("tileunload", (event) => {
            const x: number = event.coords.x;
            const z: number = event.coords.y;
            this.blockInfos.delete(`${x}_${z}`);
        });
        this.addEventListener("zoomend", (event) => {
            this.blockInfos.clear();
        })

        this.setZIndex(0);
    }

    getBlockInfo(x: number, z: number): BlockInfo | undefined {
        return this.blockInfos.get(`${x}_${z}`);
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
}
