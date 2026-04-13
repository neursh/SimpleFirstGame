package io.github.gucksus.simplefirstgame.entities.base;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

/*REMEBER TO DECLARE ALL THESE VARIABLES IN SUBCLASSES: isCircle, circleHitBox OR rectangleHitbox.*/
public abstract class EnemyBullet {
    float speed;
    float width;
    float height;
    public float shootAngle;
    float timer;
    float damage = 1;
    public Sprite sprite;
    Rectangle rectangleHitbox;
    float rectangleHitboxOffsetX;
    float rectangleHitboxOffsetY;
    protected Circle circleHitbox;
    float circleHitboxOffsetX;
    float circleHitboxOffsetY;
    protected boolean isCircle;
    protected enum MovingType {
        Straight,
        Curve,
        Roundabout
    }
    protected MovingType movingType;

    public EnemyBullet (Texture texture, float iniX, float iniY, float width, float height, float shootAngle) {
        this.width = width;
        this.height = height;
        sprite = new Sprite(texture);
        sprite.setSize(width, height);
        sprite.setCenterX(iniX);
        sprite.setY(iniY);
        this.shootAngle = shootAngle;
    }

    void updateHitbox() {
        if (isCircle)
            circleHitbox.setPosition(sprite.getX() + circleHitboxOffsetX, sprite.getY() + circleHitboxOffsetY);
        else
            rectangleHitbox.setPosition(sprite.getX() + rectangleHitboxOffsetX, sprite.getY() + rectangleHitboxOffsetY);
    }

    public void updateStraight(float delta) {
        timer += delta;
        float theTraveledDistance = speed * timer;
        sprite.setPosition(theTraveledDistance * MathUtils.cos(shootAngle), theTraveledDistance * MathUtils.sin(shootAngle));
    }

    public void update(float delta) {
        switch (movingType) {
            case Straight:
                updateStraight(delta);
                break;
        }
        updateHitbox();
    }
}
