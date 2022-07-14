import {Control, DomEvent, DomUtil} from "leaflet";
import Layers = Control.Layers;

export default class LayersControl extends Layers {
    declare _layersLink: HTMLAnchorElement;
    declare _container: HTMLDivElement;
    declare _baseLayersList: HTMLDivElement;
    declare _separator: HTMLDivElement;
    declare _overlaysList: HTMLDivElement;
    declare _section: HTMLElement;
    private expanded = false;

    // noinspection JSUnusedGlobalSymbols
    _initLayout() {
        // copied the contents of _initLayout() from leaflet-src (line 5055-5100)
        // now we can modify it to our needs without @ts-ignore \o/
        // i removed all the events, so we don't have to turn any off

        this._container = DomUtil.create('div', 'leaflet-control-layers')

        // makes this work on IE touch devices by stopping it from firing a mouseout event when the touch is released
        this._container.setAttribute('aria-haspopup', 'true');

        DomEvent.disableClickPropagation(this._container);
        DomEvent.disableScrollPropagation(this._container);

        this._section = DomUtil.create('section', 'leaflet-control-layers-list');

        this._layersLink = DomUtil.create('a', 'leaflet-control-layers-toggle', this._container);
        this._layersLink.href = '#';
        this._layersLink.title = 'Layers';
        this._layersLink.setAttribute('role', 'button');

        DomEvent.on(this._layersLink, 'click', (e: Event) => {
            this.expanded ? this.collapse() : this.expand();
            e.preventDefault();
        }, this);

        this._baseLayersList = DomUtil.create('div', 'leaflet-control-layers-base', this._section);
        this._separator = DomUtil.create('div', 'leaflet-control-layers-separator', this._section);
        this._overlaysList = DomUtil.create('div', 'leaflet-control-layers-overlays', this._section);

        this._container.appendChild(this._section);
    }

    expand() {
        this.expanded = true;
        return super.expand();
    }

    collapse() {
        this.expanded = false;
        return super.collapse();
    }
}
