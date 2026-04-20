import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../controllers/theme_controller.dart';
import '../routes/app_routes.dart';

class _Category {
  final String title;
  final String subtitle;
  final IconData icon;
  final Color color;
  final String route;
  const _Category(this.title, this.subtitle, this.icon, this.color, this.route);
}

class HomeView extends StatelessWidget {
  const HomeView({super.key});

  static const _categories = [
    _Category('基础组件', 'Text, Icon, Container, Button', Icons.text_fields, Colors.blue, AppRoutes.basics),
    _Category('布局组件', 'Row, Column, Stack, Wrap, Expanded', Icons.view_quilt, Colors.green, AppRoutes.layout),
    _Category('容器组件', 'Card, Padding, DecoratedBox, Transform', Icons.crop_square, Colors.orange, AppRoutes.containers),
    _Category('滚动组件', 'ListView, GridView, PageView, Slivers', Icons.view_stream, Colors.purple, AppRoutes.scroll),
    _Category('交互组件', 'Gesture, Dismissible, Draggable, Slider', Icons.touch_app, Colors.red, AppRoutes.interactive),
    _Category('动画组件', 'AnimatedContainer, Hero, Tween', Icons.animation, Colors.teal, AppRoutes.animation),
    _Category('表单组件', 'TextField, Checkbox, Radio, Dropdown', Icons.edit_note, Colors.pink, AppRoutes.form),
    _Category('弹窗组件', 'AlertDialog, BottomSheet, Snackbar', Icons.chat_bubble, Colors.amber, AppRoutes.dialog),
    _Category('Material', 'AppBar, TabBar, Drawer, Chip, DataTable', Icons.palette, Colors.indigo, AppRoutes.material),
    _Category('高级组件', 'FutureBuilder, StreamBuilder, CustomPaint', Icons.star, Colors.cyan, AppRoutes.advanced),
    _Category('GetX 特性', '状态管理, Workers, 导航, DI, 国际化', Icons.settings_suggest, Colors.deepPurple, AppRoutes.getxFeatures),
  ];

  @override
  Widget build(BuildContext context) {
    final themeCtrl = Get.find<ThemeController>();
    return Scaffold(
      appBar: AppBar(
        title: const Text('Flutter GetX Demo'),
        actions: [
          IconButton(icon: const Icon(Icons.language), onPressed: themeCtrl.switchLocale),
          IconButton(
            icon: Obx(() => Icon(themeCtrl.themeMode.value == ThemeMode.dark ? Icons.light_mode : Icons.dark_mode)),
            onPressed: themeCtrl.toggleTheme,
          ),
        ],
      ),
      body: GridView.builder(
        padding: const EdgeInsets.all(16),
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 2,
          childAspectRatio: 1.1,
          crossAxisSpacing: 12,
          mainAxisSpacing: 12,
        ),
        itemCount: _categories.length,
        itemBuilder: (context, index) {
          final c = _categories[index];
          return Card(
            clipBehavior: Clip.antiAlias,
            child: InkWell(
              onTap: () => Get.toNamed(c.route),
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Container(
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(color: c.color.withValues(alpha: 0.15), shape: BoxShape.circle),
                      child: Icon(c.icon, size: 32, color: c.color),
                    ),
                    const SizedBox(height: 12),
                    Text(c.title, style: const TextStyle(fontSize: 15, fontWeight: FontWeight.bold)),
                    const SizedBox(height: 4),
                    Text(c.subtitle, style: TextStyle(fontSize: 11, color: Colors.grey[600]), textAlign: TextAlign.center, maxLines: 2, overflow: TextOverflow.ellipsis),
                  ],
                ),
              ),
            ),
          );
        },
      ),
    );
  }
}

/// Demo page scaffold used by all demo pages
class DemoScaffold extends StatelessWidget {
  final String title;
  final List<Widget> children;
  const DemoScaffold({super.key, required this.title, required this.children});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(title)),
      body: ListView(padding: const EdgeInsets.only(bottom: 24), children: children),
    );
  }
}

/// Section header
class Section extends StatelessWidget {
  final String title;
  const Section(this.title, {super.key});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 20, 16, 8),
      child: Text(title, style: Theme.of(context).textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold, color: Theme.of(context).colorScheme.primary)),
    );
  }
}

/// Demo card
class DemoCard extends StatelessWidget {
  final String title;
  final String? desc;
  final Widget child;
  const DemoCard({super.key, required this.title, this.desc, required this.child});

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Text(title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
          if (desc != null) Padding(padding: const EdgeInsets.only(top: 4), child: Text(desc!, style: TextStyle(fontSize: 12, color: Colors.grey[600]))),
          const SizedBox(height: 12),
          Center(child: child),
        ]),
      ),
    );
  }
}
