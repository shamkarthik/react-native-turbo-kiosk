import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  isAppInLockTaskMode(): boolean;

  clearDeviceOwnerApp(): void;

  startLockTaskWith(additionalPackages?: Array<string>): void;

  startLockTask(): void;

  stopLockTask(): void;

  enableExitByUnpinning(): void;

  disableExitByUnpinning(): void;

  requestDeviceAdminPermission(): void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('AwesomeLibrary');
