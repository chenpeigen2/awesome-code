import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
import '../../widgets/example_card.dart';

/// Cupertino 组件示例页面（iOS 风格）
class CupertinoPage extends StatelessWidget {
  const CupertinoPage({super.key});

  @override
  Widget build(BuildContext context) {
    return ListView(
      children: [
        const ExampleSectionHeader(title: '导航栏', icon: CupertinoIcons.gear),

        // CupertinoNavigationBar
        ExampleCard(
          title: 'CupertinoNavigationBar',
          description: 'iOS 风格导航栏',
          child: _CupertinoNavBarDemo(),
          code: 'CupertinoNavigationBar(middle: Text("标题"))',
        ),

        // CupertinoTabBar
        ExampleCard(
          title: 'CupertinoTabBar',
          description: 'iOS 风格底部标签栏',
          child: _CupertinoTabBarDemo(),
          code: 'CupertinoTabBar(items: [...])',
        ),

        const ExampleSectionHeader(title: '按钮', icon: CupertinoIcons.hand_draw),

        // CupertinoButton
        ExampleCard(
          title: 'CupertinoButton',
          description: 'iOS 风格按钮',
          child: _CupertinoButtonDemo(),
          code: 'CupertinoButton(child: Text("按钮"), onPressed: ...)',
        ),

        // CupertinoActionSheet
        ExampleCard(
          title: 'CupertinoActionSheet',
          description: 'iOS 风格操作表',
          child: _CupertinoActionSheetDemo(),
          code: 'showCupertinoModalPopup(context: context, builder: ...)',
        ),

        const ExampleSectionHeader(title: '输入组件', icon: CupertinoIcons.textbox),

        // CupertinoTextField
        ExampleCard(
          title: 'CupertinoTextField',
          description: 'iOS 风格文本输入框',
          child: const _CupertinoTextFieldDemo(),
          code: 'CupertinoTextField(placeholder: "请输入")',
        ),

        // CupertinoPicker
        ExampleCard(
          title: 'CupertinoPicker',
          description: 'iOS 风格选择器',
          child: _CupertinoPickerDemo(),
          code: 'CupertinoPicker(itemCount: n, onSelectedItemChanged: ...)',
        ),

        // CupertinoDatePicker
        ExampleCard(
          title: 'CupertinoDatePicker',
          description: 'iOS 风格日期选择器',
          child: _CupertinoDatePickerDemo(),
          code: 'CupertinoDatePicker(onDateTimeChanged: ...)',
        ),

        // CupertinoTimerPicker
        ExampleCard(
          title: 'CupertinoTimerPicker',
          description: 'iOS 风格时间选择器',
          child: _CupertinoTimerPickerDemo(),
          code: 'CupertinoTimerPicker(onTimerDurationChanged: ...)',
        ),

        const ExampleSectionHeader(title: '滑块与开关', icon: CupertinoIcons.slider_horizontal_3),

        // CupertinoSlider
        ExampleCard(
          title: 'CupertinoSlider',
          description: 'iOS 风格滑块',
          child: _CupertinoSliderDemo(),
          code: 'CupertinoSlider(value: value, onChanged: ...)',
        ),

        // CupertinoSwitch
        ExampleCard(
          title: 'CupertinoSwitch',
          description: 'iOS 风格开关',
          child: _CupertinoSwitchDemo(),
          code: 'CupertinoSwitch(value: value, onChanged: ...)',
        ),

        // CupertinoSegmentedControl
        ExampleCard(
          title: 'CupertinoSegmentedControl',
          description: 'iOS 风格分段控件',
          child: _CupertinoSegmentedControlDemo(),
          code: 'CupertinoSegmentedControl(children: {...})',
        ),

        const ExampleSectionHeader(title: '对话框', icon: CupertinoIcons.chat_bubble),

        // CupertinoAlertDialog
        ExampleCard(
          title: 'CupertinoAlertDialog',
          description: 'iOS 风格警告对话框',
          child: _CupertinoAlertDialogDemo(),
          code: 'showCupertinoDialog(context: context, builder: ...)',
        ),

        const ExampleSectionHeader(title: '进度指示器', icon: CupertinoIcons.time),

        // CupertinoActivityIndicator
        ExampleCard(
          title: 'CupertinoActivityIndicator',
          description: 'iOS 风格加载指示器',
          child: _CupertinoActivityIndicatorDemo(),
          code: 'CupertinoActivityIndicator(radius: 15)',
        ),

        // CupertinoProgressIndicator
        ExampleCard(
          title: 'CupertinoLinearProgressIndicator',
          description: 'iOS 风格线性进度条',
          child: _CupertinoLinearProgressDemo(),
          code: 'LinearProgressIndicator(value: progress)',
        ),

        const ExampleSectionHeader(title: '其他组件', icon: CupertinoIcons.ellipsis_circle),

        // CupertinoContextMenu
        ExampleCard(
          title: 'CupertinoContextMenu',
          description: 'iOS 风格上下文菜单',
          child: _CupertinoContextMenuDemo(),
          code: 'CupertinoContextMenu(actions: [...], child: Widget)',
        ),

        // CupertinoSearchTextField
        ExampleCard(
          title: 'CupertinoSearchTextField',
          description: 'iOS 风格搜索框',
          child: const _CupertinoSearchTextFieldDemo(),
          code: 'CupertinoSearchTextField(onChanged: ...)',
        ),

        const SizedBox(height: 24),
      ],
    );
  }
}

