import * as L from "leaflet";
import {Pl3xMap} from "../Pl3xMap";
import {ControlBox} from "./ControlBox";
import {createSVGIcon, toPoint} from "../util/Util";
import Pl3xMapLeafletMap from "../map/Pl3xMapLeafletMap";
import '../svg/link.svg';
import {World} from "../world/World";

export class LinkControl extends ControlBox {
    private readonly _dom: HTMLAnchorElement;

    private onEvent = () => {
        this.update();
    }

    constructor(pl3xmap: Pl3xMap, position: string) {
        super(pl3xmap, position);
        this._dom = L.DomUtil.create('a', 'leaflet-control leaflet-control-button leaflet-control-link');
        this._dom.appendChild(createSVGIcon('link'));
    }

    onAdd(map: Pl3xMapLeafletMap): HTMLAnchorElement {
        map.addEventListener('moveend', this.onEvent);
        map.addEventListener('zoomend', this.onEvent);
        window.addEventListener('rendererselected', this.onEvent);
        window.addEventListener('worldselected', this.onEvent);
        this.update();
        return this._dom;
    }

    onRemove(map: Pl3xMapLeafletMap): void {
        map.removeEventListener('moveend', this.onEvent);
        map.removeEventListener('zoomend', this.onEvent);
        window.removeEventListener("rendererselected", this.onEvent);
        window.removeEventListener("worldselected", this.onEvent);
    }

    public update(): void {
        const url = this.getUrlFromView(this._pl3xmap.worldManager.currentWorld);
        this._dom.href = url;
        window.history.replaceState(null, this._pl3xmap.settings!.lang.title, url);
    }

    public getUrlFromView(world?: World): string {
        const center: L.PointTuple = toPoint(this._pl3xmap.map.getCenter());
        const zoom: number = this._pl3xmap.map.getCurrentZoom();
        const x: number = Math.floor(center[0]);
        const z: number = Math.floor(center[1]);
        let url = `?`;
        if (world !== undefined) {
            url += `world=${world.name}&renderer=${world.currentRenderer}`;
        }
        return `${url}&zoom=${zoom}&x=${x}&z=${z}`;
    }
}
