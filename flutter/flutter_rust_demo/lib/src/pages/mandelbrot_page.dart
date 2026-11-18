import 'dart:async';
import 'dart:ui' as ui;

import 'package:flutter/material.dart';

import '../rust/api/mandelbrot.dart' as rust;

/// Mandelbrot 分形渲染页。
///
/// Rust 计算整个视口的 RGBA 像素缓冲，Flutter 只负责展示与交互：
/// 点击画面 → 以点击点为中心放大；预设视点可跳转到经典取景。
class MandelbrotPage extends StatefulWidget {
  const MandelbrotPage({super.key});

  @override
  State<MandelbrotPage> createState() => _MandelbrotPageState();
}

class _MandelbrotPageState extends State<MandelbrotPage> {
  double _centerX = -0.75;
  double _centerY = 0.0;
  double _scale = 3.0;
  double _maxIter = 256;
  int _renderWidth = 480;

  rust.MandelbrotResult? _result;
  ui.Image? _image;
  bool _loading = false;
  String? _error;

  /// 经典取景预设：名称 → (centerX, centerY, scale)
  static const _presets = <String, (double, double, double)>{
    '全景': (-0.75, 0.0, 3.0),
    '海马谷': (-0.743643887037151, 0.13182590420533, 0.005),
    '大象谷': (0.2820, 0.01, 0.02),
    '螺旋': (-0.7269, 0.1889, 0.01),
    '迷你曼德博': (-1.7687, 0.0017, 0.04),
  };

  String _currentPreset = '全景';

  @override
  void initState() {
    super.initState();
    _render();
  }

  Future<void> _render() async {
    setState(() {
      _loading = true;
      _error = null;
    });
    try {
      final aspect = _canvasHeight / _renderWidth;
      final result = await rust.renderMandelbrot(
        config: rust.MandelbrotConfig(
          width: _renderWidth,
          height: (_renderWidth * aspect).round().clamp(1, 4096),
          centerX: _centerX,
          centerY: _centerY,
          scale: _scale,
          maxIter: _maxIter.round(),
        ),
      );
      final image = await _toImage(result);
      if (!mounted) return;
      setState(() {
        _result = result;
        _image?.dispose();
        _image = image;
        _loading = false;
      });
    } catch (e) {
      if (!mounted) return;
      setState(() {
        _error = e.toString();
        _loading = false;
      });
    }
  }

  /// RGBA 像素缓冲 → ui.Image（纵横比与画布一致）
  static const _canvasHeight = 360;

  Future<ui.Image> _toImage(rust.MandelbrotResult result) {
    final completer = Completer<ui.Image>();
    ui.decodeImageFromPixels(
      result.pixels,
      result.width,
      result.height,
      ui.PixelFormat.rgba8888,
      completer.complete,
    );
    return completer.future;
  }

  void _onTapToZoom(Offset localPosition, Size canvasSize) {
    // 像素坐标 → 复平面坐标（与 Rust 渲染时的映射一致）
    final aspect = canvasSize.height / canvasSize.width;
    final xMin = _centerX - _scale / 2;
    final yMin = _centerY - _scale * aspect / 2;
    final dx = _scale / canvasSize.width;
    final dy = _scale * aspect / canvasSize.height;

    final cx = xMin + (localPosition.dx / canvasSize.width + 0.0) * dx;
    final cy = yMin + localPosition.dy / canvasSize.height * dy;

    setState(() {
      _centerX = cx;
      _centerY = cy;
      _scale /= 2.5;
      _currentPreset = '自定义';
    });
    _render();
  }

  void _applyPreset(String name) {
    final (cx, cy, scale) = _presets[name]!;
    setState(() {
      _centerX = cx;
      _centerY = cy;
      _scale = scale;
      _currentPreset = name;
    });
    _render();
  }

