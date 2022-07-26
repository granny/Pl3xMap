import {Control, DomUtil, Map} from "leaflet";
import {Pl3xMap} from "../Pl3xMap";
import {createSVGIcon} from "../Util";
import '../svg/link.svg';

export class LinkControl extends Control {
    private _pl3xmap: Pl3xMap;
    private readonly _dom: HTMLAnchorElement;

    private onEvent = () => {
        this.update();
    }

    constructor(pl3xmap: Pl3xMap) {
        super();
        this._pl3xmap = pl3xmap;
        super.options = {
            position: 'bottomright'
        };
        this._dom = DomUtil.create('a', 'leaflet-control leaflet-control-button leaflet-control-link');
        this._dom.appendChild(createSVGIcon('link'));
    }

    onAdd(): HTMLAnchorElement {
        this._pl3xmap.map.addEventListener('moveend', this.onEvent);
        this._pl3xmap.map.addEventListener('zoomend', this.onEvent);
        window.addEventListener('rendererselected', this.onEvent);
        window.addEventListener('worldselected', this.onEvent);
        this.update();
        return this._dom;
    }

    onRemove(map: Map): void {
        super.onRemove!(map);
        this._pl3xmap.map.removeEventListener('moveend', this.onEvent);
        this._pl3xmap.map.removeEventListener('zoomend', this.onEvent);
        window.removeEventListener("rendererselected", this.onEvent);
        window.removeEventListener("worldselected", this.onEvent);
    }

    public update(): void {
        this._dom.href = this._pl3xmap.currentWorld == null ? '' : this._pl3xmap.getUrlFromView();
    }
}
