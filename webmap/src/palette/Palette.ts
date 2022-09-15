export class Palette {
    private readonly _index: string;
    private readonly _value: string;

    constructor(index: string, value: string) {
        this._index = index;
        this._value = value;
    }

    get index(): string {
        return this._index
    }

    get value(): string {
        return this._value
    }
}
