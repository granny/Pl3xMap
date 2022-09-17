import * as L from "leaflet";
import {Marker, Type} from "./Marker";
import {Point} from "../util/Point";
import {toCenteredLatLng} from "../util/Util";

interface PolylineOptions extends L.PolylineOptions {
    key: string;
    points: Point[];
}

export class Polyline extends Marker {
    constructor(type: Type) {
        const data = type.data as unknown as PolylineOptions;

        const line = [];

        for (const point of data.points) {
            line.push(toCenteredLatLng(point))
        }

        super(data.key, L.polyline(
            line,
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
