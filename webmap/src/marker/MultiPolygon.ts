import * as L from "leaflet";
import {Marker, Type} from "./Marker";
import {Polygon} from "../util/Polygon";
import {getOrCreatePane, isset, toCenteredLatLng} from "../util/Util";

interface MultiPolygonOptions extends L.PolylineOptions {
    key: string;
    polygons: Polygon[];
    pane: string;
}

export class MultiPolygon extends Marker {
    constructor(type: Type) {
        const data = type.data as unknown as MultiPolygonOptions;

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

        super(data.key, L.polygon(MultiPolygon.createPolys(data), options));
    }

    public update(raw: unknown[]): void {
        const data = raw as unknown as MultiPolygonOptions;
        const polygon = this.marker as L.Polygon;
        polygon.setLatLngs(MultiPolygon.createPolys(data));
    }

    private static createPolys(data: MultiPolygonOptions): L.LatLng[][][] {
        const polys = [];
        for (const polygon of data.polygons) {
            const poly = [];
            for (const polyline of polygon.polylines) {
                const line = [];
                for (const point of polyline.points) {
                    line.push(toCenteredLatLng(point))
                }
                poly.push(line);
            }
            polys.push(poly);
        }
        return polys;
    }
}
