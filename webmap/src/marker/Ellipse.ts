import * as L from "leaflet";
import {Marker, Type} from "./Marker";
import {Point} from "../util/Point";
import {isset, pixelsToMeters, toCenteredLatLng} from "../util/Util";
import "../lib/L.ellipse";

interface EllipseOptions extends L.PolylineOptions {
    key: string;
    center: Point;
    radius: Point;
    tilt?: number;
}

export class Ellipse extends Marker {
    constructor(type: Type) {
        const data = type.data as unknown as EllipseOptions;

        super(data.key, L.ellipse(
            toCenteredLatLng(data.center),
            [
                pixelsToMeters(data.radius.x),
                pixelsToMeters(data.radius.z)
            ],
            isset(data.tilt) ? data.tilt! : 0,
            {
                ...type.options?.properties
            })
        );
    }

    public update(raw: unknown[]): void {
    }
}
