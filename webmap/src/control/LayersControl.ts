import {Map, Control, DomEvent, DomUtil, stamp, Layer} from "leaflet";
import Layers = Control.Layers;
import {createSVGIcon, handleKeyboardEvent} from "../Util";
import {Pl3xMap} from "../Pl3xMap";
import '../svg/layers.svg';

interface LayerControlInput extends HTMLInputElement {
    layerId: number;
}

export default class LayersControl extends Layers {
    declare _map: Map;
    declare _layersLink: HTMLButtonElement;
    declare _container: HTMLDivElement;
    declare _baseLayersList: HTMLDivElement;
    declare _separator: HTMLDivElement;
    declare _overlaysList: HTMLDivElement;
    declare _section: HTMLElement;
    declare _layerControlInputs: HTMLInputElement[];

    declare _checkDisabledLayers: () => void;
    declare _onInputClick: (e: Event) => void;
    declare _createRadioElement: (className: string, checked: boolean) => HTMLInputElement;

    private readonly _pl3xmap: Pl3xMap;
    private expanded = false;

    constructor(pl3xmap: Pl3xMap) {
        super({}, {}, {position: 'topleft'});
        this._pl3xmap = pl3xmap;
    }

    // noinspection JSUnusedGlobalSymbols
    _initLayout() {
        // copied the contents of _initLayout() from leaflet-src (line 5055-5100)
        // now we can modify it to our needs without @ts-ignore \o/
        // i removed all the events, so we don't have to turn any off

        this._container = DomUtil.create('div', 'leaflet-control-layers');

        DomEvent.disableClickPropagation(this._container);
        DomEvent.disableScrollPropagation(this._container);

        this._section = DomUtil.create('section', 'leaflet-control-layers-list');

        this._layersLink =  DomUtil.create('button', 'leaflet-control-layers-toggle', this._container);
        this._layersLink.title = this._pl3xmap.lang.layers;
        this._layersLink.appendChild(createSVGIcon('layers'));

        //Avoiding DomEvent here for more specific event typings
        this._layersLink.addEventListener('click', (e: MouseEvent) => {
            this.expanded ? this.collapse() : this.expand();
            e.preventDefault();
        });

        //Expand on right arrow press on button
        this._layersLink.addEventListener('keydown', (e: KeyboardEvent) => {
            if(e.key === 'ArrowRight') {
                this.expand();
                e.preventDefault();
            }
        });

        //Collapse on left arrow press on list
        this._section.addEventListener('keydown', (e: KeyboardEvent) => {
            if(e.key === 'ArrowLeft') {
                this.collapse();
                e.preventDefault();
            } else {
                handleKeyboardEvent(e, Array.from(this._section.querySelectorAll('input')));
            }
        });

        this._baseLayersList = DomUtil.create('div', 'leaflet-control-layers-base', this._section);
        this._separator = DomUtil.create('div', 'leaflet-control-layers-separator', this._section);
        this._overlaysList = DomUtil.create('div', 'leaflet-control-layers-overlays', this._section);

        this._container.appendChild(this._section);
    }

    // noinspection JSUnusedGlobalSymbols
    _addItem(layer: {layer: Layer, name: string, overlay: boolean}) {
		const label = DomUtil.create('label', 'leaflet-control-layers-selector'),
            name = document.createElement('span'),
		    checked = this._map.hasLayer(layer.layer);
        let input;

        name.innerText = layer.name;

		if (layer.overlay) {
			input = DomUtil.create('input');
			input.type = 'checkbox';
            input.name = 'overlay';
			input.defaultChecked = checked;
		} else {
			input = this._createRadioElement('leaflet-base-layers_' + stamp(this), checked);
		}

		this._layerControlInputs.push(input);
        (input as LayerControlInput).layerId = stamp(layer.layer);

		DomEvent.on(input, 'click', this._onInputClick, this);

        label.appendChild(input);
        label.appendChild(name);

		if(layer.overlay) {
            this._overlaysList.appendChild(label);
        } else {
            this._baseLayersList.appendChild(label);
        }

		this._checkDisabledLayers();
		return label;
	}

    expand() {
        this.expanded = true;
        this._layersLink.setAttribute('aria-expanded', 'true');

        super.expand();

        //Focus first layer checkbox
        const firstItem = this._section.querySelector('input');

        if(firstItem) {
            firstItem.focus();
        }

        return this;
    }

    collapse() {
        this.expanded = false;
        this._layersLink.removeAttribute('aria-expanded');
        this._layersLink.focus();

        return super.collapse();
    }
}
