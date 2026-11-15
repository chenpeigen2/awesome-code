import 'package:flutter/material.dart';

import '../rust/api/text.dart' as rust;

/// 文本统计分析：Unicode 感知的字符/词/行统计、词频 Top-K、回文检测。
class TextPage extends StatefulWidget {
  const TextPage({super.key});

  @override
  State<TextPage> createState() => _TextPageState();
}

class _TextPageState extends State<TextPage> {
  final _controller = TextEditingController(
    text: 'Rust 与 Flutter 是一对好搭档。\n'
        'Rust 负责计算密集型任务，Flutter 负责界面。\n'
        'Hello, Rust! Hello, Flutter!\n',
  );
  double _topK = 5;
  bool _palindrome = false;
  rust.TextStats? _stats;
  bool _busy = false;

  @override
  void initState() {
    super.initState();
    _analyze();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  Future<void> _analyze() async {
    setState(() => _busy = true);
    try {
      final text = _controller.text;
      // 两个调用相互独立，并发执行
      final (stats, palindrome) = (
        await rust.analyzeText(text: text, topK: _topK.round()),
        await rust.isPalindrome(text: text),
      );
      if (!mounted) return;
      setState(() {
        _stats = stats;
        _palindrome = palindrome;
        _busy = false;
      });
    } catch (_) {
      if (!mounted) return;
      setState(() => _busy = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    return Scaffold(
      appBar: AppBar(
        title: const Text('文本分析'),
        actions: [
          IconButton(
            tooltip: '分析',
            onPressed: _busy ? null : _analyze,
            icon: const Icon(Icons.analytics_outlined),
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            TextField(
              controller: _controller,
              maxLines: 6,
              minLines: 3,
              decoration: InputDecoration(
                hintText: '输入任意文本（支持中文/Emoji/多行）...',
                border: const OutlineInputBorder(),
                suffixIcon: IconButton(
                  icon: const Icon(Icons.clear),
                  onPressed: () {
                    _controller.clear();
                    setState(() {
                      _stats = null;
                      _palindrome = false;
                    });
                  },
                ),
              ),
              onChanged: (_) => _analyze(),
            ),
            const SizedBox(height: 12),
            Card(
              child: Padding(
                padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                child: Row(
                  children: [
                    Text('榜单长度：${_topK.round()}',
                        style: const TextStyle(fontSize: 13)),
                    Expanded(
                      child: Slider(
                        value: _topK,
                        min: 0,
                        max: 20,
                        divisions: 20,
                        label: '${_topK.round()}',
                        onChanged: (v) => setState(() => _topK = v),
                        onChangeEnd: (_) => _analyze(),
                      ),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 16),
            if (_busy) const Center(child: CircularProgressIndicator()),
            if (!_busy) ...[
              _PalindromeBanner(isPalindrome: _palindrome, isEmpty: _controller.text.trim().isEmpty),
              const SizedBox(height: 12),
              if (_stats != null) ...[
                _CountsGrid(stats: _stats!),
                const SizedBox(height: 12),
                if (_stats!.topWords.isNotEmpty)
                  _TopList(
                    title: '高频词',
                    entries: [for (final w in _stats!.topWords) (w.word, w.count)],
                  ),
                if (_stats!.topChars.isNotEmpty) ...[
                  const SizedBox(height: 12),
                  _TopList(
                    title: '高频字符',
                    entries: [for (final c in _stats!.topChars) (c.ch, c.count)],
                  ),
                ],
                const SizedBox(height: 12),
                Card(
                  child: ListTile(
                    dense: true,
                    leading: const Icon(Icons.notes, size: 20),
                    title: Text(
                      _stats!.longestWord.isEmpty ? '—' : _stats!.longestWord,
                      style: TextStyle(color: colorScheme.primary, fontWeight: FontWeight.bold),
                    ),
                    subtitle: const Text('最长单词', style: TextStyle(fontSize: 11)),
                    trailing: Text(
                      '平均词长 ${_stats!.avgWordLen.toStringAsFixed(2)}',
                      style: TextStyle(
                          fontSize: 12, color: colorScheme.onSurfaceVariant),
                    ),
                  ),
                ),
              ],
            ],
          ],
        ),
      ),
    );
  }
}

class _PalindromeBanner extends StatelessWidget {
  final bool isPalindrome;
  final bool isEmpty;

  const _PalindromeBanner({required this.isPalindrome, required this.isEmpty});

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    if (isEmpty) {
      return Card(
        child: Padding(
          padding: const EdgeInsets.all(12),
          child: Text('输入文本后自动分析',
              style: TextStyle(color: colorScheme.outline, fontSize: 13)),
        ),
      );
    }
    return Card(
      color: isPalindrome ? colorScheme.primaryContainer : colorScheme.surfaceContainerHigh,
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Row(
          children: [
            Icon(
              isPalindrome ? Icons.check_circle : Icons.radio_button_unchecked,
              color: isPalindrome ? colorScheme.primary : colorScheme.outline,
              size: 20,
            ),
            const SizedBox(width: 8),
            Text(
              isPalindrome ? '是回文（忽略大小写与标点）' : '不是回文',
              style: TextStyle(
                color: isPalindrome
                    ? colorScheme.onPrimaryContainer
                    : colorScheme.onSurfaceVariant,
                fontSize: 13,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _CountsGrid extends StatelessWidget {
  final rust.TextStats stats;

  const _CountsGrid({required this.stats});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          children: [
            _Cell('字符', '${stats.charCount}'),
            _Cell('字节', '${stats.byteCount}'),
            _Cell('单词', '${stats.wordCount}'),
            _Cell('去重', '${stats.uniqueWords}'),
            _Cell('行数', '${stats.lineCount}'),
          ],
        ),
      ),
    );
  }
}

class _Cell extends StatelessWidget {
  final String label;
  final String value;

  const _Cell(this.label, this.value);

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    return Column(
      children: [
        Text(value,
            style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: colorScheme.primary)),
        const SizedBox(height: 2),
        Text(label,
            style: TextStyle(
                fontSize: 11, color: colorScheme.onSurfaceVariant)),
      ],
    );
  }
}

class _TopList extends StatelessWidget {
  final String title;
  final List<(String, int)> entries;

  const _TopList({required this.title, required this.entries});

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final maxCount = entries.map((e) => e.$2).fold(1, (a, b) => a > b ? a : b);

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
            const SizedBox(height: 8),
            for (final (word, count) in entries.take(10))
              Padding(
                padding: const EdgeInsets.symmetric(vertical: 2),
                child: Row(
                  children: [
                    SizedBox(
                      width: 120,
                      child: Text(
                        word,
                        overflow: TextOverflow.ellipsis,
                        style: const TextStyle(fontSize: 13),
                      ),
                    ),
                    Expanded(
                      child: ClipRRect(
                        borderRadius: BorderRadius.circular(4),
                        child: LinearProgressIndicator(
                          value: count / maxCount,
                          minHeight: 8,
                          backgroundColor: colorScheme.surfaceContainerHighest,
                        ),
                      ),
                    ),
                    const SizedBox(width: 8),
                    SizedBox(
                      width: 36,
                      child: Text('$count',
                          textAlign: TextAlign.right,
                          style: TextStyle(
                              fontSize: 12, color: colorScheme.onSurfaceVariant)),
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
