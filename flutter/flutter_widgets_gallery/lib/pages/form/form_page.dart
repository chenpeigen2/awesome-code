import 'package:flutter/material.dart';
import '../../widgets/example_card.dart';

/// 表单 Widget 示例页面
class FormPage extends StatelessWidget {
  const FormPage({super.key});

  @override
  Widget build(BuildContext context) {
    return ListView(
      children: [
        const ExampleSectionHeader(title: '文本输入', icon: Icons.text_fields),

        // TextFormField
        ExampleCard(
          title: 'TextFormField',
          description: '表单文本输入框',
          child: _TextFormFieldDemo(),
          code: 'TextFormField(decoration: InputDecoration(), validator: (v) => null)',
        ),

        // TextField 样式
        ExampleCard(
          title: 'TextField 样式',
          description: '各种输入框样式',
          child: Column(
            children: [
              TextField(
                decoration: InputDecoration(
                  labelText: 'Outlined 样式',
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                ),
              ),
              const SizedBox(height: 12),
              TextField(
                decoration: InputDecoration(
                  labelText: 'Filled 样式',
                  filled: true,
                  fillColor: Theme.of(context).colorScheme.surfaceContainerHighest,
                ),
              ),
              const SizedBox(height: 12),
              TextField(
                decoration: InputDecoration(
                  labelText: 'Underline 样式',
                  prefixIcon: const Icon(Icons.search),
                  suffixIcon: IconButton(
                    icon: const Icon(Icons.clear),
                    onPressed: () {},
                  ),
                ),
              ),
            ],
          ),
          code: 'TextField(decoration: InputDecoration(border: OutlineInputBorder()))',
        ),

        // TextField 类型
        ExampleCard(
          title: 'TextField 输入类型',
          description: '不同的键盘类型',
          child: Column(
            children: [
              TextField(
                keyboardType: TextInputType.text,
                decoration: const InputDecoration(
                  labelText: '文本',
                  prefixIcon: Icon(Icons.text_fields),
                ),
              ),
              const SizedBox(height: 12),
              TextField(
                keyboardType: TextInputType.number,
                decoration: const InputDecoration(
                  labelText: '数字',
                  prefixIcon: Icon(Icons.numbers),
                ),
              ),
              const SizedBox(height: 12),
              TextField(
                keyboardType: TextInputType.emailAddress,
                decoration: const InputDecoration(
                  labelText: '邮箱',
                  prefixIcon: Icon(Icons.email),
                ),
              ),
              const SizedBox(height: 12),
              TextField(
                keyboardType: TextInputType.phone,
                decoration: const InputDecoration(
                  labelText: '电话',
                  prefixIcon: Icon(Icons.phone),
                ),
              ),
              const SizedBox(height: 12),
              TextField(
                keyboardType: TextInputType.multiline,
                maxLines: 3,
                decoration: const InputDecoration(
                  labelText: '多行文本',
                  alignLabelWithHint: true,
                ),
              ),
            ],
          ),
          code: 'TextField(keyboardType: TextInputType.emailAddress)',
        ),

        const ExampleSectionHeader(title: '选择控件', icon: Icons.check_box),

        // Checkbox
        ExampleCard(
          title: 'Checkbox',
          description: '复选框',
          child: _CheckboxDemo(),
          code: 'Checkbox(value: true, onChanged: (v) {})',
        ),

        // CheckboxListTile
        ExampleCard(
          title: 'CheckboxListTile',
          description: '带标题的复选框',
          child: _CheckboxListTileDemo(),
          code: 'CheckboxListTile(title: Text(), value: true, onChanged: (v) {})',
        ),

        // Radio
        ExampleCard(
          title: 'Radio',
          description: '单选按钮',
          child: _RadioDemo(),
          code: 'Radio(value: 1, groupValue: selectedValue, onChanged: (v) {})',
        ),

        // RadioListTile
        ExampleCard(
          title: 'RadioListTile',
          description: '带标题的单选按钮',
          child: _RadioListTileDemo(),
          code: 'RadioListTile(title: Text(), value: 1, groupValue: selected, onChanged: ...)',
        ),

        // Switch
        ExampleCard(
          title: 'Switch',
          description: '开关',
          child: _SwitchDemo(),
          code: 'Switch(value: true, onChanged: (v) {})',
        ),

        // SwitchListTile
        ExampleCard(
          title: 'SwitchListTile',
          description: '带标题的开关',
          child: _SwitchListTileDemo(),
          code: 'SwitchListTile(title: Text(), value: true, onChanged: (v) {})',
        ),

        const ExampleSectionHeader(title: '滑块和进度', icon: Icons.linear_scale),

        // Slider
        ExampleCard(
          title: 'Slider',
          description: '滑块',
          child: _SliderDemo(),
          code: 'Slider(value: 50, min: 0, max: 100, onChanged: (v) {})',
        ),

        // RangeSlider
        ExampleCard(
          title: 'RangeSlider',
          description: '范围滑块',
          child: _RangeSliderDemo(),
          code: 'RangeSlider(values: RangeValues(20, 80), min: 0, max: 100)',
        ),

        // CircularProgressIndicator (交互式)
        ExampleCard(
          title: '交互式进度',
          description: '可调整的进度指示器',
          child: _InteractiveProgressDemo(),
          code: 'CircularProgressIndicator(value: progress)',
        ),

        const ExampleSectionHeader(title: '选择器', icon: Icons.date_range),

        // DatePicker
        ExampleCard(
          title: 'showDatePicker',
          description: '日期选择器',
          child: _DatePickerDemo(),
          code: 'showDatePicker(context: context, firstDate: ..., lastDate: ...)',
        ),

        // TimePicker
        ExampleCard(
          title: 'showTimePicker',
          description: '时间选择器',
          child: _TimePickerDemo(),
          code: 'showTimePicker(context: context, initialTime: TimeOfDay.now())',
        ),

        // DateRangePicker
        ExampleCard(
          title: 'showDateRangePicker',
          description: '日期范围选择器',
          child: _DateRangePickerDemo(),
          code: 'showDateRangePicker(context: context, firstDate: ..., lastDate: ...)',
        ),

        const ExampleSectionHeader(title: '下拉选择', icon: Icons.arrow_drop_down),

        // DropdownButton
        ExampleCard(
          title: 'DropdownButton',
          description: '下拉按钮',
          child: _DropdownButtonDemo(),
          code: 'DropdownButton(value: selected, items: [...], onChanged: (v) {})',
        ),

        // DropdownButtonFormField
        ExampleCard(
          title: 'DropdownButtonFormField',
          description: '下拉表单字段',
          child: _DropdownFormFieldDemo(),
          code: 'DropdownButtonFormField(value: selected, items: [...], onChanged: (v) {})',
        ),

        // Autocomplete
        ExampleCard(
          title: 'Autocomplete',
          description: '自动完成',
          child: _AutocompleteDemo(),
          code: 'Autocomplete(optionsBuilder: (text) => ...)',
        ),

        const ExampleSectionHeader(title: '完整表单', icon: Icons.assignment),

        // Form
        ExampleCard(
          title: 'Form 表单示例',
          description: '完整的表单验证示例',
          child: _CompleteFormDemo(),
          code: 'Form(key: formKey, child: Column(children: [TextFormField()...]))',
        ),

        const SizedBox(height: 24),
      ],
    );
  }
}

