package hitsz.factory;

import hitsz.aircraft.AbstractEnemy;
import hitsz.application.ImageManager;
import hitsz.basic.AbstractFlyingObject;
import hitsz.tools.AbstractTools;
import hitsz.tools.Bullettool;
import hitsz.tools.Superbullettool;

public class SuperBulletToolsFactory implements Factory{
    public AbstractEnemy createEnemy(){
        return null;
    }
    public AbstractTools createTools(){
        int screenWidth = AbstractFlyingObject.getScreenWidth();
        int screenHeight = AbstractFlyingObject.getScreenHeight();
        AbstractTools tools=new Superbullettool(
                (int) (Math.random() * (screenWidth - ImageManager.TOOL_4_IMAGE.getWidth())),
                (int) (Math.random() * screenHeight * 0.05),
                0,
                6,
                4);
        return tools;
    }
}