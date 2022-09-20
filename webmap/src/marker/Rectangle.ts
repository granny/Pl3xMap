import * as L from "leaflet";
import {Marker, Type} from "./Marker";
import {Point} from "../util/Point";
import {getOrCreatePane, isset, toLatLngBounds} from "../util/Util";

interface RectangleOptions extends L.PolylineOptions {
    key: string;
    point1: Point;
    point2: Point;
    pane: string;
}

export class Rectangle extends Marker {
    constructor(type: Type) {
        const data = type.data as unknown as RectangleOptions;

        let options = {
            ...type.options?.properties,
            smoothFactor: 1.0,
            noClip: false,
            bubblingMouseEvents: true,
            interactive: true,
            attribution: undefined
        };

        if (isset(data.pane)) {
            const dom = getOrCreatePane(data.pane);
            options = {
                ...options,
                pane: dom.className.split("-")[1]
            };
        }

        super(data.key, L.rectangle(toLatLngBounds(data.point1, data.point2), options));
    }

    public update(raw: unknown[]): void {
        const data = raw as unknown as RectangleOptions;
        const rectangle = this.marker as L.Rectangle;
        rectangle.setBounds(toLatLngBounds(data.point1, data.point2));
    }
}
