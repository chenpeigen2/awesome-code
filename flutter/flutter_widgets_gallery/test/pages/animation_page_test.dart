import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_widgets_gallery/pages/animation/animation_page.dart';

void main() {
  group('AnimationPage Tests', () {
    testWidgets('页面正常渲染', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(home: Scaffold(body: AnimationPage())),
      );
      await tester.pumpAndSettle();

      expect(find.byType(AnimationPage), findsOneWidget);
    });

    testWidgets('包含 ListView 组件', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(home: Scaffold(body: AnimationPage())),
      );
      await tester.pumpAndSettle();

      expect(find.byType(ListView), findsOneWidget);
    });

    testWidgets('包含 AnimatedContainer 示例标题', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(home: Scaffold(body: AnimationPage())),
      );
      await tester.pumpAndSettle();

      // 使用 findsWidgets 因为可能有多个匹配
      expect(find.text('AnimatedContainer'), findsWidgets);
    });

    testWidgets('包含 AnimatedOpacity 示例标题', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(home: Scaffold(body: AnimationPage())),
      );
      await tester.pumpAndSettle();

      // 使用 findsWidgets 因为可能有多个匹配
      expect(find.text('AnimatedOpacity'), findsWidgets);
    });

    testWidgets('页面可以滚动', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(home: Scaffold(body: AnimationPage())),
      );
      await tester.pumpAndSettle();

      // 执行滚动操作
      await tester.drag(find.byType(ListView), const Offset(0, -300));
      await tester.pumpAndSettle();

      // 验证页面仍然正常
      expect(find.byType(AnimationPage), findsOneWidget);
    });
  });
}
