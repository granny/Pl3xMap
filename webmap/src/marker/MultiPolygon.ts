import * as L from "leaflet";
import {Marker, Type} from "./Marker";
import {Polygon} from "../util/Polygon";
import {toCenteredLatLng} from "../util/Util";

interface MultiPolygonOptions extends L.PolylineOptions {
    key: string;
    polygons: Polygon[];
}

export class MultiPolygon extends Marker {
    constructor(type: Type) {
        const data = type.data as unknown as MultiPolygonOptions;

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

        super(data.key, L.polygon(
            polys,
            {
                ...type.options?.properties,
                smoothFactor: 1.0,
                noClip: false,
                bubblingMouseEvents: true,
                interactive: true,
                attribution: undefined
            })
        );
    }

    public update(raw: unknown[]): void {
    }
}