/// TextFormField 演示
class _TextFormFieldDemo extends StatelessWidget {
  final _formKey = GlobalKey<FormState>();

  @override
  Widget build(BuildContext context) {
    return Form(
      key: _formKey,
      child: Column(
        children: [
          TextFormField(
            decoration: const InputDecoration(
              labelText: '用户名',
              hintText: '请输入用户名',
              prefixIcon: Icon(Icons.person),
            ),
            validator: (value) {
              if (value == null || value.isEmpty) {
                return '请输入用户名';
              }
              if (value.length < 3) {
                return '用户名至少3个字符';
              }
              return null;
            },
          ),
          const SizedBox(height: 12),
          TextFormField(
            obscureText: true,
            decoration: const InputDecoration(
              labelText: '密码',
              hintText: '请输入密码',
              prefixIcon: Icon(Icons.lock),
            ),
            validator: (value) {
              if (value == null || value.isEmpty) {
                return '请输入密码';
              }
              if (value.length < 6) {
                return '密码至少6个字符';
              }
              return null;
            },
          ),
          const SizedBox(height: 12),
          ElevatedButton(
            onPressed: () {
              if (_formKey.currentState!.validate()) {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('验证通过')),
                );
              }
            },
            child: const Text('验证表单'),
          ),
        ],
      ),
    );
  }
}

