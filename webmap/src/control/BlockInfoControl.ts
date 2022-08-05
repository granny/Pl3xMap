import {Control, DomUtil} from "leaflet";
import {Pl3xMap} from "../Pl3xMap";
import {Util} from "../Util";
import {Block, BlockInfo} from "../module/BlockInfo";
import {Palette} from "../types/Json";
import Pl3xmapLeafletMap from "../map/Pl3xmapLeafletMap";

export class BlockInfoControl extends Control {
    private _pl3xmap: Pl3xMap;
    private _dom: HTMLDivElement = DomUtil.create('div');
    private _block: string = '';
    private _biome: string = '';

    private _blockPalette: Map<number, string> = new Map();

    constructor(pl3xmap: Pl3xMap) {
        super();
        this._pl3xmap = pl3xmap;
        super.options = {
            position: 'bottomleft'
        }

        Util.getJSON('tiles/blocks.gz').then((json: Palette[]) => {
            Object.entries(json).forEach((data, index) => {
                this._blockPalette.set(index, String(json[index]));
            });
        });
    }

    onAdd(map: Pl3xmapLeafletMap): HTMLDivElement {
        this._dom = DomUtil.create('div', 'leaflet-control leaflet-control-panel leaflet-control-blockinfo');
        this._dom.dataset.label = this._pl3xmap.lang.blockInfoLabel;
        this.update(map);
        return this._dom;
    }

    public update(map: Pl3xmapLeafletMap): void {
        const zoom: number = map.getCurrentZoom() < 0 ? 0 : map.getCurrentZoom();
        const x: number = this._pl3xmap.coordsControl?.getX() ?? 0;
        const z: number = this._pl3xmap.coordsControl?.getZ() ?? 0;
        const step: number = 1 << zoom;
        const regionX: number = x >> 9;
        const regionZ: number = z >> 9;
        const fileX: number = Math.floor(regionX / step);
        const fileZ: number = Math.floor(regionZ / step);
        const tileX: number = (x / step) & 511;
        const tileZ: number = (z / step) & 511;

        let foundData: boolean = false;

        const blockInfo: BlockInfo | undefined = this._pl3xmap.currentTileLayer?.getBlockInfo(zoom, fileX, fileZ);
        if (blockInfo !== undefined) {
            const block: Block = blockInfo.getBlock(tileZ * 512 + tileX);
            if (block != null) {
                this._block = block.block == 0 ? 'Void' : this._blockPalette.get(block.block) ?? 'unknown';
                this._biome = this._pl3xmap.currentWorld?.biomePalette.get(block.biome) ?? 'unknown';
                this._pl3xmap.coordsControl?.setY(block.yPos + 1);
                foundData = true;
            }
        }

        if (!foundData) {
            this._block = 'unknown';
            this._biome = 'unknown';
            this._pl3xmap.coordsControl?.setY(null);
        }

        this._dom.innerHTML = this._pl3xmap.lang.blockInfoValue
            .replace(/<block>/g, this._block.padEnd(15, ' '))
            .replace(/<biome>/g, this._biome.padEnd(15, ' '));
    }
}
