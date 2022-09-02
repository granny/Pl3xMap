import * as L from "leaflet";
import {Pl3xMap} from "../Pl3xMap";
import {Util} from "../util/Util";
import {ControlBox} from "./ControlBox";
import Pl3xmapLeafletMap from "../map/Pl3xmapLeafletMap";

export class CoordsControl extends ControlBox {
    declare _map: Pl3xmapLeafletMap;
    private _dom: HTMLDivElement = L.DomUtil.create('div');
    private _x: number = 0;
    private _y: number | null = null;
    private _z: number = 0;

    private onEvent = (event: L.LeafletMouseEvent) => {
        return this.update(this._map, Util.toPoint(event.latlng));
    }

    constructor(pl3xmap: Pl3xMap, position: string) {
        super(pl3xmap, position);
    }

    onAdd(map: Pl3xmapLeafletMap): HTMLDivElement {
        this._dom = L.DomUtil.create('div', 'leaflet-control leaflet-control-panel leaflet-control-coordinates');
        this._dom.dataset.label = this._pl3xmap.lang.coordsLabel;
        map.addEventListener('mousemove', this.onEvent);
        this.update(map, new L.Point(0, 0));
        return this._dom;
    }

    onRemove(map: Pl3xmapLeafletMap): void {
        map.removeEventListener('mousemove', this.onEvent);
    }

    private update(map: Pl3xmapLeafletMap, point: L.Point): void {
        this._x = Math.round(point.x) - 1;
        this._z = Math.round(point.y) - 1;
        this._pl3xmap.blockInfoControl?.update(map);
        this._dom.innerHTML = this._pl3xmap.lang.coordsValue
            .replace(/<x>/g, this._x.toString().padStart(6, ' '))
            .replace(/<y>/g, (this._y?.toString() ?? '???').padStart(2, ' ').padEnd(3, ' '))
            .replace(/<z>/g, this._z.toString().padEnd(6, ' '));
    }

    getX() {
        return this._x;
    }

    setY(y: number | null) {
        this._y = y;
    }

    getZ() {
        return this._z;
    }
}
