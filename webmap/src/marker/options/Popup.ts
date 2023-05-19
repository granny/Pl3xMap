import * as L from "leaflet";
import {Point} from "../../util/Point";
import {getOrCreatePane, isset} from "../../util/Util";

export interface PopupOptions {
    content?: string;
    pane?: string;
    offset?: Point;
    maxWidth?: number;
    minWidth?: number;
    maxHeight?: number;
    autoPan?: boolean;
    autoPanPaddingTopLeft?: Point;
    autoPanPaddingBottomRight?: Point;
    autoPanPadding?: Point;
    keepInView?: boolean;
    closeButton?: boolean;
    autoClose?: boolean;
    closeOnEscapeKey?: boolean;
    closeOnClick?: boolean;
}

export class Popup {
    private readonly _content: string;
    private readonly _properties: L.PopupOptions;

    constructor(data: PopupOptions) {
        this._content = isset(data.content) ? data.content! : "";

        let props: L.PopupOptions = {};
        if (isset(data.offset)) props = {...props, offset: [data.offset!.x, data.offset!.z]};
        if (isset(data.maxWidth)) props = {...props, maxWidth: data.maxWidth};
        if (isset(data.minWidth)) props = {...props, minWidth: data.minWidth};
        if (isset(data.maxHeight)) props = {...props, maxHeight: data.maxHeight};
        if (isset(data.autoPan)) props = {...props, autoPan: data.autoPan};
        if (isset(data.autoPanPaddingTopLeft)) props = {
            ...props,
            autoPanPaddingTopLeft: [data.autoPanPaddingTopLeft!.x, data.autoPanPaddingTopLeft!.z]
        };
        if (isset(data.autoPanPaddingBottomRight)) props = {
            ...props,
            autoPanPaddingBottomRight: [data.autoPanPaddingBottomRight!.x, data.autoPanPaddingBottomRight!.z]
        };
        if (isset(data.autoPanPadding)) props = {
            ...props,
            autoPanPadding: [data.autoPanPadding!.x, data.autoPanPadding!.z]
        };
        if (isset(data.keepInView)) props = {...props, keepInView: data.keepInView};
        if (isset(data.closeButton)) props = {...props, closeButton: data.closeButton};
        if (isset(data.autoClose)) props = {...props, autoClose: data.autoClose};
        if (isset(data.closeOnEscapeKey)) props = {...props, closeOnEscapeKey: data.closeOnEscapeKey};
        if (isset(data.closeOnClick)) props = {...props, closeOnClick: data.closeOnClick};

        if (isset(data.pane)) {
            const dom: HTMLElement = getOrCreatePane(data.pane!);
            props = {
                ...props,
                pane: dom.className.split(" ")[1].split("-")[1]
            };
        }

        this._properties = props;
    }

    get content(): string {
        return this._content;
    }

    get properties(): L.PopupOptions {
        return this._properties;
    }
}
