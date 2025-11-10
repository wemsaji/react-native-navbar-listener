import { NativeModules, NativeEventEmitter, Platform } from 'react-native';
import type { NavBarListener } from './types';

const EVENT_NAME = 'NavBarHeightChanged';

const LINKING_ERROR =
    `The package 'react-native-navbar-listener' doesn't seem to be linked.\n` +
    (Platform.OS === 'ios'
        ? '- Run "pod install" inside ios directory\n'
        : '- Rebuild the app after installing the package\n') +
    '- You must use a Custom Dev Build (Expo) or Bare React Native project\n';

const NativeModule: NavBarListener =
    NativeModules.NavBarListener ??
    new Proxy({}, { get() { throw new Error(LINKING_ERROR); } });

const emitter = Platform.OS === 'android'
    ? new NativeEventEmitter(NativeModules.NavBarListener as any)
    : null;

export const addNavBarHeightListener = (callback: (height: number) => void) => {
    if (Platform.OS !== 'android' || !emitter) return () => { };
    const sub = emitter.addListener(EVENT_NAME, callback);
    return () => sub.remove();
};

export const getNavBarHeight = async (): Promise<number> => {
    if (Platform.OS !== 'android') return 0;
    return await NativeModule.getNavBarHeight();
};

export type { NavBarListener };
export default NativeModule;
