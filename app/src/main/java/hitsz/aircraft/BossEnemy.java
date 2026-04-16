package hitsz.aircraft;

import hitsz.basic.AbstractFlyingObject;
import hitsz.bullet.BaseBullet;
import hitsz.bullet.EnemyBullet;
import hitsz.shootstrategy.CircularShootStrategy;
import hitsz.shootstrategy.ShootContext;

import java.util.LinkedList;
import java.util.List;

public class BossEnemy extends AbstractEnemy{

    @Override
    public int type(){
        return 1;
    }
    /**攻击方式 */

    /**
     * 子弹一次发射数量
     */
    private int shootNum = 10;

    /**
     * 子弹伤害
     */
    private int power = 10;

    private ShootContext shootContext = new ShootContext(new CircularShootStrategy());

    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
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
        int y = this.getLocationY();
        /*BaseBullet bullet;
        for(int i=0; i<shootNum; i++){
            // 子弹发射位置相对飞机位置向前偏移
            // 多个子弹横向分散
            bullet = new EnemyBullet(x , y,(int)(8*Math.sin(Math.PI*(i*2-shootNum+1)/(shootNum*2))) , (int)(8*Math.cos(Math.PI*(i*2-shootNum+1)/(shootNum*2))), power);
            res.add(bullet);
        }
        for(int i=0; i<shootNum; i++){
            // 子弹发射位置相对飞机位置向前偏移
            // 多个子弹横向分散
            bullet = new EnemyBullet(x , y,(int)(8*Math.sin(Math.PI*(i*2-shootNum+1)/(shootNum*2))) , -(int)(8*Math.cos(Math.PI*(i*2-shootNum+1)/(shootNum*2))), power);
            res.add(bullet);
        }
        return res;*/
        return shootContext.executeStrategy(x,y,0,0,shootNum,power,1);
    }
}