// CupertinoNavigationBar 演示
class _CupertinoNavBarDemo extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Container(
      height: 80,
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.surfaceContainerHighest,
        borderRadius: BorderRadius.circular(8),
      ),
      child: Column(
        children: [
          CupertinoNavigationBar(
            middle: const Text('导航栏标题'),
            trailing: CupertinoButton(
              padding: EdgeInsets.zero,
              child: const Icon(CupertinoIcons.add),
              onPressed: () {},
            ),
          ),
        ],
      ),
    );
  }
}

// CupertinoTabBar 演示
class _CupertinoTabBarDemo extends StatefulWidget {
  @override
  State<_CupertinoTabBarDemo> createState() => _CupertinoTabBarDemoState();
}

class _CupertinoTabBarDemoState extends State<_CupertinoTabBarDemo> {
  int _currentIndex = 0;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        SizedBox(
          height: 50,
          child: CupertinoTabBar(
            currentIndex: _currentIndex,
            onTap: (index) => setState(() => _currentIndex = index),
            items: const [
              BottomNavigationBarItem(
                icon: Icon(CupertinoIcons.home),
                label: '首页',
              ),
              BottomNavigationBarItem(
                icon: Icon(CupertinoIcons.search),
                label: '搜索',
              ),
              BottomNavigationBarItem(
                icon: Icon(CupertinoIcons.settings),
                label: '设置',
              ),
            ],
          ),
        ),
      ],
    );
  }
}

// CupertinoButton 演示
class _CupertinoButtonDemo extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Wrap(
      spacing: 8,
      runSpacing: 8,
      children: [
        CupertinoButton(
          onPressed: () {},
          child: const Text('默认按钮'),
        ),
        CupertinoButton.filled(
          onPressed: () {},
          child: const Text('填充按钮'),
        ),
        CupertinoButton(
          onPressed: null,
          child: const Text('禁用按钮'),
        ),
      ],
    );
  }
}

// CupertinoActionSheet 演示
class _CupertinoActionSheetDemo extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return CupertinoButton(
      child: const Text('显示操作表'),
      onPressed: () {
        showCupertinoModalPopup<void>(
          context: context,
          builder: (context) => CupertinoActionSheet(
            title: const Text('操作标题'),
            message: const Text('请选择一个操作'),
            actions: [
              CupertinoActionSheetAction(
                onPressed: () => Navigator.pop(context),
                child: const Text('操作1'),
              ),
              CupertinoActionSheetAction(
                onPressed: () => Navigator.pop(context),
                child: const Text('操作2'),
              ),
            ],
            cancelButton: CupertinoActionSheetAction(
              onPressed: () => Navigator.pop(context),
              child: const Text('取消'),
            ),
          ),
        );
      },
    );
  }
}

// CupertinoTextField 演示
class _CupertinoTextFieldDemo extends StatefulWidget {
  const _CupertinoTextFieldDemo();

