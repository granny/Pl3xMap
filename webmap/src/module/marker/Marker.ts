import * as L from "leaflet";
import {MarkerOptions} from "./options/MarkerOptions";

export abstract class Marker {
    private readonly _marker: L.Layer;

    protected constructor(marker: L.Layer) {
        this._marker = marker;
    }

    get marker(): L.Layer {
        return this._marker;
    }
}

export class Type {
    data: unknown[];
    options: MarkerOptions | undefined;

    constructor(data: unknown[], options: MarkerOptions | undefined) {
        this.data = data;
        this.options = options;
    }
}
