export type RootJSON = {
    format: string;
    ui: {
        lang: {
            title: string;
            coords: string;
            players: string;
            worlds: string;
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

export type JSON = RootJSON & WorldListJSON & WorldJSON;
