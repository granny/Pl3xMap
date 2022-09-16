import {Pl3xMap} from "../Pl3xMap";
import {createSVGIcon} from "../util/Util";
import BaseTab from "./BaseTab";
import '../svg/marker_point.svg';

export default class MarkersTab extends BaseTab {
    constructor(pl3xmap: Pl3xMap) {
        super(pl3xmap, 'markers');

        const markers = pl3xmap.settings?.lang.markers;

        this._button.appendChild(createSVGIcon('marker_point'));
        this._content.innerHTML = `<h2>${markers?.label ?? 'Markers'}</h2>// TODO`;
    }
}
