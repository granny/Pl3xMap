import {ControlManager} from "./control/ControlManager";
import {Settings} from "./settings/Settings";
import {WorldManager} from "./world/WorldManager";
import {getJSON} from "./util/Util";
import SidebarControl from "./control/SidebarControl";
import Pl3xMapLeafletMap from "./map/Pl3xMapLeafletMap";
import "./scss/styles.scss";

window.onload = function () {
    new Pl3xMap();
};

/**
 * Represents the main Pl3xMap class.
 */
export class Pl3xMap {
    private static _instance: Pl3xMap;

    private readonly _map: Pl3xMapLeafletMap;

    private readonly _controlManager: ControlManager;
    private readonly _worldManager: WorldManager;

    private _settings?: Settings;

    constructor() {
        Pl3xMap._instance = this;

        this._map = new Pl3xMapLeafletMap(this);

        this._controlManager = new ControlManager(this);
        this._worldManager = new WorldManager(this);

        getJSON('tiles/settings.json').then((json) => this.init(json as Settings));
    }

    public static get instance(): Pl3xMap {
        return Pl3xMap._instance;
    }

    private async init(settings: Settings) {
        document.title = settings.lang.title;
        this._settings = settings;

        this.controlManager.sidebarControl = new SidebarControl(this);

        await this.worldManager.init(this._settings);
    }

    get map(): Pl3xMapLeafletMap {
        return this._map;
    }

    get worldManager(): WorldManager {
        return this._worldManager;
    }

    get controlManager(): ControlManager {
        return this._controlManager;
    }

    get settings(): Settings | undefined {
        return this._settings;
    }
}
