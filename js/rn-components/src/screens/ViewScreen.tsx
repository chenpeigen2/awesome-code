import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { ScreenWrapper } from '../components/ScreenWrapper';

export const ViewScreen = () => (
  <ScreenWrapper title="View">
    <Text style={styles.sectionTitle}>基础容器</Text>
    <View style={[styles.box, { backgroundColor: '#e74c3c' }]} />
    <View style={[styles.box, { backgroundColor: '#3498db' }]} />
    <View style={[styles.box, { backgroundColor: '#2ecc71' }]} />

    <Text style={styles.sectionTitle}>Flexbox 布局</Text>
    <View style={styles.flexRow}>
      <View style={[styles.flexItem, { backgroundColor: '#9b59b6' }]} />
      <View style={[styles.flexItem, { backgroundColor: '#f39c12' }]} />
      <View style={[styles.flexItem, { backgroundColor: '#1abc9c' }]} />
    </View>

    <Text style={styles.sectionTitle}>嵌套 View</Text>
    <View style={styles.nestedOuter}>
      <View style={styles.nestedInner}>
        <Text style={styles.nestedText}>内层 View</Text>
      </View>
    </View>

    <Text style={styles.sectionTitle}>边框和圆角</Text>
    <View style={styles.borderRow}>
      <View style={[styles.borderBox, { borderRadius: 0 }]}><Text>直角</Text></View>
      <View style={[styles.borderBox, { borderRadius: 12 }]}><Text>圆角</Text></View>
      <View style={[styles.borderBox, { borderRadius: 50 }]}><Text>圆形</Text></View>
    </View>

    <Text style={styles.sectionTitle}>透明度</Text>
    <View style={styles.opacityRow}>
      <View style={[styles.opacityBox, { opacity: 1 }]}><Text style={styles.white}>1.0</Text></View>
      <View style={[styles.opacityBox, { opacity: 0.7 }]}><Text style={styles.white}>0.7</Text></View>
      <View style={[styles.opacityBox, { opacity: 0.4 }]}><Text style={styles.white}>0.4</Text></View>
      <View style={[styles.opacityBox, { opacity: 0.2 }]}><Text style={styles.white}>0.2</Text></View>
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
  box: {
    width: '100%',
    height: 60,
    borderRadius: 8,
    marginBottom: 8,
  },
  flexRow: {
    flexDirection: 'row',
    gap: 8,
  },
  flexItem: {
    flex: 1,
    height: 80,
    borderRadius: 8,
  },
  nestedOuter: {
    backgroundColor: '#ddd',
    padding: 20,
    borderRadius: 8,
  },
  nestedInner: {
    backgroundColor: '#fff',
    padding: 16,
    borderRadius: 8,
    alignItems: 'center',
  },
  nestedText: {
    color: '#333',
  },
  borderRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    gap: 8,
  },
  borderBox: {
    flex: 1,
    height: 60,
    backgroundColor: '#ecf0f1',
    borderWidth: 2,
    borderColor: '#bdc3c7',
    justifyContent: 'center',
    alignItems: 'center',
  },
  opacityRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    gap: 8,
  },
  opacityBox: {
    flex: 1,
    height: 60,
    backgroundColor: '#2c3e50',
    justifyContent: 'center',
    alignItems: 'center',
    borderRadius: 8,
  },
  white: { color: '#fff' },
});
