package hitsz.factory;

import hitsz.aircraft.AbstractEnemy;
import hitsz.aircraft.EliteEnemy;
import hitsz.application.ImageManager;
import hitsz.basic.AbstractFlyingObject;
import hitsz.tools.AbstractTools;

public class EliteEnemyFactory implements Factory{
    public int EliteEnemyHp = 50;
    public AbstractEnemy createEnemy(){
        int screenWidth = AbstractFlyingObject.getScreenWidth();
        int screenHeight = AbstractFlyingObject.getScreenHeight();
        AbstractEnemy Enemy=new EliteEnemy(
                (int) (Math.random() * (screenWidth - ImageManager.ELITE_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * screenHeight * 0.05),
                0,
                7,
                EliteEnemyHp);
        return Enemy;
    }
    public AbstractTools createTools(){
        return null;
    }
}