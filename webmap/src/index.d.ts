import {LatLngExpression, Layer, PathOptions, Path, PointTuple, Point, LatLngBounds, LatLng} from "leaflet";
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
    export function ellipse(latLng: LatLngExpression, radii: PointTuple, tilt: number, options: PathOptions): Ellipse;

    interface Ellipse extends Path {
        setRadius(radii: PointTuple): this;
        getRadius(): Point;
        setTilt(tilt: number): this;
        getBounds(): LatLngBounds;
        getLatLng(): LatLng;
        setLatLng(latLng: LatLngExpression): this;
    }
}