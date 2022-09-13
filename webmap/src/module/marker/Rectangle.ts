import * as L from "leaflet";
import {Util} from "../../util/Util";
import {Marker, Type} from "./Marker";

export class Rectangle extends Marker {

    // [[0,0],[0,0]]

    constructor(type: Type) {
        const data = type.data;
        const options = type.options;

        super(L.rectangle(
            L.latLngBounds(
                Util.toLatLng(data[0] as L.PointTuple),
                Util.toLatLng(data[1] as L.PointTuple)
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
