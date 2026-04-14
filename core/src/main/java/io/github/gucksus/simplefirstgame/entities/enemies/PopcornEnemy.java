package io.github.gucksus.simplefirstgame.entities.enemies;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import io.github.gucksus.simplefirstgame.entities.base.Enemy;
import io.github.gucksus.simplefirstgame.entities.base.EnemyBullet;

public class PopcornEnemy extends Enemy {
    public PopcornEnemy(TextureRegion staticTexture, float iniX, float iniY) {
        super(staticTexture , iniX, iniY, 1, 1);
        health = 1f;
        hitboxOffsetX = width / 32 * 6;
        hitboxOffsetY = height / 32 * 6;
        hurtboxOffsetX = width / 32 * 3;
        hurtboxOffsetY = height / 32 * 7;
        hitbox = new Rectangle(iniX + hitboxOffsetX, iniY + hitboxOffsetY, width / 32 * 20f, height / 32 * 16);
        hurtbox = new Rectangle(iniX + hurtboxOffsetX, iniY + hurtboxOffsetY, width / 32 * 26, height / 32 * 15);
    }

    @Override
    protected EnemyBullet returnBulletType(float shootPointX, float shootPointY, float dx, float dy) {
        return null;
    }

    @Override
    protected boolean shootThisFrame() {return false;}
}
