import 'package:flutter/material.dart';
import '../../widgets/example_card.dart';

/// 容器 Widget 示例页面
class ContainerPage extends StatelessWidget {
  const ContainerPage({super.key});

  @override
  Widget build(BuildContext context) {
    return ListView(
      children: [
        const ExampleSectionHeader(title: '基础容器', icon: Icons.crop_square),

        // Container
        ExampleCard(
          title: 'Container',
          description: '多功能容器组件',
          code: 'Container(padding: EdgeInsets.all(16), decoration: BoxDecoration(...))',
          child: Column(
            children: [
              Container(
                width: double.infinity,
                padding: const EdgeInsets.all(16),
                margin: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  color: Theme.of(context).colorScheme.primaryContainer,
                  borderRadius: BorderRadius.circular(12),
                  border: Border.all(
                    color: Theme.of(context).colorScheme.primary,
                    width: 2,
                  ),
                  boxShadow: const [
                    BoxShadow(
                      color: Colors.black26,
                      blurRadius: 8,
                      offset: Offset(4, 4),
                    ),
                  ],
                ),
                child: const Text('带边框、圆角、阴影的容器'),
              ),
              const SizedBox(height: 12),
              Container(
                width: double.infinity,
                height: 80,
                decoration: BoxDecoration(
                  gradient: LinearGradient(
                    colors: [
                      Theme.of(context).colorScheme.primary,
                      Theme.of(context).colorScheme.secondary,
                    ],
                    begin: Alignment.topLeft,
                    end: Alignment.bottomRight,
                  ),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: const Center(
                  child: Text('渐变背景容器', style: TextStyle(color: Colors.white)),
                ),
              ),
            ],
          ),
        ),

        // DecoratedBox
        ExampleCard(
          title: 'DecoratedBox',
          description: '装饰盒子',
          code: 'DecoratedBox(decoration: BoxDecoration(gradient: ...))',
          child: DecoratedBox(
            decoration: BoxDecoration(
              gradient: const RadialGradient(
                colors: [Colors.yellow, Colors.orange],
              ),
              borderRadius: BorderRadius.circular(8),
            ),
            child: Container(
              width: double.infinity,
              height: 80,
              alignment: Alignment.center,
              child: const Text('径向渐变装饰', style: TextStyle(fontWeight: FontWeight.bold)),
            ),
          ),
        ),

        // ColoredBox
        ExampleCard(
          title: 'ColoredBox',
          description: '简单的颜色盒子',
          code: 'ColoredBox(color: Colors.teal, child: Widget)',
          child: const ColoredBox(
            color: Colors.teal,
            child: SizedBox(
              width: double.infinity,
              height: 60,
              child: Center(
                child: Text('ColoredBox', style: TextStyle(color: Colors.white)),
              ),
            ),
          ),
        ),

        const ExampleSectionHeader(title: '间距与填充', icon: Icons.padding),

        // Padding
        ExampleCard(
          title: 'Padding',
          description: '内边距组件',
          code: 'Padding(padding: EdgeInsets.all(16), child: Widget)',
          child: Container(
            color: Theme.of(context).colorScheme.surfaceContainerHighest,
            child: Padding(
              padding: const EdgeInsets.all(24),
              child: Container(
                color: Theme.of(context).colorScheme.primary,
                padding: const EdgeInsets.all(16),
                child: const Text('Padding(24) + Container', style: TextStyle(color: Colors.white)),
              ),
            ),
          ),
        ),

        // EdgeInsets 演示
        ExampleCard(
          title: 'EdgeInsets',
          description: '各种边距用法',
          code: 'EdgeInsets.all(16) / symmetric(horizontal: 16, vertical: 8)',
          child: Column(
            children: [
              _buildEdgeInsetsDemo(context, 'all(16)', const EdgeInsets.all(16)),
              const SizedBox(height: 8),
              _buildEdgeInsetsDemo(context, 'symmetric(horizontal: 20)', 
                const EdgeInsets.symmetric(horizontal: 20)),
              const SizedBox(height: 8),
              _buildEdgeInsetsDemo(context, 'only(left: 40)', 
                const EdgeInsets.only(left: 40)),
              const SizedBox(height: 8),
              _buildEdgeInsetsDemo(context, 'fromLTRB(10, 20, 30, 40)', 
                const EdgeInsets.fromLTRB(10, 20, 30, 40)),
            ],
          ),
        ),

        const ExampleSectionHeader(title: '对齐与定位', icon: Icons.align_horizontal_left),

        // Center
        ExampleCard(
          title: 'Center',
          description: '居中对齐组件',
          code: 'Center(child: Widget)',
          child: Container(
            height: 100,
            color: Theme.of(context).colorScheme.surfaceContainerHighest,
            child: const Center(
              child: Text('居中的内容'),
            ),
          ),
        ),

        // Align
        ExampleCard(
          title: 'Align',
          description: '对齐组件',
          code: 'Align(alignment: Alignment.center, child: Widget)',
          child: Column(
            children: [
              SizedBox(
                height: 60,
                child: Row(
                  children: [
                    Expanded(
                      child: Container(
                        color: Theme.of(context).colorScheme.surfaceContainerHighest,
                        child: const Align(
                          alignment: Alignment.topLeft,
                          child: Text('左上'),
                        ),
                      ),
                    ),
                    const SizedBox(width: 8),
                    Expanded(
                      child: Container(
                        color: Theme.of(context).colorScheme.surfaceContainerHighest,
                        child: const Align(
                          alignment: Alignment.center,
                          child: Text('居中'),
                        ),
                      ),
                    ),
                    const SizedBox(width: 8),
                    Expanded(
                      child: Container(
                        color: Theme.of(context).colorScheme.surfaceContainerHighest,
                        child: const Align(
                          alignment: Alignment.bottomRight,
                          child: Text('右下'),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 8),
              Container(
                height: 60,
                color: Theme.of(context).colorScheme.surfaceContainerHighest,
                child: const Align(
                  alignment: Alignment(0.8, 0.5),
                  child: Text('Alignment(0.8, 0.5)'),
                ),
              ),
            ],
          ),
        ),

        // FractionalOffset
        ExampleCard(
          title: 'Alignment & FractionalOffset',
          description: '对齐位置常量',
          code: 'Alignment.topLeft / Alignment.center',
          child: Wrap(
            spacing: 8,
            runSpacing: 8,
            children: [
              _buildAlignmentChip('topLeft', Alignment.topLeft),
              _buildAlignmentChip('topCenter', Alignment.topCenter),
              _buildAlignmentChip('topRight', Alignment.topRight),
              _buildAlignmentChip('centerLeft', Alignment.centerLeft),
              _buildAlignmentChip('center', Alignment.center),
              _buildAlignmentChip('centerRight', Alignment.centerRight),
              _buildAlignmentChip('bottomLeft', Alignment.bottomLeft),
              _buildAlignmentChip('bottomCenter', Alignment.bottomCenter),
              _buildAlignmentChip('bottomRight', Alignment.bottomRight),
            ],
          ),
        ),

        const ExampleSectionHeader(title: '限制尺寸', icon: Icons.open_in_full),

        // LimitedBox
        ExampleCard(
          title: 'LimitedBox',
          description: '限制最大尺寸',
          code: 'LimitedBox(maxWidth: 150, maxHeight: 60, child: Widget)',
          child: SingleChildScrollView(
            scrollDirection: Axis.horizontal,
            child: Row(
              children: [
                Container(
                  width: 300,
                  height: 100,
                  color: Theme.of(context).colorScheme.surfaceContainerHighest,
                  child: const LimitedBox(
                    maxWidth: 150,
                    maxHeight: 60,
                    child: ColoredBox(
                      color: Colors.orange,
                      child: Center(child: Text('maxWidth: 150')),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),

        // UnconstrainedBox
        ExampleCard(
          title: 'UnconstrainedBox',
          description: '移除父组件约束',
          code: 'UnconstrainedBox(child: Widget)',
          child: Container(
            width: 100,
            height: 100,
            color: Theme.of(context).colorScheme.surfaceContainerHighest,
            child: UnconstrainedBox(
              alignment: Alignment.center,
              child: Container(
                width: 150,
                height: 50,
                color: Colors.green,
                child: const Center(child: Text('150x50')),
              ),
            ),
          ),
        ),

        // OverflowBox
        ExampleCard(
          title: 'OverflowBox',
          description: '允许溢出父组件',
          code: 'OverflowBox(maxWidth: 200, child: Widget)',
          child: Container(
            width: 150,
            height: 80,
            color: Theme.of(context).colorScheme.surfaceContainerHighest,
            child: OverflowBox(
              maxWidth: 200,
              maxHeight: 100,
              alignment: Alignment.center,
              child: Container(
                width: 200,
                height: 60,
                color: Colors.red,
                child: const Center(child: Text('200x60 溢出')),
              ),
            ),
          ),
        ),

        // ClipRect
        ExampleCard(
          title: 'ClipRect',
          description: '矩形裁剪',
          code: 'ClipRect(child: Widget)',
          child: ClipRect(
            child: Container(
              width: double.infinity,
              height: 80,
              color: Colors.blue,
              child: Align(
                alignment: Alignment.topLeft,
                child: Container(
                  width: 200,
                  height: 120,
                  color: Colors.red,
                  child: const Center(child: Text('被裁剪的内容')),
                ),
              ),
            ),
          ),
        ),

        // ClipRRect
        ExampleCard(
          title: 'ClipRRect',
          description: '圆角矩形裁剪',
          code: 'ClipRRect(borderRadius: BorderRadius.circular(20), child: Widget)',
          child: ClipRRect(
            borderRadius: BorderRadius.circular(20),
            child: Container(
              width: double.infinity,
              height: 100,
              color: Colors.purple,
              child: const Center(
                child: Text('圆角矩形裁剪', style: TextStyle(color: Colors.white)),
              ),
            ),
          ),
        ),

        // ClipOval
        ExampleCard(
          title: 'ClipOval',
          description: '椭圆裁剪',
          code: 'ClipOval(child: Widget)',
          child: Center(
            child: ClipOval(
              child: Container(
                width: 150,
                height: 100,
                color: Colors.teal,
                child: const Center(
                  child: Text('椭圆裁剪', style: TextStyle(color: Colors.white)),
                ),
              ),
            ),
          ),
        ),

        // ClipPath
        ExampleCard(
          title: 'ClipPath',
          description: '自定义路径裁剪',
          code: 'ClipPath(clipper: CustomClipper, child: Widget)',
          child: Center(
            child: ClipPath(
              clipper: _TriangleClipper(),
              child: Container(
                width: 150,
                height: 120,
                color: Colors.amber,
                child: const Center(
                  child: Padding(
                    padding: EdgeInsets.only(bottom: 20),
                    child: Text('三角形裁剪'),
                  ),
                ),
              ),
            ),
          ),
        ),

        const ExampleSectionHeader(title: '装饰与变换', icon: Icons.transform),

        // Transform
        ExampleCard(
          title: 'Transform',
          description: '变换组件',
          code: 'Transform.rotate(angle: 0.5, child: Widget)',
          child: Column(
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceAround,
                children: [
                  Transform.rotate(
                    angle: 0.2,
                    child: Container(
                      width: 60,
                      height: 60,
                      color: Colors.red,
                      child: const Center(child: Text('旋转')),
                    ),
                  ),
                  Transform.scale(
                    scale: 1.2,
                    child: Container(
                      width: 60,
                      height: 60,
                      color: Colors.green,
                      child: const Center(child: Text('缩放')),
                    ),
                  ),
                  Transform.translate(
                    offset: const Offset(10, 10),
                    child: Container(
                      width: 60,
                      height: 60,
                      color: Colors.blue,
                      child: const Center(child: Text('平移')),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 12),
              Transform(
                transform: Matrix4.skewX(0.3),
                child: Container(
                  width: double.infinity,
                  height: 60,
                  color: Colors.purple,
                  child: const Center(
                    child: Text('X轴斜切', style: TextStyle(color: Colors.white)),
                  ),
                ),
              ),
            ],
          ),
        ),

        // RotatedBox
        ExampleCard(
          title: 'RotatedBox',
          description: '旋转盒子',
          code: 'RotatedBox(quarterTurns: 1, child: Widget)',
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            children: List.generate(4, (index) {
              return RotatedBox(
                quarterTurns: index,
                child: Container(
                  padding: const EdgeInsets.all(8),
                  color: Colors.primaries[index],
                  child: Text('$index'),
                ),
              );
            }),
          ),
        ),

        // FittedBox
        ExampleCard(
          title: 'FittedBox',
          description: '自适应缩放',
          code: 'FittedBox(fit: BoxFit.contain, child: Widget)',
          child: Column(
            children: [
              Container(
                width: double.infinity,
                height: 60,
                color: Theme.of(context).colorScheme.surfaceContainerHighest,
                child: const FittedBox(
                  child: Text('这是一段很长很长的文本，FittedBox 会自动缩放'),
                ),
              ),
              const SizedBox(height: 8),
              Container(
                width: 150,
                height: 80,
                color: Theme.of(context).colorScheme.surfaceContainerHighest,
                child: const FittedBox(
                  fit: BoxFit.cover,
                  child: FlutterLogo(size: 100),
                ),
              ),
            ],
          ),
        ),

        // Opacity
        ExampleCard(
          title: 'Opacity',
          description: '透明度',
          code: 'Opacity(opacity: 0.5, child: Widget)',
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            children: [
              _buildOpacityBox(context, 1.0),
              _buildOpacityBox(context, 0.7),
              _buildOpacityBox(context, 0.4),
              _buildOpacityBox(context, 0.2),
            ],
          ),
        ),

        // Visibility
        ExampleCard(
          title: 'Visibility',
          description: '可见性控制',
          code: 'Visibility(visible: true, child: Widget)',
          child: _VisibilityDemo(),
        ),

        // Offstage
        ExampleCard(
          title: 'Offstage',
          description: '离场控制',
          code: 'Offstage(offstage: true, child: Widget)',
          child: _OffstageDemo(),
        ),

        const SizedBox(height: 24),
      ],
    );
  }

  Widget _buildEdgeInsetsDemo(BuildContext context, String label, EdgeInsets padding) {
    return Container(
      color: Theme.of(context).colorScheme.surfaceContainerHighest,
      child: Padding(
        padding: padding,
        child: Container(
          color: Theme.of(context).colorScheme.primary,
          child: Center(
            child: Text(label, style: const TextStyle(color: Colors.white, fontSize: 12)),
          ),
        ),
      ),
    );
  }

  Widget _buildAlignmentChip(String label, Alignment alignment) {
    return Chip(
      label: Text(label, style: const TextStyle(fontSize: 10)),
      backgroundColor: Colors.primaries[label.hashCode % Colors.primaries.length].withValues(alpha: 0.2),
    );
  }

  Widget _buildOpacityBox(BuildContext context, double opacity) {
    return Column(
      children: [
        Opacity(
          opacity: opacity,
          child: Container(
            width: 60,
            height: 60,
            color: Colors.blue,
            child: const Center(child: Text('Box')),
          ),
        ),
        Text('opacity: $opacity', style: const TextStyle(fontSize: 10)),
      ],
    );
  }
}

/// 三角形裁剪器
class _TriangleClipper extends CustomClipper<Path> {
  @override
  Path getClip(Size size) {
    final path = Path();
    path.moveTo(size.width / 2, 0);
    path.lineTo(size.width, size.height);
    path.lineTo(0, size.height);
    path.close();
    return path;
  }

  @override
  bool shouldReclip(covariant CustomClipper<Path> oldClipper) => false;
}

/// Visibility 演示
class _VisibilityDemo extends StatefulWidget {
  @override
  State<_VisibilityDemo> createState() => _VisibilityDemoState();
}

class _VisibilityDemoState extends State<_VisibilityDemo> {
  bool _visible = true;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Visibility(
          visible: _visible,
          child: Container(
            width: double.infinity,
            height: 60,
            color: Colors.green,
            child: const Center(child: Text('可见的内容')),
          ),
        ),
        const SizedBox(height: 8),
        ElevatedButton(
          onPressed: () => setState(() => _visible = !_visible),
          child: Text(_visible ? '隐藏' : '显示'),
        ),
      ],
    );
  }
}

/// Offstage 演示
class _OffstageDemo extends StatefulWidget {
  @override
  State<_OffstageDemo> createState() => _OffstageDemoState();
}

class _OffstageDemoState extends State<_OffstageDemo> {
  bool _offstage = false;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Offstage(
          offstage: _offstage,
          child: Container(
            width: double.infinity,
            height: 60,
            color: Colors.purple,
            child: const Center(child: Text('Offstage 内容')),
          ),
        ),
        const SizedBox(height: 8),
        ElevatedButton(
          onPressed: () => setState(() => _offstage = !_offstage),
          child: Text(_offstage ? '显示' : '隐藏'),
        ),
      ],
    );
  }
}
