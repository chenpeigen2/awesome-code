"""
Streamlit 综合示例 Demo
展示了 Streamlit 的核心功能：布局、图表、组件、交互、缓存等
运行方式: uv run streamlit run main.py
"""

import streamlit as st
import pandas as pd
import numpy as np
import altair as alt
import time
from datetime import date


# ---------- 页面配置 ----------
st.set_page_config(
    page_title="Streamlit 功能演示",
    page_icon="🚀",
    layout="wide",
    initial_sidebar_state="expanded",
)

# ---------- 侧边栏导航 ----------
st.sidebar.title("🚀 Streamlit Demo")
page = st.sidebar.radio(
    "选择示例页面",
    ["🏠 首页", "📊 图表展示", "🧩 交互组件", "📋 数据表格", "🎨 布局示例", "💬 聊天界面"],
)
st.sidebar.markdown("---")
st.sidebar.markdown("### 关于")
st.sidebar.info("Streamlit 功能综合演示，涵盖常用 API 和组件。")


# ==============================================================
# 首页
# ==============================================================
if page == "🏠 首页":
    st.title("🚀 Streamlit 功能演示")
    st.markdown("欢迎体验 Streamlit！纯 Python 快速构建数据 Web 应用。")

    st.markdown("### 快速上手")
    st.code(
        """
import streamlit as st
x = st.slider("选择一个值")
st.write(x, "的平方是", x * x)
""",
        language="python",
    )

    st.markdown("### 试一试")
    c1, c2 = st.columns(2)
    with c1:
        x = st.slider("选择一个值", 0, 100, 25)
    with c2:
        st.metric("计算结果", f"{x}² = {x**2}")

    st.markdown("---")
    st.markdown("### 核心特性")
    for col, (title, desc) in zip(
        st.columns(4),
        [
            ("⚡ 极速开发", "纯 Python，无需前端知识"),
            ("🔄 热重载", "保存即刷新，实时预览"),
            ("📊 数据可视化", "内置多种图表支持"),
            ("🧩 丰富组件", "滑块、按钮、表格等"),
        ],
    ):
        col.markdown(f"**{title}**  \n{desc}")


# ==============================================================
# 图表展示
# ==============================================================
elif page == "📊 图表展示":
    st.title("📊 图表展示")

    chart_type = st.selectbox("选择图表类型", ["折线图", "柱状图", "面积图", "散点图"])

    @st.cache_data
    def generate_chart_data():
        np.random.seed(42)
        dates = pd.date_range("2025-01-01", periods=100)
        return pd.DataFrame(
            {
                "date": dates,
                "销售额": np.random.randn(100).cumsum() + 50,
                "利润": np.random.randn(100).cumsum() + 20,
                "客户数": np.random.randint(10, 100, 100),
            }
        )

    df_chart = generate_chart_data()

    cl, cr = st.columns([2, 1])
    with cl:
        if chart_type == "折线图":
            st.line_chart(df_chart.set_index("date")[["销售额", "利润"]])
        elif chart_type == "柱状图":
            st.bar_chart(df_chart.set_index("date")[["客户数"]])
        elif chart_type == "面积图":
            src = df_chart.melt("date", var_name="指标", value_name="值")
            st.altair_chart(
                alt.Chart(src)
                .mark_area(opacity=0.4)
                .encode(x="date:T", y=alt.Y("值:Q", stack=None), color="指标:N"),
                use_container_width=True,
            )
        else:
            st.altair_chart(
                alt.Chart(df_chart)
                .mark_circle(size=60)
                .encode(
                    x="销售额",
                    y="利润",
                    color="客户数",
                    tooltip=["date", "销售额", "利润", "客户数"],
                ),
                use_container_width=True,
            )
    with cr:
        st.markdown("**数据摘要**")
        st.dataframe(df_chart.describe(), use_container_width=True)

    st.markdown("---")
    st.subheader("🗺 地图可视化")

    @st.cache_data
    def generate_map_data():
        np.random.seed(0)
        return pd.DataFrame(
            {"lat": np.random.randn(200) * 2 + 39.9, "lon": np.random.randn(200) * 2 + 116.4}
        )

    st.map(generate_map_data(), size=10)


# ==============================================================
# 交互组件
# ==============================================================
elif page == "🧩 交互组件":
    st.title("🧩 交互组件")

    st.subheader("输入组件")
    c1, c2, c3 = st.columns(3)

    with c1:
        name = st.text_input("姓名", placeholder="请输入姓名")
        age = st.number_input("年龄", min_value=0, max_value=150, value=25)
        birthday = st.date_input("生日", date(2000, 1, 1))

    with c2:
        color = st.color_picker("选择颜色", "#00f900")
        score = st.slider("评分", 0.0, 10.0, 7.5, 0.5)
        tags = st.multiselect("标签", ["Python", "Java", "Go", "Rust", "C++"], ["Python"])

    with c3:
        agree = st.checkbox("同意协议")
        plan = st.radio("方案", ["免费版", "专业版", "企业版"])
        st.file_uploader("上传文件", type=["csv", "json"])

    st.markdown("---")
    st.subheader("输入结果")
    rc = st.columns(3)
    rc[0].metric("姓名", name or "未填写")
    rc[1].metric("年龄", f"{age} 岁")
    rc[2].metric("评分", f"{score}/10")
    st.write(f"**生日:** {birthday}  |  **方案:** {plan}  |  **标签:** {', '.join(tags)}")
    st.write(f"**颜色:** `{color}`  |  **同意协议:** {'✅' if agree else '❌'}")

    st.markdown("---")
    st.subheader("按钮与进度")
    bc = st.columns(4)
    if bc[0].button("普通按钮"):
        st.toast("按钮被点击了!", icon="🎉")
    if bc[1].button("下载 CSV"):
        csv_data = pd.DataFrame({"name": [name], "age": [age]}).to_csv(index=False)
        st.download_button("确认下载", csv_data, "data.csv", "text/csv")
    if bc[2].button("运行进度条"):
        bar = st.progress(0)
        for i in range(100):
            bar.progress(i + 1)
            time.sleep(0.01)
        st.success("完成!")
    bc[3].link_button("访问 Streamlit 官网", "https://streamlit.io")


