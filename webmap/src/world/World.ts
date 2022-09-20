import {Pl3xMap} from "../Pl3xMap";
import {MarkerLayer} from "../layergroup/MarkerLayer";
import {BlockInfo} from "../palette/BlockInfo";
import {Palette} from "../palette/Palette";
import {Label} from "../settings/Lang";
import {Spawn, WorldSettings, Zoom} from "../settings/WorldSettings";
import {ReversedZoomTileLayer} from "../tilelayer/ReversedZoomTileLayer";
import {WorldManager} from "./WorldManager";
import {fireCustomEvent, getBytes, getJSON} from "../util/Util";

/**
 * Represents a loaded world.
 */
export class World {
    private readonly _pl3xmap: Pl3xMap;
    private readonly _settings: WorldSettings;

    private _currentRenderer?: Label;
    private _currentRendererLayer?: ReversedZoomTileLayer;

    private _rendererLayers: Map<Label, ReversedZoomTileLayer> = new Map();
    private _markerLayers: MarkerLayer[] = [];

    private _biomePalette: Map<number, string> = new Map();
    private _blockInfo: Map<number, Map<string, BlockInfo>> = new Map();

    private _loaded = false;

    constructor(pl3xmap: Pl3xMap, worldManager: WorldManager, settings: WorldSettings) {
        this._pl3xmap = pl3xmap;
        this._settings = settings;
    }

    public load(): Promise<World> {
        if (this._loaded) {
            return Promise.resolve(this);
        }

        getJSON(`tiles/${this.name}/biomes.gz`)
            .then((palettes: Palette[]) => {
                for (const [index, biome] of Object.entries(palettes)) {
                    this.biomePalette.set(Number(index), String(biome)
                        .split(".").pop()!        // everything after the last period
                        .replace(/_+/g, ' ')      // replace underscores with spaces
                        .replace(/\w\S*/g, (w) => // capitalize first letter of every word
                            w.charAt(0).toUpperCase() + w.substring(1)
                        )
                    );
                }
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
                        this._rendererLayers.set(renderer, new ReversedZoomTileLayer(this._pl3xmap, this, renderer));
                    }

                    resolve(this);
                });
        });
    }

    public unload(): void {
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
                    markerLayer.addTo(this._pl3xmap.map);
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

    public getRendererLayer(renderer: Label): ReversedZoomTileLayer | undefined {
        return this._rendererLayers.get(renderer);
    }

    get currentRendererLayer(): ReversedZoomTileLayer | undefined {
        return this._currentRendererLayer;
    }

    get currentRenderer(): Label | undefined {
        return this._currentRenderer;
    }

    public setRenderer(renderer: Label | string): void {
        if (!(renderer instanceof Label)) {
            for (const label of this.renderers) {
                if (label.label == renderer) {
                    renderer = label;
                }
            }
        }

        this._currentRendererLayer?.remove();
        this._currentRenderer = this.settings.renderers.indexOf(renderer as Label) > -1 ? renderer as Label : this.settings.renderers[0];
        this._currentRendererLayer = this._rendererLayers.get(this._currentRenderer);
        this._currentRendererLayer!.addTo(this._pl3xmap.map);

        fireCustomEvent('rendererselected', this);
    }

    public resetRenderer(renderer?: Label | string): void {
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

    get renderers(): Label[] {
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
}
