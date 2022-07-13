export class Lang {
    private _coords: string = 'Coordinates<br/><x>, <z>';
    private _players: string = 'Players (<online>/<max>)';
    private _worlds: string = 'Worlds';

    get coords(): string {
        return this._coords;
    }

    set coords(value: string) {
        this._coords = value;
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
