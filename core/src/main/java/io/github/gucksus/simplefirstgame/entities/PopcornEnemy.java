package io.github.gucksus.simplefirstgame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class PopcornEnemy extends Enemy {
    public PopcornEnemy(Texture texture, float iniX, float iniY) {
        super(texture, iniX, iniY, 1, 1);
        health = .3f;
        amplitude = 2f;
        frequency = 2f;
        hitboxOffsetX = width / 32 * 6;
        hitboxOffsetY = height / 32 * 6;
        hurtboxOffsetX = width / 32 * 3;
        hurtboxOffsetY = height / 32 * 7;
        hitbox = new Rectangle(iniX + hitboxOffsetX, iniY + hitboxOffsetY, width / 32 * 20f, height / 32 * 16);
        hurtbox = new Rectangle(iniX + hurtboxOffsetX, iniY + hurtboxOffsetY, width / 32 * 26, height / 32 * 15);
    }
}
