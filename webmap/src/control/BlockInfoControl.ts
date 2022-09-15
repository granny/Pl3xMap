import * as L from "leaflet";
import {Pl3xMap} from "../Pl3xMap";
import {Block} from "../palette/Block";
import {Palette} from "../palette/Palette";
import {ControlBox} from "./ControlBox";
import {getJSON} from "../util/Util";
import Pl3xMapLeafletMap from "../map/Pl3xMapLeafletMap";

export class BlockInfoControl extends ControlBox {
    private _dom: HTMLDivElement = L.DomUtil.create('div');

    private _blockPalette: Map<number, string> = new Map();

    constructor(pl3xmap: Pl3xMap, position: string) {
        super(pl3xmap, position);

        getJSON('tiles/blocks.gz').then((json: Palette[]) => {
            Object.entries(json).forEach((data, index) => {
                this._blockPalette.set(index, String(json[index]));
            });
        });
    }

    onAdd(map: Pl3xMapLeafletMap): HTMLDivElement {
        this._dom = L.DomUtil.create('div', 'leaflet-control leaflet-control-panel leaflet-control-blockinfo');
        this._dom.dataset.label = this._pl3xmap.settings!.lang.blockInfo.label;
        this.update(map);
        return this._dom;
    }

    public update(map: Pl3xMapLeafletMap): void {
        const coords = this._pl3xmap.controlManager.coordsControl!;
        const zoom: number = map.getCurrentZoom() < 0 ? 0 : map.getCurrentZoom();
        const x: number = coords.x;
        const z: number = coords.z;
        const step: number = 1 << zoom;
        const regionX: number = x >> 9;
        const regionZ: number = z >> 9;
        const fileX: number = Math.floor(regionX / step);
        const fileZ: number = Math.floor(regionZ / step);
        const tileX: number = (x / step) & 511;
        const tileZ: number = (z / step) & 511;

        let blockName: string = 'unknown';
        let biomeName: string = 'unknown';
        let y: number | undefined;

        const blockInfo = this._pl3xmap.worldManager.currentWorld?.getBlockInfo(zoom, fileX, fileZ);
        if (blockInfo !== undefined) {
            const block: Block = blockInfo.getBlock(tileZ * 512 + tileX);
            if (block != null) {
                blockName = block.block == 0 ? 'unknown' : this._blockPalette.get(block.block) ?? 'unknown';
                biomeName = block.biome == 0 ? 'unknown' : this._pl3xmap.worldManager.currentWorld?.biomePalette.get(block.biome) ?? 'unknown';

                if (block.block != 0) {
                    y = block.yPos + 1;
                }
            }
        }

        coords.y = y;
        this._dom.innerHTML = this._pl3xmap.settings!.lang.blockInfo.value
            .replace(/<block>/g, blockName!.padEnd(15, ' '))
            .replace(/<biome>/g, biomeName!.padEnd(15, ' '));
    }
}
