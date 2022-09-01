import * as L from "leaflet";
import {Color} from "../../../util/Color";

export class Stroke {
    private readonly _properties: L.PathOptions;

    // [1, 3, -65536, 1, 1, null, null]

    constructor(data: unknown[]) {
        let props = {};

        if (data[0]) props = {...props, stroke: data[0] as boolean};
        if (data[1]) props = {...props, weight: data[1] as number};
        if (data[2]) {
            const color = new Color(data[2] as number);
            props = {
                ...props,
                color: color.hex,
                opacity: color.opacity
            };
        }
        if (data[3]) props = {...props, lineCap: LineCap[data[3] as number] as L.LineCapShape};
        if (data[4]) props = {...props, lineJoin: LineJoin[data[4] as number] as L.LineJoinShape};
        if (data[5]) props = {...props, dashArray: data[5] as string};
        if (data[6]) props = {...props, dashOffset: data[6] as string};

        this._properties = props;
    }

    get properties(): L.PathOptions {
        return this._properties;
    }
}

export enum LineCap {
    butt,
    round,
    square
}

export enum LineJoin {
    miter,
    round,
    bevel
}
