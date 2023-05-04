import {Pl3xMap} from "../Pl3xMap";
import {MarkerLayer} from "../layergroup/MarkerLayer";
import {BlockInfo} from "../palette/BlockInfo";
import {Label} from "../settings/Lang";
import {Spawn, WorldSettings, Zoom} from "../settings/WorldSettings";
import {DoubleTileLayer} from "../tilelayer/DoubleTileLayer";
import {WorldManager} from "./WorldManager";
import {fireCustomEvent, getBytes, getJSON} from "../util/Util";

/**
 * Represents a loaded world.
 */
export class World {
    private readonly _pl3xmap: Pl3xMap;
    private readonly _settings: WorldSettings;

    private _currentRenderer?: Renderer;
    private _currentRendererLayer?: DoubleTileLayer;

    private _rendererLayers: Map<Renderer, DoubleTileLayer> = new Map();
    private _markerLayers: MarkerLayer[] = [];

    private _biomePalette: Map<number, string> = new Map();
    private _blockInfo: Map<number, Map<string, BlockInfo>> = new Map();

    private _loaded = false;

    private _timer: NodeJS.Timeout | undefined;

    constructor(pl3xmap: Pl3xMap, worldManager: WorldManager, settings: WorldSettings) {
        this._pl3xmap = pl3xmap;
        this._settings = settings;
    }

    public load(): Promise<World> {
        if (this._loaded) {
            return Promise.resolve(this);
        }

        getJSON(`tiles/${this.name}/biomes.gz`).then((json): void => {
            Object.entries(json).forEach((data: [string, unknown]): void => {
                let name: string = <string>data[1];
                if (name.startsWith('minecraft:')) {
                    name = this._pl3xmap.langPalette.get('biome.minecraft.' + name.split('minecraft:')[1]) ?? name;
                    if (name.indexOf(':') !== -1) {
                        name = name.split(':')[1]             // split out the namespace
                            .split(".").pop()!                // everything after the last period
                            .replace(/_+/g, ' ')              // replace underscores with spaces
                            .replace(/\w\S*/g, (w: string) => // capitalize first letter of every word
                                w.charAt(0).toUpperCase() + w.substring(1)
                            )
                    }
                }
                this._biomePalette.set(Number(data[0]), name);
            });
        });

        //TODO: Handle errors
        return new Promise((resolve) => {
            getJSON(`tiles/${this.name}/settings.json`)
                .then((settings: WorldSettings) => {
                    this._loaded = true;

                    // copy settings values
                    this.settings.spawn = settings.spawn;
                    this.settings.zoom = settings.zoom;
                    this.settings.tileUpdateInterval = settings.tileUpdateInterval;
                    this.settings.ui = settings.ui;

                    // setup renderers
                    for (const renderer of this.settings.renderers) {
                        this._rendererLayers.set(renderer, new DoubleTileLayer(this._pl3xmap, this, renderer));
                    }

                    resolve(this);
                });
        });
    }

    public unload(): void {
        clearTimeout(this._timer);
        // unload and clear markers
        this._markerLayers.forEach(layer => layer.unload())
        this._markerLayers = [];
        // unload renderer layer
        this._currentRendererLayer?.remove();
        this._currentRendererLayer = undefined;
        this._currentRenderer = undefined;
    }

    public loadMarkers() {
        getJSON(`tiles/${this.name}/markers.json`)
            .then((json) => {
                (json as MarkerLayer[]).forEach((layer) => {
                    const markerLayer = new MarkerLayer(layer.key, layer.label, layer.updateInterval, layer.showControls, layer.defaultHidden, layer.priority, layer.zIndex, layer.pane, layer.css);
                    this._markerLayers.push(markerLayer);
                    markerLayer.update(this);
                });
            });
    }

    public loadBlockInfo(zoom: number, x: number, z: number) {
        if (!this.settings.ui.blockinfo) {
            return;
        }
        getBytes(`tiles/${this.name}/${zoom}/blockinfo/${x}_${z}.pl3xmap.gz`)
            .then((buffer?: ArrayBuffer) => {
                this.setBlockInfo(zoom, x, z, buffer);
            });
    }

    public getBlockInfo(zoom: number, x: number, z: number): BlockInfo | undefined {
        return this.blockInfo.get(zoom < 0 ? 0 : zoom)?.get(`${x}_${z}`);
    }

    public setBlockInfo(zoom: number, x: number, z: number, buffer?: ArrayBuffer) {
        let infoMap = this.blockInfo.get(zoom < 0 ? 0 : zoom);
        if (infoMap == undefined) {
            infoMap = new Map<string, BlockInfo>();
            this.blockInfo.set(zoom, infoMap);
        }

        const blockInfo = buffer == undefined ? null : new BlockInfo(new Uint8Array(buffer));

        if (blockInfo == null) {
            infoMap.delete(`${x}_${z}`);
        } else {
            infoMap.set(`${x}_${z}`, blockInfo);
        }
    }

    public unsetBlockInfo(zoom: number, x: number, z: number) {
        this.blockInfo.get(zoom)?.delete(`${x}_${z}`);
    }

    public getRendererLayer(renderer: Renderer): DoubleTileLayer | undefined {
        return this._rendererLayers.get(renderer);
    }

    get currentRendererLayer(): DoubleTileLayer | undefined {
        return this._currentRendererLayer;
    }

    get currentRenderer(): Renderer | undefined {
        return this._currentRenderer;
    }

    public setRenderer(renderer: Renderer | string): void {
        clearTimeout(this._timer);

        if (!(renderer instanceof Renderer)) {
            for (const label of this.renderers) {
                if (label.label == renderer) {
                    renderer = label;
                }
            }
        }

        this._currentRendererLayer?.remove();
        this._currentRenderer = this.settings.renderers.indexOf(renderer as Renderer) > -1 ? renderer as Renderer : this.settings.renderers[0];
        this._currentRendererLayer = this._rendererLayers.get(this._currentRenderer);
        this._currentRendererLayer!.addTo(this._pl3xmap.map);

        this.tick();

        fireCustomEvent('rendererselected', this);
    }

    public resetRenderer(renderer?: Renderer | string): void {
        this.setRenderer(renderer ?? this.settings.renderers[0]);
    }

    get settings(): WorldSettings {
        return this._settings;
    }

    get name(): string {
        return this.settings.name;
    }

    get displayName(): string {
        return this.settings.displayName;
    }

    get type(): string {
        return this.settings.type;
    }

    get order(): number {
        return this.settings.order;
    }

    get renderers(): Renderer[] {
        return this.settings.renderers;
    }

    get spawn(): Spawn {
        return this.settings.spawn;
    }

    get zoom(): Zoom {
        return this.settings.zoom;
    }

    get markerLayers(): MarkerLayer[] {
        return this._markerLayers;
    }

    get blockInfo(): Map<number, Map<string, BlockInfo>> {
        return this._blockInfo;
    }

    get biomePalette(): Map<number, string> {
        return this._biomePalette;
    }

    get background(): string {
        switch (this.type) {
            case "nether":
                return "url('images/sky/nether.png')";
            case "the_end":
                return "url('images/sky/the_end.png')";
            case "normal":
            default:
                return "url('images/sky/overworld.png')";
        }
    }

    private tick(): void {
        this.currentRendererLayer?.updateTileLayer();
        this._timer = setTimeout(() => this.tick(), this.settings.tileUpdateInterval * 1000);
    }
}

export class Renderer extends Label {
    private readonly _icon: string;

    constructor(label: string, value: string, icon: string) {
        super(label, value);
        this._icon = icon;
    }

    get icon(): string {
        return this._icon;
    }
}
