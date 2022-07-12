import * as L from "leaflet";
import {Control, Point} from "leaflet";
import {Pl3xMap} from "../Pl3xMap";

export class CoordsControl extends Control {
    private pl3xmap: Pl3xMap;
    private _dom: any;
    private x: any = 0;
    private z: any = 0;

    constructor(pl3xmap: Pl3xMap) {
        super();
        this.pl3xmap = pl3xmap;
        super.options = {
            position: 'bottomleft'
        };
    }

    onAdd(): any {
        this._dom = L.DomUtil.create('div', 'leaflet-control-layers coordinates');
        this.pl3xmap.map.addEventListener('mousemove', (event: any) => this.update(this.pl3xmap.toPoint(event.latlng)));
        this.update(null);
        return this._dom;
    }

    update(point: Point | null): void {
        this.x = point == null ? '---' : Math.round(point.x);
        this.z = point == null ? '---' : Math.round(point.y);
        this._dom.innerHTML = this.pl3xmap.lang.coords
            .replace(/<x>/g, this.x)
            .replace(/<z>/g, this.z);
    }

    static create(pl3xmap: Pl3xMap): CoordsControl {
        return new CoordsControl(pl3xmap);
    }
}
