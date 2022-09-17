import {Pl3xMap} from "../Pl3xMap";
import {Player} from "./Player";
import {Settings} from "../settings/Settings";
import {fireCustomEvent} from "../util/Util";

export class PlayerManager {
    private readonly _pl3xmap: Pl3xMap;

    private _players: Map<string, Player> = new Map();

    constructor(pl3xmap: Pl3xMap) {
        this._pl3xmap = pl3xmap;
    }

    public update(settings: Settings) {
        const toRemove: Set<string> = new Set(this._players.keys());

        for (const data of settings.players) {
            const existing = this._players.get(data.uuid);
            if (existing) {
                // update existing
                existing.displayName = data.displayName;
                existing.world = data.world;

                // do not remove this player
                toRemove.delete(existing.uuid);
            } else {
                // create new player
                const player = new Player(data.name, data.uuid, data.displayName, data.world);
                this._players.set(player.uuid, player);

                // inform the events
                fireCustomEvent('playeradded', player);
            }
        }

        toRemove.forEach(uuid => {
            // remove players not in updated settings file
            const player = this._players.get(uuid);
            this._players.delete(uuid);
            fireCustomEvent('playerremoved', player);
        });
    }

    get players(): Map<string, Player> {
        return this._players;
    }
}