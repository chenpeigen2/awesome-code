import 'package:flutter/material.dart';
import 'home_view.dart';

class ScrollPage extends StatelessWidget {
  const ScrollPage({super.key});

  @override
  Widget build(BuildContext context) {
    return DemoScaffold(title: '滚动组件', children: [
      const Section('SingleChildScrollView'),
      DemoCard(title: 'SingleChildScrollView', desc: '单个子组件可滚动', child: SizedBox(
        height: 120,
        child: SingleChildScrollView(
          scrollDirection: Axis.horizontal,
          child: Row(children: List.generate(10, (i) => Container(
            width: 80, height: 80, margin: const EdgeInsets.only(right: 8),
            color: Colors.primaries[i % Colors.primaries.length][200],
            child: Center(child: Text('Item ${i + 1}')),
          ))),
        ),
      )),
      const Section('ListView'),
      DemoCard(title: 'ListView.builder', desc: '高效列表构建', child: SizedBox(
        height: 250,
        child: ListView.builder(
          itemCount: 20,
          itemBuilder: (ctx, i) => ListTile(
            leading: CircleAvatar(child: Text('${i + 1}')),
            title: Text('列表项 ${i + 1}'),
            subtitle: Text('这是第 ${i + 1} 个列表项的描述'),
            trailing: const Icon(Icons.chevron_right),
          ),
        ),
      )),
      DemoCard(title: 'ListView.separated', desc: '带分隔线的列表', child: SizedBox(
        height: 200,
        child: ListView.separated(
          itemCount: 6,
          separatorBuilder: (_, __) => const Divider(height: 1),
          itemBuilder: (ctx, i) => Padding(padding: const EdgeInsets.symmetric(vertical: 12, horizontal: 16), child: Text('Separator Item ${i + 1}')),
        ),
      )),
      const Section('GridView'),
      DemoCard(title: 'GridView.extent', desc: '按最大宽度自动排列', child: SizedBox(
        height: 200,
        child: GridView.extent(
          maxCrossAxisExtent: 100,
          children: List.generate(12, (i) => Card(color: Colors.primaries[i % Colors.primaries.length][100], child: Center(child: Text('${i + 1}')))),
        ),
      )),
      DemoCard(title: 'GridView.builder', desc: 'SliverGridDelegateWithFixedCrossAxisCount', child: SizedBox(
        height: 200,
        child: GridView.builder(
          gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(crossAxisCount: 3, mainAxisSpacing: 8, crossAxisSpacing: 8),
          itemCount: 9,
          itemBuilder: (ctx, i) => Container(
            color: Colors.primaries[i % Colors.primaries.length][200],
            child: Center(child: Text('Grid ${i + 1}')),
          ),
        ),
      )),
      const Section('PageView'),
      DemoCard(title: 'PageView', desc: '可滑动的页面视图', child: SizedBox(
        height: 150,
        child: PageView(children: [
          _pageItem('Page 1', Colors.blue),
          _pageItem('Page 2', Colors.green),
          _pageItem('Page 3', Colors.orange),
        ]),
      )),
      const Section('CustomScrollView + Slivers'),
      DemoCard(title: 'SliverAppBar + SliverList', desc: '自定义滚动效果', child: SizedBox(
        height: 300,
        child: CustomScrollView(
          slivers: [
            SliverAppBar(expandedHeight: 100, pinned: true, flexibleSpace: FlexibleSpaceBar(title: const Text('Sliver Demo'), background: Container(color: Colors.blue[100]))),
            SliverToBoxAdapter(child: Container(padding: const EdgeInsets.all(16), color: Colors.amber[50], child: const Text('SliverToBoxAdapter: 可以放任何普通 Widget'))),
            SliverGrid(gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(crossAxisCount: 3), delegate: SliverChildBuilderDelegate((ctx, i) => Container(color: Colors.primaries[i % 6][100], child: Center(child: Text('$i'))), childCount: 6)),
            SliverList(delegate: SliverChildBuilderDelegate((ctx, i) => ListTile(title: Text('SliverList Item ${i + 1}')), childCount: 10)),
          ],
        ),
      )),
      const Section('ScrollController'),
      DemoCard(title: 'ScrollNotification', desc: '监听滚动位置', child: SizedBox(
        height: 150,
        child: NotificationListener<ScrollNotification>(
          onNotification: (notification) => true,
          child: ListView.builder(
            itemCount: 30,
            itemBuilder: (ctx, i) => ListTile(title: Text('Scroll Item ${i + 1}')),
          ),
        ),
      )),
    ]);
  }

  Widget _pageItem(String text, Color color) => Container(
    color: color.withValues(alpha: 0.3),
    child: Center(child: Text(text, style: TextStyle(fontSize: 24, color: color, fontWeight: FontWeight.bold))),
  );
}
