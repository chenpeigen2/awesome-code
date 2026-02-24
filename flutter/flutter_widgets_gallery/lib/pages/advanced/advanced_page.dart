import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:flutter/gestures.dart';
import '../../widgets/example_card.dart';

/// 高级 Widget 示例页面
class AdvancedPage extends StatelessWidget {
  const AdvancedPage({super.key});

  @override
  Widget build(BuildContext context) {
    return ListView(
      children: [
        const ExampleSectionHeader(title: '数据表格', icon: Icons.table_chart),

        // DataTable
        ExampleCard(
          title: 'DataTable',
          description: '数据表格',
          child: _DataTableDemo(),
          code: 'DataTable(columns: [...], rows: [...])',
        ),

        // PaginatedDataTable
        ExampleCard(
          title: 'PaginatedDataTable',
          description: '分页数据表格',
          child: _PaginatedDataTableDemo(),
          code: 'PaginatedDataTable(columns: [...], source: DataTableSource)',
        ),

        const ExampleSectionHeader(title: '展开收起', icon: Icons.unfold_more),

        // ExpansionTile
        ExampleCard(
          title: 'ExpansionTile',
          description: '可展开的列表项',
          child: _ExpansionTileDemo(),
          code: 'ExpansionTile(title: Text(), children: [...])',
        ),

        // ExpansionPanelList
        ExampleCard(
          title: 'ExpansionPanelList.radio',
          description: '单选展开面板',
          child: _ExpansionPanelRadioDemo(),
          code: 'ExpansionPanelList.radio(children: [ExpansionPanelRadio()...])',
        ),

        const ExampleSectionHeader(title: '选择器', icon: Icons.select_all),

        // SelectableText
        ExampleCard(
          title: 'SelectableText',
          description: '可选择文本',
          child: const SelectableText(
            '这段文本可以被选择和复制。长按或双击文本试试！\n\nFlutter 是 Google 开源的移动 UI 框架，可以快速在 iOS 和 Android 上构建高质量的原生用户界面。',
            style: TextStyle(fontSize: 14),
          ),
          code: 'SelectableText("可选文本")',
        ),

        // SelectionArea
        ExampleCard(
          title: 'SelectionArea',
          description: '选择区域',
          child: SelectionArea(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text('区域内的所有文本都可以被选择'),
                const SizedBox(height: 8),
                const Text('包括多个 Text 组件'),
                const SizedBox(height: 8),
                Container(
                  padding: const EdgeInsets.all(8),
                  color: Theme.of(context).colorScheme.surfaceContainerHighest,
                  child: const Text('容器内的文本也可以选择'),
                ),
              ],
            ),
          ),
          code: 'SelectionArea(child: Column(children: [Text()...]))',
        ),

        const ExampleSectionHeader(title: '状态管理', icon: Icons.account_tree),

        // InheritedWidget
        ExampleCard(
          title: 'InheritedWidget',
          description: '数据向下传递',
          child: _InheritedWidgetDemo(),
          code: 'InheritedWidget + InheritedWidget.of(context)',
        ),

        // ValueListenableBuilder
        ExampleCard(
          title: 'ValueListenableBuilder',
          description: '值监听构建器',
          child: _ValueListenableBuilderDemo(),
          code: 'ValueListenableBuilder(valueListenable: notifier, builder: ...)',
        ),

        // ListenableBuilder
        ExampleCard(
          title: 'ListenableBuilder',
          description: '监听器构建器',
          child: _ListenableBuilderDemo(),
          code: 'ListenableBuilder(listenable: animation, builder: ...)',
        ),

        // AnimatedBuilder
        ExampleCard(
          title: 'AnimatedBuilder',
          description: '动画构建器',
          child: _AnimatedBuilderDemo(),
          code: 'AnimatedBuilder(animation: controller, builder: ...)',
        ),

        // StreamBuilder
        ExampleCard(
          title: 'StreamBuilder',
          description: '流构建器',
          child: _StreamBuilderDemo(),
          code: 'StreamBuilder(stream: stream, builder: ...)',
        ),

        // FutureBuilder
        ExampleCard(
          title: 'FutureBuilder',
          description: '异步构建器',
          child: _FutureBuilderDemo(),
          code: 'FutureBuilder(future: future, builder: ...)',
        ),

        const ExampleSectionHeader(title: '布局限制', icon: Icons.aspect_ratio),

        // LayoutBuilder
        ExampleCard(
          title: 'LayoutBuilder',
          description: '布局构建器',
          child: _LayoutBuilderDemo(),
          code: 'LayoutBuilder(builder: (ctx, constraints) => ...)',
        ),

        // Builder
        ExampleCard(
          title: 'Builder',
          description: '构建器',
          child: Builder(
            builder: (context) {
              return Text(
                '通过 Builder 获取 context\nTheme: ${Theme.of(context).colorScheme.primary}',
                style: const TextStyle(fontSize: 12),
              );
            },
          ),
          code: 'Builder(builder: (ctx) => Widget)',
        ),

        // MediaQuery
        ExampleCard(
          title: 'MediaQuery',
          description: '媒体查询',
          child: Builder(
            builder: (context) {
              final media = MediaQuery.of(context);
              return Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text('屏幕尺寸: ${media.size.width.toStringAsFixed(0)} x ${media.size.height.toStringAsFixed(0)}'),
                  Text('设备像素比: ${media.devicePixelRatio.toStringAsFixed(2)}'),
                  Text('方向: ${media.orientation}'),
                  Text('状态栏高度: ${media.padding.top.toStringAsFixed(0)}'),
                  Text('底部安全区: ${media.padding.bottom.toStringAsFixed(0)}'),
                ],
              );
            },
          ),
          code: 'MediaQuery.of(context).size',
        ),

        const ExampleSectionHeader(title: '其他高级', icon: Icons.extension),

        // RefreshIndicator
        ExampleCard(
          title: 'RefreshIndicator',
          description: '下拉刷新',
          child: _RefreshIndicatorDemo(),
          code: 'RefreshIndicator(onRefresh: () async {}, child: ListView())',
        ),

        // RepaintBoundary
        ExampleCard(
          title: 'RepaintBoundary',
          description: '重绘边界，优化性能',
          child: Column(
            children: [
              const Text('RepaintBoundary 创建独立的重绘层'),
              const SizedBox(height: 8),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceAround,
                children: [
                  RepaintBoundary(
                    child: Container(
                      width: 60,
                      height: 60,
                      color: Colors.red,
                      child: const Center(child: Text('独立')),
                    ),
                  ),
                  RepaintBoundary(
                    child: Container(
                      width: 60,
                      height: 60,
                      color: Colors.green,
                      child: const Center(child: Text('独立')),
                    ),
                  ),
                ],
              ),
            ],
          ),
          code: 'RepaintBoundary(child: Widget)',
        ),

        // AutomaticKeepAliveClientMixin
        ExampleCard(
          title: 'KeepAlive',
          description: '保持页面状态',
          child: _KeepAliveDemo(),
          code: 'AutomaticKeepAliveClientMixin',
        ),

        // IndexedStack (TabBarView 替代)
        ExampleCard(
          title: 'IndexedStack 保持状态',
          description: '切换时保持页面状态',
          child: _IndexedStackKeepAliveDemo(),
          code: 'IndexedStack(children: [...])',
        ),

        // WillPopScope / PopScope
        ExampleCard(
          title: 'PopScope',
          description: '拦截返回操作',
          child: _PopScopeDemo(),
          code: 'PopScope(canPop: false, onPopInvoked: ...)',
        ),

        // RawGestureDetector
        ExampleCard(
          title: 'RawGestureDetector',
          description: '原始手势检测',
          child: _RawGestureDetectorDemo(),
          code: 'RawGestureDetector(gestures: {...})',
        ),

        const ExampleSectionHeader(title: '安全区域与主题', icon: Icons.security),

        // SafeArea
        ExampleCard(
          title: 'SafeArea',
          description: '安全区域（避开刘海、状态栏）',
          child: _SafeAreaDemo(),
          code: 'SafeArea(child: Widget)',
        ),

        // Theme
        ExampleCard(
          title: 'Theme',
          description: '主题配置',
          child: _ThemeDemo(),
          code: 'Theme(data: ThemeData(...), child: Widget)',
        ),

        // DefaultTextStyle
        ExampleCard(
          title: 'DefaultTextStyle',
          description: '默认文本样式',
          child: _DefaultTextStyleDemo(),
          code: 'DefaultTextStyle(style: TextStyle(...), child: Widget)',
        ),

        const ExampleSectionHeader(title: '视觉效果', icon: Icons.palette),

        // BackdropFilter
        ExampleCard(
          title: 'BackdropFilter',
          description: '背景滤镜（毛玻璃效果）',
          child: _BackdropFilterDemo(),
          code: 'BackdropFilter(filter: ImageFilter.blur(), child: Widget)',
        ),

        // ShaderMask
        ExampleCard(
          title: 'ShaderMask',
          description: '着色器遮罩（渐变效果）',
          child: _ShaderMaskDemo(),
          code: 'ShaderMask(shaderCallback: (bounds) => Shader, child: Widget)',
        ),

        // Directionality
        ExampleCard(
          title: 'Directionality',
          description: '文本方向（RTL/LTR）',
          child: _DirectionalityDemo(),
          code: 'Directionality(textDirection: TextDirection.rtl, child: Widget)',
        ),

        const SizedBox(height: 24),
      ],
    );
  }
}

