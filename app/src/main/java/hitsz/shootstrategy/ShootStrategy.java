package hitsz.shootstrategy;

import hitsz.bullet.BaseBullet;

import java.util.List;

public interface ShootStrategy {
    List<BaseBullet> ShootThisWay (int x,int y,int speedX,int speedY,int shootNum,int power,int heroORenemy);
}