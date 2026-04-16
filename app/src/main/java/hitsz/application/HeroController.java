package hitsz.application;

import hitsz.aircraft.HeroAircraft;
import hitsz.basic.AbstractFlyingObject;
import android.view.MotionEvent;

/**
 * 英雄机控制类
 * 监听触摸，控制英雄机的移动
 *
 * @author edu.hitsz
 */
public class HeroController  {
    private Game game;
    private HeroAircraft heroAircraft;

    public HeroController(Game game, HeroAircraft heroAircraft){
        this.game = game;
        this.heroAircraft = heroAircraft;
        // 在Android中，触摸事件直接在Game类的onTouchEvent方法中处理
        // 这里不需要添加监听器，因为Game类已经实现了onTouchEvent方法
    }

    /**
     * 处理触摸事件
     * @param event 触摸事件
     * @return 是否处理了事件
     */
    public boolean handleTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                int screenWidth = AbstractFlyingObject.getScreenWidth();
                int screenHeight = AbstractFlyingObject.getScreenHeight();
                if (x < 0 || x > screenWidth || y < 0 || y > screenHeight) {
                    // 防止超出边界
                    return false;
                }
                // 将float类型转换为double类型，与setLocation方法的参数类型保持一致
                heroAircraft.setLocation((double) x, (double) y);
                return true;
            default:
                return false;
        }
    }


}