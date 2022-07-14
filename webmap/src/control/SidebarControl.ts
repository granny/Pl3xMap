import {Control, DomUtil, DomEvent} from "leaflet";
import MapsTab from "../sidebar/MapsTab";
import SidebarTab from "../sidebar/SidebarTab";
import PlayersTab from "../sidebar/PlayersTab";
import MarkersTab from "../sidebar/MarkersTab";
import disableClickPropagation = DomEvent.disableClickPropagation;
import disableScrollPropagation = DomEvent.disableScrollPropagation;

export default class SidebarControl extends Control {
    private readonly _container: HTMLDivElement;
    private readonly _buttons: HTMLElement;
    private readonly _content: HTMLElement;

    private _tabs: Set<SidebarTab> = new Set();
    private _expanded: boolean = false;
    private _currentTab?: SidebarTab;

    constructor() {
        super({ position: 'topright' });

        this._container = DomUtil.create('div');
        this._container.id = 'sidebar';

        this._buttons = DomUtil.create('header', '', this._container);
        this._buttons.id = 'sidebar__buttons';

        this._content = DomUtil.create('section', '', this._container);
        this._content.id = 'sidebar__content';

        disableClickPropagation(this._container);
        disableScrollPropagation(this._container);

        this.addTab(new MapsTab());
        this.addTab(new PlayersTab());
        this.addTab(new MarkersTab());
    }

    onAdd() {
        return this._container;
    }

    toggle() {
        if(this._expanded) {
            this.collapse();
        } else {
            this.expand();
        }
    }

    expand() {
        this._expanded = true;
        this._container.classList.add('sidebar--expanded');
        this._content.removeAttribute('aria-hidden');
    }

    collapse() {
        if(this._currentTab) {
            this.deactivateTab(this._currentTab);
        }

        this._expanded = false;
        this._container.classList.remove('sidebar--expanded');
        this._content.setAttribute('aria-hidden', 'true');
    }

    addTab(tab: SidebarTab) {
        if(this._tabs.has(tab)) {
            return;
        }

        this._tabs.add(tab);
        const button = tab.button;
        button.addEventListener('click', () => this.toggleTab(tab));

        this._buttons.appendChild(button);
    }

    removeTab(tab: SidebarTab) {
        if(!this._tabs.has(tab)) {
            return;
        }

        this.deactivateTab(tab);
        tab.button.remove();
    }

    toggleTab(tab: SidebarTab) {
        if(this._currentTab === tab) {
            this.collapse();
        } else {
            this.activateTab(tab);
        }
    }

    activateTab(tab: SidebarTab) {
        if(!this._expanded) {
            this.expand();
        }

        if(this._currentTab) {
            this.deactivateTab(this._currentTab);
        }

        tab.button.setAttribute('aria-expanded', 'true');
        this._currentTab = tab;
        this._content.replaceChildren(tab.content);
    }

    deactivateTab(tab: SidebarTab) {
        if(tab !== this._currentTab) {
            return;
        }

        tab.button.removeAttribute('aria-expanded');
        this._currentTab = undefined;
        tab.content.remove();
    }
}