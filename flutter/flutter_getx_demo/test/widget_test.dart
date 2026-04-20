import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_getx_demo/main.dart';

void main() {
  testWidgets('App smoke test', (WidgetTester tester) async {
    await tester.pumpWidget(const FlutterGetXDemo());
    expect(find.text('Flutter GetX Demo'), findsOneWidget);
  });
}
