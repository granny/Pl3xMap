export class Lang {
    private _blockInfoLabel: string = 'BlockInfo';
    private _blockInfoValue: string = '<block><br /><biome>';
    private _coordsLabel: string = 'Coordinates';
    private _coordsValue: string = '<x>, <z>';
    private _players: string = 'Players (<online>/<max>)';
    private _worlds: string = 'Worlds';
    private _layers: string = 'Layers';

    get blockInfoLabel(): string {
        return this._blockInfoLabel;
    }

    set blockInfoLabel(value: string) {
        this._blockInfoLabel = value;
    }

    get blockInfoValue(): string {
        return this._blockInfoValue;
    }

    set blockInfoValue(value: string) {
        this._blockInfoValue = value;
    }

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

    get layers(): string {
        return this._layers;
    }

    set layers(value: string) {
        this._layers = value;
    }
}
