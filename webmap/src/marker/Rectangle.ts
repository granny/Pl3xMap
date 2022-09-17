import * as L from "leaflet";
import {Marker, Type} from "./Marker";
import {toCenteredLatLng} from "../util/Util";

export class Rectangle extends Marker {

    // [[0,0],[0,0]]

    constructor(type: Type) {
        const data = type.data;
        const options = type.options;

        super(L.rectangle(
            L.latLngBounds(
                toCenteredLatLng(data[0] as L.PointTuple),
                toCenteredLatLng(data[1] as L.PointTuple)
            ),
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
