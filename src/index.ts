import {
	EventEmitter,
	NativeModulesProxy,
	Subscription,
} from 'expo-modules-core';

// Import the native module. On web, it will be resolved to UhfSerialReader.web.ts
// and on native platforms to UhfSerialReader.ts
import { ChangeEventPayload } from './UhfSerialReader.types';
import UhfSerialReaderModule from './UhfSerialReaderModule';

// Get the native constant value.
export const PI = UhfSerialReaderModule.PI;

export function hello(): string {
	return UhfSerialReaderModule.hello();
}

export async function setValueAsync(value: string) {
	return await UhfSerialReaderModule.setValueAsync(value);
}

const emitter = new EventEmitter(
	UhfSerialReaderModule ?? NativeModulesProxy.UhfSerialReader
);

export function addChangeListener(
	listener: (event: ChangeEventPayload) => void
): Subscription {
	return emitter.addListener<ChangeEventPayload>('onChange', listener);
}

export { ChangeEventPayload };
