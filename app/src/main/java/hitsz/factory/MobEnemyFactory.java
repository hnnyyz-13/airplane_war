package hitsz.factory;

import hitsz.aircraft.AbstractEnemy;
import hitsz.aircraft.EliteEnemy;
import hitsz.aircraft.MobEnemy;
import hitsz.application.ImageManager;
import hitsz.basic.AbstractFlyingObject;
import hitsz.tools.AbstractTools;

public class MobEnemyFactory implements Factory{
    public int MobEnemyHp = 30;
    public AbstractEnemy createEnemy(){
        int screenWidth = AbstractFlyingObject.getScreenWidth();
        int screenHeight = AbstractFlyingObject.getScreenHeight();
        AbstractEnemy Enemy=new MobEnemy(
                (int) (Math.random() * (screenWidth - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * screenHeight * 0.05),
                0,
                10,
                30);
        return Enemy;
    }
    public AbstractTools createTools(){
        return null;
    }
}