import {Control, ControlOptions, ControlPosition, DomUtil, LeafletMouseEvent, Point} from "leaflet";
import {Pl3xMap} from "../Pl3xMap";
import Pl3xmapLeafletMap from "../map/Pl3xmapLeafletMap";

interface ExtendedControlOptions extends ControlOptions {
    position?: ControlPosition & Position | undefined;
}

type Position = 'topcenter' | 'bottomcenter';

export class CoordsControl extends Control {
    declare _map: Pl3xmapLeafletMap;
    private _pl3xmap: Pl3xMap;
    private _dom: HTMLDivElement = DomUtil.create('div');
    private _x: number = 0;
    private _y: number | null = null;
    private _z: number = 0;

    constructor(pl3xmap: Pl3xMap) {
        super();
        this._pl3xmap = pl3xmap;
        super.options = {
            position: 'bottomcenter'
        } as unknown as ExtendedControlOptions;
    }

    onAdd(): HTMLDivElement {
        this._dom = DomUtil.create('div', 'leaflet-control leaflet-control-panel leaflet-control-coordinates');
        this._dom.dataset.label = this._pl3xmap.lang.coordsLabel;

        this._pl3xmap.map.addEventListener('mousemove', (event: LeafletMouseEvent) => this.update(this._map.toPoint(event.latlng)));
        this.update(new Point(0, 0));
        return this._dom;
    }

    private update(point: Point): void {
        this._x = Math.round(point.x) - 1;
        this._z = Math.round(point.y) - 1;
        this._pl3xmap.blockInfoControl?.update();
        this._dom.innerHTML = this._pl3xmap.lang.coordsValue
            .replace(/<x>/g, this._x.toString().padStart(6, ' '))
            .replace(/<y>/g, this._y?.toString() ?? '63') // default to 63 (sea level)
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
