import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'home_view.dart';

class InteractiveController extends GetxController {
  final sliderValue = 0.5.obs;
  final switchValue = false.obs;
  final checkboxValue = false.obs;
  final radioValue = 0.obs;
  final expandedIndex = (-1).obs;
}

class InteractivePage extends StatelessWidget {
  const InteractivePage({super.key});

  @override
  Widget build(BuildContext context) {
    final ctrl = Get.put(InteractiveController());
    return DemoScaffold(title: '交互组件', children: [
      const Section('GestureDetector 手势'),
      DemoCard(title: 'GestureDetector', desc: '点击、双击、长按、拖拽', child: GetBuilder<InteractiveController>(
        init: ctrl,
        builder: (c) {
          return Column(children: [
            GestureDetector(
              onTap: () => Get.snackbar('点击', '你点击了!', snackPosition: SnackPosition.BOTTOM, duration: const Duration(seconds: 1)),
              onDoubleTap: () => Get.snackbar('双击', '你双击了!', snackPosition: SnackPosition.BOTTOM, duration: const Duration(seconds: 1)),
              onLongPress: () => Get.snackbar('长按', '你长按了!', snackPosition: SnackPosition.BOTTOM, duration: const Duration(seconds: 1)),
              child: Container(
                width: 200, height: 80,
                decoration: BoxDecoration(color: Colors.blue[100], borderRadius: BorderRadius.circular(12), border: Border.all(color: Colors.blue)),
                child: const Center(child: Text('点击 / 双击 / 长按试试')),
              ),
            ),
          ]);
        },
      )),
      const Section('InkWell 水波纹'),
      DemoCard(title: 'InkWell', desc: 'Material 水波纹点击效果', child: Material(
        color: Colors.transparent,
        child: InkWell(
          onTap: () {},
          borderRadius: BorderRadius.circular(12),
          child: Container(
            width: 200, height: 60,
            decoration: BoxDecoration(borderRadius: BorderRadius.circular(12), border: Border.all(color: Colors.grey)),
            child: const Center(child: Text('点击查看水波纹效果')),
          ),
        ),
      )),
      const Section('Dismissible 滑动删除'),
      DemoCard(title: 'Dismissible', desc: '左滑/右滑删除', child: SizedBox(
        height: 200,
        child: _DismissibleList(),
      )),
      const Section('Draggable + DragTarget'),
      DemoCard(title: 'Draggable & DragTarget', desc: '拖放交互', child: const _DragDemo()),
      const Section('Slider 滑块'),
      DemoCard(title: 'Slider', child: Obx(() => Column(children: [
        Slider(value: ctrl.sliderValue.value, onChanged: (v) => ctrl.sliderValue.value = v),
        Text('当前值: ${ctrl.sliderValue.value.toStringAsFixed(2)}'),
      ]))),
      const Section('RangeSlider 范围滑块'),
      DemoCard(title: 'RangeSlider', child: _RangeSliderDemo()),
      const Section('Switch 开关'),
      DemoCard(title: 'Switch', child: Obx(() => SwitchListTile(
        title: const Text('通知开关'),
        value: ctrl.switchValue.value,
        onChanged: (v) => ctrl.switchValue.value = v,
      ))),
      const Section('Checkbox 复选框'),
      DemoCard(title: 'Checkbox', child: Obx(() => CheckboxListTile(
        title: const Text('同意用户协议'),
        value: ctrl.checkboxValue.value,
        onChanged: (v) => ctrl.checkboxValue.value = v ?? false,
      ))),
      const Section('Radio 单选'),
      DemoCard(title: 'Radio', child: Obx(() => Column(children: [1, 2, 3].map((i) => RadioListTile<int>(
        title: Text('选项 $i'),
        value: i,
        groupValue: ctrl.radioValue.value,
        onChanged: (v) => ctrl.radioValue.value = v!,
      )).toList()))),
      const Section('PopupMenuButton'),
      DemoCard(title: 'PopupMenuButton', child: PopupMenuButton<String>(
        onSelected: (v) => Get.snackbar('选择', v, duration: const Duration(seconds: 1)),
        itemBuilder: (_) => const [PopupMenuItem(value: '复制', child: Text('复制')), PopupMenuItem(value: '粘贴', child: Text('粘贴')), PopupMenuItem(value: '剪切', child: Text('剪切'))],
        child: const Padding(padding: EdgeInsets.all(8), child: Text('点击弹出菜单 ▾')),
      )),
      const Section('ExpansionPanel'),
      DemoCard(title: 'ExpansionPanelList', child: _ExpansionDemo()),
      const Section('Tooltip'),
      DemoCard(title: 'Tooltip', desc: '长按显示提示', child: Wrap(spacing: 16, children: const [
        Tooltip(message: '这是一个添加按钮', child: Icon(Icons.add, size: 32)),
        Tooltip(message: '这是一个搜索按钮', child: Icon(Icons.search, size: 32)),
        Tooltip(message: '这是一个设置按钮', child: Icon(Icons.settings, size: 32)),
      ])),
    ]);
  }
}