/// Checkbox 演示
class _CheckboxDemo extends StatefulWidget {
  @override
  State<_CheckboxDemo> createState() => _CheckboxDemoState();
}

class _CheckboxDemoState extends State<_CheckboxDemo> {
  bool? _checked = false;

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceAround,
      children: [
        Checkbox(
          value: _checked == true,
          onChanged: (v) => setState(() => _checked = v),
        ),
        Checkbox(
          value: _checked,
          tristate: true,
          onChanged: (v) => setState(() => _checked = v),
        ),
        Checkbox(
          value: true,
          activeColor: Colors.red,
          onChanged: null,
        ),
      ],
    );
  }
}

/// CheckboxListTile 演示
class _CheckboxListTileDemo extends StatefulWidget {
  @override
  State<_CheckboxListTileDemo> createState() => _CheckboxListTileDemoState();
}

class _CheckboxListTileDemoState extends State<_CheckboxListTileDemo> {
  final List<bool> _checked = [true, false, true];

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        CheckboxListTile(
          title: const Text('选项一'),
          subtitle: const Text('这是选项一的描述'),
          value: _checked[0],
          onChanged: (v) => setState(() => _checked[0] = v!),
        ),
        CheckboxListTile(
          title: const Text('选项二'),
          secondary: const Icon(Icons.star),
          value: _checked[1],
          onChanged: (v) => setState(() => _checked[1] = v!),
        ),
        CheckboxListTile(
          title: const Text('选项三 (禁用)'),
          value: _checked[2],
          onChanged: null,
        ),
      ],
    );
  }
}

/// Radio 演示
class _RadioDemo extends StatefulWidget {
  @override
  State<_RadioDemo> createState() => _RadioDemoState();
}

class _RadioDemoState extends State<_RadioDemo> {
  int? _selected = 1;

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceAround,
      children: [1, 2, 3].map((i) {
        return Radio<int>(
          value: i,
          groupValue: _selected,
          onChanged: (v) => setState(() => _selected = v),
        );
      }).toList(),
    );
  }
}

/// RadioListTile 演示
class _RadioListTileDemo extends StatefulWidget {
  @override
  State<_RadioListTileDemo> createState() => _RadioListTileDemoState();
}

class _RadioListTileDemoState extends State<_RadioListTileDemo> {
  int _selected = 1;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [1, 2, 3].map((i) {
        return RadioListTile<int>(
          title: Text('选项 $i'),
          subtitle: Text('选项 $i 的描述'),
          value: i,
          groupValue: _selected,
          onChanged: (v) => setState(() => _selected = v!),
        );
      }).toList(),
    );
  }
}

/// Switch 演示
class _SwitchDemo extends StatefulWidget {
  @override
  State<_SwitchDemo> createState() => _SwitchDemoState();
}

class _SwitchDemoState extends State<_SwitchDemo> {
  bool _enabled = false;

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceAround,
      children: [
        Switch(
          value: _enabled,
          onChanged: (v) => setState(() => _enabled = v),
        ),
        Switch(
          value: true,
          activeColor: Colors.red,
          onChanged: null,
        ),
        Switch.adaptive(
          value: _enabled,
          onChanged: (v) => setState(() => _enabled = v),
        ),
      ],
    );
  }
}

/// SwitchListTile 演示
class _SwitchListTileDemo extends StatefulWidget {
  @override
  State<_SwitchListTileDemo> createState() => _SwitchListTileDemoState();
}

class _SwitchListTileDemoState extends State<_SwitchListTileDemo> {
  final Map<String, bool> _settings = {
    '通知': true,
    '声音': false,
    '振动': true,
  };

  @override
  Widget build(BuildContext context) {
    return Column(
      children: _settings.keys.map((key) {
        return SwitchListTile(
          title: Text(key),
          subtitle: Text('${key}设置'),
          value: _settings[key]!,
          onChanged: (v) => setState(() => _settings[key] = v),
        );
      }).toList(),
    );
  }
}

/// Slider 演示
class _SliderDemo extends StatefulWidget {
  @override
  State<_SliderDemo> createState() => _SliderDemoState();
}

