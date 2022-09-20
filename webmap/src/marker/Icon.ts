import * as L from "leaflet";
import {Marker, Type} from "./Marker";
import {MarkerOptions} from "./options/MarkerOptions";
import {Point} from "../util/Point";
import {getOrCreatePane, isset, toCenteredLatLng} from "../util/Util";
import "../lib/L.rotated";

interface IconOptions extends L.MarkerOptions {
    key: string;
    point: Point;
    image: string;
    retina?: string;
    size?: Point;
    anchor?: Point;
    shadow?: string;
    shadowRetina?: string;
    shadowSize?: Point;
    shadowAnchor?: Point;
    rotationAngle?: number;
    rotationOrigin?: string;
    pane: string;
}

export class Icon extends Marker {
    constructor(type: Type) {
        function url(image: string) {
            return `images/icon/registered/${image}.png`;
        }

        const data = type.data as unknown as IconOptions;

        let props = {};
        if (isset(data.image)) props = {...props, iconUrl: url(data.image)};
        if (isset(data.retina)) props = {...props, iconRetinaUrl: url(data.retina!)};
        if (isset(data.size)) props = {...props, iconSize: [data.size!.x, data.size!.z]};
        if (isset(data.anchor)) props = {...props, iconAnchor: [data.anchor!.x, data.anchor!.z]};
        if (isset(data.shadow)) props = {...props, shadowUrl: url(data.shadow!)};
        if (isset(data.shadowRetina)) props = {...props, shadowRetinaUrl: url(data.shadowRetina!)};
        if (isset(data.shadowSize)) props = {...props, shadowSize: [data.shadowSize!.x, data.shadowSize!.z]};
        if (isset(data.shadowAnchor)) props = {...props, shadowAnchor: [data.shadowAnchor!.x, data.shadowAnchor!.z]};

        const tooltipOffset = type.options?.tooltip?.properties?.offset;
        const popupOffset = type.options?.popup?.properties?.offset;

        if (isset(tooltipOffset)) props = {...props, tooltipAnchor: tooltipOffset};
        if (isset(popupOffset)) props = {...props, popupAnchor: popupOffset};

        let options = {
            ...type.options?.properties,
            rotationAngle: data.rotationAngle,
            rotationOrigin: data.rotationOrigin,
            icon: L.icon(props as L.IconOptions),
            bubblingMouseEvents: true,
            interactive: true,
            attribution: undefined
        } as IconOptions;

        if (isset(data.pane)) {
            const dom = getOrCreatePane(data.pane);
            options = {
                ...options,
                pane: dom.className.split(" ")[1].split("-")[1]
            };
        }

        super(data.key, L.marker(toCenteredLatLng(data.point), options));
    }

    public update(raw: unknown[], options?: MarkerOptions): void {
        const data = raw as unknown as IconOptions;
        const icon = this.marker as L.Marker;
        icon.setLatLng(toCenteredLatLng(data.point));
        const iconOptions = icon.options as IconOptions;
        iconOptions.rotationAngle = data.rotationAngle;
        iconOptions.rotationOrigin = data.rotationOrigin;
        if (options?.tooltip?.content) {
            icon.setTooltipContent(options?.tooltip?.content);
        }
    }
}
