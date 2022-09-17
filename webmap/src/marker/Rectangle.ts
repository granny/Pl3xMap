import * as L from "leaflet";
import {Marker, Type} from "./Marker";
import {Point} from "../util/Point";
import {toCenteredLatLng} from "../util/Util";

interface RectangleOptions extends L.PolylineOptions {
    key: string;
    point1: Point;
    point2: Point;
}

export class Rectangle extends Marker {
    constructor(type: Type) {
        const data = type.data as unknown as RectangleOptions;

        super(data.key, L.rectangle(
            L.latLngBounds(
                toCenteredLatLng(data.point1),
                toCenteredLatLng(data.point2)
            ),
            {
                ...type.options?.properties,
                smoothFactor: 1.0,
                noClip: false,
                bubblingMouseEvents: true,
                interactive: true,
                attribution: undefined
            })
        );
    }

    public update(raw: unknown[]): void {
    }
}
