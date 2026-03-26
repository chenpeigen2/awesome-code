// ==================== 应用状态 ====================
const state = {
    currentLevel: 1,
    currentDemo: 'variables',
    isLoading: false
};

// ==================== Demo 配置 ====================
const demoConfig = {
    // Level 1
    variables: { level: 1, command: 'demo_variables', title: '变量与常量' },
    types: { level: 1, command: 'demo_types', title: '数据类型' },
    strings: { level: 1, command: 'demo_strings', title: '字符串操作' },

    // Level 2
    functions: { level: 2, command: 'demo_functions', title: '函数定义' },
    control_flow: { level: 2, command: 'demo_control_flow', title: '控制流' },
    loops: { level: 2, command: 'demo_loops', title: '循环' },

    // Level 3
    ownership: { level: 3, command: 'demo_ownership', title: '所有权规则' },
    borrowing: { level: 3, command: 'demo_borrowing', title: '借用引用' },
    lifetimes: { level: 3, command: 'demo_lifetimes', title: '生命周期' },

    // Level 4
    structs: { level: 4, command: 'demo_structs', title: '结构体' },
    enums: { level: 4, command: 'demo_enums', title: '枚举' },
    pattern_matching: { level: 4, command: 'demo_pattern_matching', title: '模式匹配' },

    // Level 5
    traits: { level: 5, command: 'demo_traits', title: 'Trait定义' },
    generics: { level: 5, command: 'demo_generics', title: '泛型' },
    trait_bounds: { level: 5, command: 'demo_trait_bounds', title: 'Trait Bounds' },

    // Level 6
    result: { level: 6, command: 'demo_result', title: 'Result类型' },
    option: { level: 6, command: 'demo_option', title: 'Option类型' },
    custom_errors: { level: 6, command: 'demo_custom_errors', title: '自定义错误' },

    // Level 7
    vectors: { level: 7, command: 'demo_vectors', title: 'Vector' },
    hashmaps: { level: 7, command: 'demo_hashmaps', title: 'HashMap' },
    iterators: { level: 7, command: 'demo_iterators', title: '迭代器' },

    // Level 8
    threads: { level: 8, command: 'demo_threads', title: '线程' },
    channels: { level: 8, command: 'demo_channels', title: '通道' },
    async: { level: 8, command: 'demo_async', title: '异步编程' },

    // Level 9
    file_io: { level: 9, command: 'demo_file_io', title: '文件操作' },
    json: { level: 9, command: 'demo_json', title: 'JSON序列化' },
    path_operations: { level: 9, command: 'demo_path_operations', title: '路径处理' },

    // Level 10
    api_client: { level: 10, command: 'demo_api_client', title: 'API客户端' },
    cache: { level: 10, command: 'demo_cache', title: '缓存系统' },
    state_machine: { level: 10, command: 'demo_state_machine', title: '状态机' }
};

// ==================== DOM 元素 ====================
const elements = {
    navLinks: document.querySelectorAll('.nav-link'),
    pageTitle: document.querySelector('.page-title'),
    loading: document.getElementById('loading'),
    demoContent: document.getElementById('demoContent'),
    welcomePage: document.getElementById('welcomePage'),
    refreshBtn: document.getElementById('refreshBtn')
};

// ==================== Tauri 命令调用 ====================
async function invokeTauri(command) {
    try {
        // 检查是否在 Tauri 环境中
        if (window.__TAURI__) {
            const { invoke } = window.__TAURI__.core;
            return await invoke(command);
        } else {
            // 开发模式：返回模拟数据
            return getMockData(command);
        }
    } catch (error) {
        console.error('Tauri 命令调用失败:', error);
        throw error;
    }
}

// ==================== 模拟数据（开发模式） ====================
function getMockData(command) {
    const mockData = {
        demo_variables: {
            explanation: "Rust 的变量系统设计强调安全性和可预测性。默认不可变的设计迫使开发者明确表达'我需要修改这个值'的意图。",
            code: `// 不可变变量\nlet x = 5;\n\n// 可变变量\nlet mut y = 10;\ny = 20;\n\n// 常量\nconst MAX_POINTS: u32 = 100_000;`,
            output: `不可变变量 x = 5\n可变变量 y 初始值 = 10\ny 修改后 = 20\n常量 MAX_POINTS = 100000`,
            tips: [
                "Rust 变量默认不可变，这是安全性的重要特性",
                "使用 mut 关键字声明可变变量",
                "变量遮蔽允许复用变量名，甚至改变类型"
            ]
        },
        demo_types: [
            {
                category: "整数类型",
                types: [
                    { name: "i8", size_bytes: 1, range_or_example: "-128 到 127", example_code: "let a: i8 = -128;" },
                    { name: "u8", size_bytes: 1, range_or_example: "0 到 255", example_code: "let b: u8 = 255;" },
                    { name: "i32", size_bytes: 4, range_or_example: "约 -21亿 到 21亿", example_code: "let c = 42;" }
                ]
            },
            {
                category: "浮点类型",
                types: [
                    { name: "f32", size_bytes: 4, range_or_example: "单精度浮点数", example_code: "let pi: f32 = 3.14159;" },
                    { name: "f64", size_bytes: 8, range_or_example: "双精度浮点数", example_code: "let e = 2.71828;" }
                ]
            }
        ],
        demo_strings: {
            "类型说明": "String: 堆分配的可增长字符串\n&str: 字符串切片",
            "创建String": 'String::from("hello")\n"world".to_string()',
            "最佳实践": "使用 String 存储需要修改的字符串\n使用 &str 作为函数参数"
        }
    };

    return mockData[command] || { message: `Mock data for ${command}` };
}

