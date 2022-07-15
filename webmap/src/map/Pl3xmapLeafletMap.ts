import {CRS, DomUtil, LatLng, latLng, Map, point, Point, Transformation, Util} from "leaflet";
import {World} from "../module/World";
import {ReversedZoomTileLayer} from "../tilelayer/ReversedZoomTileLayer";
import {Pl3xMap} from "../Pl3xMap";

export default class Pl3xmapLeafletMap extends Map {
    declare _controlCorners: { [x: string]: HTMLDivElement; };
    declare _controlContainer?: HTMLElement;
    declare _container?: HTMLElement;

    private _pl3xmap: Pl3xMap;
    private _world: World | null = null;
    private _renderer: string = 'basic';
    private _tileLayer: ReversedZoomTileLayer | null = null;

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
        this.setView([0, 0], 0);
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
        return 1 / Math.pow(2, this.getMaxZoom());
    }

    centerOn(x: number, z: number, zoom: number) {
        this.setView(this.toLatLng(x, z), this.getMaxZoom() - zoom);
    }

    private updateTileLayer() {
        if(!this._world) {
            return;
        }

        if (this._tileLayer) {
            if (this._tileLayer.world !== this._world) {
                this.removeLayer(this._tileLayer!);
                this._tileLayer = new ReversedZoomTileLayer(this._world, this._renderer)
                    .setZIndex(0)
                    .addTo(this);
            } else if (this._tileLayer && (this._tileLayer.renderer !== this._renderer)) {
                this._tileLayer.renderer = this._renderer;
            }
        } else {
            this._tileLayer = new ReversedZoomTileLayer(this._world, this._renderer)
                .setZIndex(0)
                .addTo(this);
        }
    }

    get world(): World | null {
        return this._world;
    }

    set world(world: World | null) {
        if(!world) {
            return;
        }

        world.load().then(() => {
            this.setMaxZoom(world.zoom.maxOut ?? 0);

            // Use URL position on initial load
            if(!this._world) {
                this._renderer = this._pl3xmap.getUrlParam('renderer', world.renderers[0]);
                this.centerOn(
                    this._pl3xmap.getUrlParam('x', world.spawn.x),
                    this._pl3xmap.getUrlParam('z', world.spawn.z),
                    this._pl3xmap.getUrlParam('zoom', world.zoom.default)
                );
            } else {
                this._renderer = world.renderers[0];
                this.centerOn(world.spawn.x, world.spawn.z, world.zoom.default);
            }

            this._world = world;

            this.updateTileLayer();
        });
    }

    get renderer(): string {
        return this._renderer;
    }

    set renderer(renderer: string) {
        this._renderer = renderer;
        this.updateTileLayer();
    }
}
