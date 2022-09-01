import * as L from "leaflet";
import {MarkerOptions} from "./options/MarkerOptions";
import {Marker} from "./Marker";

export class Polyline extends Marker {
    constructor(data: unknown[], options: MarkerOptions | undefined) {
        const line1 = [[0, 0], [100, 100], [0, 300]];
        const line2 = [[0, 0], [100, 100], [0, 300]];
        const lines = [line1, line2] as L.LatLngExpression[][];

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
