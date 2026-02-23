import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_rust_bridge/flutter_rust_bridge_for_generated.dart';
import 'package:flutter_rust_demo/src/rust/api/simple.dart';
import 'package:flutter_rust_demo/src/rust/frb_generated.dart';

void main() {
  group('Rust FFI Tests', () {
    setUpAll(() async {
      await RustLib.init();
    });

    group('基础四则运算', () {
      test('加法运算', () async {
        final result = await createRecord(a: 5, b: 3, operation: Operation.add);
        expect(result.result, isA<CalcResult_Success>());
        final success = result.result as CalcResult_Success;
        expect(success.value, equals(8));
      });

      test('减法运算', () async {
        final result = await createRecord(a: 10, b: 4, operation: Operation.subtract);
        expect(result.result, isA<CalcResult_Success>());
        final success = result.result as CalcResult_Success;
        expect(success.value, equals(6));
      });

      test('乘法运算', () async {
        final result = await createRecord(a: 6, b: 7, operation: Operation.multiply);
        expect(result.result, isA<CalcResult_Success>());
        final success = result.result as CalcResult_Success;
        expect(success.value, equals(42));
      });

      test('除法运算', () async {
        final result = await createRecord(a: 20, b: 4, operation: Operation.divide);
        expect(result.result, isA<CalcResult_Success>());
        final success = result.result as CalcResult_Success;
        expect(success.value, equals(5));
      });
    });

    group('边界情况测试', () {
      test('除以零应返回错误', () async {
        final result = await createRecord(a: 10, b: 0, operation: Operation.divide);
        expect(result.result, isA<CalcResult_Error>());
      });

      test('零除以数字', () async {
        final result = await createRecord(a: 0, b: 5, operation: Operation.divide);
        expect(result.result, isA<CalcResult_Success>());
        final success = result.result as CalcResult_Success;
        expect(success.value, equals(0));
      });

      test('负数运算', () async {
        final result = await createRecord(a: -5, b: 3, operation: Operation.add);
        expect(result.result, isA<CalcResult_Success>());
        final success = result.result as CalcResult_Success;
        expect(success.value, equals(-2));
      });

      test('两个负数相乘', () async {
        final result = await createRecord(a: -3, b: -4, operation: Operation.multiply);
        expect(result.result, isA<CalcResult_Success>());
        final success = result.result as CalcResult_Success;
        expect(success.value, equals(12));
      });

      test('小数运算', () async {
        final result = await createRecord(a: 1.5, b: 2.5, operation: Operation.add);
        expect(result.result, isA<CalcResult_Success>());
        final success = result.result as CalcResult_Success;
        expect(success.value, closeTo(4.0, 0.0001));
      });
    });

    group('性能测试', () {
      test('heavyComputation 应返回有效结果', () async {
        final result = await heavyComputation(iterations: 100);
        expect(result, isNotNull);
        expect(result, isA<double>());
      });

      test('多次调用应返回一致结果', () async {
        final result1 = await heavyComputation(iterations: 100);
        final result2 = await heavyComputation(iterations: 100);
        expect(result1, closeTo(result2, 0.0001));
      });
    });

    group('统计功能测试', () {
      test('空历史记录统计', () async {
        final stats = await computeStats(records: []);
        expect(stats.totalCalculations, equals(0));
        expect(stats.successCount, equals(0));
        expect(stats.errorCount, equals(0));
      });

      test('单条成功记录统计', () async {
        final record = await createRecord(a: 5, b: 3, operation: Operation.add);
        final stats = await computeStats(records: [record]);
        expect(stats.totalCalculations, equals(1));
        expect(stats.successCount, equals(1));
        expect(stats.errorCount, equals(0));
      });

      test('包含错误记录的统计', () async {
        final successRecord = await createRecord(a: 5, b: 3, operation: Operation.add);
        final errorRecord = await createRecord(a: 10, b: 0, operation: Operation.divide);
        final stats = await computeStats(records: [successRecord, errorRecord]);
        expect(stats.totalCalculations, equals(2));
        expect(stats.successCount, equals(1));
        expect(stats.errorCount, equals(1));
      });

      test('平均值计算', () async {
        final r1 = await createRecord(a: 10, b: 0, operation: Operation.add);
        final r2 = await createRecord(a: 20, b: 0, operation: Operation.add);
        final r3 = await createRecord(a: 30, b: 0, operation: Operation.add);
        final stats = await computeStats(records: [r1, r2, r3]);
        expect(stats.averageValue, closeTo(20.0, 0.0001));
      });
    });

    group('记录属性测试', () {
      test('记录包含正确的数值', () async {
        final record = await createRecord(a: 7.5, b: 2.5, operation: Operation.multiply);
        expect(record.a, equals(7.5));
        expect(record.b, equals(2.5));
        expect(record.operation, equals(Operation.multiply));
      });

      test('记录包含时间戳', () async {
        final record = await createRecord(a: 1, b: 1, operation: Operation.add);
        expect(record.timestamp, isNotNull);
      });
    });
  });
}
