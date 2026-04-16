package hitsz.application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import hitsz.Music.MusicThread;
import hitsz.aircraft.AbstractAircraft;
import hitsz.aircraft.HeroAircraft;
import hitsz.aircraft.MobEnemy;
import hitsz.aircraft.EliteEnemy;
import hitsz.aircraft.BossEnemy;
import hitsz.bullet.BaseBullet;
import hitsz.basic.AbstractFlyingObject;
import hitsz.factory.EliteEnemyFactory;
import hitsz.factory.BossEnemyFactory;
import hitsz.factory.SuperEnemyFactory;
import hitsz.factory.MobEnemyFactory;
import hitsz.factory.BloodToolsFactory;
import hitsz.factory.BulletToolsFactory;
import hitsz.factory.SuperBulletToolsFactory;
import hitsz.factory.BombToolsFactory;
import hitsz.tools.AbstractTools;

/**
 * 游戏主面板，游戏启动
 *
 * @author edu.hitsz
 */
public class Game extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private int backGroundTop = 0;

    /**
     * 游戏线程和Handler
     */
    private Thread gameThread;
    private Handler gameHandler;
    private boolean isRunning = false;

    /**
     * 时间间隔(ms)，控制刷新频率
     */
    private int timeInterval = 40;
    private MusicThread bullet_hit;
    private MusicThread bomb_explosion;
    private MusicThread bgm;
    private MusicThread bgm_boss;
    private MusicThread game_over;
    private MusicThread get_supply;
    private final EliteEnemyFactory eliteEnemyFactory = new EliteEnemyFactory();
    private final BossEnemyFactory bossEnemyFactory = new BossEnemyFactory();
    private final SuperEnemyFactory superEnemyFactory = new SuperEnemyFactory();
    private final MobEnemyFactory mobEnemyFactory = new MobEnemyFactory();
    private final BloodToolsFactory bloodToolsFactory = new BloodToolsFactory();
    private final BulletToolsFactory bulletToolsFactory = new BulletToolsFactory();
    private final SuperBulletToolsFactory superBulletToolsFactory = new SuperBulletToolsFactory();
    private final BombToolsFactory bombToolsFactory = new BombToolsFactory();
    private HeroAircraft heroAircraft;
    private List<AbstractAircraft> enemyAircrafts;
    private List<AbstractTools> tools;
    private List<BaseBullet> heroBullets;
    private List<BaseBullet> enemyBullets;
    private HeroController heroController;


    //精英机产生概率
    private double ElitePoint = 0.3;

    /**
     * 屏幕中出现的敌机最大数量
     */
    private int enemyMaxNumber = 5;

    /**
     * 当前得分
     */
    private int score = 0;
    /**
     * 当前时刻
     */
    private int time = 0;
    private int count_boss = 1;

    /**
     * 周期（ms)
     * 指示子弹的发射、敌机的产生频率
     */
    private int cycleDuration = 600;
    private int cycleTime = 0;

    private int DifficultyTime = 0;
    private int DifficultyDuration = 20000;
    /**
     * 游戏结束标志
     */
    private boolean gameOverFlag = false;
    private int difficulty =0;
    private SurfaceHolder surfaceHolder;
    private Paint paint;
    
    public Game(Context context) {
        super(context);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        paint = new Paint();
        paint.setAntiAlias(true);
        initializeGame();
    }
    private void initializeGame() {

        // 初始化图像资源
        if (!ImageManager.isInitialized()) {
            ImageManager.init(getContext());
        }

        heroAircraft = HeroAircraft.getInstance();
        // 重置英雄机HP，确保每次游戏开始时都是满血
        heroAircraft.setHp(10000);
        enemyAircrafts = new CopyOnWriteArrayList<>();
        heroBullets = new CopyOnWriteArrayList<>();
        enemyBullets = new CopyOnWriteArrayList<>();
        tools = new CopyOnWriteArrayList<>();

        // 初始化英雄机控制器
        heroController = new HeroController(this, heroAircraft);

        // 初始化音频资源
        bullet_hit = new MusicThread("bullet_hit", false, getContext());
        bomb_explosion = new MusicThread("bomb_explosion", false, getContext());
        bgm = new MusicThread("bgm", true, getContext());
        // 设置背景音乐音量为最大音量的80%
        bgm.setVolume(0.8f);
        bgm_boss = new MusicThread("bgm_boss", true, getContext());
        // 设置BOSS背景音乐音量为最大音量的80%
        bgm_boss.setVolume(0.8f);
        game_over = new MusicThread("game_over", false, getContext());
        get_supply = new MusicThread("get_supply", false, getContext());
    }
    public void restart() {
        // 停止游戏
        isRunning = false;
        if (gameThread != null) {
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        // 停止并释放音频资源
        if (bullet_hit != null) {
            bullet_hit.stopPlay();
            bullet_hit = null;
        }
        if (bomb_explosion != null) {
            bomb_explosion.stopPlay();
            bomb_explosion = null;
        }
        if (bgm != null) {
            bgm.stopPlay();
            bgm = null;
        }
        if (bgm_boss != null) {
            bgm_boss.stopPlay();
            bgm_boss = null;
        }
        if (game_over != null) {
            game_over.stopPlay();
            game_over = null;
        }
        if (get_supply != null) {
            get_supply.stopPlay();
            get_supply = null;
        }
        
        // 释放图像资源
        ImageManager.release();
        
        // 重新初始化游戏状态
        initializeGame();
        // 重置英雄机HP
        heroAircraft.setHp(10000);
        gameOverFlag = false;
        // 重新开始游戏
        startGame();
    }
    
    public void startGame() {
        isRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
        
        // 开始播放主背景音乐
        if (bgm != null && !bgm.IsPlaying && SettingsActivity.isMusicEnabled(getContext())) {
            bgm.start();
        }
    }
    
    public void stopGame() {
        isRunning = false;
        if (gameThread != null) {
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        // 停止并释放音频资源
        if (bullet_hit != null) {
            bullet_hit.stopPlay();
            bullet_hit = null;
        }
        if (bomb_explosion != null) {
            bomb_explosion.stopPlay();
            bomb_explosion = null;
        }
        if (bgm != null) {
            bgm.stopPlay();
            bgm = null;
        }
        if (bgm_boss != null) {
            bgm_boss.stopPlay();
            bgm_boss = null;
        }
        if (game_over != null) {
            game_over.stopPlay();
            game_over = null;
        }
        if (get_supply != null) {
            get_supply.stopPlay();
            get_supply = null;
        }
        
        // 释放图像资源
        ImageManager.release();
    }
    @Override
    public void run() {
        while (isRunning) {
            long startTime = System.currentTimeMillis();
            
            time += timeInterval;

            int count_enemy=0;
            // 周期性执行（控制频率）
            if (timeCountAndNewCycleJudge()) {
                System.out.println(time);
                // 新敌机产生
                double rand = Math.random();
                if(difficulty != 0){
                    if(score>=count_boss*1000*difficulty*difficulty){
                        // 暂停主背景音乐
                        if (bgm != null && bgm.IsPlaying) {
                            bgm.stopPlay();
                        }
                        // 播放BOSS音乐
                if (!bgm_boss.IsPlaying && SettingsActivity.isMusicEnabled(getContext())) {
                    bgm_boss = new MusicThread("bgm_boss", true, getContext());
                    bgm_boss.start();
                    // 设置背景音乐音量为最大音量的80%
                    bgm_boss.setVolume(0.8f);
                }
                        enemyAircrafts.add(bossEnemyFactory.createEnemy());
                        count_boss = count_boss+1;
                    }
                }

                if (enemyAircrafts.size() < enemyMaxNumber) {
                    if(rand < ElitePoint){
                        enemyAircrafts.add(eliteEnemyFactory.createEnemy());
                        count_enemy = count_enemy + 1;
                        if(count_enemy >= rand*10){
                            count_enemy = 0;
                            enemyAircrafts.add(superEnemyFactory.createEnemy());
                        }
                    }else{
                        enemyAircrafts.add(mobEnemyFactory.createEnemy());
                    }
                }
                // 飞机射出子弹
                shootAction();
            }

            // 子弹移动
            bulletsMoveAction();

            // 飞机移动
            aircraftsMoveAction();

            //工具移动
            toolsMoveAction();

            // 撞击检测
            crashCheckAction();

            // 后处理
            postProcessAction();

            // 绘制界面
            drawGame();

            //检查是否需要难度升级
            checkIfDifficulty();

            // 游戏结束检查英雄机是否存活
            if (heroAircraft.getHp() <= 0 && !gameOverFlag) {
                // 游戏结束
                // 播放游戏结束音乐
                if (!game_over.IsPlaying && SettingsActivity.isMusicEnabled(getContext())) {
                    game_over.start();
                }
                
                gameOverFlag = true;
                System.out.println("Game Over!");
                
                // 在主线程中显示对话框
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // 显示输入对话框，让用户输入玩家ID
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                        builder.setTitle("游戏结束");
                        builder.setMessage("请输入您的Player ID：\n本次得分：" + score);

                        // 创建输入框
                        final android.widget.EditText input = new android.widget.EditText(getContext());
                        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
                        builder.setView(input);

                        // 设置确定按钮
                        builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                try{
                                    String basicInput = input.getText().toString();
                                    if (basicInput.isEmpty()) {
                                        basicInput = "Player";
                                    }

                                    // 更新排行榜
                                    hitsz.ranklist.ScoresDaoImpl scoresDao = new hitsz.ranklist.ScoresDaoImpl(difficulty, getContext());
                                    scoresDao.doAdd(new hitsz.ranklist.Scores(score, basicInput));
                                    scoresDao.sortPrintSave();

                                    // 停止游戏并释放资源
                                    stopGame();

                                    // 打开排行榜界面
                                    android.content.Intent intent = new android.content.Intent(getContext(), RankListActivity.class);
                                    intent.putExtra("difficulty", difficulty);
                                    getContext().startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    // 显示错误信息
                                    Toast.makeText(getContext(), "保存分数失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        // 设置取消按钮
                        builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                // 停止游戏并释放资源
                                stopGame();
                                // 返回主菜单
                                android.content.Intent intent = new android.content.Intent(getContext(), MainActivity.class);
                                intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                getContext().startActivity(intent);
                                dialog.cancel();
                            }
                        });

                        // 显示对话框
                        builder.show();
                    }
                });
                
                // 停止游戏循环
                isRunning = false;
            }
            
            // 控制帧率
            long endTime = System.currentTimeMillis();
            long sleepTime = timeInterval - (endTime - startTime);
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void checkIfDifficulty(){
        try{
            // 从DifficultySelectionActivity获取难度设置
            int newDifficulty = DifficultySelectionActivity.difficultySet;

            // 如果难度发生变化，切换背景
            if(newDifficulty != difficulty){
                difficulty = newDifficulty;
                // 切换背景图片
                ImageManager.ShiftBackground(difficulty);
            }

            if(difficulty != 0){
                DifficultyTime+=timeInterval;
                if(DifficultyTime>=DifficultyDuration/difficulty){
                    DifficultyTime%=(DifficultyDuration/difficulty);
                    updateDifficulty();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //
    //难度升级
    //

    private void updateDifficulty(){
        cycleDuration = (cycleDuration-160)*8/10 + 160;
        System.out.println("难度升级：飞机射击周期缩短！敌机产生周期缩短！目前周期数为"+cycleDuration);
        enemyMaxNumber ++;
        System.out.println("难度升级：最大敌机数量增加！目前数量"+enemyMaxNumber);
        ElitePoint = ElitePoint*1.25;
        System.out.println("难度升级：精英机产生概率上升了！目前概率为"+ElitePoint);
        mobEnemyFactory.MobEnemyHp +=20;
        superEnemyFactory.SuperEnemyHp +=20;
        eliteEnemyFactory.EliteEnemyHp +=20;
        System.out.println("难度升级：所有非Boss敌机血量上升了20");
    }
    //***********************
    //      Action 各部分
    //***********************

    private boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration) {
            // 跨越到新的周期
            cycleTime %= cycleDuration;
            return true;
        } else {
            return false;
        }
    }

    private void shootAction() {
        // TODO 敌机射击
        for(AbstractAircraft enemyAircraft :enemyAircrafts ){
            if(enemyAircraft.getSpeedY()!=10){
                enemyBullets.addAll(enemyAircraft.shoot());
            }
        }

        // 英雄射击
        heroBullets.addAll(heroAircraft.shoot());
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
            if(enemyAircraft.getSpeedY()==2){
                if(enemyAircraft.getLocationY()>=AbstractFlyingObject.getScreenHeight()*0.1){
                    enemyAircraft.boss_stop(time/1000);
                }
            }
        }
    }

    private void toolsMoveAction(){
        for(AbstractTools tool:tools){
            tool.forward();
        }
    }

    /**
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     */
    private void crashCheckAction() {
        // TODO 敌机子弹攻击英雄
        // 使用副本避免并发修改异常
        for(BaseBullet bullet : new LinkedList<>(enemyBullets)) {
            if (bullet.notValid()) {
                continue;
            }
            if(heroAircraft.crash(bullet)){
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }
        // 英雄子弹攻击敌机
        for (BaseBullet bullet : new LinkedList<>(heroBullets)) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractAircraft enemyAircraft : new LinkedList<>(enemyAircrafts)) {
                if (enemyAircraft.notValid()) {
                    // 已被其他子弹击毁的敌机，不再检测
                    // 避免多个子弹重复击毁同一敌机的判定
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    enemyAircraft.decreaseHp(bullet.getPower());
                    // 播放子弹击中声音
                    // 每次创建新的MusicThread实例，避免重复启动同一线程
                    if (SettingsActivity.isMusicEnabled(getContext())) {
                        final MusicThread bulletHitSound = new MusicThread("bullet_hit", false, getContext());
                        bulletHitSound.start();
                        // 播放完成后自动停止并释放
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000); // 等待音频播放完成
                                    bulletHitSound.stopPlay();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                    bullet.vanish();
                    if (enemyAircraft.notValid()) {
                        // TODO 获得分数，产生道具补给
                        if(enemyAircraft.getSpeedY()!=10){
                            // 生成工具
                            produceTools(enemyAircraft.type());
                            if(enemyAircraft.type()==1){
                                // 停止BOSS音乐
                                bgm_boss.stopPlay();
                                // 恢复主背景音乐
                                if (bgm != null && !bgm.IsPlaying && SettingsActivity.isMusicEnabled(getContext())) {
                                    bgm = new MusicThread("bgm", true, getContext());
                                    bgm.setVolume(0.8f);
                                    bgm.start();
                                }
                            }
                        }
                        score += 10;
                    }
                }
                // 英雄机 与 敌机 相撞，均损毁
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        // Todo: 我方获得道具，道具生效
        for(AbstractTools tool: new LinkedList<>(tools)){
            if(tool.crash(heroAircraft)){
                // 播放获得道具声音
                // 每次创建新的MusicThread实例，避免重复启动同一线程
                if (SettingsActivity.isMusicEnabled(getContext())) {
                    final MusicThread getSupplySound = new MusicThread("get_supply", false, getContext());
                    getSupplySound.start();
                    // 播放完成后自动停止并释放
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000); // 等待音频播放完成
                                getSupplySound.stopPlay();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                int func=tool.getFunc();
                if(func==1){
                    heroAircraft.decreaseHp(-20);
                    System.out.println("HP UP!");
                    tool.vanish();
                }
                if(func==2){
                    System.out.println("Bomb!");
                    tool.vanish();
                    bombAct();
                    // 播放炸弹爆炸声音
                    // 每次创建新的MusicThread实例，避免重复启动同一线程
                    if (SettingsActivity.isMusicEnabled(getContext())) {
                        final MusicThread bombExplosionSound = new MusicThread("bomb_explosion", false, getContext());
                        bombExplosionSound.start();
                        // 播放完成后自动停止并释放
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000); // 等待音频播放完成
                                    bombExplosionSound.stopPlay();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
                if(func==3){
                    System.out.println("Fire the home!");
                    heroAircraft.FireTheHome(1);
                    tool.vanish();
                }
                if(func==4){
                    System.out.println("Fire the home!PLUS");
                    heroAircraft.FireTheHome(2);
                    tool.vanish();
                }
            }
        }
    }
    private void bombAct(){
        for(AbstractAircraft enemy :enemyAircrafts){
            if(!enemy.notValid()){
                switch (enemy.type()){
                    case 2:{
                        enemy.vanish();
                        score+=20;
                        break;
                    }
                    case 3:{
                        enemy.vanish();
                        score+=10;
                        break;
                    }
                    case 4:{
                        enemy.decreaseHp(50);
                        break;
                    }
                }
            }
        }
        for(BaseBullet bullet:enemyBullets){
            if(!bullet.notValid()){
                bullet.vanish();
            }
        }
    }
    private void produceTools(int type){
        for(int i=1;i<= ((type==1)?3:1);i++){
            double tooltype = Math.random();
            if(tooltype<0.3){
                tools.add(bloodToolsFactory.createTools());
            }else if(tooltype<0.55){
                tools.add(bombToolsFactory.createTools());
            }else if(tooltype<0.75){
                tools.add(bulletToolsFactory.createTools());
            }else if(tooltype<0.95){
                tools.add(superBulletToolsFactory.createTools());
            }else{
                System.out.println("Nothing happen!");
            }
            score += 10;//正常精英机给30分
            if(i==3)score+=470;//boss给500分
        }

    }
    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * <p>
     * 无效的原因可能是撞击或者飞出边界
     */
    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        tools.removeIf(AbstractFlyingObject::notValid);
    }


    //***********************
    //      Paint 各部分
    //***********************

    /**
     * 重写paint方法
     * 通过重复调用paint方法，实现游戏动画
     *
     * @param  
     */
    private void drawGame() {
        Canvas canvas = surfaceHolder.lockCanvas();
        if (canvas != null) {
            try {
                // 绘制背景,图片滚动
                Bitmap background = ImageManager.BACKGROUND_IMAGE;
                if (background != null && !background.isRecycled()) {
                    int width = getWidth();
                    int height = getHeight();

                    // 缩放背景图片以适应屏幕尺寸
                    Bitmap scaledBackground = Bitmap.createScaledBitmap(background, width, height, true);
                    canvas.drawBitmap(scaledBackground, 0, backGroundTop - height, paint);
                    canvas.drawBitmap(scaledBackground, 0, backGroundTop, paint);

                    // 释放缩放后的bitmap，避免内存泄漏
                    if (!scaledBackground.isRecycled()) {
                        scaledBackground.recycle();
                    }
                } else {
                    // 临时使用纯色背景
                    canvas.drawColor(Color.BLACK);
                }
                
                backGroundTop += 1;
                if (backGroundTop == getHeight()) {
                    backGroundTop = 0;
                }

                // 先绘制子弹，后绘制飞机
                // 这样子弹显示在飞机的下层
                drawObjects(canvas, enemyBullets);
                drawObjects(canvas, heroBullets);
                drawObjects(canvas, tools);
                drawObjects(canvas, enemyAircrafts);

                // 绘制英雄机
                Bitmap heroImage = ImageManager.HERO_IMAGE;
                if (heroImage != null && !heroImage.isRecycled()) {
                    canvas.drawBitmap(heroImage, heroAircraft.getLocationX() - heroImage.getWidth() / 2,
                            heroAircraft.getLocationY() - heroImage.getHeight() / 2, paint);
                } else {
                    // 临时绘制一个矩形代表英雄机
                    paint.setColor(Color.BLUE);
                    canvas.drawRect(heroAircraft.getLocationX() - 20, heroAircraft.getLocationY() - 20,
                            heroAircraft.getLocationX() + 20, heroAircraft.getLocationY() + 20, paint);
                }

                //绘制得分和生命值
                drawScoreAndLife(canvas);
            } finally {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void drawObjects(Canvas canvas, List<? extends AbstractFlyingObject> objects) {
        if (objects.size() == 0) {
            return;
        }

        // 使用副本避免并发修改异常
        for (AbstractFlyingObject object : new LinkedList<>(objects)) {
            // 获取对象的图片
            Bitmap image = ImageManager.get(object);
            if (image != null && !image.isRecycled()) {
                canvas.drawBitmap(image, object.getLocationX() - image.getWidth() / 2,
                        object.getLocationY() - image.getHeight() / 2, paint);
            } else {
                // 临时绘制不同颜色的矩形代表不同对象
                if (object instanceof BaseBullet) {
                    paint.setColor(Color.YELLOW);
                    canvas.drawRect(object.getLocationX() - 2, object.getLocationY() - 2,
                            object.getLocationX() + 2, object.getLocationY() + 2, paint);
                } else if (object instanceof AbstractAircraft) {
                    if (object instanceof MobEnemy) {
                        paint.setColor(Color.RED);
                    } else if (object instanceof EliteEnemy) {
                        paint.setColor(Color.GREEN);
                    } else if (object instanceof BossEnemy) {
                        paint.setColor(Color.MAGENTA);
                    }
                    canvas.drawRect(object.getLocationX() - 15, object.getLocationY() - 15,
                            object.getLocationX() + 15, object.getLocationY() + 15, paint);
                } else if (object instanceof AbstractTools) {
                    paint.setColor(Color.CYAN);
                    canvas.drawRect(object.getLocationX() - 10, object.getLocationY() - 10,
                            object.getLocationX() + 10, object.getLocationY() + 10, paint);
                }
            }
        }
    }

    private void drawScoreAndLife(Canvas canvas) {
        int x = 10;
        int y = 30;
        paint.setColor(Color.WHITE);
        paint.setTextSize(22);
        canvas.drawText("SCORE:" + this.score, x, y, paint);
        y = y + 30;
        canvas.drawText("LIFE:" + this.heroAircraft.getHp(), x, y, paint);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return heroController.handleTouchEvent(event);
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //  surface创建时调用
        startGame();
    }
    
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //  surface大小改变时调用
        // 设置屏幕尺寸
        AbstractFlyingObject.setScreenSize(width, height);
        // 初始化英雄机位置
        if (heroAircraft != null) {
            heroAircraft.setLocation(width / 2, height - 100);
        }
    }
    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //  surface销毁时调用
        stopGame();
    }
}