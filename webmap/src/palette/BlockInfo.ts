import {Block} from "./Block";

export class BlockInfo {
    private readonly _data: Uint8Array;

    constructor(data: Uint8Array) {
        this._data = data;
    }

    get minY(): number {
        return this.getInt(8);
    }

    getBlock(index: number): Block {
        return new Block(this.getInt(12 + index * 4), this.minY);
    }

    private getInt(position: number): number {
        let val: number = 0;
        for (let i: number = 0; i < 4; i++) {
            val |= (this._data[position + i] & 0xFF) << (i * 8);
        }
        return val;
    }
}
