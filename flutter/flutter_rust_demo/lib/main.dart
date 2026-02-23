import 'package:flutter/material.dart';
import 'package:flutter_rust_bridge/flutter_rust_bridge_for_generated.dart';
import 'package:flutter_rust_demo/src/rust/api/simple.dart';
import 'package:flutter_rust_demo/src/rust/frb_generated.dart';
import 'package:flutter_rust_demo/src/utils/unit_converter.dart';

Future<void> main() async {
  await RustLib.init();
  runApp(const CalculatorApp());
}

class CalculatorApp extends StatelessWidget {
  const CalculatorApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: '计算器',
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

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  int _selectedIndex = 0;
  final List<CalculationRecord> _history = [];
  
  // 保存计算器页面状态
  final CalculatorState _calculatorState = CalculatorState();
  
  // 保存性能测试页面状态
  final PerformanceState _performanceState = PerformanceState();
  
  // 保存单位换算页面状态
  final UnitConverterState _unitConverterState = UnitConverterState();

  // 判断是否为宽屏（平板/桌面）
  bool _isWideScreen(BuildContext context) {
    return MediaQuery.of(context).size.width >= 600;
  }

  @override
  Widget build(BuildContext context) {
    final isWide = _isWideScreen(context);

    if (isWide) {
      // 宽屏：使用 NavigationRail
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
                  icon: Icon(Icons.swap_horiz_outlined),
                  selectedIcon: Icon(Icons.swap_horiz),
                  label: Text('单位换算'),
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
    } else {
      // 窄屏（手机）：使用 BottomNavigationBar
      return Scaffold(
        body: _buildContent(),
        bottomNavigationBar: NavigationBar(
          selectedIndex: _selectedIndex,
          onDestinationSelected: (index) {
            setState(() {
              _selectedIndex = index;
            });
          },
          destinations: const [
            NavigationDestination(
              icon: Icon(Icons.calculate_outlined),
              selectedIcon: Icon(Icons.calculate),
              label: '计算器',
            ),
            NavigationDestination(
              icon: Icon(Icons.swap_horiz_outlined),
              selectedIcon: Icon(Icons.swap_horiz),
              label: '单位换算',
            ),
            NavigationDestination(
              icon: Icon(Icons.history_outlined),
              selectedIcon: Icon(Icons.history),
              label: '历史记录',
            ),
            NavigationDestination(
              icon: Icon(Icons.analytics_outlined),
              selectedIcon: Icon(Icons.analytics),
              label: '统计',
            ),
            NavigationDestination(
              icon: Icon(Icons.speed_outlined),
              selectedIcon: Icon(Icons.speed),
              label: '性能测试',
            ),
          ],
        ),
      );
    }
  }

  Widget _buildContent() {
    switch (_selectedIndex) {
      case 0:
        return CalculatorPage(
          state: _calculatorState,
          onCalculated: (record) {
            setState(() {
              _history.insert(0, record);
            });
          },
        );
      case 1:
        return UnitConverterPage(state: _unitConverterState);
      case 2:
        return HistoryPage(history: _history);
      case 3:
        return StatsPage(history: _history);
      case 4:
        return PerformancePage(state: _performanceState);
      default:
        return const SizedBox();
    }
  }
}

// 计算器状态类
class CalculatorState {
  String numA = '0';
  String numB = '0';
  Operation operation = Operation.add;
  CalcResult? result;
}

// 性能测试状态类
class PerformanceState {
  String iterations = '1000';
  double? result;
  Duration? duration;
}

// 单位换算状态类
class UnitConverterState {
  int tabIndex = 0;
  Map<String, String> inputValues = {};
  Map<String, String> fromUnits = {};
  Map<String, String> toUnits = {};
}

// ==================== 计算器页面 ====================

class CalculatorPage extends StatefulWidget {
  final CalculatorState state;
  final void Function(CalculationRecord) onCalculated;

  const CalculatorPage({super.key, required this.state, required this.onCalculated});

  @override
  State<CalculatorPage> createState() => _CalculatorPageState();
}

