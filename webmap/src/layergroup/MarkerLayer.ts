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
import {getJSON} from "../util/Util";

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

    private readonly _name: string;
    private readonly _world: World;
    private readonly _interval: number;

    private _timer: NodeJS.Timeout | undefined;

    constructor(name: string, world: World, interval: number) {
        super(undefined, {
            pane: undefined,
            attribution: undefined
        });
        this._name = name;
        this._world = world;
        this._interval = interval * 1000;

        this.addTo(Pl3xMap.instance.map);

        this.update();
    }

    update(): void {
        //console.log("Update markers: " + this._name + " " + this._world.name);

        getJSON(`tiles/${this._world.name}/markers/${this._name}.json`)
            .then((json) => {
                this.clearLayers();
                for (const index in Object.keys(json)) {
                    this.parseMarker(json[index])?.addTo(this);
                }
                this._timer = setTimeout(() => this.update(), this._interval);
            });
    }

    unload(): void {
        clearTimeout(this._timer);
        this.clearLayers();
        this.removeFrom(Pl3xMap.instance.map);
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
