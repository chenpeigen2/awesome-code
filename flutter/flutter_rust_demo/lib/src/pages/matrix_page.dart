import 'dart:typed_data';
import 'dart:ui' as ui;

import 'package:flutter/material.dart';

import '../rust/api/matrix.dart' as rust;

/// 矩阵计算器：A·B、行列式、转置、单位阵。
///
/// 演示结构化错误：Rust 返回 MatrixOutcome sealed class，
/// Dart 侧用 switch 模式匹配分别处理成功/失败（含错误类别）。
class MatrixPage extends StatefulWidget {
  const MatrixPage({super.key});

  @override
  State<MatrixPage> createState() => _MatrixPageState();
}

class _MatrixPageState extends State<MatrixPage> {
  // A 默认 3×3，B 默认 3×3
  int _rowsA = 3, _colsA = 3, _rowsB = 3, _colsB = 3;

  late List<List<TextEditingController>> _cellsA;
  late List<List<TextEditingController>> _cellsB;

  rust.MatrixOutcome? _outcome;
  String _lastOp = '';
  bool _busy = false;

  @override
  void initState() {
    super.initState();
    _cellsA = _defaultCells(_rowsA, _colsA);
    _cellsB = _defaultCells(_rowsB, _colsB);
  }

  static List<List<TextEditingController>> _defaultCells(int rows, int cols) {
    return List.generate(rows, (r) => List.generate(cols, (c) {
          // 对角线放 1，其他 0，作为可运行的默认值
          return TextEditingController(text: r == c ? '1' : '0');
        }));
  }

  @override
  void dispose() {
    for (final row in _cellsA) {
      for (final c in row) {
        c.dispose();
      }
    }
    for (final row in _cellsB) {
      for (final c in row) {
        c.dispose();
      }
    }
    super.dispose();
  }

  rust.Matrix? _collect(List<List<TextEditingController>> cells, int rows, int cols) {
    final data = <double>[];
    for (final row in cells) {
      for (final c in row) {
        final v = double.tryParse(c.text.trim());
        if (v == null) return null;
        data.add(v);
      }
    }
    return rust.Matrix(rows: rows, cols: cols, data: Float64List.fromList(data));
  }

  Future<void> _run(Future<rust.MatrixOutcome> Function() op, String opName) async {
    setState(() {
      _busy = true;
      _outcome = null;
      _lastOp = opName;
    });
    try {
      final outcome = await op();
      if (!mounted) return;
      setState(() {
        _outcome = outcome;
        _busy = false;
      });
    } catch (e) {
      if (!mounted) return;
      setState(() {
        _outcome = rust.MatrixOutcome.failure(
          kind: rust.MatrixErrorKind.invalidShape,
          message: '调用失败：$e',
        );
        _busy = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final a = _collect(_cellsA, _rowsA, _colsA);
    final b = _collect(_cellsB, _rowsB, _colsB);
    final inputsValid = a != null && b != null;

    return Scaffold(
      appBar: AppBar(title: const Text('矩阵计算器')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            _MatrixEditor(
              title: '矩阵 A',
              rows: _rowsA,
              cols: _colsA,
              cells: _cellsA,
              onResize: (r, c) => setState(() {
                _resize(ref: 1, rows: r, cols: c);
              }),
            ),
            const SizedBox(height: 12),
            _MatrixEditor(
              title: '矩阵 B',
              rows: _rowsB,
              cols: _colsB,
              cells: _cellsB,
              onResize: (r, c) => setState(() {
                _resize(ref: 2, rows: r, cols: c);
              }),
            ),
            const SizedBox(height: 16),
            Wrap(
              spacing: 8,
              runSpacing: 8,
              alignment: WrapAlignment.center,
              children: [
                FilledButton.icon(
                  onPressed: inputsValid && !_busy ? () => _run(() => rust.multiply(a: a, b: b), 'A × B') : null,
                  icon: const Icon(Icons.close),
                  label: const Text('A × B'),
                ),
                OutlinedButton(
                  onPressed: !_busy ? () => _run(() => rust.determinant(a: a!), 'det(A)') : null,
                  child: const Text('det(A)'),
                ),
                OutlinedButton(
                  onPressed: !_busy ? () => _run(() => rust.determinant(a: b!), 'det(B)') : null,
                  child: const Text('det(B)'),
                ),
                OutlinedButton(
                  onPressed: !_busy ? () => _run(() => rust.transpose(a: a!), 'Aᵀ') : null,
                  child: const Text('Aᵀ'),
                ),
                OutlinedButton(
                  onPressed: !_busy ? () => _run(() => rust.identity(n: _rowsA), 'I($_rowsA)') : null,
                  child: Text('I($_rowsA)'),
                ),
              ],
            ),
            const SizedBox(height: 16),
            if (_busy) const Center(child: CircularProgressIndicator()),
            if (!_busy && _outcome != null) _OutcomeView(outcome: _outcome!, opName: _lastOp),
            if (!inputsValid)
              Text(
                '有单元格不是合法数字，无法提交给 Rust',
                textAlign: TextAlign.center,
                style: TextStyle(color: colorScheme.error, fontSize: 12),
              ),
          ],
        ),
      ),
    );
  }

  void _resize({required int ref, required int rows, required int cols}) {
    final old = ref == 1 ? _cellsA : _cellsB;
    final oldRows = ref == 1 ? _rowsA : _rowsB;
    final oldCols = ref == 1 ? _colsA : _colsB;

    final next = List.generate(rows, (r) {
      return List.generate(cols, (c) {
        if (r < oldRows && c < oldCols) return old[r][c];
        return TextEditingController(text: r == c ? '1' : '0');
      });
    });
    // 释放被裁剪的控制器
    for (var r = 0; r < oldRows; r++) {
      for (var c = 0; c < oldCols; c++) {
        if (r >= rows || c >= cols) old[r][c].dispose();
      }
    }

    setState(() {
      if (ref == 1) {
        _cellsA = next;
        _rowsA = rows;
        _colsA = cols;
      } else {
        _cellsB = next;
        _rowsB = rows;
        _colsB = cols;
      }
    });
  }
}

class _MatrixEditor extends StatelessWidget {
  final String title;
  final int rows;
  final int cols;
  final List<List<TextEditingController>> cells;
  final void Function(int rows, int cols) onResize;

