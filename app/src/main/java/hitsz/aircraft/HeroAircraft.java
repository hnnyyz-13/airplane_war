package hitsz.aircraft;

import hitsz.application.ImageManager;
import hitsz.bullet.BaseBullet;
import hitsz.bullet.HeroBullet;
import hitsz.shootstrategy.CircularShootStrategy;
import hitsz.shootstrategy.ScatterShootStrategy;
import hitsz.shootstrategy.ShootContext;
import hitsz.shootstrategy.StraightShootStrategy;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//import static org.apache.commons.lang3.math.NumberUtils.min;

/**
 * 英雄飞机，游戏玩家操控
 * @author edu.hitsz
 */
public class HeroAircraft extends AbstractAircraft {
    private static HeroAircraft instance = null;//懒汉式构建单例模式
    public static synchronized HeroAircraft getInstance(){
        if(instance==null){
            // 使用默认值初始化，后续可以通过setLocation方法更新位置
            instance = new HeroAircraft(
                    256, // 默认x坐标
                    600, // 默认y坐标
                    0, 0, 10000);
        }
        return instance;
    }
    
    /**
     * 更新英雄机位置
     * @param screenWidth 屏幕宽度
     * @param screenHeight 屏幕高度
     */
    public void updatePosition(int screenWidth, int screenHeight) {
        setLocation(screenWidth / 2, screenHeight - 100); // 底部上方100像素
    }


    /**攻击方式 */

    /**
     * 子弹一次发射数量
     */
    private int shootNum = 1;

    /**
     * 子弹伤害
     */
    private int power = 30;

    /**
     * 子弹射击方向 (向上发射：-1，向下发射：1)
     */
    private int direction = -1;

    private Timer resetTimer;
    private ShootContext shootContext = new ShootContext(new StraightShootStrategy());
    /**
     * @param locationX 英雄机位置x坐标
     * @param locationY 英雄机位置y坐标
     * @param speedX 英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param speedY 英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param hp    初始生命值
     */
    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    public void FireTheHome(int shootType){
        if (resetTimer != null) {
            resetTimer.cancel();
        }

        resetTimer = new Timer();

        // 根据shootType切换火力效果
        if (shootType == 1) { // 普通火力
            shootNum = shootNum + 1;
            power = 40;
            shootContext.setStrategy(new ScatterShootStrategy());
            System.out.println("切换到普通火力模式");
        } else if (shootType == 2) { // 超级火力
            shootNum = shootNum + 2;
            power = 50;
            shootContext.setStrategy(new CircularShootStrategy());
            System.out.println("切换到超级火力模式");
        } else {
            shootNum = 1;
            power = 30;
            shootContext.setStrategy(new StraightShootStrategy());
            System.out.println("使用默认火力模式");
            return; // 默认模式不需要定时恢复
        }

        // 启动5秒定时器，自动恢复默认火力
        resetTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                shootNum = 1;
                power = 30;
                shootContext.setStrategy(new StraightShootStrategy());
                System.out.println("5秒计时结束，已恢复默认火力模式");
            }
        }, 5000); // 5秒后执行

    }
    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }

    @Override
    /**
     * 通过射击产生子弹
     * @return 射击出的子弹List
     */
    public List<BaseBullet> shoot() {
        List<BaseBullet> res = new LinkedList<>();
        int x = this.getLocationX();
        int y = this.getLocationY() + direction*2;
        int speedX = 2;//为散射特设
        int speedY = this.getSpeedY() + direction*7;
        /*BaseBullet bullet;
        if(shootNum>=4){
            speedX = 1;
            speedY = speedY-shootNum+3;
        }
        for(int i=0; i<shootNum; i++){
            // 子弹发射位置相对飞机位置向前偏移
            // 多个子弹横向分散
            bullet = new HeroBullet(x + (i*2 - shootNum + 1)*15, y, speedX*(i*2 - shootNum + 1)*5/shootNum, speedY, power);
            res.add(bullet);
        }
        return res;*/
        return shootContext.executeStrategy(x,y,speedX,speedY,shootNum,power,0);
    }

}