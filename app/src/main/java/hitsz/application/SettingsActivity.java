package hitsz.application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.example.myapplication.R;

public class SettingsActivity extends Activity {
    private Switch musicSwitch;
    private Button backButton;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "game_settings";
    private static final String MUSIC_ENABLED_KEY = "music_enabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 初始化SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // 初始化UI组件
        musicSwitch = findViewById(R.id.music_switch);
        backButton = findViewById(R.id.back_button);

        // 加载保存的音乐设置
        boolean musicEnabled = sharedPreferences.getBoolean(MUSIC_ENABLED_KEY, true);
        musicSwitch.setChecked(musicEnabled);

        // 音乐开关监听器
        musicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 保存音乐设置
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(MUSIC_ENABLED_KEY, isChecked);
            editor.apply();
        });

        // 返回按钮监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // 静态方法，用于获取音乐是否开启
    public static boolean isMusicEnabled(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(MUSIC_ENABLED_KEY, true);
    }
}