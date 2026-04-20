import 'package:flutter/material.dart';
import 'home_view.dart';

class BasicsPage extends StatelessWidget {
  const BasicsPage({super.key});

  @override
  Widget build(BuildContext context) {
    return DemoScaffold(title: '基础组件', children: [
      const Section('Text 文本'),
      DemoCard(title: 'Text', desc: '各种文本样式', child: Column(children: [
        const Text('普通文本'),
        const SizedBox(height: 4),
        const Text('加粗文本', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18)),
        const SizedBox(height: 4),
        const Text('斜体彩色文本', style: TextStyle(fontStyle: FontStyle.italic, color: Colors.blue, fontSize: 16)),
        const SizedBox(height: 4),
        const Text('带阴影文本', style: TextStyle(shadows: [Shadow(color: Colors.grey, offset: Offset(2, 2), blurRadius: 2)], fontSize: 16)),
        const SizedBox(height: 4),
        Text('带下划线和删除线', style: TextStyle(decoration: TextDecoration.combine([TextDecoration.underline, TextDecoration.lineThrough]))),
        const SizedBox(height: 4),
        const Text('字母间距 2.0', style: TextStyle(letterSpacing: 2.0)),
        const SizedBox(height: 4),
        Text.rich(const TextSpan(children: [
          TextSpan(text: '富文本 ', style: TextStyle(color: Colors.red, fontWeight: FontWeight.bold)),
          TextSpan(text: '混合 ', style: TextStyle(color: Colors.blue, fontSize: 20)),
          TextSpan(text: '样式', style: TextStyle(color: Colors.green, fontStyle: FontStyle.italic)),
        ])),
      ])),
      const Section('Icon 图标'),
      DemoCard(title: 'Icon', desc: 'Material Icons 和自定义图标', child: Wrap(spacing: 16, runSpacing: 12, children: const [
        Icon(Icons.favorite, color: Colors.red, size: 32),
        Icon(Icons.star, color: Colors.amber, size: 32),
        Icon(Icons.home, color: Colors.blue, size: 32),
        Icon(Icons.settings, color: Colors.grey, size: 32),
        Icon(Icons.phone, color: Colors.green, size: 32),
        Icon(Icons.camera_alt, color: Colors.purple, size: 32),
        Icon(Icons.notifications, color: Colors.orange, size: 32),
        Icon(Icons.search, color: Colors.teal, size: 32),
        IconButton(icon: Icon(Icons.thumb_up), onPressed: null, tooltip: 'Thumb Up', iconSize: 32),
      ])),
      const Section('Placeholder 占位符'),
      DemoCard(title: 'Placeholder', child: const SizedBox(width: 200, height: 60, child: Placeholder())),
      const Section('Container 容器'),
      DemoCard(title: 'Container', desc: '基础容器，可设置颜色、边距、圆角等', child: Column(children: [
        Container(width: 200, height: 60, color: Colors.blue[100], alignment: Alignment.center, child: const Text('纯色容器')),
        const SizedBox(height: 8),
        Container(
          width: 200, height: 60,
          padding: const EdgeInsets.all(12),
          decoration: BoxDecoration(color: Colors.green[100], borderRadius: BorderRadius.circular(12), border: Border.all(color: Colors.green, width: 2)),
          child: const Center(child: Text('圆角+边框')),
        ),
        const SizedBox(height: 8),
        Container(
          width: 200, height: 60,
          decoration: BoxDecoration(
            gradient: const LinearGradient(colors: [Colors.blue, Colors.purple]),
            borderRadius: BorderRadius.circular(30),
            boxShadow: [BoxShadow(color: Colors.blue.withValues(alpha: 0.4), blurRadius: 8, offset: const Offset(0, 4))],
          ),
          alignment: Alignment.center,
          child: const Text('渐变+阴影', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
        ),
      ])),
      const Section('Button 按钮'),
      DemoCard(title: 'ElevatedButton', desc: '凸起按钮', child: Wrap(spacing: 12, children: [
        ElevatedButton(onPressed: () {}, child: const Text('默认')),
        ElevatedButton(onPressed: null, child: const Text('禁用')),
        ElevatedButton.icon(onPressed: () {}, icon: const Icon(Icons.add), label: const Text('图标按钮')),
      ])),
      DemoCard(title: 'FilledButton', desc: '填充按钮 (Material 3)', child: Wrap(spacing: 12, children: [
        FilledButton(onPressed: () {}, child: const Text('Filled')),
        FilledButton.tonal(onPressed: () {}, child: const Text('Tonal')),
      ])),
      DemoCard(title: 'OutlinedButton', desc: '边框按钮', child: Wrap(spacing: 12, children: [
        OutlinedButton(onPressed: () {}, child: const Text('默认')),
        OutlinedButton.icon(onPressed: () {}, icon: const Icon(Icons.login), label: const Text('登录')),
      ])),
      DemoCard(title: 'TextButton', desc: '文本按钮', child: Wrap(spacing: 12, children: [
        TextButton(onPressed: () {}, child: const Text('文本按钮')),
        TextButton.icon(onPressed: () {}, icon: const Icon(Icons.info), label: const Text('了解更多')),
      ])),
      DemoCard(title: 'IconButton', desc: '图标按钮', child: Wrap(spacing: 8, children: [
        IconButton(onPressed: () {}, icon: const Icon(Icons.volume_up), tooltip: '音量'),
        IconButton(onPressed: () {}, icon: const Icon(Icons.favorite), color: Colors.red, tooltip: '喜欢'),
        IconButton.filled(onPressed: () {}, icon: const Icon(Icons.add), tooltip: '添加'),
      ])),
      DemoCard(title: 'FloatingActionButton', desc: '浮动操作按钮', child: Wrap(spacing: 12, children: [
        FloatingActionButton.small(onPressed: () {}, child: const Icon(Icons.add)),
        FloatingActionButton(onPressed: () {}, child: const Icon(Icons.edit)),
        FloatingActionButton.extended(onPressed: () {}, icon: const Icon(Icons.navigation), label: const Text('Navigate')),
      ])),
    ]);
  }
}
