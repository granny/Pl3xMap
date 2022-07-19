import {createSVGIcon} from "../Util";

import '../svg/players.svg';
import {Pl3xMap} from "../Pl3xMap";
import BaseTab from "./BaseTab";

export default class PlayersTab extends BaseTab {
    constructor(pl3xmap: Pl3xMap) {
        super(pl3xmap, 'players');

        this._button.appendChild(createSVGIcon('players'));
        this._content.innerHTML = '<h2>//TODO</h2>'
    }
}
