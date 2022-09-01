import * as L from "leaflet";
import {MarkerOptions} from "./options/MarkerOptions";
import {Marker} from "./Marker";

export class Ellipse extends Marker {
    constructor(data: unknown[], options: MarkerOptions | undefined) {
        super(L.circle([0, 0]));
    }
}
