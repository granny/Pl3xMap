export class UI {
    private _link: string = 'bottomright';
    private _coords: string = 'bottomcenter';
    private _blockinfo: string = 'bottomleft';

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
}
