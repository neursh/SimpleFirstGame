package io.github.gucksus.simplefirstgame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class Bulletlv1 {
    final float speed = 5f;
//    final float damage = 1;
    public Sprite selfSprite;
    Texture bulletlv1Texture;
    public Rectangle hitbox;
    float bulletWidth = .5f;
    float bulletHeight = .5f;

    public Bulletlv1(float iniX, float iniY) {
        bulletlv1Texture = new Texture("bullet_texture.png");
        selfSprite = new Sprite(bulletlv1Texture);
        selfSprite.setSize(bulletWidth, bulletHeight);
        selfSprite.setCenterX(iniX);
        selfSprite.setY(iniY);
        hitbox = new Rectangle(iniX + bulletWidth / 16 * 5, iniY, bulletWidth / 16 * 6, bulletHeight / 16 * 10);
    }

    public void update(float delta){
        selfSprite.translateY(delta * speed);
        hitbox.setPosition(selfSprite.getX()  + bulletWidth / 16 * 5, selfSprite.getY());
    }
}
