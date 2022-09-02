import * as L from "leaflet";
import {Util} from "../../util/Util";
import {MarkerOptions} from "./options/MarkerOptions";
import {Marker} from "./Marker";
import "../../lib/L.ellipse";

export class Ellipse extends Marker {
    constructor(data: unknown[], options: MarkerOptions | undefined) {
        const center = L.point(data[0] as L.PointExpression);
        const radii = data[1] as L.PointTuple;
        const tilt = data[2] as number;

        radii[0] = Util.pixelsToMeters(radii[0]);
        radii[1] = Util.pixelsToMeters(radii[1]);

        const marker = L.ellipse(Util.toLatLng(center.x, center.y), radii, tilt, {
            ...options?.properties
        });

        super(marker);
    }
}
