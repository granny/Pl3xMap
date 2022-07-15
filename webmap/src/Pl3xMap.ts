import {Control, Point} from "leaflet";
import {CoordsControl} from "./control/CoordsControl";
import {LinkControl} from "./control/LinkControl";
import {PlayerLayerGroup} from "./layergroup/PlayerLayerGroup";
import {World} from "./module/World";
import {Lang} from "./options/Lang";
import {Options} from "./options/Options";
import {RootJSON} from "./types/Json";

import "./scss/styles.scss";
import Pl3xmapLeafletMap from "./map/Pl3xmapLeafletMap";
import LayersControl from "./control/LayersControl";
import SidebarControl from "./control/SidebarControl";
import {getJSON, getUrlParam} from "./Util";

window.onload = function () {
    new Pl3xMap();
};

export class Pl3xMap {
    private readonly _map: Pl3xmapLeafletMap;
    private _options: Options = new Options();
    private _lang: Lang = new Lang();

    private readonly _worlds: Map<string, World> = new Map();
    private _currentWorld: World | null = null;
    private _currentRenderer: string = 'basic';

    private _playersLayer: PlayerLayerGroup | null = null;
    private _layerControls: Control.Layers | null = null;
    private _coordsControl: CoordsControl | null = null;
    private _linkControl: LinkControl | null = null;
    private _sidebarControl: SidebarControl = new SidebarControl(this);

    constructor() {
        this._map = new Pl3xmapLeafletMap(this);

        getJSON('tiles/settings.json').then((json: RootJSON) => this.init(json));
    }

    async init(json: RootJSON) {
        document.title = json.ui.lang.title;

        this._lang.coordsLabel = json.ui.lang.coords.label;
        this._lang.coordsValue = json.ui.lang.coords.value;
        this._lang.players = json.ui.lang.players;
        this._lang.worlds = json.ui.lang.worlds;
        this._lang.layers = json.ui.lang.layers;

        this._options.ui.link = json.ui.link;
        this._options.ui.coords = json.ui.coords;

        this._options.format = json.format;

        for (const world of json.worlds) {
            this.addWorld(new World(this, world));
        }

        // player tracker layer
        this._playersLayer = new PlayerLayerGroup().setZIndex(100).addTo(this._map);

        // set up layer controls
        this._layerControls = new LayersControl(this)
            .addOverlay(this._playersLayer, 'Players')
            .addTo(this._map);

        this._sidebarControl.addTo(this._map);

        // add the coords ui control box
        if (this._options.ui.coords) {
            this._coordsControl = new CoordsControl(this).addTo(this._map);
        }

        // add the link ui control box
        if (this._options.ui.link) {
            this._linkControl = new LinkControl(this).addTo(this._map);
        }

        // load world from url, or first world from json
        const initialWorld = getUrlParam('world', this._worlds.keys().next().value),
            initialRenderer = getUrlParam('renderer', this._currentRenderer);

        if (this._worlds.has(initialWorld)) {
            await this.setCurrentMap(this.worlds.get(initialWorld)!, initialRenderer);
        }
    }

    addWorld(world: World) {
        this._worlds.set(world.name, world);

        dispatchEvent(new CustomEvent('worldadded', {
            bubbles: false,
            composed: false,
            detail: world
        }));
    }

    getUrlFromView(): string {
        const center: Point = this._map.toPoint(this._map.getCenter());
        const zoom: number = this._map.getCurrentZoom();
        const x: number = Math.floor(center.x);
        const z: number = Math.floor(center.y);
        const world: string = this._currentWorld?.name ?? '';
        const renderer: string = this._map.renderer ?? '';
        return `?world=${world}&renderer=${renderer}&zoom=${zoom}&x=${x}&z=${z}`;
    }

    setCurrentMap(world: World, renderer?: string): Promise<void> {
        return world.load().then(() => {
            renderer = renderer || this._currentRenderer;
            renderer = world.renderers.indexOf(renderer) > -1 ? renderer : world.renderers[0] ?? 'basic';

            this._currentWorld = world
            this._currentRenderer = renderer;
            this._map.setCurrentMap(world, renderer);

            window.dispatchEvent(new CustomEvent('mapchanged', {
                detail: {
                    world,
                    renderer,
                }
            }))
        });
    }

    get map(): Pl3xmapLeafletMap {
        return this._map;
    }

    get options(): Options {
        return this._options;
    }

    get lang(): Lang {
        return this._lang;
    }

    get worlds(): Map<string, World> {
        return this._worlds;
    }

    get currentWorld(): World | null {
        return this._currentWorld;
    }

    get playersLayer(): PlayerLayerGroup | null {
        return this._playersLayer;
    }

    get layerControls(): Control.Layers | null {
        return this._layerControls;
    }

    get coordsControl(): CoordsControl | null {
        return this._coordsControl;
    }

    get linkControl(): LinkControl | null {
        return this._linkControl;
    }
}
