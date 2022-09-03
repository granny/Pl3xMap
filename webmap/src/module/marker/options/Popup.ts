import * as L from "leaflet";
import {Util} from "../../../util/Util";

export class Popup {
    private readonly _content: string;
    private readonly _properties: L.PopupOptions;

    // ["content", null, null, 300, 50, null, 1, null, null, null, false, true, true, true, true]

    constructor(data: unknown[]) {
        this._content = data[0] as string;

        let props: L.PopupOptions = {};
        if (Util.isset(data[1])) props = {...props, pane: data[1] as string};
        if (Util.isset(data[2])) props = {...props, offset: data[2] as L.PointTuple};
        if (Util.isset(data[3])) props = {...props, maxWidth: data[3] as number};
        if (Util.isset(data[4])) props = {...props, minWidth: data[4] as number};
        if (Util.isset(data[5])) props = {...props, maxHeight: data[5] as number};
        if (Util.isset(data[6])) props = {...props, autoPan: data[6] as boolean};
        if (Util.isset(data[7])) props = {...props, autoPanPaddingTopLeft: data[7] as L.PointTuple};
        if (Util.isset(data[8])) props = {...props, autoPanPaddingBottomRight: data[8] as L.PointTuple};
        if (Util.isset(data[9])) props = {...props, autoPanPadding: data[9] as L.PointTuple};
        if (Util.isset(data[10])) props = {...props, keepInView: data[10] as boolean};
        if (Util.isset(data[11])) props = {...props, closeButton: data[11] as boolean};
        if (Util.isset(data[12])) props = {...props, autoClose: data[12] as boolean};
        if (Util.isset(data[13])) props = {...props, closeOnEscapeKey: data[13] as boolean};
        if (Util.isset(data[14])) props = {...props, closeOnClick: data[14] as boolean};

        this._properties = props;
    }

    get content(): string {
        return this._content;
    }

    get properties(): L.PopupOptions {
        return this._properties;
    }
}
