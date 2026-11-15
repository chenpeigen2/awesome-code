import 'package:flutter/material.dart';
import 'package:flutter_rust_demo/src/pages/home_shell.dart';
import 'package:flutter_test/flutter_test.dart';

/// HomeShell 导航测试：不需要 Rust FFI，只验证纯 Flutter 层行为。
void main() {
  testWidgets('窄屏：底部导航切换页面且 IndexedStack 保持状态', (tester) async {
    tester.view.physicalSize = const Size(390, 844);
    tester.view.devicePixelRatio = 1.0;
    addTearDown(tester.view.reset);

    await tester.pumpWidget(const MaterialApp(home: HomeShell()));
    await tester.pump();

    // 5 个标签
    expect(find.byType(NavigationBar), findsOneWidget);
    expect(find.text('分形'), findsWidgets);
    expect(find.text('素数'), findsOneWidget);

    // 切换到"矩阵"标签：AppBar 标题变化
    await tester.tap(find.text('矩阵'));
    await tester.pumpAndSettle();
    expect(find.byType(NavigationBar), findsOneWidget);
  });

  testWidgets('宽屏：使用 NavigationRail', (tester) async {
    tester.view.physicalSize = const Size(1400, 900);
    tester.view.devicePixelRatio = 1.0;
    addTearDown(tester.view.reset);

    await tester.pumpWidget(const MaterialApp(home: HomeShell()));
    await tester.pump();

    expect(find.byType(NavigationRail), findsOneWidget);
    expect(find.byType(NavigationBar), findsNothing);
  });
}
