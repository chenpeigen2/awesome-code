import 'package:flutter/material.dart';
import 'package:flutter_rust_demo/main.dart';
import 'package:flutter_rust_demo/src/rust/api/primes.dart' as primes;
import 'package:flutter_rust_demo/src/rust/api/simple.dart' as simple;
import 'package:flutter_rust_demo/src/rust/api/streaming.dart' as streaming;
import 'package:flutter_rust_demo/src/rust/frb_generated.dart';
import 'package:flutter_test/flutter_test.dart';

/// 集成测试：在真机/模拟器上运行（`flutter test integration_test`），
/// 验证 FFI 通路在新 API 下完整可用。
void main() {
  setUpAll(() async {
    await RustLib.init();
  });

  testWidgets('应用能启动并渲染首页', (tester) async {
    await tester.pumpWidget(const RustLabApp());
    await tester.pumpAndSettle();
    expect(find.text('Mandelbrot 分形'), findsOneWidget);
  });

  group('Rust FFI 基础通路', () {
    test('引擎信息（sync 调用）', () {
      final info = simple.engineInfo();
      expect(info.name, 'rust-lab-engine');
    });

    test('素数筛计数符合 OEIS A000720', () async {
      final stats = await primes.sieveStats(limit: BigInt.from(10000));
      expect(stats.count, 1229);
      expect(stats.largest, 9973);
    });

    test('第 1000 个素数是 7919', () async {
      expect(await primes.nthPrime(n: BigInt.from(1000)), BigInt.from(7919));
    });

    test('流式任务推送进度并完成', () async {
      final events = <streaming.ProgressEvent>[];
      final sub = streaming.monteCarloPi(iterations: 20000, chunks: 4).listen(events.add);
      // 等待流自然结束（Done 事件）
      await Future.delayed(const Duration(seconds: 10));
      await sub.cancel();
      expect(events, isNotEmpty);
      expect(events.last, isA<streaming.ProgressEvent_Done>());
      final done = events.last as streaming.ProgressEvent_Done;
      // 20 万采样的统计界足够宽松
      expect((done.estimate - 3.14159).abs(), lessThan(0.1));
    });
  });
}
