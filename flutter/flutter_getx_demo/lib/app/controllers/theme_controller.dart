import 'package:flutter/material.dart';
import 'package:get/get.dart';

class ThemeController extends GetxController {
  final themeMode = ThemeMode.system.obs;
  final locale = const Locale('zh', 'CN').obs;

  void toggleTheme() {
    themeMode.value =
        themeMode.value == ThemeMode.light ? ThemeMode.dark : ThemeMode.light;
  }

  void switchLocale() {
    locale.value =
        locale.value.languageCode == 'zh' ? const Locale('en', 'US') : const Locale('zh', 'CN');
  }
}
