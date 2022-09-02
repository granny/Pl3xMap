import * as L from "leaflet";
import {Pl3xMap} from "../Pl3xMap";
import {Util} from "../util/Util";
import {ControlBox} from "./ControlBox";
import Pl3xmapLeafletMap from "../map/Pl3xmapLeafletMap";
import '../svg/link.svg';

export class LinkControl extends ControlBox {
    private readonly _dom: HTMLAnchorElement;

    private onEvent = () => {
        this.update();
    }

    constructor(pl3xmap: Pl3xMap, position: string) {
        super(pl3xmap, position);
        this._dom = L.DomUtil.create('a', 'leaflet-control leaflet-control-button leaflet-control-link');
        this._dom.appendChild(Util.createSVGIcon('link'));
    }

    onAdd(map: Pl3xmapLeafletMap): HTMLAnchorElement {
        map.addEventListener('moveend', this.onEvent);
        map.addEventListener('zoomend', this.onEvent);
        window.addEventListener('rendererselected', this.onEvent);
        window.addEventListener('worldselected', this.onEvent);
        this.update();
        return this._dom;
    }

    onRemove(map: Pl3xmapLeafletMap): void {
        map.removeEventListener('moveend', this.onEvent);
        map.removeEventListener('zoomend', this.onEvent);
        window.removeEventListener("rendererselected", this.onEvent);
        window.removeEventListener("worldselected", this.onEvent);
    }

    public update(): void {
        this._dom.href = this._pl3xmap.currentWorld == null ? '' : this._pl3xmap.getUrlFromView();
    }
}