// ==================== UI 更新函数 ====================
function showLoading() {
    elements.loading.style.display = 'flex';
    elements.demoContent.style.display = 'none';
    elements.welcomePage.style.display = 'none';
}

function hideLoading() {
    elements.loading.style.display = 'none';
}

function showWelcome() {
    elements.welcomePage.style.display = 'block';
    elements.demoContent.style.display = 'none';
}

function showDemoContent() {
    elements.demoContent.style.display = 'block';
    elements.welcomePage.style.display = 'none';
}

function updatePageTitle(level, title) {
    const badge = document.createElement('span');
    badge.className = 'level-badge';
    badge.textContent = `Level ${level}`;

    const textNode = document.createTextNode(` Rust ${title}`);

    elements.pageTitle.textContent = '';
    elements.pageTitle.appendChild(badge);
    elements.pageTitle.appendChild(textNode);
}

// ==================== 安全的 DOM 创建 ====================
function createElement(tag, className, textContent) {
    const el = document.createElement(tag);
    if (className) el.className = className;
    if (textContent) el.textContent = textContent;
    return el;
}

function createCodeBlock(code, lang = 'rust') {
    const block = createElement('div', 'code-block');
    const header = createElement('div', 'code-header');
    const langSpan = createElement('span', 'code-lang', lang);
    const copyBtn = createElement('button', 'copy-btn', '复制');
    copyBtn.onclick = function() { copyCode(this); };
    header.appendChild(langSpan);
    header.appendChild(copyBtn);

    const content = createElement('div', 'code-content');
    const pre = createElement('pre', null, code);
    content.appendChild(pre);

    block.appendChild(header);
    block.appendChild(content);
    return block;
}

function createOutputBlock(output) {
    return createElement('div', 'output-block', output);
}

function createTipsBlock(tips) {
    const block = createElement('div', 'tips-block');
    const title = createElement('h4', null, '💡 最佳实践');
    block.appendChild(title);

    const ul = createElement('ul');
    tips.forEach(tip => {
        const li = createElement('li', null, tip);
        ul.appendChild(li);
    });
    block.appendChild(ul);
    return block;
}

// ==================== 内容渲染 ====================
function renderDemoContent(demo, data) {
    const config = demoConfig[demo];

    // 清空内容区
    while (elements.demoContent.firstChild) {
        elements.demoContent.removeChild(elements.demoContent.firstChild);
    }

    const card = createElement('div', 'demo-card');
    const header = createElement('div', 'demo-card-header');
    const title = createElement('h3', 'demo-card-title');

    const icon = createElement('span', 'icon', '📚');
    title.appendChild(icon);
    title.appendChild(document.createTextNode(` ${config.title}`));
    header.appendChild(title);
    card.appendChild(header);

    const body = createElement('div', 'demo-card-body');

    // 根据数据类型渲染
    if (data.explanation) {
        const p = createElement('p', 'explanation', data.explanation);
        body.appendChild(p);
    }

    if (data.code) {
        const h4 = createElement('h4', null, '代码示例');
        body.appendChild(h4);
        body.appendChild(createCodeBlock(data.code));
    }

    if (data.output) {
        const h4 = createElement('h4', null, '运行输出');
        body.appendChild(h4);
        body.appendChild(createOutputBlock(data.output));
    }

    if (data.tips && Array.isArray(data.tips)) {
        body.appendChild(createTipsBlock(data.tips));
    }

    // 处理 HashMap/Object 类型数据
    if (typeof data === 'object' && !Array.isArray(data) && !data.explanation) {
        Object.entries(data).forEach(([key, value]) => {
            const section = createElement('div', 'demo-section');
            section.style.marginBottom = '25px';

            const h4 = createElement('h4', null, key);
            section.appendChild(h4);

            const val = typeof value === 'string' ? value : JSON.stringify(value, null, 2);
            if (val.includes('fn ') || val.includes('let ') || val.includes('struct ')) {
                section.appendChild(createCodeBlock(val));
            } else {
                const p = createElement('p', 'explanation', val);
                section.appendChild(p);
            }

            body.appendChild(section);
        });
    }

    // 处理数组类型数据
    if (Array.isArray(data)) {
        data.forEach(item => {
            const section = createElement('div', 'demo-section');
            section.style.marginBottom = '25px';

            if (item.category) {
                const h4 = createElement('h4', null, item.category);
                section.appendChild(h4);

                const table = createTypeTable(item.types);
                section.appendChild(table);
            } else if (item.concept) {
                const h4 = createElement('h4', null, item.concept);
                section.appendChild(h4);

                if (item.code) {
                    section.appendChild(createCodeBlock(item.code));
                }
                if (item.result) {
                    section.appendChild(createOutputBlock(item.result));
                }
            } else if (item.rule) {
                const h4 = createElement('h4', null, item.rule);
                h4.style.color = 'var(--primary)';
                section.appendChild(h4);

                if (item.explanation) {
                    const p = createElement('p', 'explanation', item.explanation);
                    section.appendChild(p);
                }
                if (item.code_example) {
                    section.appendChild(createCodeBlock(item.code_example));
                }
            }

            body.appendChild(section);
        });
    }

    card.appendChild(body);
    elements.demoContent.appendChild(card);
    showDemoContent();
}

