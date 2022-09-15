import * as L from "leaflet";
import {Color} from "../../util/Color";
import {isset} from "../../util/Util";

export class Fill {
    private readonly _properties: L.PathOptions;

    // [1, 1, 872349696]

    constructor(data: unknown[]) {
        let props = {};

        if (isset(data[0])) props = {...props, fill: data[0] as number == 1};
        if (isset(data[1])) props = {...props, fillRule: Type[data[1] as number] as L.FillRule};
        if (isset(data[2])) {
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
