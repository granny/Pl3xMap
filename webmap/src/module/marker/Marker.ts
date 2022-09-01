import * as L from "leaflet";

export abstract class Marker {
    private readonly _marker: L.Layer;

    protected constructor(marker: L.Layer) {
        this._marker = marker;
    }

    get marker(): L.Layer {
        return this._marker;
    }
}
