package hitsz.application;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import com.example.myapplication.R;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 程序入口
 * @author edu.hitsz
 */
public class MainActivity extends Activity {

    private Game game;
    private boolean hasSelectedDifficulty = false;
    private TextView loginStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 设置主菜单布局
        setContentView(R.layout.main_menu);

        // 初始化登录状态显示
        loginStatusTextView = findViewById(R.id.login_status_text_view);
        updateLoginStatus();

        // 初始化按钮
        initializeButtons();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // 检查是否需要直接开始游戏
        if(intent.getBooleanExtra("startGame", false)){
            hasSelectedDifficulty = true;
            // 确保旧的游戏实例已被销毁
            if (game != null) {
                game.stopGame();
                game = null;
            }
            // 延迟一点时间再设置布局，确保SurfaceView完全销毁
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    game = new Game(MainActivity.this);
                    setContentView(game);
                    game.startGame();
                }
            }, 20);
            return;
        }

        // 当从排行榜返回时，重置游戏状态
        if (game != null) {
            game.stopGame();
            game = null;
        }
        hasSelectedDifficulty = false;
        // 延迟一点时间再设置布局，确保SurfaceView完全销毁
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 重新设置主菜单布局
                setContentView(R.layout.main_menu);
                // 重新初始化登录状态显示
                loginStatusTextView = findViewById(R.id.login_status_text_view);
                updateLoginStatus();
                // 重新初始化按钮
                initializeButtons();
            }
        }, 20);
    }

    // 初始化按钮的方法
    private void initializeButtons() {
        Button startGameButton = findViewById(R.id.start_game_button);
        Button difficultyButton = findViewById(R.id.difficulty_button);
        Button rankButton = findViewById(R.id.rank_button);
        Button settingsButton = findViewById(R.id.settings_button);
        Button loginButton = findViewById(R.id.login_button);
        Button logoutButton = findViewById(R.id.logout_button);
        Button onlineGameButton = findViewById(R.id.online_game_button);

        // 开始游戏按钮点击事件
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开难度设置界面
                Intent intent = new Intent(MainActivity.this, DifficultySelectionActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        // 难度设置按钮点击事件
        difficultyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开难度设置界面
                Intent intent = new Intent(MainActivity.this, DifficultySelectionActivity.class);
                startActivity(intent);
            }
        });

        // 排行榜按钮点击事件
        rankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开排行榜界面
                Intent intent = new Intent(MainActivity.this, RankListActivity.class);
                intent.putExtra("difficulty", DifficultySelectionActivity.difficultySet);
                startActivity(intent);
            }
        });

        // 设置按钮点击事件
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开设置界面
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        // 登录按钮点击事件
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 无论是否已登录，都打开登录界面进行验证
                // 这样用户可以随时验证登录功能
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, 2);
            }
        });

        // 退出登录按钮点击事件
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 调用退出登录方法
                logout();
            }
        });

        // 在线对战按钮点击事件
        onlineGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 检查是否已登录
                if (!isLoggedIn()) {
                    Toast.makeText(MainActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent, 2);
                } else {
                    // 跳转到在线对战界面
                    Intent intent = new Intent(MainActivity.this, OnlineActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void restartGame() {
        // 重置难度选择标志
        hasSelectedDifficulty = false;
        // 确保旧的游戏实例已被销毁
        if (game != null) {
            game.stopGame();
            game = null;
        }
        // 延迟一点时间再设置布局，确保SurfaceView完全销毁
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 重新初始化游戏
                game = new Game(MainActivity.this);
                setContentView(game);
                game.startGame();
            }
        }, 100);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (game != null) {
            game.stopGame();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // 只有当用户点击了开始游戏按钮时才启动游戏
            hasSelectedDifficulty = true;
            // 确保旧的游戏实例已被销毁
            if (game != null) {
                game.stopGame();
                game = null;
            }
            // 延迟一点时间再设置布局，确保SurfaceView完全销毁
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    game = new Game(MainActivity.this);
                    setContentView(game);
                    game.startGame();
                }
            }, 100);
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            // 登录成功
            boolean isLoggedIn = data.getBooleanExtra("isLoggedIn", false);
            if (isLoggedIn) {
                // 显示登录状态
                updateLoginStatus();
                String username = data.getStringExtra("username");
                if (username != null && !username.isEmpty()) {
                    Toast.makeText(MainActivity.this, "欢迎回来，" + username, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 只有当游戏正在进行中时才恢复游戏
        // 从排行榜返回时不自动开始游戏
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (game != null) {
            game.stopGame();
        }
    }

    // 检查登录状态
    private boolean isLoggedIn() {
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        return sp.getBoolean("isLoggedIn", false);
    }

    // 更新登录状态显示
    private void updateLoginStatus() {
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);
        String username = sp.getString("username", "");

        if (isLoggedIn && !username.isEmpty()) {
            loginStatusTextView.setText("当前登录: " + username);
        } else {
            loginStatusTextView.setText("未登录");
        }
    }

    // 退出登录
    private void logout() {
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        sp.edit().clear().apply();
        updateLoginStatus();
        Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();
    }
}