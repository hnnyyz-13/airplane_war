package hitsz.application;

import android.app.Activity;
import android.content.Intent;
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
 * 注册界面
 */
public class RegisterActivity extends Activity {

    private static final String TAG = "RegisterActivity";

    private EditText registerID;
    private EditText registerPassword;
    private EditText registerConfirmPassword;
    private Button registerButton;
    private Button backButton;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        registerID = findViewById(R.id.register_ID);
        registerPassword = findViewById(R.id.register_password);
        registerConfirmPassword = findViewById(R.id.confirm_password);
        registerButton = findViewById(R.id.register_btn);
        backButton = findViewById(R.id.back_button);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = registerID.getText().toString();
                String userPassword = registerPassword.getText().toString();
                String confirmPassword = registerConfirmPassword.getText().toString();

                if (userID.isEmpty() || userPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!userPassword.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "两次密码输入不同，请重新输入", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 通过网络注册
                new Thread(new Connect(userID, userPassword)).start();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 初始化Handler
        handler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x44) {
                    // 注册成功
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                } else if (msg.what == 0x55) {
                    // 用户已存在
                    Toast.makeText(RegisterActivity.this, "该用户ID已存在", Toast.LENGTH_SHORT).show();
                } else if (msg.what == 0x66) {
                    // 注册失败
                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                } else if (msg.what == 0x99) {
                    // 网络错误
                    Toast.makeText(RegisterActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                }
            }
        };
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
                socket.connect(new InetSocketAddress(LoginActivity.SERVER_IP, LoginActivity.SERVER_PORT), 5000);

                // 向服务器发送请求
                PrintWriter out = new PrintWriter(new OutputStreamWriter(
                        socket.getOutputStream(), StandardCharsets.UTF_8), true);

                // 组装JSON对象
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("operation", "register");
                jsonObject.put("ID", userID);
                jsonObject.put("PSW", userPassword);
                out.println(jsonObject.toString());

                // 读取服务器返回
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = reader.readLine();

                if (Objects.equals(line, "register_success")) {
                    Message message = Message.obtain();
                    message.what = 0x44;
                    handler.sendMessage(message);
                } else if (Objects.equals(line, "register_failed")) {
                    Message message = Message.obtain();
                    message.what = 0x55;
                    handler.sendMessage(message);
                } else {
                    Message message = Message.obtain();
                    message.what = 0x66;
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
