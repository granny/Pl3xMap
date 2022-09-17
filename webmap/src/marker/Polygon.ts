import * as L from "leaflet";
import {Marker, Type} from "./Marker";
import {Polyline} from "../util/Polyline";
import {toCenteredLatLng} from "../util/Util";

interface PolylineOptions extends L.PolylineOptions {
    key: string;
    polylines: Polyline[];
}

export class Polygon extends Marker {
    constructor(type: Type) {
        const data = type.data as unknown as PolylineOptions;

        const poly = [];

        for (const polyline of data.polylines) {
            const line = [];
            for (const point of polyline.points) {
                line.push(toCenteredLatLng(point));
            }
            poly.push(line);
        }

        super(data.key, L.polygon(
            poly,
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
