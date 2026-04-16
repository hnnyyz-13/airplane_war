package hitsz.application;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;

/**
 * 登录界面
 */
public class LoginActivity extends Activity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        // 初始化控件
        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);
        cancelButton = findViewById(R.id.cancel_button);

        // 加载已存储的用户信息
        loadUserInfo();

        // 设置登录按钮点击事件
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // 验证登录
                if (validateLogin(username, password)) {
                    // 登录成功
                    saveUserInfo(username, password);
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    // 返回MainActivity
                    Intent intent = new Intent();
                    intent.putExtra("isLoggedIn", true);
                    intent.putExtra("username", username);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    // 登录失败
                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 设置取消按钮点击事件
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // 保存用户信息到本地存储
    private void saveUserInfo(String username, String password) {
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }

    // 从本地存储加载用户信息
    private void loadUserInfo() {
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        String username = sp.getString("username", "");
        String password = sp.getString("password", "");
        if (!username.isEmpty()) {
            usernameEditText.setText(username);
        }
    }

    // 验证登录
    private boolean validateLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return false;
        }

        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        String savedUsername = sp.getString("username", "");
        String savedPassword = sp.getString("password", "");

        if (savedUsername.isEmpty() && savedPassword.isEmpty()) {
            // 首次登录，直接存储用户信息
            return true;
        } else {
            // 非首次登录，验证用户名和密码
            return savedUsername.equals(username) && savedPassword.equals(password);
        }
    }
}