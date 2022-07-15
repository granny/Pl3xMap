import {createSVGIcon} from "../Util";

import '../svg/maps.svg';
import {DomUtil} from "leaflet";
import SidebarTab from "./SidebarTab";

export default class WorldsTab extends SidebarTab {

    constructor() {
        super('worlds');

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
