import {createSVGIcon} from "../Util";
import SidebarTab from "./SidebarTab";

import '../svg/marker_point.svg';
import {Pl3xMap} from "../Pl3xMap";

export default class MarkersTab extends SidebarTab {
    constructor(pl3xmap: Pl3xMap) {
        super(pl3xmap,'markers');

        this._button.appendChild(createSVGIcon('marker_point'));
        this._content.innerHTML = '<h2>//TODO</h2>'
    }

    onEnable() {

    }

    onDisable() {

    }
}
