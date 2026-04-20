import 'package:flutter/material.dart';
import 'home_view.dart';

class LayoutPage extends StatelessWidget {
  const LayoutPage({super.key});

  @override
  Widget build(BuildContext context) {
    return DemoScaffold(title: '布局组件', children: [
      const Section('Row 水平布局'),
      DemoCard(title: 'Row', desc: '水平排列子组件', child: Column(children: [
        Row(children: const [
          Icon(Icons.star, color: Colors.amber), Text(' Star'), Spacer(), Icon(Icons.arrow_forward),
        ]),
        const SizedBox(height: 8),
        Row(mainAxisAlignment: MainAxisAlignment.spaceEvenly, children: [
          _box('A', Colors.red), _box('B', Colors.blue), _box('C', Colors.green),
        ]),
      ])),
      const Section('Column 垂直布局'),
      DemoCard(title: 'Column', desc: '垂直排列子组件', child: SizedBox(
        height: 150,
        child: Row(children: [
          Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: const [
            Text('crossAxisAlignment.start', style: TextStyle(fontWeight: FontWeight.bold)),
            Text('默认左对齐'),
            Divider(),
            Text('第二行'),
            Text('第三行'),
          ])),
          Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.center, children: const [
            Text('crossAxisAlignment.center', style: TextStyle(fontWeight: FontWeight.bold)),
            Text('居中对齐'),
            Divider(),
            Text('第二行'),
            Text('第三行'),
          ])),
        ]),
      )),
      const Section('Stack 层叠布局'),
      DemoCard(title: 'Stack + Positioned', desc: '子组件可层叠并精确定位', child: SizedBox(
        width: 250, height: 150,
        child: Stack(children: [
          Container(decoration: BoxDecoration(color: Colors.blue[100], borderRadius: BorderRadius.circular(8))),
          Positioned(top: 10, left: 10, child: _box('TopLeft', Colors.red)),
          Positioned(bottom: 10, right: 10, child: _box('BottomRight', Colors.green)),
          Center(child: _box('Center', Colors.orange)),
        ]),
      )),
      const Section('Wrap 流式布局'),
      DemoCard(title: 'Wrap', desc: '自动换行的水平/垂直布局', child: Wrap(
        spacing: 8, runSpacing: 8, alignment: WrapAlignment.center,
        children: List.generate(12, (i) => Chip(label: Text('Tag ${i + 1}'))),
      )),
      const Section('Expanded / Flexible'),
      DemoCard(title: 'Expanded vs Flexible', desc: 'Expanded 填满剩余空间，Flexible 可自适应', child: Column(children: [
        Row(children: [
          Flexible(flex: 1, child: _coloredBox('Flex 1', Colors.red)),
          Expanded(flex: 2, child: _coloredBox('Expanded 2', Colors.blue)),
          Flexible(flex: 1, child: _coloredBox('Flex 1', Colors.green)),
        ]),
        const SizedBox(height: 8),
        Row(children: [
          _box('Fixed', Colors.purple),
          const Spacer(),
          _box('Spacer', Colors.orange),
        ]),
      ])),
      const Section('SizedBox / ConstrainedBox'),
      DemoCard(title: 'SizedBox & ConstrainedBox', child: Column(children: [
        Row(mainAxisAlignment: MainAxisAlignment.center, children: [
          Container(width: 50, height: 50, color: Colors.red, child: const Center(child: Text('50x50', style: TextStyle(fontSize: 10, color: Colors.white)))),
          const SizedBox(width: 16),
          ConstrainedBox(constraints: const BoxConstraints(minWidth: 100, maxWidth: 200, minHeight: 50, maxHeight: 80),
            child: Container(color: Colors.blue, child: const Center(child: Text('Constrained\n100-200 x 50-80', textAlign: TextAlign.center, style: TextStyle(fontSize: 10, color: Colors.white)))),
          ),
        ]),
      ])),
      const Section('Align / Center'),
      DemoCard(title: 'Align & Center', child: SizedBox(
        height: 120,
        child: Row(children: [
          Expanded(child: Align(alignment: Alignment.topLeft, child: _box('TL', Colors.red))),
          Expanded(child: Center(child: _box('Center', Colors.blue))),
          Expanded(child: Align(alignment: Alignment.bottomRight, child: _box('BR', Colors.green))),
        ]),
      )),
      const Section('Baseline 对齐'),
      DemoCard(title: 'Baseline', desc: '基于文本基线对齐', child: Row(
        crossAxisAlignment: CrossAxisAlignment.baseline,
        textBaseline: TextBaseline.alphabetic,
        children: const [
          Text('Baseline', style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold)),
          Text('Aligned', style: TextStyle(fontSize: 16)),
          Text('Text', style: TextStyle(fontSize: 12)),
        ],
      )),
    ]);
  }

  Widget _box(String text, Color color) => Container(
    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
    decoration: BoxDecoration(color: color, borderRadius: BorderRadius.circular(6)),
    child: Text(text, style: const TextStyle(color: Colors.white, fontSize: 12)),
  );

  Widget _coloredBox(String text, Color color) => Container(
    height: 50, color: color,
    child: Center(child: Text(text, style: const TextStyle(color: Colors.white, fontSize: 12))),
  );
}
