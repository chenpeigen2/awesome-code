import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_widgets_gallery/pages/material/material_page.dart' as mat;

void main() {
  group('MaterialPage Tests', () {
    testWidgets('页面正常渲染', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(home: Scaffold(body: mat.MaterialPage())),
      );
      await tester.pumpAndSettle();

      expect(find.byType(mat.MaterialPage), findsOneWidget);
    });

    testWidgets('包含 ListView 组件', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(home: Scaffold(body: mat.MaterialPage())),
      );
      await tester.pumpAndSettle();

      expect(find.byType(ListView), findsWidgets);
    });
  });
}
