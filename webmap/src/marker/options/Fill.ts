import * as L from "leaflet";
import {Color} from "../../util/Color";
import {isset} from "../../util/Util";

export interface FillOptions {
    enabled?: boolean;
    type?: number;
    color?: number;
}

export class Fill {
    private readonly _properties: L.PathOptions;

    constructor(data: FillOptions) {
        let props = {};

        if (isset(data.enabled)) props = {...props, fill: data.enabled};
        if (isset(data.type)) props = {...props, fillRule: Type[data.type!]};
        if (isset(data.color)) {
            const color = new Color(data.color!);
            props = {...props, fillColor: color.hex, fillOpacity: color.opacity};
        }

        this._properties = props;
    }

    get properties(): L.PathOptions {
        return this._properties;
    }
}

export enum Type {
    nonzero,
    evenodd
}
