package hitsz.tools;

import hitsz.basic.AbstractFlyingObject;
public abstract class AbstractTools extends AbstractFlyingObject{
    int func;
    /*
    * func功能
    * 1 生命
    * 2 炸弹
    * 3 火力
    * */
    public AbstractTools(int locationX, int locationY, int speedX, int speedY ,int Func) {
        super(locationX,locationY,speedX,speedY);
        this.func=Func;
    }
    @Override
    public void forward() {
        super.forward();

        // 判定 x 轴出界
        if (locationX <= 0 || locationX >= AbstractFlyingObject.getScreenWidth()) {
            vanish();
        }

        // 判定 y 轴出界
        if (speedY > 0 && locationY >= AbstractFlyingObject.getScreenHeight() ) {
            // 向下飞行出界
            vanish();
        }else if (locationY <= 0){
            // 向上飞行出界
            vanish();
        }
    }
    public int getFunc() {
        return func;
    }
}