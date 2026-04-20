import 'package:flutter/material.dart';
import 'home_view.dart';

class ContainersPage extends StatelessWidget {
  const ContainersPage({super.key});

  @override
  Widget build(BuildContext context) {
    return DemoScaffold(title: '容器组件', children: [
      const Section('Padding 内边距'),
      DemoCard(title: 'Padding', desc: '不同方向的内边距', child: Column(children: [
        Container(color: Colors.blue[100], child: Padding(padding: const EdgeInsets.all(16), child: Container(color: Colors.blue[300], child: const Text('EdgeInsets.all(16)')))),
        const SizedBox(height: 8),
        Container(color: Colors.green[100], child: Padding(padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 8), child: Container(color: Colors.green[300], child: const Text('EdgeInsets.symmetric(h:32, v:8)')))),
        const SizedBox(height: 8),
        Container(color: Colors.orange[100], child: Padding(padding: const EdgeInsets.only(left: 40), child: Container(color: Colors.orange[300], child: const Text('EdgeInsets.only(left:40)')))),
      ])),
      const Section('Card 卡片'),
      DemoCard(title: 'Card', desc: 'Material Design 卡片', child: Column(children: [
        Card(child: ListTile(leading: const Icon(Icons.album), title: const Text('标准卡片'), subtitle: const Text('带副标题'))),
        Card(elevation: 8, shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)), child: Padding(padding: const EdgeInsets.all(16), child: const Text('高阴影圆角卡片'))),
      ])),
      const Section('DecoratedBox 装饰盒子'),
      DemoCard(title: 'DecoratedBox', desc: '各种装饰效果', child: Column(children: [
        DecoratedBox(
          decoration: BoxDecoration(gradient: const LinearGradient(colors: [Colors.pink, Colors.orange]), borderRadius: BorderRadius.circular(8)),
          child: const Padding(padding: EdgeInsets.all(12), child: Text('渐变装饰', style: TextStyle(color: Colors.white))),
        ),
        const SizedBox(height: 8),
        DecoratedBox(
          decoration: BoxDecoration(color: Colors.white, border: Border.all(color: Colors.blue, width: 2), borderRadius: BorderRadius.circular(12), boxShadow: const [BoxShadow(color: Colors.black12, blurRadius: 6, offset: Offset(2, 2))]),
          child: const Padding(padding: EdgeInsets.all(12), child: Text('边框+阴影+圆角')),
        ),
      ])),
      const Section('Transform 变换'),
      DemoCard(title: 'Transform', desc: '旋转、缩放、平移', child: Wrap(spacing: 24, runSpacing: 16, children: [
        Transform.rotate(angle: 0.2, child: _box('旋转 0.2')),
        Transform.scale(scale: 1.3, child: _box('放大 1.3x')),
        Transform.translate(offset: const Offset(10, -10), child: _box('平移')),
      ])),
      const Section('Opacity 透明度'),
      DemoCard(title: 'Opacity', child: Row(mainAxisAlignment: MainAxisAlignment.spaceEvenly, children: [
        Opacity(opacity: 1.0, child: _box('1.0')), Opacity(opacity: 0.7, child: _box('0.7')),
        Opacity(opacity: 0.4, child: _box('0.4')), Opacity(opacity: 0.1, child: _box('0.1')),
      ])),
      const Section('Clip 裁剪'),
      DemoCard(title: 'ClipRect / ClipRRect / ClipOval', child: Wrap(spacing: 16, runSpacing: 12, children: [
        ClipRRect(borderRadius: BorderRadius.circular(20), child: Container(width: 80, height: 80, color: Colors.blue, child: const Center(child: Text('RRect', style: TextStyle(color: Colors.white))))),
        ClipOval(child: Container(width: 80, height: 80, color: Colors.red, child: const Center(child: Text('Oval', style: TextStyle(color: Colors.white))))),
        ClipRect(child: Container(width: 80, height: 80, color: Colors.green, child: const Center(child: Text('Rect', style: TextStyle(color: Colors.white))))),
      ])),
      const Section('AspectRatio / FractionallySizedBox'),
      DemoCard(title: 'AspectRatio', desc: '保持宽高比', child: Column(children: [
        AspectRatio(aspectRatio: 16 / 9, child: Container(color: Colors.indigo[100], child: const Center(child: Text('16:9')))),
        const SizedBox(height: 8),
        FractionallySizedBox(widthFactor: 0.6, child: Container(color: Colors.teal[100], child: const Center(child: Text('FractionallySizedBox 60%')))),
      ])),
      const Section('LimitedBox / OverflowBox'),
      DemoCard(title: 'LimitedBox & OverflowBox', child: SizedBox(
        height: 100,
        child: Row(children: [
          Flexible(child: LimitedBox(maxHeight: 80, maxWidth: 120, child: Container(color: Colors.pink[100], child: const Center(child: Text('LimitedBox'))))),
          const SizedBox(width: 8),
          Flexible(child: OverflowBox(maxHeight: 150, alignment: Alignment.topLeft, child: Container(width: 80, height: 80, color: Colors.amber[200], child: const Center(child: Text('Overflow'))))),
        ]),
      )),
    ]);
  }

  Widget _box(String text) => Container(
    padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
    color: Colors.blue,
    child: Text(text, style: const TextStyle(color: Colors.white)),
  );
}
