package io.github.gucksus.simplefirstgame.entities.bullets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.gucksus.simplefirstgame.entities.base.Bullet;
import io.github.gucksus.simplefirstgame.tools.BoxWithOffset;

public class PowerUp extends Bullet {
    public PowerUp(TextureRegion[] idleAnimationFrames, float iniX, float iniY,
            float initialVelocity, float initialAngle, SpriteBatch batch) {
        super(idleAnimationFrames, iniX, iniY, 1, 1, batch);
        super.initializeIdleAnimation(idleAnimationFrames);
        speed = 11;
        damage = 0;
        fireRate = 0;
        rectangleHitbox = new BoxWithOffset(iniX, iniY, 28, 24, 2, 5, pixelLength.x, pixelLength.y);
        this.initialAngle = initialAngle;
        this.initialVelocity = initialVelocity;
        movingType = MovingType.Oblique;
    }
}
