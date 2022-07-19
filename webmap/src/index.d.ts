import {World} from "./module/World";
import {Layer} from "leaflet";

interface WorldRendererPair {
    world: World;
    renderer: string;
}

interface OverlayAddedPayload {
    layer: Layer;
    name: string;
    showInControl: boolean;
}

declare global {
  interface WindowEventMap {
    worldadded: CustomEvent<World>;
    worldremoved: CustomEvent<World>;
    mapchanged: CustomEvent<WorldRendererPair>;
    overlayadded: CustomEvent<OverlayAddedPayload>;
  }
}
