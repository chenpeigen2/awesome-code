import 'package:flutter/material.dart';
import '../../widgets/example_card.dart';

/// 交互 Widget 示例页面
class InteractivePage extends StatelessWidget {
  const InteractivePage({super.key});

  @override
  Widget build(BuildContext context) {
    return ListView(
      children: [
        const ExampleSectionHeader(title: '手势检测', icon: Icons.touch_app),

        // GestureDetector
        ExampleCard(
          title: 'GestureDetector',
          description: '手势检测器',
          child: _GestureDetectorDemo(),
          code: 'GestureDetector(onTap: () {}, onDoubleTap: () {})',
        ),

        // InkWell
        ExampleCard(
          title: 'InkWell',
          description: '带水波纹效果的点击区域',
          child: Column(
            children: [
              InkWell(
                onTap: () => _showSnackBar(context, '点击了 InkWell'),
                borderRadius: BorderRadius.circular(8),
                child: Container(
                  width: double.infinity,
                  padding: const EdgeInsets.all(16),
                  decoration: BoxDecoration(
                    border: Border.all(color: Colors.grey),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: const Center(child: Text('点击查看水波纹效果')),
                ),
              ),
              const SizedBox(height: 8),
              Row(
                children: [
                  Expanded(
                    child: InkWell(
                      onTap: () => _showSnackBar(context, '点击 1'),
                      child: Container(
                        padding: const EdgeInsets.all(12),
                        color: Colors.blue.withOpacity(0.1),
                        child: const Center(child: Text('区域 1')),
                      ),
                    ),
                  ),
                  Expanded(
                    child: InkWell(
                      onTap: () => _showSnackBar(context, '点击 2'),
                      child: Container(
                        padding: const EdgeInsets.all(12),
                        color: Colors.green.withOpacity(0.1),
                        child: const Center(child: Text('区域 2')),
                      ),
                    ),
                  ),
                ],
              ),
            ],
          ),
          code: 'InkWell(onTap: () {}, child: Container())',
        ),

        // InkResponse
        ExampleCard(
          title: 'InkResponse',
          description: '可自定义的水波纹效果',
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            children: [
              InkResponse(
                onTap: () => _showSnackBar(context, '圆形波纹'),
                radius: 30,
                child: Container(
                  width: 60,
                  height: 60,
                  decoration: BoxDecoration(
                    shape: BoxShape.circle,
                    color: Colors.blue.withOpacity(0.2),
                  ),
                  child: const Icon(Icons.circle_outlined),
                ),
              ),
              InkResponse(
                onTap: () => _showSnackBar(context, '矩形波纹'),
                containedInkWell: true,
                highlightShape: BoxShape.rectangle,
                borderRadius: BorderRadius.circular(8),
                child: Container(
                  width: 80,
                  height: 60,
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(8),
                    color: Colors.green.withOpacity(0.2),
                  ),
                  child: const Icon(Icons.square_outlined),
                ),
              ),
            ],
          ),
          code: 'InkResponse(onTap: () {}, radius: 30)',
        ),

        const ExampleSectionHeader(title: '拖拽交互', icon: Icons.drag_indicator),

        // Draggable
        ExampleCard(
          title: 'Draggable',
          description: '可拖拽组件',
          child: _DraggableDemo(),
          code: 'Draggable(data: data, feedback: Widget, child: Widget)',
        ),

        // LongPressDraggable
        ExampleCard(
          title: 'LongPressDraggable',
          description: '长按后可拖拽',
          child: _LongPressDraggableDemo(),
          code: 'LongPressDraggable(data: data, child: Widget)',
        ),

        // DragTarget
        ExampleCard(
          title: 'DragTarget',
          description: '拖拽目标区域',
          child: _DragTargetDemo(),
          code: 'DragTarget(onWillAccept: ..., onAccept: ...)',
        ),

        // Dismissible
        ExampleCard(
          title: 'Dismissible',
          description: '可滑出删除的列表项',
          child: _DismissibleDemo(),
          code: 'Dismissible(key: Key, onDismissed: ...)',
        ),

        const ExampleSectionHeader(title: '滑动选择', icon: Icons.swipe),

        // Swipe actions
        ExampleCard(
          title: '滑动操作',
          description: '列表项滑动显示操作按钮',
          child: SizedBox(
            height: 200,
            child: ListView(
              children: List.generate(4, (index) {
                return Dismissible(
                  key: ValueKey(index),
                  direction: DismissDirection.endToStart,
                  background: Container(
                    color: Colors.red,
                    alignment: Alignment.centerRight,
                    padding: const EdgeInsets.only(right: 20),
                    child: const Icon(Icons.delete, color: Colors.white),
                  ),
                  secondaryBackground: Container(
                    color: Colors.blue,
                    alignment: Alignment.centerRight,
                    padding: const EdgeInsets.only(right: 20),
                    child: const Icon(Icons.archive, color: Colors.white),
                  ),
                  confirmDismiss: (direction) async {
                    _showSnackBar(context, '滑动方向: $direction');
                    return false;
                  },
                  child: ListTile(
                    title: Text('列表项 ${index + 1}'),
                    subtitle: const Text('向左滑动查看操作'),
                  ),
                );
              }),
            ),
          ),
          code: 'Dismissible(background: Container(), secondaryBackground: ...)',
        ),

        const ExampleSectionHeader(title: '缩放旋转', icon: Icons.pinch),

        // InteractiveViewer
        ExampleCard(
          title: 'InteractiveViewer',
          description: '可缩放、平移的视图',
          child: SizedBox(
            height: 180,
            child: ClipRect(
              child: InteractiveViewer(
                boundaryMargin: const EdgeInsets.all(20),
                minScale: 0.5,
                maxScale: 4,
                child: Container(
                  width: 200,
                  height: 200,
                  decoration: const BoxDecoration(
                    gradient: LinearGradient(
                      colors: [Colors.red, Colors.blue],
                    ),
                  ),
                  child: const Center(
                    child: Text(
                      '双指缩放\n单指平移',
                      textAlign: TextAlign.center,
                      style: TextStyle(color: Colors.white, fontSize: 16),
                    ),
                  ),
                ),
              ),
            ),
          ),
          code: 'InteractiveViewer(minScale: 0.5, maxScale: 4, child: Widget)',
        ),

        // GestureDetector 缩放旋转
        ExampleCard(
          title: 'GestureDetector 缩放',
          description: '自定义手势缩放',
          child: _ScaleGestureDemo(),
          code: 'GestureDetector(onScaleStart:, onScaleUpdate:)',
        ),

        const ExampleSectionHeader(title: '其他交互', icon: Icons.more_horiz),

        // IgnorePointer
        ExampleCard(
          title: 'IgnorePointer',
          description: '忽略指针事件',
          child: Row(
            children: [
              Expanded(
                child: Container(
                  height: 60,
                  color: Colors.blue.withOpacity(0.2),
                  child: IgnorePointer(
                    ignoring: true,
                    child: ElevatedButton(
                      onPressed: () => _showSnackBar(context, '不会触发'),
                      child: const Text('忽略点击'),
                    ),
                  ),
                ),
              ),
              const SizedBox(width: 8),
              Expanded(
                child: Container(
                  height: 60,
                  color: Colors.green.withOpacity(0.2),
                  child: IgnorePointer(
                    ignoring: false,
                    child: ElevatedButton(
                      onPressed: () => _showSnackBar(context, '可以点击'),
                      child: const Text('可点击'),
                    ),
                  ),
                ),
              ),
            ],
          ),
          code: 'IgnorePointer(ignoring: true, child: Widget)',
        ),

        // AbsorbPointer
        ExampleCard(
          title: 'AbsorbPointer',
          description: '吸收指针事件',
          child: Stack(
            children: [
              Row(
                children: [
                  Expanded(
                    child: ElevatedButton(
                      onPressed: () => _showSnackBar(context, '底部按钮'),
                      child: const Text('底部按钮'),
                    ),
                  ),
                  const SizedBox(width: 8),
                  Expanded(
                    child: ElevatedButton(
                      onPressed: () => _showSnackBar(context, '底部按钮'),
                      child: const Text('底部按钮'),
                    ),
                  ),
                ],
              ),
              Positioned(
                left: 0,
                right: 0,
                top: 0,
                bottom: 0,
                child: AbsorbPointer(
                  absorbing: true,
                  child: Container(
                    color: Colors.transparent,
                  ),
                ),
              ),
            ],
          ),
          code: 'AbsorbPointer(absorbing: true, child: Widget)',
        ),

        // Listener
        ExampleCard(
          title: 'Listener',
          description: '原始指针事件监听',
          child: _ListenerDemo(),
          code: 'Listener(onPointerDown:, onPointerMove:, onPointerUp:)',
        ),

        const SizedBox(height: 24),
      ],
    );
  }

  void _showSnackBar(BuildContext context, String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        behavior: SnackBarBehavior.floating,
        duration: const Duration(seconds: 1),
      ),
    );
  }
}

