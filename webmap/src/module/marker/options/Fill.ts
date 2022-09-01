import * as L from "leaflet";
import {Color} from "../../../util/Color";

export class Fill {
    private readonly _properties: L.PathOptions;

    // [1, 1, 872349696]

    constructor(data: unknown[]) {
        let props = {};

        if (data[0]) props = {...props, fill: data[0] as boolean};
        if (data[1]) props = {...props, fillRule: Type[data[1] as number] as L.FillRule};
        if (data[2]) {
            const color = new Color(data[2] as number);
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
