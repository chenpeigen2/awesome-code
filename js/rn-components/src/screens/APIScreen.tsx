import React, { useState, useEffect } from 'react';
import {
  Vibration,
  AppState,
  Text,
  View,
  StyleSheet,
  TouchableOpacity,
  AppStateStatus,
} from 'react-native';
import { ScreenWrapper } from '../components/ScreenWrapper';

export const APIScreen = () => {
  const [appState, setAppState] = useState<AppStateStatus>(AppState.currentState);

  useEffect(() => {
    const sub = AppState.addEventListener('change', setAppState);
    return () => sub.remove();
  }, []);

  return (
    <ScreenWrapper title="API 组件">
      <Text style={styles.sectionTitle}>AppState 应用状态</Text>
      <View style={styles.stateCard}>
        <Text style={styles.stateLabel}>当前状态</Text>
        <Text style={[styles.stateValue, { color: appState === 'active' ? '#2ecc71' : '#e74c3c' }]}>
          {appState === 'active' ? '活跃 (active)' : appState === 'background' ? '后台 (background)' : '非活跃 (inactive)'}
        </Text>
      </View>
      <Text style={styles.desc}>
        AppState 可以检测应用是否在前台、后台或非活跃状态。
      </Text>

      <Text style={styles.sectionTitle}>Vibration 振动</Text>
      <View style={styles.btnRow}>
        <TouchableOpacity
          style={styles.actionBtn}
          onPress={() => Vibration.vibrate()}
        >
          <Text style={styles.actionBtnText}>短振动</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={styles.actionBtn}
          onPress={() => Vibration.vibrate(500)}
        >
          <Text style={styles.actionBtnText}>长振动 (500ms)</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={styles.actionBtn}
          onPress={() => Vibration.vibrate([0, 200, 100, 200], true)}
        >
          <Text style={styles.actionBtnText}>模式振动</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={[styles.actionBtn, { backgroundColor: '#e74c3c' }]}
          onPress={() => Vibration.cancel()}
        >
          <Text style={styles.actionBtnText}>停止振动</Text>
        </TouchableOpacity>
      </View>

      <Text style={styles.sectionTitle}>其他 API 说明</Text>
      <View style={styles.infoCard}>
        {[
          ['BackHandler', '处理 Android 返回按钮事件'],
          ['Clipboard', '读写剪贴板内容 (需 @react-native-clipboard/clipboard)'],
          ['Linking', '打开 URL、拨号、发邮件等'],
          ['Share', '调用系统分享功能'],
          ['PermissionsAndroid', 'Android 运行时权限请求'],
          ['ToastAndroid', 'Android 原生 Toast 提示'],
          ['AccessibilityInfo', '获取无障碍功能状态'],
          ['DeviceInfo', '获取设备信息'],
          ['InteractionManager', '管理动画和交互任务'],
          ['LayoutAnimation', '布局变化时的自动动画'],
          ['Appearance', '获取用户偏好的颜色方案 (深色/浅色)'],
          ['Keyboard', '监听键盘显示/隐藏事件'],
        ].map(([name, desc], i) => (
          <View key={i} style={styles.apiRow}>
            <Text style={styles.apiName}>{name}</Text>
            <Text style={styles.apiDesc}>{desc}</Text>
          </View>
        ))}
      </View>
    </ScreenWrapper>
  );
};

const styles = StyleSheet.create({
  sectionTitle: { fontSize: 16, fontWeight: '600', color: '#555', marginTop: 16, marginBottom: 8 },
  desc: { fontSize: 14, color: '#666', lineHeight: 22 },
  stateCard: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  stateLabel: { fontSize: 15, color: '#555' },
  stateValue: { fontSize: 15, fontWeight: '600' },
  btnRow: { gap: 8 },
  actionBtn: {
    backgroundColor: '#3498db',
    paddingVertical: 12,
    borderRadius: 8,
    alignItems: 'center',
  },
  actionBtnText: { color: '#fff', fontSize: 15, fontWeight: '600' },
  infoCard: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 12,
  },
  apiRow: {
    paddingVertical: 10,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  apiName: { fontSize: 15, fontWeight: '600', color: '#333' },
  apiDesc: { fontSize: 13, color: '#888', marginTop: 2 },
});
