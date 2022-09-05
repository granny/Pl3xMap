import * as L from "leaflet";
import {Marker} from "./Marker";
import {MarkerOptions} from "./options/MarkerOptions";
import {Util} from "../../util/Util";

export class MultiPolygon extends Marker {

    // [[[[0,0],[0,0],[0,0]],[[0,0],[0,0],[0,0]]],[[[0,0],[0,0],[0,0]],[[0,0],[0,0],[0,0]]]]

    constructor(data: unknown[], options: MarkerOptions | undefined) {
        const polys = [];
        for (const shape of data as unknown[][][]) {
            const poly = [];
            for (const points of shape) {
                const line = [];
                for (const point of points) {
                    line.push(Util.toLatLng(point as L.PointTuple))
                }
                poly.push(line);
            }
            polys.push(poly);
        }

        const marker = L.polygon(polys, {
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
