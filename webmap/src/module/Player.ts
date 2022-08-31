import {Pl3xMap} from "../Pl3xMap";
import {World} from "./World";

export class Player {
    private readonly _name: string;
    private readonly _uuid: string;
    private readonly _world: World | undefined;
    private readonly _position: Position;
    private readonly _stats: Stats;

    constructor(pl3xmap: Pl3xMap, data: PlayerJSON) {
        this._name = data.name;
        this._uuid = data.uuid;
        this._world = pl3xmap.getWorld(data.world);
        this._position = new Position(data);
        this._stats = new Stats(data);
    }

    get name(): string {
        return this._name;
    }

    get uuid(): string {
        return this._uuid;
    }

    get world(): World | undefined {
        return this._world;
    }

    get position(): Position {
        return this._position;
    }

    get stats(): Stats {
        return this._stats;
    }
}

export class Position {
    private readonly _x: number;
    private readonly _z: number;
    private readonly _yaw: number;

    constructor(json: PlayerJSON) {
        this._x = json.x;
        this._z = json.z;
        this._yaw = json.yaw;
    }

    get x(): number {
        return this._x;
    }

    get z(): number {
        return this._z;
    }

    get yaw(): number {
        return this._yaw;
    }
}

export class Stats {
    private readonly _armor: number;
    private readonly _health: number;

    constructor(json: PlayerJSON) {
        this._armor = json.armor;
        this._health = json.health;
    }

    get armor(): number {
        return this._armor;
    }

    get health(): number {
        return this._health;
    }
}

export type PlayerJSON = {
    name: string,
    uuid: string,
    world: string,
    x: number,
    z: number,
    yaw: number,
    armor: number,
    health: number
}