/// DataTable 演示
class _DataTableDemo extends StatelessWidget {
  final List<Map<String, String>> _data = [
    {'name': '张三', 'age': '25', 'city': '北京'},
    {'name': '李四', 'age': '30', 'city': '上海'},
    {'name': '王五', 'age': '28', 'city': '广州'},
  ];

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      scrollDirection: Axis.horizontal,
      child: DataTable(
        columns: const [
          DataColumn(label: Text('姓名')),
          DataColumn(label: Text('年龄'), numeric: true),
          DataColumn(label: Text('城市')),
        ],
        rows: _data.map((item) {
          return DataRow(
            cells: [
              DataCell(Text(item['name']!)),
              DataCell(Text(item['age']!)),
              DataCell(Text(item['city']!)),
            ],
          );
        }).toList(),
      ),
    );
  }
}

/// PaginatedDataTable 演示
class _PaginatedDataTableDemo extends StatefulWidget {
  @override
  State<_PaginatedDataTableDemo> createState() => _PaginatedDataTableDemoState();
}

class _PaginatedDataTableDemoState extends State<_PaginatedDataTableDemo> {
  late final _TableDataSource _dataSource;

  @override
  void initState() {
    super.initState();
    _dataSource = _TableDataSource();
  }

  @override
  void dispose() {
    _dataSource.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return PaginatedDataTable(
      header: const Text('用户数据'),
      rowsPerPage: 5,
      columns: const [
        DataColumn(label: Text('ID')),
        DataColumn(label: Text('姓名')),
        DataColumn(label: Text('年龄')),
      ],
      source: _dataSource,
    );
  }
}

