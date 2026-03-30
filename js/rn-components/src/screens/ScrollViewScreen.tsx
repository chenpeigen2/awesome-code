import React, { useState } from 'react';
import { ScrollView, Text, View, StyleSheet, RefreshControl } from 'react-native';
import { ScreenWrapper } from '../components/ScreenWrapper';

export const ScrollViewScreen = () => {
  const [refreshing, setRefreshing] = useState(false);

  const onRefresh = () => {
    setRefreshing(true);
    setTimeout(() => setRefreshing(false), 2000);
  };

  return (
    <ScreenWrapper title="ScrollView">
      <Text style={styles.sectionTitle}>垂直滚动</Text>
      <ScrollView style={styles.scrollView} showsVerticalScrollIndicator>
        {Array.from({ length: 10 }, (_, i) => (
          <View key={i} style={[styles.scrollItem, { backgroundColor: `hsl(${i * 36}, 70%, 60%)` }]}>
            <Text style={styles.scrollText}>项目 {i + 1}</Text>
          </View>
        ))}
      </ScrollView>

      <Text style={styles.sectionTitle}>水平滚动</Text>
      <ScrollView horizontal showsHorizontalScrollIndicator style={styles.horizontalScroll}>
        {Array.from({ length: 8 }, (_, i) => (
          <View key={i} style={[styles.horizontalItem, { backgroundColor: `hsl(${i * 45}, 65%, 55%)` }]}>
            <Text style={styles.scrollText}>{['🏠', '📷', '🎵', '📚', '🎮', '⚽', '🎸', '🎭'][i]}</Text>
          </View>
        ))}
      </ScrollView>

      <Text style={styles.sectionTitle}>下拉刷新 (RefreshControl)</Text>
      <ScrollView
        style={styles.scrollView}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} colors={['#3498db']} />
        }
      >
        <Text style={styles.refreshText}>
          {refreshing ? '正在刷新...' : '下拉此区域触发刷新'}
        </Text>
      </ScrollView>

      <Text style={styles.sectionTitle}>分页滚动 (pagingEnabled)</Text>
      <ScrollView horizontal pagingEnabled style={styles.pagingScroll} showsHorizontalScrollIndicator={false}>
        {['#e74c3c', '#3498db', '#2ecc71', '#f39c12'].map((color, i) => (
          <View key={i} style={[styles.page, { backgroundColor: color }]}>
            <Text style={styles.pageText}>第 {i + 1} 页</Text>
          </View>
        ))}
      </ScrollView>
    </ScreenWrapper>
  );
};

const styles = StyleSheet.create({
  sectionTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#555',
    marginTop: 16,
    marginBottom: 8,
  },
  scrollView: {
    maxHeight: 160,
    borderRadius: 8,
  },
  scrollItem: {
    padding: 16,
    marginHorizontal: 4,
    marginVertical: 4,
    borderRadius: 8,
  },
  scrollText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
    textAlign: 'center',
  },
  horizontalScroll: {
    maxHeight: 100,
  },
  horizontalItem: {
    width: 80,
    height: 80,
    borderRadius: 12,
    justifyContent: 'center',
    alignItems: 'center',
    marginHorizontal: 6,
  },
  refreshText: {
    textAlign: 'center',
    padding: 20,
    color: '#888',
  },
  pagingScroll: {
    maxHeight: 120,
    borderRadius: 12,
  },
  page: {
    width: 260,
    height: 120,
    justifyContent: 'center',
    alignItems: 'center',
    borderRadius: 12,
    marginHorizontal: 4,
  },
  pageText: {
    color: '#fff',
    fontSize: 22,
    fontWeight: '700',
  },
});
