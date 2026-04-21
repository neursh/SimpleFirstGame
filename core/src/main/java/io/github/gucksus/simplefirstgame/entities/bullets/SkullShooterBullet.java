package io.github.gucksus.simplefirstgame.entities.bullets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import io.github.gucksus.simplefirstgame.entities.base.Bullet;

public class SkullShooterBullet extends Bullet {
    public SkullShooterBullet(Texture texture, float iniX, float iniY, float width, float height,
            float dx, float dy, SpriteBatch batch) {
        super(texture, iniX, iniY, width, height, dx, dy, batch);
        movingType = MovingType.Straight;
        isCircle = true;
        circleHitbox = new Circle(sprite.getX(), sprite.getY(), .25f);
        speed = 5f;
        circleHitboxOffset.x = sprite.getWidth() / 2;
        circleHitboxOffset.y = sprite.getHeight() / 2;
    }
}
