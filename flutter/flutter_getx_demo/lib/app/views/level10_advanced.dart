import 'dart:async';
import 'package:flutter/material.dart';
import 'home_view.dart';

class AdvancedPage extends StatelessWidget {
  const AdvancedPage({super.key});

  @override
  Widget build(BuildContext context) {
    return DemoScaffold(title: '高级组件', children: [
      const Section('FutureBuilder'),
      DemoCard(title: 'FutureBuilder', desc: '异步数据加载', child: FutureBuilder<String>(
        future: _mockApiCall(),
        builder: (ctx, snap) {
          if (snap.connectionState == ConnectionState.waiting) return const CircularProgressIndicator();
          if (snap.hasError) return Text('Error: ${snap.error}');
          return Column(children: [
            const Icon(Icons.check_circle, color: Colors.green, size: 48),
            const SizedBox(height: 8),
            Text(snap.data ?? ''),
          ]);
        },
      )),
      const Section('StreamBuilder'),
      DemoCard(title: 'StreamBuilder', desc: '实时数据流', child: SizedBox(
        height: 150,
        child: _StreamDemo(),
      )),
      const Section('ValueListenableBuilder'),
      DemoCard(title: 'ValueListenableBuilder', desc: '值变化监听', child: _ValueListenableDemo()),
      const Section('LayoutBuilder'),
      DemoCard(title: 'LayoutBuilder', desc: '根据父级约束构建', child: LayoutBuilder(
        builder: (ctx, constraints) {
          final cols = constraints.maxWidth > 400 ? 4 : constraints.maxWidth > 250 ? 3 : 2;
          return Column(children: [
            Text('父级宽度: ${constraints.maxWidth.toStringAsFixed(0)}', style: const TextStyle(fontWeight: FontWeight.bold)),
            Text('列数: $cols'),
            const SizedBox(height: 8),
            GridView.count(crossAxisCount: cols, shrinkWrap: true, physics: const NeverScrollableScrollPhysics(),
              children: List.generate(cols * 2, (i) => Container(margin: const EdgeInsets.all(4), color: Colors.primaries[i % Colors.primaries.length][100], child: Center(child: Text('${i + 1}'))))),
          ]);
        },
      )),
      const Section('OrientationBuilder'),
      DemoCard(title: 'OrientationBuilder', desc: '根据方向构建', child: OrientationBuilder(
        builder: (ctx, orientation) => Container(
          padding: const EdgeInsets.all(16),
          color: orientation == Orientation.portrait ? Colors.blue[100] : Colors.green[100],
          child: Text('当前方向: ${orientation == Orientation.portrait ? "竖屏" : "横屏"}'),
        ),
      )),
      const Section('NotificationListener'),
      DemoCard(title: 'NotificationListener', desc: '监听滚动通知', child: SizedBox(
        height: 120,
        child: _ScrollNotificationDemo(),
      )),
      const Section('CustomPaint'),
      DemoCard(title: 'CustomPaint', desc: '自定义绘制', child: CustomPaint(size: const Size(double.infinity, 120), painter: _CirclePainter())),
      const Section('InheritedWidget'),
      DemoCard(title: 'InheritedWidget', desc: '数据向下传递', child: _InheritedDemo()),
      const Section('Builder'),
      DemoCard(title: 'Builder', desc: '获取 BuildContext', child: Builder(
        builder: (ctx) => Text('当前主题: ${Theme.of(ctx).brightness == Brightness.dark ? "深色" : "浅色"}', style: const TextStyle(fontWeight: FontWeight.bold)),
      )),
      const Section('RepaintBoundary'),
      DemoCard(title: 'RepaintBoundary', desc: '隔离重绘区域，提升性能', child: Container(
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(border: Border.all(color: Colors.grey), borderRadius: BorderRadius.circular(8)),
        child: const Text('RepaintBoundary 包裹频繁更新的组件可避免全局重绘'),
      )),
      const Section('AutomaticKeepAlive'),
      DemoCard(title: 'AutomaticKeepAliveClientMixin', desc: '保持列表项状态 (见 TabBarView 中使用)', child: const Text('在 StatefulWidget 的 State 中 with AutomaticKeepAliveClientMixin 并设置 wantKeepAlive = true', style: TextStyle(fontSize: 12))),
    ]);
  }

