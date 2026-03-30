import React, { useState } from 'react';
import { StatusBar, Text, View, StyleSheet, TouchableOpacity } from 'react-native';
import { ScreenWrapper } from '../components/ScreenWrapper';

export const StatusBarScreen = () => {
  const [style, setStyle] = useState<'default' | 'dark-content' | 'light-content'>('default');
  const [hidden, setHidden] = useState(false);

  return (
    <ScreenWrapper title="StatusBar">
      <StatusBar barStyle={style} hidden={hidden} />

      <Text style={styles.sectionTitle}>状态栏样式</Text>
      <View style={styles.row}>
        {(['default', 'dark-content', 'light-content'] as const).map((s) => (
          <TouchableOpacity
            key={s}
            style={[styles.btn, style === s && styles.activeBtn]}
            onPress={() => setStyle(s)}
          >
            <Text style={[styles.btnText, style === s && styles.activeBtnText]}>{s}</Text>
          </TouchableOpacity>
        ))}
      </View>

      <Text style={styles.sectionTitle}>显示/隐藏</Text>
      <TouchableOpacity
        style={[styles.toggleBtn, hidden && styles.toggleBtnActive]}
        onPress={() => setHidden(h => !h)}
      >
        <Text style={styles.toggleBtnText}>{hidden ? '已隐藏' : '显示中'}</Text>
      </TouchableOpacity>

      <Text style={styles.sectionTitle}>说明</Text>
      <Text style={styles.desc}>
        StatusBar 用于控制应用顶部状态栏的显示样式。{'\n'}
        · default: 默认样式{'\n'}
        · dark-content: 深色内容（浅色背景）{'\n'}
        · light-content: 浅色内容（深色背景）
      </Text>
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
    gap: 8,
    flexWrap: 'wrap',
  },
  btn: {
    backgroundColor: '#ecf0f1',
    paddingVertical: 10,
    paddingHorizontal: 16,
    borderRadius: 8,
  },
  activeBtn: {
    backgroundColor: '#3498db',
  },
  btnText: {
    fontSize: 13,
    color: '#666',
  },
  activeBtnText: {
    color: '#fff',
  },
  toggleBtn: {
    backgroundColor: '#2ecc71',
    paddingVertical: 14,
    borderRadius: 10,
    alignItems: 'center',
  },
  toggleBtnActive: {
    backgroundColor: '#e74c3c',
  },
  toggleBtnText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
  desc: {
    fontSize: 14,
    color: '#666',
    lineHeight: 22,
    backgroundColor: '#fff',
    padding: 16,
    borderRadius: 8,
  },
});
