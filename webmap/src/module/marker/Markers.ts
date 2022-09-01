import * as L from "leaflet";
import {Pl3xMap} from "../../Pl3xMap";
import {MarkerOptions} from "./options/MarkerOptions";
import {Circle} from "./Circle";
import {Ellipse} from "./Ellipse";
import {Icon} from "./Icon";
import {Polygon} from "./Polygon";
import {Polyline} from "./Polyline";
import {Rectangle} from "./Rectangle";

export class Markers {
    private _types = {
        "circ": (data: unknown[], options: MarkerOptions | undefined) => new Circle(data, options),
        "elli": (data: unknown[], options: MarkerOptions | undefined) => new Ellipse(data, options),
        "icon": (data: unknown[], options: MarkerOptions | undefined) => new Icon(data, options),
        "poly": (data: unknown[], options: MarkerOptions | undefined) => new Polygon(data, options),
        "line": (data: unknown[], options: MarkerOptions | undefined) => new Polyline(data, options),
        "rect": (data: unknown[], options: MarkerOptions | undefined) => new Rectangle(data, options)
    }

    test(): void {
        const testMarkers = [
            ["circ", [[0, 0], 10.0], [[1, 3, -65536, 1, 1, null, null], [1, 1, 872349696], [], []]],
            ["rect", [[0, 0], [20, 20]]],
            ["rect", [[30, 30], [40, 40]], [[], [], [], ["test1", null, null, 300, 50, null, 1, null, null, null, 0, 1, 1, 1, 1]]],
            ["icon", [[0, 0], "test", null, null, [13, 41], null, null, "shadow", null, null, [13, 41]], [[], [], ["test2", null, null, 2, 0, 0, 0.9], []]],
            ["icon", [[20, 0], "test", null, null, [13, 41], null, null, "shadow", null, null, [13, 41]], [[], [], [], ["test3", null, null, 300, 50, null, 1, null, null, null, 0, 1, 1, 1, 1]]]
        ];
        for (let i = 0; i < testMarkers.length; i++) {
            this.parseMarker(testMarkers[i])?.addTo(Pl3xMap.getInstance().map);
        }
    }

    parseMarker(data: unknown[]): L.Layer | undefined {
        const options = data.length > 2 ? new MarkerOptions(data[2] as unknown[]) : undefined;

        const type = this._types[data[0] as keyof typeof this._types];
        const marker = type ? type(data[1] as unknown[], options).marker : undefined;

        if (marker) {
            const popup = options?.popup;
            const tooltip = options?.tooltip;
            if (popup) {
                marker.bindPopup(() => popup.content, popup.properties);
            }
            if (tooltip) {
                marker.bindTooltip(() => tooltip.content, tooltip.properties);
            }
        }

        return marker;
    }
}
