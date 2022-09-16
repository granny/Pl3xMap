import * as L from "leaflet";
import {Pl3xMap} from "../Pl3xMap";
import {SidebarTab} from "../control/SidebarControl";
import {createSVGIcon} from "../util/Util";
import '../svg/layers.svg';

interface LayerControlInput extends HTMLInputElement {
    layerId: number;
}

interface LayerListItem {
    layer: L.Layer;
    name: string;
    overlay: boolean;
}

export default class LayersTab extends L.Control.Layers implements SidebarTab {
    declare protected _map: L.Map;

    declare protected _layerControlInputs: HTMLInputElement[];

    declare protected _layers: LayerListItem[];
    declare protected _addLayer: (layer: L.Layer, name: string, overlay: boolean) => this;
    declare protected _onInputClick: (e: Event) => void;
    declare protected _checkDisabledLayers: () => void;

    protected _pl3xmap: Pl3xMap;
    protected _button: HTMLButtonElement = L.DomUtil.create('button');
    protected _content: HTMLDivElement = L.DomUtil.create('div');

    protected _skeleton: HTMLParagraphElement = L.DomUtil.create('p');
    protected _baseContainer: HTMLDivElement = L.DomUtil.create('div');
    protected _baseLayersList: HTMLFieldSetElement = L.DomUtil.create('fieldset');
    protected _overlayContainer: HTMLDivElement = L.DomUtil.create('div');
    protected _overlaysList: HTMLFieldSetElement = L.DomUtil.create('fieldset');

    constructor(pl3xmap: Pl3xMap) {
        super({}, {}, {hideSingleBase: true});

        this._pl3xmap = pl3xmap;

        this._button.type = 'button';
        this._button.setAttribute('aria-expanded', 'false');
        this._button.setAttribute('aria-controls', `sidebar__layers`);
        this._button.appendChild(createSVGIcon('layers'));
        this._button.setAttribute('aria-label', pl3xmap.settings!.lang.layers.label);

        this._content.hidden = true;
        this._content.id = `sidebar__layers`;
        this._content.setAttribute('aria-hidden', 'true');

        /* this can be removed?, renderers now in worlds tab
        //Remove base layers and repopulate from current world
        window.addEventListener('worldselected', (e) => {
            for (const layer of this._layers.filter(layer => !layer.overlay)) {
                this.removeLayer(layer.layer);
            }

            for (const renderer of e.detail.renderers) {
                this._addLayer(e.detail.getRendererLayer(renderer)!, renderer, false);
            }

            this._update();
        });
        */

        window.addEventListener('overlayadded', (e) => {
            if (e.detail.showControls) {
                this._addLayer(e.detail, e.detail.label, true);
            }
            this._update();
        });
    }

    private _initLayout() {
        const heading = L.DomUtil.create('h2', '', this._content);
        heading.innerText = this._pl3xmap.settings!.lang.layers.label;
        heading.id = 'layers-heading';

        const baseHeading = L.DomUtil.create('h3', '', this._baseContainer);
        baseHeading.innerText = 'Renderers';
        baseHeading.id = 'base-layers-heading';

        const overlayHeading = L.DomUtil.create('h3', '', this._overlayContainer);
        overlayHeading.innerText = 'Overlays';
        overlayHeading.id = 'overlay-layers-heading';

        this._skeleton.innerText = this._pl3xmap.settings!.lang.layers.value;
        this._skeleton.id = 'layers-skeleton';
        this._skeleton.tabIndex = -1;

        this._baseLayersList.setAttribute('aria-labelledby', 'base-layers-heading');
        this._overlaysList.setAttribute('aria-labelledby', 'overlay-layers-heading');
        this._baseLayersList.classList.add('menu');
        this._baseLayersList.setAttribute('role', 'radiogroup');
        this._overlaysList.setAttribute('role', 'listbox');

        this._baseContainer.hidden = this._overlayContainer.hidden = true;
        this._baseContainer.appendChild(this._baseLayersList);
        this._overlayContainer.appendChild(this._overlaysList);

        this._content.appendChild(this._skeleton);
        this._content.appendChild(this._baseContainer);
        this._content.appendChild(this._overlayContainer);
    }

    private _update() {
        if (!this._map) {
            return this;
        }

        L.DomUtil.empty(this._baseLayersList);
        L.DomUtil.empty(this._overlaysList);

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
            this._baseContainer.hidden = baseLayersCount <= 1;
        } else {
            this._baseContainer.hidden = !baseLayersCount;
        }

        this._overlayContainer.hidden = !overlaysCount;
        this._skeleton.hidden = !!(baseLayersCount || overlaysCount);

        return this;
    }

    private _addItem(layer: LayerListItem) {
        const label = L.DomUtil.create('label'),
            input = L.DomUtil.create('input') as LayerControlInput,
            container = layer.overlay ? this._overlaysList : this._baseLayersList;

        input.type = layer.overlay ? 'checkbox' : 'radio';
        input.name = layer.overlay ? 'overlays' : 'base';
        input.layerId = L.Util.stamp(layer.layer);
        input.id = label.htmlFor = `${layer.overlay ? 'overlay-' : 'base-'}${input.layerId}`;
        input.defaultChecked = this._map.hasLayer(layer.layer);

        this._layerControlInputs.push(input);

        L.DomEvent.on(input, 'click', this._onInputClick, this);

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
        if (!this._baseContainer.hidden) {
            (this._baseLayersList.querySelector('input:checked') as HTMLElement)!.focus();
        } else if (!this._overlayContainer.hidden) {
            (this._overlaysList.querySelector('input') as HTMLElement)!.focus();
        } else {
            this._skeleton.focus();
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
