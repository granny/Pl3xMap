import * as L from "leaflet";
import {Marker, Type} from "./Marker";
import {Polyline} from "../util/Polyline";
import {toCenteredLatLng} from "../util/Util";

interface MultiPolylineOptions extends L.PolylineOptions {
    key: string;
    polylines: Polyline[];
}

export class MultiPolyline extends Marker {
    constructor(type: Type) {
        const data = type.data as unknown as MultiPolylineOptions;

        const lines = [];

        for (const polylines of data.polylines) {
            const line = [];
            for (const point of polylines.points) {
                line.push(toCenteredLatLng(point))
            }
            lines.push(line);
        }

        super(data.key, L.polyline(
            lines,
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
