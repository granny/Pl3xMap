import * as L from "leaflet";
import {Util} from "../../Util";
import {MarkerOptions} from "./options/MarkerOptions";
import {Marker} from "./Marker";

export class Rectangle extends Marker {

    // [[0, 0], [20, 20]]

    constructor(data: unknown[], options: MarkerOptions | undefined) {
        const point1 = L.point(data[0] as L.PointExpression);
        const point2 = L.point(data[1] as L.PointExpression);

        const bounds = L.latLngBounds(
            Util.toLatLng(point1.x, point1.y),
            Util.toLatLng(point2.x, point2.y)
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
