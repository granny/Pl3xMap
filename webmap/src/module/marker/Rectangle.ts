import * as L from "leaflet";
import {Util} from "../../util/Util";
import {MarkerOptions} from "./options/MarkerOptions";
import {Marker} from "./Marker";

export class Rectangle extends Marker {

    // [[0,0],[0,0]]

    constructor(data: unknown[], options: MarkerOptions | undefined) {
        const bounds = L.latLngBounds(
            Util.toLatLng(data[0] as L.PointTuple),
            Util.toLatLng(data[1] as L.PointTuple)
        );

        const marker = L.rectangle(bounds, {
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
