package hitsz.application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.R;

/**
 * 在线对战结果界面
 */
public class OverActivity extends Activity {

    private TextView myNameTextView;
    private TextView myScoreTextView;
    private TextView opponentNameTextView;
    private TextView opponentScoreTextView;
    private TextView resultTextView;
    private Button backButton;
    private Button replayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        // 初始化控件
        myNameTextView = findViewById(R.id.result_my_name);
        myScoreTextView = findViewById(R.id.result_my_score);
        opponentNameTextView = findViewById(R.id.result_opponent_name);
        opponentScoreTextView = findViewById(R.id.result_opponent_score);
        resultTextView = findViewById(R.id.result_text);
        backButton = findViewById(R.id.back_button);
        replayButton = findViewById(R.id.replay_button);

        // 获取传递的数据
        Intent intent = getIntent();
        int myScore = intent.getIntExtra("myScore", 0);
        int otherScore = intent.getIntExtra("otherScore", 0);
        String opName = intent.getStringExtra("opName");
        String myName = intent.getStringExtra("myName");

        // 设置显示内容
        myNameTextView.setText(myName != null ? myName : "我");
        myScoreTextView.setText(String.valueOf(myScore));
        opponentNameTextView.setText(opName != null ? opName : "对手");
        opponentScoreTextView.setText(String.valueOf(otherScore));

        // 判断胜负
        if (myScore > otherScore) {
            resultTextView.setText("胜利！");
            resultTextView.setTextColor(0xFF99CC00); // 绿色
        } else if (myScore < otherScore) {
            resultTextView.setText("失败！");
            resultTextView.setTextColor(0xFFFF4444); // 红色
        } else {
            resultTextView.setText("平局！");
            resultTextView.setTextColor(0xFFFFFF00); // 黄色
        }

        // 返回主菜单
        backButton.setOnClickListener(v -> {
            Intent mainIntent = new Intent(OverActivity.this, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            finish();
        });

        // 再来一局
        replayButton.setOnClickListener(v -> {
            Intent onlineIntent = new Intent(OverActivity.this, OnlineActivity.class);
            startActivity(onlineIntent);
            finish();
        });
    }
}