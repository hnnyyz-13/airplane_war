package hitsz.factory;

import hitsz.aircraft.AbstractEnemy;
import hitsz.aircraft.SuperEnemy;
import hitsz.application.ImageManager;
import hitsz.basic.AbstractFlyingObject;
import hitsz.tools.AbstractTools;

public class SuperEnemyFactory implements Factory{
    public int SuperEnemyHp = 90;
    public AbstractEnemy createEnemy(){
        int screenWidth = AbstractFlyingObject.getScreenWidth();
        int screenHeight = AbstractFlyingObject.getScreenHeight();
        AbstractEnemy Enemy=new SuperEnemy(
                (int) (Math.random() * (screenWidth - ImageManager.SUPER_ENEMY_IMAGE.getWidth())+ImageManager.SUPER_ENEMY_IMAGE.getWidth()/2),
                (int) (Math.random() * screenHeight * 0.05),
                2*((Math.random()<0.5)?1:-1),
                4,
                SuperEnemyHp);
        return Enemy;
    }
    public AbstractTools createTools(){
        return null;
    }
}