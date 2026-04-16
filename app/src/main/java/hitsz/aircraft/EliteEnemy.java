package hitsz.aircraft;

import hitsz.basic.AbstractFlyingObject;
import hitsz.bullet.BaseBullet;
import hitsz.bullet.EnemyBullet;
import hitsz.shootstrategy.CircularShootStrategy;
import hitsz.shootstrategy.ScatterShootStrategy;
import hitsz.shootstrategy.ShootContext;
import hitsz.shootstrategy.StraightShootStrategy;

import java.util.LinkedList;
import java.util.List;

public class EliteEnemy extends AbstractEnemy{

    @Override
    public int type(){
        return 2;
    }
    /**攻击方式 */

    /**
     * 子弹一次发射数量
     */
    private int shootNum = 1;

    /**
     * 子弹伤害
     */
    private int power = 10;

    /**
     * 子弹射击方向 (向上发射：1，向下发射：-1)
     */
    private int direction = 1;

    private ShootContext shootContext = new ShootContext(new StraightShootStrategy());


    public EliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= AbstractFlyingObject.getScreenHeight() ) {
            vanish();
        }
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
        int speedX = 0;
        int speedY = this.getSpeedY() + direction*5;
        /*BaseBullet bullet;
        for(int i=0; i<shootNum; i++){
            // 子弹发射位置相对飞机位置向前偏移
            // 多个子弹横向分散
            bullet = new EnemyBullet(x + (i*2 - shootNum + 1)*10, y, speedX, speedY, power);
            res.add(bullet);
        }
        return res;*/
        return shootContext.executeStrategy(x,y,speedX,speedY,shootNum,power,1);
    }
}