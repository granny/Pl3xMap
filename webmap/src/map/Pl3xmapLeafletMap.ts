import {CRS, DomUtil, LatLng, latLng, Map, point, Point, Transformation, Util} from "leaflet";
import {World} from "../module/World";
import {Pl3xMap} from "../Pl3xMap";

export default class Pl3xmapLeafletMap extends Map {
    declare _controlCorners: { [x: string]: HTMLDivElement; };
    declare _controlContainer?: HTMLElement;
    declare _container?: HTMLElement;

    private _pl3xmap: Pl3xMap;
    private _world: World | null = null;
    private _renderer: string = 'basic';

    constructor(pl3xmap: Pl3xMap) {
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

        this._pl3xmap = pl3xmap;

        // always set center and zoom before doing anything else
        // this sets the internal "_loaded" value to true
        //this.setView([0, 0], 0);
    }

    // https://stackoverflow.com/a/60391674/3530727
    // noinspection JSUnusedGlobalSymbols
    _initControlPos(): void {
        this._controlContainer = DomUtil.create('div', 'leaflet-control-container', this._container);

        const corners: { [x: string]: HTMLDivElement; } = this._controlCorners = {},
            topContainer = DomUtil.create('div', 'leaflet-control-container-top', this._controlContainer),
            bottomContainer = DomUtil.create('div', 'leaflet-control-container-bottom', this._controlContainer);

        function createCorner(vSide: string, hSide: string) {
            const className = `leaflet-${vSide} leaflet-${hSide}`,
                container = vSide === 'top' ? topContainer : bottomContainer;
            corners[`${vSide}${hSide}`] = DomUtil.create('div', className, container);
        }

        createCorner('top', 'left');
        createCorner('top', 'center');
        createCorner('top', 'right');
        createCorner('bottom', 'left');
        createCorner('bottom', 'center');
        createCorner('bottom', 'right');
    }

    toLatLng(x: number, z: number): LatLng {
        return latLng(this.pixelsToMeters(z), this.pixelsToMeters(x));
    }

    toPoint(latlng: LatLng): Point {
        return point(this.metersToPixels(latlng.lng), this.metersToPixels(latlng.lat));
    }

    pixelsToMeters(num: number): number {
        return num * this.scale();
    }

    metersToPixels(num: number): number {
        return num / this.scale();
    }

    scale(): number {
        return 1 / Math.pow(2, this.getMaxZoomOut());
    }

    centerOn(x: number, z: number, zoom: number) {
        this.setView(this.toLatLng(x, z), this.getMaxZoomOut() - zoom);
    }

    getMaxZoomOut(): number {
        return this._world?.zoom.maxOut ?? 0;
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

    get renderer(): string {
        return this._renderer;
    }
}
