package com.peter.androidx.apt;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.peter.apt.annotation.Bindable;
import com.peter.androidx.R;

/**
 * APT测试Activity
 */
@Bindable
public class AptTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apt_test);
        
        // 编译成功后取消注释：
         AptTestActivity_Binding.bind(this);
    }
}
