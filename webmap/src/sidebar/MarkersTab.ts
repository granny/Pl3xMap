import {createSVGIcon} from "../Util";

import '../svg/marker_point.svg';
import {Pl3xMap} from "../Pl3xMap";
import BaseTab from "./BaseTab";

export default class MarkersTab extends BaseTab {
    constructor(pl3xmap: Pl3xMap) {
        super(pl3xmap, 'markers');

        this._button.appendChild(createSVGIcon('marker_point'));
        this._content.innerHTML = '<h2>//TODO</h2>'
    }
}
