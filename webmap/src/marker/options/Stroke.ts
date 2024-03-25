import * as L from "leaflet";
import {Color} from "../../util/Color";
import {isset} from "../../util/Util";

export interface StrokeOptions {
    enabled?: boolean;
    weight?: number;
    color?: number;
    lineCap?: number;
    lineJoin?: number;
    dashArray?: string;
    dashOffset?: string;
}

export class Stroke {
    private readonly _properties: L.PathOptions;

    constructor(data: StrokeOptions) {
        let props: object = {};

        if (isset(data.enabled)) props = {...props, stroke: data.enabled};
        if (isset(data.weight)) props = {...props, weight: data.weight};
        if (isset(data.color)) {
            const color: Color = new Color(data.color!);
            props = {...props, color: color.hex, opacity: color.opacity};
        }
        if (isset(data.lineCap)) props = {...props, lineCap: LineCap[data.lineCap!]};
        if (isset(data.lineJoin)) props = {...props, lineJoin: LineJoin[data.lineJoin!]};
        if (isset(data.dashArray)) props = {...props, dashArray: data.dashArray};
        if (isset(data.dashOffset)) props = {...props, dashOffset: data.dashOffset};

        this._properties = props;
    }

    get properties(): L.PathOptions {
        return this._properties;
    }
}

export enum LineCap {
    butt = 0,
    round = 1,
    square = 2
}

export enum LineJoin {
    miter = 0,
    round = 1,
    bevel = 2
}
