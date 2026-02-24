import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_widgets_gallery/pages/home_page.dart';
import 'package:flutter_widgets_gallery/pages/basics/basics_page.dart';
import 'package:flutter_widgets_gallery/pages/layout/layout_page.dart';
import 'package:flutter_widgets_gallery/pages/container/container_page.dart';

void main() {
  group('HomePage Tests', () {
    testWidgets('页面正常渲染', (WidgetTester tester) async {
      await tester.pumpWidget(const MaterialApp(home: HomePage()));
      await tester.pumpAndSettle();

      expect(find.byType(HomePage), findsOneWidget);
    });

    testWidgets('包含 Scaffold 组件', (WidgetTester tester) async {
      await tester.pumpWidget(const MaterialApp(home: HomePage()));
      await tester.pumpAndSettle();

      expect(find.byType(Scaffold), findsOneWidget);
    });

    testWidgets('包含 BottomNavigationBar', (WidgetTester tester) async {
      await tester.pumpWidget(const MaterialApp(home: HomePage()));
      await tester.pumpAndSettle();

      expect(find.byType(BottomNavigationBar), findsOneWidget);
    });

    testWidgets('包含 PageView', (WidgetTester tester) async {
      await tester.pumpWidget(const MaterialApp(home: HomePage()));
      await tester.pumpAndSettle();

      expect(find.byType(PageView), findsOneWidget);
    });

    testWidgets('默认显示基础页面', (WidgetTester tester) async {
      await tester.pumpWidget(const MaterialApp(home: HomePage()));
      await tester.pumpAndSettle();

      expect(find.byType(BasicsPage), findsOneWidget);
    });

    testWidgets('底部导航可以切换到布局页面', (WidgetTester tester) async {
      await tester.pumpWidget(const MaterialApp(home: HomePage()));
      await tester.pumpAndSettle();

      // 切换到布局页面
      await tester.tap(find.text('布局'));
      await tester.pumpAndSettle();
      expect(find.byType(LayoutPage), findsOneWidget);
    });

    testWidgets('底部导航可以切换到容器页面', (WidgetTester tester) async {
      await tester.pumpWidget(const MaterialApp(home: HomePage()));
      await tester.pumpAndSettle();

      // 切换到容器页面
      await tester.tap(find.text('容器'));
      await tester.pumpAndSettle();
      expect(find.byType(ContainerPage), findsOneWidget);
    });

    testWidgets('可以滑动 PageView 切换页面', (WidgetTester tester) async {
      await tester.pumpWidget(const MaterialApp(home: HomePage()));
      await tester.pumpAndSettle();

      // 向左滑动切换到下一个页面
      await tester.drag(find.byType(PageView), const Offset(-500, 0));
      await tester.pumpAndSettle();

      // 现在应该在布局页面
      expect(find.byType(LayoutPage), findsOneWidget);
    });
  });
}