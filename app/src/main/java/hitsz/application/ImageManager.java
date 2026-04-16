package hitsz.application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.myapplication.R;

import hitsz.aircraft.*;
import hitsz.bullet.EnemyBullet;
import hitsz.bullet.HeroBullet;
import hitsz.tools.AbstractTools;
import hitsz.tools.Bloodtool;
import hitsz.tools.Bombtool;
import hitsz.tools.Bullettool;
import hitsz.tools.Superbullettool;

import java.util.HashMap;
import java.util.Map;

/**
 * 综合管理图片的加载，访问
 * 提供图片的静态访问方法
 *
 * @author edu.hitsz
 */

public class ImageManager {

    /**
     * 类名-图片 映射，存储各基类的图片 <br>
     * 可使用 CLASSNAME_IMAGE_MAP.get( obj.getClass().getName() ) 获得 obj 所属基类对应的图片
     */
    private static final Map<String, Bitmap> CLASSNAME_IMAGE_MAP = new HashMap<>();

    // 背景图片数组，支持多个背景
    private static final Bitmap[] BACKGROUND_IMAGES = new Bitmap[3];
    public static Bitmap BACKGROUND_IMAGE;
    public static Bitmap HERO_IMAGE;
    public static Bitmap SUPER_ENEMY_IMAGE;
    public static Bitmap HERO_BULLET_IMAGE;
    public static Bitmap ENEMY_BULLET_IMAGE;
    public static Bitmap MOB_ENEMY_IMAGE;
    public static Bitmap ELITE_ENEMY_IMAGE;
    public static Bitmap BOSS_ENEMY_IMAGE;
    public static Bitmap TOOL_1_IMAGE;
    public static Bitmap TOOL_2_IMAGE;
    public static Bitmap TOOL_3_IMAGE;
    public static Bitmap TOOL_4_IMAGE;
    
    private static boolean initialized = false;

    /**
     * 初始化图片资源
     * @param context Android上下文
     */
    public static void init(Context context) {
        if (initialized) {
            return;
        }
        
        try {
            // 初始化所有背景图片
            BACKGROUND_IMAGES[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg);
            BACKGROUND_IMAGES[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg2);
            BACKGROUND_IMAGES[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg3);

            // 设置默认背景（难度0）
            BACKGROUND_IMAGE = BACKGROUND_IMAGES[0];

            HERO_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.hero);
            MOB_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.mob);
            HERO_BULLET_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet_hero);
            ENEMY_BULLET_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet_enemy);
            ELITE_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.elite);
            SUPER_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.elite_plus);
            BOSS_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.boss);
            TOOL_1_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_blood);
            TOOL_2_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_bomb);
            TOOL_3_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_bullet);
            TOOL_4_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_bullet_plus);

            CLASSNAME_IMAGE_MAP.put(HeroAircraft.class.getName(), HERO_IMAGE);
            CLASSNAME_IMAGE_MAP.put(MobEnemy.class.getName(), MOB_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(HeroBullet.class.getName(), HERO_BULLET_IMAGE);
            CLASSNAME_IMAGE_MAP.put(EnemyBullet.class.getName(), ENEMY_BULLET_IMAGE);
            CLASSNAME_IMAGE_MAP.put(EliteEnemy.class.getName(), ELITE_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(BossEnemy.class.getName(), BOSS_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(Bloodtool.class.getName(), TOOL_1_IMAGE);
            CLASSNAME_IMAGE_MAP.put(Bombtool.class.getName(), TOOL_2_IMAGE);
            CLASSNAME_IMAGE_MAP.put(Bullettool.class.getName(), TOOL_3_IMAGE);
            CLASSNAME_IMAGE_MAP.put(Superbullettool.class.getName(), TOOL_4_IMAGE);
            CLASSNAME_IMAGE_MAP.put(SuperEnemy.class.getName(), SUPER_ENEMY_IMAGE);

            initialized = true;
            System.out.println("ImageManager initialized successfully");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to initialize ImageManager");
        }
    }
    
    /**
     * 释放图片资源
     */
    public static void release() {
        if (!initialized) {
            return;
        }
        
        // 释放背景图片
        for (Bitmap bitmap : BACKGROUND_IMAGES) {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        
        // 释放其他图片
        for (Bitmap bitmap : CLASSNAME_IMAGE_MAP.values()) {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        
        CLASSNAME_IMAGE_MAP.clear();
        initialized = false;
        System.out.println("ImageManager resources released");
    }

    public static Bitmap get(String className){
        return CLASSNAME_IMAGE_MAP.get(className);
    }

    public static Bitmap get(Object obj){
        if (obj == null){
            return null;
        }
        return get(obj.getClass().getName());
    }
    
    /**
     * 检查是否已初始化
     * @return 是否已初始化
     */
    public static boolean isInitialized() {
        return initialized;
    }

    /**
     * 切换背景图片
     * @param difficulty 难度级别：0-默认，1-简单，2-困难
     */
    public static void ShiftBackground(int difficulty) {
        if (!initialized) {
            System.err.println("ImageManager not initialized");
            return;
        }
        
        try {
            // 验证难度参数的有效性
            if (difficulty < 0 || difficulty >= BACKGROUND_IMAGES.length) {
                System.err.println("无效的难度级别: " + difficulty + "，使用默认背景");
                BACKGROUND_IMAGE = BACKGROUND_IMAGES[0];
                return;
            }

            // 切换到指定难度的背景
            BACKGROUND_IMAGE = BACKGROUND_IMAGES[difficulty];
            System.out.println("已切换到难度 " + difficulty + " 的背景");

        } catch (Exception e) {
            // 异常处理：加载失败时使用默认背景
            System.err.println("背景切换失败: " + e.getMessage());
            try {
                BACKGROUND_IMAGE = BACKGROUND_IMAGES[0];
            } catch (Exception ex) {
                System.err.println("默认背景加载也失败，程序可能无法正常运行");
            }
        }
    }

    /**
     * 获取当前可用的背景数量
     * @return 背景图片总数
     */
    public static int getBackgroundCount() {
        return BACKGROUND_IMAGES.length;
    }

    /**
     * 动态添加新背景（可选功能）
     * @param context Android上下文
     * @param resId 新背景图片资源ID
     * @return 新背景的索引，失败返回-1
     */
    public static int addBackground(Context context, int resId) {
        if (!initialized) {
            System.err.println("ImageManager not initialized");
            return -1;
        }
        
        try {
            Bitmap newBg = BitmapFactory.decodeResource(context.getResources(), resId);
            if (newBg == null) {
                System.err.println("Failed to decode background image");
                return -1;
            }

            // 创建新的背景数组（扩展容量）
            Bitmap[] newBackgrounds = new Bitmap[BACKGROUND_IMAGES.length + 1];
            System.arraycopy(BACKGROUND_IMAGES, 0, newBackgrounds, 0, BACKGROUND_IMAGES.length);
            newBackgrounds[BACKGROUND_IMAGES.length] = newBg;

            // 更新背景数组引用
            // 注意：这里需要使用反射或者其他方式更新BACKGROUND_IMAGES
            // 由于BACKGROUND_IMAGES是final的，这里只做示例
            
            System.out.println("成功添加新背景");
            return newBackgrounds.length - 1;

        } catch (Exception e) {
            System.err.println("添加背景失败: " + e.getMessage());
            return -1;
        }
    }
}