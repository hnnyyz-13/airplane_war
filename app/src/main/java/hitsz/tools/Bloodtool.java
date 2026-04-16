package hitsz.tools;

public class Bloodtool extends AbstractTools{
    public Bloodtool(int locationX, int locationY, int speedX, int speedY, int func) {
        super(locationX, locationY, speedX, speedY, func);
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= hitsz.basic.AbstractFlyingObject.getScreenHeight() ) {
            vanish();
        }
    }

}