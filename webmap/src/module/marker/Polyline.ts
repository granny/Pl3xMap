import * as L from "leaflet";
import {Util} from "../../util/Util";
import {MarkerOptions} from "./options/MarkerOptions";
import {Marker} from "./Marker";

export class Polyline extends Marker {

    // [[[0,0],[100,100],[100,200],[200,150],[300,300]]]

    constructor(data: unknown[], options: MarkerOptions | undefined) {
        const lines = [];
        for (const points of data as unknown[][]) {
            const line = [];
            for (const point of points) {
                line.push(Util.toLatLng(point as L.PointTuple))
            }
            lines.push(line);
        }

        const marker = L.polyline(lines, {
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
