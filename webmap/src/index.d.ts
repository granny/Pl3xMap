import {Layer} from "leaflet";
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
