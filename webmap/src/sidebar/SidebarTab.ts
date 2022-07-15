import {Pl3xMap} from "../Pl3xMap";

export default class SidebarTab {
    protected readonly _pl3xmap: Pl3xMap;
    protected readonly _button: HTMLButtonElement = document.createElement('button');
    protected readonly _content: HTMLDivElement = document.createElement('div');
    protected readonly _id: string;

    constructor(pl3xmap: Pl3xMap, id: string) {
        this._pl3xmap = pl3xmap;
        this._id = id;

        this._button.type = 'button';
        this._button.setAttribute('aria-expanded', 'false');
        this._button.setAttribute('aria-controls', `sidebar__${this._id}`);

        this._content.hidden = true;
        this._content.id = `sidebar__${this._id}`;
        this._content.setAttribute('aria-hidden', 'true');
    }

    get button(): HTMLElement {
        return this._button;
    }

    get content(): HTMLElement {
        return this._content;
    }

    get id(): string{
        return this._id;
    }

    onEnable() {
    }

    onDisable() {
    }
}
