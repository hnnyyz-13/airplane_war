package hitsz.basic;

import android.graphics.Bitmap;

import hitsz.aircraft.AbstractAircraft;
import hitsz.application.ImageManager;
import hitsz.application.MainActivity;

/**
 * 可飞行对象的父类
 *
 * @author edu.hitsz
 */
public abstract class AbstractFlyingObject {
    // 屏幕尺寸
    private static int screenWidth = 512; // 默认值
    private static int screenHeight = 768; // 默认值
    
    //locationX、locationY为图片中心位置坐标
    /**
     * x 轴坐标
     */
    protected int locationX;

    /**
     * y 轴坐标
     */
    protected int locationY;


    /**
     * x 轴移动速度
     */
    protected int speedX;

    /**
     * y 轴移动速度
     */
    protected int speedY;

    /**
     * 图片,
     * null 表示未设置
     */
    protected Bitmap image = null;

    /**
     * x 轴长度，根据图片尺寸获得
     * -1 表示未设置
     */
    protected int width = -1;

    /**
     * y 轴长度，根据图片尺寸获得
     * -1 表示未设置
     */
    protected int height = -1;


    /**
     * 有效（生存）标记，
     * 通常标记为 false的对象会在下次刷新时清除
     */
    protected boolean isValid = true;


    public AbstractFlyingObject(int locationX, int locationY, int speedX, int speedY) {
        this.locationX = locationX;
        this.locationY = locationY;
        this.speedX = speedX;
        this.speedY = speedY;
    }
    public void boss_stop(int rand){
        speedY=0;
        if(rand%10<Math.random()*10){
            speedX = -speedX;
        }
    }

    /**
     * 可飞行对象根据速度移动
     * 若飞行对象触碰到横向边界，横向速度反向
     */
    public void forward() {
        locationX += speedX;
        locationY += speedY;

    }
    
    /**
     * 设置屏幕尺寸
     * @param width 屏幕宽度
     * @param height 屏幕高度
     */
    /**
     * 设置屏幕尺寸
     * @param width 屏幕宽度
     * @param height 屏幕高度
     */
    public static void setScreenSize(int width, int height) {
        screenWidth = width;
        screenHeight = height;
    }
    
    /**
     * 获取屏幕宽度
     * @return 屏幕宽度
     */
    public static int getScreenWidth() {
        return screenWidth;
    }
    
    /**
     * 获取屏幕高度
     * @return 屏幕高度
     */
    public static int getScreenHeight() {
        return screenHeight;
    }

    /**
     * 碰撞检测，当对方坐标进入我方范围，判定我方击中<br>
     * 对方与我方覆盖区域有交叉即判定撞击。
     *  <br>
     * 非飞机对象区域：
     *  横向，[x - width/2, x + width/2]
     *  纵向，[y - height/2, y + height/2]
     *  <br>
     * 飞机对象区域：
     *  横向，[x - width/2, x + width/2]
     *  纵向，[y - height/4, y + height/4]
     *
     * @param flyingObject 撞击对方
     * @return true: 我方被击中; false 我方未被击中
     */
    public boolean crash(AbstractFlyingObject flyingObject) {
        // 缩放因子，用于控制 y轴方向区域范围
        int factor = this instanceof AbstractAircraft ? 2 : 1; //我方
        int fFactor = flyingObject instanceof AbstractAircraft ? 2 : 1;//对方

        //对方坐标、宽度、高度
        int x = flyingObject.getLocationX();
        int y = flyingObject.getLocationY();
        int fWidth = flyingObject.getWidth();
        int fHeight = flyingObject.getHeight();

        return x + (fWidth+this.getWidth())/2 > locationX
                && x - (fWidth+this.getWidth())/2 < locationX
                && y + ( fHeight/fFactor+this.getHeight()/factor )/2 > locationY
                && y - ( fHeight/fFactor+this.getHeight()/factor )/2 < locationY;
    }

    public int getLocationX() {
        return locationX;
    }

    public int getLocationY() {
        return locationY;
    }

    public void setLocation(double locationX, double locationY){
        this.locationX = (int) locationX;
        this.locationY = (int) locationY;
    }

    public int getSpeedY() {
        return speedY;
    }

    public Bitmap getImage() {
        if (image == null){
            image = ImageManager.get(this);
        }
        return image;
    }

    public int getWidth() {
        if (width == -1){
            // 若未设置，则查询图片宽度并设置
            Bitmap img = ImageManager.get(this);
            if (img != null) {
                width = img.getWidth();
            } else {
                width = 30; // 默认宽度
            }
        }
        return width;
    }

    public int getHeight() {
        if (height == -1){
            // 若未设置，则查询图片高度并设置
            Bitmap img = ImageManager.get(this);
            if (img != null) {
                height = img.getHeight();
            } else {
                height = 30; // 默认高度
            }
        }
        return height;
    }
    public boolean notValid() {
        return !this.isValid;
    }

    /**
     * 标记消失，
     * isValid = false.
     * notValid() => true.
     */
    public void vanish() {
        isValid = false;
    }

}