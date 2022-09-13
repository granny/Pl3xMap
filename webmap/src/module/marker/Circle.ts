import * as L from "leaflet";
import {Util} from "../../util/Util";
import {Marker, Type} from "./Marker";

export class Circle extends Marker {

    // [[0,0],0]

    constructor(type: Type) {
        const data = type.data;
        const options = type.options;

        super(L.circle(
            Util.toLatLng(data[0] as L.PointTuple),
            {
                ...options?.properties,
                radius: Util.pixelsToMeters(data[1] as number),
                interactive: true,
                attribution: undefined
            })
        );
    }
}
