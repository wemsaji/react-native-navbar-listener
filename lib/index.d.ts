import type { NavBarListener } from './types';
declare const NativeModule: NavBarListener;
export declare const addNavBarHeightListener: (callback: (height: number) => void) => () => void;
export declare const getNavBarHeight: () => Promise<number>;
export type { NavBarListener };
export default NativeModule;
