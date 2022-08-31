import * as L from "leaflet";

export class MarkerOptions {
    private readonly _properties: L.PathOptions;
    private readonly _tooltip: Tooltip | undefined;

    constructor(arr: unknown[]) {
        if (arr.length != 3) {
            this._properties = {};
            return;
        }

        const stroke = this.parseStroke(arr[0] as number[]);
        const fill = this.parseFill(arr[1] as number[]);

        this._properties = {...stroke, ...fill};
        this._tooltip = this.parseTooltip(arr[2] as unknown[]);
    }

    private parseStroke(arr: number[]): L.PathOptions {
        if (arr.length != 2) {
            return {};
        }

        const weight = arr[0];
        const color = new Color(arr[1]);

        return {
            weight: weight,
            color: color.hex,
            opacity: color.opacity
        };
    }

    private parseFill(arr: number[]): L.PathOptions {
        if (arr.length != 2) {
            return {};
        }

        const rule = (arr[0] == 0 ? 'nonzero' : 'evenodd') as L.FillRule;
        const color = new Color(arr[1]);

        return {
            fillRule: rule,
            fillColor: color.hex,
            fillOpacity: color.opacity
        };
    }

    private parseTooltip(arr: unknown[]): Tooltip | undefined {
        if (arr.length != 3) {
            return undefined;
        }

        const click = arr[0] as number;
        const string = arr[1] as string;
        const offset = arr[2] as number[];

        return new Tooltip(click == 0, string, new L.Point(offset[0], offset[1]));
    }

    get properties(): L.PathOptions {
        return this._properties;
    }

    get tooltip(): Tooltip | undefined {
        return this._tooltip;
    }
}

export class Color {
    private readonly _hex: string;
    private readonly _opacity: number;

    constructor(color: number) {
        const string = color.toString(16);
        if (string.length == 8) {
            this._hex = `#${string.substring(2)}`;
            this._opacity = parseInt(string.substring(0, 2), 16) / 0xFF;
        } else {
            this._hex = `#${string}`;
            this._opacity = 1.0;
        }
    }

    get hex(): string {
        return this._hex;
    }

    get opacity(): number {
        return this._opacity;
    }
}

export class Tooltip {
    private readonly _click: boolean;
    private readonly _string: string;
    private readonly _offset: L.Point;

    constructor(click: boolean, string: string, offset: L.Point) {
        this._click = click;
        this._string = string;
        this._offset = offset;
    }

    get click(): boolean {
        return this._click;
    }

    get string(): string {
        return this._string;
    }

    get offset(): L.Point {
        return this._offset;
    }
}
