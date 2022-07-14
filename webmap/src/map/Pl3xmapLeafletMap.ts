import {CRS, DomUtil, Map, Transformation, Util} from "leaflet";

export default class Pl3xmapLeafletMap extends Map {
    declare _controlCorners: { [x: string]: HTMLDivElement; };
    declare _controlContainer?: HTMLElement;
    declare _container?: HTMLElement;

    constructor() {
        super('map', {
            // simple crs for custom map tiles
            crs: Util.extend(CRS.Simple, {
                // we need to flip the y-axis correctly
                // https://stackoverflow.com/a/62320569/3530727
                transformation: new Transformation(1, 0, 1, 0)
            }),
            // always 0,0 center
            center: [0, 0],
            // hides the leaflet attribution footer
            attributionControl: false,
            // canvas is faster than default svg
            preferCanvas: true
        });

        // always set center and zoom before doing anything else
        // this sets the internal "_loaded" value to true
        this.setView([0, 0], 0);
    }

    // https://stackoverflow.com/a/60391674/3530727
    // noinspection JSUnusedGlobalSymbols
    _initControlPos(): void {
        const corners: { [x: string]: HTMLDivElement; } = this._controlCorners = {},
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
