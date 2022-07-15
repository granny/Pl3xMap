import {createSVGIcon} from "../Util";
import SidebarTab from "./SidebarTab";

import '../svg/players.svg';

export default class PlayersTab extends SidebarTab {
    constructor() {
        super('players');

        this._button.appendChild(createSVGIcon('players'));
        this._content.innerHTML = '<h2>//TODO</h2>'
    }

    onEnable() {

    }

    onDisable() {

    }
}
