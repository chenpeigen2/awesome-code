import 'dart:async';

import 'package:flutter/material.dart';

import '../rust/api/streaming.dart' as rust;
import '../rust/api/simple.dart' as rust_simple;

/// 流式任务演示：蒙特卡洛法估算 π。
///
/// Rust 侧通过 StreamSink 持续推送 ProgressEvent，
/// Dart 订阅流并实时刷新进度条与估算值 —— 展示 Rust → Dart 事件流。
class StreamPage extends StatefulWidget {
  const StreamPage({super.key});

  @override
  State<StreamPage> createState() => _StreamPageState();
}

class _StreamPageState extends State<StreamPage> {
  final _iterationsController = TextEditingController(text: '10000000');
  final _chunksController = TextEditingController(text: '100');

  StreamSubscription<rust.ProgressEvent>? _subscription;
  int _done = 0;
  int _total = 0;
  double? _partialEstimate;
  rust.ProgressEvent_Done? _final;
  bool _running = false;

  @override
  void initState() {
    super.initState();
    _loadEngineInfo();
  }

  @override
  void dispose() {
    _subscription?.cancel();
    _iterationsController.dispose();
    _chunksController.dispose();
    super.dispose();
  }

  rust_simple.EngineInfo? _engineInfo;

  Future<void> _loadEngineInfo() async {
    try {
      final info = rust_simple.engineInfo(); // sync 调用，零开销
      if (mounted) setState(() => _engineInfo = info);
    } catch (_) {
      // RustLib 未初始化（如纯 Widget 测试环境）时静默降级
    }
  }

  void _start() {
    final iterations = int.tryParse(_iterationsController.text) ?? 0;
    final chunks = int.tryParse(_chunksController.text) ?? 1;
    if (iterations <= 0 || iterations > 200_000_000_000) return;

    _subscription?.cancel();
    setState(() {
      _running = true;
      _done = 0;
      _total = iterations;
      _partialEstimate = null;
      _final = null;
    });

    _subscription = rust.monteCarloPi(iterations: iterations, chunks: chunks).listen((event) {
      if (!mounted) return;
      switch (event) {
        case rust.ProgressEvent_Progress(:final done, :final total, :final partialEstimate):
          setState(() {
            _done = done.toInt();
            _total = total.toInt();
            _partialEstimate = partialEstimate;
          });
        case rust.ProgressEvent_Done(:final estimate, :final absError, :final elapsedMs):
          setState(() {
            _partialEstimate = estimate;
            _final = event;
            _running = false;
          });
          // ignore: avoid_print
          debugPrint('π ≈ $estimate, 误差 $absError, 耗时 ${elapsedMs}ms');
      }
    }, onError: (Object e) {
      if (!mounted) return;
      setState(() => _running = false);
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('$e')));
    });
  }

  void _cancel() {
    _subscription?.cancel();
    setState(() => _running = false);
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final progress = _total > 0 ? (_done / _total).clamp(0.0, 1.0) : 0.0;

    return Scaffold(
      appBar: AppBar(title: const Text('流式任务 · 蒙特卡洛估算 π')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            if (_engineInfo != null)
              Card(
                child: Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                  child: Row(
                    children: [
                      Icon(Icons.memory, color: colorScheme.primary, size: 20),
                      const SizedBox(width: 8),
                      Expanded(
                        child: Text(
                          '${_engineInfo!.name} v${_engineInfo!.version} · '
                          '${_engineInfo!.target} · '
                          '${_engineInfo!.debugBuild ? "debug" : "release"} 构建',
                          style: TextStyle(
                              fontSize: 12, color: colorScheme.onSurfaceVariant),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            const SizedBox(height: 12),
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text('任务参数', style: TextStyle(fontWeight: FontWeight.bold)),
                    const SizedBox(height: 4),
                    Text(
                      '在单位正方形内均匀撒点，落入四分之一圆的比例 × 4 ≈ π。'
                      'Rust 在独立线程分块计算，每块完成即推送一次进度事件。',
                      style: TextStyle(
                          fontSize: 12, color: colorScheme.onSurfaceVariant),
                    ),
                    const SizedBox(height: 12),
                    Row(
                      children: [
                        Expanded(
                          child: TextField(
                            controller: _iterationsController,
                            keyboardType: TextInputType.number,
                            decoration: const InputDecoration(
                              labelText: '总采样数',
                              border: OutlineInputBorder(),
                              isDense: true,
                            ),
                          ),
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: TextField(
                            controller: _chunksController,
                            keyboardType: TextInputType.number,
                            decoration: const InputDecoration(
                              labelText: '分块数',
                              border: OutlineInputBorder(),
                              isDense: true,
                            ),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 12),
                    Row(
                      children: [
                        Expanded(
                          child: FilledButton.icon(
                            onPressed: _running ? null : _start,
                            icon: const Icon(Icons.play_arrow),
                            label: const Text('开始'),
                          ),
                        ),
                        const SizedBox(width: 12),
                        OutlinedButton.icon(
                          onPressed: _running ? _cancel : null,
                          icon: const Icon(Icons.stop),
                          label: const Text('取消订阅'),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 16),
            if (_running || _final != null)
              Card(
                color: _final != null
                    ? colorScheme.primaryContainer
                    : colorScheme.surfaceContainerHigh,
                child: Padding(
                  padding: const EdgeInsets.all(20),
                  child: Column(
                    children: [
                      Text(
                        _partialEstimate?.toStringAsFixed(6) ?? '—',
                        style: TextStyle(
                          fontSize: 40,
                          fontWeight: FontWeight.bold,
                          color: _final != null
                              ? colorScheme.onPrimaryContainer
                              : colorScheme.primary,
                          fontFeatures: const [FontFeature.tabularFigures()],
                        ),
                      ),
                      const SizedBox(height: 8),
                      ClipRRect(
                        borderRadius: BorderRadius.circular(8),
                        child: LinearProgressIndicator(
                          value: _final != null ? 1.0 : progress,
                          minHeight: 10,
                        ),
                      ),
                      const SizedBox(height: 8),
                      Text(
                        _final != null
                            ? '完成 · 耗时 ${_final!.elapsedMs} ms · 绝对误差 ${_final!.absError.toStringAsFixed(6)}'
                            : '$_done / $_total 采样',
                        style: TextStyle(
                            fontSize: 12, color: colorScheme.onSurfaceVariant),
                      ),
                    ],
                  ),
                ),
              ),
          ],
        ),
      ),
    );
  }
}
