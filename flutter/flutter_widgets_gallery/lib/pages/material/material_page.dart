import 'package:flutter/material.dart';
import '../../widgets/example_card.dart';

/// Material Design 组件示例页面
class MaterialPage extends StatelessWidget {
  const MaterialPage({super.key});

  @override
  Widget build(BuildContext context) {
    return ListView(
      children: [
        const ExampleSectionHeader(title: '导航栏', icon: Icons.navigation),

        // AppBar
        ExampleCard(
          title: 'AppBar',
          description: '应用栏',
          child: Column(
            children: [
              AppBar(
                title: const Text('AppBar 标题'),
                leading: const Icon(Icons.menu),
                actions: [
                  IconButton(icon: const Icon(Icons.search), onPressed: () {}),
                  IconButton(icon: const Icon(Icons.more_vert), onPressed: () {}),
                ],
              ),
              const SizedBox(height: 8),
              AppBar(
                title: const Text('带背景色的 AppBar'),
                backgroundColor: Theme.of(context).colorScheme.primaryContainer,
                foregroundColor: Theme.of(context).colorScheme.onPrimaryContainer,
              ),
            ],
          ),
          code: 'AppBar(title: Text("标题"), actions: [IconButton()...])',
        ),

        // TabBar
        ExampleCard(
          title: 'TabBar',
          description: '标签栏',
          child: _TabBarDemo(),
          code: 'TabBar(tabs: [Tab(text: "标签1"), Tab(text: "标签2")])',
        ),

        // BottomNavigationBar
        ExampleCard(
          title: 'BottomNavigationBar',
          description: '底部导航栏',
          child: _BottomNavDemo(),
          code: 'BottomNavigationBar(items: [BottomNavigationBarItem()...])',
        ),

        // NavigationRail
        ExampleCard(
          title: 'NavigationRail',
          description: '侧边导航栏',
          child: _NavigationRailDemo(),
          code: 'NavigationRail(destinations: [NavigationRailDestination()...])',
        ),

        // NavigationBar
        ExampleCard(
          title: 'NavigationBar (Material 3)',
          description: 'Material 3 底部导航栏',
          child: _NavigationBarDemo(),
          code: 'NavigationBar(destinations: [NavigationDestination()...])',
        ),

        const ExampleSectionHeader(title: '信息展示', icon: Icons.info),

        // Card
        ExampleCard(
          title: 'Card',
          description: '卡片组件',
          child: Column(
            children: [
              Card(
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text('卡片标题', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                      const SizedBox(height: 8),
                      const Text('这是卡片的描述内容，可以包含多行文本。'),
                      const SizedBox(height: 12),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.end,
                        children: [
                          TextButton(onPressed: () {}, child: const Text('取消')),
                          TextButton(onPressed: () {}, child: const Text('确定')),
                        ],
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
          code: 'Card(child: Padding(padding: EdgeInsets.all(16), child: ...))',
        ),

        // ListTile
        ExampleCard(
          title: 'ListTile',
          description: '列表项组件',
          child: const Column(
            children: [
              ListTile(
                leading: CircleAvatar(child: Text('A')),
                title: Text('标题'),
                subtitle: Text('副标题'),
                trailing: Icon(Icons.chevron_right),
              ),
              Divider(),
              ListTile(
                leading: Icon(Icons.star, color: Colors.amber),
                title: Text('带图标'),
                trailing: Text('附加信息'),
              ),
            ],
          ),
          code: 'ListTile(leading: Icon(), title: Text(), subtitle: Text())',
        ),

        // Chip
        ExampleCard(
          title: 'Chip',
          description: '标签芯片',
          child: Wrap(
            spacing: 8,
            runSpacing: 8,
            children: [
              const Chip(label: Text('基础 Chip')),
              Chip(
                avatar: CircleAvatar(child: const Text('U')),
                label: const Text('带头像'),
              ),
              const InputChip(
                label: Text('InputChip'),
                onPressed: null,
              ),
              FilterChip(
                label: const Text('FilterChip'),
                selected: false,
                onSelected: (_) {},
              ),
              ChoiceChip(
                label: const Text('ChoiceChip'),
                selected: true,
                onSelected: (_) {},
              ),
              ActionChip(
                avatar: const Icon(Icons.play_arrow, size: 18),
                label: const Text('ActionChip'),
                onPressed: () {},
              ),
            ],
          ),
          code: 'Chip(label: Text("标签")) / FilterChip / ChoiceChip',
        ),

        // Badge
        ExampleCard(
          title: 'Badge',
          description: '徽章标记',
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            children: [
              Badge(
                label: const Text('3'),
                child: const Icon(Icons.mail, size: 32),
              ),
              Badge(
                label: const Text('99+'),
                backgroundColor: Theme.of(context).colorScheme.error,
                child: const Icon(Icons.notifications, size: 32),
              ),
              const Badge(
                child: Icon(Icons.shopping_cart, size: 32),
              ),
            ],
          ),
          code: 'Badge(label: Text("3"), child: Icon(Icons.mail))',
        ),

        // ProgressIndicator
        ExampleCard(
          title: 'ProgressIndicator',
          description: '进度指示器',
          child: Column(
            children: [
              const Row(
                mainAxisAlignment: MainAxisAlignment.spaceAround,
                children: [
                  CircularProgressIndicator(),
                  SizedBox(
                    width: 100,
                    child: LinearProgressIndicator(),
                  ),
                ],
              ),
              const SizedBox(height: 16),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceAround,
                children: [
                  CircularProgressIndicator(
                    value: 0.7,
                    strokeWidth: 6,
                    backgroundColor: Colors.grey[200],
                  ),
                  SizedBox(
                    width: 100,
                    child: LinearProgressIndicator(
                      value: 0.7,
                      minHeight: 8,
                      borderRadius: BorderRadius.circular(4),
                    ),
                  ),
                ],
              ),
            ],
          ),
          code: 'CircularProgressIndicator() / LinearProgressIndicator(value: 0.7)',
        ),

        // Divider
        ExampleCard(
          title: 'Divider & VerticalDivider',
          description: '分割线',
          child: const Column(
            children: [
              Text('上方内容'),
              Divider(height: 32, thickness: 2),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text('左边'),
                  VerticalDivider(width: 32, thickness: 2),
                  Text('右边'),
                ],
              ),
            ],
          ),
          code: 'Divider(height: 32) / VerticalDivider(width: 32)',
        ),

        const ExampleSectionHeader(title: '底部组件', icon: Icons.vertical_align_bottom),

        // BottomSheet
        ExampleCard(
          title: 'BottomSheet',
          description: '底部抽屉',
          child: ElevatedButton(
            onPressed: () {
              showModalBottomSheet(
                context: context,
                builder: (context) => Container(
                  height: 200,
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text('底部抽屉', style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
                      const SizedBox(height: 16),
                      const Text('这是底部抽屉的内容，可以包含任意组件。'),
                      const Spacer(),
                      ElevatedButton(
                        onPressed: () => Navigator.pop(context),
                        child: const Text('关闭'),
                      ),
                    ],
                  ),
                ),
              );
            },
            child: const Text('显示 BottomSheet'),
          ),
          code: 'showModalBottomSheet(context: context, builder: (ctx) => Widget)',
        ),

        // SnackBar
        ExampleCard(
          title: 'SnackBar',
          description: '消息提示条',
          child: ElevatedButton(
            onPressed: () {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(
                  content: const Text('这是一条消息提示'),
                  action: SnackBarAction(
                    label: '撤销',
                    onPressed: () {},
                  ),
                  behavior: SnackBarBehavior.floating,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                ),
              );
            },
            child: const Text('显示 SnackBar'),
          ),
          code: 'ScaffoldMessenger.of(context).showSnackBar(SnackBar(...))',
        ),

        // Banner
        ExampleCard(
          title: 'MaterialBanner',
          description: '横幅提示',
          child: ElevatedButton(
            onPressed: () {
              ScaffoldMessenger.of(context).showMaterialBanner(
                MaterialBanner(
                  content: const Text('这是一条重要消息'),
                  leading: const Icon(Icons.info),
                  actions: [
                    TextButton(
                      onPressed: () => ScaffoldMessenger.of(context).hideCurrentMaterialBanner(),
                      child: const Text('关闭'),
                    ),
                  ],
                ),
              );
            },
            child: const Text('显示 MaterialBanner'),
          ),
          code: 'ScaffoldMessenger.of(context).showMaterialBanner(MaterialBanner(...))',
        ),

        const ExampleSectionHeader(title: '其他组件', icon: Icons.more_horiz),

        // Drawer
        ExampleCard(
          title: 'Drawer',
          description: '侧边抽屉（点击左上角菜单查看）',
          child: const Text('已在主页面的 Scaffold.drawer 中配置'),
          code: 'Scaffold(drawer: Drawer(child: ListView(...)))',
        ),

        // FloatingActionButton
        ExampleCard(
          title: 'FloatingActionButton',
          description: '浮动操作按钮',
          child: Wrap(
            spacing: 16,
            runSpacing: 16,
            children: [
              FloatingActionButton(
                heroTag: 'fab1',
                onPressed: () {},
                child: const Icon(Icons.add),
              ),
              FloatingActionButton.small(
                heroTag: 'fab2',
                onPressed: () {},
                child: const Icon(Icons.edit),
              ),
              FloatingActionButton.extended(
                heroTag: 'fab3',
                onPressed: () {},
                icon: const Icon(Icons.create),
                label: const Text('创建'),
              ),
            ],
          ),
          code: 'FloatingActionButton(onPressed: () {}, child: Icon(Icons.add))',
        ),

        // Tooltip
        ExampleCard(
          title: 'Tooltip',
          description: '工具提示',
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            children: [
              Tooltip(
                message: '这是一个提示信息',
                child: const Icon(Icons.info, size: 32),
              ),
              Tooltip(
                message: '点击分享',
                child: IconButton(
                  icon: const Icon(Icons.share),
                  onPressed: () {},
                ),
              ),
              const Tooltip(
                message: '长按查看提示',
                child: Text('长按我'),
              ),
            ],
          ),
          code: 'Tooltip(message: "提示信息", child: Widget)',
        ),