class _SliderDemoState extends State<_SliderDemo> {
  double _value = 50;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Slider(
          value: _value,
          min: 0,
          max: 100,
          divisions: 10,
          label: _value.toStringAsFixed(0),
          onChanged: (v) => setState(() => _value = v),
        ),
        Text('当前值: ${_value.toStringAsFixed(0)}'),
      ],
    );
  }
}

/// RangeSlider 演示
class _RangeSliderDemo extends StatefulWidget {
  @override
  State<_RangeSliderDemo> createState() => _RangeSliderDemoState();
}

class _RangeSliderDemoState extends State<_RangeSliderDemo> {
  RangeValues _values = const RangeValues(20, 80);

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        RangeSlider(
          values: _values,
          min: 0,
          max: 100,
          divisions: 10,
          labels: RangeLabels(
            _values.start.toStringAsFixed(0),
            _values.end.toStringAsFixed(0),
          ),
          onChanged: (v) => setState(() => _values = v),
        ),
        Text('范围: ${_values.start.toStringAsFixed(0)} - ${_values.end.toStringAsFixed(0)}'),
      ],
    );
  }
}

/// 交互式进度演示
class _InteractiveProgressDemo extends StatefulWidget {
  @override
  State<_InteractiveProgressDemo> createState() => _InteractiveProgressDemoState();
}

class _InteractiveProgressDemoState extends State<_InteractiveProgressDemo> {
  double _progress = 0.5;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        CircularProgressIndicator(value: _progress),
        const SizedBox(height: 8),
        LinearProgressIndicator(value: _progress),
        const SizedBox(height: 8),
        Slider(
          value: _progress,
          onChanged: (v) => setState(() => _progress = v),
        ),
      ],
    );
  }
}

/// DatePicker 演示
class _DatePickerDemo extends StatefulWidget {
  @override
  State<_DatePickerDemo> createState() => _DatePickerDemoState();
}

class _DatePickerDemoState extends State<_DatePickerDemo> {
  DateTime? _selectedDate;

  @override
  Widget build(BuildContext context) {
    return ListTile(
      title: Text(_selectedDate == null
          ? '选择日期'
          : '${_selectedDate!.year}-${_selectedDate!.month}-${_selectedDate!.day}'),
      trailing: const Icon(Icons.calendar_today),
      onTap: () async {
        final date = await showDatePicker(
          context: context,
          initialDate: DateTime.now(),
          firstDate: DateTime(2020),
          lastDate: DateTime(2030),
        );
        if (date != null) setState(() => _selectedDate = date);
      },
    );
  }
}

/// TimePicker 演示
class _TimePickerDemo extends StatefulWidget {
  @override
  State<_TimePickerDemo> createState() => _TimePickerDemoState();
}

class _TimePickerDemoState extends State<_TimePickerDemo> {
  TimeOfDay? _selectedTime;

  @override
  Widget build(BuildContext context) {
    return ListTile(
      title: Text(_selectedTime == null
          ? '选择时间'
          : '${_selectedTime!.hour}:${_selectedTime!.minute.toString().padLeft(2, '0')}'),
      trailing: const Icon(Icons.access_time),
      onTap: () async {
        final time = await showTimePicker(
          context: context,
          initialTime: TimeOfDay.now(),
        );
        if (time != null) setState(() => _selectedTime = time);
      },
    );
  }
}

/// DateRangePicker 演示
class _DateRangePickerDemo extends StatefulWidget {
  @override
  State<_DateRangePickerDemo> createState() => _DateRangePickerDemoState();
}

class _DateRangePickerDemoState extends State<_DateRangePickerDemo> {
  DateTimeRange? _selectedRange;

  @override
  Widget build(BuildContext context) {
    return ListTile(
      title: Text(_selectedRange == null
          ? '选择日期范围'
          : '${_selectedRange!.start.year}-${_selectedRange!.start.month}-${_selectedRange!.start.day} 至 '
              '${_selectedRange!.end.year}-${_selectedRange!.end.month}-${_selectedRange!.end.day}'),
      trailing: const Icon(Icons.date_range),
      onTap: () async {
        final range = await showDateRangePicker(
          context: context,
          firstDate: DateTime(2020),
          lastDate: DateTime(2030),
        );
        if (range != null) setState(() => _selectedRange = range);
      },
    );
  }
}

