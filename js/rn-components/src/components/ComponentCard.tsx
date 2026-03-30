import React from 'react';
import { TouchableOpacity, Text, StyleSheet, View } from 'react-native';

interface ComponentCardProps {
  title: string;
  description: string;
  onPress: () => void;
}

export const ComponentCard: React.FC<ComponentCardProps> = ({ title, description, onPress }) => (
  <TouchableOpacity style={styles.card} onPress={onPress} activeOpacity={0.7}>
    <View>
      <Text style={styles.title}>{title}</Text>
      <Text style={styles.description}>{description}</Text>
    </View>
    <Text style={styles.arrow}>›</Text>
  </TouchableOpacity>
);

const styles = StyleSheet.create({
  card: {
    backgroundColor: '#fff',
    padding: 16,
    marginHorizontal: 16,
    marginVertical: 6,
    borderRadius: 12,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.18,
    shadowRadius: 2,
  },
  title: {
    fontSize: 17,
    fontWeight: '600',
    color: '#333',
  },
  description: {
    fontSize: 13,
    color: '#888',
    marginTop: 4,
  },
  arrow: {
    fontSize: 28,
    color: '#ccc',
  },
});
