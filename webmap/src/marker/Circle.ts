import * as L from "leaflet";
import {Marker, Type} from "./Marker";
import {Point} from "../util/Point";
import {getOrCreatePane, isset, pixelsToMeters, toCenteredLatLng} from "../util/Util";

interface CircleOptions extends L.PolylineOptions {
    key: string;
    center: Point;
    radius: number;
    pane: string;
}

export class Circle extends Marker {
    constructor(type: Type) {
        const data: CircleOptions = type.data as unknown as CircleOptions;

        let options = {
            ...type.options?.properties,
            radius: pixelsToMeters(data.radius),
            bubblingMouseEvents: true,
            interactive: true,
            attribution: undefined
        };

        if (isset(data.pane)) {
            const dom: HTMLElement = getOrCreatePane(data.pane);
            options = {
                ...options,
                pane: dom.className.split("-")[1]
            };
        }

        super(data.key, L.circle(toCenteredLatLng(data.center), options));
    }

    public update(raw: unknown[]): void {
        const data: CircleOptions = raw as unknown as CircleOptions;
        const circle: L.Circle = this.marker as L.Circle;
        circle.setLatLng(toCenteredLatLng(data.center));
        circle.setRadius(pixelsToMeters(data.radius));
    }
}
