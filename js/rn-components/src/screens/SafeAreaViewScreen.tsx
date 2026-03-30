import React from 'react';
import { SafeAreaView, Text, StyleSheet, View } from 'react-native';
import { ScreenWrapper } from '../components/ScreenWrapper';

export const SafeAreaViewScreen = () => (
  <ScreenWrapper title="SafeAreaView">
    <Text style={styles.sectionTitle}>SafeAreaView 区域对比</Text>
    <Text style={styles.desc}>
      SafeAreaView 会自动避开刘海屏、底部横条等不安全区域，确保内容在可见范围内显示。
    </Text>

    <Text style={styles.sectionTitle}>不带 SafeAreaView</Text>
    <View style={styles.dangerZone}>
      <Text style={styles.dangerText}>这个区域可能被遮挡</Text>
    </View>

    <Text style={styles.sectionTitle}>使用 SafeAreaView</Text>
    <SafeAreaView style={styles.safeZone}>
      <Text style={styles.safeText}>这个区域安全显示</Text>
    </SafeAreaView>

    <Text style={styles.sectionTitle}>使用说明</Text>
    <View style={styles.infoBox}>
      <Text style={styles.infoText}>
        · 在 iOS 上自动处理刘海屏和底部安全区{'\n'}
        · 在 Android 上默认不生效，需要额外配置{'\n'}
        · 推荐在根布局或每个页面外层使用{'\n'}
        · 也可使用 react-native-safe-area-context 库获取更精确的安全区域
      </Text>
    </View>
  </ScreenWrapper>
);

const styles = StyleSheet.create({
  sectionTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#555',
    marginTop: 16,
    marginBottom: 8,
  },
  desc: {
    fontSize: 14,
    color: '#666',
    lineHeight: 22,
  },
  dangerZone: {
    backgroundColor: '#e74c3c',
    padding: 20,
    borderRadius: 8,
  },
  dangerText: {
    color: '#fff',
    textAlign: 'center',
    fontSize: 16,
  },
  safeZone: {
    backgroundColor: '#2ecc71',
    padding: 20,
    borderRadius: 8,
  },
  safeText: {
    color: '#fff',
    textAlign: 'center',
    fontSize: 16,
  },
  infoBox: {
    backgroundColor: '#fff',
    padding: 16,
    borderRadius: 8,
  },
  infoText: {
    fontSize: 14,
    color: '#555',
    lineHeight: 24,
  },
});
