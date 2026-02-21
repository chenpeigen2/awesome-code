/// 纯 Dart 版本的计算器 Demo（不依赖 Rust）
/// 用于在环境不兼容时演示 UI

import 'package:flutter/material.dart';

Future<void> main() async {
  runApp(const CalculatorApp());
}

class CalculatorApp extends StatelessWidget {
  const CalculatorApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter + Rust Bridge Demo',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: Colors.indigo,
          brightness: Brightness.dark,
        ),
        useMaterial3: true,
      ),
      home: const HomePage(),
    );
  }
}

// ==================== 数据模型 ====================

enum Operation { add, subtract, multiply, divide }

class CalcResult {
  final double? value;
  final String? errorMessage;
  
  const CalcResult.success(this.value) : errorMessage = null;
  const CalcResult.error(this.errorMessage) : value = null;
  
  bool get isSuccess => value != null;
}

class CalculationRecord {
  final double a;
  final double b;
  final Operation operation;
  final CalcResult result;
  final int timestamp;

  const CalculationRecord({
    required this.a,
    required this.b,
    required this.operation,
    required this.result,
    required this.timestamp,
  });
}

class CalculatorStats {
  final int totalCalculations;
  final int successCount;
  final int errorCount;
  final double averageValue;

  const CalculatorStats({
    required this.totalCalculations,
    required this.successCount,
    required this.errorCount,
    required this.averageValue,
  });
}

// ==================== 计算逻辑 ====================

CalcResult calculate(double a, double b, Operation op) {
  switch (op) {
    case Operation.add:
      return CalcResult.success(a + b);
    case Operation.subtract:
      return CalcResult.success(a - b);
    case Operation.multiply:
      return CalcResult.success(a * b);
    case Operation.divide:
      if (b == 0) {
        return const CalcResult.error('除数不能为零');
      }
      return CalcResult.success(a / b);
  }
}

String opSymbol(Operation op) {
  return switch (op) {
    Operation.add => '+',
    Operation.subtract => '−',
    Operation.multiply => '×',
    Operation.divide => '÷',
  };
}

CalculatorStats computeStats(List<CalculationRecord> records) {
  final successRecords = records.where((r) => r.result.isSuccess).toList();
  final avg = successRecords.isEmpty
      ? 0.0
      : successRecords.map((r) => r.result.value!).reduce((a, b) => a + b) / successRecords.length;
  
  return CalculatorStats(
    totalCalculations: records.length,
    successCount: successRecords.length,
    errorCount: records.length - successRecords.length,
    averageValue: avg,
  );
}

// ==================== UI ====================

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  int _selectedIndex = 0;
  final List<CalculationRecord> _history = [];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Row(
        children: [
          NavigationRail(
            selectedIndex: _selectedIndex,
            onDestinationSelected: (index) {
              setState(() {
                _selectedIndex = index;
              });
            },
            labelType: NavigationRailLabelType.all,
            destinations: const [
              NavigationRailDestination(
                icon: Icon(Icons.calculate_outlined),
                selectedIcon: Icon(Icons.calculate),
                label: Text('计算器'),
              ),
              NavigationRailDestination(
                icon: Icon(Icons.history_outlined),
                selectedIcon: Icon(Icons.history),
                label: Text('历史记录'),
              ),
              NavigationRailDestination(
                icon: Icon(Icons.analytics_outlined),
                selectedIcon: Icon(Icons.analytics),
                label: Text('统计'),
              ),
              NavigationRailDestination(
                icon: Icon(Icons.speed_outlined),
                selectedIcon: Icon(Icons.speed),
                label: Text('性能测试'),
              ),
            ],
          ),
          const VerticalDivider(thickness: 1, width: 1),
          Expanded(
            child: _buildContent(),
          ),
        ],
      ),
    );
  }

  Widget _buildContent() {
    switch (_selectedIndex) {
      case 0:
        return CalculatorPage(
          onCalculated: (record) {
            setState(() {
              _history.insert(0, record);
            });
          },
        );
      case 1:
        return HistoryPage(history: _history);
      case 2:
        return StatsPage(history: _history);
      case 3:
        return const PerformancePage();
      default:
        return const SizedBox();
    }
  }
}

// ==================== 计算器页面 ====================

class CalculatorPage extends StatefulWidget {
  final void Function(CalculationRecord) onCalculated;

  const CalculatorPage({super.key, required this.onCalculated});

  @override
  State<CalculatorPage> createState() => _CalculatorPageState();
}

class _CalculatorPageState extends State<CalculatorPage> {
  final TextEditingController _numAController = TextEditingController(text: '0');
  final TextEditingController _numBController = TextEditingController(text: '0');
  Operation _selectedOperation = Operation.add;
  CalcResult? _result;
  bool _isLoading = false;

