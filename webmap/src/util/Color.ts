export class Color {
    private readonly _rgb: number;
    private readonly _hex: string;
    private readonly _opacity: number;

    constructor(color: number) {
        this._rgb = color;
        this._hex = "#" + (color & 0xFFFFFF).toString(16).padStart(6, '0');
        this._opacity = (color >> 24 & 0xFF) / 0xFF;
    }

    get rgb(): number {
        return this._rgb;
    }

    get hex(): string {
        return this._hex;
    }

    get opacity(): number {
        return this._opacity;
    }
}
