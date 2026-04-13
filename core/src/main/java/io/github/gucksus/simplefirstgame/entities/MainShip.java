package io.github.gucksus.simplefirstgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.gucksus.simplefirstgame.entities.base.Bullet;
import io.github.gucksus.simplefirstgame.entities.bullets.BasicBullet;

public class MainShip {
    Texture shipTexture;
    Texture basicBulletTexture;
    public Circle shipHurtbox;
    float hurtboxOffsetY;
    Sprite shipSprite;
    public Array<Bullet> bulletArray;
    float width;
    float height;
    float shipSpeed = 6f;
    Bullet currentBullet;
    float timerSinceLastShot;
    public short lives = 1;
    public float timerSinceLastDamage;
    public float invulnerableDuration = 1f;
    public boolean isDead = false;
    public boolean isInAnimation;
    Vector2 directionDifferenceMultiplier;
    Animation <TextureRegion> spinAnimation;
    Texture spinAnimationSheet;
    float stateTime;

    public MainShip(float centerX, float iniY, float width, float height) {
        this.width = width;
        this.height = height;
        hurtboxOffsetY = + height / 2.5f * 1.01f;
        timerSinceLastDamage = invulnerableDuration;
        shipTexture = new Texture("Mainship/ShipSprite.png");
        basicBulletTexture = new Texture("Bullet/basicBullet.png");
        spinAnimationSheet = new Texture("Mainship/ship_sprite_animation1.png");

        TextureRegion[][] temp = TextureRegion.split(spinAnimationSheet, spinAnimationSheet.getWidth() / 11, spinAnimationSheet.getHeight());
        TextureRegion[] spinFrames = new TextureRegion[11];
        System.arraycopy(temp[0], 0, spinFrames, 0, 11);

        spinAnimation = new Animation<>(0.1f, spinFrames);

        shipSprite = new Sprite(spinFrames[0]);
        shipSprite.setSize(width, height);
        shipSprite.setCenterX(centerX);
        shipSprite.setY(iniY);
        shipHurtbox = new Circle(shipSprite.getX() + width / 2, iniY + hurtboxOffsetY, .1f);
        bulletArray = new Array<>();
        currentBullet = new BasicBullet(basicBulletTexture, 69, 69);
        directionDifferenceMultiplier = new Vector2();
    }

    public void update(float delta, float worldWidth, float worldHeight) {
        timerSinceLastShot += delta;
        timerSinceLastDamage += delta;
        input(delta, worldWidth, worldHeight);
        // Update hurtbox position for the ship.
        shipHurtbox.setPosition(shipSprite.getX() + width / 2, shipSprite.getY() + hurtboxOffsetY);
        updateBullet(delta, worldHeight);
    }

    private void input(float delta, float worldWidth, float worldHeight) {
        float dx = 0;
        float dy = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) dx -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) dx += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) dy += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) dy -= 1;

        directionDifferenceMultiplier.set(dx, dy).nor();

        shipSprite.translateX(directionDifferenceMultiplier.x * shipSpeed * delta);
        shipSprite.translateY(directionDifferenceMultiplier.y * shipSpeed * delta);

        // Here I set so that the ship can go off-screen a quarter of its width.
        shipSprite.setX(MathUtils.clamp(shipSprite.getX(), -(shipSprite.getWidth()/4), worldWidth - shipSprite.getWidth() / 4 * 3));
        shipSprite.setY(MathUtils.clamp(shipSprite.getY(), 0, worldHeight - shipSprite.getHeight()));

        if (Gdx.input.isKeyPressed(Input.Keys.J)) {
            if (bulletArray.size < currentBullet.maxBulletOnScreen && timerSinceLastShot >= currentBullet.fireRate && !isDead) { // If the amount of bullet on screen is smaller than max amount, then allow shooting.
                float iniX = shipSprite.getX() + shipSprite.getWidth() / 2;
                float iniY = shipSprite.getY() + shipSprite.getHeight();
                bulletArray.add(new BasicBullet(basicBulletTexture , iniX, iniY));
                timerSinceLastShot = 0;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.L) && !isInAnimation) {
            isInAnimation = true;
        }
    }

    private void updateBullet(float delta, float worldHeight) {
        for (int i = bulletArray.size - 1; i >= 0; i--){
            bulletArray.get(i).update(delta);
        }

        for (int i = bulletArray.size - 1; i >= 0; i--) {
            Sprite currentBulletSprite = bulletArray.get(i).sprite;
            if (currentBulletSprite.getY() > worldHeight) {
                bulletArray.removeIndex(i);
            }
        }
    }

    public void draw(Batch batch, float delta) {
        if (!isDead && !isInAnimation) {
            shipSprite.draw(batch);
        }
        if (!isDead && isInAnimation) {
            stateTime += delta;

            TextureRegion currentFrame = spinAnimation.getKeyFrame(stateTime, false);
             batch.draw(currentFrame, shipSprite.getX(), shipSprite.getY(), width, height);

        }
        for (Bullet basicBullet : bulletArray) {
            basicBullet.sprite.draw(batch);
        }
    }

    public void drawShipHurtbox(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(shipHurtbox.x, shipHurtbox.y, shipHurtbox.radius, 12);
    }

    public void drawBulletHitbox(ShapeRenderer shapeRenderer) {
        Rectangle currentBulletHitbox = currentBullet.hitbox;
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(currentBulletHitbox.x, currentBulletHitbox.y, currentBulletHitbox.width, currentBulletHitbox.height);
    }

    public float getShipHurtboxCenterY() {
        return shipHurtbox.y + shipHurtbox.radius / 2;
    }

    public float getShipHurtboxCenterX() {
        return shipHurtbox.x + shipHurtbox.radius / 2;
    }

    public void dispose() {
        shipTexture.dispose();
        basicBulletTexture.dispose();
        spinAnimationSheet.dispose();
    }
}
