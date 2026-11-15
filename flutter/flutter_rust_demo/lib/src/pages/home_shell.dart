import 'package:flutter/material.dart';

import 'mandelbrot_page.dart';
import 'matrix_page.dart';
import 'primes_page.dart';
import 'stream_page.dart';
import 'text_page.dart';

/// 应用外壳：窄屏用 NavigationBar，宽屏用 NavigationRail。
///
/// 各页面状态通过 [GlobalKey] 保持，切换标签不丢失。
class HomeShell extends StatefulWidget {
  const HomeShell({super.key});

  @override
  State<HomeShell> createState() => HomeShellState();
}

class HomeShellState extends State<HomeShell> {
  int selectedIndex = 0;

  final _pages = const [
    MandelbrotPage(),
    PrimesPage(),
    MatrixPage(),
    TextPage(),
    StreamPage(),
  ];

  static const _destinations = [
    NavigationDestination(
      icon: Icon(Icons.grain_outlined),
      selectedIcon: Icon(Icons.grain),
      label: '分形',
    ),
    NavigationDestination(
      icon: Icon(Icons.filter_9_plus_outlined),
      selectedIcon: Icon(Icons.filter_9_plus),
      label: '素数',
    ),
    NavigationDestination(
      icon: Icon(Icons.grid_on_outlined),
      selectedIcon: Icon(Icons.grid_on),
      label: '矩阵',
    ),
    NavigationDestination(
      icon: Icon(Icons.text_fields_outlined),
      selectedIcon: Icon(Icons.text_fields),
      label: '文本',
    ),
    NavigationDestination(
      icon: Icon(Icons.stream_outlined),
      selectedIcon: Icon(Icons.stream),
      label: '流式',
    ),
  ];

  @override
  Widget build(BuildContext context) {
    final isWide = MediaQuery.sizeOf(context).width >= 700;

    if (isWide) {
      return Scaffold(
        body: Row(
          children: [
            NavigationRail(
              selectedIndex: selectedIndex,
              onDestinationSelected: (i) => setState(() => selectedIndex = i),
              labelType: NavigationRailLabelType.all,
              leading: const Padding(
                padding: EdgeInsets.symmetric(vertical: 16),
                child: _Logo(),
              ),
              destinations: [
                for (final d in _destinations)
                  NavigationRailDestination(
                    icon: d.icon,
                    selectedIcon: d.selectedIcon,
                    label: Text(d.label!),
                  ),
              ],
            ),
            const VerticalDivider(thickness: 1, width: 1),
            Expanded(child: IndexedStack(index: selectedIndex, children: _pages)),
          ],
        ),
      );
    }

    return Scaffold(
      body: IndexedStack(index: selectedIndex, children: _pages),
      bottomNavigationBar: NavigationBar(
        selectedIndex: selectedIndex,
        onDestinationSelected: (i) => setState(() => selectedIndex = i),
        destinations: _destinations,
      ),
    );
  }
}

class _Logo extends StatelessWidget {
  const _Logo();

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    return Container(
      width: 40,
      height: 40,
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [colorScheme.primary, colorScheme.tertiary],
        ),
        borderRadius: BorderRadius.circular(12),
      ),
      alignment: Alignment.center,
      child: Text(
        'R',
        style: TextStyle(
          color: colorScheme.onPrimary,
          fontWeight: FontWeight.bold,
          fontSize: 20,
        ),
      ),
    );
  }
}
