package hitsz.factory;

import hitsz.aircraft.AbstractEnemy;
import hitsz.aircraft.EliteEnemy;
import hitsz.application.ImageManager;
import hitsz.basic.AbstractFlyingObject;
import hitsz.tools.AbstractTools;
import hitsz.tools.Bloodtool;

public class BloodToolsFactory implements Factory{
    public AbstractEnemy createEnemy(){
        return null;
    }
    public AbstractTools createTools(){
        int screenWidth = AbstractFlyingObject.getScreenWidth();
        int screenHeight = AbstractFlyingObject.getScreenHeight();
        AbstractTools tools=new Bloodtool(
                (int) (Math.random() * (screenWidth - ImageManager.TOOL_1_IMAGE.getWidth())),
                (int) (Math.random() * screenHeight * 0.05),
                0,
                6,
                1);
        return tools;
    }
}