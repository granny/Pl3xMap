import * as L from "leaflet";
import {Util} from "../../util/Util";
import {MarkerOptions} from "./options/MarkerOptions";
import {Marker} from "./Marker";
import "../../lib/L.ellipse";

export class Ellipse extends Marker {
    constructor(data: unknown[], options: MarkerOptions | undefined) {
        const center = data[0] as L.PointTuple;
        const radii = data[1] as L.PointTuple;
        const marker = L.ellipse(
            Util.toLatLng(center),
            [Util.pixelsToMeters(radii[0]), Util.pixelsToMeters(radii[1])],
            data[2] as number, {
                ...options?.properties
            });
        super(marker);
    }
}
