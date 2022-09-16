import * as L from "leaflet";
import {MarkerLayer} from "./layergroup/MarkerLayer";
import {World} from "./world/World";

declare global {
    interface WindowEventMap {
        worldadded: CustomEvent<World>;
        worldremoved: CustomEvent<World>;
        worldselected: CustomEvent<World>;
        rendererselected: CustomEvent<World>;
        overlayadded: CustomEvent<MarkerLayer>;
    }
}

module "leaflet" {
    export function ellipse(latLng: L.LatLngExpression, radii: L.PointTuple, tilt: number, options: L.PathOptions): Ellipse;

    interface Ellipse extends L.Path {
        setRadius(radii: L.PointTuple): this;

        getRadius(): L.Point;

        setTilt(tilt: number): this;

        getBounds(): L.LatLngBounds;

        getLatLng(): L.LatLng;

        setLatLng(latLng: L.LatLngExpression): this;
    }
}