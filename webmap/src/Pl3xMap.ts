import {Settings} from "./settings/Settings";
import {ControlManager} from "./control/ControlManager";
import {PlayerManager} from "./player/PlayerManager";
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
    private readonly _playerManager: PlayerManager;
    private readonly _worldManager: WorldManager;

    private _settings?: Settings;

    private _timer: NodeJS.Timeout | undefined;

    constructor() {
        Pl3xMap._instance = this;

        this._map = new Pl3xMapLeafletMap(this);

        this._map.createPane("nameplates").style.zIndex = '1000';

        this._controlManager = new ControlManager(this);
        this._playerManager = new PlayerManager(this);
        this._worldManager = new WorldManager(this);

        getJSON('tiles/settings.json').then((json) => {
            this._settings = json as Settings;
            document.title = this._settings.lang.title;
            this.controlManager.sidebarControl = new SidebarControl(this);
            const promise = this.worldManager.init(this._settings);
            this.update();
            return promise;
        });
    }

    public static get instance(): Pl3xMap {
        return Pl3xMap._instance;
    }

    private update(): void {
        getJSON('tiles/settings.json').then((json) => {
            this._settings = json as Settings;
            this.playerManager.update(this._settings);

            this._timer = setTimeout(() => this.update(), 1000);
        });
    }

    get map(): Pl3xMapLeafletMap {
        return this._map;
    }

    get controlManager(): ControlManager {
        return this._controlManager;
    }

    get playerManager(): PlayerManager {
        return this._playerManager;
    }

    get worldManager(): WorldManager {
        return this._worldManager;
    }

    get settings(): Settings | undefined {
        return this._settings;
    }
}
