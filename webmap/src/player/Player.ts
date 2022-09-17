export class Player {
    private readonly _name: string;
    private readonly _uuid: string;

    private _displayName: string;
    private _world: string;

    constructor(name: string, uuid: string, displayName: string, world: string) {
        this._name = name;
        this._uuid = uuid;
        this._displayName = displayName;
        this._world = world;
    }

    get name(): string {
        return this._name;
    }

    get uuid(): string {
        return this._uuid;
    }

    get displayName(): string {
        return this._displayName;
    }

    set displayName(displayName: string) {
        this._displayName = displayName;
    }

    get world(): string {
        return this._world;
    }

    set world(world: string) {
        this._world = world;
    }
}
