import React, { useState } from 'react';
import { Switch, Text, StyleSheet, View } from 'react-native';
import { ScreenWrapper } from '../components/ScreenWrapper';

export const SwitchScreen = () => {
  const [basic, setBasic] = useState(false);
  const [darkMode, setDarkMode] = useState(false);
  const [notify, setNotify] = useState(true);
  const [sound, setSound] = useState(true);
  const [disabled] = useState(true);

  return (
    <ScreenWrapper title="Switch">
      <Text style={styles.sectionTitle}>基本开关</Text>
      <View style={styles.row}>
        <Text style={styles.label}>开关状态: {basic ? '开' : '关'}</Text>
        <Switch value={basic} onValueChange={setBasic} />
      </View>

      <Text style={styles.sectionTitle}>自定义颜色</Text>
      <View style={styles.row}>
        <Text style={styles.label}>红色</Text>
        <Switch value={true} onValueChange={() => {}} trackColor={{ false: '#ccc', true: '#e74c3c' }} thumbColor="#fff" />
      </View>
      <View style={styles.row}>
        <Text style={styles.label}>绿色</Text>
        <Switch value={true} onValueChange={() => {}} trackColor={{ false: '#ccc', true: '#2ecc71' }} thumbColor="#fff" />
      </View>
      <View style={styles.row}>
        <Text style={styles.label}>紫色</Text>
        <Switch value={true} onValueChange={() => {}} trackColor={{ false: '#ccc', true: '#9b59b6' }} thumbColor="#fff" />
      </View>

      <Text style={styles.sectionTitle}>设置面板示例</Text>
      <View style={styles.panel}>
        <View style={styles.row}>
          <Text style={styles.label}>深色模式</Text>
          <Switch value={darkMode} onValueChange={setDarkMode} trackColor={{ true: '#3498db' }} />
        </View>
        <View style={styles.divider} />
        <View style={styles.row}>
          <Text style={styles.label}>推送通知</Text>
          <Switch value={notify} onValueChange={setNotify} trackColor={{ true: '#3498db' }} />
        </View>
        <View style={styles.divider} />
        <View style={styles.row}>
          <Text style={styles.label}>声音</Text>
          <Switch value={sound} onValueChange={setSound} trackColor={{ true: '#3498db' }} />
        </View>
      </View>

      <Text style={styles.sectionTitle}>禁用状态</Text>
      <View style={styles.row}>
        <Text style={styles.label}>开启（禁用）</Text>
        <Switch value={true} disabled />
      </View>
      <View style={styles.row}>
        <Text style={styles.label}>关闭（禁用）</Text>
        <Switch value={false} disabled />
      </View>
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
  row: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 4,
  },
  label: {
    fontSize: 16,
    color: '#333',
  },
  panel: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
  },
  divider: {
    height: 1,
    backgroundColor: '#eee',
    marginVertical: 8,
  },
});
