import * as L from "leaflet";
import {Util} from "../../util/Util";
import {MarkerOptions} from "./options/MarkerOptions";
import {Marker} from "./Marker";

export class Circle extends Marker {

    // [[0, 0], 10.0]

    constructor(data: unknown[], options: MarkerOptions | undefined) {
        const marker = L.circle(Util.toLatLng(data[0] as L.PointTuple), {
            ...options?.properties,
            radius: Util.pixelsToMeters(data[1] as number),
            interactive: true,
            attribution: undefined
        });
        super(marker);
    }
}
