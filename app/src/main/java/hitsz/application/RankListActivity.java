package hitsz.application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;

import java.util.List;

import hitsz.ranklist.Scores;
import hitsz.ranklist.ScoresDaoImpl;

public class RankListActivity extends Activity {
    private LinearLayout rankListContainer;
    private Button backButton;
    private TextView titleTextView;
    
    private String[] title = new String[] {"简单难度排行榜", "普通难度排行榜", "困难难度排行榜"};
    private ScoresDaoImpl scoreRankEasy;
    private ScoresDaoImpl scoreRankNormal;
    private ScoresDaoImpl scoreRankHard;
    
    private int currentDifficulty = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_list);

        scoreRankEasy = new ScoresDaoImpl(0, this);
        scoreRankNormal = new ScoresDaoImpl(1, this);
        scoreRankHard = new ScoresDaoImpl(2, this);

        // 初始化UI组件
        rankListContainer = findViewById(R.id.rank_list_container);
        backButton = findViewById(R.id.back_button);
        titleTextView = findViewById(R.id.title_text_view);
        
        // 获取从上一个Activity传递的难度参数
        currentDifficulty = getIntent().getIntExtra("difficulty", 0);
        
        // 更新排行榜
        updateRank(currentDifficulty, -1, "");
        
        // 返回按钮监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 显式启动MainActivity，回到主目录
                Intent intent = new Intent(RankListActivity.this, MainActivity.class);
                // 设置标志，确保回到主目录而不是创建新的实例
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
    
    public void updateRank(int difficulty, int score, String playerID) {
        titleTextView.setText(title[difficulty]);
        
        ScoresDaoImpl scoresDao = (difficulty == 0) ? scoreRankEasy : ((difficulty == 1) ? scoreRankNormal : scoreRankHard);
        
        if (score != -1) {
            scoresDao.doAdd(new Scores(score, playerID));
            scoresDao.sortPrintSave();
        }
        
        List<Scores> scoresList = scoresDao.getAllScores();
        
        // 清空容器
        rankListContainer.removeAllViews();
        
        // 添加排行榜标题行
        View headerView = getLayoutInflater().inflate(R.layout.rank_item, null);
        TextView rankHeader = headerView.findViewById(R.id.rank_text_view);
        TextView playerIdHeader = headerView.findViewById(R.id.player_id_text_view);
        TextView scoreHeader = headerView.findViewById(R.id.score_text_view);
        Button deleteButtonHeader = headerView.findViewById(R.id.delete_button);
        
        rankHeader.setText("排名");
        playerIdHeader.setText("Player ID");
        scoreHeader.setText("Score");
        // 隐藏表头行的删除按钮，但保留空间
        deleteButtonHeader.setVisibility(View.INVISIBLE);
        
        headerView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        rankListContainer.addView(headerView);
        
        // 填充排行榜数据
        for (int i = 0; i < scoresList.size(); i++) {
            View itemView = getLayoutInflater().inflate(R.layout.rank_item, null);
            TextView rankTextView = itemView.findViewById(R.id.rank_text_view);
            TextView playerIdTextView = itemView.findViewById(R.id.player_id_text_view);
            TextView scoreTextView = itemView.findViewById(R.id.score_text_view);
            Button deleteButton = itemView.findViewById(R.id.delete_button);
            
            final int position = i;
            rankTextView.setText(String.valueOf(i + 1));
            playerIdTextView.setText(scoresList.get(i).getPlayerID());
            scoreTextView.setText(String.valueOf(scoresList.get(i).getScore()));
            
            // 删除按钮监听器
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scoresDao.deleteScore(scoresList.get(position).getPlayerID(), scoresList.get(position).getScore());
                    scoresDao.sortPrintSave();
                    updateRank(difficulty, -1, "");
                    Toast.makeText(RankListActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                }
            });
            
            rankListContainer.addView(itemView);
        }
    }
}