package io.github.gucksus.simplefirstgame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class Bulletlv1 {
    final float speed = 10f;
    final float damage = 1;
    public Sprite selfSprite;
    Texture bulletlv1Texture;
    public Rectangle hitbox;
    float width = .5f;
    float height = .5f;
    public float bulletFireRate = .2f;
    float hitboxOffsetX = width / 16 * 5;

    public Bulletlv1(float iniX, float iniY) {
        bulletlv1Texture = new Texture("bullet_texture.png");
        selfSprite = new Sprite(bulletlv1Texture);
        selfSprite.setSize(width, height);
        selfSprite.setCenterX(iniX);
        selfSprite.setY(iniY);
        hitbox = new Rectangle(iniX + hitboxOffsetX, iniY, width / 16 * 6, height / 16 * 10);
    }

    public void update(float delta){
        selfSprite.translateY(delta * speed);
        hitbox.setPosition(selfSprite.getX() + hitboxOffsetX, selfSprite.getY());
    }
}
