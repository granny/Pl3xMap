import * as L from "leaflet";
import {Marker, Type} from "./Marker";
import {Point} from "../util/Point";
import {getOrCreatePane, isset, pixelsToMeters, toCenteredLatLng} from "../util/Util";
import "../lib/L.ellipse";

interface EllipseOptions extends L.PolylineOptions {
    key: string;
    center: Point;
    radius: Point;
    tilt?: number;
    pane: string;
}

export class Ellipse extends Marker {
    constructor(type: Type) {
        const data = type.data as unknown as EllipseOptions;

        let options = {
            ...type.options?.properties,
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

        super(data.key, L.ellipse(
            toCenteredLatLng(data.center),
            [
                pixelsToMeters(data.radius.x),
                pixelsToMeters(data.radius.z)
            ],
            isset(data.tilt) ? data.tilt! : 0,
            options)
        );
    }

    public update(raw: unknown[]): void {
        const data = raw as unknown as EllipseOptions;
        const ellipse = this.marker as L.Ellipse;
        ellipse.setLatLng(toCenteredLatLng(data.center));
        ellipse.setRadius([
            pixelsToMeters(data.radius.x),
            pixelsToMeters(data.radius.z)
        ]);
        if (isset(data.tilt)) {
            ellipse.setTilt(data.tilt!);
        }
    }
}
