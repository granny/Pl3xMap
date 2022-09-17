import * as L from "leaflet";
import {Marker, Type} from "./Marker";
import {Point} from "../util/Point";
import {pixelsToMeters, toCenteredLatLng} from "../util/Util";

interface CircleOptions extends L.PolylineOptions {
    key: string;
    center: Point;
    radius: number;
}

export class Circle extends Marker {
    constructor(type: Type) {
        const data = type.data as unknown as CircleOptions;

        super(data.key, L.circle(
            toCenteredLatLng(data.center),
            {
                ...type.options?.properties,
                radius: pixelsToMeters(data.radius),
                interactive: true,
                attribution: undefined
            })
        );
    }

    public update(raw: unknown[]): void {
        const data = raw as unknown as CircleOptions;
        const circle = this.marker as L.Circle;
        circle.setLatLng(toCenteredLatLng(data.center));
        circle.setRadius(pixelsToMeters(data.radius));
    }
}
