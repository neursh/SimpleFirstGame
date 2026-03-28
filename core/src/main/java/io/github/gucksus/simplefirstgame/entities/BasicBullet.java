package io.github.gucksus.simplefirstgame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class BasicBullet extends Bullet {
    public BasicBullet(Texture texture ,float iniX, float iniY) {
        super(texture, iniX, iniY, .5f, .5f);
        speed = 17f;
        damage = 1f;
        fireRate = .3f;
        hitboxOffsetX = width / 16 * 5;
        hitbox = new Rectangle(iniX + hitboxOffsetX, iniY, width / 16 * 6, height / 16 * 10);
        maxBulletOnScreen = 5;
    }
}
