import {Pl3xMap} from "../Pl3xMap";
import {WorldJSON} from "./Json";

export class World {
    private pl3xmap: Pl3xMap;
    name: string = 'world';
    renderer: string = 'basic';

    constructor(pl3xmap: Pl3xMap, json: WorldJSON | null) {
        this.pl3xmap = pl3xmap;
        this.name = pl3xmap.getUrlParam('world', json?.name ?? 'world');
    }
}
