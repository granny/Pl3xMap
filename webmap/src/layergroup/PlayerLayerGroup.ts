import {LayerGroup} from "leaflet";

export class PlayerLayerGroup extends LayerGroup {
    static create() {
        return new PlayerLayerGroup();
    }
}
