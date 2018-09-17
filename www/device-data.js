var exec = require('cordova/exec');

function DeviceDataPlugin() { };

DeviceDataPlugin.prototype.getInfo = function(success, error) {
    exec(success, error, 'DeviceDataPlugin', 'getInfo', []);
}

var instance = new DeviceDataPlugin();
module.exports = instance;