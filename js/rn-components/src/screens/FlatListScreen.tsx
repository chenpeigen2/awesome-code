import React from 'react';
import { FlatList, Text, View, StyleSheet, TouchableOpacity } from 'react-native';
import { ScreenWrapper } from '../components/ScreenWrapper';

const DATA = Array.from({ length: 30 }, (_, i) => ({
  id: String(i),
  title: `项目 ${i + 1}`,
  subtitle: `这是第 ${i + 1} 个列表项的描述`,
}));

export const FlatListScreen = () => (
  <ScreenWrapper title="FlatList">
    <Text style={styles.sectionTitle}>基本列表</Text>
    <FlatList
      data={DATA.slice(0, 6)}
      keyExtractor={(item) => item.id}
      renderItem={({ item, index }) => (
        <View style={[styles.item, { borderLeftColor: `hsl(${index * 30}, 60%, 50%)` }]}>
          <Text style={styles.itemTitle}>{item.title}</Text>
          <Text style={styles.itemSub}>{item.subtitle}</Text>
        </View>
      )}
      ItemSeparatorComponent={() => <View style={styles.separator} />}
    />

    <Text style={styles.sectionTitle}>水平 FlatList</Text>
    <FlatList
      horizontal
      data={DATA.slice(0, 8)}
      keyExtractor={(item) => item.id}
      showsHorizontalScrollIndicator={false}
      renderItem={({ item, index }) => (
        <View style={[styles.horizontalCard, { backgroundColor: `hsl(${index * 45}, 55%, 55%)` }]}>
          <Text style={styles.horizontalTitle}>{item.title}</Text>
        </View>
      )}
    />

    <Text style={styles.sectionTitle}>带点击效果</Text>
    <FlatList
      data={DATA.slice(0, 4)}
      keyExtractor={(item) => item.id}
      renderItem={({ item }) => (
        <TouchableOpacity style={styles.touchItem} activeOpacity={0.6}>
          <Text style={styles.itemTitle}>{item.title}</Text>
          <Text style={styles.arrow}>›</Text>
        </TouchableOpacity>
      )}
      ItemSeparatorComponent={() => <View style={styles.separator} />}
    />

    <Text style={styles.sectionTitle}>多列布局</Text>
    <FlatList
      data={DATA.slice(0, 8)}
      keyExtractor={(item) => item.id}
      numColumns={2}
      columnWrapperStyle={styles.row}
      renderItem={({ item, index }) => (
        <View style={[styles.gridItem, { backgroundColor: `hsl(${index * 45}, 50%, 60%)` }]}>
          <Text style={styles.gridText}>{item.title}</Text>
        </View>
      )}
    />
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
  item: {
    backgroundColor: '#fff',
    padding: 14,
    borderRadius: 8,
    borderLeftWidth: 4,
  },
  itemTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
  },
  itemSub: {
    fontSize: 13,
    color: '#888',
    marginTop: 2,
  },
  separator: {
    height: 8,
  },
  horizontalCard: {
    width: 100,
    height: 70,
    borderRadius: 10,
    justifyContent: 'center',
    alignItems: 'center',
    marginHorizontal: 6,
  },
  horizontalTitle: {
    color: '#fff',
    fontWeight: '600',
  },
  touchItem: {
    backgroundColor: '#fff',
    padding: 14,
    borderRadius: 8,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  arrow: {
    fontSize: 24,
    color: '#ccc',
  },
  row: {
    justifyContent: 'space-between',
    marginBottom: 8,
  },
  gridItem: {
    flex: 1,
    height: 70,
    borderRadius: 10,
    justifyContent: 'center',
    alignItems: 'center',
    marginHorizontal: 4,
  },
  gridText: {
    color: '#fff',
    fontWeight: '600',
  },
});
