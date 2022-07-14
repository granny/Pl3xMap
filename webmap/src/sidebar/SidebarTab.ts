import {DomEvent} from "leaflet";
import disableClickPropagation = DomEvent.disableClickPropagation;

export default class SidebarTab {
    protected readonly _button: HTMLButtonElement = document.createElement('button');
    protected readonly _content: HTMLDivElement = document.createElement('div');

    constructor() {
        this._button.type = 'button';
        disableClickPropagation(this._button);
    }

    get button(): HTMLElement {
        return this._button;
    }

    get content(): HTMLElement {
        return this._content;
    }

    onEnable() {}
    onDisable() {}
}