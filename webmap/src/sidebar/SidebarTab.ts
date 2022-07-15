import {DomEvent} from "leaflet";
import disableClickPropagation = DomEvent.disableClickPropagation;

export default class SidebarTab {
    protected readonly _button: HTMLButtonElement = document.createElement('button');
    protected readonly _content: HTMLDivElement = document.createElement('div');
    protected readonly _id: string;

    constructor(id: string) {
        this._id = id;
        this._button.type = 'button';
        this._button.setAttribute('aria-expanded', 'false');
        this._button.setAttribute('aria-controls', `sidebar__${this._id}`);

        this._content.hidden = true;
        this._content.id = `sidebar__${this._id}`;
        this._content.setAttribute('aria-hidden', 'true');

        disableClickPropagation(this._button);
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
