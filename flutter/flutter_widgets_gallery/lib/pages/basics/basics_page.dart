import 'package:flutter/material.dart';
import '../../widgets/example_card.dart';

/// 基础 Widget 示例页面
class BasicsPage extends StatelessWidget {
  const BasicsPage({super.key});

  @override
  Widget build(BuildContext context) {
    return ListView(
      children: [
        const ExampleSectionHeader(title: '文本组件', icon: Icons.text_fields),
        
        // Text Widget
        ExampleCard(
          title: 'Text',
          description: '用于显示简单文本',
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text('普通文本'),
              const SizedBox(height: 8),
              Text(
                '自定义样式文本',
                style: TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                  color: Theme.of(context).colorScheme.primary,
                  decoration: TextDecoration.underline,
                ),
              ),
              const SizedBox(height: 8),
              const Text(
                '最大行数限制文本，超出部分显示省略号...',
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
              ),
              const SizedBox(height: 8),
              const Text(
                '这是一段很长的文本，用于展示文本换行和溢出处理的示例效果，Flutter 会自动处理换行',
                style: TextStyle(fontSize: 14),
              ),
            ],
          ),
          code: 'Text("Hello World")',
        ),

        // RichText
        ExampleCard(
          title: 'RichText',
          description: '富文本，支持多种样式',
          child: RichText(
            text: TextSpan(
              style: DefaultTextStyle.of(context).style,
              children: const [
                TextSpan(
                  text: '普通文本 ',
                  style: TextStyle(fontSize: 16),
                ),
                TextSpan(
                  text: '粗体文本 ',
                  style: TextStyle(fontWeight: FontWeight.bold),
                ),
                TextSpan(
                  text: '彩色文本 ',
                  style: TextStyle(color: Colors.blue),
                ),
                TextSpan(
                  text: '斜体文本',
                  style: TextStyle(fontStyle: FontStyle.italic),
                ),
              ],
            ),
          ),
          code: 'RichText(text: TextSpan(children: [...]))',
        ),

        // SelectableText
        ExampleCard(
          title: 'SelectableText',
          description: '可选择复制的文本',
          child: const SelectableText(
            '这段文本可以被选择和复制。长按文本试试！',
            style: TextStyle(fontSize: 16),
          ),
          code: 'SelectableText("可选文本")',
        ),

        const ExampleSectionHeader(title: '图片组件', icon: Icons.image),

