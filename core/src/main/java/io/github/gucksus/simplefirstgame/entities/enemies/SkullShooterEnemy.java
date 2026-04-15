package io.github.gucksus.simplefirstgame.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.gucksus.simplefirstgame.entities.base.Enemy;
import io.github.gucksus.simplefirstgame.entities.base.EnemyBullet;
import io.github.gucksus.simplefirstgame.entities.bullets.SkullShooterBullet;
import io.github.gucksus.simplefirstgame.tools.BoxWithOffset;

public class SkullShooterEnemy extends Enemy {
    public SkullShooterEnemy(TextureRegion staticTexture, Texture bulletTexture, float iniX, float iniY) {
        super(staticTexture , iniX, iniY, 4, 4);
        health = 5f;
        shootPointsOffsets.add(new Vector2(pixelLengthX * 32, pixelLengthY * 22));
        hitboxes.add(new BoxWithOffset(iniX, iniY, pixelLengthX * 10, pixelLengthY * 16, pixelLengthX * 27, pixelLengthY * 26));
        hurtboxes.add(new BoxWithOffset(iniX, iniY, pixelLengthX * 18, pixelLengthY * 20, pixelLengthX * 23, pixelLengthY * 25));
        this.bulletTexture = bulletTexture;
        animationInterval = 1;
        shootAnimationRepeat = 15;
    }

    @Override
    protected EnemyBullet returnBulletType(float shootPointX, float shootPointY, float dx, float dy) {
        return new SkullShooterBullet(bulletTexture, shootPointX, shootPointY, 3, 3, dx, dy);
    }

    @Override
    protected boolean shootThisFrame() {
        return (shootAnimation.getKeyFrameIndex(stateTime) == 6);
    }
}