class _TableDataSource extends DataTableSource {
  final List<Map<String, dynamic>> _data = List.generate(
    20,
    (i) => {'id': i + 1, 'name': '用户${i + 1}', 'age': 20 + i % 30},
  );

  @override
  DataRow getRow(int index) {
    final item = _data[index];
    return DataRow(cells: [
      DataCell(Text('${item['id']}')),
      DataCell(Text(item['name'])),
      DataCell(Text('${item['age']}')),
    ]);
  }

  @override
  bool get isRowCountApproximate => false;

  @override
  int get rowCount => _data.length;

  @override
  int get selectedRowCount => 0;
}

/// ExpansionTile 演示
class _ExpansionTileDemo extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        ExpansionTile(
          title: const Text('展开项 1'),
          subtitle: const Text('这是第一个展开项'),
          leading: const Icon(Icons.info),
          children: [
            Container(
              padding: const EdgeInsets.all(16),
              color: Theme.of(context).colorScheme.surfaceContainerHighest,
              child: const Text('展开后显示的内容'),
            ),
          ],
        ),
        ExpansionTile(
          title: const Text('展开项 2'),
          initiallyExpanded: true,
          children: [
            Container(
              padding: const EdgeInsets.all(16),
              color: Theme.of(context).colorScheme.surfaceContainerHighest,
              child: const Text('默认展开的内容'),
            ),
          ],
        ),
      ],
    );
  }
}

/// ExpansionPanel Radio 演示
class _ExpansionPanelRadioDemo extends StatefulWidget {
  @override
  State<_ExpansionPanelRadioDemo> createState() => _ExpansionPanelRadioDemoState();
}

