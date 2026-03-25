package io.github.gucksus.simplefirstgame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class PopcornEnemy extends Enemy {
    public PopcornEnemy(Texture texture, float iniX, float iniY) {
        super(texture, iniX, iniY, 1, 1);
        health = 1f;
        speedY = .3f;
        amplitude = 2f;
        frequency = 2f;
        hitboxOffsetX = width / 32 * 7;
        hitboxOffsetY = height / 32 * 8;
        hurtboxOffsetX = width / 32 * 3;
        hurtboxOffsetY = height / 32 * 7;
        hitbox = new Rectangle(iniX + hitboxOffsetX, iniY + hitboxOffsetY, width / 32 * 18f, height / 32 * 14);
        hurtbox = new Rectangle(iniX + hurtboxOffsetX, iniY + hurtboxOffsetY, width / 32 * 26, height / 32 * 15);
    }

    @Override
    public void update(float delta) {
        timer += delta;

        float newX = initialX + MathUtils.sin(timer * frequency) * amplitude;

        float newY = sprite.getY() - speedY * delta;

        hitbox.setPosition(newX + hitboxOffsetX, newY + hitboxOffsetY);
        hurtbox.setPosition(newX + hurtboxOffsetX, newY + hurtboxOffsetY);
        sprite.setPosition(newX, newY);
    }
}
