import * as L from "leaflet";
import {Util} from "../../util/Util";
import {Marker, Type} from "./Marker";

export class Polyline extends Marker {

    // [[0,0],[0,0],[0,0]]

    constructor(type: Type) {
        const data = type.data;
        const options = type.options;

        const line = [];

        for (const point of data as unknown[]) {
            line.push(Util.toLatLng(point as L.PointTuple))
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
