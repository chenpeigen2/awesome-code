import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'home_view.dart';

// ============================================================
// Controllers
// ============================================================

class CounterController extends GetxController {
  final count = 0.obs;
  void increment() => count.value++;
  void decrement() => count.value--;
  void reset() => count.value = 0;
}

class WorkerController extends GetxController {
  final searchQuery = ''.obs;
  final clickCount = 0.obs;
  final logs = <String>[].obs;

  late final Worker _everWorker;
  late final Worker _onceWorker;
  late final Worker _debounceWorker;
  late final Worker _intervalWorker;

  void addLog(String msg) => logs.insert(0, '${DateTime.now().toIso8601String().substring(11, 19)} $msg');

  @override
  void onInit() {
    super.onInit();

    _everWorker = ever(searchQuery, (v) => addLog('ever: "$v"'));
    _onceWorker = once(clickCount, (v) => addLog('once: 首次点击 count=$v'));
    _debounceWorker = debounce(searchQuery, (v) => addLog('debounce: "$v"'), time: const Duration(milliseconds: 500));
    _intervalWorker = interval(clickCount, (v) => addLog('interval: count=$v'), time: const Duration(seconds: 1));
  }

  @override
  void onClose() {
    _everWorker.dispose();
    _onceWorker.dispose();
    _debounceWorker.dispose();
    _intervalWorker.dispose();
    super.onClose();
  }
}

class DIController extends GetxController {
  final apiService = Get.find<ApiService>();
  final message = ''.obs;

  void fetchData() {
    message.value = apiService.getData();
  }
}

class ApiService extends GetxService {
  String getData() => '来自 ApiService 的数据 (${DateTime.now().toIso8601String().substring(11, 19)})';
}

class ReactiveListController extends GetxController {
  final items = <String>[].obs;
  final inputText = ''.obs;

  void addItem() {
    if (inputText.value.isNotEmpty) {
      items.add(inputText.value);
      inputText.value = '';
    }
  }

  void removeItem(int index) => items.removeAt(index);
}

class GetBuilderController extends GetxController {
  int count = 0;
  void increment() { count++; update(); }
}

class NavDemoController extends GetxController {
  final navigationLog = <String>[].obs;
}

// ============================================================
// Page
// ============================================================

class GetXFeaturesPage extends StatelessWidget {
  const GetXFeaturesPage({super.key});

  @override
  Widget build(BuildContext context) {
    // Register services for DI demo
    if (!Get.isRegistered<ApiService>()) Get.put(ApiService());

    return DemoScaffold(title: 'GetX 特性', children: [
      const Section('Obx 响应式状态'),
      DemoCard(title: 'Obx + .obs', desc: '.obs 变量自动触发 UI 更新', child: _ReactiveCounterDemo()),
      const Section('GetBuilder'),
      DemoCard(title: 'GetBuilder', desc: '手动控制 UI 更新，更轻量', child: _GetBuilderDemo()),
      const Section('GetX<Controller> Widget'),
      DemoCard(title: 'GetX<T>', desc: '同时获取 Controller 和响应式重建', child: GetX<CounterController>(
        init: CounterController(),
        builder: (ctrl) => Column(children: [
          Text('Count: ${ctrl.count.value}', style: const TextStyle(fontSize: 32, fontWeight: FontWeight.bold)),
          const SizedBox(height: 8),
          Row(mainAxisAlignment: MainAxisAlignment.center, children: [
            ElevatedButton(onPressed: ctrl.decrement, child: const Text('-1')),
            const SizedBox(width: 8),
            ElevatedButton(onPressed: ctrl.increment, child: const Text('+1')),
          ]),
        ]),
      )),
      const Section('Workers'),
      DemoCard(title: 'ever / once / debounce / interval', desc: '响应式变量的副作用监听', child: _WorkersDemo()),
      const Section('依赖注入 (DI)'),
      DemoCard(title: 'Get.put / Get.find', desc: 'GetX 依赖注入系统', child: _DIDemo()),
      const Section('响应式列表'),
      DemoCard(title: 'RxList', desc: '响应式列表操作', child: _ReactiveListDemo()),
      const Section('GetX 导航'),
      DemoCard(title: 'Get.to / Get.back / Get.off', desc: '无 context 导航', child: _NavigationDemo()),
      const Section('GetX Snackbar / Dialog / BottomSheet'),
      DemoCard(title: 'GetX Overlays', desc: '无需 context 的 Overlay 组件', child: Wrap(spacing: 8, runSpacing: 8, children: [
        ElevatedButton(onPressed: () => Get.snackbar('GetX', '无需 context!', snackPosition: SnackPosition.BOTTOM, duration: const Duration(seconds: 2)), child: const Text('Snackbar')),
        ElevatedButton(onPressed: () => Get.defaultDialog(title: 'GetX Dialog', middleText: '无需 context 的对话框', textConfirm: 'OK', onConfirm: () => Get.back()), child: const Text('Dialog')),
        ElevatedButton(onPressed: () => Get.bottomSheet(Container(
          padding: const EdgeInsets.all(20),
          decoration: BoxDecoration(color: Get.theme.scaffoldBackgroundColor, borderRadius: const BorderRadius.vertical(top: Radius.circular(20))),
          child: Column(mainAxisSize: MainAxisSize.min, children: [const Text('GetX BottomSheet', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)), const SizedBox(height: 16), ElevatedButton(onPressed: () => Get.back(), child: const Text('关闭'))]),
        )), child: const Text('BottomSheet')),
      ])),
      const Section('GetX Utils 工具'),
      DemoCard(title: 'GetUtils', desc: '各种实用工具方法', child: const _UtilsDemo()),
      const Section('Bindings'),
      DemoCard(title: 'Bindings', desc: '路由级别的依赖注入', child: const Text('使用 GetPage(binding: MyBinding()) 在进入页面时自动注入依赖，退出时自动释放。', style: TextStyle(fontSize: 12))),
    ]);
  }
}

