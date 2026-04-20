import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'home_view.dart';

class DialogPage extends StatelessWidget {
  const DialogPage({super.key});

  @override
  Widget build(BuildContext context) {
    return DemoScaffold(title: '弹窗组件', children: [
      const Section('AlertDialog'),
      DemoCard(title: 'AlertDialog', desc: '标准警告对话框', child: ElevatedButton(
        onPressed: () => showDialog(context: context, builder: (_) => AlertDialog(
          title: const Text('确认删除'),
          content: const Text('确定要删除这个项目吗？此操作不可撤销。'),
          actions: [TextButton(onPressed: () => Navigator.pop(context), child: const Text('取消')), FilledButton(onPressed: () => Navigator.pop(context), child: const Text('确定'))],
        )),
        child: const Text('显示 AlertDialog'),
      )),
      const Section('SimpleDialog'),
      DemoCard(title: 'SimpleDialog', desc: '简单选项对话框', child: ElevatedButton(
        onPressed: () => showDialog(context: context, builder: (_) => SimpleDialog(
          title: const Text('选择颜色'),
          children: ['红色', '绿色', '蓝色'].map((c) => SimpleDialogOption(onPressed: () { Navigator.pop(context); Get.snackbar('选择', c, duration: const Duration(seconds: 1)); }, child: Text(c))).toList(),
        )),
        child: const Text('显示 SimpleDialog'),
      )),
      const Section('BottomSheet'),
      DemoCard(title: 'showModalBottomSheet', desc: '底部弹出面板', child: ElevatedButton(
        onPressed: () => showModalBottomSheet(context: context, isScrollControlled: true, shape: const RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(20))),
          builder: (_) => DraggableScrollableSheet(initialChildSize: 0.4, minChildSize: 0.2, maxChildSize: 0.8, expand: false,
            builder: (ctx, scrollController) => ListView(controller: scrollController, children: const [
              ListTile(leading: Icon(Icons.share), title: Text('分享')),
              ListTile(leading: Icon(Icons.copy), title: Text('复制')),
              ListTile(leading: Icon(Icons.delete), title: Text('删除')),
              ListTile(leading: Icon(Icons.edit), title: Text('编辑')),
              ListTile(leading: Icon(Icons.info), title: Text('详情')),
            ]),
          ),
        ),
        child: const Text('显示 BottomSheet'),
      )),
      const Section('GetX Snackbar'),
      DemoCard(title: 'Get.snackbar', desc: 'GetX 提供的提示条', child: Wrap(spacing: 8, runSpacing: 8, children: [
        ElevatedButton(onPressed: () => Get.snackbar('标题', '这是一条普通消息', snackPosition: SnackPosition.BOTTOM), child: const Text('普通')),
        ElevatedButton(onPressed: () => Get.snackbar('成功', '操作成功!', snackPosition: SnackPosition.BOTTOM, backgroundColor: Colors.green.withValues(alpha: 0.8), colorText: Colors.white), child: const Text('成功')),
        ElevatedButton(onPressed: () => Get.snackbar('错误', '操作失败!', snackPosition: SnackPosition.BOTTOM, backgroundColor: Colors.red.withValues(alpha: 0.8), colorText: Colors.white), child: const Text('错误')),
        ElevatedButton(onPressed: () => Get.snackbar('警告', '请注意!', snackPosition: SnackPosition.TOP, backgroundColor: Colors.orange.withValues(alpha: 0.8), colorText: Colors.white, duration: const Duration(seconds: 5)), child: const Text('警告(顶部5s)')),
      ])),
      const Section('GetX Dialog'),
      DemoCard(title: 'Get.dialog / Get.defaultDialog', desc: 'GetX 对话框', child: Wrap(spacing: 8, runSpacing: 8, children: [
        ElevatedButton(onPressed: () => Get.defaultDialog(title: '提示', middleText: '这是 GetX 默认对话框', textConfirm: '确定', textCancel: '取消', onConfirm: () => Get.back()), child: const Text('Default Dialog')),
        ElevatedButton(onPressed: () => Get.dialog(Center(child: Container(padding: const EdgeInsets.all(24), decoration: BoxDecoration(color: Get.theme.scaffoldBackgroundColor, borderRadius: BorderRadius.circular(16)), child: Column(mainAxisSize: MainAxisSize.min, children: [const CircularProgressIndicator(), const SizedBox(height: 16), const Text('加载中...'), const SizedBox(height: 16), ElevatedButton(onPressed: () => Get.back(), child: const Text('关闭'))]))), barrierDismissible: false), child: const Text('Custom Dialog')),
      ])),
      const Section('GetX BottomSheet'),
      DemoCard(title: 'Get.bottomSheet', desc: 'GetX 底部面板', child: ElevatedButton(
        onPressed: () => Get.bottomSheet(Container(
          padding: const EdgeInsets.all(24),
          decoration: BoxDecoration(color: Get.theme.scaffoldBackgroundColor, borderRadius: const BorderRadius.vertical(top: Radius.circular(20))),
          child: Column(mainAxisSize: MainAxisSize.min, children: [
            Container(width: 40, height: 4, decoration: BoxDecoration(color: Colors.grey[300], borderRadius: BorderRadius.circular(2))),
            const SizedBox(height: 20),
            const Text('GetX BottomSheet', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
            const SizedBox(height: 16),
            const ListTile(leading: Icon(Icons.camera_alt), title: Text('拍照')),
            const ListTile(leading: Icon(Icons.photo_library), title: Text('相册')),
            const SizedBox(height: 16),
            SizedBox(width: double.infinity, child: ElevatedButton(onPressed: () => Get.back(), child: const Text('关闭'))),
          ]),
        )),
        child: const Text('GetX BottomSheet'),
      )),
      const Section('AboutDialog'),
      DemoCard(title: 'showAboutDialog', child: ElevatedButton(
        onPressed: () => showAboutDialog(context: context, applicationName: 'Flutter GetX Demo', applicationVersion: '1.0.0', applicationLegalese: '© 2024', children: const [SizedBox(height: 16), Text('一个全面的 Flutter Widget 示例应用。')]),
        child: const Text('关于'),
      )),
      const Section('LicensePage'),
      DemoCard(title: 'LicensePage', child: ElevatedButton(
        onPressed: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const LicensePage(applicationName: 'Flutter GetX Demo', applicationVersion: '1.0.0'))),
        child: const Text('查看许可证'),
      )),
    ]);
  }
}
