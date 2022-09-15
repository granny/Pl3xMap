import * as L from "leaflet";
import {isset} from "../../util/Util";

export class Tooltip {
    private readonly _content: string;
    private readonly _properties: L.TooltipOptions;

    // ["content", null, null, 2, 0, 0, 0.9]

    constructor(data: unknown[]) {
        this._content = isset(data[0]) ? data[0] as string : "";

        let props = {};
        if (isset(data[1])) props = {...props, pane: data[1] as string};
        if (isset(data[2])) props = {...props, offset: data[2] as L.PointTuple};
        if (isset(data[3])) props = {...props, direction: Direction[data[3] as number] as L.Direction};
        if (isset(data[4])) props = {...props, permanent: data[4] as boolean};
        if (isset(data[5])) props = {...props, sticky: data[5] as boolean};
        if (isset(data[6])) props = {...props, opacity: data[6] as number};
        this._properties = props;
    }

    get content(): string {
        return this._content;
    }

    get properties(): L.TooltipOptions {
        return this._properties;
    }
}

export enum Direction {
    right,
    left,
    top,
    bottom,
    center,
    auto
}
