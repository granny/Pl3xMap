import * as L from "leaflet";
import {Marker, Type} from "./Marker";
import {Polyline} from "../util/Polyline";
import {getOrCreatePane, isset, toCenteredLatLng} from "../util/Util";

interface MultiPolylineOptions extends L.PolylineOptions {
    key: string;
    polylines: Polyline[];
    pane: string;
}

export class MultiPolyline extends Marker {
    constructor(type: Type) {
        const data = type.data as unknown as MultiPolylineOptions;

        const lines = [];

        for (const polylines of data.polylines) {
            const line = [];
            for (const point of polylines.points) {
                line.push(toCenteredLatLng(point))
            }
            lines.push(line);
        }

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

        super(data.key, L.polyline(lines, options));
    }

    public update(raw: unknown[]): void {
    }
}