class _ExpansionPanelRadioDemoState extends State<_ExpansionPanelRadioDemo> {
  @override
  Widget build(BuildContext context) {
    return ExpansionPanelList.radio(
      children: [1, 2, 3].map((i) {
        return ExpansionPanelRadio(
          value: i,
          headerBuilder: (context, isExpanded) => ListTile(title: Text('选项 $i')),
          body: Container(
            padding: const EdgeInsets.all(16),
            child: Text('选项 $i 的详细内容'),
          ),
        );
      }).toList(),
    );
  }
}

/// InheritedWidget 演示
class _InheritedWidgetDemo extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return _MyInheritedWidget(
      data: '从顶层传递的数据',
      child: Builder(
        builder: (context) {
          final data = _MyInheritedWidget.of(context)?.data ?? '无数据';
          return Text('获取到的数据: $data');
        },
      ),
    );
  }
}

class _MyInheritedWidget extends InheritedWidget {
  final String data;

  const _MyInheritedWidget({
    required this.data,
    required super.child,
  });

  static _MyInheritedWidget? of(BuildContext context) {
    return context.dependOnInheritedWidgetOfExactType<_MyInheritedWidget>();
  }

  @override
  bool updateShouldNotify(covariant _MyInheritedWidget oldWidget) {
    return data != oldWidget.data;
  }
}

/// ValueListenableBuilder 演示
class _ValueListenableBuilderDemo extends StatefulWidget {
  @override
  State<_ValueListenableBuilderDemo> createState() => _ValueListenableBuilderDemoState();
}

class _ValueListenableBuilderDemoState extends State<_ValueListenableBuilderDemo> {
  final ValueNotifier<int> _counter = ValueNotifier(0);

  @override
  void dispose() {
    _counter.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        ValueListenableBuilder<int>(
          valueListenable: _counter,
          builder: (context, value, child) {
            return Text('计数: $value', style: const TextStyle(fontSize: 24));
          },
        ),
        const SizedBox(height: 8),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            IconButton(
              icon: const Icon(Icons.remove),
              onPressed: () => _counter.value--,
            ),
            IconButton(
              icon: const Icon(Icons.add),
              onPressed: () => _counter.value++,
            ),
          ],
        ),
      ],
    );
  }
}

/// ListenableBuilder 演示
class _ListenableBuilderDemo extends StatefulWidget {
  @override
  State<_ListenableBuilderDemo> createState() => _ListenableBuilderDemoState();
}

class _ListenableBuilderDemoState extends State<_ListenableBuilderDemo>
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
    return ListenableBuilder(
      listenable: _controller,
      builder: (context, child) {
        return LinearProgressIndicator(value: _controller.value);
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
      duration: const Duration(seconds: 1),
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
          color: Color.lerp(Colors.blue, Colors.red, _controller.value),
          child: Center(
            child: Text('颜色动画: ${(_controller.value * 100).toStringAsFixed(0)}%'),
          ),
        );
      },
    );
  }
}

/// StreamBuilder 演示
class _StreamBuilderDemo extends StatefulWidget {
  @override
  State<_StreamBuilderDemo> createState() => _StreamBuilderDemoState();
}

class _StreamBuilderDemoState extends State<_StreamBuilderDemo> {
  late Stream<int> _stream;

  @override
  void initState() {
    super.initState();
    _stream = Stream.periodic(const Duration(seconds: 1), (x) => x);
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        StreamBuilder<int>(
          stream: _stream,
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              return Text('流数据: ${snapshot.data}');
            }
            return const Text('等待数据...');
          },
        ),
      ],
    );
  }
}

/// FutureBuilder 演示
class _FutureBuilderDemo extends StatefulWidget {
  @override
  State<_FutureBuilderDemo> createState() => _FutureBuilderDemoState();
}

class _FutureBuilderDemoState extends State<_FutureBuilderDemo> {
  late Future<String> _future;

  Future<String> _fetchData() async {
    await Future.delayed(const Duration(seconds: 2));
    return '数据加载完成!';
  }

  @override
  void initState() {
    super.initState();
    _future = _fetchData();
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<String>(
      future: _future,
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Center(child: CircularProgressIndicator());
        } else if (snapshot.hasError) {
          return Text('错误: ${snapshot.error}');
        } else {
          return Text('结果: ${snapshot.data}');
        }
      },
    );
  }
}

/// LayoutBuilder 演示
class _LayoutBuilderDemo extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (context, constraints) {
        if (constraints.maxWidth > 400) {
          return Row(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            children: [
              Container(width: 100, height: 60, color: Colors.red),
              Container(width: 100, height: 60, color: Colors.green),
            ],
          );
        } else {
          return Column(
            children: [
              Container(width: 100, height: 60, color: Colors.red),
              const SizedBox(height: 8),
              Container(width: 100, height: 60, color: Colors.green),
            ],
          );
        }
      },
    );
  }
}

