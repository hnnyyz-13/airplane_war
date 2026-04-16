package hitsz.application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioButton;

import com.example.myapplication.R;

public class DifficultySelectionActivity extends Activity {
    private RadioGroup difficultyRadioGroup;
    private RadioButton easyRadioButton;
    private RadioButton normalRadioButton;
    private RadioButton hardRadioButton;
    private Button backButton;
    private Button startGameButton;
    
    // 难度设置，默认简单模式
    public static int difficultySet = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty_selection);
        
        // 初始化UI组件
        difficultyRadioGroup = findViewById(R.id.difficulty_radio_group);
        easyRadioButton = findViewById(R.id.easy_radio_button);
        normalRadioButton = findViewById(R.id.normal_radio_button);
        hardRadioButton = findViewById(R.id.hard_radio_button);
        backButton = findViewById(R.id.back_button);
        startGameButton = findViewById(R.id.start_game_button);
        
        // 根据当前难度设置选中对应的单选按钮
        switch (difficultySet) {
            case 0:
                easyRadioButton.setChecked(true);
                break;
            case 1:
                normalRadioButton.setChecked(true);
                break;
            case 2:
                hardRadioButton.setChecked(true);
                break;
        }
        
        // 难度选择监听器
        difficultyRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.easy_radio_button) {
                    difficultySet = 0;
                    ImageManager.ShiftBackground(0);
                } else if (checkedId == R.id.normal_radio_button) {
                    difficultySet = 1;
                    ImageManager.ShiftBackground(1);
                } else if (checkedId == R.id.hard_radio_button) {
                    difficultySet = 2;
                    ImageManager.ShiftBackground(2);
                }
            }
        });
        
        // 返回按钮监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 关闭当前Activity，返回上一个界面
            }
        });
        
        // 开始游戏按钮监听器
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 设置返回结果
                setResult(RESULT_OK);
                // 关闭当前Activity，返回MainActivity并启动游戏
                Intent intent = new Intent(DifficultySelectionActivity.this, MainActivity.class);
                intent.putExtra("startGame", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}