package hitsz.factory;

import hitsz.aircraft.AbstractEnemy;
import hitsz.aircraft.EliteEnemy;
import hitsz.application.ImageManager;
import hitsz.basic.AbstractFlyingObject;
import hitsz.tools.AbstractTools;

public interface Factory {
    AbstractEnemy createEnemy();
    AbstractTools createTools();
}