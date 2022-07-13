export class Options {
    private _ui: UI = new UI();
    private _format: string = 'png';

    get ui(): UI {
        return this._ui;
    }

    set ui(value: UI) {
        this._ui = value;
    }

    get format(): string {
        return this._format;
    }

    set format(value: string) {
        this._format = value;
    }
}

class UI {
    private _link: boolean = false;
    private _coords: boolean = false;

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
}
