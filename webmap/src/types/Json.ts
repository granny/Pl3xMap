export type RootJSON = {
    format: string;
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
    ui: {
        link: boolean;
        coords: boolean;
        blockinfo: boolean;
    }
}

export type Palette = {
    index: number;
    block: string;
}

export type JSON = RootJSON & WorldListJSON & WorldJSON;
