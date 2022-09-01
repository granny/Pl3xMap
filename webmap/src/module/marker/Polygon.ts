import * as L from "leaflet";
import {Marker} from "./Marker";
import {MarkerOptions} from "./options/MarkerOptions";

export class Polygon extends Marker {
    constructor(data: unknown[], options: MarkerOptions | undefined) {
        const poly1 = [[0, 0], [100, 100], [0, 300]];
        const poly2 = [[0, 0], [100, 100], [0, 300]];
        const polys = [poly1, poly2] as L.LatLngExpression[][];

        const marker = L.polygon(polys, {
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
