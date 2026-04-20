import 'package:flutter/material.dart';
import 'home_view.dart';

class MaterialDemoPage extends StatelessWidget {
  const MaterialDemoPage({super.key});

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(length: 3, child: DemoScaffold(title: 'Material 组件', children: [
      const Section('AppBar'),
      DemoCard(title: 'SliverAppBar', desc: '可折叠的AppBar', child: SizedBox(
        height: 200,
        child: CustomScrollView(slivers: [
          SliverAppBar(expandedHeight: 80, pinned: true, title: const Text('SliverAppBar Demo')),
          SliverList(delegate: SliverChildListDelegate(List.generate(5, (i) => ListTile(title: Text('Item ${i + 1}'))))),
        ]),
      )),
      const Section('TabBar / TabBarView'),
      DemoCard(title: 'TabBar', desc: '标签页切换', child: SizedBox(
        height: 200,
        child: Column(children: [
          const TabBar(tabs: [Tab(text: 'Tab 1'), Tab(text: 'Tab 2'), Tab(text: 'Tab 3')]),
          Expanded(child: TabBarView(children: [
            Center(child: Text('Content 1')), Center(child: Text('Content 2')), Center(child: Text('Content 3')),
          ])),
        ]),
      )),
      const Section('BottomNavigationBar'),
      DemoCard(title: 'BottomNavigationBar', desc: '底部导航栏', child: const _BottomNavDemo()),
      const Section('NavigationRail'),
      DemoCard(title: 'NavigationRail', desc: '侧边导航栏', child: SizedBox(
        height: 200,
        child: Row(children: [
          NavigationRail(selectedIndex: 0, destinations: const [NavigationRailDestination(icon: Icon(Icons.home), label: Text('Home')), NavigationRailDestination(icon: Icon(Icons.search), label: Text('Search')), NavigationRailDestination(icon: Icon(Icons.person), label: Text('Profile'))], onDestinationSelected: (_) {}),
          const Expanded(child: Center(child: Text('Content Area'))),
        ]),
      )),
      const Section('Drawer 抽屉'),
      DemoCard(title: 'Drawer', desc: '侧边抽屉菜单 (见首页)', child: const Text('已在首页实现 Drawer', style: TextStyle(fontStyle: FontStyle.italic))),
      const Section('Chip 标签'),
      DemoCard(title: 'Chip / FilterChip / ChoiceChip', child: const _ChipDemo()),
      const Section('DataTable'),
      DemoCard(title: 'DataTable', desc: '数据表格', child: SingleChildScrollView(scrollDirection: Axis.horizontal, child: DataTable(columns: const [
        DataColumn(label: Text('姓名')), DataColumn(label: Text('年龄')), DataColumn(label: Text('角色')), DataColumn(label: Text('状态')),
      ], rows: const [
        DataRow(cells: [DataCell(Text('张三')), DataCell(Text('25')), DataCell(Text('开发')), DataCell(Text('在线'))]),
        DataRow(cells: [DataCell(Text('李四')), DataCell(Text('30')), DataCell(Text('设计')), DataCell(Text('离线'))]),
        DataRow(cells: [DataCell(Text('王五')), DataCell(Text('28')), DataCell(Text('测试')), DataCell(Text('忙碌'))]),
      ]))),
      const Section('ProgressIndicator'),
      DemoCard(title: 'Linear / Circular Progress', child: Column(children: const [
        LinearProgressIndicator(), SizedBox(height: 12), LinearProgressIndicator(value: 0.7), SizedBox(height: 12),
        Row(mainAxisAlignment: MainAxisAlignment.spaceEvenly, children: [CircularProgressIndicator(), CircularProgressIndicator(value: 0.6)]),
      ])),
      const Section('Divider / VerticalDivider'),
      DemoCard(title: 'Divider', child: Column(children: const [
        Divider(), Text('分割线之间'), Divider(color: Colors.red, thickness: 2), Text('红色粗分割线'), Divider(indent: 50, endIndent: 50, color: Colors.blue),
      ])),
      const Section('ListTile'),
      DemoCard(title: 'ListTile 变体', child: Column(children: [
        ListTile(leading: const Icon(Icons.inbox), title: const Text('收件箱'), trailing: const Badge(child: Text('3')), onTap: () {}),
        const Divider(height: 1),
        SwitchListTile(secondary: const Icon(Icons.wifi), title: const Text('Wi-Fi'), value: true, onChanged: (_) {}),
        const Divider(height: 1),
        CheckboxListTile(secondary: const Icon(Icons.check_circle), title: const Text('已完成'), value: true, onChanged: (_) {}),
        const Divider(height: 1),
        ExpansionTile(leading: const Icon(Icons.folder), title: const Text('展开查看更多'), children: const [ListTile(title: Text('子项 1')), ListTile(title: Text('子项 2'))]),
      ])),
      const Section('SnackBar'),
      DemoCard(title: 'ScaffoldMessenger SnackBar', child: ElevatedButton(
        onPressed: () => ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: const Text('这是一条 SnackBar'), behavior: SnackBarBehavior.floating, action: SnackBarAction(label: '撤销', onPressed: () {}))),
        child: const Text('显示 SnackBar'),
      )),
      const Section('Badge'),
      DemoCard(title: 'Badge', desc: 'Material 3 徽章', child: Wrap(spacing: 24, children: const [
        Badge(child: Icon(Icons.mail, size: 32)),
        Badge(label: Text('5'), child: Icon(Icons.notifications, size: 32)),
        Badge(label: Text('New'), child: Icon(Icons.shopping_cart, size: 32)),
      ])),
      const Section('Stepper'),
      DemoCard(title: 'Stepper', desc: '步骤指示器', child: const _StepperDemo()),
    ]));
  }
}

