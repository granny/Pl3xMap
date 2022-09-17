export class Player {
    private readonly _name: string;
    private readonly _uuid: string;
    private readonly _world: string;

    constructor(name: string, uuid: string, world: string) {
        this._name = name;
        this._uuid = uuid;
        this._world = world;
    }

    get name(): string {
        return this._name;
    }

    get uuid(): string {
        return this._uuid;
    }

    get world(): string {
        return this._world;
    }
}
