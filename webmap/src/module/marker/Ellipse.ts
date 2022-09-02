import * as L from "leaflet";
import {MarkerOptions} from "./options/MarkerOptions";
import {Marker} from "./Marker";
import "../../lib/L.ellipse";

export class Ellipse extends Marker {
    constructor(data: unknown[], options: MarkerOptions | undefined) {
        const center = data[0] as L.LatLngExpression;
        const radius = data[1] as L.PointTuple;
        const tilt = data[3] as number;

        const marker = L.ellipse(center, radius, tilt, {
           ...options?.properties
        });

        super(marker);
    }
}
