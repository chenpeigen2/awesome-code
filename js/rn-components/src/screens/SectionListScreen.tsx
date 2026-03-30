import React from 'react';
import { SectionList, Text, View, StyleSheet } from 'react-native';
import { ScreenWrapper } from '../components/ScreenWrapper';

const SECTIONS = [
  {
    title: '水果',
    data: ['苹果', '香蕉', '橙子', '葡萄', '草莓'],
  },
  {
    title: '蔬菜',
    data: ['西红柿', '黄瓜', '胡萝卜', '白菜'],
  },
  {
    title: '饮料',
    data: ['可乐', '果汁', '牛奶', '茶', '咖啡', '矿泉水'],
  },
  {
    title: '零食',
    data: ['薯片', '饼干', '巧克力'],
  },
];

export const SectionListScreen = () => (
  <ScreenWrapper title="SectionList">
    <Text style={styles.sectionTitle}>分组列表</Text>
    <SectionList
      sections={SECTIONS}
      keyExtractor={(item, index) => item + index}
      renderItem={({ item }) => (
        <View style={styles.item}>
          <Text style={styles.itemText}>{item}</Text>
        </View>
      )}
      renderSectionHeader={({ section: { title } }) => (
        <View style={styles.sectionHeader}>
          <Text style={styles.sectionHeaderText}>{title}</Text>
          <Text style={styles.sectionCount}>{SECTIONS.find(s => s.title === title)?.data.length} 项</Text>
        </View>
      )}
      ItemSeparatorComponent={() => <View style={styles.separator} />}
      stickySectionHeadersEnabled
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
  sectionHeader: {
    backgroundColor: '#3498db',
    padding: 12,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    borderRadius: 8,
    marginBottom: 4,
  },
  sectionHeaderText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '700',
  },
  sectionCount: {
    color: 'rgba(255,255,255,0.8)',
    fontSize: 13,
  },
  item: {
    backgroundColor: '#fff',
    padding: 14,
    borderRadius: 8,
  },
  itemText: {
    fontSize: 15,
    color: '#333',
  },
  separator: {
    height: 4,
  },
});
