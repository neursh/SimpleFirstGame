package io.github.gucksus.simplefirstgame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class PopcornEnemy {
    public final float health = 1f;
    public final float speedY = .3f;
    final float amplitude = 2f;
    final float frequency = 2f;
    float timer = 0;
    Texture enemyLv1Texture;
    public Sprite selfSprite;
    float initialX;
    public Rectangle hitbox;
    public Rectangle hurtbox;
    float width = 1;
    float height = 1;
    float hitboxOffsetX = width / 32 * 7;
    float hitboxOffsetY = height / 32 * 8;
    float hurtboxOffsetX = width / 32 * 3;
    float hurtboxOffsetY = height / 32 * 7;

    public PopcornEnemy(float iniX, float iniY) {
        enemyLv1Texture = new Texture("enemylv1.png");
        selfSprite = new Sprite(enemyLv1Texture);
        selfSprite.setSize(width, height);
        selfSprite.setPosition(iniX, iniY);
        initialX = iniX;
        hitbox = new Rectangle(iniX + hitboxOffsetX, iniY + hitboxOffsetY, width / 32 * 18f, height / 32 * 14);
        hurtbox = new Rectangle(iniX + hurtboxOffsetX, iniY + hurtboxOffsetY, width / 32 * 26, height / 32 * 15);
    }

    public void moveWeirdly(float delta) {
        timer += delta;

        float newX = initialX + MathUtils.sin(timer * frequency) * amplitude;

        float newY = selfSprite.getY() - speedY * delta;

        hitbox.setPosition(newX + hitboxOffsetX, newY + hitboxOffsetY);
        hurtbox.setPosition(newX + hurtboxOffsetX, newY + hurtboxOffsetY);
        selfSprite.setPosition(newX, newY);
    }
}
