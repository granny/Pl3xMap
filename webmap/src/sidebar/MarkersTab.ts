import {createSVGIcon} from "../Util";
import SidebarTab from "./SidebarTab";

import '../svg/marker_point.svg';

export default class MarkersTab extends SidebarTab {
    constructor() {
        super();

        this._button.appendChild(createSVGIcon('marker_point'));
        this._content.innerHTML = '<h2>//TODO</h2>'
    }

    onEnable() {

    }

    onDisable() {

    }
}