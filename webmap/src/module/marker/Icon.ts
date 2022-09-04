import * as L from "leaflet";
import {Util} from "../../util/Util";
import {MarkerOptions} from "./options/MarkerOptions";
import {Marker} from "./Marker";

export class Icon extends Marker {

    // [[0, 0], "iconUrl", null, null, null, null, null, null, null, null, null]

    constructor(data: unknown[], options: MarkerOptions | undefined) {
        function url(image: string) {
            return `images/icon/registered/${image}.png`;
        }

        let props = {};

        if (Util.isset(data[1])) props = {...props, iconUrl: url(data[1] as string)};
        if (Util.isset(data[2])) props = {...props, iconRetinaUrl: url(data[2] as string)};
        if (Util.isset(data[3])) props = {...props, iconSize: data[3] as L.PointTuple};
        if (Util.isset(data[4])) props = {...props, iconAnchor: data[4] as L.PointTuple};
        if (Util.isset(data[5])) props = {...props, shadowUrl: url(data[5] as string)};
        if (Util.isset(data[6])) props = {...props, shadowRetinaUrl: url(data[6] as string)};
        if (Util.isset(data[7])) props = {...props, shadowSize: data[7] as L.PointTuple};
        if (Util.isset(data[8])) props = {...props, shadowAnchor: data[8] as L.PointTuple};

        const tooltipOffset = options?.tooltip?.properties?.offset;
        const popupOffset = options?.popup?.properties?.offset;

        if (Util.isset(tooltipOffset)) props = {...props, tooltipAnchor: tooltipOffset as L.PointTuple};
        if (Util.isset(popupOffset)) props = {...props, popupAnchor: popupOffset as L.PointTuple};

        const marker = L.marker(Util.toLatLng(data[0] as L.PointTuple), {
            ...options?.properties,
            icon: L.icon(props as L.IconOptions),
            attribution: undefined
        });

        super(marker);
    }
}
