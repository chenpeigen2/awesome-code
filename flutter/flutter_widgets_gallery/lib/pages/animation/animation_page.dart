import 'dart:math';
import 'package:flutter/material.dart';
import 'package:flutter/physics.dart';
import '../../widgets/example_card.dart';

/// 动画 Widget 示例页面
class AnimationPage extends StatelessWidget {
  const AnimationPage({super.key});

  @override
  Widget build(BuildContext context) {
    return ListView(
      children: [
        const ExampleSectionHeader(title: '隐式动画', icon: Icons.animation),

        // AnimatedContainer
        ExampleCard(
          title: 'AnimatedContainer',
          description: '动画容器',
          child: _AnimatedContainerDemo(),
          code: 'AnimatedContainer(duration: Duration(milliseconds: 300))',
        ),

        // AnimatedOpacity
        ExampleCard(
          title: 'AnimatedOpacity',
          description: '动画透明度',
          child: _AnimatedOpacityDemo(),
          code: 'AnimatedOpacity(opacity: 1.0, duration: Duration(...))',
        ),

        // AnimatedAlign
        ExampleCard(
          title: 'AnimatedAlign',
          description: '动画对齐',
          child: _AnimatedAlignDemo(),
          code: 'AnimatedAlign(alignment: Alignment.center, duration: ...)',
        ),

        // AnimatedPadding
        ExampleCard(
          title: 'AnimatedPadding',
          description: '动画内边距',
          child: _AnimatedPaddingDemo(),
          code: 'AnimatedPadding(padding: EdgeInsets.all(16), duration: ...)',
        ),

        // AnimatedPositioned
        ExampleCard(
          title: 'AnimatedPositioned',
          description: '动画定位',
          child: _AnimatedPositionedDemo(),
          code: 'AnimatedPositioned(left: 0, top: 0, duration: ...)',
        ),

        // AnimatedCrossFade
        ExampleCard(
          title: 'AnimatedCrossFade',
          description: '交叉淡入淡出动画',
          child: _AnimatedCrossFadeDemo(),
          code: 'AnimatedCrossFade(firstChild: ..., secondChild: ...)',
        ),

        // AnimatedSize
        ExampleCard(
          title: 'AnimatedSize',
          description: '动画尺寸变化',
          child: _AnimatedSizeDemo(),
          code: 'AnimatedSize(duration: Duration(...), child: ...)',
        ),

        // AnimatedDefaultTextStyle
        ExampleCard(
          title: 'AnimatedDefaultTextStyle',
          description: '动画文本样式',
          child: _AnimatedTextStyleDemo(),
          code: 'AnimatedDefaultTextStyle(style: TextStyle(...), duration: ...)',
        ),

        const ExampleSectionHeader(title: '过渡动画', icon: Icons.swap_horiz),

        // AnimatedSwitcher
        ExampleCard(
          title: 'AnimatedSwitcher',
          description: '子组件切换动画',
          child: _AnimatedSwitcherDemo(),
          code: 'AnimatedSwitcher(duration: Duration(...), child: ...)',
        ),

        // TweenAnimationBuilder
        ExampleCard(
          title: 'TweenAnimationBuilder',
          description: '补间动画构建器',
          child: _TweenAnimationBuilderDemo(),
          code: 'TweenAnimationBuilder(tween: Tween(...), builder: ...)',
        ),

        const ExampleSectionHeader(title: '显式动画', icon: Icons.animation),

        // AnimationController
        ExampleCard(
          title: 'AnimationController',
          description: '动画控制器（需要 StatefulWidget）',
          child: _AnimationControllerDemo(),
          code: 'AnimationController(duration: Duration(...), vsync: this)',
        ),

        // AnimatedBuilder
        ExampleCard(
          title: 'AnimatedBuilder',
          description: '动画构建器',
          child: _AnimatedBuilderDemo(),
          code: 'AnimatedBuilder(animation: controller, builder: ...)',
        ),

        // Curves
        ExampleCard(
          title: 'Curves 动画曲线',
          description: '不同的动画效果曲线',
          child: _CurvesDemo(),
          code: 'Curves.easeInOut / Curves.elasticOut',
        ),

        const ExampleSectionHeader(title: 'Hero动画', icon: Icons.star),

        // Hero
        ExampleCard(
          title: 'Hero',
          description: '共享元素过渡动画',
          child: Column(
            children: [
              const Text('点击卡片跳转到详情页查看 Hero 动画效果'),
              const SizedBox(height: 12),
              Hero(
                tag: 'hero-demo',
                child: Card(
                  color: Theme.of(context).colorScheme.primaryContainer,
                  child: InkWell(
                    onTap: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (_) => _HeroDetailPage(),
                        ),
                      );
                    },
                    child: const Padding(
                      padding: EdgeInsets.all(24),
                      child: Icon(Icons.photo, size: 48),
                    ),
                  ),
                ),
              ),
            ],
          ),
          code: 'Hero(tag: "unique-tag", child: Widget)',
        ),

