import * as L from "leaflet";
import {Marker, Type} from "./Marker";
import {Polyline} from "../util/Polyline";
import {getOrCreatePane, isset, toCenteredLatLng} from "../util/Util";

interface PolygonOptions extends L.PolylineOptions {
    key: string;
    polylines: Polyline[];
    pane: string;
}

export class Polygon extends Marker {
    constructor(type: Type) {
        const data = type.data as unknown as PolygonOptions;

        let options = {
            ...type.options?.properties,
            smoothFactor: 1.0,
            noClip: false,
            bubblingMouseEvents: true,
            interactive: true,
            attribution: undefined
        };

        if (isset(data.pane)) {
            const dom = getOrCreatePane(data.pane);
            options = {
                ...options,
                pane: dom.className.split("-")[1]
            };
        }

        super(data.key, L.polygon(Polygon.createPoly(data), options));
    }

    public update(raw: unknown[]): void {
        const data = raw as unknown as PolygonOptions;
        const polygon = this.marker as L.Polygon;
        polygon.setLatLngs(Polygon.createPoly(data));
    }

    private static createPoly(data: PolygonOptions): L.LatLng[][] {
        const poly = [];
        for (const polyline of data.polylines) {
            const line = [];
            for (const point of polyline.points) {
                line.push(toCenteredLatLng(point));
            }
            poly.push(line);
        }
        return poly;
    }
}
