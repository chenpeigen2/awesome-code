import React, { useState } from 'react';
import {
  TouchableOpacity,
  TouchableHighlight,
  TouchableWithoutFeedback,
  Pressable,
  Text,
  View,
  StyleSheet,
} from 'react-native';
import { ScreenWrapper } from '../components/ScreenWrapper';

export const TouchableScreen = () => {
  const [count, setCount] = useState(0);
  const [pressState, setPressState] = useState('等待触摸');

  return (
    <ScreenWrapper title="Touchable 组件">
      <Text style={styles.sectionTitle}>TouchableOpacity</Text>
      <Text style={styles.desc}>按下时透明度降低</Text>
      <TouchableOpacity style={styles.btn} onPress={() => setCount(c => c + 1)} activeOpacity={0.5}>
        <Text style={styles.btnText}>点击次数: {count}</Text>
      </TouchableOpacity>

      <Text style={styles.sectionTitle}>TouchableHighlight</Text>
      <Text style={styles.desc}>按下时显示底层颜色</Text>
      <TouchableHighlight
        style={styles.btn}
        underlayColor="#2980b9"
        onPress={() => setCount(c => c + 1)}
      >
        <Text style={styles.btnText}>Highlight 按钮</Text>
      </TouchableHighlight>

      <Text style={styles.sectionTitle}>TouchableWithoutFeedback</Text>
      <Text style={styles.desc}>无视觉反馈的触摸组件</Text>
      <TouchableWithoutFeedback onPress={() => setPressState('已按下!')}>
        <View style={styles.btn}>
          <Text style={styles.btnText}>{pressState}</Text>
        </View>
      </TouchableWithoutFeedback>

      <Text style={styles.sectionTitle}>Pressable (推荐)</Text>
      <Text style={styles.desc}>React Native 新一代触摸组件</Text>
      <Pressable
        style={({ pressed }) => [
          styles.btn,
          pressed && { backgroundColor: '#c0392b', transform: [{ scale: 0.96 }] },
        ]}
        onPressIn={() => setPressState('按下中...')}
        onPressOut={() => setPressState('松开')}
        onLongPress={() => setPressState('长按!')}
        delayLongPress={500}
      >
        {({ pressed }) => (
          <Text style={styles.btnText}>
            {pressed ? '正在按下' : 'Pressable 按钮'}
          </Text>
        )}
      </Pressable>

      <Text style={styles.sectionTitle}>不同按压反馈样式</Text>
      <View style={styles.row}>
        <Pressable
          style={({ pressed }) => [styles.smallBtn, pressed && { opacity: 0.5 }]}
        >
          {({ pressed }) => <Text style={styles.smallBtnText}>{pressed ? '😊' : '👆'}</Text>}
        </Pressable>
        <Pressable
          style={({ pressed }) => [styles.smallBtn, pressed && { backgroundColor: '#8e44ad' }]}
        >
          {({ pressed }) => <Text style={styles.smallBtnText}>{pressed ? '变色' : '原色'}</Text>}
        </Pressable>
        <Pressable
          style={({ pressed }) => [styles.smallBtn, pressed && { transform: [{ scale: 1.2 }] }]}
        >
          {({ pressed }) => <Text style={styles.smallBtnText}>{pressed ? '大' : '缩放'}</Text>}
        </Pressable>
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
    marginBottom: 4,
  },
  desc: {
    fontSize: 13,
    color: '#888',
    marginBottom: 8,
  },
  btn: {
    backgroundColor: '#e74c3c',
    paddingVertical: 14,
    paddingHorizontal: 24,
    borderRadius: 10,
    alignItems: 'center',
    marginBottom: 8,
  },
  btnText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
  row: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    marginTop: 8,
  },
  smallBtn: {
    backgroundColor: '#2c3e50',
    width: 80,
    height: 80,
    borderRadius: 12,
    justifyContent: 'center',
    alignItems: 'center',
  },
  smallBtnText: {
    color: '#fff',
    fontWeight: '600',
  },
});