/// GestureDetector 演示
class _GestureDetectorDemo extends StatefulWidget {
  @override
  State<_GestureDetectorDemo> createState() => _GestureDetectorDemoState();
}

class _GestureDetectorDemoState extends State<_GestureDetectorDemo> {
  String _info = '尝试各种手势';
  Color _color = Colors.blue;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Container(
          padding: const EdgeInsets.all(8),
          color: Theme.of(context).colorScheme.surfaceContainerHighest,
          child: Text(_info),
        ),
        const SizedBox(height: 8),
        GestureDetector(
          onTap: () => _update('单击'),
          onDoubleTap: () => _update('双击'),
          onLongPress: () => _update('长按'),
          onScaleUpdate: (details) => _update('缩放: ${details.scale.toStringAsFixed(2)}'),
          child: Container(
            width: double.infinity,
            height: 100,
            decoration: BoxDecoration(
              color: _color,
              borderRadius: BorderRadius.circular(8),
            ),
            child: const Center(
              child: Text(
                '手势区域',
                style: TextStyle(color: Colors.white, fontSize: 18),
              ),
            ),
          ),
        ),
      ],
    );
  }

  void _update(String action) {
    setState(() {
      _info = action;
      _color = Colors.primaries[DateTime.now().millisecond % Colors.primaries.length];
    });
  }
}