class _CalculatorPageState extends State<CalculatorPage> {
  late TextEditingController _numAController;
  late TextEditingController _numBController;
  late Operation _selectedOperation;
  CalcResult? _result;
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _numAController = TextEditingController(text: widget.state.numA);
    _numBController = TextEditingController(text: widget.state.numB);
    _selectedOperation = widget.state.operation;
    _result = widget.state.result;
  }

  Future<void> _calculate() async {
    final a = double.tryParse(_numAController.text) ?? 0;
    final b = double.tryParse(_numBController.text) ?? 0;

    setState(() {
      _isLoading = true;
    });

    try {
      final record = await createRecord(
        a: a,
        b: b,
        operation: _selectedOperation,
      );
      
      setState(() {
        _result = record.result;
      });
      
      // 保存状态
      widget.state.numA = _numAController.text;
      widget.state.numB = _numBController.text;
      widget.state.operation = _selectedOperation;
      widget.state.result = record.result;
      
      // 过滤无意义的计算，不记录到历史
      if (_isValidCalculation(a, b)) {
        widget.onCalculated(record);
      }
    } finally {
      if (mounted) {
        setState(() {
          _isLoading = false;
        });
      }
    }
  }

  /// 判断是否为有效计算（过滤无意义的计算）
  bool _isValidCalculation(double a, double b) {
    if (a == 0 && b == 0) return false;
    if (_selectedOperation == Operation.add && b == 0) return false;
    if (_selectedOperation == Operation.subtract && b == 0) return false;
    if (_selectedOperation == Operation.multiply && (a == 0 || b == 0)) return false;
    if (_selectedOperation == Operation.divide && a == 0) return false;
    return true;
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
    final isWide = MediaQuery.of(context).size.width >= 600;

    return Scaffold(
      appBar: AppBar(
        title: const Text('异步计算器'),
      ),
      body: SingleChildScrollView(
        padding: EdgeInsets.all(isWide ? 24 : 16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            // 输入卡片
            Card(
              child: Padding(
                padding: EdgeInsets.all(isWide ? 24 : 16),
                child: Column(
                  children: [
                    TextField(
                      controller: _numAController,
                      keyboardType: const TextInputType.numberWithOptions(decimal: true),
                      decoration: const InputDecoration(
                        labelText: '数字 A',
                        border: OutlineInputBorder(),
                        prefixIcon: Icon(Icons.pin),
                      ),
                      style: const TextStyle(fontSize: 20),
                    ),
                    const SizedBox(height: 16),
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
                    const SizedBox(height: 16),
                    TextField(
                      controller: _numBController,
                      keyboardType: const TextInputType.numberWithOptions(decimal: true),
                      decoration: const InputDecoration(
                        labelText: '数字 B',
                        border: OutlineInputBorder(),
                        prefixIcon: Icon(Icons.pin),
                      ),
                      style: const TextStyle(fontSize: 20),
                    ),
                    const SizedBox(height: 24),
                    SizedBox(
                      width: double.infinity,
                      child: FilledButton.icon(
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
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 24),
            // 结果卡片
            Card(
              color: colorScheme.surfaceContainerHighest,
              child: Padding(
                padding: EdgeInsets.all(isWide ? 24 : 16),
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
                          : switch (_result!) {
                              CalcResult_Success(:final value) => Column(
                                  key: const ValueKey('success'),
                                  children: [
                                    Icon(Icons.check_circle, color: Colors.green, size: 48),
                                    const SizedBox(height: 8),
                                    Text(
                                      value.toStringAsFixed(4),
                                      style: Theme.of(context).textTheme.displaySmall?.copyWith(
                                            color: Colors.green,
                                            fontWeight: FontWeight.bold,
                                          ),
                                    ),
                                  ],
                                ),
                              CalcResult_Error(:final message) => Column(
                                  key: const ValueKey('error'),
                                  children: [
                                    Icon(Icons.error, color: Colors.red, size: 48),
                                    const SizedBox(height: 8),
                                    Text(
                                      message,
                                      style: Theme.of(context).textTheme.titleMedium?.copyWith(
                                            color: Colors.red,
                                          ),
                                      textAlign: TextAlign.center,
                                    ),
                                  ],
                                ),
                            },
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

// ==================== 运算符按钮 ====================

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
    
    final symbols = {
      Operation.add: '+',
      Operation.subtract: '−',
      Operation.multiply: '×',
      Operation.divide: '÷',
    };

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
            symbols[operation] ?? '?',
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

// ==================== 单位换算页面 ====================

class UnitConverterPage extends StatefulWidget {
  final UnitConverterState state;

  const UnitConverterPage({super.key, required this.state});

  @override
  State<UnitConverterPage> createState() => _UnitConverterPageState();
}

class _UnitConverterPageState extends State<UnitConverterPage> with SingleTickerProviderStateMixin {
  late TabController _tabController;
  late TextEditingController _inputController;
  String _result = '';
  
  // 定义各类单位
  final Map<String, List<String>> _unitCategories = {
    '温度': ['摄氏度 (°C)', '华氏度 (°F)', '开尔文 (K)'],
    '长度': ['米 (m)', '千米 (km)', '厘米 (cm)', '毫米 (mm)', '英寸 (in)', '英尺 (ft)', '英里 (mi)', '海里 (nmi)'],
    '速度': ['米/秒 (m/s)', '千米/时 (km/h)', '英里/时 (mph)', '节 (kn)', '马赫 (Ma)'],
    '时间': ['秒 (s)', '分 (min)', '时 (h)', '天 (d)', '周 (wk)', '月', '年'],
    '质量': ['克 (g)', '千克 (kg)', '毫克 (mg)', '吨 (t)', '盎司 (oz)', '磅 (lb)'],
    '面积': ['平方米 (m²)', '平方千米 (km²)', '平方厘米 (cm²)', '公顷 (ha)', '亩', '平方英尺 (ft²)', '英亩 (ac)'],
    '体积': ['升 (L)', '毫升 (mL)', '立方米 (m³)', '立方厘米 (cm³)', '加仑 (gal)', '品脱 (pt)'],
    '压强': ['帕斯卡 (Pa)', '千帕 (kPa)', '兆帕 (MPa)', '巴 (bar)', '标准大气压 (atm)', '毫米汞柱 (mmHg)', '磅/平方英寸 (psi)'],
    '电压': ['伏特 (V)', '千伏 (kV)', '毫伏 (mV)', '微伏 (μV)'],
    '进制': ['二进制', '八进制', '十进制', '十六进制'],
  };

  String? _fromUnit;
  String? _toUnit;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(
      length: _unitCategories.length,
      vsync: this,
      initialIndex: widget.state.tabIndex,
    );
    _tabController.addListener(() {
      widget.state.tabIndex = _tabController.index;
      _resetUnits();
    });
    
    _inputController = TextEditingController();
    _resetUnits();
  }

  void _resetUnits() {
    final category = _unitCategories.keys.elementAt(_tabController.index);
    final units = _unitCategories[category]!;
    setState(() {
      _fromUnit = units.first;
      _toUnit = units.length > 1 ? units[1] : units.first;
      _result = '';
    });
  }

  @override
  void dispose() {
    _tabController.dispose();
    _inputController.dispose();
    super.dispose();
  }

  void _convert() {
    final input = _inputController.text;
    final category = _unitCategories.keys.elementAt(_tabController.index);
    
    final result = UnitConverter.convert(category, _fromUnit!, _toUnit!, input);
    setState(() {
      _result = result ?? (input.isEmpty ? '' : '输入无效');
    });
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final categories = _unitCategories.keys.toList();

    return Scaffold(
      appBar: AppBar(
        title: const Text('单位换算'),
        bottom: TabBar(
          controller: _tabController,
          isScrollable: true,
          tabAlignment: TabAlignment.start,
          tabs: categories.map((c) => Tab(text: c)).toList(),
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: categories.map((category) {
          final units = _unitCategories[category]!;
          return SingleChildScrollView(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                // 输入卡片
                Card(
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      children: [
                        // 输入值
                        TextField(
                          controller: _inputController,
                          keyboardType: category == '进制' && _fromUnit?.contains('十六') == true
                              ? TextInputType.text
                              : const TextInputType.numberWithOptions(decimal: true, signed: true),
                          decoration: InputDecoration(
                            labelText: '输入数值',
                            border: const OutlineInputBorder(),
                            suffixIcon: IconButton(
                              icon: const Icon(Icons.clear),
                              onPressed: () {
                                _inputController.clear();
                                setState(() => _result = '');
                              },
                            ),
                          ),
                          style: const TextStyle(fontSize: 24),
                          onChanged: (_) => _convert(),
                        ),
                        const SizedBox(height: 16),
                        // 源单位
                        DropdownButtonFormField<String>(
                          value: _fromUnit,
                          decoration: const InputDecoration(
                            labelText: '从',
                            border: OutlineInputBorder(),
                          ),
                          items: units.map((u) => DropdownMenuItem(value: u, child: Text(u))).toList(),
                          onChanged: (v) {
                            setState(() => _fromUnit = v);
                            _convert();
                          },
                        ),
                        const SizedBox(height: 16),
                        // 交换按钮
                        IconButton(
                          icon: const Icon(Icons.swap_vert, size: 32),
                          onPressed: () {
                            setState(() {
                              final temp = _fromUnit;
                              _fromUnit = _toUnit;
                              _toUnit = temp;
                            });
                            _convert();
                          },
                        ),
                        const SizedBox(height: 16),
                        // 目标单位
                        DropdownButtonFormField<String>(
                          value: _toUnit,
                          decoration: const InputDecoration(
                            labelText: '到',
                            border: OutlineInputBorder(),
                          ),
                          items: units.map((u) => DropdownMenuItem(value: u, child: Text(u))).toList(),
                          onChanged: (v) {
                            setState(() => _toUnit = v);
                            _convert();
                          },
                        ),
                      ],
                    ),
                  ),
                ),
                const SizedBox(height: 24),
                // 结果卡片
                Card(
                  color: colorScheme.primaryContainer,
                  child: Padding(
                    padding: const EdgeInsets.all(24),
                    child: Column(
                      children: [
                        Text(
                          '转换结果',
                          style: Theme.of(context).textTheme.titleMedium?.copyWith(
                            color: colorScheme.onPrimaryContainer,
                          ),
                        ),
                        const SizedBox(height: 16),
                        Text(
                          _result.isEmpty ? '—' : _result,
                          style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                            color: colorScheme.onPrimaryContainer,
                            fontWeight: FontWeight.bold,
                          ),
                          textAlign: TextAlign.center,
                        ),
                        if (_result.isNotEmpty && _toUnit != null) ...[
                          const SizedBox(height: 8),
                          Text(
                            _toUnit!,
                            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                              color: colorScheme.onPrimaryContainer.withOpacity(0.7),
                            ),
                          ),
                        ],
                      ],
                    ),
                  ),
                ),
              ],
            ),
          );
        }).toList(),
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
    final opSymbols = {
      Operation.add: '+',
      Operation.subtract: '−',
      Operation.multiply: '×',
      Operation.divide: '÷',
    };

    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      child: ListTile(
        leading: switch (record.result) {
          CalcResult_Success() => const Icon(Icons.check_circle, color: Colors.green),
          CalcResult_Error() => const Icon(Icons.error, color: Colors.red),
        },
        title: Text(
          '${record.a.toStringAsFixed(2)} ${opSymbols[record.operation] ?? '?'} ${record.b.toStringAsFixed(2)}',
          style: const TextStyle(fontSize: 16),
        ),
        subtitle: switch (record.result) {
          CalcResult_Success(:final value) => Text(
              '= ${value.toStringAsFixed(4)}',
              style: TextStyle(color: Colors.green.shade300),
            ),
          CalcResult_Error(:final message) => Text(
              message,
              style: TextStyle(color: Colors.red.shade300),
            ),
        },
        trailing: Text(
          _formatTimestamp(record.timestamp),
          style: TextStyle(color: colorScheme.outline, fontSize: 12),
        ),
      ),
    );
  }

  String _formatTimestamp(PlatformInt64 timestamp) {
    final ts = timestamp.toInt();
    final dt = DateTime.fromMillisecondsSinceEpoch(ts * 1000);
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

    return Scaffold(
      appBar: AppBar(
        title: const Text('统计信息'),
      ),
      body: FutureBuilder<CalculatorStats>(
        future: computeStats(records: history),
        builder: (context, snapshot) {
          final stats = snapshot.data;
          
          return Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                _StatCard(title: '总计算次数', value: '${stats?.totalCalculations ?? 0}', icon: Icons.calculate, color: Colors.blue),
                const SizedBox(height: 12),
                _StatCard(title: '成功次数', value: '${stats?.successCount ?? 0}', icon: Icons.check_circle, color: Colors.green),
                const SizedBox(height: 12),
                _StatCard(title: '错误次数', value: '${stats?.errorCount ?? 0}', icon: Icons.error, color: Colors.red),
                const SizedBox(height: 12),
                _StatCard(title: '平均值', value: (stats?.averageValue ?? 0).toStringAsFixed(4), icon: Icons.analytics, color: Colors.purple),
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
                          Text('Rust 计算逻辑', style: Theme.of(context).textTheme.titleMedium),
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
          );
        },
      ),
    );
  }
}

class _StatCard extends StatelessWidget {
  final String title;
  final String value;
  final IconData icon;
  final Color color;

  const _StatCard({required this.title, required this.value, required this.icon, required this.color});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          children: [
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: color.withOpacity(0.2),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Icon(icon, color: color, size: 28),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(title, style: Theme.of(context).textTheme.bodyMedium?.copyWith(color: Theme.of(context).colorScheme.outline)),
                  const SizedBox(height: 4),
                  Text(value, style: Theme.of(context).textTheme.headlineSmall?.copyWith(color: color, fontWeight: FontWeight.bold)),
                ],
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
  final PerformanceState state;

  const PerformancePage({super.key, required this.state});

  @override
  State<PerformancePage> createState() => _PerformancePageState();
}

class _PerformancePageState extends State<PerformancePage> {
  late TextEditingController _iterationsController;
  double? _result;
  bool _isLoading = false;
  Duration? _duration;

  @override
  void initState() {
    super.initState();
    _iterationsController = TextEditingController(text: widget.state.iterations);
    _result = widget.state.result;
    _duration = widget.state.duration;
  }

  Future<void> _runBenchmark() async {
    final iterations = int.tryParse(_iterationsController.text) ?? 1000;

    setState(() {
      _isLoading = true;
      _result = null;
      _duration = null;
    });

    final stopwatch = Stopwatch()..start();
    
    try {
      final result = await heavyComputation(iterations: iterations);
      stopwatch.stop();
      
      if (mounted) {
        setState(() {
          _result = result;
          _duration = stopwatch.elapsed;
        });
        
        widget.state.iterations = _iterationsController.text;
        widget.state.result = result;
        widget.state.duration = stopwatch.elapsed;
      }
    } finally {
      if (mounted) {
        setState(() {
          _isLoading = false;
        });
      }
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
        title: const Text('性能测试'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text('模拟耗时计算', style: Theme.of(context).textTheme.titleLarge),
                    const SizedBox(height: 8),
                    Text(
                      '此功能演示 Rust 异步计算的能力。Rust 后端会执行指定次数的三角函数计算，期间 UI 保持响应。',
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
                    SizedBox(
                      width: double.infinity,
                      child: FilledButton.icon(
                        onPressed: _isLoading ? null : _runBenchmark,
                        icon: _isLoading
                            ? const SizedBox(width: 20, height: 20, child: CircularProgressIndicator(strokeWidth: 2))
                            : const Icon(Icons.play_arrow),
                        label: Text(_isLoading ? '计算中...' : '开始测试'),
                        style: FilledButton.styleFrom(
                          padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 12),
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
                      Text('耗时: ${_duration?.inMilliseconds ?? 0} ms', style: Theme.of(context).textTheme.headlineSmall),
                      const SizedBox(height: 8),
                      Text('结果: ${_result?.toStringAsFixed(6) ?? 'N/A'}', style: Theme.of(context).textTheme.titleMedium),
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
