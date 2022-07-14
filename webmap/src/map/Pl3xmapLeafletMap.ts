import {DomUtil, Map, MapOptions} from "leaflet";

export default class Pl3xmapLeafletMap extends Map {
    declare _controlCorners: any;
    declare _controlContainer?: HTMLElement;
    declare _container?: HTMLElement;

    constructor(element: string | HTMLElement, options?: MapOptions) {
        super(element, options);
    }

    // noinspection JSUnusedGlobalSymbols
    _initControlPos() {
        const corners: any = this._controlCorners = {},
            l = 'leaflet-',
            container = this._controlContainer =
                DomUtil.create('div', l + 'control-container', this._container),
            topContainer = DomUtil.create('div', l + 'control-container-top', container),
            bottomContainer = DomUtil.create('div', l + 'control-container-bottom', container);

        function createCorner(vSide: string, hSide: string) {
            const className = l + vSide + ' ' + l + hSide;

            corners[`${vSide}${hSide}`] = DomUtil.create('div', className, vSide === 'top' ? topContainer : bottomContainer);
        }

        createCorner('top', 'left');
        createCorner('top', 'center');
        createCorner('top', 'right');
        createCorner('bottom', 'left');
        createCorner('bottom', 'center');
        createCorner('bottom', 'right');
    }
}