/// Draggable 演示
class _DraggableDemo extends StatefulWidget {
  @override
  State<_DraggableDemo> createState() => _DraggableDemoState();
}

class _DraggableDemoState extends State<_DraggableDemo> {
  Offset _offset = Offset.zero;

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        Container(
          height: 150,
          color: Theme.of(context).colorScheme.surfaceContainerHighest,
          child: Center(
            child: Text('拖拽下方方块到此区域\n位置: ${_offset.dx.toStringAsFixed(0)}, ${_offset.dy.toStringAsFixed(0)}'),
          ),
        ),
        Positioned(
          left: 20,
          bottom: 10,
          child: Draggable<String>(
            data: '拖拽数据',
            feedback: Container(
              width: 60,
              height: 60,
              color: Colors.blue.withOpacity(0.7),
              child: const Center(child: Text('拖拽中')),
            ),
            childWhenDragging: Opacity(
              opacity: 0.5,
              child: _buildDraggableChild(),
            ),
            onDraggableCanceled: (velocity, offset) {
              setState(() => _offset = offset);
            },
            child: _buildDraggableChild(),
          ),
        ),
      ],
    );
  }

  Widget _buildDraggableChild() {
    return Container(
      width: 60,
      height: 60,
      color: Colors.blue,
      child: const Center(child: Text('拖我', style: TextStyle(color: Colors.white))),
    );
  }
}

/// LongPressDraggable 演示
class _LongPressDraggableDemo extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Container(
      height: 80,
      color: Theme.of(context).colorScheme.surfaceContainerHighest,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: [
          LongPressDraggable<int>(
            data: 1,
            feedback: Container(
              padding: const EdgeInsets.all(16),
              color: Colors.red.withOpacity(0.8),
              child: const Text('拖拽中'),
            ),
            child: Container(
              padding: const EdgeInsets.all(16),
              color: Colors.red,
              child: const Text('长按拖拽', style: TextStyle(color: Colors.white)),
            ),
          ),
          LongPressDraggable<int>(
            data: 2,
            feedback: Container(
              padding: const EdgeInsets.all(16),
              color: Colors.green.withOpacity(0.8),
              child: const Text('拖拽中'),
            ),
            child: Container(
              padding: const EdgeInsets.all(16),
              color: Colors.green,
              child: const Text('长按拖拽', style: TextStyle(color: Colors.white)),
            ),
          ),
        ],
      ),
    );
  }
}

/// DragTarget 演示
class _DragTargetDemo extends StatefulWidget {
  @override
  State<_DragTargetDemo> createState() => _DragTargetDemoState();
}

