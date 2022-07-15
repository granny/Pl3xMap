import {World} from "./module/World";

interface WorldRendererPair {
    world: World;
    renderer: string;
}

declare global {
  interface WindowEventMap {
    worldadded: CustomEvent<World>;
    worldremoved: CustomEvent<World>;
    mapchanged: CustomEvent<WorldRendererPair>; //TODO type?
  }
}