        // Stepper
        ExampleCard(
          title: 'Stepper',
          description: '步骤指示器',
          child: _StepperDemo(),
          code: 'Stepper(steps: [Step()...], onStepContinue: ...)',
        ),

        // ExpansionPanel
        ExampleCard(
          title: 'ExpansionPanelList',
          description: '展开面板列表',
          child: _ExpansionPanelDemo(),
          code: 'ExpansionPanelList(children: [ExpansionPanel()...])',
        ),

        // SearchAnchor
        ExampleCard(
          title: 'SearchAnchor',
          description: '搜索栏 (Material 3)',
          child: _SearchAnchorDemo(),
          code: 'SearchAnchor(builder: ..., suggestionsBuilder: ...)',
        ),

        const SizedBox(height: 24),
      ],
    );
  }
}

/// TabBar 演示
class _TabBarDemo extends StatefulWidget {
  @override
  State<_TabBarDemo> createState() => _TabBarDemoState();
}

class _TabBarDemoState extends State<_TabBarDemo> with SingleTickerProviderStateMixin {
  late TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 3, vsync: this);
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        TabBar(
          controller: _tabController,
          tabs: const [
            Tab(icon: Icon(Icons.phone), text: '电话'),
            Tab(icon: Icon(Icons.message), text: '消息'),
            Tab(icon: Icon(Icons.contacts), text: '联系人'),
          ],
        ),
        SizedBox(
          height: 80,
          child: TabBarView(
            controller: _tabController,
            children: const [
              Center(child: Text('电话列表')),
              Center(child: Text('消息列表')),
              Center(child: Text('联系人列表')),
            ],
          ),
        ),
      ],
    );
  }
}

