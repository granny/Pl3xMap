import {Pl3xMap} from "../Pl3xMap";
import {Util} from "../Util";
import {BlockInfo, Palette} from "./BlockInfo";
import {Spawn} from "./Spawn";
import {Zoom} from "./Zoom";
import {LinkControl} from "../control/LinkControl";
import {CoordsControl} from "../control/CoordsControl";
import {BlockInfoControl} from "../control/BlockInfoControl";
import {UI} from "../options/UI";
import {ReversedZoomTileLayer} from "../tilelayer/ReversedZoomTileLayer";

export class World {
    private readonly _pl3xmap: Pl3xMap;

    private readonly _name: string = 'world';
    private readonly _displayName: string = 'World';
    private readonly _icon: string = '';
    private readonly _type: string = 'overworld';
    private readonly _order: number = 0;

    private _ui: UI = new UI();
    private _spawn: Spawn = new Spawn(0, 0);
    private _zoom: Zoom = new Zoom(0, 0, 0);
    private _renderers: string[] = ['basic'];
    private _rendererLayers: Map<string, ReversedZoomTileLayer> = new Map();
    private _loaded = false;

    private _biomePalette: Map<number, string> = new Map();

    constructor(pl3xmap: Pl3xMap, data: WorldListJSON) {
        this._pl3xmap = pl3xmap;
        this._name = data.name;
        this._displayName = data.display_name;
        this._icon = data.icon;
        this._type = data.type;
        this._order = data.order;
    }

    public load(): Promise<World> {
        if (this._loaded) {
            return Promise.resolve(this);
        }

        Util.getJSON(`tiles/${this.name}/biomes.gz`)
            .then((json: Palette[]) => {
                Object.entries(json).forEach((data, index) => {
                    // get biome name
                    let biome: string = String(json[index]);

                    // get everything after the last period
                    const i: number = biome.lastIndexOf(".");
                    if (i >= 0) {
                        biome = biome.substring(i + 1);
                    }

                    // replace underscores with spaces and capitalize first letter of every word
                    const words: string[] = biome.replace(/_+/g, ' ').split(" ");
                    for (let i = 0; i < words.length; i++) {
                        words[i] = words[i][0].toUpperCase() + words[i].substring(1);
                    }

                    // store biome name
                    this._biomePalette.set(index, words.join(" "));
                });
            });

        //TODO: Handle errors
        return new Promise((resolve) => {
            Util.getJSON(`tiles/${this.name}/settings.json`)
                .then((json: WorldJSON) => {
                    this._loaded = true;
                    this.init(json);
                    resolve(this);
                });
        });
    }

    private init(json: WorldJSON): void {
        this._spawn = new Spawn(json.spawn.x, json.spawn.z);
        this._zoom = new Zoom(json.zoom.default, json.zoom.max_out, json.zoom.max_in);
        this._renderers = json.renderers ?? this._renderers;

        this._ui.link = json.ui.link;
        this._ui.coords = json.ui.coords;
        this._ui.blockinfo = json.ui.blockinfo;

        for (const renderer of this._renderers) {
            this._rendererLayers.set(renderer, new ReversedZoomTileLayer(this, renderer));
        }
    }

    public updateUI() {
        document.getElementById("map")!.style.background = this.background;
        this._pl3xmap.linkControl = this._ui.link ? new LinkControl(this._pl3xmap, this._ui.link) : null;
        this._pl3xmap.coordsControl = this._ui.coords ? new CoordsControl(this._pl3xmap, this._ui.coords) : null;
        this._pl3xmap.blockInfoControl = this._ui.blockinfo ? new BlockInfoControl(this._pl3xmap, this._ui.blockinfo) : null;
    }

    getTileLayer(renderer: string): ReversedZoomTileLayer | undefined {
        return this._rendererLayers.get(renderer);
    }

    get pl3xmap(): Pl3xMap {
        return this._pl3xmap;
    }

    get name(): string {
        return this._name;
    }

    get displayName(): string {
        return this._displayName;
    }

    get icon(): string {
        return this._icon;
    }

    get type(): string {
        return this._type;
    }

    get order(): number {
        return this._order;
    }

    get renderers(): string[] {
        return this._renderers;
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

    loadBlockInfo(zoom: number, x: number, z: number) {
        if (!this._ui.blockinfo) {
            return;
        }
        Util.getBytes(`tiles/${this._name}/${zoom}/blockinfo/${x}_${z}.pl3xmap.gz`)
            .then((buffer: ArrayBuffer | undefined) => {
                const blockInfo = buffer == undefined ? null : new BlockInfo(new Uint8Array(buffer));
                this._pl3xmap.currentTileLayer?.setBlockInfo(zoom, x, z, blockInfo);
            });
    }
}

export type WorldListJSON = {
    name: string;
    display_name: string;
    icon: string;
    type: string;
    order: number;
}

export type WorldJSON = {
    name: string;
    renderers: string[];
    tiles_update_interval: number;
    spawn: {
        x: number;
        z: number;
    };
    zoom: {
        default: number;
        max_out: number;
        max_in: number;
    };
    ui: {
        link: string;
        coords: string;
        blockinfo: string;
    }
}