/// RefreshIndicator 演示
class _RefreshIndicatorDemo extends StatefulWidget {
  @override
  State<_RefreshIndicatorDemo> createState() => _RefreshIndicatorDemoState();
}

class _RefreshIndicatorDemoState extends State<_RefreshIndicatorDemo> {
  final List<String> _items = List.generate(10, (i) => '项目 ${i + 1}');

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 150,
      child: RefreshIndicator(
        onRefresh: () async {
          await Future.delayed(const Duration(seconds: 1));
          setState(() {
            _items.insert(0, '新项目 ${DateTime.now().second}');
          });
        },
        child: ListView.builder(
          itemCount: _items.length,
          itemBuilder: (context, index) {
            return ListTile(
              dense: true,
              title: Text(_items[index]),
            );
          },
        ),
      ),
    );
  }
}

/// KeepAlive 演示
class _KeepAliveDemo extends StatefulWidget {
  @override
  State<_KeepAliveDemo> createState() => _KeepAliveDemoState();
}

class _KeepAliveDemoState extends State<_KeepAliveDemo>
    with AutomaticKeepAliveClientMixin {
  int _counter = 0;

  @override
  bool get wantKeepAlive => true;

  @override
  Widget build(BuildContext context) {
    super.build(context);
    return Row(
      children: [
        Text('计数: $_counter'),
        const SizedBox(width: 16),
        ElevatedButton(
          onPressed: () => setState(() => _counter++),
          child: const Text('增加'),
        ),
      ],
    );
  }
}

/// IndexedStack KeepAlive 演示
class _IndexedStackKeepAliveDemo extends StatefulWidget {
  @override
  State<_IndexedStackKeepAliveDemo> createState() => _IndexedStackKeepAliveDemoState();
}

class _IndexedStackKeepAliveDemoState extends State<_IndexedStackKeepAliveDemo> {
  int _index = 0;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [0, 1].map((i) {
            return Padding(
              padding: const EdgeInsets.symmetric(horizontal: 8),
              child: ElevatedButton(
                onPressed: () => setState(() => _index = i),
                style: ElevatedButton.styleFrom(
                  backgroundColor: _index == i ? Theme.of(context).colorScheme.primary : null,
                ),
                child: Text('页面 ${i + 1}'),
              ),
            );
          }).toList(),
        ),
        const SizedBox(height: 8),
        SizedBox(
          height: 60,
          child: IndexedStack(
            index: _index,
            children: const [
              _KeepAliveCounter(label: '页面 1'),
              _KeepAliveCounter(label: '页面 2'),
            ],
          ),
        ),
      ],
    );
  }
}

class _KeepAliveCounter extends StatefulWidget {
  final String label;
  const _KeepAliveCounter({required this.label});

  @override
  State<_KeepAliveCounter> createState() => _KeepAliveCounterState();
}

class _KeepAliveCounterState extends State<_KeepAliveCounter>
    with AutomaticKeepAliveClientMixin {
  int _counter = 0;

  @override
  bool get wantKeepAlive => true;

  @override
  Widget build(BuildContext context) {
    super.build(context);
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Text('${widget.label}: $_counter'),
        const SizedBox(width: 16),
        ElevatedButton(
          onPressed: () => setState(() => _counter++),
          child: const Text('+'),
        ),
      ],
    );
  }
}

/// PopScope 演示
class _PopScopeDemo extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return PopScope(
      canPop: false,
      onPopInvokedWithResult: (didPop, result) {
        if (didPop) return;
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('返回被拦截')),
        );
      },
      child: const Text('点击返回按钮会被拦截（仅作演示）'),
    );
  }
}

/// RawGestureDetector 演示
class _RawGestureDetectorDemo extends StatefulWidget {
  @override
  State<_RawGestureDetectorDemo> createState() => _RawGestureDetectorDemoState();
}

class _RawGestureDetectorDemoState extends State<_RawGestureDetectorDemo> {
  String _info = '等待手势';

