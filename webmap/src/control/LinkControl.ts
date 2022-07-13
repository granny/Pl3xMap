import * as L from "leaflet";
import {Control} from "leaflet";
import {Pl3xMap} from "../Pl3xMap";

export class LinkControl extends Control {
    private _pl3xmap: Pl3xMap;
    private _dom: HTMLDivElement = L.DomUtil.create('div');

    constructor(pl3xmap: Pl3xMap) {
        super();
        this._pl3xmap = pl3xmap;
        super.options = {
            position: 'bottomleft'
        };
    }

    onAdd(): HTMLDivElement {
        this._dom = L.DomUtil.create('div', 'leaflet-control-layers link');
        this._pl3xmap.map.addEventListener('move', () => this.update());
        this._pl3xmap.map.addEventListener('zoom', () => this.update());
        this.update();
        return this._dom;
    }

    private update(): void {
        const url = this._pl3xmap.world == null ? '' : this._pl3xmap.getUrlFromView();
        this._dom.innerHTML = `<a href='${url}'><img src='images/clear.png' alt=''/></a>`;
    }
}
