package hitsz.shootstrategy;

import hitsz.bullet.BaseBullet;
import hitsz.bullet.EnemyBullet;
import hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

public class StraightShootStrategy implements ShootStrategy{
    public List<BaseBullet> ShootThisWay(int x,int y,int speedX,int speedY,int shootNum,int power,int heroORenemy){
        List<BaseBullet> res = new LinkedList<>();
        for(int i=0; i<shootNum; i++){
            // 子弹发射位置相对飞机位置向前偏移
            // 多个子弹横向分散
            if(heroORenemy == 1)//enemy
                res.add(new EnemyBullet(x + (i*2 - shootNum + 1)*10, y, 0, speedY, power));
            else//hero
                res.add(new HeroBullet(x + (i*2 - shootNum + 1)*10, y, 0, speedY, power));
        }
        return res;
    }

}