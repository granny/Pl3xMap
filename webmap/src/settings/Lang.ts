/**
 * Represents language settings.
 */
export class Lang {
    private readonly _title: string;
    private readonly _coords: Label;
    private readonly _players: string;
    private readonly _worlds: Label;
    private readonly _layers: Label;
    private readonly _blockInfo: Label;

    constructor(title: string, coords: Label, players: string, worlds: Label, layers: Label, blockInfo: Label) {
        this._title = title;
        this._coords = coords;
        this._players = players;
        this._worlds = worlds;
        this._layers = layers;
        this._blockInfo = blockInfo;
    }

    get title(): string {
        return this._title;
    }

    get coords(): Label {
        return this._coords;
    }

    get players(): string {
        return this._players;
    }

    get worlds(): Label {
        return this._worlds;
    }

    get layers(): Label {
        return this._layers;
    }

    get blockInfo(): Label {
        return this._blockInfo;
    }
}

/**
 * Represents a label and value.
 */
export class Label {
    private readonly _label: string
    private readonly _value: string;

    constructor(label: string, value: string) {
        this._label = label;
        this._value = value;
    }

    get label(): string {
        return this._label;
    }

    get value(): string {
        return this._value;
    }
}
