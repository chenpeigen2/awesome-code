import React from 'react';
import { ActivityIndicator, Text, StyleSheet, View } from 'react-native';
import { ScreenWrapper } from '../components/ScreenWrapper';

export const ActivityIndicatorScreen = () => (
  <ScreenWrapper title="ActivityIndicator">
    <Text style={styles.sectionTitle}>基本加载指示器</Text>
    <View style={styles.row}>
      <ActivityIndicator size="small" />
      <ActivityIndicator size="large" />
    </View>

    <Text style={styles.sectionTitle}>不同颜色</Text>
    <View style={styles.row}>
      <ActivityIndicator size="large" color="#e74c3c" />
      <ActivityIndicator size="large" color="#3498db" />
      <ActivityIndicator size="large" color="#2ecc71" />
      <ActivityIndicator size="large" color="#f39c12" />
      <ActivityIndicator size="large" color="#9b59b6" />
    </View>

    <Text style={styles.sectionTitle}>加载场景</Text>
    <View style={styles.loadingCard}>
      <ActivityIndicator size="large" color="#3498db" />
      <Text style={styles.loadingText}>加载中，请稍候...</Text>
    </View>

    <Text style={styles.sectionTitle}>全屏加载遮罩</Text>
    <View style={styles.overlayContainer}>
      <View style={styles.overlay}>
        <ActivityIndicator size="large" color="#fff" />
        <Text style={styles.overlayText}>正在处理...</Text>
      </View>
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
  row: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
    paddingVertical: 16,
    backgroundColor: '#fff',
    borderRadius: 12,
  },
  loadingCard: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 24,
    alignItems: 'center',
    gap: 12,
  },
  loadingText: {
    fontSize: 16,
    color: '#555',
  },
  overlayContainer: {
    height: 150,
    borderRadius: 12,
    overflow: 'hidden',
    backgroundColor: '#ecf0f1',
    justifyContent: 'center',
    alignItems: 'center',
  },
  overlay: {
    backgroundColor: 'rgba(0,0,0,0.6)',
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    justifyContent: 'center',
    alignItems: 'center',
    gap: 8,
  },
  overlayText: {
    color: '#fff',
    fontSize: 14,
  },
});
