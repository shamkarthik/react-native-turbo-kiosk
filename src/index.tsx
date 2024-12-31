import AwesomeLibrary from './NativeAwesomeLibrary';

// export function multiply(a: number, b: number): number {
//   return AwesomeLibrary.multiply(a, b);
// }

export function isAppInLockTaskMode() {
  return AwesomeLibrary.isAppInLockTaskMode();
}

export function clearDeviceOwnerApp() {
  return AwesomeLibrary.clearDeviceOwnerApp();
}

export function startLockTaskWith(additionalPackages: string[] | undefined) {
  return AwesomeLibrary.startLockTaskWith(additionalPackages);
}

export function startLockTask() {
  return AwesomeLibrary.startLockTask();
}

export function stopLockTask() {
  return AwesomeLibrary.stopLockTask();
}

export function enableExitByUnpinning() {
  return AwesomeLibrary.enableExitByUnpinning();
}

export function disableExitByUnpinning() {
  return AwesomeLibrary.disableExitByUnpinning();
}

export function requestDeviceAdminPermission() {
  return AwesomeLibrary.requestDeviceAdminPermission();
}
