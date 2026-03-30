import React, { useRef, useEffect } from 'react';
import { Animated, Text, StyleSheet, View, Easing, TouchableOpacity } from 'react-native';
import { ScreenWrapper } from '../components/ScreenWrapper';

export const AnimatedScreen = () => {
  const fadeAnim = useRef(new Animated.Value(0)).current;
  const slideAnim = useRef(new Animated.Value(-200)).current;
  const scaleAnim = useRef(new Animated.Value(1)).current;
  const spinAnim = useRef(new Animated.Value(0)).current;
  const colorAnim = useRef(new Animated.Value(0)).current;
  const sequenceAnim = useRef(new Animated.Value(0)).current;

  const fadeIn = () => {
    Animated.timing(fadeAnim, { toValue: 1, duration: 600, useNativeDriver: true }).start();
  };
  const fadeOut = () => {
    Animated.timing(fadeAnim, { toValue: 0, duration: 600, useNativeDriver: true }).start();
  };
  const slideIn = () => {
    Animated.spring(slideAnim, { toValue: 0, useNativeDriver: true }).start();
  };
  const slideOut = () => {
    Animated.timing(slideAnim, { toValue: -200, useNativeDriver: true }).start();
  };
  const pulse = () => {
    Animated.sequence([
      Animated.timing(scaleAnim, { toValue: 1.3, duration: 200, useNativeDriver: true }),
      Animated.timing(scaleAnim, { toValue: 1, duration: 200, useNativeDriver: true }),
    ]).start();
  };

  useEffect(() => {
    const spin = Animated.loop(
      Animated.timing(spinAnim, {
        toValue: 1,
        duration: 2000,
        easing: Easing.linear,
        useNativeDriver: true,
      })
    );
    spin.start();
    return () => spin.stop();
  }, [spinAnim]);

  const spinInterpolate = spinAnim.interpolate({
    inputRange: [0, 1],
    outputRange: ['0deg', '360deg'],
  });

  useEffect(() => {
    const color = Animated.loop(
      Animated.timing(colorAnim, {
        toValue: 1,
        duration: 3000,
        useNativeDriver: false,
      })
    );
    color.start();
    return () => color.stop();
  }, [colorAnim]);

  const bgColor = colorAnim.interpolate({
    inputRange: [0, 0.5, 1],
    outputRange: ['#e74c3c', '#3498db', '#2ecc71'],
  });

  const runSequence = () => {
    Animated.sequence([
      Animated.timing(sequenceAnim, { toValue: 1, duration: 300, useNativeDriver: true }),
      Animated.timing(sequenceAnim, { toValue: 2, duration: 300, useNativeDriver: true }),
      Animated.timing(sequenceAnim, { toValue: 0, duration: 300, useNativeDriver: true }),
    ]).start();
  };
  const seqOpacity = sequenceAnim.interpolate({ inputRange: [0, 1, 2], outputRange: [0.3, 1, 0.3] });
  const seqScale = sequenceAnim.interpolate({ inputRange: [0, 1, 2], outputRange: [1, 1.2, 1] });

  return (
    <ScreenWrapper title="Animated">
      <Text style={styles.sectionTitle}>淡入淡出</Text>
      <View style={styles.animRow}>
        <Animated.View style={[styles.animBox, { backgroundColor: '#e74c3c', opacity: fadeAnim }]} />
        <View style={styles.btnRow}>
          <TouchableOpacity style={styles.btn} onPress={fadeIn}><Text style={styles.btnText}>淡入</Text></TouchableOpacity>
          <TouchableOpacity style={styles.btn} onPress={fadeOut}><Text style={styles.btnText}>淡出</Text></TouchableOpacity>
        </View>
      </View>

      <Text style={styles.sectionTitle}>弹性滑动 (Spring)</Text>
      <View style={styles.animRow}>
        <Animated.View style={[styles.animBox, { backgroundColor: '#3498db', transform: [{ translateX: slideAnim }] }]} />
        <View style={styles.btnRow}>
          <TouchableOpacity style={styles.btn} onPress={slideIn}><Text style={styles.btnText}>滑入</Text></TouchableOpacity>
          <TouchableOpacity style={styles.btn} onPress={slideOut}><Text style={styles.btnText}>滑出</Text></TouchableOpacity>
        </View>
      </View>

      <Text style={styles.sectionTitle}>缩放动画</Text>
      <TouchableOpacity onPress={pulse}>
        <Animated.View style={[styles.animBox, { backgroundColor: '#9b59b6', transform: [{ scale: scaleAnim }] }]} />
      </TouchableOpacity>

      <Text style={styles.sectionTitle}>旋转动画</Text>
      <Animated.View style={[styles.animBox, { backgroundColor: '#f39c12', transform: [{ rotate: spinInterpolate }] }]} />

      <Text style={styles.sectionTitle}>颜色渐变</Text>
      <Animated.View style={[styles.animBox, { backgroundColor: bgColor }]} />

      <Text style={styles.sectionTitle}>序列动画</Text>
      <TouchableOpacity onPress={runSequence}>
        <Animated.View style={[styles.animBox, { backgroundColor: '#1abc9c', opacity: seqOpacity, transform: [{ scale: seqScale }] }]} />
      </TouchableOpacity>
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
  animRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 16,
  },
  animBox: {
    width: 60,
    height: 60,
    borderRadius: 12,
  },
  btnRow: {
    flexDirection: 'row',
    gap: 8,
  },
  btn: {
    backgroundColor: '#2c3e50',
    paddingVertical: 8,
    paddingHorizontal: 16,
    borderRadius: 8,
  },
  btnText: {
    color: '#fff',
    fontSize: 14,
  },
});
