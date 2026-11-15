import 'package:flutter/material.dart';

import '../rust/api/primes.dart' as rust;

/// 素数实验室：埃氏筛统计、第 n 个素数、质因数分解。
class PrimesPage extends StatefulWidget {
  const PrimesPage({super.key});

  @override
  State<PrimesPage> createState() => _PrimesPageState();
}

class _PrimesPageState extends State<PrimesPage> {
  final _limitController = TextEditingController(text: '1000000');
  final _nthController = TextEditingController(text: '1000');
  final _factorController = TextEditingController(text: '987654321');

  rust.SieveStats? _stats;
  BigInt? _nthPrime;
  List<rust.PrimeFactor>? _factors;
  bool _busy = false;
  String? _error;

  @override
  void dispose() {
    _limitController.dispose();
    _nthController.dispose();
    _factorController.dispose();
    super.dispose();
  }

  Future<void> _runAll() async {
    setState(() {
      _busy = true;
      _error = null;
      _stats = null;
      _nthPrime = null;
      _factors = null;
    });
    try {
      final limit = BigInt.tryParse(_limitController.text);
      final n = BigInt.tryParse(_nthController.text);
      final toFactor = BigInt.tryParse(_factorController.text);

      // 三个任务相互独立，并发发起
      final results = await Future.wait([
        if (limit != null && limit >= BigInt.zero)
          rust.sieveStats(limit: limit)
        else
          Future.error('筛选上限无效'),
        if (n != null && n > BigInt.zero)
          rust.nthPrime(n: n).then((v) => v)
        else
          Future<BigInt>.error('n 必须为正整数'),
        if (toFactor != null && toFactor >= BigInt.zero)
          rust.factorize(n: toFactor)
        else
          Future<List<rust.PrimeFactor>>.error('待分解数无效'),
      ]);

      if (!mounted) return;
      setState(() {
        _stats = results[0] as rust.SieveStats;
        _nthPrime = results[1] as BigInt;
        _factors = results[2] as List<rust.PrimeFactor>;
        _busy = false;
      });
    } catch (e) {
      if (!mounted) return;
      setState(() {
        _error = e.toString();
        _busy = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    return Scaffold(
      appBar: AppBar(title: const Text('素数实验室')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            _InputCard(
              label: '筛选上限',
              controller: _limitController,
              helper: '埃拉托斯特尼筛，统计 π(limit) 与最大素数',
            ),
            const SizedBox(height: 12),
            _InputCard(
              label: '第 n 个素数',
              controller: _nthController,
              helper: '素数定理上界估计 + 筛法（p₁=2, p₁₀₀₀=7919）',
            ),
            const SizedBox(height: 12),
            _InputCard(
              label: '质因数分解',
              controller: _factorController,
              helper: '算术基本定理：试除到 √n',
            ),
            const SizedBox(height: 16),
            FilledButton.icon(
              onPressed: _busy ? null : _runAll,
              icon: _busy
                  ? const SizedBox(
                      width: 18,
                      height: 18,
                      child: CircularProgressIndicator(strokeWidth: 2))
                  : const Icon(Icons.play_arrow),
              label: Text(_busy ? 'Rust 计算中...' : '并发执行三项任务'),
            ),
            if (_error != null) ...[
              const SizedBox(height: 16),
              Card(
                color: colorScheme.errorContainer,
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Text(_error!,
                      style: TextStyle(color: colorScheme.onErrorContainer)),
                ),
              ),
            ],
            const SizedBox(height: 16),
            if (_stats != null) _StatsCard(stats: _stats!),
            if (_nthPrime != null)
              _ResultCard(
                title: '第 ${_nthController.text} 个素数',
                value: '$_nthPrime',
              ),
            if (_factors != null)
              _FactorCard(
                n: _factorController.text,
                factors: _factors!,
              ),
          ],
        ),
      ),
    );
  }
}

class _InputCard extends StatelessWidget {
  final String label;
  final String helper;
  final TextEditingController controller;

  const _InputCard({
    required this.label,
    required this.controller,
    required this.helper,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(label, style: const TextStyle(fontWeight: FontWeight.bold)),
            const SizedBox(height: 4),
            Text(helper,
                style: TextStyle(
                    fontSize: 12, color: Theme.of(context).colorScheme.outline)),
            const SizedBox(height: 8),
            TextField(
              controller: controller,
              keyboardType: TextInputType.number,
              decoration: const InputDecoration(
                border: OutlineInputBorder(),
                isDense: true,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _StatsCard extends StatelessWidget {
  final rust.SieveStats stats;

  const _StatsCard({required this.stats});

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    return Card(
      color: colorScheme.surfaceContainerHighest,
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                _Stat('π(${_compact(stats.limit)})', '${stats.count}'),
                _Stat('最大素数', '${stats.largest}'),
                _Stat('密度', stats.density.toStringAsFixed(4)),
                _Stat('耗时', '${stats.elapsedMs} ms'),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

class _ResultCard extends StatelessWidget {
  final String title;
  final String value;

  const _ResultCard({required this.title, required this.value});

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    return Card(
      child: ListTile(
        title: Text(title, style: const TextStyle(fontSize: 14)),
        trailing: Text(value,
            style: TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
                color: colorScheme.primary)),
      ),
    );
  }
}

class _FactorCard extends StatelessWidget {
  final String n;
  final List<rust.PrimeFactor> factors;

  const _FactorCard({required this.n, required this.factors});

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('$n =', style: const TextStyle(fontWeight: FontWeight.bold)),
            const SizedBox(height: 8),
            Wrap(
              spacing: 8,
              runSpacing: 8,
              children: [
                for (final f in factors)
                  Container(
                    padding:
                        const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                    decoration: BoxDecoration(
                      color: colorScheme.primaryContainer,
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Text(
                      f.exponent > 1
                          ? '${f.prime}^${f.exponent}'
                          : '${f.prime}',
                      style: TextStyle(
                          color: colorScheme.onPrimaryContainer,
                          fontWeight: FontWeight.bold),
                    ),
                  ),
              ],
            ),
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
        Text(value,
            style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
      ],
    );
  }
}

String _compact(BigInt n) {
  final s = n.toString();
  if (s.length > 9) return '${s.substring(0, 3)}…(${s.length}位)';
  return s;
}
