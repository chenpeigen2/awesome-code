import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_rust_demo/main.dart';
import 'package:flutter_rust_demo/src/rust/frb_generated.dart';
import 'package:flutter_rust_demo/src/rust/api/simple.dart';
import 'package:integration_test/integration_test.dart';

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();
  setUpAll(() async => await RustLib.init());
  
  testWidgets('App loads correctly', (WidgetTester tester) async {
    await tester.pumpWidget(const CalculatorApp());
    expect(find.text('Flutter + Rust Bridge Demo'), findsOneWidget);
  });
  
  testWidgets('Calculator page is displayed', (WidgetTester tester) async {
    await tester.pumpWidget(const CalculatorApp());
    expect(find.text('异步计算器'), findsOneWidget);
  });

  test('Rust async calculation works', () async {
    final result = await calculateAsync(
      a: 2,
      b: 3,
      operation: Operation.add,
    );
    
    expect(result, isA<CalcResult_Success>());
    final successResult = result as CalcResult_Success;
    expect(successResult.value, equals(5));
  });

  test('Rust division by zero returns error', () async {
    final result = await calculateAsync(
      a: 1,
      b: 0,
      operation: Operation.divide,
    );
    
    expect(result, isA<CalcResult_Error>());
  });

  test('Batch calculation works', () async {
    final requests = [
      CalcRequest(a: 1, b: 2, operation: Operation.add),
      CalcRequest(a: 5, b: 3, operation: Operation.subtract),
      CalcRequest(a: 4, b: 2, operation: Operation.multiply),
    ];
    
    final results = await batchCalculate(requests: requests);
    
    expect(results.length, equals(3));
    expect((results[0] as CalcResult_Success).value, equals(3));
    expect((results[1] as CalcResult_Success).value, equals(2));
    expect((results[2] as CalcResult_Success).value, equals(8));
  });

  test('Parse operation works', () async {
    expect(await parseOperation(s: '+'), equals(Operation.add));
    expect(await parseOperation(s: '-'), equals(Operation.subtract));
    expect(await parseOperation(s: 'invalid'), isNull);
  });

  test('Get supported operations', () async {
    final ops = await getSupportedOperations();
    expect(ops.length, equals(4));
    expect(ops, contains('+'));
    expect(ops, contains('-'));
  });
}
