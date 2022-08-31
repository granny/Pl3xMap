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

export class Block {
    private readonly _block: number;
    private readonly _biome: number;
    private readonly _yPos: number;
    private readonly _minY: number;

    constructor(packed: number, minY: number) {
        this._block = packed >>> 22;
        this._biome = (packed << 10) >> 22;
        this._yPos = ((packed << 20) >> 20);
        this._minY = minY;
    }

    get block(): number {
        return this._block;
    }

    get biome(): number {
        return this._biome;
    }

    get yPos(): number {
        return this._yPos + this._minY;
    }
}

export type Palette = {
    index: number;
    block: string;
}
