import React, { useState } from 'react';
import {
  KeyboardAvoidingView,
  TextInput,
  Text,
  StyleSheet,
  View,
  Platform,
  Keyboard,
  TouchableWithoutFeedback,
} from 'react-native';
import { ScreenWrapper } from '../components/ScreenWrapper';

export const KeyboardAvoidingViewScreen = () => {
  const [text, setText] = useState('');

  return (
    <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        style={{ flex: 1 }}
      >
        <ScreenWrapper title="KeyboardAvoidingView" keyboardShouldPersistTaps="handled">
          <Text style={styles.sectionTitle}>聊天输入框示例</Text>
          <Text style={styles.desc}>
            KeyboardAvoidingView 会在键盘弹出时自动调整视图位置，防止输入框被键盘遮挡。
          </Text>

          <View style={styles.chatContainer}>
            <View style={styles.chatBubble}>
              <Text style={styles.chatText}>你好！请输入消息试试键盘避让效果。</Text>
            </View>
          </View>

          <Text style={styles.sectionTitle}>表单输入示例</Text>
          <View style={styles.form}>
            <TextInput style={styles.input} placeholder="用户名" />
            <TextInput style={styles.input} placeholder="邮箱" keyboardType="email-address" />
            <TextInput style={styles.input} placeholder="密码" secureTextEntry />
            <TextInput
              style={[styles.input, { height: 80, textAlignVertical: 'top' }]}
              placeholder="备注（多行）"
              multiline
              value={text}
              onChangeText={setText}
            />
          </View>

          <Text style={styles.sectionTitle}>使用说明</Text>
          <View style={styles.infoBox}>
            <Text style={styles.infoText}>
              · behavior: iOS 用 padding，Android 用 height{'\n'}
              · 搭配 TouchableWithoutFeedback 点击收起键盘{'\n'}
              · 适合聊天界面和表单页面使用
            </Text>
          </View>
        </ScreenWrapper>
      </KeyboardAvoidingView>
    </TouchableWithoutFeedback>
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
  desc: {
    fontSize: 14,
    color: '#666',
    lineHeight: 22,
  },
  chatContainer: {
    backgroundColor: '#e8f4fd',
    borderRadius: 12,
    padding: 16,
  },
  chatBubble: {
    backgroundColor: '#3498db',
    padding: 12,
    borderRadius: 12,
    alignSelf: 'flex-start',
    maxWidth: '80%',
  },
  chatText: {
    color: '#fff',
    fontSize: 15,
  },
  form: {
    gap: 12,
  },
  input: {
    backgroundColor: '#fff',
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    padding: 12,
    fontSize: 16,
  },
  infoBox: {
    backgroundColor: '#fff',
    padding: 16,
    borderRadius: 8,
  },
  infoText: {
    fontSize: 14,
    color: '#555',
    lineHeight: 24,
  },
});
