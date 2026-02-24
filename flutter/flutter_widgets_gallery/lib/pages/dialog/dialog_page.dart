import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
import '../../widgets/example_card.dart';

/// 对话框和弹窗示例页面
class DialogPage extends StatelessWidget {
  const DialogPage({super.key});

  @override
  Widget build(BuildContext context) {
    return ListView(
      children: [
        const ExampleSectionHeader(title: '对话框', icon: Icons.chat),

        // AlertDialog
        ExampleCard(
          title: 'AlertDialog',
          description: '警告对话框',
          child: ElevatedButton(
            onPressed: () {
              showDialog(
                context: context,
                builder: (context) => AlertDialog(
                  title: const Text('确认删除'),
                  content: const Text('确定要删除这个项目吗？此操作不可撤销。'),
                  actions: [
                    TextButton(
                      onPressed: () => Navigator.pop(context),
                      child: const Text('取消'),
                    ),
                    TextButton(
                      onPressed: () => Navigator.pop(context, true),
                      child: const Text('删除'),
                    ),
                  ],
                ),
              );
            },
            child: const Text('显示 AlertDialog'),
          ),
          code: 'showDialog(context: context, builder: (ctx) => AlertDialog(...))',
        ),

        // SimpleDialog
        ExampleCard(
          title: 'SimpleDialog',
          description: '简单对话框',
          child: ElevatedButton(
            onPressed: () {
              showDialog(
                context: context,
                builder: (context) => SimpleDialog(
                  title: const Text('选择颜色'),
                  children: [
                    SimpleDialogOption(
                      onPressed: () => Navigator.pop(context, '红色'),
                      child: const Row(
                        children: [
                          Icon(Icons.circle, color: Colors.red),
                          SizedBox(width: 8),
                          Text('红色'),
                        ],
                      ),
                    ),
                    SimpleDialogOption(
                      onPressed: () => Navigator.pop(context, '绿色'),
                      child: const Row(
                        children: [
                          Icon(Icons.circle, color: Colors.green),
                          SizedBox(width: 8),
                          Text('绿色'),
                        ],
                      ),
                    ),
                    SimpleDialogOption(
                      onPressed: () => Navigator.pop(context, '蓝色'),
                      child: const Row(
                        children: [
                          Icon(Icons.circle, color: Colors.blue),
                          SizedBox(width: 8),
                          Text('蓝色'),
                        ],
                      ),
                    ),
                  ],
                ),
              ).then((value) {
                if (value != null) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text('选择了: $value')),
                  );
                }
              });
            },
            child: const Text('显示 SimpleDialog'),
          ),
          code: 'showDialog(builder: (ctx) => SimpleDialog(children: [SimpleDialogOption()...]))',
        ),

