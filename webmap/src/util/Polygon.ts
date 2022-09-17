import {Polyline} from "./Polyline";

export class Polygon {
    private readonly _key: string;
    private readonly _polylines: Polyline[];

    constructor(key: string, polylines: Polyline[]) {
        this._key = key;
        this._polylines = polylines;
    }

    get key(): string {
        return this._key;
    }

    get polylines(): Polyline[] {
        return this._polylines;
    }
}
