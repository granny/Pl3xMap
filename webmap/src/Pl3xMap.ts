import * as L from "leaflet";
import {Util} from "./util/Util";
import {BlockInfoControl} from "./control/BlockInfoControl";
import {CoordsControl} from "./control/CoordsControl";
import {LinkControl} from "./control/LinkControl";
import {PlayerLayerGroup} from "./layergroup/PlayerLayerGroup";
import {World, WorldListJSON} from "./module/World";
import {Lang} from "./options/Lang";
import {Options} from "./options/Options";
import {ReversedZoomTileLayer} from "./tilelayer/ReversedZoomTileLayer";
import SidebarControl from "./control/SidebarControl";
import Pl3xmapLeafletMap from "./map/Pl3xmapLeafletMap";
import "./scss/styles.scss";

window.onload = function () {
    new Pl3xMap();
};

export class Pl3xMap {
    private static instance: Pl3xMap;

    private readonly _map: Pl3xmapLeafletMap;
    private _options: Options = new Options();
    private _lang: Lang = new Lang();

    private readonly _worlds: Map<string, World> = new Map();
    private readonly _rendererLayers: Map<string, L.Layer> = new Map();
    private readonly _overlayLayers: Set<L.Layer> = new Set();

    private _currentWorld: World | null = null;
    private _currentRenderer: string | null = null;
    private _currentRendererLayer: ReversedZoomTileLayer | null = null;

    private _playersLayer: PlayerLayerGroup | null = null;
    private _coordsControl: CoordsControl | null = null;
    private _blockInfoControl: BlockInfoControl | null = null;
    private _linkControl: LinkControl | null = null;
    private _sidebarControl: SidebarControl = new SidebarControl(this);

    constructor() {
        Pl3xMap.instance = this;

        this._map = new Pl3xmapLeafletMap(this);

        Util.getJSON('tiles/settings.json').then((json: RootJSON) => this.init(json));
    }

    static getInstance(): Pl3xMap {
        return Pl3xMap.instance;
    }

    async init(json: RootJSON) {
        document.title = this.lang.title = json.lang.title;

        this._lang.coordsLabel = json.lang.coords.label;
        this._lang.coordsValue = json.lang.coords.value;
        this._lang.players = json.lang.players;
        this._lang.worldsHeading = json.lang.worlds.heading;
        this._lang.worldsSkeleton = json.lang.worlds.skeleton;
        this._lang.layersHeading = json.lang.layers.heading;
        this._lang.layersSkeleton = json.lang.layers.skeleton;

        this._options.format = json.format;

        for (const world of json.worlds) {
            this.addWorld(new World(this, world));
        }

        this._sidebarControl.addTo(this._map);

        // player tracker layer
        this._playersLayer = new PlayerLayerGroup().setZIndex(100);
        this.addOverlay(this._playersLayer, 'Players', true); //TODO: Lang

        // load world from url, or first world from json
        const initialWorld = Util.getUrlParam('world', this._worlds.keys().next().value),
            initialRenderer = Util.getUrlParam('renderer', this._currentRenderer);

        if (this._worlds.has(initialWorld)) {
            await this.setCurrentMap(this.worlds.get(initialWorld)!, initialRenderer);
        }

        this._map.on('baselayerchange', e => {
            this._currentRendererLayer = (e.layer as ReversedZoomTileLayer);
            this._currentRenderer = this._currentRendererLayer.renderer;
        });
    }

    addWorld(world: World) {
        this._worlds.set(world.name, world);

        Util.fireCustomEvent('worldadded', world);
    }

    getWorld(name: string): World | undefined {
        return this._worlds.get(name);
    }

    getUrlFromView(): string {
        const center: L.PointTuple = Util.toPoint(this._map.getCenter());
        const zoom: number = this._map.getCurrentZoom();
        const x: number = Math.floor(center[0]);
        const z: number = Math.floor(center[1]);
        const world: string = this._currentWorld?.name ?? '';
        const renderer: string = this._currentRenderer ?? '';
        return `?world=${world}&renderer=${renderer}&zoom=${zoom}&x=${x}&z=${z}`;
    }

    addOverlay(layer: L.Layer, name: string, showInControl: boolean) {
        if (this._overlayLayers.has(layer)) {
            return;
        }

        this._overlayLayers.add(layer);

        Util.fireCustomEvent('overlayadded', {
            layer,
            name,
            showInControl
        });
    }

    async setCurrentMap(world: World, renderer?: string | null): Promise<void> {
        let worldChanged = false,
            rendererChanged = false;

        return world.load().then(() => {
            renderer = renderer || this._currentRenderer;
            renderer = renderer && world.renderers.indexOf(renderer) > -1 ? renderer : world.renderers[0] ?? 'basic';

            if (world === this._currentWorld && renderer === this._currentRenderer) {
                return;
            }

            if (this._currentRendererLayer) {
                this._currentRendererLayer.remove();
            }

            if (this._currentWorld !== world) {
                worldChanged = rendererChanged = true;
                this._rendererLayers.clear();
                this._currentWorld?.markerLayers.forEach(layer => layer.clear())
                this._map.world = world;
                this._currentWorld = world;

                this._map.centerOn(
                    Util.getUrlParam('x', world.spawn.x),
                    Util.getUrlParam('z', world.spawn.z),
                    Util.getUrlParam('zoom', world.zoom.default)
                );
            } else if (this._currentRenderer !== renderer) {
                rendererChanged = true;
            }

            this._currentRenderer = renderer;
            this._currentRendererLayer = world.getTileLayer(renderer)!;
            this._map.addLayer(this._currentRendererLayer);

            if (worldChanged) {
                Util.fireCustomEvent('worldselected', world);
            }

            if (rendererChanged) {
                Util.fireCustomEvent('rendererselected', renderer);
            }

            this.currentWorld?.updateUI();
            window.history.replaceState(null, this.lang.title, this.getUrlFromView());
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

    get linkControl(): LinkControl | null {
        return this._linkControl;
    }

    set linkControl(control: LinkControl | null) {
        this._linkControl?.remove();
        this._linkControl = control;
        this._linkControl?.addTo(this._map);
    }

    get coordsControl(): CoordsControl | null {
        return this._coordsControl;
    }

    set coordsControl(control: CoordsControl | null) {
        this._coordsControl?.remove();
        this._coordsControl = control;
        this._coordsControl?.addTo(this._map);
    }

    get blockInfoControl(): BlockInfoControl | null {
        return this._blockInfoControl;
    }

    set blockInfoControl(control: BlockInfoControl | null) {
        this._blockInfoControl?.remove();
        this._blockInfoControl = control;
        this._blockInfoControl?.addTo(this._map);
    }

    get currentTileLayer(): ReversedZoomTileLayer | null {
        return this._currentRendererLayer;
    }
}

export type RootJSON = {
    format: string;
    lang: {
        title: string;
        coords: {
            label: string
            value: string;
        };
        players: string;
        worlds: {
            heading: string
            skeleton: string;
        };
        layers: {
            heading: string
            skeleton: string;
        };
    };
    worlds: WorldListJSON[];
};
