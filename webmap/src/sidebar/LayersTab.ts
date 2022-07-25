import {createSVGIcon} from "../Util";

import '../svg/layers.svg';
import {Map, Control, DomEvent, DomUtil, Layer, Util} from "leaflet";
import {Pl3xMap} from "../Pl3xMap";
import {SidebarTab} from "../control/SidebarControl";
import stamp = Util.stamp;

interface LayerControlInput extends HTMLInputElement {
    layerId: number;
}

interface LayerListItem {
    layer: Layer;
    name: string;
    overlay: boolean;
}

export default class LayersTab extends Control.Layers implements SidebarTab {
    declare protected _map: Map;

    declare protected _layerControlInputs: HTMLInputElement[];

    declare protected _layers: LayerListItem[];
    declare protected _addLayer: (layer: Layer, name: string, overlay: boolean) => this;
    declare protected _onInputClick: (e: Event) => void;
    declare protected _checkDisabledLayers: () => void;

    protected _pl3xmap: Pl3xMap;
    protected _button: HTMLButtonElement = document.createElement('button');
    protected _content: HTMLDivElement = document.createElement('div');

    protected _baseContainer: HTMLDivElement = document.createElement('div');
    protected _baseLayersList: HTMLFieldSetElement = document.createElement('fieldset');
    protected _overlayContainer: HTMLDivElement = document.createElement('div');
    protected _overlaysList: HTMLFieldSetElement = document.createElement('fieldset');

    constructor(pl3xmap: Pl3xMap) {
        super({}, {}, {hideSingleBase: true});

        this._pl3xmap = pl3xmap;

        this._button.type = 'button';
        this._button.setAttribute('aria-expanded', 'false');
        this._button.setAttribute('aria-controls', `sidebar__layers`);
        this._button.appendChild(createSVGIcon('layers'));
        this._button.setAttribute('aria-label', pl3xmap.lang.layers);

        this._content.hidden = true;
        this._content.id = `sidebar__layers`;
        this._content.setAttribute('aria-hidden', 'true');

        //Remove base layers and repopulate from current world
        window.addEventListener('worldselected', (e) => {
            for (const layer of this._layers.filter(layer => !layer.overlay)) {
                this.removeLayer(layer.layer);
            }

            for (const renderer of e.detail.renderers) {
                this._addLayer(e.detail.getTileLayer(renderer)!, renderer, false);
            }

            this._update();
        });

        window.addEventListener('overlayadded', (e) => {
            if (e.detail.showInControl) {
                this.addOverlay(e.detail.layer, e.detail.name);
            }
        });
    }

    private _initLayout() {
        const heading = DomUtil.create('h2', '', this._content);
        heading.innerText = this._pl3xmap.lang.layers;
        heading.id = 'layers-heading';

        const baseHeading = DomUtil.create('h3', '', this._content);
        baseHeading.innerText = 'Renderers';
        baseHeading.id = 'base-layers-heading';

        const overlayHeading = DomUtil.create('h3', '', this._content);
        overlayHeading.innerText = 'Overlays';
        overlayHeading.id = 'overlay-layers-heading';

        this._baseLayersList.setAttribute('aria-labelledby', 'base-layers-heading');
        this._overlaysList.setAttribute('aria-labelledby', 'overlay-layers-heading');
        this._baseLayersList.classList.add('menu');
        this._baseLayersList.setAttribute('role', 'radiogroup');
        this._overlaysList.setAttribute('role', 'listbox');

        this._baseContainer.hidden = this._overlayContainer.hidden = true;
        this._baseContainer.appendChild(baseHeading)
        this._baseContainer.appendChild(this._baseLayersList);
        this._overlayContainer.appendChild(overlayHeading)
        this._overlayContainer.appendChild(this._overlaysList);

        this._content.appendChild(heading);
        this._content.appendChild(this._baseContainer);
        this._content.appendChild(this._overlayContainer);
    }

    private _update() {
        if (!this._map) {
            return this;
        }

        DomUtil.empty(this._baseLayersList);
        DomUtil.empty(this._overlaysList);

        this._layerControlInputs = [];

        let overlaysCount = 0,
            baseLayersCount = 0;

        for (const layer of this._layers) {
            this._addItem(layer);
            overlaysCount += layer.overlay ? 1 : 0;
            baseLayersCount += !layer.overlay ? 1 : 0;
        }

        // Hide base layers section if there's only one layer.
        if (this.options.hideSingleBase) {
            this._baseContainer.hidden = !baseLayersCount;
            this._overlayContainer.hidden = !overlaysCount;
        }

        return this;
    }

    private _addItem(layer: LayerListItem) {
        const label = document.createElement('label'),
            input = document.createElement('input') as LayerControlInput,
            container = layer.overlay ? this._overlaysList : this._baseLayersList;

        input.type = layer.overlay ? 'checkbox' : 'radio';
        input.name = layer.overlay ? 'overlays' : 'base';
        input.layerId = stamp(layer.layer);
        input.id = label.htmlFor = `${layer.overlay ? 'overlay-' : 'base-'}${input.layerId}`;
        input.defaultChecked = this._map.hasLayer(layer.layer);

        this._layerControlInputs.push(input);

        DomEvent.on(input, 'click', this._onInputClick, this);

        label.innerText = layer.name;

        container.appendChild(input);
        container.appendChild(label);

        this._checkDisabledLayers();
        return label;
    }

    expand(): this {
        //Do nothing
        return this;
    }

    collapse(): this {
        //Do nothing
        return this;
    }

    onActivate() {
        if (!this._baseLayersList.hidden) {
            (this._baseLayersList.querySelector('input:checked') as HTMLElement)!.focus();
        } else if (!this._overlaysList.hidden) {
            (this._baseLayersList.querySelector('input') as HTMLElement)!.focus();
        }
    }

    get button(): HTMLElement {
        return this._button;
    }

    get content(): HTMLElement {
        return this._content;
    }

    get id(): string {
        return 'layers';
    }
}