  @override
  Widget build(BuildContext context) {
    return RawGestureDetector(
      gestures: {
        TapGestureRecognizer: GestureRecognizerFactoryWithHandlers<TapGestureRecognizer>(
          () => TapGestureRecognizer(),
          (instance) {
            instance.onTap = () => setState(() => _info = '点击');
          },
        ),
        LongPressGestureRecognizer: GestureRecognizerFactoryWithHandlers<LongPressGestureRecognizer>(
          () => LongPressGestureRecognizer(),
          (instance) {
            instance.onLongPress = () => setState(() => _info = '长按');
          },
        ),
      },
      child: Container(
        width: double.infinity,
        height: 60,
        color: Theme.of(context).colorScheme.surfaceContainerHighest,
        child: Center(child: Text(_info)),
      ),
    );
  }
}

/// SafeArea 演示
class _SafeAreaDemo extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Container(
          height: 60,
          color: Colors.red.withValues(alpha: 0.3),
          child: const Center(child: Text('无 SafeArea - 可能被刘海遮挡')),
        ),
        const SizedBox(height: 8),
        SafeArea(
          child: Container(
            height: 60,
            color: Colors.green.withValues(alpha: 0.3),
            child: const Center(child: Text('有 SafeArea - 安全区域内')),
          ),
        ),
      ],
    );
  }
}

/// Theme 演示
class _ThemeDemo extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text('当前主题色', style: TextStyle(color: Theme.of(context).colorScheme.primary)),
        const SizedBox(height: 8),
        Theme(
          data: Theme.of(context).copyWith(
            colorScheme: Theme.of(context).colorScheme.copyWith(
              primary: Colors.purple,
            ),
          ),
          child: Builder(
            builder: (context) => Column(
              children: [
                Text('自定义主题色', style: TextStyle(color: Theme.of(context).colorScheme.primary)),
                ElevatedButton(
                  onPressed: () {},
                  child: const Text('主题按钮'),
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }
}

/// DefaultTextStyle 演示
class _DefaultTextStyleDemo extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        const Text('默认文本样式'),
        const SizedBox(height: 8),
        DefaultTextStyle(
          style: const TextStyle(
            fontSize: 16,
            color: Colors.blue,
            fontWeight: FontWeight.bold,
          ),
          child: const Column(
            children: [
              Text('使用默认样式的文本1'),
              Text('使用默认样式的文本2'),
              Text('使用默认样式的文本3'),
            ],
          ),
        ),
      ],
    );
  }
}

/// BackdropFilter 演示
class _BackdropFilterDemo extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 120,
      child: Stack(
        alignment: Alignment.center,
        children: [
          // 背景内容
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: List.generate(
              5,
              (i) => Container(
                width: 40,
                height: 80,
                color: Colors.primaries[i % Colors.primaries.length],
              ),
            ),
          ),
          // 模糊滤镜
          ClipRect(
            child: BackdropFilter(
              filter: ImageFilter.blur(sigmaX: 5, sigmaY: 5),
              child: Container(
                width: 150,
                height: 50,
                color: Colors.white.withValues(alpha: 0.2),
                child: const Center(child: Text('毛玻璃效果', style: TextStyle(color: Colors.white))),
              ),
            ),
          ),
        ],
      ),
    );
  }
}

/// ShaderMask 演示
class _ShaderMaskDemo extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        ShaderMask(
          shaderCallback: (bounds) => const LinearGradient(
            colors: [Colors.red, Colors.blue, Colors.green],
          ).createShader(bounds),
          child: const Text(
            '渐变文字效果',
            style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold, color: Colors.white),
          ),
        ),
        const SizedBox(height: 8),
        ShaderMask(
          shaderCallback: (bounds) => const RadialGradient(
            colors: [Colors.yellow, Colors.orange, Colors.red],
          ).createShader(bounds),
          child: const Icon(Icons.star, size: 60, color: Colors.white),
        ),
      ],
    );
  }
}

/// Directionality 演示
class _DirectionalityDemo extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        const Directionality(
          textDirection: TextDirection.ltr,
          child: Row(
            children: [
              Icon(Icons.arrow_back),
              SizedBox(width: 8),
              Text('LTR (从左到右)'),
            ],
          ),
        ),
        const SizedBox(height: 8),
        const Directionality(
          textDirection: TextDirection.rtl,
          child: Row(
            children: [
              Icon(Icons.arrow_forward),
              SizedBox(width: 8),
              Text('RTL (从右到左)'),
            ],
          ),
        ),
      ],
    );
  }
}
