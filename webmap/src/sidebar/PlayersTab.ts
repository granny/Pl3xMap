import {Pl3xMap} from "../Pl3xMap";
import {createSVGIcon, isset} from "../util/Util";
import BaseTab from "./BaseTab";
import '../svg/players.svg';

export default class PlayersTab extends BaseTab {
    constructor(pl3xmap: Pl3xMap) {
        super(pl3xmap, 'players');

        this._button.appendChild(createSVGIcon('players'));

        this.update()
    }

    public update(): void {
        let online = '?';
        if (isset(this._pl3xmap.settings?.players)) {
            online = String(Object.keys(this._pl3xmap.settings!.players).length);
        }

        let max = '?';
        if (isset(this._pl3xmap.settings?.maxPlayers)) {
            max = String(this._pl3xmap.settings!.maxPlayers);
        }

        const title = this._pl3xmap.settings?.lang.players?.label ?? 'Players';
        const counts = this._pl3xmap.settings?.lang.players?.value
                .replace('<online>', online)
                .replace('<max>', max)
            ?? 'Players';

        this._content.innerHTML = `<h2>${title} ${counts}</h2>// TODO`;
    }
}
