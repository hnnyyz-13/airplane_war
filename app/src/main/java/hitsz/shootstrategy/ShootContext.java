package hitsz.shootstrategy;

import hitsz.bullet.BaseBullet;

import java.util.List;

public class ShootContext {
    private ShootStrategy strategy;
    public ShootContext(ShootStrategy strategy){
        this.strategy = strategy;
    }
    public void setStrategy(ShootStrategy strategy) {
        this.strategy = strategy;
    }
    public List<BaseBullet> executeStrategy(int x, int y, int speedX, int speedY, int shootNum, int power, int heroORenemy){
        return strategy.ShootThisWay(x,y,speedX,speedY,shootNum,power,heroORenemy);
    }
}