import 'package:flutter/material.dart';
import '../../widgets/example_card.dart';

/// 滚动 Widget 示例页面
class ScrollPage extends StatelessWidget {
  const ScrollPage({super.key});

  @override
  Widget build(BuildContext context) {
    return ListView(
      children: [
        const ExampleSectionHeader(title: '单子滚动', icon: Icons.swap_vert),

        // SingleChildScrollView
        ExampleCard(
          title: 'SingleChildScrollView',
          description: '单个子组件的滚动视图',
          child: SizedBox(
            height: 120,
            child: SingleChildScrollView(
              scrollDirection: Axis.horizontal,
              child: Row(
                children: List.generate(
                  10,
                  (index) => Container(
                    width: 100,
                    margin: const EdgeInsets.only(right: 8),
                    decoration: BoxDecoration(
                      color: Colors.primaries[index % Colors.primaries.length],
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Center(
                      child: Text(
                        'Item ${index + 1}',
                        style: const TextStyle(color: Colors.white),
                      ),
                    ),
                  ),
                ),
              ),
            ),
          ),
          code: 'SingleChildScrollView(scrollDirection: Axis.horizontal)',
        ),

        const ExampleSectionHeader(title: '列表视图', icon: Icons.list),

        // ListView
        ExampleCard(
          title: 'ListView',
          description: '列表视图',
          child: SizedBox(
            height: 200,
            child: ListView.builder(
              itemCount: 15,
              itemBuilder: (context, index) {
                return ListTile(
                  leading: CircleAvatar(
                    backgroundColor: Colors.primaries[index % Colors.primaries.length],
                    child: Text('${index + 1}'),
                  ),
                  title: Text('列表项 ${index + 1}'),
                  subtitle: Text('这是第 ${index + 1} 个列表项的描述'),
                  trailing: const Icon(Icons.arrow_forward_ios, size: 16),
                );
              },
            ),
          ),
          code: 'ListView.builder(itemCount: 10, itemBuilder: (ctx, i) => ListTile())',
        ),

        // ListView.separated
        ExampleCard(
          title: 'ListView.separated',
          description: '带分隔线的列表',
          child: SizedBox(
            height: 180,
            child: ListView.separated(
              itemCount: 6,
              separatorBuilder: (context, index) => const Divider(height: 1),
              itemBuilder: (context, index) {
                return ListTile(
                  leading: Icon(Icons.person, color: Colors.primaries[index % Colors.primaries.length]),
                  title: Text('联系人 ${index + 1}'),
                  trailing: const Icon(Icons.call),
                );
              },
            ),
          ),
          code: 'ListView.separated(itemBuilder: ..., separatorBuilder: ...)',
        ),

        // ListTile
        ExampleCard(
          title: 'ListTile',
          description: 'Material Design 列表项',
          child: Column(
            children: [
              ListTile(
                leading: const Icon(Icons.inbox),
                title: const Text('收件箱'),
                trailing: Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                  decoration: BoxDecoration(
                    color: Colors.red,
                    borderRadius: BorderRadius.circular(10),
                  ),
                  child: const Text('3', style: TextStyle(color: Colors.white)),
                ),
                onTap: () {},
              ),
              ListTile(
                leading: const Icon(Icons.send),
                title: const Text('已发送'),
                subtitle: const Text('最近: 昨天'),
                trailing: const Icon(Icons.chevron_right),
                onTap: () {},
              ),
              ListTile(
                leading: const Icon(Icons.star),
                title: const Text('已加星标'),
                subtitle: const Text('12 封邮件'),
                trailing: const Icon(Icons.chevron_right),
                onTap: () {},
              ),
              ListTile(
                leading: const Icon(Icons.delete),
                title: const Text('垃圾箱'),
                enabled: false,
                trailing: const Icon(Icons.chevron_right),
              ),
            ],
          ),
          code: 'ListTile(leading: Icon(), title: Text(), subtitle: Text())',
        ),

        // ReorderableListView
        ExampleCard(
          title: 'ReorderableListView',
          description: '可重新排序的列表',
          child: SizedBox(
            height: 200,
            child: _ReorderableListDemo(),
          ),
          code: 'ReorderableListView(onReorder: (old, new) => ...)',
        ),

        const ExampleSectionHeader(title: '网格视图', icon: Icons.grid_view),

        // GridView
        ExampleCard(
          title: 'GridView.count',
          description: '固定列数的网格',
          child: SizedBox(
            height: 180,
            child: GridView.count(
              crossAxisCount: 3,
              mainAxisSpacing: 8,
              crossAxisSpacing: 8,
              children: List.generate(
                9,
                (index) => Container(
                  decoration: BoxDecoration(
                    color: Colors.primaries[index % Colors.primaries.length],
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Center(
                    child: Text(
                      '${index + 1}',
                      style: const TextStyle(color: Colors.white, fontSize: 20),
                    ),
                  ),
                ),
              ),
            ),
          ),
          code: 'GridView.count(crossAxisCount: 3, children: [...])',
        ),

        // GridView.builder
        ExampleCard(
          title: 'GridView.builder',
          description: '动态构建网格项',
          child: SizedBox(
            height: 200,
            child: GridView.builder(
              gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 2,
                mainAxisSpacing: 8,
                crossAxisSpacing: 8,
                childAspectRatio: 1.5,
              ),
              itemCount: 6,
              itemBuilder: (context, index) {
                return Container(
                  decoration: BoxDecoration(
                    color: Colors.primaries[index % Colors.primaries.length].withOpacity(0.7),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      const Icon(Icons.image, color: Colors.white, size: 32),
                      const SizedBox(height: 8),
                      Text('图片 ${index + 1}', style: const TextStyle(color: Colors.white)),
                    ],
                  ),
                );
              },
            ),
          ),
          code: 'GridView.builder(gridDelegate: SliverGridDelegate..., itemBuilder: ...)',
        ),

        // GridView.extent
        ExampleCard(
          title: 'GridView.extent',
          description: '按最大宽度创建网格',
          child: SizedBox(
            height: 160,
            child: GridView.extent(
              maxCrossAxisExtent: 100,
              mainAxisSpacing: 8,
              crossAxisSpacing: 8,
              children: List.generate(
                12,
                (index) => Container(
                  decoration: BoxDecoration(
                    color: Colors.primaries[index % Colors.primaries.length],
                    borderRadius: BorderRadius.circular(8),
                  ),
                ),
              ),
            ),
          ),
          code: 'GridView.extent(maxCrossAxisExtent: 100)',
        ),

        const ExampleSectionHeader(title: '高级滚动', icon: Icons.auto_awesome),

        // CustomScrollView
        ExampleCard(
          title: 'CustomScrollView',
          description: '自定义滚动视图，可组合多种 Sliver',
          child: SizedBox(
            height: 300,
            child: CustomScrollView(
              slivers: [
                SliverAppBar(
                  pinned: true,
                  expandedHeight: 100,
                  flexibleSpace: FlexibleSpaceBar(
                    title: const Text('SliverAppBar'),
                    background: Container(color: Theme.of(context).colorScheme.primaryContainer),
                  ),
                ),
                SliverGrid(
                  gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                    crossAxisCount: 3,
                    mainAxisSpacing: 4,
                    crossAxisSpacing: 4,
                  ),
                  delegate: SliverChildBuilderDelegate(
                    (context, index) => Container(
                      color: Colors.primaries[index % Colors.primaries.length].withOpacity(0.5),
                      child: Center(child: Text('$index')),
                    ),
                    childCount: 12,
                  ),
                ),
                const SliverToBoxAdapter(
                  child: ListTile(title: Text('分隔标题')),
                ),
                SliverFixedExtentList(
                  itemExtent: 50,
                  delegate: SliverChildBuilderDelegate(
                    (context, index) => ListTile(
                      title: Text('列表项 $index'),
                      tileColor: index.isEven ? Colors.grey[100] : null,
                    ),
                    childCount: 6,
                  ),
                ),
              ],
            ),
          ),
          code: 'CustomScrollView(slivers: [SliverAppBar(), SliverList()...])',
        ),

        // SliverList
        ExampleCard(
          title: 'SliverList',
          description: 'Sliver 列表',
          child: SizedBox(
            height: 200,
            child: CustomScrollView(
              slivers: [
                SliverList(
                  delegate: SliverChildBuilderDelegate(
                    (context, index) => Container(
                      height: 60,
                      margin: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                      decoration: BoxDecoration(
                        color: Colors.primaries[index % Colors.primaries.length],
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: Center(
                        child: Text(
                          'SliverList Item ${index + 1}',
                          style: const TextStyle(color: Colors.white),
                        ),
                      ),
                    ),
                    childCount: 5,
                  ),
                ),
              ],
            ),
          ),
          code: 'SliverList(delegate: SliverChildBuilderDelegate(...))',
        ),

        // SliverGrid
        ExampleCard(
          title: 'SliverGrid',
          description: 'Sliver 网格',
          child: SizedBox(
            height: 180,
            child: CustomScrollView(
              slivers: [
                SliverGrid(
                  gridDelegate: const SliverGridDelegateWithMaxCrossAxisExtent(
                    maxCrossAxisExtent: 100,
                    mainAxisSpacing: 4,
                    crossAxisSpacing: 4,
                  ),
                  delegate: SliverChildBuilderDelegate(
                    (context, index) => Container(
                      color: Colors.primaries[index % Colors.primaries.length],
                      child: Center(child: Text('$index', style: const TextStyle(color: Colors.white))),
                    ),
                    childCount: 12,
                  ),
                ),
              ],
            ),
          ),
          code: 'SliverGrid(gridDelegate: ..., delegate: ...)',
        ),

        // SliverAppBar
        ExampleCard(
          title: 'SliverAppBar',
          description: '可折叠的应用栏',
          child: SizedBox(
            height: 250,
            child: CustomScrollView(
              slivers: [
                SliverAppBar(
                  expandedHeight: 120,
                  floating: false,
                  pinned: true,
                  snap: false,
                  flexibleSpace: FlexibleSpaceBar(
                    title: const Text('可折叠标题'),
                    background: Container(
                      decoration: BoxDecoration(
                        gradient: LinearGradient(
                          colors: [
                            Theme.of(context).colorScheme.primary,
                            Theme.of(context).colorScheme.secondary,
                          ],
                        ),
                      ),
                    ),
                  ),
                ),
                SliverList(
                  delegate: SliverChildBuilderDelegate(
                    (context, index) => ListTile(title: Text('项目 $index')),
                    childCount: 10,
                  ),
                ),
              ],
            ),
          ),
          code: 'SliverAppBar(expandedHeight: 120, pinned: true)',
        ),

        // SliverPersistentHeader
        ExampleCard(
          title: 'SliverPersistentHeader',
          description: '持久化头部',
          child: SizedBox(
            height: 200,
            child: CustomScrollView(
              slivers: [
                SliverPersistentHeader(
                  pinned: true,
                  delegate: _SliverHeaderDelegate(
                    minHeight: 50,
                    maxHeight: 100,
                    child: Container(
                      color: Colors.purple,
                      child: const Center(
                        child: Text('持久化头部', style: TextStyle(color: Colors.white)),
                      ),
                    ),
                  ),
                ),
                SliverList(
                  delegate: SliverChildBuilderDelegate(
                    (context, index) => ListTile(title: Text('内容 $index')),
                    childCount: 8,
                  ),
                ),
              ],
            ),
          ),
          code: 'SliverPersistentHeader(delegate: SliverPersistentHeaderDelegate)',
        ),

        // ScrollController
        ExampleCard(
          title: 'ScrollController',
          description: '滚动控制器',
          child: _ScrollControllerDemo(),
          code: 'ScrollController scrollController = ScrollController()',
        ),

        // ScrollNotification
        ExampleCard(
          title: 'NotificationListener',
          description: '滚动通知监听',
          child: _ScrollNotificationDemo(),
          code: 'NotificationListener<ScrollNotification>(onNotification: ...)',
        ),

        const SizedBox(height: 24),
      ],
    );
  }
}

