const { createRunOncePlugin } = require('@expo/config-plugins');

/**
 * Expo Config Plugin for react-native-navbar-listener
 * This plugin ensures the native module is properly linked in managed workflow
 */
const withNavBarListener = (config) => {
  // No additional configuration needed for this module
  // The module will be auto-linked through the Expo modules system
  return config;
};

const pkg = require('./package.json');

module.exports = createRunOncePlugin(withNavBarListener, pkg.name, pkg.version);
