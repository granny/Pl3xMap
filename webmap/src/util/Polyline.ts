import {Point} from "./Point";

export class Polyline {
    private readonly _key: string;
    private readonly _points: Point[];

    constructor(key: string, points: Point[]) {
        this._key = key;
        this._points = points;
    }

    get key(): string {
        return this._key;
    }

    get points(): Point[] {
        return this._points;
    }
}