/// 可重排序列表演示
class _ReorderableListDemo extends StatefulWidget {
  @override
  State<_ReorderableListDemo> createState() => _ReorderableListDemoState();
}

class _ReorderableListDemoState extends State<_ReorderableListDemo> {
  final List<String> _items = List.generate(5, (i) => '项目 ${i + 1}');

  @override
  Widget build(BuildContext context) {
    return ReorderableListView(
      onReorder: (oldIndex, newIndex) {
        setState(() {
          if (newIndex > oldIndex) newIndex--;
          final item = _items.removeAt(oldIndex);
          _items.insert(newIndex, item);
        });
      },
      children: _items.map((item) {
        return ListTile(
          key: ValueKey(item),
          title: Text(item),
          leading: const Icon(Icons.drag_handle),
          trailing: ReorderableDragStartListener(
            index: _items.indexOf(item),
            child: const Icon(Icons.reorder),
          ),
        );
      }).toList(),
    );
  }
}

/// Sliver 持久化头部代理
class _SliverHeaderDelegate extends SliverPersistentHeaderDelegate {
  final double minHeight;
  final double maxHeight;
  final Widget child;

  _SliverHeaderDelegate({
    required this.minHeight,
    required this.maxHeight,
    required this.child,
  });

  @override
  double get minExtent => minHeight;