  Future<String> _mockApiCall() async {
    await Future.delayed(const Duration(seconds: 2));
    return '数据加载成功! (模拟 2s 延迟)';
  }
}

class _StreamDemo extends StatefulWidget {
  @override
  State<_StreamDemo> createState() => _StreamDemoState();
}

class _StreamDemoState extends State<_StreamDemo> {
  late StreamController<int> _controller;
  int _counter = 0;

  @override
  void initState() {
    super.initState();
    _controller = StreamController<int>();
  }

  @override
  void dispose() {
    _controller.close();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      StreamBuilder<int>(
        stream: _controller.stream,
        initialData: 0,
        builder: (ctx, snap) => Text('Stream 值: ${snap.data}', style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
      ),
      const SizedBox(height: 8),
      ElevatedButton(onPressed: () { _counter++; _controller.sink.add(_counter); }, child: const Text('发送数据')),
    ]);
  }
}

class _ValueListenableDemo extends StatefulWidget {
  @override
  State<_ValueListenableDemo> createState() => _ValueListenableDemoState();
}

class _ValueListenableDemoState extends State<_ValueListenableDemo> {
  final _notifier = ValueNotifier<int>(0);

  @override
  void dispose() {
    _notifier.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      ValueListenableBuilder<int>(
        valueListenable: _notifier,
        builder: (ctx, value, _) => Text('Value: $value', style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
      ),
      const SizedBox(height: 8),
      ElevatedButton(onPressed: () => _notifier.value++, child: const Text('+1')),
    ]);
  }
}

class _ScrollNotificationDemo extends StatefulWidget {
  @override
  State<_ScrollNotificationDemo> createState() => _ScrollNotificationDemoState();
}

class _ScrollNotificationDemoState extends State<_ScrollNotificationDemo> {
  String _status = '等待滚动...';

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      Text(_status, style: const TextStyle(fontWeight: FontWeight.bold)),
      const SizedBox(height: 8),
      Expanded(child: NotificationListener<ScrollNotification>(
        onNotification: (notification) {
          setState(() {
            if (notification is ScrollStartNotification) _status = '滚动开始';
            else if (notification is ScrollUpdateNotification) _status = '滚动中: ${notification.metrics.pixels.toStringAsFixed(0)}px';
            else if (notification is ScrollEndNotification) _status = '滚动结束';
          });
          return true;
        },
        child: ListView.builder(itemCount: 30, itemBuilder: (ctx, i) => ListTile(title: Text('Item ${i + 1}'))),
      )),
    ]);
  }
}

class _CirclePainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final center = Offset(size.width / 2, size.height / 2);
    final colors = [Colors.red, Colors.orange, Colors.yellow, Colors.green, Colors.blue, Colors.purple];
    for (var i = 0; i < colors.length; i++) {
      canvas.drawCircle(Offset(center.dx + (i - 2.5) * 30, center.dy), 18, Paint()..color = colors[i]);
    }
    canvas.drawLine(Offset(20, size.height - 5), Offset(size.width - 20, size.height - 5), Paint()..color = Colors.grey..strokeWidth = 2);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}

class _InheritedDemo extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return _InheritedData(
      data: '从上层传递的数据',
      child: Builder(builder: (ctx) {
        final data = ctx.dependOnInheritedWidgetOfExactType<_InheritedData>()?.data ?? '无数据';
        return Text('获取到: $data');
      }),
    );
  }
}

class _InheritedData extends InheritedWidget {
  final String data;
  const _InheritedData({required this.data, required super.child});

  @override
  bool updateShouldNotify(covariant _InheritedData oldWidget) => data != oldWidget.data;
}
