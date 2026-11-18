import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_getx_demo/main.dart';
import 'package:flutter_getx_demo/app/controllers/theme_controller.dart';
import 'package:get/get.dart';

void main() {
  setUp(() {
    Get.testMode = true;
    // main() 里通过 Get.put 注册 ThemeController，测试需要复现这一步
    Get.put(ThemeController());
  });

  tearDown(Get.reset);

  testWidgets('App smoke test', (WidgetTester tester) async {
    await tester.pumpWidget(const FlutterGetXDemo());
    expect(find.text('Flutter GetX Demo'), findsOneWidget);
  });
}