  @override
  double get maxExtent => maxHeight;

  @override
  Widget build(BuildContext context, double shrinkOffset, bool overlapsContent) {
    return SizedBox.expand(child: child);
  }

  @override
  bool shouldRebuild(covariant _SliverHeaderDelegate oldDelegate) {
    return maxHeight != oldDelegate.maxHeight ||
        minHeight != oldDelegate.minHeight ||
        child != oldDelegate.child;
  }
}

/// 滚动控制器演示
class _ScrollControllerDemo extends StatefulWidget {
  @override
  State<_ScrollControllerDemo> createState() => _ScrollControllerDemoState();
}

class _ScrollControllerDemoState extends State<_ScrollControllerDemo> {
  final ScrollController _controller = ScrollController();
  bool _showButton = false;

  @override
  void initState() {
    super.initState();
    _controller.addListener(() {
      setState(() {
        _showButton = _controller.offset > 100;
      });
    });
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 180,
      child: Stack(
        children: [
          ListView.builder(
            controller: _controller,
            itemCount: 20,
            itemBuilder: (context, index) => ListTile(title: Text('项目 $index')),
          ),
          if (_showButton)
            Positioned(
              right: 16,
              bottom: 16,
              child: FloatingActionButton.small(
                onPressed: () {
                  _controller.animateTo(
                    0,
                    duration: const Duration(milliseconds: 300),
                    curve: Curves.easeOut,
                  );
                },
                child: const Icon(Icons.arrow_upward),
              ),
            ),
        ],
      ),
    );
  }
}

