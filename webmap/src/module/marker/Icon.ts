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

        const center = L.point(data[0] as L.PointExpression);

        let iconProps = {};

        if (Util.isset(data[1])) iconProps = {...iconProps, iconUrl: url(data[1] as string)};
        if (Util.isset(data[2])) iconProps = {...iconProps, iconRetinaUrl: url(data[2] as string)};
        if (Util.isset(data[3])) iconProps = {...iconProps, iconSize: data[3] as L.PointExpression};
        if (Util.isset(data[4])) iconProps = {...iconProps, iconAnchor: data[4] as L.PointExpression};
        if (Util.isset(data[5])) iconProps = {...iconProps, popupAnchor: data[5] as L.PointExpression};
        if (Util.isset(data[6])) iconProps = {...iconProps, tooltipAnchor: data[6] as L.PointExpression};
        if (Util.isset(data[7])) iconProps = {...iconProps, shadowUrl: url(data[7] as string)};
        if (Util.isset(data[8])) iconProps = {...iconProps, shadowRetinaUrl: url(data[8] as string)};
        if (Util.isset(data[9])) iconProps = {...iconProps, shadowSize: data[9] as L.PointExpression};
        if (Util.isset(data[10])) iconProps = {...iconProps, shadowAnchor: data[10] as L.PointExpression};

        const marker = L.marker(Util.toLatLng(center.x, center.y), {
            ...options?.properties,
            icon: L.icon(iconProps as L.IconOptions),
            attribution: undefined
        });
        super(marker);
    }
}
