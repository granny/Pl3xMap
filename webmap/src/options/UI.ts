export class UI {
    private _link: boolean = false;
    private _coords: boolean = false;
    private _blockinfo: boolean = false;

    get link(): boolean {
        return this._link;
    }

    set link(value: boolean) {
        this._link = value;
    }

    get coords(): boolean {
        return this._coords;
    }

    set coords(value: boolean) {
        this._coords = value;
    }

    get blockinfo(): boolean {
        return this._blockinfo;
    }

    set blockinfo(value: boolean) {
        this._blockinfo = value;
    }
}