/// BottomNavigationBar 演示
class _BottomNavDemo extends StatefulWidget {
  @override
  State<_BottomNavDemo> createState() => _BottomNavDemoState();
}

class _BottomNavDemoState extends State<_BottomNavDemo> {
  int _currentIndex = 0;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        SizedBox(
          height: 60,
          child: Center(child: Text('当前页面: ${['首页', '搜索', '我的'][_currentIndex]}')),
        ),
        BottomNavigationBar(
          currentIndex: _currentIndex,
          onTap: (index) => setState(() => _currentIndex = index),
          items: const [
            BottomNavigationBarItem(icon: Icon(Icons.home), label: '首页'),
            BottomNavigationBarItem(icon: Icon(Icons.search), label: '搜索'),
            BottomNavigationBarItem(icon: Icon(Icons.person), label: '我的'),
          ],
        ),
      ],
    );
  }
}

/// NavigationRail 演示
class _NavigationRailDemo extends StatefulWidget {
  @override
  State<_NavigationRailDemo> createState() => _NavigationRailDemoState();
}

class _NavigationRailDemoState extends State<_NavigationRailDemo> {
  int _selectedIndex = 0;

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        NavigationRail(
          selectedIndex: _selectedIndex,
          onDestinationSelected: (index) => setState(() => _selectedIndex = index),
          labelType: NavigationRailLabelType.all,
          destinations: const [
            NavigationRailDestination(icon: Icon(Icons.home_outlined), selectedIcon: Icon(Icons.home), label: Text('首页')),
            NavigationRailDestination(icon: Icon(Icons.search), label: Text('搜索')),
            NavigationRailDestination(icon: Icon(Icons.person_outline), selectedIcon: Icon(Icons.person), label: Text('我的')),
          ],
        ),
        Expanded(
          child: Center(child: Text('页面 ${_selectedIndex + 1}')),
        ),
      ],
    );
  }
}

