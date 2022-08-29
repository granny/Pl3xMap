export class Lang {
    private _title: string = 'Pl3xMap';
    private _blockInfoLabel: string = 'BlockInfo';
    private _blockInfoValue: string = '<block><br /><biome>';
    private _coordsLabel: string = 'Coordinates';
    private _coordsValue: string = '<x>, <z>';
    private _players: string = 'Players (<online>/<max>)';
    private _worldsHeading: string = 'Worlds';
    private _worldsSkeleton: string = 'No worlds have been configured';
    private _layersHeading: string = 'Layers';
    private _layersSkeleton: string = 'No layers have been configured';

    get title(): string {
        return this._title;
    }

    set title(value: string) {
        this._title = value;
    }

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

    get worldsHeading(): string {
        return this._worldsHeading;
    }

    set worldsHeading(value: string) {
        this._worldsHeading = value;
    }

    get worldsSkeleton(): string {
        return this._worldsSkeleton;
    }

    set worldsSkeleton(value: string) {
        this._worldsSkeleton = value;
    }

    get layersHeading(): string {
        return this._layersHeading;
    }

    set layersHeading(value: string) {
        this._layersHeading = value;
    }   
    
    get layersSkeleton(): string {
        return this._layersSkeleton;
    }

    set layersSkeleton(value: string) {
        this._layersSkeleton = value;
    }
}
