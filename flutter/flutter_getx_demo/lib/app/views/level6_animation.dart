import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../routes/app_routes.dart';
import 'home_view.dart';

class AnimationPage extends StatelessWidget {
  const AnimationPage({super.key});

  @override
  Widget build(BuildContext context) {
    return DemoScaffold(title: '动画组件', children: [
      const Section('AnimatedContainer'),
      DemoCard(title: 'AnimatedContainer', desc: '容器属性变化动画', child: const _AnimatedContainerDemo()),
      const Section('AnimatedOpacity'),
      DemoCard(title: 'AnimatedOpacity', desc: '透明度变化动画', child: const _AnimatedOpacityDemo()),
      const Section('AnimatedPadding'),
      DemoCard(title: 'AnimatedPadding', desc: '内边距变化动画', child: const _AnimatedPaddingDemo()),
      const Section('AnimatedCrossFade'),
      DemoCard(title: 'AnimatedCrossFade', desc: '两个组件之间切换动画', child: const _CrossFadeDemo()),
      const Section('AnimatedSwitcher'),
      DemoCard(title: 'AnimatedSwitcher', desc: '子组件切换动画', child: const _AnimatedSwitcherDemo()),
      const Section('AnimatedSize'),
      DemoCard(title: 'AnimatedSize', desc: '尺寸变化动画', child: const _AnimatedSizeDemo()),
      const Section('Hero 动画'),
      DemoCard(title: 'Hero', desc: '页面间共享元素动画', child: GestureDetector(
        onTap: () => Get.toNamed(AppRoutes.heroTarget),
        child: Hero(
          tag: 'hero-demo',
          child: Container(
            width: 120, height: 120,
            decoration: BoxDecoration(color: Colors.blue, borderRadius: BorderRadius.circular(12)),
            child: const Icon(Icons.star, color: Colors.white, size: 48),
          ),
        ),
      )),
      const Section('TweenAnimationBuilder'),
      DemoCard(title: 'TweenAnimationBuilder', desc: '自定义补间动画', child: const _TweenDemo()),
      const Section('CircularProgressIndicator'),
      DemoCard(title: 'AnimatedProgress', child: const _ProgressDemo()),
    ]);
  }
}

class _AnimatedContainerDemo extends StatefulWidget {
  const _AnimatedContainerDemo();
  @override
  State<_AnimatedContainerDemo> createState() => _AnimatedContainerDemoState();
}

class _AnimatedContainerDemoState extends State<_AnimatedContainerDemo> {
  bool _selected = false;

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      AnimatedContainer(
        duration: const Duration(milliseconds: 500),
        curve: Curves.easeInOut,
        width: _selected ? 200 : 100,
        height: _selected ? 100 : 60,
        decoration: BoxDecoration(
          color: _selected ? Colors.blue : Colors.red,
          borderRadius: BorderRadius.circular(_selected ? 50 : 8),
        ),
        child: const Center(child: Text('点击切换', style: TextStyle(color: Colors.white))),
      ),
      const SizedBox(height: 8),
      ElevatedButton(onPressed: () => setState(() => _selected = !_selected), child: const Text('Animate')),
    ]);
  }
}

class _AnimatedOpacityDemo extends StatefulWidget {
  const _AnimatedOpacityDemo();
  @override
  State<_AnimatedOpacityDemo> createState() => _AnimatedOpacityDemoState();
}

class _AnimatedOpacityDemoState extends State<_AnimatedOpacityDemo> {
  bool _visible = true;

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      AnimatedOpacity(opacity: _visible ? 1.0 : 0.2, duration: const Duration(milliseconds: 500), child: Container(width: 120, height: 60, color: Colors.purple, child: const Center(child: Text('Fade', style: TextStyle(color: Colors.white))))),
      const SizedBox(height: 8),
      ElevatedButton(onPressed: () => setState(() => _visible = !_visible), child: Text(_visible ? '隐藏' : '显示')),
    ]);
  }
}

class _AnimatedPaddingDemo extends StatefulWidget {
  const _AnimatedPaddingDemo();
  @override
  State<_AnimatedPaddingDemo> createState() => _AnimatedPaddingDemoState();
}

