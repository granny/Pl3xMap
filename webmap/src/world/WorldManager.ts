import {Pl3xMap} from "../Pl3xMap";
import {BlockInfoControl} from "../control/BlockInfoControl";
import {CoordsControl} from "../control/CoordsControl";
import {LinkControl} from "../control/LinkControl";
import {Label} from "../settings/Lang";
import {Settings} from "../settings/Settings";
import {World} from "./World";
import {fireCustomEvent, getUrlParam} from "../util/Util";

/**
 * The world manager. Manages all loaded worlds.
 */
export class WorldManager {
    private readonly _pl3xmap: Pl3xMap;
    private _worlds: Map<string, World> = new Map();
    private _currentWorld?: World;

    constructor(pl3xmap: Pl3xMap) {
        this._pl3xmap = pl3xmap;
    }

    public async init(settings: Settings) {
        // build world objects
        const worlds = [];
        for (const worldSettings of settings.worldSettings) {
            const world = new World(this._pl3xmap, this, worldSettings);
            worlds.push(world);
        }

        // sort and store worlds
        worlds.sort((w1, w2) => w1.settings.order - w2.settings.order).forEach(world => {
            this._worlds.set(world.name, world);
            fireCustomEvent('worldadded', world);
        });

        // get world name from url, or first world from settings
        const worldName: string = getUrlParam('world', this._worlds.keys().next().value);

        // load world
        const world = this._worlds.get(worldName);
        if (world) {
            await this.setWorld(world, getUrlParam('renderer', undefined));
        }
    }

    get currentWorld(): World | undefined {
        return this._currentWorld;
    }

    public getWorld(world: string): World | undefined {
        return this._worlds.get(world);
    }

    public async setWorld(world: World, renderer?: Label): Promise<void> {
        return world.load().then(() => {
            if (world === this._currentWorld && renderer === this._currentWorld.currentRenderer) {
                return;
            }

            if (world !== this._currentWorld) {
                this._currentWorld?.unload();
                this._currentWorld = world;
                world.loadMarkers();
                fireCustomEvent('worldselected', world);
            }

            this._currentWorld.resetRenderer(renderer);

            this._pl3xmap.map.centerOn(
                getUrlParam('x', world.spawn.x),
                getUrlParam('z', world.spawn.z),
                getUrlParam('zoom', world.zoom.default)
            );

            document.getElementById("map")!.style.background = world.background;

            const ui = world.settings.ui;
            this._pl3xmap.controlManager.linkControl = ui.link ? new LinkControl(this._pl3xmap, ui.link) : undefined;
            this._pl3xmap.controlManager.coordsControl = ui.coords ? new CoordsControl(this._pl3xmap, ui.coords) : undefined;
            this._pl3xmap.controlManager.blockInfoControl = ui.blockinfo ? new BlockInfoControl(this._pl3xmap, ui.blockinfo) : undefined;

            const attributeDom = this._pl3xmap.map.attributionControl.getContainer();
            if (attributeDom) {
                attributeDom.style.display = ui.attribution ? "inline-block" : "none";
            }
        });
    }
}
