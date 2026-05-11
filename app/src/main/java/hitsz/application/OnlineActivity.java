package hitsz.application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 在线对战界面
 */
public class OnlineActivity extends Activity {

    private static final String TAG = "OnlineActivity";
    private static final int ONLINE_PORT = 8888;

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private Handler handler;

    private Game game;
    private static int opponentScore = 0;
    private static boolean gameOverFlag = false;
    private static String opName;
    private static String myName;

    private TextView myNameTextView;
    private TextView myScoreTextView;
    private TextView opponentNameTextView;
    private TextView opponentScoreTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);

        // 初始化控件
        myNameTextView = findViewById(R.id.my_name);
        myScoreTextView = findViewById(R.id.my_score);
        opponentNameTextView = findViewById(R.id.opponent_name);
        opponentScoreTextView = findViewById(R.id.opponent_score);

        // Handler用于发送接收到的服务器消息，显示在界面上
        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                // 启动游戏
                if (msg.what == 0x123 && "start".equals(msg.obj)) {
                    try {
                        game = new Game(OnlineActivity.this);
                        setContentView(game);
                        game.startGame();

                        // 如果开启游戏，那么就新开一个线程给服务端发送当前分数
                        // 如果当前玩家已经死亡，那么就给服务器传"end"信息
                        new Thread(() -> {
                            // 发送当前分数
                            while (!game.isGameOverFlag()) {
                                writer.println(game.getScore());
                                Log.i(TAG, "send to server: score " + game.getScore());
                                try {
                                    Thread.sleep(50);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            // 死亡后发送结束标志
                            writer.println("end");
                            Log.i(TAG, "send to server: end");

                        }).start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (msg.what == 0x123 && "gameover".equals(msg.obj)) {
                    // 设置标志：双方游戏全部结束
                    setGameOverFlag(true);
                    Log.i(TAG, "游戏结束");

                    Intent intent = new Intent(OnlineActivity.this, OverActivity.class);
                    intent.putExtra("myScore", game.getScore());
                    intent.putExtra("otherScore", opponentScore);
                    intent.putExtra("opName", opName);
                    intent.putExtra("myName", myName);
                    startActivity(intent);
                    Log.i(TAG, "跳转");

                } else if (msg.what == 0x456) {
                    myName = (String) msg.obj;
                    Log.e(TAG, "myname: " + myName);
                    runOnUiThread(() -> myNameTextView.setText(myName));

                } else if (msg.what == 0x789) {
                    opName = (String) msg.obj;
                    Log.e(TAG, "opname: " + opName);
                    runOnUiThread(() -> opponentNameTextView.setText(opName));

                } else {
                    try {
                        if (msg.obj != null) {
                            opponentScore = Integer.parseInt((String) msg.obj);
                            Log.i(TAG, "opponent score: " + opponentScore);
                            runOnUiThread(() -> opponentScoreTextView.setText(String.valueOf(opponentScore)));
                        }
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "解析对手分数失败: " + e.getMessage());
                    }
                }
            }
        };

        // 连接服务器
        new Thread(new ClientThread(handler)).start();
    }

    class ClientThread implements Runnable {
        private Handler handler;

        public ClientThread(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            try {
                // 连接到服务器
                socket = new Socket();
                socket.connect(new InetSocketAddress(LoginActivity.SERVER_IP, ONLINE_PORT), 5000);
                writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                        socket.getOutputStream(), StandardCharsets.UTF_8)), true);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

                // 创建子线程接收服务端信息
                // 服务器端可能回复的消息："start"/"end"/不发送，此时显示对手名称及分数
                new Thread(() -> {
                    String msg;
                    int count = 0;
                    try {
                        while ((msg = reader.readLine()) != null) {
                            Log.e(TAG, "get from server: " + msg);

                            if (count == 0) {
                                Message msg1 = new Message();
                                msg1.what = 0x456;
                                msg1.obj = msg;
                                handler.sendMessage(msg1);
                                Log.e(TAG, "send myname:" + msg);
                                count++;
                            } else if (count == 1) {
                                Message msg2 = new Message();
                                msg2.what = 0x789;
                                msg2.obj = msg;
                                handler.sendMessage(msg2);
                                Log.e(TAG, "send opname:" + msg);
                                count++;
                            }

                            Message msgFromServer = new Message();
                            msgFromServer.what = 0x123;
                            msgFromServer.obj = msg;
                            handler.sendMessage(msgFromServer);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(OnlineActivity.this, "与服务器断开连接", Toast.LENGTH_SHORT).show());
                    }
                }).start();

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(OnlineActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show());
                finish();
            }
        }
    }

    public static int getOpponentScore() {
        return opponentScore;
    }

    public static String getOpponentName() {
        return opName;
    }

    private void setGameOverFlag(boolean gameOverFlag) {
        OnlineActivity.gameOverFlag = gameOverFlag;
    }

    public static boolean isGameOverFlag() {
        return gameOverFlag;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (game != null) {
            game.stopGame();
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}