package io.github.gucksus.simplefirstgame.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class SkullShooterEnemy extends Enemy{
    public SkullShooterEnemy(TextureRegion staticTexture, float iniX, float iniY) {
        super(staticTexture , iniX, iniY, 4, 4);
        health = 3f;
        hitboxOffsetX = width / 32 * 6;
        hitboxOffsetY = height / 32 * 6;
        shootPointOffsetX = width / 32 * 16;
        shootPointOffsetY = height / 32 * 11.4f;
        hurtboxOffsetX = width / 32 * 3;
        hurtboxOffsetY = height / 32 * 7;
        hitbox = new Rectangle(iniX + hitboxOffsetX, iniY + hitboxOffsetY, width / 32 * 20f, height / 32 * 16);
        hurtbox = new Rectangle(iniX + hurtboxOffsetX, iniY + hurtboxOffsetY, width / 32 * 26, height / 32 * 15);
    }
}
