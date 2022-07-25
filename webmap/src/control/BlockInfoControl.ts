import {Control, DomUtil} from "leaflet";
import {Pl3xMap} from "../Pl3xMap";
import {Block, BlockInfo, Palette} from "../types/Json";
import {getJSON} from "../Util";

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
            position: 'bottomright'
        }

        getJSON('tiles/blocks.gz').then((json: Palette[]) => {
            Object.entries(json).forEach((data, index) => {
                this._blockPalette.set(index, String(json[index]));
            });
        });
    }

    onAdd(): HTMLDivElement {
        this._dom = DomUtil.create('div', 'leaflet-control leaflet-control-panel leaflet-control-blockinfo');
        this._dom.dataset.label = this._pl3xmap.lang.blockInfoLabel;
        this.update();
        return this._dom;
    }

    public update(): void {
        const x: number = this._pl3xmap.coordsControl?.getX() ?? 0;
        const z: number = this._pl3xmap.coordsControl?.getZ() ?? 0;
        const regionX = x >> 9;
        const regionZ = z >> 9;
        const tileX = x & 511;
        const tileZ = z & 511;

        let foundData: boolean = false;

        const blockInfo: BlockInfo | undefined = this._pl3xmap.currentTileLayer?.getBlockInfo(regionX, regionZ);
        if (blockInfo !== undefined) {
            const data: Block = blockInfo?.blocks[tileZ * 512 + tileX];
            if (data != null) {
                this._block = data[0] == 0 ? 'Void' : this._blockPalette.get(data[0]) ?? 'unknown';
                this._biome = this._pl3xmap.currentWorld?.biomePalette.get(data[1]) ?? 'unknown';
                this._pl3xmap.coordsControl?.setY(data[2] + 1);
                foundData = true;
            }
        }

        if (!foundData) {
            this._block = 'unknown';
            this._biome = 'unknown';
            this._pl3xmap.coordsControl?.setY(null);
        }

        this._dom.innerHTML = this._pl3xmap.lang.blockInfoValue
            .replace(/<block>/g, this._block)
            .replace(/<biome>/g, this._biome);
    }
}
