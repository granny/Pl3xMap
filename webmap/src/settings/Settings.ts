import {Lang} from "./Lang";
import {WorldSettings} from "./WorldSettings";

/**
 * Represents global settings.
 */
export class Settings {
    private readonly _worldSettings: WorldSettings[];
    private readonly _format: string;
    private readonly _lang: Lang;

    constructor(worldSettings: WorldSettings[], format: string, lang: Lang) {
        this._worldSettings = worldSettings;
        this._format = format;
        this._lang = lang;
    }

    get worldSettings(): WorldSettings[] {
        return this._worldSettings;
    }

    get format(): string {
        return this._format;
    }

    get lang(): Lang {
        return this._lang;
    }
}
