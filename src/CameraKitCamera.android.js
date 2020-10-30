import * as _ from 'lodash';
import React, { Component } from 'react';
import {
  requireNativeComponent,
  NativeModules,
  processColor,
} from 'react-native';

const NativeCamera = requireNativeComponent('CameraView', null);
const NativeCameraModule = NativeModules.RNKitCameraModule;
const TORCH_MODE_ON = 'on';
const TORCH_MODE_CALL_ARG = 'torch';

export default class CameraKitCamera extends React.Component {
  async logData() {
    console.log('front Camera?', await NativeCameraModule.hasFrontCamera());
    console.log('hasFlash?', await NativeCameraModule.hasFlashForCurrentCamera());
    console.log('flashMode?', await NativeCameraModule.getFlashMode());
  }

  /**
   *
   * @param {
    shouldShowAlert: '0|1', // '1' will show alert
    title: '', // Permission alert title
    message: '', // Permission alert message
    button_text: '' // Permission alert button text
  } rational
   */
  static async requestDeviceCameraAuthorization(rational = {
    shouldShowAlert: '0',
    title: '',
    message: '',
    button_text: ''
  }) {
    const androidDialogValues = Object.values(rational).reduce((prev, curr, index) => {
      if(index === 0) {
        return `${curr}`
      }
      return `${prev},${curr}`
    }, '')
    return await NativeCameraModule.requestDeviceCameraAuthorization(androidDialogValues);
  }

  async capture(saveToCameraRoll = true) {
    return await NativeCameraModule.capture(saveToCameraRoll);
  }

  async changeCamera() {
    return await NativeCameraModule.changeCamera();
  }

  async setTorchMode(torchMode) {
    if (torchMode == TORCH_MODE_ON){
      return await NativeCameraModule.setFlashMode(TORCH_MODE_CALL_ARG);
    }
    return await NativeCameraModule.setFlashMode(torchMode);
  }

  async setFlashMode(flashMode = 'auto') {
    return await NativeCameraModule.setFlashMode(flashMode);
  }

  static async checkDeviceCameraAuthorizationStatus() {
    return await NativeCameraModule.checkDeviceCameraAuthorizationStatus();
  }

  static async hasCameraPermission() {
    return await NativeCameraModule.hasCameraPermission();
  }

  render() {
    const transformedProps = _.cloneDeep(this.props);
    _.update(transformedProps, 'cameraOptions.ratioOverlayColor', (c) => processColor(c));
    _.update(transformedProps, 'frameColor', (c) => processColor(c));
    _.update(transformedProps, 'laserColor', (c) => processColor(c));
    _.update(transformedProps, 'surfaceColor', (c) => processColor(c));

    return <NativeCamera {...transformedProps}/>;
  }
}
