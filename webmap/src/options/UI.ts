export class UI {
    private _link: string = 'bottomright';
    private _coords: string = 'bottomcenter';
    private _blockinfo: string = 'bottomleft';
    private _attribution: boolean = true;

    get link(): string {
        return this._link;
    }

    set link(value: string) {
        this._link = value;
    }

    get coords(): string {
        return this._coords;
    }

    set coords(value: string) {
        this._coords = value;
    }

    get blockinfo(): string {
        return this._blockinfo;
    }

    set blockinfo(value: string) {
        this._blockinfo = value;
    }

    get attribution(): boolean {
        return this._attribution;
    }

    set attribution(value: boolean) {
        this._attribution = value;
    }
}
