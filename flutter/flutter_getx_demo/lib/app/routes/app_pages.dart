import 'package:get/get.dart';
import 'app_routes.dart';
import '../views/home_view.dart';
import '../views/level1_basics.dart';
import '../views/level2_layout.dart';
import '../views/level3_containers.dart';
import '../views/level4_scroll.dart';
import '../views/level5_interactive.dart';
import '../views/level6_animation.dart';
import '../views/level7_form.dart';
import '../views/level8_dialog.dart';
import '../views/level9_material.dart';
import '../views/level10_advanced.dart';
import '../views/level11_getx.dart';

class AppPages {
  static final pages = [
    GetPage(name: AppRoutes.home, page: () => const HomeView()),
    GetPage(name: AppRoutes.basics, page: () => const BasicsPage()),
    GetPage(name: AppRoutes.layout, page: () => const LayoutPage()),
    GetPage(name: AppRoutes.containers, page: () => const ContainersPage()),
    GetPage(name: AppRoutes.scroll, page: () => const ScrollPage()),
    GetPage(name: AppRoutes.interactive, page: () => const InteractivePage()),
    GetPage(name: AppRoutes.animation, page: () => const AnimationPage(), transition: Transition.fadeIn),
    GetPage(name: AppRoutes.form, page: () => const FormPage()),
    GetPage(name: AppRoutes.dialog, page: () => const DialogPage()),
    GetPage(name: AppRoutes.material, page: () => const MaterialDemoPage()),
    GetPage(name: AppRoutes.advanced, page: () => const AdvancedPage()),
    GetPage(name: AppRoutes.getxFeatures, page: () => const GetXFeaturesPage()),
    GetPage(name: AppRoutes.heroTarget, page: () => const HeroTargetPage()),
  ];
}
