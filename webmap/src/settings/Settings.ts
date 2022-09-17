import {Lang} from "./Lang";
import {Player} from "./Player";
import {WorldSettings} from "./WorldSettings";

/**
 * Represents global settings.
 */
export class Settings {
    private readonly _format: string;
    private readonly _maxPlayers: number;
    private readonly _lang: Lang;
    private readonly _players: Player[] = [];
    private readonly _worldSettings: WorldSettings[];

    constructor(format: string, maxPlayers: number, lang: Lang, players: Player[], worldSettings: WorldSettings[]) {
        this._format = format;
        this._maxPlayers = maxPlayers;
        this._lang = lang;
        this._players = players;
        this._worldSettings = worldSettings;
    }

    get format(): string {
        return this._format;
    }

    get maxPlayers(): number {
        return this._maxPlayers;
    }

    get lang(): Lang {
        return this._lang;
    }

    get players(): Player[] {
        return this._players;
    }

    get worldSettings(): WorldSettings[] {
        return this._worldSettings;
    }
}