/// DropdownButton 演示
class _DropdownButtonDemo extends StatefulWidget {
  @override
  State<_DropdownButtonDemo> createState() => _DropdownButtonDemoState();
}

class _DropdownButtonDemoState extends State<_DropdownButtonDemo> {
  String? _selectedValue = '选项1';

  @override
  Widget build(BuildContext context) {
    return DropdownButton<String>(
      value: _selectedValue,
      isExpanded: true,
      items: ['选项1', '选项2', '选项3', '选项4'].map((value) {
        return DropdownMenuItem(
          value: value,
          child: Text(value),
        );
      }).toList(),
      onChanged: (v) => setState(() => _selectedValue = v),
    );
  }
}

/// DropdownButtonFormField 演示
class _DropdownFormFieldDemo extends StatefulWidget {
  @override
  State<_DropdownFormFieldDemo> createState() => _DropdownFormFieldDemoState();
}

class _DropdownFormFieldDemoState extends State<_DropdownFormFieldDemo> {
  String? _selectedValue;

  @override
  Widget build(BuildContext context) {
    return DropdownButtonFormField<String>(
      value: _selectedValue,
      decoration: const InputDecoration(
        labelText: '请选择',
        border: OutlineInputBorder(),
      ),
      items: ['项目 A', '项目 B', '项目 C'].map((value) {
        return DropdownMenuItem(
          value: value,
          child: Text(value),
        );
      }).toList(),
      onChanged: (v) => setState(() => _selectedValue = v),
    );
  }
}

/// Autocomplete 演示
class _AutocompleteDemo extends StatelessWidget {
  final List<String> _options = [
    'Apple', 'Banana', 'Cherry', 'Date', 'Elderberry',
    'Fig', 'Grape', 'Honeydew', 'Kiwi', 'Lemon'
  ];

  @override
  Widget build(BuildContext context) {
    return Autocomplete<String>(
      optionsBuilder: (TextEditingValue textEditingValue) {
        if (textEditingValue.text.isEmpty) {
          return const Iterable<String>.empty();
        }
        return _options.where((option) {
          return option.toLowerCase().contains(textEditingValue.text.toLowerCase());
        });
      },
      onSelected: (String selection) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('选择了: $selection')),
        );
      },
    );
  }
}

/// 完整表单演示
class _CompleteFormDemo extends StatefulWidget {
  @override
  State<_CompleteFormDemo> createState() => _CompleteFormDemoState();
}

class _CompleteFormDemoState extends State<_CompleteFormDemo> {
  final _formKey = GlobalKey<FormState>();
  final _nameController = TextEditingController();
  final _emailController = TextEditingController();
  bool _agreeTerms = false;

  @override
  void dispose() {
    _nameController.dispose();
    _emailController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Form(
      key: _formKey,
      child: Column(
        children: [
          TextFormField(
            controller: _nameController,
            decoration: const InputDecoration(
              labelText: '姓名',
              prefixIcon: Icon(Icons.person),
              border: OutlineInputBorder(),
            ),
            validator: (v) => v?.isEmpty ?? true ? '请输入姓名' : null,
          ),
          const SizedBox(height: 12),
          TextFormField(
            controller: _emailController,
            decoration: const InputDecoration(
              labelText: '邮箱',
              prefixIcon: Icon(Icons.email),
              border: OutlineInputBorder(),
            ),
            keyboardType: TextInputType.emailAddress,
            validator: (v) {
              if (v?.isEmpty ?? true) return '请输入邮箱';
              if (!v!.contains('@')) return '请输入有效的邮箱';
              return null;
            },
          ),
          const SizedBox(height: 12),
          CheckboxListTile(
            title: const Text('我同意用户协议'),
            value: _agreeTerms,
            onChanged: (v) => setState(() => _agreeTerms = v ?? false),
            controlAffinity: ListTileControlAffinity.leading,
          ),
          const SizedBox(height: 12),
          Row(
            children: [
              Expanded(
                child: OutlinedButton(
                  onPressed: () => _formKey.currentState?.reset(),
                  child: const Text('重置'),
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: ElevatedButton(
                  onPressed: _agreeTerms
                      ? () {
                          if (_formKey.currentState!.validate()) {
                            ScaffoldMessenger.of(context).showSnackBar(
                              const SnackBar(content: Text('提交成功！')),
                            );
                          }
                        }
                      : null,
                  child: const Text('提交'),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
