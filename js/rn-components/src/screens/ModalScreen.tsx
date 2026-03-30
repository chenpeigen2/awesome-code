import React, { useState } from 'react';
import { Modal, Text, View, StyleSheet, TouchableOpacity, Button } from 'react-native';
import { ScreenWrapper } from '../components/ScreenWrapper';

export const ModalScreen = () => {
  const [basicVisible, setBasicVisible] = useState(false);
  const [customVisible, setCustomVisible] = useState(false);
  const [bottomVisible, setBottomVisible] = useState(false);
  const [fullVisible, setFullVisible] = useState(false);

  return (
    <ScreenWrapper title="Modal">
      <Text style={styles.sectionTitle}>基本模态框</Text>
      <Button title="打开基本 Modal" onPress={() => setBasicVisible(true)} />
      <Modal visible={basicVisible} animationType="slide" transparent>
        <View style={styles.overlay}>
          <View style={styles.modal}>
            <Text style={styles.modalTitle}>基本模态框</Text>
            <Text style={styles.modalText}>这是一个基本的 Modal 组件示例。</Text>
            <TouchableOpacity style={styles.closeBtn} onPress={() => setBasicVisible(false)}>
              <Text style={styles.closeBtnText}>关闭</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>

      <Text style={styles.sectionTitle}>自定义内容模态框</Text>
      <Button title="打开自定义 Modal" color="#9b59b6" onPress={() => setCustomVisible(true)} />
      <Modal visible={customVisible} animationType="fade" transparent>
        <View style={styles.overlay}>
          <View style={[styles.modal, styles.customModal]}>
            <Text style={styles.emoji}>🎉</Text>
            <Text style={styles.modalTitle}>恭喜!</Text>
            <Text style={styles.modalText}>你完成了所有任务!</Text>
            <View style={styles.btnRow}>
              <TouchableOpacity style={[styles.actionBtn, { backgroundColor: '#ccc' }]} onPress={() => setCustomVisible(false)}>
                <Text>取消</Text>
              </TouchableOpacity>
              <TouchableOpacity style={[styles.actionBtn, { backgroundColor: '#2ecc71' }]} onPress={() => setCustomVisible(false)}>
                <Text style={styles.whiteText}>确认</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>

      <Text style={styles.sectionTitle}>底部弹出模态框</Text>
      <Button title="打开底部 Modal" color="#e67e22" onPress={() => setBottomVisible(true)} />
      <Modal visible={bottomVisible} animationType="slide" transparent>
        <View style={styles.bottomOverlay}>
          <View style={styles.bottomModal}>
            <View style={styles.handle} />
            <Text style={styles.modalTitle}>操作菜单</Text>
            {['拍照', '从相册选择', '分享', '删除'].map((action, i) => (
              <TouchableOpacity key={i} style={styles.actionItem} onPress={() => setBottomVisible(false)}>
                <Text style={[styles.actionText, i === 3 && { color: '#e74c3c' }]}>{action}</Text>
              </TouchableOpacity>
            ))}
          </View>
        </View>
      </Modal>

      <Text style={styles.sectionTitle}>全屏模态框</Text>
      <Button title="打开全屏 Modal" color="#2c3e50" onPress={() => setFullVisible(true)} />
      <Modal visible={fullVisible} animationType="slide">
        <View style={styles.fullScreen}>
          <Text style={styles.fullTitle}>全屏模态框</Text>
          <Text style={styles.fullText}>这个 Modal 占据了整个屏幕</Text>
          <TouchableOpacity style={styles.fullCloseBtn} onPress={() => setFullVisible(false)}>
            <Text style={styles.closeBtnText}>返回</Text>
          </TouchableOpacity>
        </View>
      </Modal>
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
  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.5)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  modal: {
    backgroundColor: '#fff',
    borderRadius: 16,
    padding: 24,
    width: '85%',
    alignItems: 'center',
  },
  customModal: {
    alignItems: 'center',
    gap: 8,
  },
  emoji: {
    fontSize: 48,
  },
  modalTitle: {
    fontSize: 20,
    fontWeight: '700',
    color: '#333',
    marginTop: 8,
  },
  modalText: {
    fontSize: 15,
    color: '#666',
    textAlign: 'center',
    marginTop: 4,
  },
  closeBtn: {
    backgroundColor: '#3498db',
    paddingVertical: 12,
    paddingHorizontal: 32,
    borderRadius: 8,
    marginTop: 16,
  },
  closeBtnText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
  btnRow: {
    flexDirection: 'row',
    gap: 12,
    marginTop: 16,
  },
  actionBtn: {
    paddingVertical: 10,
    paddingHorizontal: 24,
    borderRadius: 8,
  },
  whiteText: {
    color: '#fff',
    fontWeight: '600',
  },
  bottomOverlay: {
    flex: 1,
    justifyContent: 'flex-end',
    backgroundColor: 'rgba(0,0,0,0.3)',
  },
  bottomModal: {
    backgroundColor: '#fff',
    borderTopLeftRadius: 20,
    borderTopRightRadius: 20,
    padding: 20,
    paddingBottom: 40,
  },
  handle: {
    width: 40,
    height: 4,
    backgroundColor: '#ddd',
    borderRadius: 2,
    alignSelf: 'center',
    marginBottom: 12,
  },
  actionItem: {
    paddingVertical: 14,
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
  },
  actionText: {
    fontSize: 16,
    color: '#333',
  },
  fullScreen: {
    flex: 1,
    backgroundColor: '#2c3e50',
    justifyContent: 'center',
    alignItems: 'center',
    padding: 24,
  },
  fullTitle: {
    fontSize: 28,
    fontWeight: '700',
    color: '#fff',
  },
  fullText: {
    fontSize: 16,
    color: 'rgba(255,255,255,0.7)',
    marginTop: 8,
  },
  fullCloseBtn: {
    backgroundColor: '#e74c3c',
    paddingVertical: 14,
    paddingHorizontal: 32,
    borderRadius: 10,
    marginTop: 24,
  },
});
