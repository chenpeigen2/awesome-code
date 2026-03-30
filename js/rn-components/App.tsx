import React, { useState } from 'react';
import { StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { StatusBar } from 'expo-status-bar';
import { SafeAreaView } from 'react-native-safe-area-context';
import { HomeScreen } from './src/screens/HomeScreen';
import { ViewScreen } from './src/screens/ViewScreen';
import { TextScreen } from './src/screens/TextScreen';
import { ImageScreen } from './src/screens/ImageScreen';
import { TextInputScreen } from './src/screens/TextInputScreen';
import { ScrollViewScreen } from './src/screens/ScrollViewScreen';
import { FlatListScreen } from './src/screens/FlatListScreen';
import { SectionListScreen } from './src/screens/SectionListScreen';
import { TouchableScreen } from './src/screens/TouchableScreen';
import { ButtonScreen } from './src/screens/ButtonScreen';
import { SwitchScreen } from './src/screens/SwitchScreen';
import { ActivityIndicatorScreen } from './src/screens/ActivityIndicatorScreen';
import { ModalScreen } from './src/screens/ModalScreen';
import { StatusBarScreen } from './src/screens/StatusBarScreen';
import { SafeAreaViewScreen } from './src/screens/SafeAreaViewScreen';
import { KeyboardAvoidingViewScreen } from './src/screens/KeyboardAvoidingViewScreen';
import { AnimatedScreen } from './src/screens/AnimatedScreen';
import { PlatformScreen } from './src/screens/PlatformScreen';
import { StyleSheetScreen } from './src/screens/StyleSheetScreen';
import { RefreshControlScreen } from './src/screens/RefreshControlScreen';
import { APIScreen } from './src/screens/APIScreen';

const SCREENS: Record<string, React.FC> = {
  View: ViewScreen,
  Text: TextScreen,
  Image: ImageScreen,
  TextInput: TextInputScreen,
  ScrollView: ScrollViewScreen,
  FlatList: FlatListScreen,
  SectionList: SectionListScreen,
  Touchable: TouchableScreen,
  Button: ButtonScreen,
  Switch: SwitchScreen,
  ActivityIndicator: ActivityIndicatorScreen,
  Modal: ModalScreen,
  StatusBar: StatusBarScreen,
  SafeAreaView: SafeAreaViewScreen,
  KeyboardAvoidingView: KeyboardAvoidingViewScreen,
  Animated: AnimatedScreen,
  Platform: PlatformScreen,
  StyleSheet: StyleSheetScreen,
  RefreshControl: RefreshControlScreen,
  API: APIScreen,
};

export default function App() {
  const [currentScreen, setCurrentScreen] = useState<string | null>(null);

  const ScreenComponent = currentScreen ? SCREENS[currentScreen] : null;

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar style="auto" />
      <View style={styles.header}>
        {currentScreen ? (
          <TouchableOpacity style={styles.backBtn} onPress={() => setCurrentScreen(null)}>
            <Text style={styles.backText}>‹ 返回</Text>
          </TouchableOpacity>
        ) : null}
        <Text style={styles.headerTitle}>
          {currentScreen || 'React Native 组件'}
        </Text>
      </View>
      <View style={styles.content}>
        {ScreenComponent ? (
          <ScreenComponent />
        ) : (
          <HomeScreen onNavigate={setCurrentScreen} />
        )}
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 16,
    paddingVertical: 12,
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
  },
  headerTitle: {
    fontSize: 18,
    fontWeight: '700',
    color: '#333',
  },
  backBtn: {
    marginRight: 12,
    paddingVertical: 4,
    paddingRight: 8,
  },
  backText: {
    fontSize: 20,
    color: '#3498db',
    fontWeight: '600',
  },
  content: {
    flex: 1,
  },
});
