import * as L from "leaflet";
import {Control} from "leaflet";
import {Pl3xMap} from "../Pl3xMap";

export class LinkControl extends Control {
    private pl3xmap: Pl3xMap;
    private _dom: any;

    constructor(pl3xmap: Pl3xMap) {
        super();
        this.pl3xmap = pl3xmap;
        super.options = {
            position: 'bottomleft'
        };
    }

    onAdd(): any {
        this._dom = L.DomUtil.create('div', 'leaflet-control-layers link');
        this.pl3xmap.map.addEventListener('move', () => this.update());
        this.pl3xmap.map.addEventListener('zoom', () => this.update());
        this.update();
        return this._dom;
    }

    update(): void {
        const url = this.pl3xmap.world == null ? '' : this.pl3xmap.getUrlFromView();
        this._dom.innerHTML = `<a href='${url}'><img src='images/clear.png' alt=''/></a>`;
    }

    static create(pl3xmap: Pl3xMap): LinkControl {
        return new LinkControl(pl3xmap);
    }
}
