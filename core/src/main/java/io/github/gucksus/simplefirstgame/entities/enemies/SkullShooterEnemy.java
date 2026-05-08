package io.github.gucksus.simplefirstgame.entities.enemies;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.gucksus.simplefirstgame.entities.MainShip;
import io.github.gucksus.simplefirstgame.entities.base.Bullet;
import io.github.gucksus.simplefirstgame.entities.base.Enemy;
import io.github.gucksus.simplefirstgame.entities.bullets.SkullShooterBullet;
import io.github.gucksus.simplefirstgame.tools.BoxWithOffset;
import io.github.gucksus.simplefirstgame.waves.Wave;

public class SkullShooterEnemy extends Enemy {
    public SkullShooterEnemy(TextureRegion staticTexture, TextureRegion[] bulletIdleAnimationFrames,
            float iniX, float iniY, MainShip mainShip, Wave wave) {
        super(staticTexture, iniX, iniY, 4, 4, mainShip, wave);
        health = 6f;
        shootPointsOffsets.add(new Vector2(pixelLength.x * 32, pixelLength.y * 22));
        hitboxes.add(new BoxWithOffset(iniX, iniY, 10, 19, 27, 23, pixelLength.x, pixelLength.y));
        hurtboxes.add(new BoxWithOffset(iniX, iniY, 18, 10, 23, 33, pixelLength.x, pixelLength.y));
        hurtboxes.add(new BoxWithOffset(iniX, iniY, 12, 10, 26, 20, pixelLength.x, pixelLength.y));
        this.bulletIdleFrames = bulletIdleAnimationFrames;
        animationInterval = .7f;
        shootFrameInterval = .05f;
        shootSpriteIndex = 6;
    }

    @Override
    protected Bullet returnBulletType(float shootPointX, float shootPointY, float dx, float dy) {
        return new SkullShooterBullet(bulletIdleFrames, shootPointX, shootPointY, 3, 3, dx, dy,
                batch);
    }
}
