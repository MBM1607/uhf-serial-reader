/* eslint-disable @typescript-eslint/no-unsafe-argument */
/* eslint-disable @typescript-eslint/no-unsafe-call */
/* eslint-disable @typescript-eslint/no-unsafe-member-access */

/* eslint-disable @typescript-eslint/no-unsafe-return */

import type { Subscription } from 'expo-modules-core';
import { EventEmitter, NativeModulesProxy } from 'expo-modules-core';

import UhfSerialReaderModule from './UhfSerialReaderModule';

export function connectUhfReader(): boolean {
  return UhfSerialReaderModule.connect();
}

export function disconnectUhfReader(): void {
  return UhfSerialReaderModule.disconnect();
}

const emitter = new EventEmitter(
  UhfSerialReaderModule ?? NativeModulesProxy.UhfReader,
);

export type UhfEventPayload = {
  epc: string;
  rssi: string;
};

export function addUhfListener(
  listener: (event: UhfEventPayload) => void,
): Subscription {
  return emitter.addListener<UhfEventPayload>('onRead', listener);
}
