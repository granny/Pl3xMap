import * as L from "leaflet";
import {Stroke} from "./Stroke";
import {Fill} from "./Fill";
import {Tooltip} from "./Tooltip";
import {Popup} from "./Popup";

export class MarkerOptions {
    private readonly _stroke: Stroke | undefined;
    private readonly _fill: Fill | undefined;
    private readonly _tooltip: Tooltip | undefined;
    private readonly _popup: Popup | undefined;

    constructor(data: unknown[]) {
        const stroke = data[0] as number[];
        const fill = data[1] as unknown[];
        const tooltip = data[2] as unknown[];
        const popup = data[3] as unknown[];

        this._stroke = stroke.length > 0 ? new Stroke(stroke) : undefined;
        this._fill = fill.length > 0 ? new Fill(fill) : undefined;
        this._tooltip = tooltip.length > 0 ? new Tooltip(tooltip) : undefined;
        this._popup = popup.length > 0 ? new Popup(popup) : undefined;
    }

    get properties(): L.PathOptions {
        return {...this._stroke?.properties, ...this._fill?.properties};
    }

    get tooltip(): Tooltip | undefined {
        return this._tooltip;
    }

    get popup(): Popup | undefined {
        return this._popup;
    }
}
