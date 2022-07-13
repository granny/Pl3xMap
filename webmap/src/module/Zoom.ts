export class Zoom {
    private readonly _def: number;
    private readonly _maxOut: number;
    private readonly _maxIn: number;

    constructor(def: number, maxOut: number, maxIn: number) {
        this._def = def;
        this._maxOut = maxOut;
        this._maxIn = maxIn;
    }

    get default(): number {
        return this._def;
    }

    get maxOut(): number {
        return this._maxOut;
    }

    get maxIn(): number {
        return this._maxIn;
    }
}