  void _zoomOut() {
    setState(() {
      _scale = (_scale * 2).clamp(1e-9, 5.0);
      _currentPreset = '自定义';
    });
    _render();
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    return Scaffold(
      appBar: AppBar(
        title: const Text('Mandelbrot 分形'),
        actions: [
          if (_result != null)
            Padding(
              padding: const EdgeInsets.only(right: 16),
              child: Center(
                child: Text(
                  'Rust 渲染 ${_result!.elapsedMs} ms',
                  style: TextStyle(color: colorScheme.outline, fontSize: 12),
                ),
              ),
            ),
        ],
      ),
      body: LayoutBuilder(
        builder: (context, constraints) {
          final canvasSize = Size(
            constraints.maxWidth - 32,
            (constraints.maxWidth - 32) * _canvasHeight / _renderWidth,
          );
          return SingleChildScrollView(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                if (_error != null) ...[
                  _buildErrorBanner(colorScheme),
                  const SizedBox(height: 16),
                ],
                _buildCanvas(canvasSize),
                const SizedBox(height: 16),
                _buildPresets(),
                const SizedBox(height: 16),
                _buildControls(colorScheme),
                const SizedBox(height: 16),
                _buildStats(colorScheme),
              ],
            ),
          );
        },
      ),
    );
  }

  /// 渲染失败时给出可见提示，而不是把错误吞掉
  Widget _buildErrorBanner(ColorScheme colorScheme) {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: colorScheme.errorContainer,
        borderRadius: BorderRadius.circular(8),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Icon(Icons.error_outline, color: colorScheme.onErrorContainer, size: 18),
          const SizedBox(width: 8),
          Expanded(
            child: Text(
              '渲染失败: $_error',
              style: TextStyle(color: colorScheme.onErrorContainer, fontSize: 12),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildCanvas(Size canvasSize) {
    return ClipRRect(
      borderRadius: BorderRadius.circular(12),
      child: GestureDetector(
        onTapUp: (details) => _onTapToZoom(details.localPosition, canvasSize),
        onDoubleTap: _zoomOut,
        child: SizedBox(
          width: canvasSize.width,
          height: canvasSize.height,
          child: Stack(
            fit: StackFit.expand,
            children: [
              if (_image != null)
                RawImage(image: _image, fit: BoxFit.cover)
              else
                Container(color: Colors.black),
              if (_loading)
                const Center(child: CircularProgressIndicator(strokeWidth: 2)),
              Positioned(
                right: 8,
                bottom: 8,
                child: Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                  decoration: BoxDecoration(
                    color: Colors.black54,
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Text(
                    '单击放大 · 双击缩小 · $_currentPreset',
                    style: const TextStyle(color: Colors.white70, fontSize: 11),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildPresets() {
    return Wrap(
      spacing: 8,
      children: [
        for (final name in _presets.keys)
          ChoiceChip(
            label: Text(name),
            selected: _currentPreset == name,
            onSelected: (_) => _applyPreset(name),
          ),
      ],
    );
  }

  Widget _buildControls(ColorScheme colorScheme) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('迭代次数：${_maxIter.round()}',
                style: const TextStyle(fontWeight: FontWeight.bold)),
            Slider(
              value: _maxIter,
              min: 64,
              max: 2048,
              divisions: 31,
              label: '${_maxIter.round()}',
              onChanged: (v) => setState(() => _maxIter = v),
              onChangeEnd: (_) => _render(),
            ),
            Row(
              children: [
                Text('渲染宽度',
                    style: TextStyle(color: colorScheme.onSurfaceVariant)),
                const SizedBox(width: 12),
                Expanded(
                  child: Wrap(
                    spacing: 8,
                    runSpacing: 4,
                    children: [
                      for (final w in [240, 480, 960])
                        ChoiceChip(
                          label: Text('$w'),
                          selected: _renderWidth == w,
                          onSelected: (_) {
                            setState(() => _renderWidth = w);
                            _render();
                          },
                        ),
                    ],
                  ),
                ),
                IconButton(
                  tooltip: '缩小视图',
                  onPressed: _zoomOut,
                  icon: const Icon(Icons.zoom_out),
                ),
                IconButton(
                  tooltip: '重新渲染',
                  onPressed: _loading ? null : _render,
                  icon: const Icon(Icons.refresh),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildStats(ColorScheme colorScheme) {
    final result = _result;
    if (result == null) return const SizedBox.shrink();
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          children: [
            _Stat('中心', '(${_centerX.toStringAsFixed(6)}, ${_centerY.toStringAsFixed(6)})'),
            _Stat('视口宽', _scale.toStringAsFixed(9)),
            _Stat('内部占比', '${(result.interiorRatio * 100).toStringAsFixed(1)}%'),
            _Stat('平均迭代', result.avgEscape.toStringAsFixed(1)),
          ],
        ),
      ),
    );
  }
}

class _Stat extends StatelessWidget {
  final String label;
  final String value;

  const _Stat(this.label, this.value);

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text(label,
            style: TextStyle(
                fontSize: 11, color: Theme.of(context).colorScheme.outline)),
        const SizedBox(height: 4),
        Text(value, style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
      ],
    );
  }
}