class _AnimatedPaddingDemoState extends State<_AnimatedPaddingDemo> {
  bool _expanded = false;

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      AnimatedPadding(
        duration: const Duration(milliseconds: 500),
        padding: EdgeInsets.all(_expanded ? 32 : 8),
        child: Container(color: Colors.teal[100], child: const Text('Padding 变化')),
      ),
      const SizedBox(height: 8),
      ElevatedButton(onPressed: () => setState(() => _expanded = !_expanded), child: const Text('切换 Padding')),
    ]);
  }
}

class _CrossFadeDemo extends StatefulWidget {
  const _CrossFadeDemo();
  @override
  State<_CrossFadeDemo> createState() => _CrossFadeDemoState();
}

class _CrossFadeDemoState extends State<_CrossFadeDemo> {
  bool _showFirst = true;

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      AnimatedCrossFade(
        firstChild: Container(width: 120, height: 60, color: Colors.blue, child: const Center(child: Text('First', style: TextStyle(color: Colors.white)))),
        secondChild: Container(width: 120, height: 60, color: Colors.green, child: const Center(child: Text('Second', style: TextStyle(color: Colors.white)))),
        crossFadeState: _showFirst ? CrossFadeState.showFirst : CrossFadeState.showSecond,
        duration: const Duration(milliseconds: 500),
      ),
      const SizedBox(height: 8),
      ElevatedButton(onPressed: () => setState(() => _showFirst = !_showFirst), child: const Text('切换')),
    ]);
  }
}

class _AnimatedSwitcherDemo extends StatefulWidget {
  const _AnimatedSwitcherDemo();
  @override
  State<_AnimatedSwitcherDemo> createState() => _AnimatedSwitcherDemoState();
}

class _AnimatedSwitcherDemoState extends State<_AnimatedSwitcherDemo> {
  int _count = 0;

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      AnimatedSwitcher(duration: const Duration(milliseconds: 300), transitionBuilder: (child, anim) => ScaleTransition(scale: anim, child: child),
        child: Text('$_count', key: ValueKey<int>(_count), style: const TextStyle(fontSize: 48, fontWeight: FontWeight.bold))),
      const SizedBox(height: 8),
      ElevatedButton(onPressed: () => setState(() => _count++), child: const Text('+1')),
    ]);
  }
}

class _AnimatedSizeDemo extends StatefulWidget {
  const _AnimatedSizeDemo();
  @override
  State<_AnimatedSizeDemo> createState() => _AnimatedSizeDemoState();
}

class _AnimatedSizeDemoState extends State<_AnimatedSizeDemo> {
  bool _expanded = false;

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      AnimatedSize(duration: const Duration(milliseconds: 500), child: Container(
        width: 150,
        height: _expanded ? 120 : 60,
        color: Colors.orange[200],
        child: Center(child: Text(_expanded ? 'Expanded!' : 'Small')),
      )),
      const SizedBox(height: 8),
      ElevatedButton(onPressed: () => setState(() => _expanded = !_expanded), child: const Text('Toggle Size')),
    ]);
  }
}

class _TweenDemo extends StatefulWidget {
  const _TweenDemo();
  @override
  State<_TweenDemo> createState() => _TweenDemoState();
}

class _TweenDemoState extends State<_TweenDemo> {
  bool _animate = false;

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      TweenAnimationBuilder<double>(
        tween: Tween(begin: 0, end: _animate ? 1 : 0),
        duration: const Duration(seconds: 1),
        builder: (ctx, value, _) => LinearProgressIndicator(value: value),
      ),
      const SizedBox(height: 8),
      ElevatedButton(onPressed: () => setState(() => _animate = !_animate), child: const Text('Animate')),
    ]);
  }
}

class _ProgressDemo extends StatefulWidget {
  const _ProgressDemo();
  @override
  State<_ProgressDemo> createState() => _ProgressDemoState();
}

class _ProgressDemoState extends State<_ProgressDemo> {
  double _value = 0.5;

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      CircularProgressIndicator(value: _value),
      const SizedBox(height: 8),
      LinearProgressIndicator(value: _value),
      const SizedBox(height: 8),
      Slider(value: _value, onChanged: (v) => setState(() => _value = v)),
    ]);
  }
}

class HeroTargetPage extends StatelessWidget {
  const HeroTargetPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Hero Target')),
      body: Center(
        child: Hero(
          tag: 'hero-demo',
          child: Container(
            width: 250, height: 250,
            decoration: BoxDecoration(color: Colors.blue, borderRadius: BorderRadius.circular(24)),
            child: const Icon(Icons.star, color: Colors.white, size: 96),
          ),
        ),
      ),
    );
  }
}
