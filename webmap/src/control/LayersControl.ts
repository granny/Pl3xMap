import {Control, DomEvent} from "leaflet";
import Layers = Control.Layers;
import disableClickPropagation = DomEvent.disableClickPropagation;
import disableScrollPropagation = DomEvent.disableScrollPropagation;

export default class LayersControl extends Layers {
    declare _layersLink: HTMLAnchorElement;
    declare _container: HTMLDivElement;
    private expanded = false;

    _initLayout() {
        // @ts-ignore - Private method not in typings
        super._initLayout();

        // @ts-ignore - Single argument variant not in typings
        DomEvent.off(this._layersLink);
        // @ts-ignore - Single argument variant not in typings
        DomEvent.off(this._container);

        DomEvent.on(this._layersLink, 'click', (e: Event) => {
            this.expanded ? this.collapse() : this.expand();
            e.preventDefault();
        }, this);

        disableClickPropagation(this._container);
        disableScrollPropagation(this._container);
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