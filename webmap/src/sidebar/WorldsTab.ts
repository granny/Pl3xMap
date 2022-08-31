import * as L from "leaflet";
import {Pl3xMap} from "../Pl3xMap";
import {Util} from "../Util";
import {World} from "../module/World";
import BaseTab from "./BaseTab";
import '../svg/maps.svg';

interface WorldListItem {
    input: HTMLInputElement;
    label: HTMLLabelElement;

    //TODO: Refreshless config updates?
    icon?: HTMLImageElement;
    name: HTMLSpanElement;
}

export default class WorldsTab extends BaseTab {
    private readonly _worlds: Map<World, WorldListItem> = new Map();
    private readonly _skeleton: HTMLParagraphElement;
    private readonly _list: HTMLFieldSetElement;

    constructor(pl3xmap: Pl3xMap) {
        super(pl3xmap, 'worlds');

        this._button.appendChild(Util.createSVGIcon('maps'));
        this._button.setAttribute('aria-label', pl3xmap.lang.worldsHeading);

        const heading = L.DomUtil.create('h2', '', this._content);
        heading.innerText = pl3xmap.lang.worldsHeading;
        heading.id = 'worlds-heading';

        this._skeleton = L.DomUtil.create('p', '', this._content);
        this._skeleton.innerText = pl3xmap.lang.worldsSkeleton;
        this._skeleton.tabIndex = -1;

        this._list = L.DomUtil.create('fieldset', 'menu', this._content);
        this._list.setAttribute('aria-labelledby', 'worlds-heading');
        this._list.setAttribute('role', 'radiogroup');

        this.initEvents();
    }

    private initEvents() {
        addEventListener('worldadded', (e: CustomEvent<World>) => this.createListItem(e.detail));
        addEventListener('worldremoved', (e: CustomEvent<World>) => this.removeListItem(e.detail)); //TODO: Refreshless config updates?
        addEventListener('worldselected', (e: CustomEvent<World>) => {
            if (this._worlds.has(e.detail)) {
                this._worlds.get(e.detail)!.input.checked = true;
            }
        });

        this._list.addEventListener('keydown', (e: KeyboardEvent) =>
            Util.handleKeyboardEvent(e, Array.from(this._list.elements) as HTMLElement[]))
    }

    private createListItem(world: World) {
        const input = L.DomUtil.create('input'),
            label = L.DomUtil.create('label'),
            //TODO Icon
            name = L.DomUtil.create('span', '', label);

        name.innerText = world.displayName;
        input.id = label.htmlFor = `world-${world.name}`;
        input.type = 'radio';
        input.name = 'world';
        input.checked = false;
        input.addEventListener('click', async (e: MouseEvent) => {
            this._pl3xmap.setCurrentMap(world).catch(() => {
                e.preventDefault(); //Don't update radio button if switch fails
            });
        });

        this._worlds.set(world, {
            input,
            label,
            name,
        });

        this._skeleton.hidden = true;
        this._list.appendChild(input);
        this._list.appendChild(label);
    }

    private removeListItem(world: World) {
        const listItem = this._worlds.get(world);

        if (!listItem) {
            return;
        }

        listItem.label.remove();
        listItem.input.remove();
        this._worlds.delete(world);

        if (!this._worlds.size) {
            this._skeleton.hidden = false;
        }
    }

    onActivate() {
        if (this._worlds.size) {
            (this._list.querySelector('input:checked') as HTMLElement)!.focus()
        } else {
            this._skeleton.focus();
        }
    }
}
