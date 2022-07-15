import {Control, DomUtil} from "leaflet";
import {Pl3xMap} from "../Pl3xMap";
import {createSVGIcon} from "../Util";
import '../svg/link.svg';

export class LinkControl extends Control {
    private _pl3xmap: Pl3xMap;
    private readonly _dom: HTMLAnchorElement;

    constructor(pl3xmap: Pl3xMap) {
        super();
        this._pl3xmap = pl3xmap;
        super.options = {
            position: 'bottomleft'
        };

        this._dom = DomUtil.create('a', 'leaflet-control leaflet-control-button leaflet-control-link');
        this._dom.appendChild(createSVGIcon('link'));
    }

    onAdd(): HTMLAnchorElement {
        this._pl3xmap.map.addEventListener('move', () => this.update());
        this._pl3xmap.map.addEventListener('zoom', () => this.update());
        this.update();

        return this._dom;
    }

    private update(): void {
        this._dom.href = this._pl3xmap.currentWorld == null ? '' : this._pl3xmap.getUrlFromView();
    }
}
