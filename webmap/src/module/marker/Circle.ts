import * as L from "leaflet";
import {Util} from "../../Util";
import {MarkerOptions} from "./options/MarkerOptions";
import {Marker} from "./Marker";

export class Circle extends Marker {

    // [[0, 0], 10.0]

    constructor(data: unknown[], options: MarkerOptions | undefined) {
        const center = L.point(data[0] as L.PointExpression);
        const radius = data[1] as number;

        const marker = L.circle(Util.toLatLng(center.x, center.y), {
            ...options?.properties,
            radius: Util.pixelsToMeters(radius),
            interactive: true,
            attribution: undefined
        });

        super(marker);
    }
}