        const ExampleSectionHeader(title: '高级动画', icon: Icons.auto_awesome),

        // Lottie 风格（简化版）
        ExampleCard(
          title: '自定义动画绘制',
          description: '使用 CustomPainter 创建动画',
          child: _CustomPainterAnimationDemo(),
          code: 'CustomPaint(painter: CustomPainter())',
        ),

        // 粒子动画
        ExampleCard(
          title: '粒子动画',
          description: '多个动画组合',
          child: _ParticleAnimationDemo(),
          code: 'Stack + AnimatedBuilder + CustomPaint',
        ),

        // 物理动画
        ExampleCard(
          title: '物理动画',
          description: '模拟物理效果',
          child: _PhysicsAnimationDemo(),
          code: 'AnimationController + SpringSimulation',
        ),

        const SizedBox(height: 24),
      ],
    );
  }
}

/// AnimatedContainer 演示
class _AnimatedContainerDemo extends StatefulWidget {
  @override
  State<_AnimatedContainerDemo> createState() => _AnimatedContainerDemoState();
}

class _AnimatedContainerDemoState extends State<_AnimatedContainerDemo> {
  bool _expanded = false;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        AnimatedContainer(
          duration: const Duration(milliseconds: 300),
          width: _expanded ? double.infinity : 100,
          height: 80,
          decoration: BoxDecoration(
            color: _expanded ? Colors.blue : Colors.red,
            borderRadius: BorderRadius.circular(_expanded ? 24 : 8),
          ),
          child: const Center(child: Text('AnimatedContainer', style: TextStyle(color: Colors.white))),
        ),
        const SizedBox(height: 8),
        ElevatedButton(
          onPressed: () => setState(() => _expanded = !_expanded),
          child: Text(_expanded ? '收缩' : '展开'),
        ),
      ],
    );
  }
}

/// AnimatedOpacity 演示
class _AnimatedOpacityDemo extends StatefulWidget {
  @override
  State<_AnimatedOpacityDemo> createState() => _AnimatedOpacityDemoState();
}

class _AnimatedOpacityDemoState extends State<_AnimatedOpacityDemo> {
  double _opacity = 1.0;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        AnimatedOpacity(
          opacity: _opacity,
          duration: const Duration(milliseconds: 300),
          child: Container(
            width: double.infinity,
            height: 60,
            color: Colors.purple,
            child: const Center(child: Text('AnimatedOpacity', style: TextStyle(color: Colors.white))),
          ),
        ),
        const SizedBox(height: 8),
        ElevatedButton(
          onPressed: () => setState(() => _opacity = _opacity == 1.0 ? 0.2 : 1.0),
          child: Text(_opacity == 1.0 ? '淡出' : '淡入'),
        ),
      ],
    );
  }
}

