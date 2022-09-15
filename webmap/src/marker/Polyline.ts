import * as L from "leaflet";
import {Marker, Type} from "./Marker";
import {toLatLng} from "../util/Util";

export class Polyline extends Marker {

    // [[0,0],[0,0],[0,0]]

    constructor(type: Type) {
        const data = type.data;
        const options = type.options;

        const line = [];

        for (const point of data as unknown[]) {
            line.push(toLatLng(point as L.PointTuple))
        }

        super(L.polyline(
            line,
            {
                ...options?.properties,
                smoothFactor: 1.0,
                noClip: false,
                bubblingMouseEvents: true,
                interactive: true,
                attribution: undefined
            })
        );
    }
}
