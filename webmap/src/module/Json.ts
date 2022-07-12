export type RootJSON = {
    ui: {
        lang: {
            title: string; coords: string; players: string; worlds: string;
        };
        link: boolean;
        coords: boolean;
    };
    worlds: WorldJSON[];
};

export type WorldJSON = {
    name: string;
    spawn: {
        x: string;
        z: string;
    };
    zoom: {
        default: string;
    };
}

export type JSON = RootJSON & WorldJSON;
