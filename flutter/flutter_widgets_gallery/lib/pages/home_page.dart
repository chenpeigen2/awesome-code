import 'package:flutter/material.dart';
import 'basics/basics_page.dart';
import 'layout/layout_page.dart';
import 'container/container_page.dart';
import 'scroll/scroll_page.dart';
import 'interactive/interactive_page.dart';
import 'animation/animation_page.dart';
import 'material/material_page.dart' as material_page;
import 'form/form_page.dart';
import 'dialog/dialog_page.dart';
import 'advanced/advanced_page.dart';
import 'cupertino/cupertino_page.dart';

/// 主页面
class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  int _currentIndex = 0;
  final PageController _pageController = PageController();

  final List<Widget> _pages = const [
    BasicsPage(),
    LayoutPage(),
    ContainerPage(),
    ScrollPage(),
    InteractivePage(),
    AnimationPage(),
    material_page.MaterialPage(),
    FormPage(),
    DialogPage(),
    AdvancedPage(),
    CupertinoPage(),
  ];

  final List<_NavItem> _navItems = const [
    _NavItem(icon: Icons.widgets, label: '基础', color: Colors.blue),
    _NavItem(icon: Icons.view_quilt, label: '布局', color: Colors.green),
    _NavItem(icon: Icons.crop_square, label: '容器', color: Colors.orange),
    _NavItem(icon: Icons.view_stream, label: '滚动', color: Colors.purple),
    _NavItem(icon: Icons.touch_app, label: '交互', color: Colors.red),
    _NavItem(icon: Icons.animation, label: '动画', color: Colors.teal),
    _NavItem(icon: Icons.palette, label: 'Material', color: Colors.indigo),
    _NavItem(icon: Icons.edit_note, label: '表单', color: Colors.pink),
    _NavItem(icon: Icons.chat_bubble, label: '弹窗', color: Colors.amber),
    _NavItem(icon: Icons.star, label: '高级', color: Colors.cyan),
    _NavItem(icon: Icons.phone_iphone, label: 'Cupertino', color: Colors.deepPurple),
  ];

  @override
  void dispose() {
    _pageController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      drawer: _buildDrawer(context),
      body: NestedScrollView(
        headerSliverBuilder: (context, innerBoxIsScrolled) => [
          _buildSliverAppBar(context),
        ],
        body: PageView(
          controller: _pageController,
          onPageChanged: (index) {
            setState(() => _currentIndex = index);
          },
          children: _pages,
        ),
      ),
      bottomNavigationBar: _buildBottomNav(),
      floatingActionButton: _buildFAB(context),
    );
  }

  Widget _buildSliverAppBar(BuildContext context) {
    return SliverAppBar(
      expandedHeight: 180,
      floating: false,
      pinned: true,
      flexibleSpace: FlexibleSpaceBar(
        title: Text(
          _navItems[_currentIndex].label,
          style: const TextStyle(fontWeight: FontWeight.bold),
        ),
        background: Container(
          decoration: BoxDecoration(
            gradient: LinearGradient(
              colors: [
                _navItems[_currentIndex].color.withOpacity(0.7),
                _navItems[_currentIndex].color.withOpacity(0.3),
              ],
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
            ),
          ),
          child: Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(
                  _navItems[_currentIndex].icon,
                  size: 48,
                  color: Colors.white,
                ),
                const SizedBox(height: 8),
                const Text(
                  'Flutter Widget Gallery',
                  style: TextStyle(
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                    color: Colors.white,
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildDrawer(BuildContext context) {
    return Drawer(
      child: ListView(
        padding: EdgeInsets.zero,
        children: [
          DrawerHeader(
            decoration: BoxDecoration(
              gradient: LinearGradient(
                colors: [
                  Theme.of(context).colorScheme.primary,
                  Theme.of(context).colorScheme.primaryContainer,
                ],
              ),
            ),
            child: const Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                Icon(Icons.widgets, size: 48, color: Colors.white),
                SizedBox(height: 12),
                Text(
                  'Flutter Widget Gallery',
                  style: TextStyle(
                    fontSize: 22,
                    fontWeight: FontWeight.bold,
                    color: Colors.white,
                  ),
                ),
                SizedBox(height: 4),
                Text(
                  '学习 Flutter 所有 Widget',
                  style: TextStyle(color: Colors.white70),
                ),
              ],
            ),
          ),
          ...List.generate(_navItems.length, (index) {
            final item = _navItems[index];
            return ListTile(
              leading: Icon(item.icon, color: item.color),
              title: Text(item.label),
              selected: _currentIndex == index,
              selectedTileColor: item.color.withOpacity(0.1),
              onTap: () {
                setState(() => _currentIndex = index);
                _pageController.jumpToPage(index);
                Navigator.pop(context);
              },
            );
          }),
          const Divider(),
          ListTile(
            leading: const Icon(Icons.info_outline),
            title: const Text('关于'),
            onTap: () {
              Navigator.pop(context);
              showAboutDialog(
                context: context,
                applicationName: 'Flutter Widget Gallery',
                applicationVersion: '1.0.0',
                applicationLegalese: '© 2024 Flutter Widget Gallery',
                children: const [
                  SizedBox(height: 16),
                  Text('这是一个包含 Flutter 所有 Widget 示例的学习应用。'),
                ],
              );
            },
          ),
        ],
      ),
    );
  }

  Widget _buildBottomNav() {
    return BottomNavigationBar(
      type: BottomNavigationBarType.fixed,
      currentIndex: _currentIndex,
      selectedItemColor: _navItems[_currentIndex].color,
      unselectedItemColor: Colors.grey,
      showUnselectedLabels: true,
      onTap: (index) {
        setState(() => _currentIndex = index);
        _pageController.jumpToPage(index);
      },
      items: _navItems.map((item) {
        return BottomNavigationBarItem(
          icon: Icon(item.icon),
          label: item.label,
        );
      }).toList(),
    );
  }

  Widget _buildFAB(BuildContext context) {
    return FloatingActionButton.extended(
      onPressed: () {
        _showQuickSearch(context);
      },
      icon: const Icon(Icons.search),
      label: const Text('搜索 Widget'),
    );
  }

  void _showQuickSearch(BuildContext context) {
    showSearch(
      context: context,
      delegate: _WidgetSearchDelegate(_navItems, _pageController),
    );
  }
}

class _NavItem {
  final IconData icon;
  final String label;
  final Color color;

  const _NavItem({
    required this.icon,
    required this.label,
    required this.color,
  });
}

class _WidgetSearchDelegate extends SearchDelegate<String> {
  final List<_NavItem> navItems;
  final PageController pageController;

  _WidgetSearchDelegate(this.navItems, this.pageController);

  @override
  List<Widget> buildActions(BuildContext context) {
    return [
      IconButton(
        icon: const Icon(Icons.clear),
        onPressed: () {
          query = '';
        },
      ),
    ];
  }

  @override
  Widget buildLeading(BuildContext context) {
    return IconButton(
      icon: const Icon(Icons.arrow_back),
      onPressed: () {
        close(context, '');
      },
    );
  }

  @override
  Widget buildResults(BuildContext context) {
    return _buildSearchResults(context);
  }

  @override
  Widget buildSuggestions(BuildContext context) {
    return _buildSearchResults(context);
  }

  Widget _buildSearchResults(BuildContext context) {
    final results = navItems
        .where((item) =>
            item.label.toLowerCase().contains(query.toLowerCase()))
        .toList();

    if (results.isEmpty) {
      return const Center(
        child: Text('未找到相关 Widget'),
      );
    }

    return ListView.builder(
      itemCount: results.length,
      itemBuilder: (context, index) {
        final item = results[index];
        return ListTile(
          leading: Icon(item.icon, color: item.color),
          title: Text(item.label),
          onTap: () {
            final originalIndex = navItems.indexOf(item);
            pageController.jumpToPage(originalIndex);
            close(context, item.label);
          },
        );
      },
    );
  }
}
