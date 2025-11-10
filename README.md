# react-native-navbar-listener

🧭 Prevent React Native apps from overlapping with the Android system navigation bar on some devices by listening for navigation bar height changes and notifying the JavaScript layer.

![npm](https://img.shields.io/npm/v/react-native-navbar-listener)
![license](https://img.shields.io/npm/l/react-native-navbar-listener)
![expo](https://img.shields.io/badge/Expo-SDK_53+-000020?logo=expo)
![android](https://img.shields.io/badge/-Android-555555?logo=android&logoColor=3DDC84)
![types](https://img.shields.io/npm/types/react-native-navbar-listener)

---

## 🤔 Why

When upgrading to Expo SDK 53, some environments (for example **Xiaomi HyperOS** and **Samsung One UI**) showed an issue where bottom navigation or content overlapped the **system navigation bar**. Related libraries such as `react-native-safe-area-context` and `@react-navigation/material-top-tabs` have reports about similar behavior but no immediate plans to fix it. This small library provides a focused workaround by exposing navigation bar height and change events to the React Native JavaScript layer.

---

## 🚀 Quick Start

### Installation

```bash
npm i react-native-navbar-listener
# or
npx expo install react-native-navbar-listener
```

### Import

```ts
import { addNavBarHeightListener, getNavBarHeight } from 'react-native-navbar-listener';
```

### Basic Usage

```ts
const getNavBarInitialHeight = async () => {
    const navBarHeight = await getNavBarHeight();
    console.log('NavBarInitialHeight:', navBarHeight);
};

const onNavBarHeightChanged = (navBarHeight: number) => {
    console.log('NavBarHeightChanged:', navBarHeight);
};

useEffect(() => {
    getNavBarInitialHeight();
    const remover = addNavBarHeightListener(onNavBarHeightChanged);
    return remover;
}, []);
```

---

## ✨ Features

- Compatible with **Android 11 (API 30)** and above.
- Detects navigation bar height changes via the `NavBarHeightChanged` event.
- Retrieve the current navigation bar height (in dp) using `getNavBarHeight()`.
- Written in **TypeScript** with bundled type definitions.

---

## 📱 Supported Environment

- **Android 11 (API 30) or higher**  
  The `setWindowInsetsAnimationCallback()` API used in this library was introduced in Android 11.

---

## ⚠️ Limitations

- Fallback support for **Android 10 and below** is **not implemented**.
- On gesture navigation mode, some devices return **0 dp**, while others return around **16 dp** (the height of the gesture handle area). This device-dependent discrepancy is not normalized by the library.
- While the implementation uses `setWindowInsetsAnimationCallback`, only minimum and maximum values are reliably captured — intermediate animation frames may still trigger redundant events. Therefore, **debouncing** is recommended on the JavaScript side.

---

## 🛠️ Example Usage (Advanced)

```tsx
import { Dimensions, Platform } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { addNavBarHeightListener, getNavBarHeight } from 'react-native-navbar-listener';
import { debounce } from 'lodash';
const IOS = Platform.OS === 'ios';

// in function component:

const insets = useSafeAreaInsets();
const basePaddingBottom = IOS ? insets.bottom : 0;
const [paddingBottom, setPaddingBottom] = useState(basePaddingBottom);

const onNavBarHeightChanged = debounce((navBarHeight: number) => {
    setPaddingBottom(basePaddingBottom + (navBarHeight || (IOS ? 0 : 16)));
}, 50);

const getNavBarInitialHeight = async () => {
    const navBarHeight = await getNavBarHeight();
    onNavBarHeightChanged(navBarHeight);
};

useEffect(() => {
    getNavBarInitialHeight();
    const remover = addNavBarHeightListener(onNavBarHeightChanged);
    return remover;
}, []);

return (
  <Tab.Navigator style={{ height: Dimensions.get('screen').height, paddingBottom }}>
    {/* your tabs */}
  </Tab.Navigator>
);
```

> Note: Depending on the device, `Dimensions.get('window').height` may or may not include the navigation bar height. To avoid layout overlap, prefer `Dimensions.get('screen').height` for full-screen sizing.

---

## 🧩 API Reference

### `getNavBarHeight(): Promise<number>`

Returns the current navigation bar height (in dp).  
If the value is `0`, the navigation bar is either hidden or the device is using gesture navigation. Some devices return `0`, while others return around `16` (the height of the gesture handle area).

### `addNavBarHeightListener(callback: (height: number) => void): () => void`

Registers an event listener that is called when the navigation bar height changes.  
Returns a function to remove the listener.

```tsx
const remove = addNavBarHeightListener(h => console.log(h));
// Remove the listener
remove();
```

---

## 🧠 Implementation Overview (Android Side)

- Uses `WindowInsetsAnimationCompat.Callback` on Android 11 and above.
- Retrieves the navigation bar height from `WindowInsetsCompat.Type.navigationBars()`.
- Bridges events to the React Native JavaScript layer via `DeviceEventManagerModule.RCTDeviceEventEmitter`.

---

## 🤝 Contributing

Contributions, bug reports, and feature requests are welcome. Please open issues or pull requests on the GitHub repository.

---

## 📋 License

MIT License
