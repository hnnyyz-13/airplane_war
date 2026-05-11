package hitsz.application;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 登录界面
 */
public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";
    // 服务器IP地址
    public static final String SERVER_IP = "10.250.32.17";
    public static final int SERVER_PORT = 9999;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button cancelButton;
    private Button registerButton;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        // 初始化控件
        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);
        cancelButton = findViewById(R.id.cancel_button);
        registerButton = findViewById(R.id.register_button);

        // 加载已存储的用户信息
        loadUserInfo();

        // 设置登录按钮点击事件
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 通过网络登录
                new Thread(new Connect(username, password)).start();
            }
        });

        // 设置取消按钮点击事件
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 设置注册按钮点击事件
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // 初始化Handler
        handler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x11) {
                    // 登录成功 - 跳转到在线游戏界面
                    String username = (String) msg.obj;
                    saveUserInfo(username, passwordEditText.getText().toString());
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, OnlineActivity.class);
                    startActivity(intent);
                    finish();
                } else if (msg.what == 0x22) {
                    // 密码错误
                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                } else if (msg.what == 0x33) {
                    // 用户不存在
                    Toast.makeText(LoginActivity.this, "该用户不存在", Toast.LENGTH_SHORT).show();
                } else if (msg.what == 0x99) {
                    // 网络错误
                    Toast.makeText(LoginActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                }
            }
        };
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
        if (!password.isEmpty()) {
            passwordEditText.setText(password);
        }
    }

    // 网络连接线程
    public class Connect implements Runnable {
        private String userID;
        private String userPassword;

        public Connect(String userID, String userPassword) {
            this.userID = userID;
            this.userPassword = userPassword;
        }

        @Override
        public void run() {
            Socket socket = null;
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT), 5000);

                // 向服务器发送请求
                PrintWriter out = new PrintWriter(new OutputStreamWriter(
                        socket.getOutputStream(), StandardCharsets.UTF_8), true);

                // 组装JSON对象
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("operation", "login");
                jsonObject.put("ID", userID);
                jsonObject.put("PSW", userPassword);
                out.println(jsonObject.toString());

                // 读取服务器返回
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = in.readLine();

                if (Objects.equals(line, "login_success")) {
                    Message message = Message.obtain();
                    message.what = 0x11;
                    message.obj = userID;
                    handler.sendMessage(message);
                } else if (Objects.equals(line, "password_failed")) {
                    Message message = Message.obtain();
                    message.what = 0x22;
                    handler.sendMessage(message);
                } else if (Objects.equals(line, "userID_failed")) {
                    Message message = Message.obtain();
                    message.what = 0x33;
                    handler.sendMessage(message);
                }

                socket.close();

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = 0x99;
                handler.sendMessage(message);
            }
        }
    }
}