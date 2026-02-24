import 'package:flutter/material.dart';

/// 示例卡片组件 - 用于展示每个 Widget 示例
class ExampleCard extends StatelessWidget {
  final String title;
  final String? description;
  final Widget child;
  final VoidCallback? onTap;
  final bool expandable;
  final String? code;

  const ExampleCard({
    super.key,
    required this.title,
    this.description,
    required this.child,
    this.onTap,
    this.expandable = false,
    this.code,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // 标题栏
          InkWell(
            onTap: onTap,
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Row(
                children: [
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          title,
                          style: Theme.of(context).textTheme.titleMedium?.copyWith(
                                fontWeight: FontWeight.bold,
                              ),
                        ),
                        if (description != null) ...[
                          const SizedBox(height: 4),
                          Text(
                            description!,
                            style: Theme.of(context).textTheme.bodySmall?.copyWith(
                                  color: Theme.of(context).colorScheme.onSurfaceVariant,
                                ),
                          ),
                        ],
                      ],
                    ),
                  ),
                  if (onTap != null || expandable)
                    Icon(
                      expandable ? Icons.expand_more : Icons.arrow_forward_ios,
                      size: 16,
                    ),
                ],
              ),
            ),
          ),
          const Divider(height: 1),
          // 内容区域
          Padding(
            padding: const EdgeInsets.all(16),
            child: child,
          ),
          // 代码展示区域
          if (code != null) ...[
            const Divider(height: 1),
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(12),
              color: Theme.of(context).colorScheme.surfaceContainerHighest,
              child: Text(
                code!,
                style: TextStyle(
                  fontFamily: 'monospace',
                  fontSize: 12,
                  color: Theme.of(context).colorScheme.onSurface,
                ),
              ),
            ),
          ],
        ],
      ),
    );
  }
}

/// 示例分组标题
class ExampleSectionHeader extends StatelessWidget {
  final String title;
  final IconData? icon;

  const ExampleSectionHeader({
    super.key,
    required this.title,
    this.icon,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 24, 16, 8),
      child: Row(
        children: [
          if (icon != null) ...[
            Icon(icon, size: 20),
            const SizedBox(width: 8),
          ],
          Text(
            title,
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  fontWeight: FontWeight.bold,
                  color: Theme.of(context).colorScheme.primary,
                ),
          ),
        ],
      ),
    );
  }
}

/// 通用页面包装器
class ExamplePageWrapper extends StatelessWidget {
  final String title;
  final List<Widget> children;

  const ExamplePageWrapper({
    super.key,
    required this.title,
    required this.children,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(title),
      ),
      body: ListView(
        children: children,
      ),
    );
  }
}