class _DismissibleList extends StatefulWidget {
  @override
  State<_DismissibleList> createState() => _DismissibleListState();
}

class _DismissibleListState extends State<_DismissibleList> {
  final items = List.generate(8, (i) => 'Item ${i + 1}');

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      itemCount: items.length,
      itemBuilder: (ctx, i) => Dismissible(
        key: Key(items[i]),
        background: Container(color: Colors.red, child: const Icon(Icons.delete, color: Colors.white)),
        secondaryBackground: Container(color: Colors.green, alignment: Alignment.centerRight, padding: const EdgeInsets.only(right: 16), child: const Icon(Icons.archive, color: Colors.white)),
        onDismissed: (_) => setState(() => items.removeAt(i)),
        child: ListTile(title: Text(items[i]), subtitle: const Text('左滑删除 / 右滑归档')),
      ),
    );
  }
}

class _DragDemo extends StatefulWidget {
  const _DragDemo();
  @override
  State<_DragDemo> createState() => _DragDemoState();
}

class _DragDemoState extends State<_DragDemo> {
  Color _targetColor = Colors.grey;
  String _targetText = '拖拽到此处';

  @override
  Widget build(BuildContext context) {
    return Row(mainAxisAlignment: MainAxisAlignment.spaceEvenly, children: [
      Draggable<Color>(
        data: Colors.blue,
        feedback: Material(child: Container(width: 60, height: 60, decoration: const BoxDecoration(color: Colors.blue, shape: BoxShape.circle))),
        childWhenDragging: Container(width: 60, height: 60, decoration: BoxDecoration(color: Colors.blue.withValues(alpha: 0.3), shape: BoxShape.circle)),
        child: Container(width: 60, height: 60, decoration: const BoxDecoration(color: Colors.blue, shape: BoxShape.circle)),
      ),
      DragTarget<Color>(
        onAccept: (color) => setState(() { _targetColor = color; _targetText = '放下成功!'; }),
        onWillAcceptWithDetails: (_) => true,
        builder: (ctx, accepted, rejected) => Container(
          width: 100, height: 100,
          decoration: BoxDecoration(color: accepted.isNotEmpty ? Colors.blue.withValues(alpha: 0.2) : _targetColor.withValues(alpha: 0.3), borderRadius: BorderRadius.circular(12), border: Border.all(color: _targetColor, width: 2)),
          child: Center(child: Text(_targetText, textAlign: TextAlign.center, style: TextStyle(color: _targetColor))),
        ),
      ),
    ]);
  }
}

class _RangeSliderDemo extends StatefulWidget {
  @override
  State<_RangeSliderDemo> createState() => _RangeSliderDemoState();
}

class _RangeSliderDemoState extends State<_RangeSliderDemo> {
  RangeValues _values = const RangeValues(0.2, 0.8);

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      RangeSlider(values: _values, onChanged: (v) => setState(() => _values = v)),
      Text('范围: ${_values.start.toStringAsFixed(1)} - ${_values.end.toStringAsFixed(1)}'),
    ]);
  }
}

class _ExpansionDemo extends StatefulWidget {
  @override
  State<_ExpansionDemo> createState() => _ExpansionDemoState();
}

class _ExpansionDemoState extends State<_ExpansionDemo> {
  final _expanded = <int, bool>{};

  @override
  Widget build(BuildContext context) {
    return ExpansionPanelList(
      expansionCallback: (i, expanded) => setState(() => _expanded[i] = !expanded),
      children: [0, 1, 2].map((i) => ExpansionPanel(
        headerBuilder: (_, __) => ListTile(title: Text('面板 ${i + 1}')),
        body: Padding(padding: const EdgeInsets.all(16), child: Text('这是面板 ${i + 1} 的内容。')),
        isExpanded: _expanded[i] ?? false,
      )).toList(),
    );
  }
}