/// AnimatedAlign 演示
class _AnimatedAlignDemo extends StatefulWidget {
  @override
  State<_AnimatedAlignDemo> createState() => _AnimatedAlignDemoState();
}

class _AnimatedAlignDemoState extends State<_AnimatedAlignDemo> {
  bool _alignLeft = true;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Container(
          height: 60,
          color: Theme.of(context).colorScheme.surfaceContainerHighest,
          child: AnimatedAlign(
            alignment: _alignLeft ? Alignment.centerLeft : Alignment.centerRight,
            duration: const Duration(milliseconds: 300),
            curve: Curves.easeInOut,
            child: Container(
              width: 60,
              height: 40,
              color: Colors.orange,
              child: const Center(child: Text('盒子')),
            ),
          ),
        ),
        const SizedBox(height: 8),
        ElevatedButton(
          onPressed: () => setState(() => _alignLeft = !_alignLeft),
          child: Text(_alignLeft ? '移到右边' : '移到左边'),
        ),
      ],
    );
  }
}

/// AnimatedPadding 演示
class _AnimatedPaddingDemo extends StatefulWidget {
  @override
  State<_AnimatedPaddingDemo> createState() => _AnimatedPaddingDemoState();
}

class _AnimatedPaddingDemoState extends State<_AnimatedPaddingDemo> {
  bool _padded = false;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Container(
          color: Theme.of(context).colorScheme.surfaceContainerHighest,
          child: AnimatedPadding(
            padding: EdgeInsets.all(_padded ? 40 : 8),
            duration: const Duration(milliseconds: 300),
            child: Container(
              height: 60,
              color: Colors.teal,
              child: const Center(child: Text('AnimatedPadding', style: TextStyle(color: Colors.white))),
            ),
          ),
        ),
        const SizedBox(height: 8),
        ElevatedButton(
          onPressed: () => setState(() => _padded = !_padded),
          child: Text(_padded ? '减少边距' : '增加边距'),
        ),
      ],
    );
  }
}

/// AnimatedPositioned 演示
class _AnimatedPositionedDemo extends StatefulWidget {
  @override
  State<_AnimatedPositionedDemo> createState() => _AnimatedPositionedDemoState();
}

class _AnimatedPositionedDemoState extends State<_AnimatedPositionedDemo> {
  bool _topLeft = true;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        SizedBox(
          height: 120,
          child: Stack(
            children: [
              Container(
                decoration: BoxDecoration(
                  border: Border.all(color: Colors.grey),
                  borderRadius: BorderRadius.circular(8),
                ),
              ),
              AnimatedPositioned(
                top: _topLeft ? 10 : 60,
                left: _topLeft ? 10 : null,
                right: _topLeft ? null : 10,
                duration: const Duration(milliseconds: 300),
                child: Container(
                  width: 50,
                  height: 50,
                  color: Colors.pink,
                  child: const Center(child: Text('移动')),
                ),
              ),
            ],
          ),
        ),
        const SizedBox(height: 8),
        ElevatedButton(
          onPressed: () => setState(() => _topLeft = !_topLeft),
          child: const Text('移动位置'),
        ),
      ],
    );
  }
}

/// AnimatedCrossFade 演示
class _AnimatedCrossFadeDemo extends StatefulWidget {
  @override
  State<_AnimatedCrossFadeDemo> createState() => _AnimatedCrossFadeDemoState();
}

class _AnimatedCrossFadeDemoState extends State<_AnimatedCrossFadeDemo> {
  bool _showFirst = true;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        AnimatedCrossFade(
          firstChild: Container(
            width: double.infinity,
            height: 80,
            color: Colors.indigo,
            child: const Center(child: Text('第一个组件', style: TextStyle(color: Colors.white))),
          ),
          secondChild: Container(
            width: double.infinity,
            height: 80,
            color: Colors.cyan,
            child: const Center(child: Text('第二个组件', style: TextStyle(color: Colors.white))),
          ),
          crossFadeState: _showFirst ? CrossFadeState.showFirst : CrossFadeState.showSecond,
          duration: const Duration(milliseconds: 300),
        ),
        const SizedBox(height: 8),
        ElevatedButton(
          onPressed: () => setState(() => _showFirst = !_showFirst),
          child: Text(_showFirst ? '显示第二个' : '显示第一个'),
        ),
      ],
    );
  }
}

