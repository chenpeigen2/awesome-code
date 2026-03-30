import React, { useState, useCallback } from 'react';
import { RefreshControl, Text, View, StyleSheet, ScrollView } from 'react-native';
import { ScreenWrapper } from '../components/ScreenWrapper';

export const RefreshControlScreen = () => {
  const [refreshing, setRefreshing] = useState(false);
  const [items, setItems] = useState(Array.from({ length: 5 }, (_, i) => `项目 ${i + 1}`));

  const onRefresh = useCallback(() => {
    setRefreshing(true);
    setTimeout(() => {
      setItems(Array.from({ length: 5 }, (_, i) => `项目 ${i + 1} (刷新于 ${new Date().toLocaleTimeString()})`));
      setRefreshing(false);
    }, 2000);
  }, []);

  return (
    <ScreenWrapper title="RefreshControl">
      <Text style={styles.sectionTitle}>下拉刷新</Text>
      <Text style={styles.desc}>向下拉触发刷新操作，2秒后刷新完成。</Text>

      <ScrollView
        style={styles.list}
        refreshControl={
          <RefreshControl
            refreshing={refreshing}
            onRefresh={onRefresh}
            colors={['#e74c3c', '#3498db', '#2ecc71']}
            tintColor="#3498db"
            title="正在刷新..."
            titleColor="#888"
          />
        }
      >
        {items.map((item, i) => (
          <View key={i} style={styles.item}>
            <Text style={styles.itemText}>{item}</Text>
          </View>
        ))}
      </ScrollView>

      <Text style={styles.sectionTitle}>属性说明</Text>
      <View style={styles.infoCard}>
        {[
          ['refreshing', '是否显示刷新指示器'],
          ['onRefresh', '刷新触发回调'],
          ['colors', '指示器颜色 (Android)'],
          ['tintColor', '指示器颜色 (iOS)'],
          ['title', '刷新标题 (iOS)'],
          ['progressViewOffset', '指示器偏移 (Android)'],
        ].map(([prop, desc], i) => (
          <View key={i} style={styles.row}>
            <Text style={styles.propName}>{prop}</Text>
            <Text style={styles.propDesc}>{desc}</Text>
          </View>
        ))}
      </View>
    </ScreenWrapper>
  );
};

const styles = StyleSheet.create({
  sectionTitle: { fontSize: 16, fontWeight: '600', color: '#555', marginTop: 16, marginBottom: 8 },
  desc: { fontSize: 14, color: '#666', lineHeight: 22 },
  list: { maxHeight: 300, borderRadius: 8 },
  item: {
    backgroundColor: '#fff',
    padding: 14,
    borderRadius: 8,
    marginBottom: 6,
  },
  itemText: { fontSize: 15, color: '#333' },
  infoCard: { backgroundColor: '#fff', borderRadius: 12, padding: 12 },
  row: { paddingVertical: 8, borderBottomWidth: 1, borderBottomColor: '#f0f0f0' },
  propName: { fontSize: 14, fontWeight: '600', color: '#333', fontFamily: 'monospace' },
  propDesc: { fontSize: 13, color: '#888', marginTop: 2 },
});
