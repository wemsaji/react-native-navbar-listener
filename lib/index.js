"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var _a;
Object.defineProperty(exports, "__esModule", { value: true });
exports.getNavBarHeight = exports.addNavBarHeightListener = void 0;
const react_native_1 = require("react-native");
const LINKING_ERROR = `The package 'react-native-navbar-listener' doesn't seem to be linked.\n` +
    (react_native_1.Platform.OS === 'ios'
        ? '- Run "pod install" inside ios directory\n'
        : '- Rebuild the app after installing the package\n') +
    '- You must use a Custom Dev Build (Expo) or Bare React Native project\n';
const NativeModule = (_a = react_native_1.NativeModules.NavBarListener) !== null && _a !== void 0 ? _a : new Proxy({}, { get() { throw new Error(LINKING_ERROR); } });
const emitter = react_native_1.Platform.OS === 'android'
    ? new react_native_1.NativeEventEmitter(react_native_1.NativeModules.NavBarListener)
    : null;
const addNavBarHeightListener = (callback) => {
    if (react_native_1.Platform.OS !== 'android' || !emitter)
        return () => { };
    const sub = emitter.addListener('NavBarHeightChanged', callback);
    return () => sub.remove();
};
exports.addNavBarHeightListener = addNavBarHeightListener;
const getNavBarHeight = () => __awaiter(void 0, void 0, void 0, function* () {
    if (react_native_1.Platform.OS !== 'android')
        return 0;
    return yield NativeModule.getNavBarHeight();
});
exports.getNavBarHeight = getNavBarHeight;
exports.default = NativeModule;