/// AnimatedSize 演示
class _AnimatedSizeDemo extends StatefulWidget {
  @override
  State<_AnimatedSizeDemo> createState() => _AnimatedSizeDemoState();
}

class _AnimatedSizeDemoState extends State<_AnimatedSizeDemo> {
  bool _expanded = false;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        AnimatedSize(
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeInOut,
          child: Container(
            width: double.infinity,
            height: _expanded ? 120 : 60,
            color: Colors.lime,
            child: Center(
              child: Text(
                _expanded ? '展开状态 - 更多内容展示' : '折叠状态',
                style: const TextStyle(fontSize: 16),
              ),
            ),
          ),
        ),
        const SizedBox(height: 8),
        ElevatedButton(
          onPressed: () => setState(() => _expanded = !_expanded),
          child: Text(_expanded ? '折叠' : '展开'),
        ),
      ],
    );
  }
}

/// AnimatedTextStyle 演示
class _AnimatedTextStyleDemo extends StatefulWidget {
  @override
  State<_AnimatedTextStyleDemo> createState() => _AnimatedTextStyleDemoState();
}

class _AnimatedTextStyleDemoState extends State<_AnimatedTextStyleDemo> {
  bool _large = false;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        AnimatedDefaultTextStyle(
          duration: const Duration(milliseconds: 300),
          style: TextStyle(
            fontSize: _large ? 28 : 16,
            color: _large ? Colors.red : Colors.black,
            fontWeight: _large ? FontWeight.bold : FontWeight.normal,
          ),
          child: const Text('动画文本样式'),
        ),
        const SizedBox(height: 8),
        ElevatedButton(
          onPressed: () => setState(() => _large = !_large),
          child: Text(_large ? '小字体' : '大字体'),
        ),
      ],
    );
  }
}

/// AnimatedSwitcher 演示
class _AnimatedSwitcherDemo extends StatefulWidget {
  @override
  State<_AnimatedSwitcherDemo> createState() => _AnimatedSwitcherDemoState();
}

class _AnimatedSwitcherDemoState extends State<_AnimatedSwitcherDemo> {
  int _count = 0;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        AnimatedSwitcher(
          duration: const Duration(milliseconds: 300),
          transitionBuilder: (child, animation) {
            return ScaleTransition(scale: animation, child: child);
          },
          child: Text(
            '$_count',
            key: ValueKey(_count),
            style: const TextStyle(fontSize: 48, fontWeight: FontWeight.bold),
          ),
        ),
        const SizedBox(height: 8),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            IconButton(
              icon: const Icon(Icons.remove),
              onPressed: () => setState(() => _count--),
            ),
            IconButton(
              icon: const Icon(Icons.add),
              onPressed: () => setState(() => _count++),
            ),
          ],
        ),
      ],
    );
  }
}

/// TweenAnimationBuilder 演示
class _TweenAnimationBuilderDemo extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return TweenAnimationBuilder<double>(
      tween: Tween(begin: 0, end: 1),
      duration: const Duration(seconds: 2),
      builder: (context, value, child) {
        return Column(
          children: [
            LinearProgressIndicator(value: value),
            const SizedBox(height: 8),
            Text('进度: ${(value * 100).toStringAsFixed(0)}%'),
          ],
        );
      },
    );
  }
}

/// AnimationController 演示
class _AnimationControllerDemo extends StatefulWidget {
  @override
  State<_AnimationControllerDemo> createState() => _AnimationControllerDemoState();
}

