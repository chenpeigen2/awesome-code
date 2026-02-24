import 'package:flutter/widgets.dart';

/// Widget 分类模型
class WidgetCategory {
  final String name;
  final String description;
  final IconData icon;
  final List<WidgetExample> examples;

  const WidgetCategory({
    required this.name,
    required this.description,
    required this.icon,
    required this.examples,
  });
}

/// Widget 示例模型
class WidgetExample {
  final String name;
  final String description;
  final WidgetBuilder builder;

  const WidgetExample({
    required this.name,
    required this.description,
    required this.builder,
  });
}
