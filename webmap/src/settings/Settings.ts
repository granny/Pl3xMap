import {Lang} from "./Lang";
import {Player} from "../player/Player";
import {WorldSettings} from "./WorldSettings";

/**
 * Represents global settings.
 */
export class Settings {
    private readonly _format: string;
    private readonly _maxPlayers: number;
    private readonly _lang: Lang;
    private readonly _zoom: Zoom;
    private readonly _players: Player[];
    private readonly _worldSettings: WorldSettings[];

    constructor(format: string, maxPlayers: number, lang: Lang, zoom: Zoom, players: Player[], worldSettings: WorldSettings[]) {
        this._format = format;
        this._maxPlayers = maxPlayers;
        this._lang = lang;
        this._zoom = zoom;
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

    get zoom(): Zoom {
        return this._zoom;
    }

    get players(): Player[] {
        return this._players;
    }

    get worldSettings(): WorldSettings[] {
        return this._worldSettings;
    }
}

export class Zoom {
    private readonly _snap: number;
    private readonly _delta: number;
    private readonly _wheel: number;

    constructor(snap: number, delta: number, wheel: number) {
        this._snap = snap;
        this._delta = delta;
        this._wheel = wheel;
    }

    get snap(): number {
        return this._snap;
    }

    get delta(): number {
        return this._delta;
    }

    get wheel(): number {
        return this._wheel;
    }
}