  const _MatrixEditor({
    required this.title,
    required this.rows,
    required this.cols,
    required this.cells,
    required this.onResize,
  });

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Text(title, style: const TextStyle(fontWeight: FontWeight.bold)),
                const Spacer(),
                _DimensionStepper(
                  label: '行',
                  value: rows,
                  onChanged: (v) => onResize(v, cols),
                ),
                const SizedBox(width: 12),
                _DimensionStepper(
                  label: '列',
                  value: cols,
                  onChanged: (v) => onResize(rows, v),
                ),
              ],
            ),
            const SizedBox(height: 12),
            SingleChildScrollView(
              scrollDirection: Axis.horizontal,
              child: Column(
                children: [
                  for (var r = 0; r < rows; r++)
                    Padding(
                      padding: const EdgeInsets.only(bottom: 6),
                      child: Row(
                        children: [
                          for (var c = 0; c < cols; c++)
                            Container(
                              width: 76,
                              padding: const EdgeInsets.only(right: 6),
                              child: TextField(
                                controller: cells[r][c],
                                keyboardType: const TextInputType.numberWithOptions(
                                    decimal: true, signed: true),
                                decoration: InputDecoration(
                                  isDense: true,
                                  border: const OutlineInputBorder(),
                                  contentPadding: const EdgeInsets.symmetric(
                                      horizontal: 8, vertical: 10),
                                  filled: r == c,
                                  fillColor: r == c
                                      ? colorScheme.primaryContainer
                                          .withValues(alpha: 0.3)
                                      : null,
                                ),
                                style: const TextStyle(fontSize: 14),
                              ),
                            ),
                        ],
                      ),
                    ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _DimensionStepper extends StatelessWidget {
  final String label;
  final int value;
  final ValueChanged<int> onChanged;

  const _DimensionStepper({
    required this.label,
    required this.value,
    required this.onChanged,
  });

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Text(label, style: const TextStyle(fontSize: 12)),
        IconButton(
          visualDensity: VisualDensity.compact,
          onPressed: value > 1 ? () => onChanged(value - 1) : null,
          icon: const Icon(Icons.remove_circle_outline, size: 20),
        ),
        Text('$value', style: const TextStyle(fontWeight: FontWeight.bold)),
        IconButton(
          visualDensity: VisualDensity.compact,
          onPressed: value < 8 ? () => onChanged(value + 1) : null,
          icon: const Icon(Icons.add_circle_outline, size: 20),
        ),
      ],
    );
  }
}

/// 结果视图：对 Rust 返回的 MatrixOutcome 做模式匹配
class _OutcomeView extends StatelessWidget {
  final rust.MatrixOutcome outcome;
  final String opName;

  const _OutcomeView({required this.outcome, required this.opName});

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    return switch (outcome) {
      rust.MatrixOutcome_Success(:final matrix) => Card(
          color: colorScheme.primaryContainer,
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  '$opName = (${matrix.rows}×${matrix.cols})',
                  style: TextStyle(
                      fontWeight: FontWeight.bold,
                      color: colorScheme.onPrimaryContainer),
                ),
                const SizedBox(height: 12),
                SingleChildScrollView(
                  scrollDirection: Axis.horizontal,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      for (var r = 0; r < matrix.rows; r++)
                        Padding(
                          padding: const EdgeInsets.symmetric(vertical: 2),
                          child: Row(
                            children: [
                              for (var c = 0; c < matrix.cols; c++)
                                Container(
                                  width: 92,
                                  alignment: Alignment.centerRight,
                                  child: Text(
                                    _fmt(matrix.data[r * matrix.cols + c]),
                                    style: TextStyle(
                                        fontFeatures: const [
                                          ui.FontFeature.tabularFigures()
                                        ],
                                        color: colorScheme.onPrimaryContainer),
                                  ),
                                ),
                            ],
                          ),
                        ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ),
      rust.MatrixOutcome_Failure(:final kind, :final message) => Card(
          color: colorScheme.errorContainer,
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Row(
              children: [
                Icon(Icons.error_outline, color: colorScheme.onErrorContainer),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        switch (kind) {
                          rust.MatrixErrorKind.dimensionMismatch => '维度不匹配',
                          rust.MatrixErrorKind.notSquare => '不是方阵',
                          rust.MatrixErrorKind.dataLengthMismatch => '数据长度与形状不符',
                          rust.MatrixErrorKind.invalidShape => '形状非法',
                        },
                        style: TextStyle(
                            fontWeight: FontWeight.bold,
                            color: colorScheme.onErrorContainer),
                      ),
                      const SizedBox(height: 4),
                      Text(message,
                          style: TextStyle(
                              fontSize: 12, color: colorScheme.onErrorContainer)),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ),
    };
  }

  static String _fmt(double v) {
    if (v == v.roundToDouble() && v.abs() < 1e15) {
      return v.round().toString();
    }
    return v.toStringAsFixed(4);
  }
}
