package hitsz.Music;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.myapplication.R;

import java.io.IOException;

public class MusicThread extends Thread {

    //音频文件名
    private String filename;
    private Context context;
    public boolean IsPlaying = false;

    //新增循环控制字段
    private boolean loop = false;
    private volatile boolean stopRequested = false;
    private MediaPlayer mediaPlayer;

    //原有构造函数
    public MusicThread(String filename) {
        this.filename = filename;
    }

    //新增带循环参数的构造函数
    public MusicThread(String filename, boolean loop) {
        this.filename = filename;
        this.loop = loop;
    }
    
    //Android版本构造函数，接收Context
    public MusicThread(String filename, boolean loop, Context context) {
        this.filename = filename;
        this.loop = loop;
        this.context = context;
    }

    //设置循环播放状态的方法
    public void setLoop(boolean loop) {
        this.loop = loop;
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(loop);
        }
    }

    //获取当前循环状态
    public boolean isLoop() {
        return loop;
    }
    
    //设置音量的方法
    public void setVolume(float volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
    }

    //改进的停止播放方法
    public void stopPlay() {
        stopRequested = true;

        // 停止并释放MediaPlayer
        MediaPlayer mp = mediaPlayer;
        if (mp != null) {
            try {
                if (mp.isPlaying()) {
                    mp.stop();
                }
            } catch (IllegalStateException e) {
                // 忽略已释放的MediaPlayer的异常
            }
            try {
                mp.release();
            } catch (IllegalStateException e) {
                // 忽略已释放的MediaPlayer的异常
            }
            mediaPlayer = null;
        }

        this.interrupt();
    }

    @Override
    public void run() {
        IsPlaying = true;
        stopRequested = false;

        try {
            do {
                if (stopRequested || Thread.currentThread().isInterrupted()) {
                    break;
                }

                // 初始化MediaPlayer
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setLooping(loop);
                
                // 根据文件名加载音频资源
                int resId = getResourceId(filename);
                if (resId != 0 && context != null) {
                    mediaPlayer = MediaPlayer.create(context, resId);
                    mediaPlayer.setLooping(loop);
                    
                    // 开始播放
                    mediaPlayer.start();
                    
                    // 等待播放完成
                    while (!stopRequested) {
                        try {
                            if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
                                break;
                            }
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            // 中断异常，退出循环
                            break;
                        } catch (IllegalStateException e) {
                            // 忽略已释放的MediaPlayer的异常
                            break;
                        }
                    }
                } else {
                    Log.e("MusicThread", "Invalid filename or context: " + filename);
                    break;
                }

            } while (loop && !stopRequested && !Thread.currentThread().isInterrupted());
        } catch (Exception e) {
            if (!stopRequested) {
                e.printStackTrace();
            }
        } finally {
            // 释放资源
            MediaPlayer mp = mediaPlayer;
            if (mp != null) {
                try {
                    if (mp.isPlaying()) {
                        mp.stop();
                    }
                } catch (IllegalStateException e) {
                    // 忽略已释放的MediaPlayer的异常
                }
                try {
                    mp.release();
                } catch (IllegalStateException e) {
                    // 忽略已释放的MediaPlayer的异常
                }
                mediaPlayer = null;
            }
            IsPlaying = false;
            Log.d("MusicThread", "music off");
        }
    }

    // 根据文件名获取资源ID
    private int getResourceId(String filename) {
        if (context == null) {
            return 0;
        }
        
        // 移除文件扩展名
        String name = filename.replace(".wav", "").replace(".mp3", "");
        
        // 映射文件名到资源ID
        switch (name) {
            case "bullet_hit":
                return R.raw.bullet_hit;
            case "bomb_explosion":
                return R.raw.bomb_explosion;
            case "bgm_boss":
                return R.raw.bgm_boss;
            case "game_over":
                return R.raw.game_over;
            case "get_supply":
                return R.raw.get_supply;
            case "bgm":
                return R.raw.bgm;
            default:
                return 0;
        }
    }

    // 添加安全销毁方法
    public void safeDestroy() {
        stopPlay();
    }
}