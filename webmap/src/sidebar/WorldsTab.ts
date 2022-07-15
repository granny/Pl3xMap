import {createSVGIcon} from "../Util";

import '../svg/maps.svg';
import {DomUtil} from "leaflet";
import SidebarTab from "./SidebarTab";
import {Pl3xMap} from "../Pl3xMap";

export default class WorldsTab extends SidebarTab {

    constructor(pl3xmap: Pl3xMap) {
        super(pl3xmap, 'worlds');

        this._button.appendChild(createSVGIcon('maps'));
        this._button.setAttribute('aria-label','Worlds'); //TODO: Lang

        const heading = DomUtil.create('h2', '', this._content);
        heading.innerText = 'Worlds'; //TODO: Lang
    }

    onEnable() {

    }

    onDisable() {

    }
}
