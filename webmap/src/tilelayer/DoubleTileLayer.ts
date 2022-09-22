import {Pl3xMap} from "../Pl3xMap";
import {World} from "../world/World";
import {Label} from "../settings/Lang";
import {ReversedZoomTileLayer} from "./ReversedZoomTileLayer";
import Pl3xMapLeafletMap from "../map/Pl3xMapLeafletMap";

export class DoubleTileLayer {
    private readonly _tileLayer1: ReversedZoomTileLayer;
    private readonly _tileLayer2: ReversedZoomTileLayer;

    // start with 0. from here will switch to 1 then 2 then 1 then 2 etc.
    private _currentLayer: number = 0;

    constructor(pl3xmap: Pl3xMap, world: World, renderer: Label) {
        // we need 2 tile layers to swap between for seamless refreshing
        this._tileLayer1 = this.createTileLayer(pl3xmap, world, renderer);
        this._tileLayer2 = this.createTileLayer(pl3xmap, world, renderer);
    }

    private createTileLayer(pl3xmap: Pl3xMap, world: World, renderer: Label): ReversedZoomTileLayer {
        return new ReversedZoomTileLayer(pl3xmap, world, renderer)
            .addEventListener("load", () => {
                // when all tiles in this layer are loaded, switch to this layer
                this.switchTileLayer();
            });
    }

    private switchTileLayer(): void {
        // swap current tile layer
        if (this._currentLayer == 1) {
            this._tileLayer1.setZIndex(0);
            this._tileLayer2.setZIndex(1);
            this._currentLayer = 2;
        } else {
            this._tileLayer1.setZIndex(1);
            this._tileLayer2.setZIndex(0);
            this._currentLayer = 1;
        }
    }

    public updateTileLayer(): void {
        // redraw opposite tile layer
        // it will switch to it when all tiles load
        if (this._currentLayer == 1) {
            this._tileLayer2.redraw();
        } else {
            this._tileLayer1.redraw();
        }
    }

    public addTo(map: Pl3xMapLeafletMap): void {
        this._tileLayer1.addTo(map);
        this._tileLayer2.addTo(map);
    }

    public remove(): void {
        this._tileLayer1.remove();
        this._tileLayer2.remove();
    }
}
