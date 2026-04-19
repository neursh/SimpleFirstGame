package io.github.gucksus.simplefirstgame.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.gucksus.simplefirstgame.entities.MainShip;
import io.github.gucksus.simplefirstgame.entities.base.Enemy;
import io.github.gucksus.simplefirstgame.entities.base.EnemyBullet;
import io.github.gucksus.simplefirstgame.entities.bullets.SkullShooterBullet;
import io.github.gucksus.simplefirstgame.tools.BoxWithOffset;
import io.github.gucksus.simplefirstgame.tools.DebugRenderer;

public class SkullShooterEnemy extends Enemy {
    public SkullShooterEnemy(TextureRegion staticTexture, Texture bulletTexture, float iniX, float iniY, float worldWidth, float worldHeight, MainShip mainShip, SpriteBatch batch, DebugRenderer debugRenderer) {
        super(staticTexture , iniX, iniY, 4, 4, worldWidth, worldHeight, mainShip, batch, debugRenderer);
        health = 6f;
        shootPointsOffsets.add(new Vector2(pixelLengthX * 32, pixelLengthY * 22));
        hitboxes.add(new BoxWithOffset(iniX, iniY, 10, 19, 27, 23, pixelLengthX, pixelLengthY));
        hurtboxes.add(new BoxWithOffset(iniX, iniY, 18, 10, 23, 33, pixelLengthX, pixelLengthY));
        hurtboxes.add(new BoxWithOffset(iniX, iniY, 12, 10, 26, 20, pixelLengthX, pixelLengthY));
        this.bulletTexture = bulletTexture;
        animationInterval = .7f;
        shootFrameInterval = .05f;
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
