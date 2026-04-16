package hitsz.factory;

import hitsz.aircraft.AbstractEnemy;
import hitsz.aircraft.BossEnemy;
import hitsz.application.ImageManager;
import hitsz.basic.AbstractFlyingObject;
import hitsz.tools.AbstractTools;

public class BossEnemyFactory implements Factory{
    private int bossHp = 1000;
    // 难度设置，实际应用中应该从外部传入
    private int difficulty = 1;
    
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
    
    public AbstractEnemy createEnemy(){
        if(difficulty == 2){
            bossHp+=500;
            System.out.println("This time Boss will be more powerful! Hp updated to "+bossHp+"!");
        }
        int screenWidth = AbstractFlyingObject.getScreenWidth();
        int screenHeight = AbstractFlyingObject.getScreenHeight();
        AbstractEnemy Enemy=new BossEnemy(
                (int) (Math.random() * (screenWidth - ImageManager.BOSS_ENEMY_IMAGE.getWidth())+ImageManager.BOSS_ENEMY_IMAGE.getWidth()/2),
                (int) (Math.random() * screenHeight * 0.05),
                1,
                2,
                bossHp);
        return Enemy;
    }
    public AbstractTools createTools(){
        return null;
    }
}