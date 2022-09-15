import * as L from "leaflet";
import {Marker, Type} from "./Marker";
import {pixelsToMeters, toLatLng} from "../util/Util";
import "../lib/L.ellipse";

export class Ellipse extends Marker {

    // [[0,0],[0,0],0]

    constructor(type: Type) {
        const data = type.data;
        const options = type.options;

        const center = data[0] as L.PointTuple;
        const radii = data[1] as L.PointTuple;

        super(L.ellipse(
            toLatLng(center),
            [
                pixelsToMeters(radii[0]),
                pixelsToMeters(radii[1])
            ],
            data[2] as number, {
                ...options?.properties
            })
        );
    }
}
