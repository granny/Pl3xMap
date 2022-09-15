import * as L from "leaflet";
import {Marker, Type} from "./Marker";
import {pixelsToMeters, toLatLng} from "../util/Util";

export class Circle extends Marker {

    // [[0,0],0]

    constructor(type: Type) {
        const data = type.data;
        const options = type.options;

        super(L.circle(
            toLatLng(data[0] as L.PointTuple),
            {
                ...options?.properties,
                radius: pixelsToMeters(data[1] as number),
                interactive: true,
                attribution: undefined
            })
        );
    }
}
