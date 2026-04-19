package io.github.gucksus.simplefirstgame.entities.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

/**
 * <b>YOU HAVE TO DECLARE THESE VARIABLES IN SUBCLASSES:</b> <I>speed, damage, fireRate, hitboxOffsetX, hitbox, maxBulletOnScreen.</I>
 */
public abstract class Bullet {
    protected float speed;
    protected float damage;
    protected float width;
    protected float height;
    protected Sprite sprite;
    protected Rectangle hitbox;
    protected float hitboxOffsetX; // This is because of the bullet sprite contains unnecessary pixels on the sides.
    protected int maxBulletOnScreen; // Limit the amount of bullet that can be on screen. This is a mechanic in shmups.
    protected float fireRate;

    public Bullet(Texture texture, float iniX, float iniY, float width, float height) {
        this.width = width;
        this.height = height;
        sprite = new Sprite(texture);
        sprite.setSize(width, height);
        sprite.setCenterX(iniX);
        sprite.setY(iniY);
    }

    /**
     * Update the position of the sprite and also hitbox. Only move them up in a straight line.
     */
    public void update(){
        float delta = Gdx.graphics.getDeltaTime();
        sprite.translateY(delta * speed);
        hitbox.setPosition(sprite.getX() + hitboxOffsetX, sprite.getY());
    }

    public float getDamage() {
        return damage;
    }

    public Rectangle getHitbox() {
        return  hitbox;
    }

    public int getMaxBulletOnScreen() {
        return maxBulletOnScreen;
    }

    public float getFireRate() {
        return fireRate;
    }

    public Sprite getSprite() {
        return sprite;
    }
}
