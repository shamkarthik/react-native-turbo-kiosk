import React, { useEffect, useState } from 'react';
import {
  AppState,
  type AppStateStatus,
  BackHandler,
  Platform,
  StyleSheet,
  Text,
  View,
  Switch,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage'; // Import AsyncStorage
import {
  startLockTask,
  stopLockTask,
  enableExitByUnpinning,
  disableExitByUnpinning,
  clearDeviceOwnerApp,
  isAppInLockTaskMode,
  requestDeviceAdminPermission,
} from 'react-native-awesome-library';

export default function App() {
  const [isKioskEnabled, setIsKioskEnabled] = useState(false);
  const [shouldExitByUnpinning, setShouldExitByUnpinning] = useState(false);
  const [shouldKeepAskingToStartKiosk, setShouldKeepAskingToStartKiosk] =
    useState(false);

  // Load kiosk mode setting from AsyncStorage on app startup
  useEffect(() => {
    const loadKioskMode = async () => {
      const storedKioskMode = await AsyncStorage.getItem('isKioskEnabled');
      if (storedKioskMode !== null) {
        setIsKioskEnabled(JSON.parse(storedKioskMode));
      }
    };
    loadKioskMode();
  }, []);

  useEffect(() => {
    if (Platform.OS !== 'android') {
      return;
    }

    if (isKioskEnabled) {
      startLockTask(); // Replaced startKioskMode with startLockTask
      // startLockTaskWith(["com.google.android.youtube"]);
    } else {
      stopLockTask(); // Replaced exitKioskMode with stopLockTask
    }

    // Persist the kiosk mode setting to AsyncStorage whenever it changes
    const saveKioskMode = async () => {
      await AsyncStorage.setItem(
        'isKioskEnabled',
        JSON.stringify(isKioskEnabled)
      );
    };
    saveKioskMode();
  }, [isKioskEnabled]);

  useEffect(() => {
    if (shouldExitByUnpinning) {
      enableExitByUnpinning();
    } else {
      disableExitByUnpinning();
    }
  }, [shouldExitByUnpinning]);

  useEffect(() => {
    let interval: NodeJS.Timeout;
    if (isKioskEnabled && shouldKeepAskingToStartKiosk) {
      interval = setInterval(() => {
        if (!isAppInLockTaskMode()) {
          startLockTask(); // Replaced startKioskMode with startLockTask
        }
      }, 1000);
    }

    return () => {
      clearInterval(interval);
    };
  }, [isKioskEnabled, shouldKeepAskingToStartKiosk]);

  // Handle back press and app state
  useEffect(() => {
    const onBackPress = () => {
      return isKioskEnabled; // Prevent back press if kiosk mode is enabled
    };
    const backHandler = BackHandler.addEventListener(
      'hardwareBackPress',
      onBackPress
    );

    const onPause = (nextAppState: AppStateStatus) => {
      if (nextAppState !== 'active') {
        // No function for onRecentButtonPressed, so removing it
      }
    };
    const pauseHandler = AppState.addEventListener('change', onPause);

    return () => {
      backHandler.remove();
      pauseHandler.remove();
    };
  }, [isKioskEnabled]);

  // Function to clear device owner app
  const handleClearDeviceOwner = () => {
    clearDeviceOwnerApp();
  };

  const handleDeviceOwnerRequest = () => {
    requestDeviceAdminPermission();
  };

  return (
    <View style={styles.container}>
      <Text style={styles.header}>Lock Task Mode Example</Text>

      <View style={styles.switchContainer}>
        <Text>Lock Task Mode</Text>
        <Switch
          value={isKioskEnabled}
          onValueChange={(value) => setIsKioskEnabled(value)}
        />
      </View>

      <View style={styles.switchContainer}>
        <Text>Exit by unpinning</Text>
        <Switch
          value={shouldExitByUnpinning}
          onValueChange={(value) => setShouldExitByUnpinning(value)}
        />
      </View>

      <View style={styles.switchContainer}>
        <Text>Keep asking to start lock task</Text>
        <Switch
          value={shouldKeepAskingToStartKiosk}
          onValueChange={(value) => setShouldKeepAskingToStartKiosk(value)}
        />
      </View>

      <View style={styles.buttonContainer}>
        <Text style={styles.button} onPress={handleDeviceOwnerRequest}>
          Request Device Owner App
        </Text>
      </View>
      <View style={styles.buttonContainer}>
        <Text style={styles.button} onPress={handleClearDeviceOwner}>
          Clear Device Owner App
        </Text>
      </View>

      <Text style={styles.statusText}>
        {isKioskEnabled
          ? 'Lock Task Mode is Enabled'
          : 'Lock Task Mode is Disabled'}
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
    gap: 10,
  },
  header: {
    fontSize: 24,
    marginBottom: 20,
  },
  switchContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    width: '80%',
    marginVertical: 10,
  },
  buttonContainer: {
    marginTop: 20,
  },
  button: {
    fontSize: 16,
    color: 'blue',
    textDecorationLine: 'underline',
  },
  statusText: {
    fontSize: 16,
    marginVertical: 5,
  },
});
