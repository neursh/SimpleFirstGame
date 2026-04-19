package io.github.gucksus.simplefirstgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.gucksus.simplefirstgame.entities.base.Bullet;
import io.github.gucksus.simplefirstgame.entities.bullets.BasicBullet;
import io.github.gucksus.simplefirstgame.tools.DebugRenderer;

public class MainShip {
    Texture basicBulletTexture;
    public Circle shipHurtbox;
    float hurtboxOffsetY;
    Sprite shipSprite;
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
    float worldWidth;
    float worldHeight;
    SpriteBatch batch;
    public Array<Bullet> bulletArray;
    DebugRenderer debugRenderer;

    public MainShip(float centerX, float iniY, float width, float height, float worldWidth, float worldHeight, SpriteBatch batch, DebugRenderer debugRenderer) {
        this.width = width;
        this.height = height;
        hurtboxOffsetY = height / 2.5f * 1.01f;
        timerSinceLastDamage = invulnerableDuration;
        basicBulletTexture = new Texture("Bullet/basicBullet.png");
        spinAnimationSheet = new Texture("Mainship/ship_sprite_animation1.png");

        TextureRegion[][] temp = TextureRegion.split(spinAnimationSheet, spinAnimationSheet.getWidth() / 11, spinAnimationSheet.getHeight());

        spinAnimation = new Animation<>(0.1f, temp[0]);

        shipSprite = new Sprite(temp[0][0]);
        shipSprite.setSize(width, height);
        shipSprite.setCenterX(centerX);
        shipSprite.setY(iniY);
        shipHurtbox = new Circle(shipSprite.getX() + width / 2, iniY + hurtboxOffsetY, .1f);
        currentBullet = new BasicBullet(basicBulletTexture, 69, 69);
        directionDifferenceMultiplier = new Vector2();
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.batch = batch;
        bulletArray = new Array<>();
        this.debugRenderer = debugRenderer;
    }

    public void update() {
        float delta = Gdx.graphics.getDeltaTime();
        timerSinceLastShot += delta;
        timerSinceLastDamage += delta;
        input();
        // Update hurtbox position for the ship.
        shipHurtbox.setPosition(shipSprite.getX() + width / 2, shipSprite.getY() + hurtboxOffsetY);
        bulletUpdate();
    }

    private void input() {
        float delta = Gdx.graphics.getDeltaTime();
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
            if (bulletArray.size < currentBullet.getMaxBulletOnScreen() && timerSinceLastShot >= currentBullet.getFireRate() && !isDead) { // If the amount of bullet on screen is smaller than max amount, then allow shooting.
                float iniX = shipSprite.getX() + shipSprite.getWidth() / 2;
                float iniY = shipSprite.getY() + shipSprite.getHeight() / 64 * 48;
                bulletArray.add(new BasicBullet(basicBulletTexture, iniX, iniY));
                timerSinceLastShot = 0;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.L) && !isInAnimation) {
            isInAnimation = true;
        }
    }

    public void takeDamage() {
        lives -= 1;
        if (lives == 0){
            isDead = true;
        }
        timerSinceLastDamage = 0;
    }

    /**
     * This method updates bullet position; checks if a bullet is out of screen and removes any bullet that does.
     */
    private void bulletUpdate() {
        for (int i = bulletArray.size - 1; i >= 0; i--){
            bulletArray.get(i).update();
        }

        for (int i = bulletArray.size - 1; i >= 0; i--) {
            Sprite currentBulletSprite = bulletArray.get(i).getSprite();
            if (currentBulletSprite.getY() > worldHeight) {
                bulletArray.removeIndex(i);
            }
        }
    }

    void drawBullet() {
        for (Bullet bullet : bulletArray) {
            bullet.getSprite().draw(batch);
        }
    }

    public void draw() {
        float delta = Gdx.graphics.getDeltaTime();
        if (!isDead && !isInAnimation) {
            shipSprite.draw(batch);
        }
        if (!isDead && isInAnimation) {
            stateTime += delta;

            TextureRegion currentFrame = spinAnimation.getKeyFrame(stateTime, false);
             batch.draw(currentFrame, shipSprite.getX(), shipSprite.getY(), width, height);

        }
        drawBullet();
    }

    public void drawDebug() {
        debugRenderer.drawCircleHitbox(shipHurtbox);
        for (Bullet bullet: bulletArray) {
            debugRenderer.drawHitbox(bullet.getHitbox());
        }
    }

    public float getShipHurtboxCenterY() {
        return shipHurtbox.y + shipHurtbox.radius / 2;
    }

    public float getShipHurtboxCenterX() {
        return shipHurtbox.x + shipHurtbox.radius / 2;
    }

    public void dispose() {
        basicBulletTexture.dispose();
        spinAnimationSheet.dispose();
    }
}
