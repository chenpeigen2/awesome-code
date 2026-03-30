import React from 'react';
import { Text, StyleSheet } from 'react-native';
import { ScreenWrapper } from '../components/ScreenWrapper';

export const TextScreen = () => (
  <ScreenWrapper title="Text">
    <Text style={styles.sectionTitle}>基本文本</Text>
    <Text>这是一段普通文本。</Text>

    <Text style={styles.sectionTitle}>字体大小</Text>
    <Text style={{ fontSize: 12 }}>12px 文本</Text>
    <Text style={{ fontSize: 16 }}>16px 文本</Text>
    <Text style={{ fontSize: 20 }}>20px 文本</Text>
    <Text style={{ fontSize: 28 }}>28px 文本</Text>

    <Text style={styles.sectionTitle}>字体粗细</Text>
    <Text style={{ fontWeight: '400' }}>Regular (400)</Text>
    <Text style={{ fontWeight: '500' }}>Medium (500)</Text>
    <Text style={{ fontWeight: '600' }}>SemiBold (600)</Text>
    <Text style={{ fontWeight: '700' }}>Bold (700)</Text>
    <Text style={{ fontWeight: '900' }}>Black (900)</Text>

    <Text style={styles.sectionTitle}>颜色</Text>
    <Text style={{ color: '#e74c3c' }}>红色文本</Text>
    <Text style={{ color: '#3498db' }}>蓝色文本</Text>
    <Text style={{ color: '#2ecc71' }}>绿色文本</Text>
    <Text style={{ color: '#9b59b6' }}>紫色文本</Text>

    <Text style={styles.sectionTitle}>文本对齐</Text>
    <Text style={{ textAlign: 'left' }}>左对齐</Text>
    <Text style={{ textAlign: 'center' }}>居中对齐</Text>
    <Text style={{ textAlign: 'right' }}>右对齐</Text>

    <Text style={styles.sectionTitle}>文本装饰</Text>
    <Text style={{ textDecorationLine: 'underline' }}>下划线文本</Text>
    <Text style={{ textDecorationLine: 'line-through' }}>删除线文本</Text>
    <Text style={{ textDecorationLine: 'underline line-through' }}>下划线 + 删除线</Text>

    <Text style={styles.sectionTitle}>字体风格</Text>
    <Text style={{ fontStyle: 'normal' }}>正常风格</Text>
    <Text style={{ fontStyle: 'italic' }}>斜体风格</Text>

    <Text style={styles.sectionTitle}>行高和间距</Text>
    <Text style={{ lineHeight: 20, letterSpacing: 0 }}>正常行高和字距</Text>
    <Text style={{ lineHeight: 30, letterSpacing: 2 }}>大行高和宽字距</Text>

    <Text style={styles.sectionTitle}>文本截断</Text>
    <Text numberOfLines={1} style={{ fontSize: 16 }}>
      这是一段很长的文本用来演示 numberOfLines 属性，当文本超出容器宽度时会被截断显示省略号...
    </Text>

    <Text style={styles.sectionTitle}>嵌套文本</Text>
    <Text style={{ fontSize: 16 }}>
      这是一段<Text style={{ fontWeight: '700', color: '#e74c3c' }}>嵌套的红色粗体</Text>
      文本，以及<Text style={{ backgroundColor: '#f1c40f' }}>黄色背景高亮</Text>的效果。
    </Text>
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
});
