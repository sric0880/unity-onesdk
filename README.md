# unity-onesdk
onesdk for unity.

## Install
```sh
cd command-tools
sudo python setup.py install
or
sudo pip install .
```
## Usage
```sh
## config
cd /usr/local/onesdk
## change the sdks-path to your own path
## add your own package name and channel id
vim onesdk.conf

##for help
onesdk -h
```
## platform-android
library project for onesdk, not tested.
### todo-list:
 1. implement login. should send login json data to onesdk server for validate.
 2. implement onesdk https server.
 3. fully tested.

### workflow:
 1. Login

[![Login](https://github.com/sric0880/unity-onesdk/blob/master/1.png)]

 2. Payment

[![Pay](https://github.com/sric0880/unity-onesdk/blob/master/2.png)]

## Links
* [android-manifest-merger](https://github.com/Bresiu/android-manifest-merger)
