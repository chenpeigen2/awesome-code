import 'package:flutter/material.dart';
import '../../widgets/example_card.dart';

/// 布局 Widget 示例页面
class LayoutPage extends StatelessWidget {
  const LayoutPage({super.key});

  @override
  Widget build(BuildContext context) {
    return ListView(
      children: [
        const ExampleSectionHeader(title: '线性布局', icon: Icons.linear_scale),

        // Row
        ExampleCard(
          title: 'Row',
          description: '水平布局子组件',
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text('默认对齐：'),
              const SizedBox(height: 8),
              Container(
                width: double.infinity,
                padding: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  color: Theme.of(context).colorScheme.surfaceContainerHighest,
                  borderRadius: BorderRadius.circular(8),
                ),
                child: const Row(
                  children: [
                    _ColorBox(color: Colors.red, label: '1'),
                    _ColorBox(color: Colors.green, label: '2'),
                    _ColorBox(color: Colors.blue, label: '3'),
                  ],
                ),
              ),
              const SizedBox(height: 16),
              const Text('MainAxisAlignment.spaceEvenly：'),
              const SizedBox(height: 8),
              Container(
                width: double.infinity,
                padding: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  color: Theme.of(context).colorScheme.surfaceContainerHighest,
                  borderRadius: BorderRadius.circular(8),
                ),
                child: const Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    _ColorBox(color: Colors.red, label: '1'),
                    _ColorBox(color: Colors.green, label: '2'),
                    _ColorBox(color: Colors.blue, label: '3'),
                  ],
                ),
              ),
              const SizedBox(height: 16),
              const Text('带 Expanded：'),
              const SizedBox(height: 8),
              Container(
                width: double.infinity,
                padding: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  color: Theme.of(context).colorScheme.surfaceContainerHighest,
                  borderRadius: BorderRadius.circular(8),
                ),
                child: const Row(
                  children: [
                    Expanded(
                      child: _ColorBox(color: Colors.red, label: '1'),
                    ),
                    _ColorBox(color: Colors.green, label: '2'),
                    Expanded(
                      child: _ColorBox(color: Colors.blue, label: '3'),
                    ),
                  ],
                ),
              ),
            ],
          ),
          code: 'Row(children: [Widget1, Widget2, Widget3])',
        ),

        // Column
        ExampleCard(
          title: 'Column',
          description: '垂直布局子组件',
          child: Row(
            children: [
              Expanded(
                child: Container(
                  height: 200,
                  padding: const EdgeInsets.all(8),
                  decoration: BoxDecoration(
                    color: Theme.of(context).colorScheme.surfaceContainerHighest,
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: const Column(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      _ColorBox(color: Colors.amber, label: '1', width: 80),
                      _ColorBox(color: Colors.cyan, label: '2', width: 80),
                      _ColorBox(color: Colors.purple, label: '3', width: 80),
                    ],
                  ),
                ),
              ),
              const SizedBox(width: 8),
              Expanded(
                child: Container(
                  height: 200,
                  padding: const EdgeInsets.all(8),
                  decoration: BoxDecoration(
                    color: Theme.of(context).colorScheme.surfaceContainerHighest,
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: const Column(
                    mainAxisAlignment: MainAxisAlignment.spaceAround,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      _ColorBox(color: Colors.pink, label: '左对齐', width: 60),
                      _ColorBox(color: Colors.teal, label: '左对齐', width: 60),
                      _ColorBox(color: Colors.indigo, label: '左对齐', width: 60),
                    ],
                  ),
                ),
              ),
            ],
          ),
          code: 'Column(children: [Widget1, Widget2, Widget3])',
        ),

        // MainAxisAlignment 和 CrossAxisAlignment
        ExampleCard(
          title: 'MainAxis/CrossAxis Alignment',
          description: '主轴和交叉轴对齐方式',
          child: Column(
            children: [
              _buildAlignmentDemo(context, 'MainAxisAlignment.start', 
                mainAxisAlignment: MainAxisAlignment.start),
              const SizedBox(height: 8),
              _buildAlignmentDemo(context, 'MainAxisAlignment.center',
                mainAxisAlignment: MainAxisAlignment.center),
              const SizedBox(height: 8),
              _buildAlignmentDemo(context, 'MainAxisAlignment.spaceBetween',
                mainAxisAlignment: MainAxisAlignment.spaceBetween),
              const SizedBox(height: 8),
              _buildAlignmentDemo(context, 'MainAxisAlignment.spaceAround',
                mainAxisAlignment: MainAxisAlignment.spaceAround),
            ],
          ),
          code: 'Row(mainAxisAlignment: MainAxisAlignment.center)',
        ),

        const ExampleSectionHeader(title: '弹性布局', icon: Icons.expand),

        // Expanded
        ExampleCard(
          title: 'Expanded',
          description: '扩展填充剩余空间',
          child: Column(
            children: [
              Container(
                height: 60,
                decoration: BoxDecoration(
                  color: Theme.of(context).colorScheme.surfaceContainerHighest,
                  borderRadius: BorderRadius.circular(8),
                ),
                child: const Row(
                  children: [
                    Expanded(
                      flex: 1,
                      child: _ColorBox(color: Colors.red, label: 'flex: 1'),
                    ),
                    Expanded(
                      flex: 2,
                      child: _ColorBox(color: Colors.green, label: 'flex: 2'),
                    ),
                    Expanded(
                      flex: 1,
                      child: _ColorBox(color: Colors.blue, label: 'flex: 1'),
                    ),
                  ],
                ),
              ),
            ],
          ),
          code: 'Expanded(flex: 1, child: Widget)',
        ),

        // Flexible
        ExampleCard(
          title: 'Flexible',
          description: '灵活调整子组件大小',
          child: Container(
            height: 60,
            decoration: BoxDecoration(
              color: Theme.of(context).colorScheme.surfaceContainerHighest,
              borderRadius: BorderRadius.circular(8),
            ),
            child: const Row(
              children: [
                Flexible(
                  flex: 1,
                  fit: FlexFit.tight,
                  child: _ColorBox(color: Colors.orange, label: 'tight'),
                ),
                Flexible(
                  flex: 1,
                  fit: FlexFit.loose,
                  child: _ColorBox(color: Colors.purple, label: 'loose', width: 40),
                ),
                const _ColorBox(color: Colors.teal, label: '固定', width: 60),
              ],
            ),
          ),
          code: 'Flexible(flex: 1, fit: FlexFit.loose, child: Widget)',
        ),

        // Spacer
        ExampleCard(
          title: 'Spacer',
          description: '创建空白填充',
          child: Container(
            padding: const EdgeInsets.all(8),
            decoration: BoxDecoration(
              color: Theme.of(context).colorScheme.surfaceContainerHighest,
              borderRadius: BorderRadius.circular(8),
            ),
            child: const Row(
              children: [
                _ColorBox(color: Colors.red, label: '左'),
                Spacer(),
                _ColorBox(color: Colors.green, label: '中'),
                Spacer(flex: 2),
                _ColorBox(color: Colors.blue, label: '右'),
              ],
            ),
          ),
          code: 'Row(children: [Widget, Spacer(), Widget])',
        ),

        const ExampleSectionHeader(title: '堆叠布局', icon: Icons.layers),

        // Stack
        ExampleCard(
          title: 'Stack',
          description: '堆叠布局，子组件可以重叠',
          child: SizedBox(
            height: 180,
            child: Stack(
              children: [
                Container(
                  decoration: BoxDecoration(
                    color: Colors.blue.withOpacity(0.7),
                    borderRadius: BorderRadius.circular(12),
                  ),
                ),
                Positioned(
                  top: 20,
                  left: 20,
                  child: Container(
                    width: 120,
                    height: 80,
                    decoration: BoxDecoration(
                      color: Colors.green.withOpacity(0.7),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: const Center(child: Text('位置1')),
                  ),
                ),
                Positioned(
                  bottom: 20,
                  right: 20,
                  child: Container(
                    width: 100,
                    height: 60,
                    decoration: BoxDecoration(
                      color: Colors.red.withOpacity(0.7),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: const Center(child: Text('位置2')),
                  ),
                ),
                const Center(
                  child: Text(
                    'Stack 层叠',
                    style: TextStyle(color: Colors.white, fontSize: 16),
                  ),
                ),
              ],
            ),
          ),
          code: 'Stack(children: [Widget1, Positioned(child: Widget2)])',
        ),

        // IndexedStack
        ExampleCard(
          title: 'IndexedStack',
          description: '只显示指定索引的子组件',
          child: _IndexedStackDemo(),
          code: 'IndexedStack(index: 0, children: [...])',
        ),

        // Positioned
        ExampleCard(
          title: 'Positioned',
          description: '绝对定位组件',
          child: SizedBox(
            height: 150,
            child: Stack(
              children: [
                Container(
                  decoration: BoxDecoration(
                    border: Border.all(color: Colors.grey),
                    borderRadius: BorderRadius.circular(8),
                  ),
                ),
                Positioned(
                  top: 10,
                  left: 10,
                  child: _buildPositionedLabel('top: 10, left: 10'),
                ),
                Positioned(
                  top: 10,
                  right: 10,
                  child: _buildPositionedLabel('top: 10, right: 10'),
                ),
                Positioned(
                  bottom: 10,
                  left: 10,
                  child: _buildPositionedLabel('bottom: 10, left: 10'),
                ),
                Positioned(
                  bottom: 10,
                  right: 10,
                  child: _buildPositionedLabel('bottom: 10, right: 10'),
                ),
                Positioned.fill(
                  child: Center(
                    child: _buildPositionedLabel('Positioned.fill'),
                  ),
                ),
              ],
            ),
          ),
          code: 'Positioned(top: 10, left: 10, child: Widget)',
        ),

        const ExampleSectionHeader(title: '流式布局', icon: Icons.grid_view),

        // Wrap
        ExampleCard(
          title: 'Wrap',
          description: '流式布局，自动换行',
          child: Wrap(
            spacing: 8,
            runSpacing: 8,
            children: List.generate(
              20,
              (index) => Chip(
                label: Text('标签 ${index + 1}'),
                backgroundColor: Colors.primaries[index % Colors.primaries.length].withOpacity(0.3),
              ),
            ),
          ),
          code: 'Wrap(spacing: 8, runSpacing: 8, children: [...])',
        ),

        // Flow
        ExampleCard(
          title: 'Flow',
          description: '自定义流式布局',
          child: SizedBox(
            height: 150,
            child: Flow(
              delegate: _CircleFlowDelegate(),
              children: List.generate(
                8,
                (index) => Container(
                  width: 50,
                  height: 50,
                  decoration: BoxDecoration(
                    color: Colors.primaries[index % Colors.primaries.length],
                    shape: BoxShape.circle,
                  ),
                  child: Center(
                    child: Text(
                      '${index + 1}',
                      style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
                    ),
                  ),
                ),
              ),
            ),
          ),
          code: 'Flow(delegate: FlowDelegate, children: [...])',
        ),

        const ExampleSectionHeader(title: '约束布局', icon: Icons.aspect_ratio),

        // ConstrainedBox
        ExampleCard(
          title: 'ConstrainedBox',
          description: '约束子组件的大小',
          child: Column(
            children: [
              ConstrainedBox(
                constraints: const BoxConstraints(
                  minWidth: 100,
                  maxWidth: 200,
                  minHeight: 50,
                  maxHeight: 100,
                ),
                child: Container(
                  color: Colors.blue,
                  child: const Center(child: Text('ConstrainedBox')),
                ),
              ),
            ],
          ),
          code: 'ConstrainedBox(constraints: BoxConstraints(minWidth: 100), child: Widget)',
        ),

        // SizedBox
        ExampleCard(
          title: 'SizedBox',
          description: '固定尺寸盒子',
          child: Column(
            children: [
              const SizedBox(width: 100, height: 50, child: _ColorBox(color: Colors.red, label: '固定')),
              const SizedBox(height: 8),
              const SizedBox.expand(
                child: _ColorBox(color: Colors.green, label: 'SizedBox.expand'),
              ),
            ],
          ),
          code: 'SizedBox(width: 100, height: 50, child: Widget)',
        ),

        // AspectRatio
        ExampleCard(
          title: 'AspectRatio',
          description: '固定宽高比',
          child: Column(
            children: [
              AspectRatio(
                aspectRatio: 16 / 9,
                child: Container(
                  color: Colors.orange,
                  child: const Center(
                    child: Text('16:9 比例'),
                  ),
                ),
              ),
            ],
          ),
          code: 'AspectRatio(aspectRatio: 16/9, child: Widget)',
        ),

        // FractionallySizedBox
        ExampleCard(
          title: 'FractionallySizedBox',
          description: '按比例设置尺寸',
          child: Container(
            height: 100,
            color: Theme.of(context).colorScheme.surfaceContainerHighest,
            child: FractionallySizedBox(
              widthFactor: 0.7,
              heightFactor: 0.7,
              child: Container(
                color: Colors.purple,
                child: const Center(child: Text('70% 宽高')),
              ),
            ),
          ),
          code: 'FractionallySizedBox(widthFactor: 0.5, child: Widget)',
        ),

        // IntrinsicHeight & IntrinsicWidth
        ExampleCard(
          title: 'IntrinsicHeight/IntrinsicWidth',
          description: '根据子组件调整高度/宽度',
          child: IntrinsicHeight(
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Container(
                  width: 60,
                  color: Colors.red,
                  child: const Center(child: Text('短')),
                ),
                Container(
                  width: 80,
                  color: Colors.green,
                  child: const Center(child: Text('中等长度')),
                ),
                Container(
                  width: 70,
                  color: Colors.blue,
                  child: const Center(child: Text('较长内容')),
                ),
              ],
            ),
          ),
          code: 'IntrinsicHeight(child: Row(children: [...]))',
        ),

        const SizedBox(height: 24),
      ],
    );
  }

  Widget _buildAlignmentDemo(BuildContext context, String label,
      {MainAxisAlignment mainAxisAlignment = MainAxisAlignment.start}) {
    return Container(
      padding: const EdgeInsets.all(8),
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.surfaceContainerHighest,
        borderRadius: BorderRadius.circular(8),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(label, style: const TextStyle(fontSize: 12)),
          const SizedBox(height: 4),
          Row(
            mainAxisAlignment: mainAxisAlignment,
            children: [
              Container(width: 30, height: 30, color: Colors.red),
              Container(width: 30, height: 30, color: Colors.green),
              Container(width: 30, height: 30, color: Colors.blue),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildPositionedLabel(String text) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
        color: Colors.black54,
        borderRadius: BorderRadius.circular(4),
      ),
      child: Text(text, style: const TextStyle(color: Colors.white, fontSize: 10)),
    );
  }
}