  @override
  State<_CupertinoTextFieldDemo> createState() => _CupertinoTextFieldDemoState();
}

class _CupertinoTextFieldDemoState extends State<_CupertinoTextFieldDemo> {
  final _controller = TextEditingController();

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        CupertinoTextField(
          controller: _controller,
          placeholder: '请输入文本',
          padding: const EdgeInsets.all(12),
          decoration: BoxDecoration(
            color: Theme.of(context).colorScheme.surfaceContainerHighest,
            borderRadius: BorderRadius.circular(8),
          ),
        ),
      ],
    );
  }
}

// CupertinoPicker 演示
class _CupertinoPickerDemo extends StatefulWidget {
  @override
  State<_CupertinoPickerDemo> createState() => _CupertinoPickerDemoState();
}

class _CupertinoPickerDemoState extends State<_CupertinoPickerDemo> {
  int _selectedIndex = 0;
  final List<String> _items = List.generate(10, (i) => '选项 ${i + 1}');

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 120,
      child: CupertinoPicker(
        itemExtent: 32,
        scrollController: FixedExtentScrollController(initialItem: _selectedIndex),
        onSelectedItemChanged: (index) => setState(() => _selectedIndex = index),
        children: _items.map((item) => Center(child: Text(item))).toList(),
      ),
    );
  }
}

// CupertinoDatePicker 演示
class _CupertinoDatePickerDemo extends StatefulWidget {
  @override
  State<_CupertinoDatePickerDemo> createState() => _CupertinoDatePickerDemoState();
}

class _CupertinoDatePickerDemoState extends State<_CupertinoDatePickerDemo> {
  DateTime _selectedDate = DateTime.now();

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 120,
      child: CupertinoDatePicker(
        mode: CupertinoDatePickerMode.date,
        initialDateTime: _selectedDate,
        onDateTimeChanged: (date) => setState(() => _selectedDate = date),
      ),
    );
  }
}

// CupertinoTimerPicker 演示
class _CupertinoTimerPickerDemo extends StatefulWidget {
  @override
  State<_CupertinoTimerPickerDemo> createState() => _CupertinoTimerPickerDemoState();
}

class _CupertinoTimerPickerDemoState extends State<_CupertinoTimerPickerDemo> {
  Duration _selectedDuration = Duration.zero;

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 120,
      child: CupertinoTimerPicker(
        initialTimerDuration: _selectedDuration,
        onTimerDurationChanged: (duration) => setState(() => _selectedDuration = duration),
      ),
    );
  }
}

// CupertinoSlider 演示
class _CupertinoSliderDemo extends StatefulWidget {
  @override
  State<_CupertinoSliderDemo> createState() => _CupertinoSliderDemoState();
}

class _CupertinoSliderDemoState extends State<_CupertinoSliderDemo> {
  double _value = 0.5;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        CupertinoSlider(
          value: _value,
          onChanged: (value) => setState(() => _value = value),
        ),
        Text('当前值: ${_value.toStringAsFixed(2)}'),
      ],
    );
  }
}

// CupertinoSwitch 演示
class _CupertinoSwitchDemo extends StatefulWidget {
  @override
  State<_CupertinoSwitchDemo> createState() => _CupertinoSwitchDemoState();
}

class _CupertinoSwitchDemoState extends State<_CupertinoSwitchDemo> {
  bool _value = true;

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        CupertinoSwitch(
          value: _value,
          onChanged: (value) => setState(() => _value = value),
        ),
        const SizedBox(width: 16),
        Text(_value ? '开启' : '关闭'),
      ],
    );
  }
}

// CupertinoSegmentedControl 演示
class _CupertinoSegmentedControlDemo extends StatefulWidget {
  @override
  State<_CupertinoSegmentedControlDemo> createState() => _CupertinoSegmentedControlDemoState();
}

