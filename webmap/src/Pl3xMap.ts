import * as L from "leaflet";
import {LatLng, Point} from "leaflet";
import {CoordsControl} from "./control/CoordsControl";
import {LinkControl} from "./control/LinkControl";
import {PlayerLayerGroup} from "./layergroup/PlayerLayerGroup";
import {ReversedZoomTileLayer} from "./tilelayer/ReversedZoomTileLayer";
import {World} from "./module/World";
import {Lang} from "./module/Lang";
import {Options} from "./module/Options";
import {JSON, RootJSON, WorldJSON} from "./module/Json";

window.onload = function () {
    new Pl3xMap();
};

export class Pl3xMap {
    map: L.Map;
    options: Options = new Options();
    lang: Lang = new Lang();
    world: World = new World(this, null);

    constructor() {
        this.map = L.map('map', {
            // simple crs for custom map tiles
            crs: L.Util.extend(L.CRS.Simple, {
                // we need to flip the y-axis correctly
                // https://stackoverflow.com/a/62320569/3530727
                transformation: new L.Transformation(1, 0, 1, 0)
            }),
            // always 0,0 center
            center: [0, 0],
            // hides the leaflet attribution footer
            attributionControl: false,
            // canvas is faster than default svg
            preferCanvas: true
        });

        // set center and zoom first
        this.map.setView([0, 0], 0);

        this.getJSON('tiles/settings.json', (json: RootJSON) => this.init(json));
    }

    init(json: RootJSON): void {
        document.title = json.ui.lang.title;

        this.lang.coords = json.ui.lang.coords;
        this.lang.players = json.ui.lang.players;
        this.lang.worlds = json.ui.lang.worlds;

        this.options.ui.link = json.ui.link;
        this.options.ui.coords = json.ui.coords;

        // get world from url, or first world from json
        this.world = new World(this, json.worlds[0]);

        // center map on coords at zoom from url, or from json
        this.getJSON(`tiles/${this.world.name}/settings.json`,
            (json: WorldJSON) => {
                this.centerOn(
                    Number(this.getUrlParam('x', json.spawn.x)),
                    Number(this.getUrlParam('z', json.spawn.z)),
                    Number(this.getUrlParam('zoom', json.zoom.default))
                );
            });

        // the base layer for tiles
        ReversedZoomTileLayer.create(this).setZIndex(0).addTo(this.map);

        // player tracker layer
        const players = PlayerLayerGroup.create();
        players.setZIndex(100);
        players.addTo(this.map);

        // set up layer controls
        const layerControls = L.control.layers({}, {}, {position: 'topleft'});
        layerControls.addOverlay(players, 'Players');
        layerControls.addTo(this.map);

        // add the coords ui control box
        if (this.options.ui.coords) {
            CoordsControl.create(this).addTo(this.map);
        }

        // add the link ui control box
        if (this.options.ui.link) {
            LinkControl.create(this).addTo(this.map);
        }
    }

    toLatLng(x: number, z: number): LatLng {
        return L.latLng(this.pixelsToMeters(z), this.pixelsToMeters(x));
    }

    toPoint(latlng: LatLng): Point {
        return L.point(this.metersToPixels(latlng.lng), this.metersToPixels(-latlng.lat));
    }

    pixelsToMeters(num: number): number {
        return num * this.scale();
    }

    metersToPixels(num: number): number {
        return num / this.scale();
    }

    scale(): number {
        return 1 / Math.pow(2, this.options.zoom.maxZoom);
    }

    centerOn(x: number, z: number, zoom: number) {
        this.map.setView(this.toLatLng(x, z), this.options.zoom.maxZoom - zoom);
        return this.map;
    }

    getUrlParam(query: string, def: string): string {
        const url = window.location.search.substring(1);
        const vars = url.split('&');
        for (let i = 0; i < vars.length; i++) {
            const param = vars[i].split('=');
            if (param[0] === query) {
                const value = param[1] === undefined ? '' : decodeURIComponent(param[1]);
                return value === '' ? def : value;
            }
        }
        return def;
    }

    getUrlFromView(): string {
        const center: Point = this.toPoint(this.map.getCenter());
        const zoom: number = this.options.zoom.maxZoom - this.map.getZoom();
        const x: number = Math.floor(center.x);
        const z: number = Math.floor(-center.y);
        return `?world=${this.world?.name}&zoom=${zoom}&x=${x}&z=${z}`;
    }

    getJSON(url: string, fn: (json: JSON) => void) {
        fetch(url, {cache: "no-store"})
            .then(async res => {
                if (res.ok) {
                    fn(await res.json());
                }
            });
    }
}