// ============================================================
// Sub Widgets
// ============================================================

class _ReactiveCounterDemo extends StatelessWidget {
  final ctrl = Get.put(CounterController());

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      Obx(() => Text('${ctrl.count.value}', style: const TextStyle(fontSize: 48, fontWeight: FontWeight.bold))),
      const SizedBox(height: 8),
      Row(mainAxisAlignment: MainAxisAlignment.center, children: [
        IconButton(onPressed: ctrl.decrement, icon: const Icon(Icons.remove_circle), iconSize: 36),
        IconButton(onPressed: ctrl.reset, icon: const Icon(Icons.refresh), iconSize: 36),
        IconButton(onPressed: ctrl.increment, icon: const Icon(Icons.add_circle), iconSize: 36),
      ]),
    ]);
  }
}

class _GetBuilderDemo extends StatelessWidget {
  final ctrl = Get.put(GetBuilderController());

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      GetBuilder<GetBuilderController>(
        builder: (c) => Text('${c.count}', style: const TextStyle(fontSize: 48, fontWeight: FontWeight.bold)),
      ),
      const SizedBox(height: 8),
      ElevatedButton(onPressed: ctrl.increment, child: const Text('Increment (GetBuilder)')),
    ]);
  }
}

class _WorkersDemo extends StatelessWidget {
  final ctrl = Get.put(WorkerController());

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      TextField(decoration: const InputDecoration(labelText: '搜索 (测试 debounce)', border: OutlineInputBorder(), suffixIcon: Icon(Icons.search)), onChanged: (v) => ctrl.searchQuery.value = v),
      const SizedBox(height: 8),
      ElevatedButton(onPressed: () => ctrl.clickCount.value++, child: const Text('点击 (测试 once/interval)')),
      const SizedBox(height: 8),
      const Text('日志:', style: TextStyle(fontWeight: FontWeight.bold)),
      const SizedBox(height: 4),
      Obx(() => Container(
        height: 120,
        decoration: BoxDecoration(border: Border.all(color: Colors.grey), borderRadius: BorderRadius.circular(8)),
        child: ListView.builder(
          itemCount: ctrl.logs.length,
          itemBuilder: (ctx, i) => Padding(padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2), child: Text(ctrl.logs[i], style: const TextStyle(fontSize: 12, fontFamily: 'monospace'))),
        ),
      )),
    ]);
  }
}

