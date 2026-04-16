package hitsz.aircraft;

import hitsz.aircraft.AbstractAircraft;
import hitsz.basic.AbstractFlyingObject;
import hitsz.bullet.BaseBullet;

import java.util.List;

public abstract class AbstractEnemy extends AbstractAircraft{
    public AbstractEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }
    public void forward(){
        super.forward();
        if (locationX-this.getWidth()/2 <= 0 || locationX+ this.getWidth()/2>= AbstractFlyingObject.getScreenWidth()) {
            // 横向超出边界后反向
            speedX = -speedX;
        }
    }
    public abstract List<BaseBullet> shoot();
}