class _AnimationControllerDemoState extends State<_AnimationControllerDemo>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: const Duration(seconds: 2),
      vsync: this,
    )..repeat(reverse: true);
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _controller,
      builder: (context, child) {
        return Container(
          width: double.infinity,
          height: 60,
          decoration: BoxDecoration(
            gradient: LinearGradient(
              colors: const [Colors.blue, Colors.purple],
              stops: [0, _controller.value],
            ),
          ),
          child: const Center(
            child: Text('渐变动画', style: TextStyle(color: Colors.white)),
          ),
        );
      },
    );
  }
}

/// AnimatedBuilder 演示
class _AnimatedBuilderDemo extends StatefulWidget {
  @override
  State<_AnimatedBuilderDemo> createState() => _AnimatedBuilderDemoState();
}

class _AnimatedBuilderDemoState extends State<_AnimatedBuilderDemo>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: const Duration(milliseconds: 1500),
      vsync: this,
    )..repeat();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _controller,
      builder: (context, child) {
        return Transform.rotate(
          angle: _controller.value * 2 * 3.14159,
          child: Container(
            width: 80,
            height: 80,
            color: Colors.green,
            child: const Center(child: Text('旋转')),
          ),
        );
      },
    );
  }
}

/// Curves 演示
class _CurvesDemo extends StatefulWidget {
  @override
  State<_CurvesDemo> createState() => _CurvesDemoState();
}

class _CurvesDemoState extends State<_CurvesDemo> with TickerProviderStateMixin {
  final List<(String, Curve)> _curves = const [
    ('linear', Curves.linear),
    ('easeIn', Curves.easeIn),
    ('easeOut', Curves.easeOut),
    ('easeInOut', Curves.easeInOut),
    ('bounceOut', Curves.bounceOut),
    ('elasticOut', Curves.elasticOut),
  ];

  final Map<String, AnimationController> _controllers = {};

  @override
  void initState() {
    super.initState();
    for (final curve in _curves) {
      _controllers[curve.$1] = AnimationController(
        duration: const Duration(milliseconds: 1000),
        vsync: this,
      );
    }
  }

  @override
  void dispose() {
    for (final controller in _controllers.values) {
      controller.dispose();
    }
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: _curves.map((item) {
        final (name, curve) = item;
        final controller = _controllers[name]!;
        return Padding(
          padding: const EdgeInsets.symmetric(vertical: 4),
          child: Row(
            children: [
              SizedBox(width: 80, child: Text(name, style: const TextStyle(fontSize: 12))),
              Expanded(
                child: GestureDetector(
                  onTap: () {
                    controller.forward(from: 0);
                  },
                  child: Container(
                    height: 30,
                    color: Theme.of(context).colorScheme.surfaceContainerHighest,
                    child: AnimatedBuilder(
                      animation: CurvedAnimation(parent: controller, curve: curve),
                      builder: (context, child) {
                        return Positioned(
                          left: controller.value * (MediaQuery.of(context).size.width - 200),
                          child: Container(width: 30, height: 30, color: Colors.blue),
                        );
                      },
                    ),
                  ),
                ),
              ),
            ],
          ),
        );
      }).toList(),
    );
  }
}

/// Hero 详情页
class _HeroDetailPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Hero 详情')),
      body: Center(
        child: Hero(
          tag: 'hero-demo',
          child: Container(
            width: 200,
            height: 200,
            color: Colors.blue,
            child: const Center(
              child: Icon(Icons.photo, size: 100, color: Colors.white),
            ),
          ),
        ),
      ),
    );
  }
}

/// CustomPainter 动画演示
class _CustomPainterAnimationDemo extends StatefulWidget {
  @override
  State<_CustomPainterAnimationDemo> createState() => _CustomPainterAnimationDemoState();
}

