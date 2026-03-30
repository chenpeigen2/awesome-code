import React, { useState } from 'react';
import { TextInput, Text, StyleSheet, View, KeyboardAvoidingView, Platform } from 'react-native';
import { ScreenWrapper } from '../components/ScreenWrapper';

export const TextInputScreen = () => {
  const [basic, setBasic] = useState('');
  const [secured, setSecured] = useState('');
  const [multiline, setMultiline] = useState('');
  const [number, setNumber] = useState('');
  const [email, setEmail] = useState('');
  const [editable, setEditable] = useState('不可编辑的文本');

  return (
    <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : undefined} style={{ flex: 1 }}>
      <ScreenWrapper title="TextInput" keyboardShouldPersistTaps="handled">
        <Text style={styles.sectionTitle}>基本输入框</Text>
        <TextInput
          style={styles.input}
          value={basic}
          onChangeText={setBasic}
          placeholder="请输入文本..."
        />
        <Text style={styles.value}>当前值: {basic}</Text>

        <Text style={styles.sectionTitle}>密码输入</Text>
        <TextInput
          style={styles.input}
          value={secured}
          onChangeText={setSecured}
          placeholder="请输入密码"
          secureTextEntry
        />

        <Text style={styles.sectionTitle}>多行输入</Text>
        <TextInput
          style={[styles.input, { height: 100, textAlignVertical: 'top' }]}
          value={multiline}
          onChangeText={setMultiline}
          placeholder="请输入多行文本..."
          multiline
          numberOfLines={4}
        />

        <Text style={styles.sectionTitle}>数字输入</Text>
        <TextInput
          style={styles.input}
          value={number}
          onChangeText={setNumber}
          placeholder="只能输入数字"
          keyboardType="number-pad"
        />

        <Text style={styles.sectionTitle}>邮箱输入</Text>
        <TextInput
          style={styles.input}
          value={email}
          onChangeText={setEmail}
          placeholder="请输入邮箱"
          keyboardType="email-address"
          autoCapitalize="none"
        />

        <Text style={styles.sectionTitle}>自定义样式</Text>
        <TextInput
          style={styles.customInput}
          placeholder="自定义边框和背景色"
          placeholderTextColor="#999"
        />

        <Text style={styles.sectionTitle}>最大长度限制</Text>
        <TextInput
          style={styles.input}
          placeholder="最多 10 个字符"
          maxLength={10}
        />

        <Text style={styles.sectionTitle}>禁用状态</Text>
        <TextInput
          style={[styles.input, { backgroundColor: '#ecf0f1' }]}
          value="不可编辑的文本"
          editable={false}
        />
      </ScreenWrapper>
    </KeyboardAvoidingView>
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
  input: {
    backgroundColor: '#fff',
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    padding: 12,
    fontSize: 16,
  },
  value: {
    fontSize: 13,
    color: '#888',
    marginTop: 4,
  },
  customInput: {
    backgroundColor: '#fef9e7',
    borderWidth: 2,
    borderColor: '#f39c12',
    borderRadius: 12,
    padding: 14,
    fontSize: 16,
    color: '#333',
  },
});
