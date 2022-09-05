import * as L from "leaflet";
import {Util} from "../../util/Util";
import {MarkerOptions} from "./options/MarkerOptions";
import {Marker} from "./Marker";

export class Polyline extends Marker {

    // [[0,0],[0,0],[0,0]]

    constructor(data: unknown[], options: MarkerOptions | undefined) {
        const line = [];
        for (const point of data as unknown[]) {
            line.push(Util.toLatLng(point as L.PointTuple))
        }

        const marker = L.polyline(line, {
            ...options?.properties,
            smoothFactor: 1.0,
            noClip: false,
            bubblingMouseEvents: true,
            interactive: true,
            attribution: undefined
        });

        super(marker);
    }
}
