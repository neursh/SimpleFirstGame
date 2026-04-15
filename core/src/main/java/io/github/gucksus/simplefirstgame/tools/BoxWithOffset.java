package io.github.gucksus.simplefirstgame.tools;

import com.badlogic.gdx.math.Rectangle;

public class BoxWithOffset {
    Rectangle box;
    float offsetX;
    float offsetY;

    public BoxWithOffset(float iniX, float iniY, float width, float height, float offsetX, float offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        box = new Rectangle(iniX + offsetX, iniY + offsetY, width, height);
    }

    public void update(float newX, float newY) {
        box.setPosition(newX + offsetX, newY + offsetY);
    }

    public Rectangle getBox() {
        return box;
    }
}
