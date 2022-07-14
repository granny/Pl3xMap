import {createSVGIcon} from "../Util";
import SidebarTab from "./SidebarTab";

import '../svg/maps.svg';

export default class MapsTab extends SidebarTab {
    constructor() {
        super();

        this._button.appendChild(createSVGIcon('maps'));
        this._content.innerHTML = '<h2>//TODO</h2>'
    }

    onEnable() {

    }

    onDisable() {

    }
}