class _CustomPainterAnimationDemoState extends State<_CustomPainterAnimationDemo>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: const Duration(seconds: 3),
      vsync: this,
    )..repeat();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _controller,
      builder: (context, child) {
        return CustomPaint(
          size: const Size(double.infinity, 100),
          painter: _WavePainter(_controller.value),
        );
      },
    );
  }
}

class _WavePainter extends CustomPainter {
  final double progress;

  _WavePainter(this.progress);

  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = Colors.blue
      ..style = PaintingStyle.fill;

    final path = Path();
    path.moveTo(0, size.height / 2);

    for (double x = 0; x <= size.width; x++) {
      final y = size.height / 2 + 
                20 * (0.5 + 0.5 * (progress * 2 * 3.14159)) * 
                (0.5 + 0.5 * (x / size.width * 4 * 3.14159 + progress * 4 * 3.14159));
      path.lineTo(x, y);
    }

    path.lineTo(size.width, size.height);
    path.lineTo(0, size.height);
    path.close();

    canvas.drawPath(path, paint);
  }

  @override
  bool shouldRepaint(covariant _WavePainter oldDelegate) => progress != oldDelegate.progress;
}

/// 粒子动画演示
class _ParticleAnimationDemo extends StatefulWidget {
  @override
  State<_ParticleAnimationDemo> createState() => _ParticleAnimationDemoState();
}

class _ParticleAnimationDemoState extends State<_ParticleAnimationDemo>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  final List<_Particle> _particles = List.generate(20, (i) => _Particle());

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: const Duration(seconds: 1),
      vsync: this,
    )..repeat();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _controller,
      builder: (context, child) {
        return SizedBox(
          height: 100,
          child: Stack(
            children: _particles.map((p) {
              final x = p.x + p.vx * _controller.value * 50;
              final y = p.y + p.vy * _controller.value * 50;
              final opacity = 1 - _controller.value;
              return Positioned(
                left: x,
                top: y,
                child: Opacity(
                  opacity: opacity,
                  child: Container(
                    width: 8,
                    height: 8,
                    decoration: BoxDecoration(
                      color: Colors.primaries[Random().nextInt(Colors.primaries.length)],
                      shape: BoxShape.circle,
                    ),
                  ),
                ),
              );
            }).toList(),
          ),
        );
      },
    );
  }
}

class _Particle {
  final double x = 50 + Random().nextDouble() * 200;
  final double y = 30 + Random().nextDouble() * 40;
  final double vx = (Random().nextDouble() - 0.5) * 2;
  final double vy = Random().nextDouble() - 1;
}

/// 物理动画演示
class _PhysicsAnimationDemo extends StatefulWidget {
  @override
  State<_PhysicsAnimationDemo> createState() => _PhysicsAnimationDemoState();
}

class _PhysicsAnimationDemoState extends State<_PhysicsAnimationDemo>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  double _position = 0;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
    );
    _controller.addListener(() {
      setState(() => _position = _controller.value);
    });
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  void _animateWithSpring() {
    final simulation = SpringSimulation(
      const SpringDescription(mass: 1, stiffness: 100, damping: 10),
      _position,
      1,
      0,
    );
    _controller.animateWith(simulation);
  }

  void _reset() {
    _controller.value = 0;
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Container(
          height: 60,
          color: Theme.of(context).colorScheme.surfaceContainerHighest,
          child: Stack(
            children: [
              Positioned(
                left: _position * (MediaQuery.of(context).size.width - 200),
                top: 10,
                child: Container(
                  width: 40,
                  height: 40,
                  decoration: const BoxDecoration(
                    color: Colors.green,
                    shape: BoxShape.circle,
                  ),
                ),
              ),
            ],
          ),
        ),
        const SizedBox(height: 8),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            ElevatedButton(onPressed: _animateWithSpring, child: const Text('弹簧动画')),
            const SizedBox(width: 8),
            ElevatedButton(onPressed: _reset, child: const Text('重置')),
          ],
        ),
      ],
    );
  }
}
