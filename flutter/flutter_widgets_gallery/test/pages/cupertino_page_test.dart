import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_widgets_gallery/pages/cupertino/cupertino_page.dart';

void main() {
  group('CupertinoPage Tests', () {
    testWidgets('页面正常渲染', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(home: Scaffold(body: CupertinoPage())),
      );
      await tester.pumpAndSettle();

      expect(find.byType(CupertinoPage), findsOneWidget);
    });

    testWidgets('包含 ListView 组件', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(home: Scaffold(body: CupertinoPage())),
      );
      await tester.pumpAndSettle();

      expect(find.byType(ListView), findsOneWidget);
    });

    testWidgets('页面可以滚动', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(home: Scaffold(body: CupertinoPage())),
      );
      await tester.pumpAndSettle();

      // 执行滚动操作
      await tester.drag(find.byType(ListView), const Offset(0, -300));
      await tester.pumpAndSettle();

      // 验证页面仍然正常
      expect(find.byType(CupertinoPage), findsOneWidget);
    });
  });
}
