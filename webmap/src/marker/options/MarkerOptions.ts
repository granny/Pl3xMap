import * as L from "leaflet";
import {Stroke, StrokeOptions} from "./Stroke";
import {Fill, FillOptions} from "./Fill";
import {Tooltip, TooltipOptions} from "./Tooltip";
import {Popup, PopupOptions} from "./Popup";
import {isset} from "../../util/Util";

export interface Options {
    stroke: StrokeOptions;
    fill: FillOptions;
    tooltip: TooltipOptions;
    popup: PopupOptions;
}

export class MarkerOptions {
    private readonly _stroke?: Stroke;
    private readonly _fill?: Fill;
    private readonly _tooltip?: Tooltip;
    private readonly _popup?: Popup;

    constructor(data: Options) {
        this._stroke = isset(data.stroke) ? new Stroke(data.stroke!) : undefined;
        this._fill = isset(data.fill) ? new Fill(data.fill!) : undefined;
        this._tooltip = isset(data.tooltip) ? new Tooltip(data.tooltip!) : undefined;
        this._popup = isset(data.popup) ? new Popup(data.popup!) : undefined;
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
