/**
 * Created by lihui on 16/3/3.
 */
'use strict';

var RCTDeviceEventEmitter = require('RCTDeviceEventEmitter');
var RNSettings = require('NativeModules').RNSettings;

var invariant = require('fbjs/lib/invariant');

var subscriptions: Array<{keys: Array<string>; callback: ?Function}> = [];

var RLSettings = {
    _settings: RNSettings && RNSettings.settings,

    get(key: string): mixed {
    return this._settings[key];
},

set(settings: Object) {
    this._settings = Object.assign(this._settings, settings);
    RNSettings.setValues(settings);
},

watchKeys(keys: string | Array<string>, callback: Function): number {
    if (typeof keys === 'string') {
        keys = [keys];
    }

    invariant(
        Array.isArray(keys),
        'keys should be a string or array of strings'
    );

    var sid = subscriptions.length;
    subscriptions.push({keys: keys, callback: callback});
    return sid;
},

clearWatch(watchId: number) {
    if (watchId < subscriptions.length) {
        subscriptions[watchId] = {keys: [], callback: null};
    }
},

_sendObservations(body: Object) {
    Object.keys(body).forEach((key) => {
        var newValue = body[key];
    var didChange = this._settings[key] !== newValue;
    this._settings[key] = newValue;

    if (didChange) {
        subscriptions.forEach((sub) => {
            if (sub.keys.indexOf(key) !== -1 && sub.callback) {
            sub.callback();
        }
    });
    }
});
},
};

RCTDeviceEventEmitter.addListener(
    'settingsUpdated',
    RLSettings._sendObservations.bind(RLSettings)
);

module.exports = RLSettings;
