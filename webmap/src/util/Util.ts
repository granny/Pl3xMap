import * as L from "leaflet";
import {Pl3xMap} from "../Pl3xMap";

export function createSVGIcon(icon: string): DocumentFragment {
    const template = document.createElement('template');

    template.innerHTML = `<svg class="svg-icon"><use href="#icon--${icon}"></use></svg>`;

    return template.content;
}

export function getJSON(url: string) {
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

export function getBytes(url: string) {
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

export function getUrlParam<T>(query: string, def: T): T {
    return new URLSearchParams(window.location.search).get(query) as unknown as T ?? def;
}

/**
 * Center marker points on block centers
 *
 * @param point marker point
 * @returns block centered latlng
 */
export function toCenteredLatLng(point: L.PointTuple): L.LatLng {
    return toLatLng([Number(point[0]) + 0.5, Number(point[1]) + 0.5]);
}

export function toLatLng(point: L.PointTuple): L.LatLng {
    return L.latLng(pixelsToMeters(point[1]), pixelsToMeters(point[0]));
}

export function toPoint(latlng: L.LatLng): L.PointTuple {
    return [metersToPixels(latlng.lng), metersToPixels(latlng.lat)];
}

export function pixelsToMeters(num: number): number {
    return num * getScale();
}

export function metersToPixels(num: number): number {
    return num / getScale();
}

export function getScale() {
    const map = Pl3xMap.instance.map;
    return 1 / Math.pow(2, map.getMaxZoomOut());
}

export function isset(obj: unknown): boolean {
    return obj !== null && typeof obj !== 'undefined';
}

export function fireCustomEvent<T>(event: keyof (WindowEventMap), detail: T): void {
    window.dispatchEvent(new CustomEvent(event, {detail}));
}

const navigationKeys = new Set<string>([
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
export const handleKeyboardEvent = (e: KeyboardEvent, elements: HTMLElement[]) => {
    if (!e.target) {
        return;
    }

    if (navigationKeys.has(e.key)) {
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
