package com.peter.androidx.apt;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.peter.apt.annotation.Bindable;
import com.peter.apt.annotation.BindView;
import com.peter.androidx.R;

/**
 * APT测试Activity
 */
@Bindable
public class AptTestActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.btn_click)
    Button btnClick;

    @BindView(R.id.btn_click2)
    Button btnClick2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apt_test);

        // 调用生成的绑定类
        AptTestActivity_Binding.bind(this);

        tvTitle.setText("APT 绑定成功！");

        btnClick.setOnClickListener(v ->
            Toast.makeText(this, "按钮1被点击", Toast.LENGTH_SHORT).show()
        );

        btnClick2.setOnClickListener(v ->
            Toast.makeText(this, "按钮2被点击", Toast.LENGTH_SHORT).show()
        );
    }
}
