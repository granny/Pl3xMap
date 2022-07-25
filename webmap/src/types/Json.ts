export type RootJSON = {
    format: string;
    ui: {
        lang: {
            title: string;
            coords: {
                label: string
                value: string;
            };
            players: string;
            worlds: string;
            layers: string;
        };
        link: boolean;
        coords: boolean;
    };
    worlds: WorldListJSON[];
};

export type WorldListJSON = {
    name: string;
    display_name: string;
    icon: string;
    type: string;
    order: number;
}

export type WorldJSON = {
    name: string;
    renderers: string[];
    tiles_update_interval: number;
    spawn: {
        x: number;
        z: number;
    };
    zoom: {
        default: number;
        max_out: number;
        max_in: number;
    };
}

export type Palette = {
    index: number;
    block: string;
}

export type BlockInfo = {
    region: {
        x: number;
        z: number;
    },
    blocks: Block[];
}

export type Block = [
    number,
    number,
    number
];

export type JSON = RootJSON & WorldListJSON & WorldJSON;
