import * as L from "leaflet";
import {World} from "./module/World";

interface OverlayAddedPayload {
    layer: Layer;
    name: string;
    showInControl: boolean;
}

declare global {
    interface WindowEventMap {
        worldadded: CustomEvent<World>;
        worldremoved: CustomEvent<World>;
        worldselected: CustomEvent<World>;
        rendererselected: CustomEvent<string>;
        overlayadded: CustomEvent<OverlayAddedPayload>;
    }
}

module "leaflet" {
    export function ellipse(latLng: L.LatLngExpression, radii: L.LatLngExpression, tilt: number, options: L.PathOptions): Ellipse;

    interface Ellipse extends L.Path {
        setRadius(radii: L.LatLngExpression): this;
        getRadius(): L.PointTuple;
        setTilt(tilt: number): this;
        getBounds(): L.LatLngBounds;
        getLatLng(): L.LatLng;
        setLatLng(latLng: L.LatLngExpression): this;
    }
}