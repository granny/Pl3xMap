import * as L from "leaflet";
import {Marker, Type} from "./Marker";
import {toCenteredLatLng} from "../util/Util";

export class Polygon extends Marker {

    // [[0,0],[0,0],[0,0]]

    constructor(type: Type) {
        const data = type.data;
        const options = type.options;

        const poly = [];

        for (const points of data as unknown[][]) {
            const line = [];
            for (const point of points) {
                line.push(toCenteredLatLng(point as L.PointTuple))
            }
            poly.push(line);
        }

        super(L.polygon(
            poly,
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
