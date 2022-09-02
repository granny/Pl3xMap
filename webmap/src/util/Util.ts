import * as L from "leaflet";
import {Pl3xMap} from "../Pl3xMap";

export class Util {
    private static readonly navigationKeys = new Set<string>([
        'ArrowUp',
        'ArrowDown',
        'ArrowLeft',
        'ArrowRight',
        'Home',
        'End'
    ]);

    /**
     * Helper method for handling keyboard based selection, along with focus navigation within a set of HTML elements.
     *
     * The given {@link KeyboardEvent} will be checked for common navigation keys (currently arrow keys + home/end)
     * and the appropriate {@link HTMLElement} in the provided array of elements will be focused. No focus changes will occur
     * if none of the provided elements are currently focused
     *
     * The event will also be checked for an enter key press, and a click event will be simulated on the target element. The
     * element does not need to be in the provided element array for this to occur
     *
     * @param {KeyboardEvent} e The event to handle
     * @param {HTMLElement[]} elements The elements to consider for focusing
     */
    public static handleKeyboardEvent(e: KeyboardEvent, elements: HTMLElement[]): void {
        if (!e.target) {
            return;
        }

        if (this.navigationKeys.has(e.key)) {
            const position = elements.indexOf(e.target as HTMLElement);

            if (position < 0) {
                return;
            }

            let newPosition = position;

            switch (e.key) {
                case 'ArrowUp':
                case 'ArrowLeft':
                    newPosition = position - 1;
                    break;

                case 'ArrowDown':
                case 'ArrowRight':
                    newPosition = position + 1;
                    break;

                case 'Home':
                    newPosition = 0;
                    break;

                case 'End':
                    newPosition = elements.length - 1;
                    break;
            }

            if (newPosition < 0) {
                newPosition = elements.length - 1;
            } else if (newPosition >= elements.length) {
                newPosition = 0;
            }

            (elements[newPosition] as HTMLElement).focus();
            e.preventDefault();
        } else if (e.key === 'Enter' && e.target) {
            const mouseEvent = new MouseEvent('click', {
                ctrlKey: e.ctrlKey,
                shiftKey: e.shiftKey,
                metaKey: e.metaKey,
                altKey: e.altKey,
                bubbles: true,
            });

            e.target.dispatchEvent(mouseEvent);
            e.preventDefault();
        }
    }

    public static createSVGIcon(icon: string): DocumentFragment {
        const template = document.createElement('template');

        template.innerHTML = `<svg class="svg-icon"><use href="#icon--${icon}"></use></svg>`;

        return template.content;
    }

    public static getJSON(url: string) {
        return fetch(url, {
            cache: "no-store",
            headers: {
                "Content-Disposition": "inline"
            }
        }).then(async res => {
            if (res.ok) {
                return await res.json();
            }
        });
    }

    public static getBytes(url: string) {
        return fetch(url, {
            cache: "no-store",
            headers: {
                "Content-Disposition": "inline"
            }
        }).then(async res => {
            if (res.ok) {
                return await res.arrayBuffer();
            }
        });
    }

    public static getUrlParam<T>(query: string, def: T): T {
        return new URLSearchParams(window.location.search).get(query) as unknown as T ?? def;
    }

    public static fireCustomEvent<T>(event: keyof (WindowEventMap), detail: T): void {
        window.dispatchEvent(new CustomEvent(event, {detail}));
    }

    public static toLatLng(x: number, z: number): L.LatLng {
        return L.latLng(Util.pixelsToMeters(z + 0.5), Util.pixelsToMeters(x + 0.5));
    }

    public static toPoint(latlng: L.LatLng): L.Point {
        return L.point(Util.metersToPixels(latlng.lng), Util.metersToPixels(latlng.lat));
    }

    public static pixelsToMeters(num: number): number {
        return num * Util.getScale();
    }

    public static metersToPixels(num: number): number {
        return num / Util.getScale();
    }

    public static getScale() {
        const map = Pl3xMap.getInstance().map;
        return 1 / Math.pow(2, map.getMaxZoomOut());
    }

    public static isset(obj: unknown): boolean {
        return obj !== null && typeof obj !== 'undefined';
    }
}
