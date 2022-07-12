import {Pl3xMap} from "../Pl3xMap";

export class World {
    private pl3xmap: Pl3xMap;
    name: string = 'world';
    renderer: string = 'basic';

    constructor(pl3xmap: Pl3xMap, json: any) {
        this.pl3xmap = pl3xmap;
        this.name = pl3xmap.getUrlParam('world', json?.name ?? 'world');
    }
}
