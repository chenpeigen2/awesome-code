import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'app/routes/app_pages.dart';
import 'app/routes/app_routes.dart';
import 'app/controllers/theme_controller.dart';
import 'utils/app_theme.dart';
import 'utils/translations.dart';

void main() {
  Get.put(ThemeController());
  runApp(const FlutterGetXDemo());
}

class FlutterGetXDemo extends StatelessWidget {
  const FlutterGetXDemo({super.key});

  @override
  Widget build(BuildContext context) {
    final themeCtrl = Get.find<ThemeController>();
    return Obx(() => GetMaterialApp(
          title: 'Flutter GetX Demo',
          debugShowCheckedModeBanner: false,
          theme: AppTheme.lightTheme,
          darkTheme: AppTheme.darkTheme,
          themeMode: themeCtrl.themeMode.value,
          translations: AppTranslations(),
          locale: themeCtrl.locale.value,
          fallbackLocale: const Locale('en', 'US'),
          initialRoute: AppRoutes.home,
          getPages: AppPages.pages,
        ));
  }
}