/// 滚动通知演示
class _ScrollNotificationDemo extends StatefulWidget {
  @override
  State<_ScrollNotificationDemo> createState() => _ScrollNotificationDemoState();
}

class _ScrollNotificationDemoState extends State<_ScrollNotificationDemo> {
  String _scrollInfo = '滚动状态将在这里显示';

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Container(
          padding: const EdgeInsets.all(8),
          color: Theme.of(context).colorScheme.surfaceContainerHighest,
          child: Text(_scrollInfo, style: const TextStyle(fontSize: 12)),
        ),
        const SizedBox(height: 8),
        SizedBox(
          height: 100,
          child: NotificationListener<ScrollNotification>(
            onNotification: (notification) {
              setState(() {
                _scrollInfo = '位置: ${notification.metrics.pixels.toStringAsFixed(1)}\n'
                    '范围: ${notification.metrics.minScrollExtent.toStringAsFixed(1)} - '
                    '${notification.metrics.maxScrollExtent.toStringAsFixed(1)}\n'
                    '方向: ${notification is ScrollStartNotification ? "开始" : notification is ScrollEndNotification ? "结束" : "滚动中"}';
              });
              return true;
            },
            child: ListView.builder(
              itemCount: 20,
              itemBuilder: (context, index) => ListTile(title: Text('项目 $index')),
            ),
          ),
        ),
      ],
    );
  }
}
