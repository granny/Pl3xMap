import * as L from "leaflet";
import {Pl3xMap} from "../Pl3xMap";
import {Player} from "../player/Player";
import {createSVGIcon, handleKeyboardEvent, isset} from "../util/Util";
import BaseTab from "./BaseTab";
import '../svg/players.svg';

interface PlayerListItem {
    input: HTMLInputElement;
    label: HTMLLabelElement;
    name: HTMLSpanElement;
}

export default class PlayersTab extends BaseTab {
    private readonly _players: Map<Player, PlayerListItem> = new Map();
    private readonly _skeleton: HTMLParagraphElement;
    private readonly _list: HTMLFieldSetElement;
    private readonly _heading: HTMLHeadingElement;

    constructor(pl3xmap: Pl3xMap) {
        super(pl3xmap, 'players');

        const lang = pl3xmap.settings!.lang;

        this._button.appendChild(createSVGIcon('players'));
        this._button.setAttribute('aria-label', lang.worlds.label);
        this._button.title = lang.players.label;

        this._heading = L.DomUtil.create('h2', '', this._content);
        this._heading.innerText = lang.players.label;
        this._heading.id = 'players-heading';

        this._skeleton = L.DomUtil.create('p', '', this._content);
        this._skeleton.innerText = lang.players.value;
        this._skeleton.tabIndex = -1;

        this._list = L.DomUtil.create('fieldset', 'menu', this._content);
        this._list.setAttribute('aria-labelledby', 'players-heading');
        this._list.setAttribute('role', 'radiogroup');

        this.initEvents();

        this._update();
    }

    private initEvents() {
        addEventListener('playeradded', (e: CustomEvent<Player>) => {
            this.createListItem(e.detail);
            this._update();
        });
        addEventListener('playerremoved', (e: CustomEvent<Player>) => {
            this.removeListItem(e.detail);
            this._update();
        });
        addEventListener('followplayer', (e: CustomEvent<Player>) => {
            this._players.forEach((item: PlayerListItem, player: Player) => {
                item.input.checked = player === e.detail;
            });
        });

        this._list.addEventListener('keydown', (e: KeyboardEvent) =>
            handleKeyboardEvent(e, Array.from(this._list.elements) as HTMLElement[]))
    }

    private _update(): void {
        const settings = this._pl3xmap.settings;

        const online = String(isset(settings?.players) ? Object.keys(settings!.players).length : '???');
        const max = String(settings?.maxPlayers ?? '???');

        const title = settings?.lang.players?.label
                .replace('<online>', online)
                .replace('<max>', max)
            ?? 'Players';

        this._heading.innerText = title;
        this._button.title = title;
    }

    private createListItem(player: Player) {
        const input = L.DomUtil.create('input'),
            label = L.DomUtil.create('label'),
            name = L.DomUtil.create('span', '', label);

        name.innerText = player.displayName;
        input.id = label.htmlFor = `${player.uuid}`;
        input.type = 'radio';
        input.name = 'player';
        input.checked = false;
        input.addEventListener('click', async () => {
            const manager = this._pl3xmap.playerManager;
            const player = manager.players.get(input.id);
            if (player === manager.follow) {
                manager.follow = undefined;
            } else {
                manager.follow = player;
            }
            manager.updateFollow();
        });

        this._players.set(player, {
            input,
            label,
            name,
        });

        this._skeleton.hidden = true;
        this._list.appendChild(input);
        this._list.appendChild(label);
    }

    private removeListItem(player: Player) {
        const listItem = this._players.get(player);

        if (!listItem) {
            return;
        }

        listItem.label.remove();
        listItem.input.remove();
        this._players.delete(player);

        if (!this._players.size) {
            this._skeleton.hidden = false;
        }
    }

    onActivate() {
        if (this._players.size) {
            (this._list.querySelector('input:checked') as HTMLElement)?.focus()
        } else {
            this._skeleton.focus();
        }
    }
}
