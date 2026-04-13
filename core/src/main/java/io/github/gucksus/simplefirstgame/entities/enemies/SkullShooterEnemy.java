package io.github.gucksus.simplefirstgame.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import io.github.gucksus.simplefirstgame.entities.base.Enemy;
import io.github.gucksus.simplefirstgame.entities.base.EnemyBullet;
import io.github.gucksus.simplefirstgame.entities.bullets.SkullShooterBullet;

public class SkullShooterEnemy extends Enemy {
    public SkullShooterEnemy(TextureRegion staticTexture, float iniX, float iniY) {
        super(staticTexture , iniX, iniY, 4, 4);
        health = 3f;
        hitboxOffsetX = width / 32 * 6;
        hitboxOffsetY = height / 32 * 6;
        shootPointOffsetX = 4/ 64f * 32;
        shootPointOffsetY = 4 / 64f * 22;
        hurtboxOffsetX = width / 32 * 3;
        hurtboxOffsetY = height / 32 * 7;
        hitbox = new Rectangle(iniX + hitboxOffsetX, iniY + hitboxOffsetY, width / 32 * 20f, height / 32 * 16);
        hurtbox = new Rectangle(iniX + hurtboxOffsetX, iniY + hurtboxOffsetY, width / 32 * 26, height / 32 * 15);
        bulletTexture = new Texture("Bullet/skull_bullet_texture.png");
        animationIntervalTime = 1;
        shootAnimationRepeat = 15;
    }

    @Override
    protected EnemyBullet returnBulletType(float shootPointX, float shootPointY, float dx, float dy) {
        return new SkullShooterBullet(bulletTexture, shootPointX, shootPointY, 2, 2, dx, dy);
    }
}
