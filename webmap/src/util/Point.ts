export class Point {
    private readonly _x: number;
    private readonly _z: number;

    constructor(x: number, z: number) {
        this._x = x;
        this._z = z;
    }

    get x(): number {
        return this._x;
    }

    get z(): number {
        return this._z;
    }
}
