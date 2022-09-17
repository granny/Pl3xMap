import {Pl3xMap} from "../Pl3xMap";
import {createSVGIcon} from "../util/Util";
import BaseTab from "./BaseTab";
import '../svg/marker_point.svg';

export default class MarkersTab extends BaseTab {
    constructor(pl3xmap: Pl3xMap) {
        super(pl3xmap, 'markers');

        const lang = pl3xmap.settings!.lang;

        this._button.appendChild(createSVGIcon('marker_point'));
        this._button.title = lang.markers.label;

        this._content.innerHTML = `<h2>${lang.markers.label}</h2>// TODO`;
    }
}