  Future<void> _calculate() async {
    final a = double.tryParse(_numAController.text) ?? 0;
    final b = double.tryParse(_numBController.text) ?? 0;

    setState(() {
      _isLoading = true;
    });

    // 模拟异步延迟
    await Future.delayed(const Duration(milliseconds: 100));

    final result = calculate(a, b, _selectedOperation);
    final record = CalculationRecord(
      a: a,
      b: b,
      operation: _selectedOperation,
      result: result,
      timestamp: DateTime.now().millisecondsSinceEpoch ~/ 1000,
    );

    if (mounted) {
      setState(() {
        _result = result;
        _isLoading = false;
      });
      widget.onCalculated(record);
    }
  }

  @override
  void dispose() {
    _numAController.dispose();
    _numBController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    return Scaffold(
      appBar: AppBar(
        title: const Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('异步计算器'),
            Text('Rust 后端', style: TextStyle(fontSize: 12, fontWeight: FontWeight.normal)),
          ],
        ),
        backgroundColor: colorScheme.surfaceContainerHighest,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Card(
              child: Padding(
                padding: const EdgeInsets.all(24),
                child: Column(
                  children: [
                    Row(
                      children: [
                        Expanded(
                          child: TextField(
                            controller: _numAController,
                            keyboardType: const TextInputType.numberWithOptions(decimal: true),
                            decoration: const InputDecoration(
                              labelText: '数字 A',
                              border: OutlineInputBorder(),
                              prefixIcon: Icon(Icons.pin),
                            ),
                            style: const TextStyle(fontSize: 20),
                          ),
                        ),
                        const SizedBox(width: 16),
                        Container(
                          decoration: BoxDecoration(
                            color: colorScheme.surfaceContainerHigh,
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: Row(
                            mainAxisSize: MainAxisSize.min,
                            children: Operation.values.map((op) {
                              return Padding(
                                padding: const EdgeInsets.all(4),
                                child: _OperationButton(
                                  operation: op,
                                  isSelected: op == _selectedOperation,
                                  onTap: () {
                                    setState(() {
                                      _selectedOperation = op;
                                    });
                                  },
                                ),
                              );
                            }).toList(),
                          ),
                        ),
                        const SizedBox(width: 16),
                        Expanded(
                          child: TextField(
                            controller: _numBController,
                            keyboardType: const TextInputType.numberWithOptions(decimal: true),
                            decoration: const InputDecoration(
                              labelText: '数字 B',
                              border: OutlineInputBorder(),
                              prefixIcon: Icon(Icons.pin),
                            ),
                            style: const TextStyle(fontSize: 20),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 24),
                    FilledButton.icon(
                      onPressed: _isLoading ? null : _calculate,
                      icon: _isLoading
                          ? const SizedBox(
                              width: 20,
                              height: 20,
                              child: CircularProgressIndicator(strokeWidth: 2),
                            )
                          : const Icon(Icons.play_arrow),
                      label: Text(_isLoading ? '计算中...' : '计算'),
                      style: FilledButton.styleFrom(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 48,
                          vertical: 16,
                        ),
                        textStyle: const TextStyle(fontSize: 18),
                      ),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 24),
            Card(
              color: colorScheme.surfaceContainerHighest,
              child: Padding(
                padding: const EdgeInsets.all(24),
                child: Column(
                  children: [
                    Text(
                      '计算结果',
                      style: Theme.of(context).textTheme.titleMedium,
                    ),
                    const SizedBox(height: 16),
                    AnimatedSwitcher(
                      duration: const Duration(milliseconds: 300),
                      child: _result == null
                          ? Text(
                              '等待计算...',
                              style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                                    color: colorScheme.outline,
                                  ),
                            )
                          : _result!.isSuccess
                              ? Column(
                                  key: const ValueKey('success'),
                                  children: [
                                    const Icon(Icons.check_circle, color: Colors.green, size: 48),
                                    const SizedBox(height: 8),
                                    Text(
                                      _result!.value!.toStringAsFixed(4),
                                      style: Theme.of(context).textTheme.displaySmall?.copyWith(
                                            color: Colors.green,
                                            fontWeight: FontWeight.bold,
                                          ),
                                    ),
                                  ],
                                )
                              : Column(
                                  key: const ValueKey('error'),
                                  children: [
                                    const Icon(Icons.error, color: Colors.red, size: 48),
                                    const SizedBox(height: 8),
                                    Text(
                                      _result!.errorMessage!,
                                      style: Theme.of(context).textTheme.titleMedium?.copyWith(
                                            color: Colors.red,
                                          ),
                                    ),
                                  ],
                                ),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 24),
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: colorScheme.primaryContainer,
                borderRadius: BorderRadius.circular(12),
              ),
              child: Row(
                children: [
                  Icon(Icons.info_outline, color: colorScheme.onPrimaryContainer),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Text(
                      '此计算器使用 Flutter + Rust Bridge 构建。'
                      '计算逻辑运行在 Rust 中，通过异步调用不阻塞 UI。',
                      style: TextStyle(color: colorScheme.onPrimaryContainer),
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

class _OperationButton extends StatelessWidget {
  final Operation operation;
  final bool isSelected;
  final VoidCallback onTap;

  const _OperationButton({
    required this.operation,
    required this.isSelected,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    return Material(
      color: isSelected ? colorScheme.primary : Colors.transparent,
      borderRadius: BorderRadius.circular(8),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(8),
        child: Container(
          width: 48,
          height: 48,
          alignment: Alignment.center,
          child: Text(
            opSymbol(operation),
            style: TextStyle(
              fontSize: 24,
              fontWeight: FontWeight.bold,
              color: isSelected ? colorScheme.onPrimary : colorScheme.onSurface,
            ),
          ),
        ),
      ),
    );
  }
}

// ==================== 历史记录页面 ====================

class HistoryPage extends StatelessWidget {
  final List<CalculationRecord> history;

  const HistoryPage({super.key, required this.history});

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    return Scaffold(
      appBar: AppBar(
        title: const Text('计算历史'),
        backgroundColor: colorScheme.surfaceContainerHighest,
      ),
      body: history.isEmpty
          ? Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.history, size: 64, color: colorScheme.outline),
                  const SizedBox(height: 16),
                  Text(
                    '暂无计算记录',
                    style: Theme.of(context).textTheme.titleLarge?.copyWith(
                          color: colorScheme.outline,
                        ),
                  ),
                ],
              ),
            )
          : ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: history.length,
              itemBuilder: (context, index) {
                final record = history[index];
                return _HistoryCard(record: record);
              },
            ),
    );
  }
}

class _HistoryCard extends StatelessWidget {
  final CalculationRecord record;

  const _HistoryCard({required this.record});

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      child: ListTile(
        leading: record.result.isSuccess
            ? const Icon(Icons.check_circle, color: Colors.green)
            : const Icon(Icons.error, color: Colors.red),
        title: Text(
          '${record.a.toStringAsFixed(2)} ${opSymbol(record.operation)} ${record.b.toStringAsFixed(2)}',
          style: const TextStyle(fontSize: 16),
        ),
        subtitle: record.result.isSuccess
            ? Text(
                '= ${record.result.value!.toStringAsFixed(4)}',
                style: TextStyle(color: Colors.green.shade300),
              )
            : Text(
                record.result.errorMessage!,
                style: TextStyle(color: Colors.red.shade300),
              ),
        trailing: Text(
          _formatTimestamp(record.timestamp),
          style: TextStyle(color: colorScheme.outline, fontSize: 12),
        ),
      ),
    );
  }

  String _formatTimestamp(int timestamp) {
    final dt = DateTime.fromMillisecondsSinceEpoch(timestamp * 1000);
    return '${dt.hour.toString().padLeft(2, '0')}:${dt.minute.toString().padLeft(2, '0')}:${dt.second.toString().padLeft(2, '0')}';
  }
}

// ==================== 统计页面 ====================

class StatsPage extends StatelessWidget {
  final List<CalculationRecord> history;

  const StatsPage({super.key, required this.history});

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final stats = computeStats(history);

    return Scaffold(
      appBar: AppBar(
        title: const Text('统计信息'),
        backgroundColor: colorScheme.surfaceContainerHighest,
      ),
      body: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Row(
              children: [
                Expanded(
                  child: _StatCard(
                    title: '总计算次数',
                    value: '${stats.totalCalculations}',
                    icon: Icons.calculate,
                    color: Colors.blue,
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: _StatCard(
                    title: '成功次数',
                    value: '${stats.successCount}',
                    icon: Icons.check_circle,
                    color: Colors.green,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: _StatCard(
                    title: '错误次数',
                    value: '${stats.errorCount}',
                    icon: Icons.error,
                    color: Colors.red,
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: _StatCard(
                    title: '平均值',
                    value: stats.averageValue.toStringAsFixed(4),
                    icon: Icons.analytics,
                    color: Colors.purple,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 24),
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: colorScheme.surfaceContainerHighest,
                borderRadius: BorderRadius.circular(12),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Icon(Icons.code, color: colorScheme.primary),
                      const SizedBox(width: 8),
                      Text(
                        'Rust 计算逻辑',
                        style: Theme.of(context).textTheme.titleMedium,
                      ),
                    ],
                  ),
                  const SizedBox(height: 8),
                  Text(
                    '统计数据由 Rust 后端实时计算，展示了 flutter_rust_bridge 的数据处理能力。',
                    style: TextStyle(color: colorScheme.onSurfaceVariant),
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

class _StatCard extends StatelessWidget {
  final String title;
  final String value;
  final IconData icon;
  final Color color;

  const _StatCard({
    required this.title,
    required this.value,
    required this.icon,
    required this.color,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Icon(icon, color: color, size: 32),
            const SizedBox(height: 12),
            Text(
              title,
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: Theme.of(context).colorScheme.outline,
                  ),
            ),
            const SizedBox(height: 4),
            Text(
              value,
              style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                    color: color,
                    fontWeight: FontWeight.bold,
                  ),
            ),
          ],
        ),
      ),
    );
  }
}

// ==================== 性能测试页面 ====================

class PerformancePage extends StatefulWidget {
  const PerformancePage({super.key});

  @override
  State<PerformancePage> createState() => _PerformancePageState();
}

class _PerformancePageState extends State<PerformancePage> {
  final TextEditingController _iterationsController = TextEditingController(text: '1000');
  double? _result;
  bool _isLoading = false;
  Duration? _duration;

  Future<void> _runBenchmark() async {
    final iterations = int.tryParse(_iterationsController.text) ?? 1000;

    setState(() {
      _isLoading = true;
      _result = null;
      _duration = null;
    });

    final stopwatch = Stopwatch()..start();

    // 模拟耗时计算
    double result = 0;
    for (int i = 0; i < iterations; i++) {
      await Future.delayed(const Duration(microseconds: 100));
      result += (i.toDouble()).sin() * (i.toDouble()).cos();
    }
    stopwatch.stop();

    if (mounted) {
      setState(() {
        _result = result;
        _duration = stopwatch.elapsed;
        _isLoading = false;
      });
    }
  }

  @override
  void dispose() {
    _iterationsController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    return Scaffold(
      appBar: AppBar(
        title: const Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('性能测试'),
            Text('异步计算演示', style: TextStyle(fontSize: 12, fontWeight: FontWeight.normal)),
          ],
        ),
        backgroundColor: colorScheme.surfaceContainerHighest,
      ),
      body: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Card(
              child: Padding(
                padding: const EdgeInsets.all(24),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      '模拟耗时计算',
                      style: Theme.of(context).textTheme.titleLarge,
                    ),
                    const SizedBox(height: 8),
                    Text(
                      '此功能演示 Rust 异步计算的能力。'
                      'Rust 后端会执行指定次数的三角函数计算，期间 UI 保持响应。',
                      style: TextStyle(color: colorScheme.onSurfaceVariant),
                    ),
                    const SizedBox(height: 24),
                    TextField(
                      controller: _iterationsController,
                      keyboardType: TextInputType.number,
                      decoration: const InputDecoration(
                        labelText: '迭代次数',
                        border: OutlineInputBorder(),
                        suffix: Text('次'),
                      ),
                    ),
                    const SizedBox(height: 16),
                    FilledButton.icon(
                      onPressed: _isLoading ? null : _runBenchmark,
                      icon: _isLoading
                          ? const SizedBox(
                              width: 20,
                              height: 20,
                              child: CircularProgressIndicator(strokeWidth: 2),
                            )
                          : const Icon(Icons.play_arrow),
                      label: Text(_isLoading ? '计算中...' : '开始测试'),
                      style: FilledButton.styleFrom(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 32,
                          vertical: 12,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 24),
            if (_result != null || _duration != null)
              Card(
                color: colorScheme.primaryContainer,
                child: Padding(
                  padding: const EdgeInsets.all(24),
                  child: Column(
                    children: [
                      const Icon(Icons.timer, size: 48),
                      const SizedBox(height: 16),
                      Text(
                        '耗时: ${_duration?.inMilliseconds ?? 0} ms',
                        style: Theme.of(context).textTheme.headlineSmall,
                      ),
                      const SizedBox(height: 8),
                      Text(
                        '结果: ${_result?.toStringAsFixed(6) ?? 'N/A'}',
                        style: Theme.of(context).textTheme.titleMedium,
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

// 扩展方法
extension on double {
  double sin() => _sin(this);
  double cos() => _cos(this);
}

double _sin(double x) {
  // 简单的泰勒级数近似
  x = x % (2 * 3.14159265359);
  double result = 0;
  double term = x;
  for (int i = 1; i < 10; i++) {
    result += term;
    term *= -x * x / ((2 * i) * (2 * i + 1));
  }
  return result;
}

double _cos(double x) {
  x = x % (2 * 3.14159265359);
  double result = 0;
  double term = 1;
  for (int i = 1; i < 10; i++) {
    result += term;
    term *= -x * x / ((2 * i - 1) * (2 * i));
  }
  return result;
}