        // Dialog (自定义)
        ExampleCard(
          title: '自定义 Dialog',
          description: '完全自定义的对话框',
          child: ElevatedButton(
            onPressed: () {
              showDialog(
                context: context,
                builder: (context) => Dialog(
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(16),
                  ),
                  child: Padding(
                    padding: const EdgeInsets.all(20),
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        const Icon(Icons.check_circle, color: Colors.green, size: 64),
                        const SizedBox(height: 16),
                        const Text('成功!', style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
                        const SizedBox(height: 8),
                        const Text('您的操作已成功完成'),
                        const SizedBox(height: 20),
                        ElevatedButton(
                          onPressed: () => Navigator.pop(context),
                          child: const Text('确定'),
                        ),
                      ],
                    ),
                  ),
                ),
              );
            },
            child: const Text('显示自定义 Dialog'),
          ),
          code: 'Dialog(shape: RoundedRectangleBorder(...), child: ...)',
        ),

        // FullscreenDialog
        ExampleCard(
          title: '全屏对话框',
          description: '全屏显示的对话框',
          child: ElevatedButton(
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  fullscreenDialog: true,
                  builder: (context) => Scaffold(
                    appBar: AppBar(
                      title: const Text('全屏对话框'),
                      leading: IconButton(
                        icon: const Icon(Icons.close),
                        onPressed: () => Navigator.pop(context),
                      ),
                    ),
                    body: const Center(
                      child: Text('这是一个全屏对话框页面'),
                    ),
                  ),
                ),
              );
            },
            child: const Text('显示全屏对话框'),
          ),
          code: 'Navigator.push(context, MaterialPageRoute(fullscreenDialog: true))',
        ),

        const ExampleSectionHeader(title: '底部抽屉', icon: Icons.vertical_align_bottom),

        // ModalBottomSheet
        ExampleCard(
          title: 'ModalBottomSheet',
          description: '模态底部抽屉',
          child: ElevatedButton(
            onPressed: () {
              showModalBottomSheet(
                context: context,
                isScrollControlled: true,
                shape: const RoundedRectangleBorder(
                  borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
                ),
                builder: (context) => DraggableScrollableSheet(
                  initialChildSize: 0.5,
                  maxChildSize: 0.9,
                  minChildSize: 0.25,
                  expand: false,
                  builder: (context, scrollController) {
                    return Column(
                      children: [
                        Container(
                          width: 40,
                          height: 4,
                          margin: const EdgeInsets.symmetric(vertical: 8),
                          decoration: BoxDecoration(
                            color: Colors.grey[300],
                            borderRadius: BorderRadius.circular(2),
                          ),
                        ),
                        const Padding(
                          padding: EdgeInsets.all(16),
                          child: Text('可拖拽的底部抽屉', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                        ),
                        Expanded(
                          child: ListView.builder(
                            controller: scrollController,
                            itemCount: 20,
                            itemBuilder: (context, index) => ListTile(
                              title: Text('项目 $index'),
                            ),
                          ),
                        ),
                      ],
                    );
                  },
                ),
              );
            },
            child: const Text('显示 ModalBottomSheet'),
          ),
          code: 'showModalBottomSheet(builder: (ctx) => DraggableScrollableSheet(...))',
        ),

        // PersistentBottomSheet
        ExampleCard(
          title: 'PersistentBottomSheet',
          description: '持久化底部抽屉',
          child: _PersistentBottomSheetDemo(),
          code: 'Scaffold.of(context).showBottomSheet((ctx) => ...)',
        ),

        const ExampleSectionHeader(title: '菜单', icon: Icons.menu),

        // PopupMenuButton
        ExampleCard(
          title: 'PopupMenuButton',
          description: '弹出菜单按钮',
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            children: [
              PopupMenuButton<String>(
                onSelected: (value) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text('选择了: $value')),
                  );
                },
                itemBuilder: (context) => [
                  const PopupMenuItem(value: '编辑', child: Text('编辑')),
                  const PopupMenuItem(value: '分享', child: Text('分享')),
                  const PopupMenuItem(value: '删除', child: Text('删除')),
                ],
                child: const Padding(
                  padding: EdgeInsets.all(8),
                  child: Row(
                    children: [
                      Text('更多操作'),
                      Icon(Icons.arrow_drop_down),
                    ],
                  ),
                ),
              ),
              PopupMenuButton<String>(
                icon: const Icon(Icons.more_vert),
                onSelected: (value) {},
                itemBuilder: (context) => [
                  const PopupMenuItem(value: '1', child: Text('选项一')),
                  const PopupMenuItem(value: '2', child: Text('选项二')),
                  const PopupMenuDivider(),
                  const PopupMenuItem(value: '3', child: Text('选项三')),
                ],
              ),
            ],
          ),
          code: 'PopupMenuButton(itemBuilder: (ctx) => [PopupMenuItem()...])',
        ),

        // DropdownMenu (Material 3)
        ExampleCard(
          title: 'DropdownMenu',
          description: 'Material 3 下拉菜单',
          child: _DropdownMenuDemo(),
          code: 'DropdownMenu(entries: [DropdownMenuEntry()...], onSelected: ...)',
        ),

        // MenuAnchor
        ExampleCard(
          title: 'MenuAnchor',
          description: '菜单锚点 (Material 3)',
          child: _MenuAnchorDemo(),
          code: 'MenuAnchor(menuChildren: [MenuItemButton()...], child: ...)',
        ),

        const ExampleSectionHeader(title: '其他弹窗', icon: Icons.more_horiz),

        // DatePickerDialog
        ExampleCard(
          title: 'showDatePicker',
          description: '日期选择对话框',
          child: ElevatedButton(
            onPressed: () async {
              final date = await showDatePicker(
                context: context,
                initialDate: DateTime.now(),
                firstDate: DateTime(2020),
                lastDate: DateTime(2030),
                builder: (context, child) {
                  return Theme(
                    data: Theme.of(context).copyWith(
                      colorScheme: const ColorScheme.light(
                        primary: Colors.purple,
                        onPrimary: Colors.white,
                        surface: Colors.white,
                        onSurface: Colors.black,
                      ),
                    ),
                    child: child!,
                  );
                },
              );
              if (date != null) {
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text('选择了: ${date.year}-${date.month}-${date.day}')),
                );
              }
            },
            child: const Text('显示日期选择器'),
          ),
          code: 'showDatePicker(context: context, initialDate: ..., firstDate: ..., lastDate: ...)',
        ),

        // TimePickerDialog
        ExampleCard(
          title: 'showTimePicker',
          description: '时间选择对话框',
          child: ElevatedButton(
            onPressed: () async {
              final time = await showTimePicker(
                context: context,
                initialTime: TimeOfDay.now(),
                builder: (context, child) {
                  return MediaQuery(
                    data: MediaQuery.of(context).copyWith(alwaysUse24HourFormat: true),
                    child: child!,
                  );
                },
              );
              if (time != null) {
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text('选择了: ${time.hour}:${time.minute.toString().padLeft(2, '0')}')),
                );
              }
            },
            child: const Text('显示时间选择器'),
          ),
          code: 'showTimePicker(context: context, initialTime: TimeOfDay.now())',
        ),

        // showLicensePage
        ExampleCard(
          title: 'showLicensePage',
          description: '许可证页面',
          child: ElevatedButton(
            onPressed: () {
              showLicensePage(
                context: context,
                applicationName: 'Flutter Widget Gallery',
                applicationVersion: '1.0.0',
              );
            },
            child: const Text('显示许可证页面'),
          ),
          code: 'showLicensePage(context: context, applicationName: ...)',
        ),

        // showAboutDialog
        ExampleCard(
          title: 'showAboutDialog',
          description: '关于对话框',
          child: ElevatedButton(
            onPressed: () {
              showAboutDialog(
                context: context,
                applicationName: 'Flutter Widget Gallery',
                applicationVersion: '1.0.0',
                applicationIcon: const FlutterLogo(size: 48),
                applicationLegalese: '© 2024 Flutter Widget Gallery',
                children: const [
                  SizedBox(height: 16),
                  Text('这是一个学习 Flutter Widget 的示例应用'),
                ],
              );
            },
            child: const Text('显示关于对话框'),
          ),
          code: 'showAboutDialog(context: context, applicationName: ...)',
        ),

        // showSearch
        ExampleCard(
          title: 'showSearch',
          description: '搜索页面',
          child: ElevatedButton(
            onPressed: () {
              showSearch(
                context: context,
                delegate: _CustomSearchDelegate(),
              );
            },
            child: const Text('显示搜索页面'),
          ),
          code: 'showSearch(context: context, delegate: SearchDelegate())',
        ),

        // Adaptive dialog
        ExampleCard(
          title: '自适应对话框',
          description: '根据平台显示不同样式',
          child: ElevatedButton(
            onPressed: () {
              if (Theme.of(context).platform == TargetPlatform.iOS) {
                showCupertinoDialog(
                  context: context,
                  builder: (context) => CupertinoAlertDialog(
                    title: const Text('iOS 风格'),
                    content: const Text('这是 iOS 风格的对话框'),
                    actions: [
                      CupertinoDialogAction(
                        child: const Text('取消'),
                        onPressed: () => Navigator.pop(context),
                      ),
                      CupertinoDialogAction(
                        isDefaultAction: true,
                        child: const Text('确定'),
                        onPressed: () => Navigator.pop(context),
                      ),
                    ],
                  ),
                );
              } else {
                showDialog(
                  context: context,
                  builder: (context) => AlertDialog(
                    title: const Text('Material 风格'),
                    content: const Text('这是 Material 风格的对话框'),
                    actions: [
                      TextButton(
                        onPressed: () => Navigator.pop(context),
                        child: const Text('取消'),
                      ),
                      TextButton(
                        onPressed: () => Navigator.pop(context),
                        child: const Text('确定'),
                      ),
                    ],
                  ),
                );
              }
            },
            child: const Text('显示自适应对话框'),
          ),
          code: 'showDialog / showCupertinoDialog',
        ),

        const SizedBox(height: 24),
      ],
    );
  }
}

