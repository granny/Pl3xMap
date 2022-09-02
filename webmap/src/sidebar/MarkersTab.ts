import {Pl3xMap} from "../Pl3xMap";
import {Util} from "../util/Util";
import BaseTab from "./BaseTab";
import '../svg/marker_point.svg';

export default class MarkersTab extends BaseTab {
    constructor(pl3xmap: Pl3xMap) {
        super(pl3xmap, 'markers');

        this._button.appendChild(Util.createSVGIcon('marker_point'));
        this._content.innerHTML = '<h2>//TODO</h2>'
    }
}
