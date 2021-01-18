import { P } from '../map.js';

class UILink {
    constructor() {
        const Link = L.Control.extend({
            _container: null,
            options: {
                position: 'bottomleft'
            },
            onAdd: function () {
                const link = L.DomUtil.create('div', 'leaflet-control-layers link');
                this._link = link;
                this.update();
                return link;
            },
            update: function() {
                const url = P.worldList.curWorld == null ? "" : P.getUrlFromView();
                //P.updateBrowserUrl(url); // this spams browser history
                this._link.innerHTML = "<a href='" + url + "'><img src='images/clear.png'/></a>";
            }
        });
        this.link = new Link();
        P.map.addControl(this.link)
            .addEventListener('move', () => this.update())
            .addEventListener('zoom', () => this.update());
    }
    update() {
        this.link.update();
    }
}

export { UILink };