/// 彩色盒子组件
class _ColorBox extends StatelessWidget {
  final Color color;
  final String label;
  final double? width;
  final double? height;

  const _ColorBox({
    required this.color,
    required this.label,
    this.width,
    this.height,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: width ?? 50,
      height: height ?? 50,
      decoration: BoxDecoration(
        color: color,
        borderRadius: BorderRadius.circular(4),
      ),
      child: Center(
        child: Text(
          label,
          style: const TextStyle(
            color: Colors.white,
            fontSize: 12,
            fontWeight: FontWeight.bold,
          ),
        ),
      ),
    );
  }
}

/// IndexedStack 演示
class _IndexedStackDemo extends StatefulWidget {
  const _IndexedStackDemo();

  @override
  State<_IndexedStackDemo> createState() => _IndexedStackDemoState();
}

class _IndexedStackDemoState extends State<_IndexedStackDemo> {
  int _index = 0;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        SizedBox(
          height: 100,
          child: IndexedStack(
            index: _index,
            children: const [
              _ColorBox(color: Colors.red, label: '页面 1', width: double.infinity, height: 100),
              _ColorBox(color: Colors.green, label: '页面 2', width: double.infinity, height: 100),
              _ColorBox(color: Colors.blue, label: '页面 3', width: double.infinity, height: 100),
            ],
          ),
        ),
        const SizedBox(height: 12),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: List.generate(3, (index) {
            return Padding(
              padding: const EdgeInsets.symmetric(horizontal: 4),
              child: ElevatedButton(
                onPressed: () => setState(() => _index = index),
                style: ElevatedButton.styleFrom(
                  backgroundColor: _index == index ? Theme.of(context).colorScheme.primary : null,
                ),
                child: Text('${index + 1}'),
              ),
            );
          }),
        ),
      ],
    );
  }
}

/// 圆形 Flow 布局代理
class _CircleFlowDelegate extends FlowDelegate {
  @override
  void paintChildren(FlowPaintingContext context) {
    final size = context.size;
    final count = context.childCount;
    final radius = size.width / 3;

    for (int i = 0; i < count; i++) {
      final angle = (i * 2 * 3.1415926 / count) - 3.1415926 / 2;
      final x = size.width / 2 + radius * 0.8 * (angle == 0 ? 0 : (angle > 0 ? 1 : -1)) - 25;
      final y = size.height / 2 + radius * 0.5 * (i % 2 == 0 ? -1 : 1) - 25;
      context.paintChild(i, transform: Matrix4.translationValues(x, y, 0));
    }
  }

  @override
  bool shouldRepaint(covariant FlowDelegate oldDelegate) => false;
}
