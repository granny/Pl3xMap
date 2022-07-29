import {TileLayer} from "leaflet";
import {BlockInfo} from "../module/BlockInfo";
import {World} from "../module/World";

export class ReversedZoomTileLayer extends TileLayer {
    declare private _url: string;
    private readonly _world: World;
    private readonly _renderer: string;

    // temporary storage for tile block info
    private _blockInfos: Map<number, Map<string, BlockInfo>> = new Map();

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
            const zoom: number = this.world.pl3xmap.map.getMaxZoomOut() - event.coords.z;
            const x: number = event.coords.x;
            const z: number = event.coords.y;
            this._world.loadBlockInfo(zoom, x, z);
        });
        this.addEventListener("tileunload", (event) => {
            const zoom: number = event.coords.z;
            const x: number = event.coords.x;
            const z: number = event.coords.y;
            this._blockInfos.get(zoom)?.delete(`${x}_${z}`);
        });

        this.setZIndex(0);
    }

    getBlockInfo(zoom: number, x: number, z: number): BlockInfo | undefined {
        return this._blockInfos.get(zoom < 0 ? 0 : zoom)?.get(`${x}_${z}`);
    }

    setBlockInfo(zoom: number, x: number, z: number, blockInfo: BlockInfo | null): void {
        let infoMap = this._blockInfos.get(zoom < 0 ? 0 : zoom);
        if (infoMap == undefined) {
            infoMap = new Map();
            this._blockInfos.set(zoom, infoMap);
        }
        if (blockInfo == null) {
            infoMap.delete(`${x}_${z}`);
        } else {
            infoMap.set(`${x}_${z}`, blockInfo);
        }
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
