import React from 'react';
import { Button, Text, StyleSheet, View, Alert } from 'react-native';
import { ScreenWrapper } from '../components/ScreenWrapper';

export const ButtonScreen = () => (
  <ScreenWrapper title="Button">
    <Text style={styles.sectionTitle}>基本按钮</Text>
    <Button title="默认按钮" onPress={() => Alert.alert('提示', '按钮被点击')} />

    <Text style={styles.sectionTitle}>带颜色</Text>
    <View style={styles.row}>
      <Button title="红色" color="#e74c3c" onPress={() => {}} />
      <Button title="蓝色" color="#3498db" onPress={() => {}} />
      <Button title="绿色" color="#2ecc71" onPress={() => {}} />
    </View>

    <Text style={styles.sectionTitle}>禁用状态</Text>
    <Button title="禁用按钮" disabled onPress={() => {}} />

    <Text style={styles.sectionTitle}>Alert 弹窗</Text>
    <Button
      title="普通弹窗"
      onPress={() => Alert.alert('标题', '这是一个普通弹窗')}
    />
    <View style={styles.gap} />
    <Button
      title="多按钮弹窗"
      onPress={() =>
        Alert.alert('确认操作', '你确定要执行此操作吗？', [
          { text: '取消', style: 'cancel' },
          { text: '确定', onPress: () => {} },
          { text: '删除', style: 'destructive', onPress: () => {} },
        ])
      }
    />
    <View style={styles.gap} />
    <Button
      title="输入弹窗"
      onPress={() =>
        Alert.prompt('输入名称', '请输入你的名字', (text) => Alert.alert('你输入了', text))
      }
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
  row: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    gap: 8,
  },
  gap: {
    height: 8,
  },
});