        // Image
        ExampleCard(
          title: 'Image',
          description: '图片显示组件',
          child: Column(
            children: [
              ClipRRect(
                borderRadius: BorderRadius.circular(8),
                child: Image.network(
                  'https://picsum.photos/400/200',
                  width: double.infinity,
                  height: 150,
                  fit: BoxFit.cover,
                  loadingBuilder: (context, child, loadingProgress) {
                    if (loadingProgress == null) return child;
                    return Center(
                      child: CircularProgressIndicator(
                        value: loadingProgress.expectedTotalBytes != null
                            ? loadingProgress.cumulativeBytesLoaded /
                                loadingProgress.expectedTotalBytes!
                            : null,
                      ),
                    );
                  },
                  errorBuilder: (context, error, stackTrace) {
                    return Container(
                      height: 150,
                      color: Colors.grey[300],
                      child: const Center(
                        child: Icon(Icons.error, color: Colors.red),
                      ),
                    );
                  },
                ),
              ),
              const SizedBox(height: 12),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceAround,
                children: [
                  _buildImageItem(Icons.aspect_ratio, 'BoxFit.cover'),
                  _buildImageItem(Icons.center_focus_strong, 'BoxFit.contain'),
                  _buildImageItem(Icons.fit_screen, 'BoxFit.fill'),
                ],
              ),
            ],
          ),
          code: 'Image.network("url", fit: BoxFit.cover)',
        ),

        // AssetImage
        ExampleCard(
          title: 'Image.asset',
          description: '从资源加载图片',
          child: Container(
            height: 100,
            decoration: BoxDecoration(
              color: Theme.of(context).colorScheme.surfaceContainerHighest,
              borderRadius: BorderRadius.circular(8),
            ),
            child: const Center(
              child: Text('从 pubspec.yaml 中声明的资源加载图片'),
            ),
          ),
          code: 'Image.asset("assets/image.png")',
        ),

        // DecorationImage
        ExampleCard(
          title: 'DecorationImage',
          description: '装饰图片，用作容器背景',
          child: Container(
            height: 120,
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(12),
              image: const DecorationImage(
                image: NetworkImage('https://picsum.photos/400/120'),
                fit: BoxFit.cover,
                colorFilter: ColorFilter.mode(
                  Colors.black26,
                  BlendMode.darken,
                ),
              ),
            ),
            child: const Center(
              child: Text(
                '带背景图的容器',
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 20,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ),
          code: 'BoxDecoration(image: DecorationImage(...))',
        ),

        const ExampleSectionHeader(title: '图标组件', icon: Icons.emoji_emotions),

        // Icon
        ExampleCard(
          title: 'Icon',
          description: 'Material Design 图标',
          child: Wrap(
            spacing: 16,
            runSpacing: 16,
            children: [
              _buildIconItem(Icons.home, 'home'),
              _buildIconItem(Icons.favorite, 'favorite'),
              _buildIconItem(Icons.settings, 'settings'),
              _buildIconItem(Icons.person, 'person'),
              _buildIconItem(Icons.search, 'search'),
              _buildIconItem(Icons.mail, 'mail'),
              _buildIconItem(Icons.phone, 'phone'),
              _buildIconItem(Icons.camera, 'camera'),
            ],
          ),
          code: 'Icon(Icons.home, size: 32, color: Colors.blue)',
        ),

        // IconButton
        ExampleCard(
          title: 'IconButton',
          description: '可点击的图标按钮',
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            children: [
              IconButton(
                icon: const Icon(Icons.volume_up),
                onPressed: () => _showSnackBar(context, '音量'),
                tooltip: '音量',
              ),
              IconButton(
                icon: const Icon(Icons.star),
                color: Colors.amber,
                onPressed: () => _showSnackBar(context, '收藏'),
                tooltip: '收藏',
              ),
              IconButton(
                icon: const Icon(Icons.favorite),
                color: Colors.red,
                onPressed: () => _showSnackBar(context, '喜欢'),
                tooltip: '喜欢',
              ),
              IconButton(
                icon: const Icon(Icons.share),
                onPressed: () => _showSnackBar(context, '分享'),
                tooltip: '分享',
              ),
            ],
          ),
          code: 'IconButton(icon: Icon(Icons.star), onPressed: () {})',
        ),

        const ExampleSectionHeader(title: '按钮组件', icon: Icons.smart_button),

        // ElevatedButton
        ExampleCard(
          title: 'ElevatedButton',
          description: '浮动按钮，带阴影效果',
          child: Wrap(
            spacing: 12,
            runSpacing: 12,
            children: [
              ElevatedButton(
                onPressed: () => _showSnackBar(context, '点击了普通按钮'),
                child: const Text('普通按钮'),
              ),
              ElevatedButton.icon(
                onPressed: () => _showSnackBar(context, '点击了带图标按钮'),
                icon: const Icon(Icons.add),
                label: const Text('带图标'),
              ),
              ElevatedButton(
                onPressed: null,
                child: const Text('禁用按钮'),
              ),
            ],
          ),
          code: 'ElevatedButton(onPressed: () {}, child: Text("按钮"))',
        ),

        // FilledButton
        ExampleCard(
          title: 'FilledButton',
          description: '填充按钮，Material 3 风格',
          child: Wrap(
            spacing: 12,
            runSpacing: 12,
            children: [
              FilledButton(
                onPressed: () => _showSnackBar(context, 'Filled'),
                child: const Text('填充按钮'),
              ),
              FilledButton.icon(
                onPressed: () => _showSnackBar(context, 'Filled Icon'),
                icon: const Icon(Icons.send),
                label: const Text('发送'),
              ),
              FilledButton.tonal(
                onPressed: () => _showSnackBar(context, 'Tonal'),
                child: const Text('Tonal 按钮'),
              ),
            ],
          ),
          code: 'FilledButton(onPressed: () {}, child: Text("按钮"))',
        ),

        // OutlinedButton
        ExampleCard(
          title: 'OutlinedButton',
          description: '边框按钮',
          child: Wrap(
            spacing: 12,
            runSpacing: 12,
            children: [
              OutlinedButton(
                onPressed: () => _showSnackBar(context, 'Outlined'),
                child: const Text('边框按钮'),
              ),
              OutlinedButton.icon(
                onPressed: () => _showSnackBar(context, 'Outlined Icon'),
                icon: const Icon(Icons.edit),
                label: const Text('编辑'),
              ),
            ],
          ),
          code: 'OutlinedButton(onPressed: () {}, child: Text("按钮"))',
        ),

        // TextButton
        ExampleCard(
          title: 'TextButton',
          description: '文本按钮，无背景',
          child: Wrap(
            spacing: 12,
            runSpacing: 12,
            children: [
              TextButton(
                onPressed: () => _showSnackBar(context, 'TextButton'),
                child: const Text('文本按钮'),
              ),
              TextButton.icon(
                onPressed: () => _showSnackBar(context, 'TextButton Icon'),
                icon: const Icon(Icons.link),
                label: const Text('链接'),
              ),
            ],
          ),
          code: 'TextButton(onPressed: () {}, child: Text("按钮"))',
        ),

        // FloatingActionButton
        ExampleCard(
          title: 'FloatingActionButton',
          description: '浮动操作按钮',
          child: Wrap(
            spacing: 16,
            runSpacing: 16,
            children: [
              FloatingActionButton(
                heroTag: 'fab1',
                onPressed: () => _showSnackBar(context, 'FAB'),
                child: const Icon(Icons.add),
              ),
              FloatingActionButton.small(
                heroTag: 'fab2',
                onPressed: () => _showSnackBar(context, 'FAB Small'),
                child: const Icon(Icons.edit),
              ),
              FloatingActionButton.extended(
                heroTag: 'fab3',
                onPressed: () => _showSnackBar(context, 'FAB Extended'),
                icon: const Icon(Icons.create),
                label: const Text('创建'),
              ),
              FloatingActionButton.large(
                heroTag: 'fab4',
                onPressed: () => _showSnackBar(context, 'FAB Large'),
                child: const Icon(Icons.star),
              ),
            ],
          ),
          code: 'FloatingActionButton(onPressed: () {}, child: Icon(Icons.add))',
        ),

        const ExampleSectionHeader(title: '输入组件', icon: Icons.edit),

        // TextField
        ExampleCard(
          title: 'TextField',
          description: '文本输入框',
          child: Column(
            children: [
              TextField(
                decoration: InputDecoration(
                  labelText: '用户名',
                  hintText: '请输入用户名',
                  prefixIcon: const Icon(Icons.person),
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                ),
              ),
              const SizedBox(height: 12),
              TextField(
                obscureText: true,
                decoration: InputDecoration(
                  labelText: '密码',
                  hintText: '请输入密码',
                  prefixIcon: const Icon(Icons.lock),
                  suffixIcon: IconButton(
                    icon: const Icon(Icons.visibility),
                    onPressed: () {},
                  ),
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                ),
              ),
              const SizedBox(height: 12),
              TextField(
                maxLines: 3,
                decoration: InputDecoration(
                  labelText: '多行输入',
                  hintText: '请输入内容...',
                  alignLabelWithHint: true,
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                ),
              ),
            ],
          ),
          code: 'TextField(decoration: InputDecoration(labelText: "标签"))',
        ),

        // TextFormField
        ExampleCard(
          title: 'TextFormField',
          description: '带验证的表单输入框',
          child: Form(
            child: Column(
              children: [
                TextFormField(
                  decoration: const InputDecoration(
                    labelText: '邮箱',
                    prefixIcon: Icon(Icons.email),
                  ),
                  validator: (value) {
                    if (value == null || value.isEmpty) {
                      return '请输入邮箱';
                    }
                    if (!value.contains('@')) {
                      return '请输入有效的邮箱地址';
                    }
                    return null;
                  },
                ),
                const SizedBox(height: 12),
                TextFormField(
                  decoration: const InputDecoration(
                    labelText: '手机号',
                    prefixIcon: Icon(Icons.phone),
                  ),
                  keyboardType: TextInputType.phone,
                  validator: (value) {
                    if (value == null || value.length != 11) {
                      return '请输入11位手机号';
                    }
                    return null;
                  },
                ),
              ],
            ),
          ),
          code: 'TextFormField(validator: (value) => null)',
        ),

        const SizedBox(height: 24),
      ],
    );
  }

  Widget _buildImageItem(IconData icon, String label) {
    return Column(
      children: [
        Icon(icon, size: 32, color: Colors.blue),
        const SizedBox(height: 4),
        Text(label, style: const TextStyle(fontSize: 12)),
      ],
    );
  }

  Widget _buildIconItem(IconData icon, String label) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        Icon(icon, size: 32),
        const SizedBox(height: 4),
        Text(label, style: const TextStyle(fontSize: 10)),
      ],
    );
  }

  void _showSnackBar(BuildContext context, String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        behavior: SnackBarBehavior.floating,
        duration: const Duration(seconds: 1),
      ),
    );
  }
}