class _DragTargetDemoState extends State<_DragTargetDemo> {
  String _targetText = '放置到这里';
  Color _targetColor = Colors.grey;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          children: [
            Draggable<String>(
              data: '红色',
              feedback: Container(width: 50, height: 50, color: Colors.red),
              child: Container(width: 50, height: 50, color: Colors.red),
            ),
            Draggable<String>(
              data: '绿色',
              feedback: Container(width: 50, height: 50, color: Colors.green),
              child: Container(width: 50, height: 50, color: Colors.green),
            ),
            Draggable<String>(
              data: '蓝色',
              feedback: Container(width: 50, height: 50, color: Colors.blue),
              child: Container(width: 50, height: 50, color: Colors.blue),
            ),
          ],
        ),
        const SizedBox(height: 12),
        DragTarget<String>(
          onWillAcceptWithDetails: (details) => true,
          onAcceptWithDetails: (details) {
            setState(() {
              _targetText = details.data;
              _targetColor = details.data == '红色'
                  ? Colors.red
                  : details.data == '绿色'
                      ? Colors.green
                      : Colors.blue;
            });
          },
          builder: (context, candidateData, rejectedData) {
            return Container(
              width: double.infinity,
              height: 60,
              decoration: BoxDecoration(
                color: _targetColor,
                borderRadius: BorderRadius.circular(8),
                border: Border.all(
                  color: candidateData.isNotEmpty ? Colors.amber : Colors.transparent,
                  width: 3,
                ),
              ),
              child: Center(
                child: Text(
                  _targetText,
                  style: const TextStyle(color: Colors.white, fontSize: 16),
                ),
              ),
            );
          },
        ),
      ],
    );
  }
}

/// Dismissible 演示
class _DismissibleDemo extends StatefulWidget {
  @override
  State<_DismissibleDemo> createState() => _DismissibleDemoState();
}

class _DismissibleDemoState extends State<_DismissibleDemo> {
  final List<String> _items = List.generate(5, (i) => '项目 ${i + 1}');

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 200,
      child: ListView.builder(
        itemCount: _items.length,
        itemBuilder: (context, index) {
          return Dismissible(
            key: Key(_items[index]),
            onDismissed: (direction) {
              setState(() {
                _items.removeAt(index);
              });
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text('已删除 ${_items[index]}')),
              );
            },
            background: Container(
              color: Colors.red,
              child: const Row(
                mainAxisAlignment: MainAxisAlignment.start,
                children: [
                  SizedBox(width: 20),
                  Icon(Icons.delete, color: Colors.white),
                ],
              ),
            ),
            secondaryBackground: Container(
              color: Colors.blue,
              child: const Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  Icon(Icons.archive, color: Colors.white),
                  SizedBox(width: 20),
                ],
              ),
            ),
            child: ListTile(
              title: Text(_items[index]),
              subtitle: const Text('向左或向右滑动删除'),
            ),
          );
        },
      ),
    );
  }
}

/// 缩放手势演示
class _ScaleGestureDemo extends StatefulWidget {
  @override
  State<_ScaleGestureDemo> createState() => _ScaleGestureDemoState();
}

class _ScaleGestureDemoState extends State<_ScaleGestureDemo> {
  double _scale = 1.0;
  double _rotation = 0.0;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onScaleUpdate: (details) {
        setState(() {
          _scale = details.scale.clamp(0.5, 3.0);
          _rotation = details.rotation;
        });
      },
      onScaleEnd: (details) {
        setState(() {
          _scale = 1.0;
          _rotation = 0.0;
        });
      },
      child: Container(
        height: 120,
        color: Theme.of(context).colorScheme.surfaceContainerHighest,
        child: Center(
          child: Transform.scale(
            scale: _scale,
            child: Transform.rotate(
              angle: _rotation,
              child: Container(
                width: 80,
                height: 80,
                color: Colors.teal,
                child: const Center(
                  child: Text('缩放旋转', style: TextStyle(color: Colors.white)),
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}

/// Listener 演示
class _ListenerDemo extends StatefulWidget {
  @override
  State<_ListenerDemo> createState() => _ListenerDemoState();
}

class _ListenerDemoState extends State<_ListenerDemo> {
  String _info = '触摸此区域';
  Color _color = Colors.grey;

  @override
  Widget build(BuildContext context) {
    return Listener(
      onPointerDown: (_) => setState(() {
        _info = 'PointerDown (按下)';
        _color = Colors.green;
      }),
      onPointerMove: (_) => setState(() {
        _info = 'PointerMove (移动)';
      }),
      onPointerUp: (_) => setState(() {
        _info = 'PointerUp (抬起)';
        _color = Colors.blue;
      }),
      onPointerCancel: (_) => setState(() {
        _info = 'PointerCancel (取消)';
        _color = Colors.red;
      }),
      child: Container(
        width: double.infinity,
        height: 80,
        decoration: BoxDecoration(
          color: _color,
          borderRadius: BorderRadius.circular(8),
        ),
        child: Center(
          child: Text(_info, style: const TextStyle(color: Colors.white)),
        ),
      ),
    );
  }
}