class _BottomNavDemo extends StatefulWidget {
  const _BottomNavDemo();
  @override
  State<_BottomNavDemo> createState() => _BottomNavDemoState();
}

class _BottomNavDemoState extends State<_BottomNavDemo> {
  int _idx = 0;

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      SizedBox(height: 80, child: Center(child: Text('Page ${_idx + 1}'))),
      BottomNavigationBar(
        currentIndex: _idx,
        onTap: (i) => setState(() => _idx = i),
        items: const [BottomNavigationBarItem(icon: Icon(Icons.home), label: 'Home'), BottomNavigationBarItem(icon: Icon(Icons.search), label: 'Search'), BottomNavigationBarItem(icon: Icon(Icons.person), label: 'Profile')],
      ),
    ]);
  }
}

class _ChipDemo extends StatefulWidget {
  const _ChipDemo();
  @override
  State<_ChipDemo> createState() => _ChipDemoState();
}

class _ChipDemoState extends State<_ChipDemo> {
  final _tags = ['Flutter', 'Dart', 'GetX', 'Material'];
  int? _choice;
  final _filters = <String>{};

  @override
  Widget build(BuildContext context) {
    return Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
      const Text('Chip:', style: TextStyle(fontWeight: FontWeight.bold)),
      const SizedBox(height: 4),
      Wrap(spacing: 8, children: _tags.map((t) => Chip(label: Text(t), onDeleted: () => setState(() => _tags.remove(t)))).toList()),
      const SizedBox(height: 12),
      const Text('ChoiceChip:', style: TextStyle(fontWeight: FontWeight.bold)),
      const SizedBox(height: 4),
      Wrap(spacing: 8, children: _tags.map((t) => ChoiceChip(label: Text(t), selected: _choice == _tags.indexOf(t), onSelected: (_) => setState(() => _choice = _tags.indexOf(t)))).toList()),
      const SizedBox(height: 12),
      const Text('FilterChip:', style: TextStyle(fontWeight: FontWeight.bold)),
      const SizedBox(height: 4),
      Wrap(spacing: 8, children: _tags.map((t) => FilterChip(label: Text(t), selected: _filters.contains(t), onSelected: (s) => setState(() => s ? _filters.add(t) : _filters.remove(t)))).toList()),
    ]);
  }
}

class _StepperDemo extends StatefulWidget {
  const _StepperDemo();
  @override
  State<_StepperDemo> createState() => _StepperDemoState();
}

class _StepperDemoState extends State<_StepperDemo> {
  int _step = 0;

  @override
  Widget build(BuildContext context) {
    return Stepper(
      currentStep: _step,
      onStepTapped: (i) => setState(() => _step = i),
      onStepContinue: () => setState(() => _step < 2 ? _step++ : null),
      onStepCancel: () => setState(() => _step > 0 ? _step-- : null),
      steps: const [
        Step(title: Text('步骤一'), content: Text('填写基本信息')),
        Step(title: Text('步骤二'), content: Text('上传文件')),
        Step(title: Text('步骤三'), content: Text('确认提交')),
      ],
    );
  }
}
