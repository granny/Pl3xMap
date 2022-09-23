import {Point} from "../util/Point";

export class Player {
    private readonly _name: string;
    private readonly _uuid: string;

    private _displayName: string;
    private _world?: string;
    private _position?: Point;

    constructor(name: string, uuid: string, displayName: string, world?: string, position?: Point) {
        this._name = name;
        this._uuid = uuid;
        this._displayName = displayName;
        this._world = world;
        this._position = position;
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

    get world(): string | undefined {
        return this._world;
    }

    set world(world: string | undefined) {
        this._world = world;
    }

    get position(): Point | undefined {
        return this._position;
    }

    set position(position: Point | undefined) {
        this._position = position;
    }
}
