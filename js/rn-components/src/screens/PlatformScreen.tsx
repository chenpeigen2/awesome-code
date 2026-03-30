import React from 'react';
import { Platform, Text, StyleSheet, View, Dimensions } from 'react-native';
import { ScreenWrapper } from '../components/ScreenWrapper';

const { width, height, fontScale, scale } = Dimensions.get('window');
const screen = Dimensions.get('screen');

export const PlatformScreen = () => (
  <ScreenWrapper title="Platform & Dimensions">
    <Text style={styles.sectionTitle}>Platform 信息</Text>
    <View style={styles.infoCard}>
      <InfoRow label="操作系统" value={Platform.OS} />
      <InfoRow label="版本" value={String(Platform.Version)} />
      <InfoRow label="是否 iOS" value={String(Platform.OS === 'ios')} />
      <InfoRow label="是否 Android" value={String(Platform.OS === 'android')} />
      <InfoRow label="是否 Web" value={String(Platform.OS === 'web')} />
    </View>

    <Text style={styles.sectionTitle}>平台条件渲染</Text>
    <View style={styles.platformBox}>
      {Platform.OS === 'ios' && <Text style={styles.ios}>当前是 iOS 平台</Text>}
      {Platform.OS === 'android' && <Text style={styles.android}>当前是 Android 平台</Text>}
      {Platform.OS === 'web' && <Text style={styles.web}>当前是 Web 平台</Text>}
    </View>

    <Text style={styles.sectionTitle}>Dimensions 屏幕尺寸</Text>
    <View style={styles.infoCard}>
      <InfoRow label="Window 宽度" value={`${width.toFixed(0)}px`} />
      <InfoRow label="Window 高度" value={`${height.toFixed(0)}px`} />
      <InfoRow label="Screen 宽度" value={`${screen.width.toFixed(0)}px`} />
      <InfoRow label="Screen 高度" value={`${screen.height.toFixed(0)}px`} />
      <InfoRow label="字体缩放比" value={fontScale.toFixed(2)} />
      <InfoRow label="像素密度比" value={scale.toFixed(2)} />
    </View>

    <Text style={styles.sectionTitle}>响应式示例</Text>
    <View style={[styles.responsiveBox, width > 400 ? styles.wide : styles.narrow]}>
      <Text style={styles.responsiveText}>
        {width > 400 ? '宽屏布局' : '窄屏布局'} ({width.toFixed(0)}px)
      </Text>
    </View>

    <Text style={styles.sectionTitle}>PixelRatio</Text>
    <View style={styles.infoCard}>
      <InfoRow label="设备像素比" value={String(scale)} />
    </View>
  </ScreenWrapper>
);

const InfoRow = ({ label, value }: { label: string; value: string }) => (
  <View style={styles.infoRow}>
    <Text style={styles.infoLabel}>{label}</Text>
    <Text style={styles.infoValue}>{value}</Text>
  </View>
);

const styles = StyleSheet.create({
  sectionTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#555',
    marginTop: 16,
    marginBottom: 8,
  },
  infoCard: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 12,
  },
  infoRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingVertical: 8,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  infoLabel: {
    fontSize: 15,
    color: '#555',
  },
  infoValue: {
    fontSize: 15,
    color: '#333',
    fontWeight: '600',
  },
  platformBox: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 20,
    alignItems: 'center',
  },
  ios: {
    fontSize: 16,
    color: '#3498db',
    fontWeight: '600',
  },
  android: {
    fontSize: 16,
    color: '#2ecc71',
    fontWeight: '600',
  },
  web: {
    fontSize: 16,
    color: '#e74c3c',
    fontWeight: '600',
  },
  responsiveBox: {
    borderRadius: 12,
    padding: 20,
    alignItems: 'center',
  },
  wide: {
    backgroundColor: '#2ecc71',
  },
  narrow: {
    backgroundColor: '#e74c3c',
  },
  responsiveText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
});
