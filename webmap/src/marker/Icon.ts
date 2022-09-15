import * as L from "leaflet";
import {Marker, Type} from "./Marker";
import {isset, toLatLng} from "../util/Util";

export class Icon extends Marker {

    // [[0,0],"","",[0,0],[0,0],"","",[0,0],[0,0]]

    constructor(type: Type) {
        function url(image: string) {
            return `images/icon/registered/${image}.png`;
        }

        const data = type.data;
        const options = type.options;

        let props = {};

        if (isset(data[1])) props = {...props, iconUrl: url(data[1] as string)};
        if (isset(data[2])) props = {...props, iconRetinaUrl: url(data[2] as string)};
        if (isset(data[3])) props = {...props, iconSize: data[3] as L.PointTuple};
        if (isset(data[4])) props = {...props, iconAnchor: data[4] as L.PointTuple};
        if (isset(data[5])) props = {...props, shadowUrl: url(data[5] as string)};
        if (isset(data[6])) props = {...props, shadowRetinaUrl: url(data[6] as string)};
        if (isset(data[7])) props = {...props, shadowSize: data[7] as L.PointTuple};
        if (isset(data[8])) props = {...props, shadowAnchor: data[8] as L.PointTuple};

        const tooltipOffset = options?.tooltip?.properties?.offset;
        const popupOffset = options?.popup?.properties?.offset;

        if (isset(tooltipOffset)) props = {...props, tooltipAnchor: tooltipOffset as L.PointTuple};
        if (isset(popupOffset)) props = {...props, popupAnchor: popupOffset as L.PointTuple};

        super(L.marker(
            toLatLng(data[0] as L.PointTuple),
            {
                ...options?.properties,
                icon: L.icon(props as L.IconOptions),
                attribution: undefined
            })
        );
    }
}
