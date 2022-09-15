import {Pl3xMap} from "../Pl3xMap";
import {createSVGIcon} from "../util/Util";
import BaseTab from "./BaseTab";
import '../svg/players.svg';

export default class PlayersTab extends BaseTab {
    constructor(pl3xmap: Pl3xMap) {
        super(pl3xmap, 'players');

        this._button.appendChild(createSVGIcon('players'));
        this._content.innerHTML = '<h2>//TODO</h2>'
    }
}
