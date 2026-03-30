import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { ScreenWrapper } from '../components/ScreenWrapper';

export const StyleSheetScreen = () => (
  <ScreenWrapper title="StyleSheet">
    <Text style={styles.sectionTitle}>样式对比</Text>
    <Text style={styles.desc}>
      StyleSheet.create() 提供了类型安全的样式定义，并且会对样式进行验证和优化。
    </Text>

    <Text style={styles.sectionTitle}>边框样式</Text>
    <View style={styles.borderRow}>
      <View style={styles.border1}><Text>实线</Text></View>
      <View style={styles.border2}><Text>虚线</Text></View>
      <View style={styles.border3}><Text>点线</Text></View>
    </View>

    <Text style={styles.sectionTitle}>阴影 (iOS)</Text>
    <View style={styles.shadowRow}>
      <View style={[styles.shadowBox, styles.shadow1]}><Text>轻阴影</Text></View>
      <View style={[styles.shadowBox, styles.shadow2]}><Text>中阴影</Text></View>
      <View style={[styles.shadowBox, styles.shadow3]}><Text>重阴影</Text></View>
    </View>

    <Text style={styles.sectionTitle}>Flexbox 布局</Text>
    <Text style={styles.subTitle}>justifyContent</Text>
    <View style={styles.flexDemo}>
      <View style={styles.flexRow}><View style={styles.dot} /><View style={styles.dot} /><View style={styles.dot} /></View>
      <Text style={styles.flexLabel}>flex-start (默认)</Text>
    </View>
    <View style={styles.flexDemo}>
      <View style={[styles.flexRow, { justifyContent: 'center' }]}><View style={styles.dot} /><View style={styles.dot} /><View style={styles.dot} /></View>
      <Text style={styles.flexLabel}>center</Text>
    </View>
    <View style={styles.flexDemo}>
      <View style={[styles.flexRow, { justifyContent: 'space-between' }]}><View style={styles.dot} /><View style={styles.dot} /><View style={styles.dot} /></View>
      <Text style={styles.flexLabel}>space-between</Text>
    </View>
    <View style={styles.flexDemo}>
      <View style={[styles.flexRow, { justifyContent: 'space-around' }]}><View style={styles.dot} /><View style={styles.dot} /><View style={styles.dot} /></View>
      <Text style={styles.flexLabel}>space-around</Text>
    </View>

    <Text style={styles.subTitle}>alignItems</Text>
    <View style={styles.alignRow}>
      <View style={styles.alignDemo}><Text style={styles.alignLabel}>stretch</Text><View style={styles.alignBar} /></View>
      <View style={[styles.alignDemo, { alignItems: 'center' }]}><Text style={styles.alignLabel}>center</Text><View style={styles.alignBarCenter} /></View>
      <View style={[styles.alignDemo, { alignItems: 'flex-end' }]}><Text style={styles.alignLabel}>flex-end</Text><View style={styles.alignBarEnd} /></View>
    </View>

    <Text style={styles.sectionTitle}>绝对定位</Text>
    <View style={styles.positionContainer}>
      <View style={[styles.positionBox, { top: 0, left: 0 }]}><Text style={styles.posText}>TL</Text></View>
      <View style={[styles.positionBox, { top: 0, right: 0 }]}><Text style={styles.posText}>TR</Text></View>
      <View style={[styles.positionBox, { bottom: 0, left: 0 }]}><Text style={styles.posText}>BL</Text></View>
      <View style={[styles.positionBox, { bottom: 0, right: 0 }]}><Text style={styles.posText}>BR</Text></View>
      <View style={[styles.positionBox, { top: '50%', left: '50%', transform: [{ translateX: -20 }, { translateY: -20 }] }]}><Text style={styles.posText}>C</Text></View>
    </View>
  </ScreenWrapper>
);

const styles = StyleSheet.create({
  sectionTitle: { fontSize: 16, fontWeight: '600', color: '#555', marginTop: 16, marginBottom: 8 },
  subTitle: { fontSize: 14, fontWeight: '500', color: '#777', marginTop: 12, marginBottom: 6 },
  desc: { fontSize: 14, color: '#666', lineHeight: 22 },
  borderRow: { flexDirection: 'row', justifyContent: 'space-around', gap: 8 },
  border1: { borderWidth: 2, borderColor: '#333', padding: 12, borderRadius: 8, flex: 1, alignItems: 'center' },
  border2: { borderWidth: 2, borderColor: '#333', borderStyle: 'dashed', padding: 12, borderRadius: 8, flex: 1, alignItems: 'center' },
  border3: { borderWidth: 2, borderColor: '#333', borderStyle: 'dotted', padding: 12, borderRadius: 8, flex: 1, alignItems: 'center' },
  shadowRow: { flexDirection: 'row', justifyContent: 'space-around', gap: 8 },
  shadowBox: { backgroundColor: '#fff', padding: 16, borderRadius: 8, flex: 1, alignItems: 'center' },
  shadow1: { shadowColor: '#000', shadowOffset: { width: 0, height: 1 }, shadowOpacity: 0.1, shadowRadius: 2 },
  shadow2: { shadowColor: '#000', shadowOffset: { width: 0, height: 3 }, shadowOpacity: 0.2, shadowRadius: 4 },
  shadow3: { shadowColor: '#000', shadowOffset: { width: 0, height: 6 }, shadowOpacity: 0.3, shadowRadius: 8 },
  flexDemo: { marginBottom: 8 },
  flexRow: { flexDirection: 'row', backgroundColor: '#ecf0f1', borderRadius: 8, padding: 12 },
  dot: { width: 24, height: 24, borderRadius: 12, backgroundColor: '#3498db', marginHorizontal: 4 },
  flexLabel: { fontSize: 12, color: '#888', marginTop: 4 },
  alignRow: { flexDirection: 'row', gap: 8 },
  alignDemo: { flex: 1, backgroundColor: '#ecf0f1', borderRadius: 8, padding: 8, height: 80 },
  alignLabel: { fontSize: 11, color: '#888' },
  alignBar: { width: 30, height: 40, backgroundColor: '#e74c3c', borderRadius: 4, marginTop: 4 },
  alignBarCenter: { width: 30, height: 40, backgroundColor: '#e74c3c', borderRadius: 4, marginTop: 4, alignSelf: 'center' },
  alignBarEnd: { width: 30, height: 40, backgroundColor: '#e74c3c', borderRadius: 4, marginTop: 4, alignSelf: 'flex-end' },
  positionContainer: { height: 140, backgroundColor: '#ecf0f1', borderRadius: 12, position: 'relative' },
  positionBox: { position: 'absolute', width: 40, height: 40, backgroundColor: '#3498db', borderRadius: 8, justifyContent: 'center', alignItems: 'center' },
  posText: { color: '#fff', fontSize: 12, fontWeight: '700' },
});
