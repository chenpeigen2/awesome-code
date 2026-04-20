import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'home_view.dart';

class FormController extends GetxController {
  final textInput = ''.obs;
  final password = ''.obs;
  final multiline = ''.obs;
  final selectedDate = Rxn<DateTime>();
  final selectedTime = Rxn<TimeOfDay>();
  final sliderVal = 50.0.obs;
  final switchVal = false.obs;
  final checkboxVals = [true, false, false].obs;
  final radioVal = 0.obs;
  final dropdownVal = 'Apple'.obs;
  final rangeValues = const RangeValues(20, 80).obs;

  final formKey = GlobalKey<FormState>();
  final nameCtrl = TextEditingController();
  final emailCtrl = TextEditingController();
  final phoneCtrl = TextEditingController();

  void submitForm() {
    if (formKey.currentState!.validate()) {
      Get.snackbar('成功', '表单验证通过!', snackPosition: SnackPosition.BOTTOM);
    }
  }

  @override
  void onClose() {
    nameCtrl.dispose();
    emailCtrl.dispose();
    phoneCtrl.dispose();
    super.onClose();
  }
}

class FormPage extends StatelessWidget {
  const FormPage({super.key});

  @override
  Widget build(BuildContext context) {
    final ctrl = Get.put(FormController());
    return DemoScaffold(title: '表单组件', children: [
      const Section('TextField 输入框'),
      DemoCard(title: 'TextField', desc: '各种输入框样式', child: Column(children: [
        TextField(decoration: const InputDecoration(labelText: '用户名', border: OutlineInputBorder(), prefixIcon: Icon(Icons.person)), onChanged: (v) => ctrl.textInput.value = v),
        const SizedBox(height: 12),
        TextField(obscureText: true, decoration: const InputDecoration(labelText: '密码', border: OutlineInputBorder(), prefixIcon: Icon(Icons.lock)), onChanged: (v) => ctrl.password.value = v),
        const SizedBox(height: 12),
        TextField(maxLines: 3, decoration: const InputDecoration(labelText: '多行输入', border: OutlineInputBorder()), onChanged: (v) => ctrl.multiline.value = v),
        const SizedBox(height: 12),
        Obx(() => Text('用户名: ${ctrl.textInput.value}\n密码长度: ${ctrl.password.value.length}\n多行: ${ctrl.multiline.value}', style: const TextStyle(fontSize: 12))),
      ])),
      const Section('InputDecoration 装饰'),
      DemoCard(title: 'InputDecoration', desc: '丰富的输入框装饰', child: Column(children: const [
        TextField(decoration: InputDecoration(labelText: '带标签', hintText: '请输入内容', border: OutlineInputBorder())),
        SizedBox(height: 12),
        TextField(decoration: InputDecoration(filled: true, fillColor: Colors.blue, hintText: '填充背景', border: UnderlineInputBorder())),
        SizedBox(height: 12),
        TextField(decoration: InputDecoration(labelText: '带前缀和后缀', prefixIcon: Icon(Icons.search), suffixIcon: Icon(Icons.clear), border: OutlineInputBorder())),
        SizedBox(height: 12),
        TextField(decoration: InputDecoration(labelText: '带 helper 和 counter', helperText: '请输入邮箱地址', counterText: '0/50', border: OutlineInputBorder())),
      ])),
      const Section('Checkbox 复选框'),
      DemoCard(title: 'CheckboxListTile', child: Obx(() => Column(children: [
        CheckboxListTile(title: const Text('阅读'), value: ctrl.checkboxVals[0], onChanged: (v) => ctrl.checkboxVals[0] = v ?? false),
        CheckboxListTile(title: const Text('运动'), value: ctrl.checkboxVals[1], onChanged: (v) => ctrl.checkboxVals[1] = v ?? false),
        CheckboxListTile(title: const Text('音乐'), value: ctrl.checkboxVals[2], onChanged: (v) => ctrl.checkboxVals[2] = v ?? false),
      ]))),
      const Section('Radio 单选'),
      DemoCard(title: 'RadioListTile', child: Obx(() => Column(children: ['Android', 'iOS', 'Flutter'].asMap().entries.map((e) => RadioListTile<int>(
        title: Text(e.value), value: e.key, groupValue: ctrl.radioVal.value, onChanged: (v) => ctrl.radioVal.value = v!,
      )).toList()))),
      const Section('Switch 开关'),
      DemoCard(title: 'Switch & SwitchListTile', child: Obx(() => SwitchListTile(
        title: const Text('开启推送通知'),
        subtitle: Text(ctrl.switchVal.value ? '已开启' : '已关闭'),
        value: ctrl.switchVal.value,
        onChanged: (v) => ctrl.switchVal.value = v,
      ))),
      const Section('Slider / RangeSlider'),
      DemoCard(title: 'Slider', child: Obx(() => Column(children: [
        Slider(value: ctrl.sliderVal.value, min: 0, max: 100, divisions: 10, label: ctrl.sliderVal.value.round().toString(), onChanged: (v) => ctrl.sliderVal.value = v),
        Text('值: ${ctrl.sliderVal.value.round()}'),
      ]))),
      DemoCard(title: 'RangeSlider', child: Obx(() => Column(children: [
        RangeSlider(values: ctrl.rangeValues.value, min: 0, max: 100, divisions: 10, labels: RangeLabels(ctrl.rangeValues.value.start.round().toString(), ctrl.rangeValues.value.end.round().toString()), onChanged: (v) => ctrl.rangeValues.value = v),
        Text('范围: ${ctrl.rangeValues.value.start.round()} - ${ctrl.rangeValues.value.end.round()}'),
      ]))),
      const Section('DropdownButton'),
      DemoCard(title: 'DropdownButton', child: Obx(() => DropdownButton<String>(
        value: ctrl.dropdownVal.value,
        isExpanded: true,
        items: ['Apple', 'Banana', 'Orange', 'Grape'].map((e) => DropdownMenuItem(value: e, child: Text(e))).toList(),
        onChanged: (v) { if (v != null) ctrl.dropdownVal.value = v; },
      ))),
      const Section('DatePicker / TimePicker'),
      DemoCard(title: 'DatePicker & TimePicker', child: Column(children: [
        Obx(() => ListTile(
          title: const Text('选择日期'),
          subtitle: Text(ctrl.selectedDate.value != null ? '${ctrl.selectedDate.value!.year}-${ctrl.selectedDate.value!.month}-${ctrl.selectedDate.value!.day}' : '未选择'),
          trailing: const Icon(Icons.calendar_today),
          onTap: () async {
            final d = await showDatePicker(context: context, initialDate: DateTime.now(), firstDate: DateTime(2000), lastDate: DateTime(2100));
            if (d != null) ctrl.selectedDate.value = d;
          },
        )),
        Obx(() => ListTile(
          title: const Text('选择时间'),
          subtitle: Text(ctrl.selectedTime.value != null ? ctrl.selectedTime.value!.format(context) : '未选择'),
          trailing: const Icon(Icons.access_time),
          onTap: () async {
            final t = await showTimePicker(context: context, initialTime: TimeOfDay.now());
            if (t != null) ctrl.selectedTime.value = t;
          },
        )),
      ])),
      const Section('Form 表单验证'),
      DemoCard(title: 'Form + TextFormField', desc: '带验证的完整表单', child: Form(
        key: ctrl.formKey,
        child: Column(children: [
          TextFormField(controller: ctrl.nameCtrl, decoration: const InputDecoration(labelText: '姓名', border: OutlineInputBorder(), prefixIcon: Icon(Icons.person)),
            validator: (v) => v == null || v.isEmpty ? '请输入姓名' : null),
          const SizedBox(height: 12),
          TextFormField(controller: ctrl.emailCtrl, decoration: const InputDecoration(labelText: '邮箱', border: OutlineInputBorder(), prefixIcon: Icon(Icons.email)),
            validator: (v) => v == null || !v.contains('@') ? '请输入有效邮箱' : null),
          const SizedBox(height: 12),
          TextFormField(controller: ctrl.phoneCtrl, decoration: const InputDecoration(labelText: '手机号', border: OutlineInputBorder(), prefixIcon: Icon(Icons.phone)),
            keyboardType: TextInputType.phone,
            validator: (v) => v == null || v.length < 11 ? '请输入有效手机号' : null),
          const SizedBox(height: 16),
          SizedBox(width: double.infinity, child: ElevatedButton(onPressed: ctrl.submitForm, child: const Text('提交表单'))),
        ]),
      )),
    ]);
  }
}
