import {Pl3xMap} from "../Pl3xMap";
import {ReversedZoomTileLayer} from "../tilelayer/ReversedZoomTileLayer";
import {Spawn} from "./Spawn";
import {Zoom} from "./Zoom";
import {WorldJSON} from "../types/Json";

export class World {
    private readonly _pl3xmap: Pl3xMap;
    private readonly _name: string = 'world';

    private _renderer: string = 'basic';
    private _spawn: Spawn = new Spawn(0, 0);
    private _zoom: Zoom = new Zoom(0, 0, 0);

    constructor(pl3xmap: Pl3xMap, name: string) {
        this._pl3xmap = pl3xmap;
        this._name = name;

        pl3xmap.getJSON(`tiles/${name}/settings.json`,
            (json: WorldJSON) => this.init(json));
    }

    private init(json: WorldJSON): void {
        this._spawn = new Spawn(json.spawn.x, json.spawn.z);
        this._zoom = new Zoom(json.zoom.default, json.zoom.max_out, json.zoom.max_in)
        this.renderer = this._pl3xmap.getUrlParam('renderer', json.renderers[0]);
        this.centerOn(
            this._pl3xmap.getUrlParam('x', json.spawn.x),
            this._pl3xmap.getUrlParam('z', json.spawn.z),
            this._pl3xmap.getUrlParam('zoom', json.zoom.default)
        );
    }

    centerOn(x: number, z: number, zoom: number): void {
        this._pl3xmap.centerOn(x, z, zoom);
    }

    get name(): string {
        return this._name;
    }

    get renderer(): string {
        return this._renderer;
    }

    set renderer(renderer: string) {
        this._renderer = renderer;
        if (this._pl3xmap.tileLayer != null) {
            this._pl3xmap.map.removeLayer(this._pl3xmap.tileLayer);
        }
        this._pl3xmap.tileLayer = new ReversedZoomTileLayer(this)
            .setZIndex(0)
            .addTo(this._pl3xmap.map);
    }

    get spawn(): Spawn {
        return this._spawn;
    }

    get zoom(): Zoom {
        return this._zoom;
    }

    get format(): string {
        return this._pl3xmap.options.format;
    }
}
