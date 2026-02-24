import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_widgets_gallery/main.dart';

void main() {
  group('Main App Tests', () {
    testWidgets('应用正常启动', (WidgetTester tester) async {
      await tester.pumpWidget(const FlutterWidgetGalleryApp());
      await tester.pumpAndSettle();

      expect(find.byType(MaterialApp), findsOneWidget);
    });

    testWidgets('应用包含 HomePage', (WidgetTester tester) async {
      await tester.pumpWidget(const FlutterWidgetGalleryApp());
      await tester.pumpAndSettle();

      // 验证主页面存在
      expect(find.text('Flutter Widget Gallery'), findsWidgets);
    });

    testWidgets('应用支持深色/浅色主题', (WidgetTester tester) async {
      await tester.pumpWidget(const FlutterWidgetGalleryApp());
      await tester.pumpAndSettle();

      // 验证 MaterialApp 存在主题配置
      final materialApp = tester.widget<MaterialApp>(find.byType(MaterialApp));
      expect(materialApp.theme, isNotNull);
      expect(materialApp.darkTheme, isNotNull);
    });
  });
}