class _DIDemo extends StatelessWidget {
  final ctrl = Get.put(DIController());

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      Obx(() => Text(ctrl.message.value.isEmpty ? '点击按钮获取数据' : ctrl.message.value, style: const TextStyle(fontSize: 14))),
      const SizedBox(height: 8),
      ElevatedButton(onPressed: ctrl.fetchData, child: const Text('通过 DI 获取数据')),
      const SizedBox(height: 4),
      Text('ApiService isRegistered: ${Get.isRegistered<ApiService>()}', style: const TextStyle(fontSize: 12, color: Colors.grey)),
    ]);
  }
}

class _ReactiveListDemo extends StatelessWidget {
  final ctrl = Get.put(ReactiveListController());

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      Row(children: [
        Expanded(child: TextField(decoration: const InputDecoration(hintText: '输入项目名', border: OutlineInputBorder(), isDense: true), onChanged: (v) => ctrl.inputText.value = v, onSubmitted: (_) => ctrl.addItem())),
        const SizedBox(width: 8),
        ElevatedButton(onPressed: ctrl.addItem, child: const Text('添加')),
      ]),
      const SizedBox(height: 8),
      Obx(() => ctrl.items.isEmpty
          ? const Text('列表为空')
          : Column(children: ctrl.items.asMap().entries.map((e) => ListTile(
              title: Text(e.value),
              trailing: IconButton(icon: const Icon(Icons.delete, color: Colors.red), onPressed: () => ctrl.removeItem(e.key)),
            )).toList())),
    ]);
  }
}

class _NavigationDemo extends StatelessWidget {
  final ctrl = Get.put(NavDemoController());

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      Wrap(spacing: 8, runSpacing: 8, children: [
        ElevatedButton(onPressed: () { Get.to(const _NavTargetPage()); ctrl.navigationLog.add('Get.to()'); }, child: const Text('Get.to')),
        ElevatedButton(onPressed: () { Get.off(const _NavTargetPage()); ctrl.navigationLog.add('Get.off()'); }, child: const Text('Get.off')),
        ElevatedButton(onPressed: () { Get.offAll(const _NavTargetPage()); ctrl.navigationLog.add('Get.offAll()'); }, child: const Text('Get.offAll')),
      ]),
      const SizedBox(height: 8),
      ElevatedButton(onPressed: () => Get.back(), child: const Text('Get.back')),
      const SizedBox(height: 8),
      Text('Can back: ${Get.isOverlaysOpen}', style: const TextStyle(fontSize: 12)),
    ]);
  }
}

class _NavTargetPage extends StatelessWidget {
  const _NavTargetPage();
  @override
  Widget build(BuildContext context) {
    return Scaffold(appBar: AppBar(title: const Text('导航目标页')), body: Center(child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
      const Text('这是导航目标页面'),
      const SizedBox(height: 16),
      Text('路由参数: ${Get.arguments ?? "无"}'),
      const SizedBox(height: 16),
      ElevatedButton(onPressed: () => Get.back(result: '返回数据'), child: const Text('返回并带数据')),
    ])));
  }
}

class _UtilsDemo extends StatelessWidget {
  const _UtilsDemo();
  @override
  Widget build(BuildContext context) {
    return Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
      _utilRow('isEmail', GetUtils.isEmail('test@example.com').toString()),
      _utilRow('isPhoneNumber', GetUtils.isPhoneNumber('+8613800138000').toString()),
      _utilRow('isURL', GetUtils.isURL('https://flutter.dev').toString()),
      _utilRow('isDateTime', GetUtils.isDateTime('2024-01-01').toString()),
      _utilRow('capitalize', GetUtils.capitalize('hello world') ?? ''),
      _utilRow('capitalizeFirst', GetUtils.capitalizeFirst('flutter') ?? ''),
      _utilRow('removeAllWhitespace', GetUtils.removeAllWhitespace('  hello  world  ')),
      _utilRow('numericOnly', GetUtils.numericOnly('abc123def456')),
      _utilRow('isLengthGreaterThan', GetUtils.isLengthGreaterThan('flutter', 5).toString()),
    ]);
  }

  Widget _utilRow(String name, String result) => Padding(
    padding: const EdgeInsets.symmetric(vertical: 2),
    child: Row(children: [SizedBox(width: 160, child: Text(name, style: const TextStyle(fontFamily: 'monospace', fontSize: 12))), Expanded(child: Text(result, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 12)))]),
  );
}
