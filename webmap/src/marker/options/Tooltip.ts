import * as L from "leaflet";
import {Point} from "../../util/Point";
import {getOrCreatePane, isset} from "../../util/Util";

export interface TooltipOptions {
    content?: string;
    pane?: string;
    offset?: Point;
    direction?: number;
    permanent?: boolean;
    sticky?: boolean;
    opacity?: number;
}

export class Tooltip {
    private readonly _content: string;
    private readonly _properties: L.TooltipOptions;

    constructor(data: TooltipOptions) {
        this._content = isset(data.content) ? data.content! : "";

        let props: object = {};
        if (isset(data.offset)) props = {...props, offset: [data.offset!.x, data.offset!.z]};
        if (isset(data.direction)) props = {...props, direction: Direction[data.direction!]};
        if (isset(data.permanent)) props = {...props, permanent: data.permanent};
        if (isset(data.sticky)) props = {...props, sticky: data.sticky};
        if (isset(data.opacity)) props = {...props, opacity: data.opacity};

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

    get properties(): L.TooltipOptions {
        return this._properties;
    }
}

export enum Direction {
    right = 0,
    left = 1,
    top = 2,
    bottom = 3,
    center = 4,
    auto = 5
}