function createTypeTable(types) {
    const table = createElement('table', 'type-table');
    const thead = document.createElement('thead');
    const headerRow = document.createElement('tr');

    ['类型', '大小', '范围/示例', '代码示例'].forEach(text => {
        const th = createElement('th', null, text);
        headerRow.appendChild(th);
    });
    thead.appendChild(headerRow);
    table.appendChild(thead);

    const tbody = document.createElement('tbody');
    types.forEach(t => {
        const row = document.createElement('tr');

        const tdName = createElement('td');
        const codeName = createElement('code', null, t.name);
        tdName.appendChild(codeName);
        row.appendChild(tdName);

        row.appendChild(createElement('td', null, `${t.size_bytes} 字节`));
        row.appendChild(createElement('td', null, t.range_or_example));

        const tdCode = createElement('td');
        const codeEx = createElement('code', null, t.example_code);
        tdCode.appendChild(codeEx);
        row.appendChild(tdCode);

        tbody.appendChild(row);
    });
    table.appendChild(tbody);

    return table;
}

// ==================== 工具函数 ====================
function copyCode(button) {
    const codeBlock = button.closest('.code-block');
    const code = codeBlock.querySelector('pre').textContent;

    navigator.clipboard.writeText(code).then(() => {
        button.textContent = '已复制!';
        setTimeout(() => {
            button.textContent = '复制';
        }, 2000);
    });
}

// ==================== 事件处理 ====================
async function loadDemo(demo) {
    const config = demoConfig[demo];
    if (!config) return;

    showLoading();
    state.currentDemo = demo;
    state.currentLevel = config.level;

    try {
        const data = await invokeTauri(config.command);
        updatePageTitle(config.level, config.title);
        renderDemoContent(demo, data);
    } catch (error) {
        console.error('加载 Demo 失败:', error);

        // 清空并显示错误
        while (elements.demoContent.firstChild) {
            elements.demoContent.removeChild(elements.demoContent.firstChild);
        }

        const card = createElement('div', 'demo-card');
        const body = createElement('div', 'demo-card-body');

        const errorP = createElement('p', null);
        errorP.style.color = 'var(--error)';
        errorP.textContent = `加载失败: ${error.message}`;
        body.appendChild(errorP);

        const hintP = createElement('p', null);
        hintP.style.color = 'var(--text-muted)';
        hintP.style.marginTop = '10px';
        hintP.textContent = '请确保已安装 Rust 并运行 Tauri 应用。';
        body.appendChild(hintP);

        card.appendChild(body);
        elements.demoContent.appendChild(card);
        showDemoContent();
    } finally {
        hideLoading();
    }
}

// ==================== 初始化 ====================
function init() {
    // 导航链接点击事件
    elements.navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();

            // 更新活动状态
            elements.navLinks.forEach(l => l.classList.remove('active'));
            link.classList.add('active');

            // 加载对应 Demo
            const demo = link.dataset.demo;
            loadDemo(demo);
        });
    });

    // 刷新按钮
    elements.refreshBtn.addEventListener('click', () => {
        loadDemo(state.currentDemo);
    });

    // 显示欢迎页面
    showWelcome();
}

// 启动应用
document.addEventListener('DOMContentLoaded', init);