# ==============================================================
# 数据表格
# ==============================================================
elif page == "📋 数据表格":
    st.title("📋 数据表格")

    @st.cache_data
    def generate_table_data(n=50):
        np.random.seed(1)
        return pd.DataFrame(
            {
                "ID": range(1, n + 1),
                "姓名": [f"用户_{i}" for i in range(1, n + 1)],
                "部门": np.random.choice(["研发", "市场", "运营", "设计", "产品"], n),
                "薪资": np.random.randint(8000, 50000, n),
                "入职日期": pd.date_range("2022-01-01", periods=n, freq="W"),
                "绩效": np.random.choice(["S", "A", "B", "C"], n, p=[0.1, 0.4, 0.4, 0.1]),
            }
        )

    df = generate_table_data()

    st.subheader("数据筛选")
    fc = st.columns(3)
    departments = fc[0].multiselect("选择部门", df["部门"].unique(), list(df["部门"].unique()))
    perf_levels = fc[1].multiselect("选择绩效", df["绩效"].unique(), list(df["绩效"].unique()))
    salary_range = fc[2].slider(
        "薪资范围", int(df["薪资"].min()), int(df["薪资"].max()), (8000, 50000)
    )

    filtered = df[
        df["部门"].isin(departments)
        & df["绩效"].isin(perf_levels)
        & df["薪资"].between(salary_range[0], salary_range[1])
    ]

    st.markdown(f"**共 {len(filtered)} 条记录**")
    st.dataframe(filtered, use_container_width=True, hide_index=True)

    st.markdown("---")
    st.subheader("部门薪资统计")
    stat = filtered.groupby("部门")["薪资"].agg(["mean", "min", "max", "count"]).round(0)
    stat.columns = ["平均薪资", "最低薪资", "最高薪资", "人数"]
    st.dataframe(stat, use_container_width=True)
    st.bar_chart(filtered.groupby("部门")["薪资"].mean())


# ==============================================================
# 布局示例
# ==============================================================
elif page == "🎨 布局示例":
    st.title("🎨 布局示例")

    # Tabs
    st.subheader("Tabs 标签页")
    tab1, tab2, tab3 = st.tabs(["🐱 猫", "🐶 狗", "🦊 狐狸"])
    with tab1:
        st.markdown("猫是一种独立的动物。")
        st.metric("体重", "4.5 kg", "+0.3 kg")
    with tab2:
        st.markdown("狗是人类最好的朋友。")
        st.metric("体重", "12 kg", "+1.2 kg")
    with tab3:
        st.markdown("狐狸非常聪明。")
        st.metric("体重", "6 kg", "-0.5 kg")

    # Expander
    st.markdown("---")
    st.subheader("Expander 折叠面板")
    with st.expander("Streamlit 是什么？"):
        st.write("Streamlit 是一个开源 Python 框架，可以快速创建数据应用。")
    with st.expander("如何安装？"):
        st.code("uv add streamlit", language="bash")

    # Columns
    st.markdown("---")
    st.subheader("Columns 多列布局 (比例 1:2:1)")
    for i, col in enumerate(st.columns([1, 2, 1])):
        col.markdown(
            f"""
            <div style="background:#f0f2f6;padding:20px;border-radius:8px;text-align:center">
                <h3>第 {i + 1} 列</h3>
                <p>宽度比例: {[1, 2, 1][i]}</p>
            </div>
            """,
            unsafe_allow_html=True,
        )

    # Empty
    st.markdown("---")
    st.subheader("Empty 占位")
    empty_slot = st.empty()
    if st.button("倒计时"):
        for i in range(5, 0, -1):
            empty_slot.markdown(f"### ⏳ {i} 秒后消失...")
            time.sleep(1)
        empty_slot.markdown("### ✅ 完成!")


# ==============================================================
# 聊天界面
# ==============================================================
elif page == "💬 聊天界面":
    st.title("💬 聊天界面")
    st.markdown("使用 `st.chat_message` 和 `st.chat_input` 构建。")

    if "chat_history" not in st.session_state:
        st.session_state.chat_history = []

    for msg in st.session_state.chat_history:
        with st.chat_message(msg["role"]):
            st.markdown(msg["content"])

    def mock_reply(prompt: str):
        special = {"你好": "你好！我是 Streamlit 聊天机器人 🤖", "帮助": "问我关于 Streamlit 的任何问题！"}
        for key, resp in special.items():
            if key in prompt:
                return resp
        return f'你说: "{prompt}"。输入包含"你好"或"帮助"有特殊回复！'

    if prompt := st.chat_input("输入消息..."):
        with st.chat_message("user"):
            st.markdown(prompt)
        st.session_state.chat_history.append({"role": "user", "content": prompt})

        with st.chat_message("assistant"):
            response = st.write_stream(char for char in mock_reply(prompt))
        st.session_state.chat_history.append({"role": "assistant", "content": response})

    st.sidebar.markdown("---")
    if st.sidebar.button("清空聊天记录"):
        st.session_state.chat_history = []
        st.rerun()
