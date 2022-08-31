import * as L from "leaflet";
import {Pl3xMap} from "../Pl3xMap";

interface ExtendedControlOptions extends L.ControlOptions {
    position?: L.ControlPosition & Position | undefined;
}

type Position = 'topcenter' | 'bottomcenter';

export abstract class ControlBox extends L.Control {
    protected readonly _pl3xmap: Pl3xMap;

    protected constructor(pl3xmap: Pl3xMap, position: string) {
        super();
        this._pl3xmap = pl3xmap;
        super.options = {
            position: position
        } as unknown as ExtendedControlOptions;
    }
}
