import {Control} from "leaflet";
import {Pl3xMap} from "../Pl3xMap";
import {ExtendedControlOptions} from "./LayersControl";

export abstract class ControlBox extends Control {
    protected readonly _pl3xmap: Pl3xMap;

    protected constructor(pl3xmap: Pl3xMap, position: string) {
        super();
        this._pl3xmap = pl3xmap;
        super.options = {
            position: position
        } as unknown as ExtendedControlOptions;
    }
}