/// 持久化底部抽屉演示
class _PersistentBottomSheetDemo extends StatefulWidget {
  @override
  State<_PersistentBottomSheetDemo> createState() => _PersistentBottomSheetDemoState();
}

class _PersistentBottomSheetDemoState extends State<_PersistentBottomSheetDemo> {
  PersistentBottomSheetController? _controller;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        ElevatedButton(
          onPressed: () {
            if (_controller == null) {
              _controller = Scaffold.of(context).showBottomSheet(
                (context) => Container(
                  height: 150,
                  decoration: BoxDecoration(
                    color: Theme.of(context).colorScheme.surface,
                    borderRadius: const BorderRadius.vertical(top: Radius.circular(16)),
                  ),
                  child: Column(
                    children: [
                      Container(
                        width: 40,
                        height: 4,
                        margin: const EdgeInsets.symmetric(vertical: 8),
                        decoration: BoxDecoration(
                          color: Colors.grey[300],
                          borderRadius: BorderRadius.circular(2),
                        ),
                      ),
                      const Padding(
                        padding: EdgeInsets.all(16),
                        child: Text('持久化底部抽屉'),
                      ),
                      ElevatedButton(
                        onPressed: () => _controller?.close(),
                        child: const Text('关闭'),
                      ),
                    ],
                  ),
                ),
              );
              _controller!.closed.then((_) {
                setState(() => _controller = null);
              });
            } else {
              _controller!.close();
              setState(() => _controller = null);
            }
          },
          child: Text(_controller == null ? '显示底部抽屉' : '关闭底部抽屉'),
        ),
      ],
    );
  }
}