class _CupertinoSegmentedControlDemoState extends State<_CupertinoSegmentedControlDemo> {
  int _selected = 0;

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: double.infinity,
      child: CupertinoSegmentedControl<int>(
        groupValue: _selected,
        onValueChanged: (value) => setState(() => _selected = value),
        children: const {
          0: Padding(padding: EdgeInsets.symmetric(horizontal: 20), child: Text('选项1')),
          1: Padding(padding: EdgeInsets.symmetric(horizontal: 20), child: Text('选项2')),
          2: Padding(padding: EdgeInsets.symmetric(horizontal: 20), child: Text('选项3')),
        },
      ),
    );
  }
}

// CupertinoAlertDialog 演示
class _CupertinoAlertDialogDemo extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return CupertinoButton(
      child: const Text('显示警告对话框'),
      onPressed: () {
        showCupertinoDialog<void>(
          context: context,
          builder: (context) => CupertinoAlertDialog(
            title: const Text('警告'),
            content: const Text('这是一个 iOS 风格的警告对话框'),
            actions: [
              CupertinoDialogAction(
                onPressed: () => Navigator.pop(context),
                child: const Text('取消'),
              ),
              CupertinoDialogAction(
                isDestructiveAction: true,
                onPressed: () => Navigator.pop(context),
                child: const Text('确定'),
              ),
            ],
          ),
        );
      },
    );
  }
}

// CupertinoActivityIndicator 演示
class _CupertinoActivityIndicatorDemo extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
      children: const [
        CupertinoActivityIndicator(radius: 10),
        CupertinoActivityIndicator(radius: 15),
        CupertinoActivityIndicator(radius: 20, animating: true),
        CupertinoActivityIndicator(radius: 15, animating: false),
      ],
    );
  }
}

// CupertinoProgressIndicator 演示
class _CupertinoLinearProgressDemo extends StatefulWidget {
  @override
  State<_CupertinoLinearProgressDemo> createState() => _CupertinoLinearProgressDemoState();
}

class _CupertinoLinearProgressDemoState extends State<_CupertinoLinearProgressDemo> {
  double _progress = 0.5;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        LinearProgressIndicator(value: _progress, minHeight: 6),
        const SizedBox(height: 8),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            CupertinoButton(
              padding: const EdgeInsets.symmetric(horizontal: 12),
              child: const Icon(CupertinoIcons.minus),
              onPressed: () => setState(() => _progress = (_progress - 0.1).clamp(0.0, 1.0)),
            ),
            Text('${(_progress * 100).toInt()}%'),
            CupertinoButton(
              padding: const EdgeInsets.symmetric(horizontal: 12),
              child: const Icon(CupertinoIcons.add),
              onPressed: () => setState(() => _progress = (_progress + 0.1).clamp(0.0, 1.0)),
            ),
          ],
        ),
      ],
    );
  }
}

// CupertinoContextMenu 演示
class _CupertinoContextMenuDemo extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return CupertinoContextMenu(
      actions: [
        CupertinoContextMenuAction(
          onPressed: () => Navigator.pop(context),
          child: const Text('复制'),
        ),
        CupertinoContextMenuAction(
          onPressed: () => Navigator.pop(context),
          child: const Text('分享'),
        ),
        CupertinoContextMenuAction(
          isDestructiveAction: true,
          onPressed: () => Navigator.pop(context),
          child: const Text('删除'),
        ),
      ],
      child: Container(
        width: 80,
        height: 80,
        decoration: BoxDecoration(
          color: Theme.of(context).colorScheme.primaryContainer,
          borderRadius: BorderRadius.circular(12),
        ),
        child: const Center(child: Icon(CupertinoIcons.photo, size: 40)),
      ),
    );
  }
}

// CupertinoSearchTextField 演示
class _CupertinoSearchTextFieldDemo extends StatefulWidget {
  const _CupertinoSearchTextFieldDemo();

  @override
  State<_CupertinoSearchTextFieldDemo> createState() => _CupertinoSearchTextFieldDemoState();
}

class _CupertinoSearchTextFieldDemoState extends State<_CupertinoSearchTextFieldDemo> {
  String _searchText = '';

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        CupertinoSearchTextField(
          onChanged: (value) => setState(() => _searchText = value),
          placeholder: '搜索',
        ),
        if (_searchText.isNotEmpty) ...[
          const SizedBox(height: 8),
          Text('搜索: $_searchText'),
        ],
      ],
    );
  }
}
