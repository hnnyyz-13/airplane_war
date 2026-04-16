package hitsz.aircraft;

import hitsz.basic.AbstractFlyingObject;
import hitsz.bullet.BaseBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 普通敌机
 * 不可射击
 *
 * @author edu.hitsz
 */
public class MobEnemy extends AbstractEnemy {
    @Override
    public int type(){
        return 3;
    }
    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
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
    public List<BaseBullet> shoot() {
        return new LinkedList<>();
    }

}