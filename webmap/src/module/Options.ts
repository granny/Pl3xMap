export class Options {
    zoom: Zoom = new Zoom();
    ui: UI = new UI();
    format: string = 'png';
}

class Zoom {
    defZoom: number = 0;
    maxZoom: number = 3;
    extraZoomIn: number = 2;
}

class UI {
    link: boolean = false;
    coords: boolean = false;
}
