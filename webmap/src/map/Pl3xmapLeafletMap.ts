import * as L from "leaflet";
import {Pl3xMap} from "../Pl3xMap";
import {Util} from "../util/Util";
import {World} from "../module/World";

export default class Pl3xmapLeafletMap extends L.Map {
    declare _controlCorners: { [x: string]: HTMLDivElement; };
    declare _controlContainer?: HTMLElement;
    declare _container?: HTMLElement;

    private _pl3xmap: Pl3xMap;
    private _world: World | null = null;
    private _rendererType: string = 'basic';

    constructor(pl3xmap: Pl3xMap) {
        super('map', {
            // simple crs for custom map tiles
            crs: L.Util.extend(L.CRS.Simple, {
                // we need to flip the y-axis correctly
                // https://stackoverflow.com/a/62320569/3530727
                transformation: new L.Transformation(1, 0, 1, 0)
            }),
            // always 0,0 center
            center: [0, 0],
            // show the attribution footer
            attributionControl: true,
            // canvas is faster than default svg
            preferCanvas: true
        });

        // sets the leaflet attribution prefix to our project page
        this.attributionControl.setPrefix("<a href='https://modrinth.com/plugin/pl3xmap/'>Pl3xMap &copy; 2022</a>");

        this._pl3xmap = pl3xmap;
    }

    // https://stackoverflow.com/a/60391674/3530727
    // noinspection JSUnusedGlobalSymbols
    _initControlPos(): void {
        this._controlContainer = L.DomUtil.create('div', 'leaflet-control-container', this._container);

        const corners: { [x: string]: HTMLDivElement; } = this._controlCorners = {},
            top = L.DomUtil.create('div', 'leaflet-control-container-top', this._controlContainer),
            bottom = L.DomUtil.create('div', 'leaflet-control-container-bottom', this._controlContainer);

        function createCorner(vSide: string, hSide: string) {
            corners[`${vSide}${hSide}`] = L.DomUtil.create('div', `leaflet-${vSide} leaflet-${hSide}`, vSide === 'top' ? top : bottom);
        }

        createCorner('top', 'left');
        createCorner('top', 'center');
        createCorner('top', 'right');
        createCorner('bottom', 'left');
        createCorner('bottom', 'center');
        createCorner('bottom', 'right');
    }

    centerOn(x: number, z: number, zoom: number) {
        this.setView(Util.toLatLng([x, z]), this.getMaxZoomOut() - zoom);
    }

    getMaxZoomOut(): number {
        return this.world?.zoom.maxOut ?? 0;
    }

    getCurrentZoom(): number {
        return this.getMaxZoomOut() - this.getZoom();
    }

    get world(): World | null {
        return this._world;
    }

    set world(world: World | null) {
        this._world = world;
    }

    get rendererType(): string {
        return this._rendererType;
    }
}
