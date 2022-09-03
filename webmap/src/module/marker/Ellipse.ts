import * as L from "leaflet";
import {Util} from "../../util/Util";
import {MarkerOptions} from "./options/MarkerOptions";
import {Marker} from "./Marker";
import "../../lib/L.ellipse";

export class Ellipse extends Marker {
    constructor(data: unknown[], options: MarkerOptions | undefined) {
        const marker = L.ellipse(
            Util.toLatLng(data[0] as L.PointTuple),
            Util.toLatLng(data[1] as L.PointTuple),
            data[2] as number, {
                ...options?.properties
            });
        super(marker);
    }
}
