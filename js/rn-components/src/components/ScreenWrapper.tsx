import React from 'react';
import { View, Text, StyleSheet, ScrollView, type ScrollViewProps } from 'react-native';

interface ScreenWrapperProps extends ScrollViewProps {
  title: string;
  children: React.ReactNode;
}

export const ScreenWrapper: React.FC<ScreenWrapperProps> = ({ title, children, ...props }) => (
  <ScrollView style={styles.container} contentContainerStyle={styles.content} {...props}>
    <Text style={styles.title}>{title}</Text>
    {children}
  </ScrollView>
);

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  content: {
    padding: 16,
    paddingBottom: 40,
  },
  title: {
    fontSize: 24,
    fontWeight: '700',
    color: '#333',
    marginBottom: 16,
  },
});
