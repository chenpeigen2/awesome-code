import React from 'react';
import { Image, Text, StyleSheet, View } from 'react-native';
import { ScreenWrapper } from '../components/ScreenWrapper';

const REMOTE_URI = 'https://picsum.photos/200/200';

export const ImageScreen = () => (
  <ScreenWrapper title="Image">
    <Text style={styles.sectionTitle}>网络图片</Text>
    <Image source={{ uri: REMOTE_URI }} style={styles.square} />

    <Text style={styles.sectionTitle}>不同 resize 模式</Text>
    <View style={styles.resizeRow}>
      <View style={styles.resizeItem}>
        <Image source={{ uri: REMOTE_URI }} style={styles.resizeImage} resizeMode="cover" />
        <Text style={styles.resizeLabel}>cover</Text>
      </View>
      <View style={styles.resizeItem}>
        <Image source={{ uri: REMOTE_URI }} style={styles.resizeImage} resizeMode="contain" />
        <Text style={styles.resizeLabel}>contain</Text>
      </View>
      <View style={styles.resizeItem}>
        <Image source={{ uri: REMOTE_URI }} style={styles.resizeImage} resizeMode="stretch" />
        <Text style={styles.resizeLabel}>stretch</Text>
      </View>
      <View style={styles.resizeItem}>
        <Image source={{ uri: REMOTE_URI }} style={styles.resizeImage} resizeMode="repeat" />
        <Text style={styles.resizeLabel}>repeat</Text>
      </View>
    </View>

    <Text style={styles.sectionTitle}>圆角图片</Text>
    <View style={styles.roundRow}>
      <Image source={{ uri: REMOTE_URI }} style={[styles.roundImage, { borderRadius: 8 }]} />
      <Image source={{ uri: REMOTE_URI }} style={[styles.roundImage, { borderRadius: 50 }]} />
    </View>

    <Text style={styles.sectionTitle}>不同尺寸</Text>
    <View style={styles.sizeRow}>
      <Image source={{ uri: REMOTE_URI }} style={{ width: 60, height: 60 }} />
      <Image source={{ uri: REMOTE_URI }} style={{ width: 100, height: 100 }} />
      <Image source={{ uri: REMOTE_URI }} style={{ width: 140, height: 140 }} />
    </View>

    <Text style={styles.sectionTitle}>带边框</Text>
    <Image
      source={{ uri: REMOTE_URI }}
      style={{
        width: 120,
        height: 120,
        borderWidth: 3,
        borderColor: '#3498db',
        borderRadius: 12,
      }}
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
  square: {
    width: 200,
    height: 200,
    borderRadius: 12,
  },
  resizeRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  resizeItem: {
    alignItems: 'center',
  },
  resizeImage: {
    width: 80,
    height: 80,
    borderWidth: 1,
    borderColor: '#ddd',
  },
  resizeLabel: {
    fontSize: 12,
    marginTop: 4,
    color: '#888',
  },
  roundRow: {
    flexDirection: 'row',
    gap: 16,
  },
  roundImage: {
    width: 100,
    height: 100,
  },
  sizeRow: {
    flexDirection: 'row',
    alignItems: 'flex-end',
    gap: 12,
  },
});