/// DropdownMenu 演示
class _DropdownMenuDemo extends StatefulWidget {
  @override
  State<_DropdownMenuDemo> createState() => _DropdownMenuDemoState();
}

class _DropdownMenuDemoState extends State<_DropdownMenuDemo> {
  String? _selectedValue;

  @override
  Widget build(BuildContext context) {
    return DropdownMenu<String>(
      initialSelection: _selectedValue,
      onSelected: (value) => setState(() => _selectedValue = value),
      dropdownMenuEntries: ['红色', '绿色', '蓝色', '黄色'].map((color) {
        return DropdownMenuEntry(
          value: color,
          label: color,
        );
      }).toList(),
    );
  }
}

/// MenuAnchor 演示
class _MenuAnchorDemo extends StatefulWidget {
  @override
  State<_MenuAnchorDemo> createState() => _MenuAnchorDemoState();
}

class _MenuAnchorDemoState extends State<_MenuAnchorDemo> {
  final MenuController _controller = MenuController();

  @override
  Widget build(BuildContext context) {
    return MenuAnchor(
      controller: _controller,
      menuChildren: [
        MenuItemButton(
          leadingIcon: const Icon(Icons.copy),
          child: const Text('复制'),
          onPressed: () => _showMessage(context, '复制'),
        ),
        MenuItemButton(
          leadingIcon: const Icon(Icons.paste),
          child: const Text('粘贴'),
          onPressed: () => _showMessage(context, '粘贴'),
        ),
        const Divider(),
        SubmenuButton(
          menuChildren: [
            MenuItemButton(
              child: const Text('子菜单项 1'),
              onPressed: () => _showMessage(context, '子菜单项 1'),
            ),
            MenuItemButton(
              child: const Text('子菜单项 2'),
              onPressed: () => _showMessage(context, '子菜单项 2'),
            ),
          ],
          child: const Text('更多选项'),
        ),
      ],
      child: ElevatedButton(
        onPressed: () {
          if (_controller.isOpen) {
            _controller.close();
          } else {
            _controller.open();
          }
        },
        child: const Text('打开菜单'),
      ),
    );
  }

  void _showMessage(BuildContext context, String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text('选择了: $message')),
    );
  }
}

/// 自定义搜索代理
class _CustomSearchDelegate extends SearchDelegate<String> {
  final List<String> _data = [
    'Apple', 'Banana', 'Cherry', 'Date', 'Elderberry',
    'Fig', 'Grape', 'Honeydew', 'Kiwi', 'Lemon',
  ];

  @override
  List<Widget> buildActions(BuildContext context) {
    return [
      IconButton(
        icon: const Icon(Icons.clear),
        onPressed: () => query = '',
      ),
    ];
  }

  @override
  Widget buildLeading(BuildContext context) {
    return IconButton(
      icon: const Icon(Icons.arrow_back),
      onPressed: () => close(context, ''),
    );
  }

  @override
  Widget buildResults(BuildContext context) {
    return _buildList();
  }

  @override
  Widget buildSuggestions(BuildContext context) {
    return _buildList();
  }

  Widget _buildList() {
    final results = _data.where((item) =>
        item.toLowerCase().contains(query.toLowerCase())).toList();

    return ListView.builder(
      itemCount: results.length,
      itemBuilder: (context, index) {
        return ListTile(
          title: Text(results[index]),
          onTap: () => close(context, results[index]),
        );
      },
    );
  }
}
