import {Control, DomEvent, DomUtil} from "leaflet";
import WorldsTab from "../sidebar/WorldsTab";
import SidebarTab from "../sidebar/SidebarTab";
import PlayersTab from "../sidebar/PlayersTab";
import MarkersTab from "../sidebar/MarkersTab";
import disableClickPropagation = DomEvent.disableClickPropagation;
import disableScrollPropagation = DomEvent.disableScrollPropagation;
import {Pl3xMap} from "../Pl3xMap";

export default class SidebarControl extends Control {
    private readonly _container: HTMLDivElement;
    private readonly _buttons: HTMLElement;
    private readonly _content: HTMLElement;

    private _tabs: Set<SidebarTab> = new Set();
    private _expanded: boolean = false;
    private _currentTab?: SidebarTab;

    constructor(pl3xmap: Pl3xMap) {
        super({position: 'topright'});

        this._container = DomUtil.create('div');
        this._container.id = 'sidebar';

        this._buttons = DomUtil.create('header', '', this._container);
        this._buttons.id = 'sidebar__buttons';

        this._content = DomUtil.create('section', '', this._container);
        this._content.id = 'sidebar__content';

        disableClickPropagation(this._container);
        disableScrollPropagation(this._container);

        this.addTab(new WorldsTab(pl3xmap));
        this.addTab(new PlayersTab(pl3xmap));
        this.addTab(new MarkersTab(pl3xmap));
    }

    onAdd() {
        return this._container;
    }

    toggle() {
        if (this._expanded) {
            this.collapse();
        } else {
            this.expand();
        }
    }

    expand() {
        this._expanded = true;
        this._container.classList.add('sidebar--expanded');
        this._content.setAttribute('aria-hidden', 'false');
    }

    collapse() {
        if (this._currentTab) {
            this.deactivateTab(this._currentTab, true);
        }

        this._expanded = false;
        this._container.classList.remove('sidebar--expanded');
        this._content.setAttribute('aria-hidden', 'true');
    }

    addTab(tab: SidebarTab) {
        if (this._tabs.has(tab)) {
            return;
        }

        this._tabs.add(tab);

        tab.button.addEventListener('click', () => this.toggleTab(tab));
        this._buttons.appendChild(tab.button);

        tab.content.addEventListener('keydown', (e: KeyboardEvent) => {
            if(e.key === 'Escape') {
                this.collapse();
            }
        });

        //Allow moving focus to first child of content when button is pressed
        if(tab.content.children.length) {
            (tab.content.firstElementChild as HTMLElement).tabIndex = -1;
        }

        this._content.appendChild(tab.content);

    }

    removeTab(tab: SidebarTab) {
        if (!this._tabs.has(tab)) {
            return;
        }

        this.deactivateTab(tab, false);
        tab.button.remove();
    }

    toggleTab(tab: SidebarTab) {
        if (this._currentTab === tab) {
            this.collapse();
        } else {
            this.activateTab(tab);
        }
    }

    activateTab(tab: SidebarTab) {
        if (!this._expanded) {
            this.expand();
        }

        if (this._currentTab) {
            this.deactivateTab(this._currentTab, false);
        }

        tab.button.setAttribute('aria-expanded', 'true');
        tab.content.hidden = false;
        tab.content.setAttribute('aria-hidden', 'false');
        this._currentTab = tab;

        (tab.content.firstElementChild as HTMLElement).focus();
    }

    deactivateTab(tab: SidebarTab, moveFocus: boolean) {
        if (tab !== this._currentTab) {
            return;
        }

        tab.button.setAttribute('aria-expanded', 'false');
        tab.content.hidden = true;
        tab.content.setAttribute('aria-hidden', 'true');
        this._currentTab = undefined;

        if(moveFocus) {
            tab.button.focus();
        }
    }
}