/// NavigationBar 演示
class _NavigationBarDemo extends StatefulWidget {
  @override
  State<_NavigationBarDemo> createState() => _NavigationBarDemoState();
}

class _NavigationBarDemoState extends State<_NavigationBarDemo> {
  int _selectedIndex = 0;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        SizedBox(
          height: 60,
          child: Center(child: Text('当前页面: ${['首页', '发现', '消息', '我的'][_selectedIndex]}')),
        ),
        NavigationBar(
          selectedIndex: _selectedIndex,
          onDestinationSelected: (index) => setState(() => _selectedIndex = index),
          destinations: const [
            NavigationDestination(icon: Icon(Icons.home_outlined), selectedIcon: Icon(Icons.home), label: '首页'),
            NavigationDestination(icon: Icon(Icons.explore_outlined), selectedIcon: Icon(Icons.explore), label: '发现'),
            NavigationDestination(icon: Badge(child: Icon(Icons.message_outlined)), label: '消息'),
            NavigationDestination(icon: Icon(Icons.person_outline), selectedIcon: Icon(Icons.person), label: '我的'),
          ],
        ),
      ],
    );
  }
}

/// Stepper 演示
class _StepperDemo extends StatefulWidget {
  @override
  State<_StepperDemo> createState() => _StepperDemoState();
}

class _StepperDemoState extends State<_StepperDemo> {
  int _currentStep = 0;

  @override
  Widget build(BuildContext context) {
    return Stepper(
      currentStep: _currentStep,
      onStepContinue: () {
        if (_currentStep < 2) setState(() => _currentStep++);
      },
      onStepCancel: () {
        if (_currentStep > 0) setState(() => _currentStep--);
      },
      onStepTapped: (index) => setState(() => _currentStep = index),
      steps: const [
        Step(
          title: Text('第一步'),
          content: Text('填写基本信息'),
          isActive: true,
        ),
        Step(
          title: Text('第二步'),
          content: Text('上传照片'),
        ),
        Step(
          title: Text('第三步'),
          content: Text('确认提交'),
        ),
      ],
    );
  }
}

/// ExpansionPanel 演示
class _ExpansionPanelDemo extends StatefulWidget {
  @override
  State<_ExpansionPanelDemo> createState() => _ExpansionPanelDemoState();
}

class _ExpansionPanelDemoState extends State<_ExpansionPanelDemo> {
  final List<bool> _expanded = [false, false, false];

  @override
  Widget build(BuildContext context) {
    return ExpansionPanelList(
      expansionCallback: (index, isExpanded) {
        setState(() => _expanded[index] = !isExpanded);
      },
      children: [
        ExpansionPanel(
          headerBuilder: (context, isExpanded) => const ListTile(title: Text('面板 1')),
          body: const Padding(
            padding: EdgeInsets.all(16),
            child: Text('这是第一个面板的内容'),
          ),
          isExpanded: _expanded[0],
        ),
        ExpansionPanel(
          headerBuilder: (context, isExpanded) => const ListTile(title: Text('面板 2')),
          body: const Padding(
            padding: EdgeInsets.all(16),
            child: Text('这是第二个面板的内容'),
          ),
          isExpanded: _expanded[1],
        ),
        ExpansionPanel(
          headerBuilder: (context, isExpanded) => const ListTile(title: Text('面板 3')),
          body: const Padding(
            padding: EdgeInsets.all(16),
            child: Text('这是第三个面板的内容'),
          ),
          isExpanded: _expanded[2],
        ),
      ],
    );
  }
}

/// SearchAnchor 演示
class _SearchAnchorDemo extends StatefulWidget {
  @override
  State<_SearchAnchorDemo> createState() => _SearchAnchorDemoState();
}

class _SearchAnchorDemoState extends State<_SearchAnchorDemo> {
  final List<String> _suggestions = ['苹果', '香蕉', '橙子', '葡萄', '西瓜'];
  List<String> _filteredSuggestions = [];

  @override
  void initState() {
    super.initState();
    _filteredSuggestions = _suggestions;
  }

  @override
  Widget build(BuildContext context) {
    return SearchAnchor(
      builder: (context, controller) {
        return SearchBar(
          controller: controller,
          onTap: () => controller.openView(),
          onChanged: (_) => controller.openView(),
          leading: const Icon(Icons.search),
          hintText: '搜索水果',
        );
      },
      suggestionsBuilder: (context, controller) {
        final query = controller.text;
        _filteredSuggestions = _suggestions.where((s) => s.contains(query)).toList();
        return _filteredSuggestions.map((suggestion) => ListTile(
          title: Text(suggestion),
          onTap: () {
            controller.closeView(suggestion);
          },
        ));
      },
    );
  }
}
