import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

/// 测试工具类
class TestUtils {
  /// 创建测试用的 MaterialApp 包装器
  static Widget wrapWithMaterial(Widget widget) {
    return MaterialApp(
      home: Scaffold(body: widget),
    );
  }

  /// 验证 Widget 是否正常渲染
  static void expectRenders(WidgetTester tester, Widget widget) {
    tester.widget(find.byWidget(widget));
  }

  /// 查找并点击按钮
  static Future<void> tapButton(
    WidgetTester tester,
    Finder finder, {
    int tapCount = 1,
  }) async {
    for (int i = 0; i < tapCount; i++) {
      await tester.tap(finder);
      await tester.pumpAndSettle();
    }
  }

  /// 查找并输入文本
  static Future<void> enterText(
    WidgetTester tester,
    Finder finder,
    String text,
  ) async {
    await tester.enterText(finder, text);
    await tester.pumpAndSettle();
  }

  /// 滚动列表到指定位置
  static Future<void> scrollList(
    WidgetTester tester,
    Finder finder,
    double offset,
  ) async {
    await tester.drag(finder, Offset(0, -offset));
    await tester.pumpAndSettle();
  }

  /// 验证页面是否包含指定的文本
  static void expectText(WidgetTester tester, String text) {
    expect(find.text(text), findsOneWidget);
  }

  /// 验证页面是否包含指定的 Widget 类型
  static void expectWidgetType<T extends Widget>(WidgetTester tester) {
    expect(find.byType(T), findsWidgets);
  }

  /// 验证页面包含指定数量的 Widget
  static void expectWidgetCount<T extends Widget>(
    WidgetTester tester,
    int count,
  ) {
    expect(find.byType(T), findsNWidgets(count));
  }

  /// 验证 SnackBar 是否显示
  static void expectSnackBar(WidgetTester tester, String text) {
    expect(find.text(text), findsOneWidget);
    expect(find.byType(SnackBar), findsOneWidget);
  }
}

/// 页面测试基类
abstract class PageTester {
  /// 页面标题
  String get pageTitle;

  /// 创建测试页面
  Widget createPage();

  /// 测试页面是否正常渲染
  Future<void> testPageRenders(WidgetTester tester) async {
    await tester.pumpWidget(MaterialApp(home: createPage()));
    await tester.pumpAndSettle();
    expect(find.byType(createPage().runtimeType), findsOneWidget);
  }

  /// 测试页面是否包含关键组件
  Future<void> testContainsKeyWidgets(WidgetTester tester);
}
