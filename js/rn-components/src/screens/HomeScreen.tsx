import React from 'react';
import { FlatList, StyleSheet } from 'react-native';
import { ComponentCard } from '../components/ComponentCard';

export interface ComponentItem {
  id: string;
  title: string;
  description: string;
  screen: string;
}

export const COMPONENTS: ComponentItem[] = [
  { id: 'view', title: 'View', description: '基础容器组件，支持 Flexbox 布局', screen: 'View' },
  { id: 'text', title: 'Text', description: '文本显示组件，支持嵌套和样式', screen: 'Text' },
  { id: 'image', title: 'Image', description: '图片显示组件，支持网络和本地资源', screen: 'Image' },
  { id: 'textinput', title: 'TextInput', description: '文本输入组件，支持多种键盘类型', screen: 'TextInput' },
  { id: 'scrollview', title: 'ScrollView', description: '可滚动容器，支持垂直/水平滚动', screen: 'ScrollView' },
  { id: 'flatlist', title: 'FlatList', description: '高性能列表组件，支持多列和懒加载', screen: 'FlatList' },
  { id: 'sectionlist', title: 'SectionList', description: '分组列表组件，支持粘性头部', screen: 'SectionList' },
  { id: 'touchable', title: 'Touchable', description: 'TouchableOpacity/Highlight/WithoutFeedback/Pressable', screen: 'Touchable' },
  { id: 'button', title: 'Button & Alert', description: '系统按钮和弹窗组件', screen: 'Button' },
  { id: 'switch', title: 'Switch', description: '开关切换组件', screen: 'Switch' },
  { id: 'activity', title: 'ActivityIndicator', description: '加载指示器组件', screen: 'ActivityIndicator' },
  { id: 'modal', title: 'Modal', description: '模态框组件，支持多种弹出样式', screen: 'Modal' },
  { id: 'statusbar', title: 'StatusBar', description: '状态栏控制组件', screen: 'StatusBar' },
  { id: 'safearea', title: 'SafeAreaView', description: '安全区域视图组件', screen: 'SafeAreaView' },
  { id: 'keyboard', title: 'KeyboardAvoidingView', description: '键盘避让组件', screen: 'KeyboardAvoidingView' },
  { id: 'animated', title: 'Animated', description: '动画系统，支持多种动画效果', screen: 'Animated' },
  { id: 'platform', title: 'Platform & Dimensions', description: '平台信息和屏幕尺寸', screen: 'Platform' },
  { id: 'stylesheet', title: 'StyleSheet', description: '样式系统，Flexbox 布局', screen: 'StyleSheet' },
  { id: 'refresh', title: 'RefreshControl', description: '下拉刷新控制组件', screen: 'RefreshControl' },
  { id: 'api', title: 'API 组件', description: 'AppState, Vibration, Share 等系统 API', screen: 'API' },
];

interface HomeScreenProps {
  onNavigate: (screen: string) => void;
}

export const HomeScreen: React.FC<HomeScreenProps> = ({ onNavigate }) => (
  <FlatList
    data={COMPONENTS}
    keyExtractor={(item) => item.id}
    renderItem={({ item }) => (
      <ComponentCard
        title={item.title}
        description={item.description}
        onPress={() => onNavigate(item.screen)}
      />
    )}
    contentContainerStyle={styles.list}
  />
);

const styles = StyleSheet.create({
  list: {
    paddingTop: 8,
    paddingBottom: 24,
  },
});
