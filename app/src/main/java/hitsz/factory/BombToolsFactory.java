package hitsz.factory;

import hitsz.aircraft.AbstractEnemy;
import hitsz.application.ImageManager;
import hitsz.basic.AbstractFlyingObject;
import hitsz.tools.AbstractTools;
import hitsz.tools.Bombtool;

public class BombToolsFactory implements Factory{
    public AbstractEnemy createEnemy(){
        return null;
    }
    public AbstractTools createTools(){
        int screenWidth = AbstractFlyingObject.getScreenWidth();
        int screenHeight = AbstractFlyingObject.getScreenHeight();
        AbstractTools tools=new Bombtool(
                (int) (Math.random() * (screenWidth - ImageManager.TOOL_1_IMAGE.getWidth())),
                (int) (Math.random() * screenHeight * 0.05),
                0,
                6,
                2);
        return tools;
    }
}