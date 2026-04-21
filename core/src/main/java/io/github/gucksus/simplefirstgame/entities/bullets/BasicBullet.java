package io.github.gucksus.simplefirstgame.entities.bullets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import io.github.gucksus.simplefirstgame.entities.base.Bullet;

public class BasicBullet extends Bullet {
    public BasicBullet(Texture texture, float iniX, float iniY, float dx, float dy,
            SpriteBatch batch) {
        super(texture, iniX, iniY, .5f, .5f, dx, dy, batch);
        speed = 17f;
        damage = 1f;
        fireRate = .2f;
        rectangleHitboxOffset.x = width / 16 * 5;
        rectangleHitbox = new Rectangle(sprite.getX() + rectangleHitboxOffset.x, sprite.getY(),
                width / 16 * 6, height / 16 * 10);
        maxBulletOnScreen = 5;
    }
}
