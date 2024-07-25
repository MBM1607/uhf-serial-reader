import { NativeModulesProxy, EventEmitter, Subscription } from 'expo-modules-core';

// Import the native module. On web, it will be resolved to UhfSerialReader.web.ts
// and on native platforms to UhfSerialReader.ts
import UhfSerialReaderModule from './UhfSerialReaderModule';
import UhfSerialReaderView from './UhfSerialReaderView';
import { ChangeEventPayload, UhfSerialReaderViewProps } from './UhfSerialReader.types';

// Get the native constant value.
export const PI = UhfSerialReaderModule.PI;

export function hello(): string {
  return UhfSerialReaderModule.hello();
}

export async function setValueAsync(value: string) {
  return await UhfSerialReaderModule.setValueAsync(value);
}

const emitter = new EventEmitter(UhfSerialReaderModule ?? NativeModulesProxy.UhfSerialReader);

export function addChangeListener(listener: (event: ChangeEventPayload) => void): Subscription {
  return emitter.addListener<ChangeEventPayload>('onChange', listener);
}

export { UhfSerialReaderView, UhfSerialReaderViewProps, ChangeEventPayload };
