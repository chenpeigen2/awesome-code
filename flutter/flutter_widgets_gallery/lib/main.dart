import 'package:flutter/material.dart';
import 'pages/home_page.dart';
import 'utils/app_theme.dart';

void main() {
  runApp(const FlutterWidgetGalleryApp());
}

/// Flutter Widget Gallery 主应用
class FlutterWidgetGalleryApp extends StatefulWidget {
  const FlutterWidgetGalleryApp({super.key});

  @override
  State<FlutterWidgetGalleryApp> createState() => _FlutterWidgetGalleryAppState();
}

class _FlutterWidgetGalleryAppState extends State<FlutterWidgetGalleryApp> {
  ThemeMode _themeMode = ThemeMode.system;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Widget Gallery',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.lightTheme,
      darkTheme: AppTheme.darkTheme,
      themeMode: _themeMode,
      home: HomePage(),
    );
  }
}