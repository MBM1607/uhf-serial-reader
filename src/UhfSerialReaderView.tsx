import { requireNativeViewManager } from 'expo-modules-core';
import * as React from 'react';

import { UhfSerialReaderViewProps } from './UhfSerialReader.types';

const NativeView: React.ComponentType<UhfSerialReaderViewProps> =
  requireNativeViewManager('UhfSerialReader');

export default function UhfSerialReaderView(props: UhfSerialReaderViewProps) {
  return <NativeView {...props} />;
}
