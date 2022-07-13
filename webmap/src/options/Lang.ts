export class Lang {
    private _coordsLabel: string = 'Coordinates';
    private _coordsValue: string = '<x>, <z>';
    private _players: string = 'Players (<online>/<max>)';
    private _worlds: string = 'Worlds';

    get coordsLabel(): string {
        return this._coordsLabel;
    }

    set coordsLabel(value: string) {
        this._coordsLabel = value;
    }

    get coordsValue(): string {
        return this._coordsValue;
    }

    set coordsValue(value: string) {
        this._coordsValue = value;
    }

    get players(): string {
        return this._players;
    }

    set players(value: string) {
        this._players = value;
    }

    get worlds(): string {
        return this._worlds;
    }

    set worlds(value: string) {
        this._worlds = value;
    }
}
