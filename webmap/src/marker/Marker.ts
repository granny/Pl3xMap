import * as L from "leaflet";
import {MarkerOptions} from "./options/MarkerOptions";

export abstract class Marker {
    private readonly _key: string;
    private readonly _marker: L.Layer;

    protected constructor(key: string, marker: L.Layer) {
        this._key = key;
        this._marker = marker;
    }

    get key(): string {
        return this._key;
    }

    get marker(): L.Layer {
        return this._marker;
    }

    abstract update(data: unknown[], options?: MarkerOptions): void;
}

export class Type {
    data: unknown[];
    options?: MarkerOptions;

    constructor(data: unknown[], options?: MarkerOptions) {
        this.data = data;
        this.options = options;
    }
}
