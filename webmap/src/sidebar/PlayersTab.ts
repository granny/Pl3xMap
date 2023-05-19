import * as L from "leaflet";
import {Pl3xMap} from "../Pl3xMap";
import {Player} from "../player/Player";
import {createSVGIcon, handleKeyboardEvent, isset} from "../util/Util";
import BaseTab from "./BaseTab";
import '../svg/players.svg';
import {Lang} from "../settings/Lang";
import {Settings} from "../settings/Settings";
import {PlayerManager} from "../player/PlayerManager";

interface PlayerListItem {
    input: HTMLInputElement;
    label: HTMLLabelElement;
}

export default class PlayersTab extends BaseTab {
    private readonly _players: Map<Player, PlayerListItem> = new Map();
    private readonly _skeleton: HTMLParagraphElement;
    private readonly _list: HTMLFieldSetElement;
    private readonly _heading: HTMLHeadingElement;

    constructor(pl3xmap: Pl3xMap) {
        super(pl3xmap, 'players');

        const lang: Lang = pl3xmap.settings!.lang;

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

    private initEvents(): void {
        addEventListener('playeradded', (e: CustomEvent<Player>): void => {
            this.createListItem(e.detail);
            this._update();
        });
        addEventListener('playerremoved', (e: CustomEvent<Player>): void => {
            this.removeListItem(e.detail);
            this._update();
        });
        addEventListener('followplayer', (e: CustomEvent<Player>): void => {
            this._players.forEach((item: PlayerListItem, player: Player): void => {
                item.input.checked = player === e.detail;
            });
        });

        this._list.addEventListener('keydown', (e: KeyboardEvent) =>
            handleKeyboardEvent(e, Array.from(this._list.elements) as HTMLElement[]))
    }

    private _update(): void {
        const settings: Settings | undefined = this._pl3xmap.settings;

        const online: string = String(isset(settings?.players) ? Object.keys(settings!.players).length : '???');
        const max: string = String(settings?.maxPlayers ?? '???');

        const title: any = settings?.lang.players?.label
                .replace('<online>', online)
                .replace('<max>', max)
            ?? 'Players';

        this._heading.innerText = title;
        this._button.title = title;
    }

    private createListItem(player: Player): void {
        const input: HTMLInputElement = L.DomUtil.create('input', 'players'),
            label: HTMLLabelElement = L.DomUtil.create('label', '');

        label.style.backgroundImage = `url('images/skins/3D/${player.uuid}.png')`;
        label.innerText = player.displayName;
        input.id = label.htmlFor = `${player.uuid}`;
        input.type = 'radio';
        input.name = 'player';
        input.checked = false;
        input.addEventListener('click', async (): Promise<void> => {
            const manager: PlayerManager = this._pl3xmap.playerManager;
            const player: Player | undefined = manager.players.get(input.id);
            if (player === manager.follow) {
                manager.follow = undefined;
            } else {
                manager.follow = player;
            }
            manager.updateFollow();
        });

        this._players.set(player, {
            input,
            label
        });

        this._skeleton.hidden = true;
        this._list.appendChild(input);
        this._list.appendChild(label);
    }

    private removeListItem(player: Player): void {
        const listItem: PlayerListItem | undefined = this._players.get(player);

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

    onActivate(): void {
        if (this._players.size) {
            (this._list.querySelector('input:checked') as HTMLElement)?.focus()
        } else {
            this._skeleton.focus();
        }
    }
}
