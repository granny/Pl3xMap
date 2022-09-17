import * as L from "leaflet";
import {Pl3xMap} from "../Pl3xMap";
import {Circle} from "../marker/Circle";
import {Ellipse} from "../marker/Ellipse";
import {Icon} from "../marker/Icon";
import {MultiPolygon} from "../marker/MultiPolygon";
import {MultiPolyline} from "../marker/MultiPolyline";
import {Polygon} from "../marker/Polygon";
import {Polyline} from "../marker/Polyline";
import {Rectangle} from "../marker/Rectangle";
import {Type} from "../marker/Marker";
import {MarkerOptions} from "../marker/options/MarkerOptions";
import {World} from "../world/World";
import {fireCustomEvent, getJSON} from "../util/Util";

export class MarkerLayer extends L.LayerGroup {
    private static readonly TYPES = {
        "circ": (type: Type) => new Circle(type),
        "elli": (type: Type) => new Ellipse(type),
        "icon": (type: Type) => new Icon(type),
        "multipoly": (type: Type) => new MultiPolygon(type),
        "multiline": (type: Type) => new MultiPolyline(type),
        "poly": (type: Type) => new Polygon(type),
        "line": (type: Type) => new Polyline(type),
        "rect": (type: Type) => new Rectangle(type)
    }

    private readonly _key: string;
    private readonly _label: string;
    private readonly _updateInterval: number;
    private readonly _showControls: boolean;
    private readonly _defaultHidden: boolean;
    private readonly _priority: number;
    private readonly _zIndex: number;

    private _timer: NodeJS.Timeout | undefined;

    constructor(key: string, label: string, interval: number, showControls: boolean, defaultHidden: boolean, priority: number, zIndex: number) {
        super(undefined, {
            pane: undefined,
            attribution: undefined
        });
        this._key = key;
        this._label = label;
        this._updateInterval = interval * 1000;
        this._showControls = showControls;
        this._defaultHidden = defaultHidden;
        this._priority = priority;
        this._zIndex = zIndex;

        this.addTo(Pl3xMap.instance.map);

        fireCustomEvent("overlayadded", this);
    }

    get key(): string {
        return this._key;
    }

    get label(): string {
        return this._label;
    }

    get updateInterval(): number {
        return this._updateInterval;
    }

    get showControls(): boolean {
        return this._showControls;
    }

    get defaultHidden(): boolean {
        return this._defaultHidden;
    }

    get priority(): number {
        return this._priority;
    }

    get zIndex(): number {
        return this._zIndex;
    }

    update(world: World): void {
        //console.log("Update markers: " + this._name + " " + this._world.name);

        getJSON(`tiles/${world.name}/markers/${this._key}.json`)
            .then((json) => {
                this.clearLayers();
                for (const index in Object.keys(json)) {
                    this.parseMarker(json[index])?.addTo(this);
                }
                this._timer = setTimeout(() => this.update(world), this._updateInterval);
            });
    }

    unload(): void {
        clearTimeout(this._timer);
        this.clearLayers();
        this.removeFrom(Pl3xMap.instance.map);
        fireCustomEvent("overlayremoved", this);
    }

    parseMarker(data: unknown[]): L.Layer | undefined {
        const options = data.length > 2 ? new MarkerOptions(data[2] as unknown[]) : undefined;

        const type = MarkerLayer.TYPES[data[0] as keyof typeof MarkerLayer.TYPES];
        const marker = type ? type(new Type(data[1] as unknown[], options)).marker : undefined;

        if (marker) {
            const popup = options?.popup;
            const tooltip = options?.tooltip;
            if (popup) {
                marker.bindPopup(() => popup.content, popup.properties);
            }
            if (tooltip) {
                marker.bindTooltip(() => tooltip.content, tooltip.properties);
            }
        }

        return marker;
    }
}
