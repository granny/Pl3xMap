import * as L from "leaflet";
import {Util} from "../Util";
import {MarkerOptions} from "./MarkerOptions";

export class Markers {
    test(): void {
        const a = ["circ", [10, 10, 10.0], [[3, -65536], [0, 872349696], []]];
        const b = ["rect", [10, 10, 20, 20]];
        const c = ["rect", [10, 10, 20, 20], [[], [], [0, "testing 1 2 3", [0, 0]]]];
        const z = [a, b];
        this.parseMarker(a);
    }

    parseMarker(arr: unknown[]): L.Layer | undefined {
        const type = arr[0] as string;
        const data = arr[1] as unknown[];
        const options = arr.length > 2 ? new MarkerOptions(arr[2] as unknown[]) : undefined;

        const properties = options?.properties;
        const tooltip = options?.tooltip;

        let marker;

        switch (type) {
            case "circ":
                const x = data[0] as number;
                const z = data[1] as number;
                const radius = data[2] as number;
                marker = L.circle(Util.toLatLng(x, z), {...properties, radius: Util.pixelsToMeters(radius)});
                break;
            case "elli":
                break;
            case "icon":
                break;
            case "poly":
                break;
            case "line":
                break;
            case "rect":
                const point1 = Util.toLatLng(data[0] as number, data[1] as number);
                const point2 = Util.toLatLng(data[2] as number, data[3] as number);
                marker = L.rectangle(L.latLngBounds(point1, point2), {...properties});
                break;
        }

        if (marker && tooltip) {
            const options = {} as L.DivOverlayOptions;
            if (tooltip.offset) {
                options.offset = tooltip.offset;
            }
            if (tooltip.click) {
                marker.bindPopup(() => tooltip.string, {...options});
            } else {
                marker.bindTooltip(() => tooltip.string, {...options});
            }
        }

        return marker;
    }
}
