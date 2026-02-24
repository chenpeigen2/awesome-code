import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_widgets_gallery/pages/scroll/scroll_page.dart';

void main() {
  group('ScrollPage Tests', () {
    testWidgets('页面正常渲染', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(home: Scaffold(body: ScrollPage())),
      );
      await tester.pumpAndSettle();

      expect(find.byType(ScrollPage), findsOneWidget);
    });

    testWidgets('包含 ListView 组件', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(home: Scaffold(body: ScrollPage())),
      );
      await tester.pumpAndSettle();

      // 使用 findsWidgets 因为页面内部可能有多个 ListView
      expect(find.byType(ListView), findsWidgets);
    });

    testWidgets('页面可以滚动', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(home: Scaffold(body: ScrollPage())),
      );
      await tester.pumpAndSettle();

      // 执行滚动操作
      await tester.drag(find.byType(ListView).first, const Offset(0, -300));
      await tester.pumpAndSettle();

      // 验证页面仍然正常
      expect(find.byType(ScrollPage), findsOneWidget);
    });
  });
}