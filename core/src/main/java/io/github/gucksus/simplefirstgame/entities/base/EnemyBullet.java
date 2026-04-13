package io.github.gucksus.simplefirstgame.entities.base;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/*REMEMBER TO DECLARE ALL THESE VARIABLES IN SUBCLASSES: isCircle, circleHitBox OR rectangleHitbox, speed.*/
public abstract class EnemyBullet {
    float iniX;
    float iniY;
    protected float speed;
    float width;
    float height;
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
    public Vector2 direction;

    public EnemyBullet (Texture texture, float iniX, float iniY, float width, float height, float dx, float dy) {
        this.width = width;
        this.height = height;
        this.iniX = iniX;
        this.iniY = iniY;
        sprite = new Sprite(texture);
        sprite.setSize(width, height);
        sprite.setCenterX(iniX);
        sprite.setY(iniY);
        direction = new Vector2();
        direction.set(dx, dy);
    }

    void updateHitbox() {
        if (isCircle)
            circleHitbox.setPosition(sprite.getX() + circleHitboxOffsetX, sprite.getY() + circleHitboxOffsetY);
        else
            rectangleHitbox.setPosition(sprite.getX() + rectangleHitboxOffsetX, sprite.getY() + rectangleHitboxOffsetY);
    }

    public void updateStraight(float delta) {
        timer += delta;
        float distanceMultiplier = speed / direction.len();
        sprite.setCenterX(iniX + direction.x * distanceMultiplier * timer);
        sprite.setCenterY(iniY + direction.y * distanceMultiplier * timer);
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
