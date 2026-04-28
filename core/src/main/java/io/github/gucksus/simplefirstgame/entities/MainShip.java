package io.github.gucksus.simplefirstgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.gucksus.simplefirstgame.Constants;
import io.github.gucksus.simplefirstgame.entities.base.Bullet;
import io.github.gucksus.simplefirstgame.entities.bullets.BasicBullet;
import io.github.gucksus.simplefirstgame.tools.BulletHolder;
import io.github.gucksus.simplefirstgame.tools.DebugRenderer;

public class MainShip {
    Texture basicBulletIdleSheet;
    TextureRegion[] basicBulletIdleFrames;
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
    Animation<TextureRegion> spinAnimation;
    Texture spinAnimationSheet;
    float stateTime;
    float worldWidth;
    float worldHeight;
    SpriteBatch batch;
    DebugRenderer debugRenderer;
    BulletHolder bulletHolder;

    public MainShip(float centerX, float iniY, float width, float height,
            BulletHolder bulletHolder) {
        this.width = width;
        this.height = height;
        hurtboxOffsetY = height / 2.5f * 1.01f;
        timerSinceLastDamage = invulnerableDuration;
        basicBulletIdleSheet = new Texture("Bullet/basicBullet.png");
        spinAnimationSheet = new Texture("Mainship/ship_sprite_animation1.png");

        TextureRegion[][] temp = TextureRegion.split(spinAnimationSheet,
                spinAnimationSheet.getWidth() / 11, spinAnimationSheet.getHeight());

        spinAnimation = new Animation<>(0.1f, temp[0]);
        basicBulletIdleFrames = getBasicBulletIdleFrames();

        shipSprite = new Sprite(temp[0][0]);
        shipSprite.setSize(width, height);
        shipSprite.setCenterX(centerX);
        shipSprite.setY(iniY);
        shipHurtbox = new Circle(shipSprite.getX() + width / 2, iniY + hurtboxOffsetY, .1f);
        currentBullet = new BasicBullet(basicBulletIdleFrames, 69, 69, 67, 67, batch);
        directionDifferenceMultiplier = new Vector2();
        worldWidth = Constants.worldWidth;
        worldHeight = Constants.worldHeight;
        batch = Constants.batch;
        debugRenderer = Constants.debugRenderer;
        this.bulletHolder = bulletHolder;
    }

    TextureRegion[] getBasicBulletIdleFrames() {
        return TextureRegion.split(basicBulletIdleSheet, 16, 16)[0];
    }

    public void update() {
        float delta = Gdx.graphics.getDeltaTime();
        timerSinceLastShot += delta;
        timerSinceLastDamage += delta;
        input();
        // Update hurtbox position for the ship.
        shipHurtbox.setPosition(shipSprite.getX() + width / 2, shipSprite.getY() + hurtboxOffsetY);
    }

    private void input() {
        float delta = Gdx.graphics.getDeltaTime();
        float dx = 0;
        float dy = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.A))
            dx -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            dx += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.W))
            dy += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S))
            dy -= 1;

        directionDifferenceMultiplier.set(dx, dy).nor();

        shipSprite.translateX(directionDifferenceMultiplier.x * shipSpeed * delta);
        shipSprite.translateY(directionDifferenceMultiplier.y * shipSpeed * delta);

        // Here I set so that the ship can go off-screen a quarter of its width.
        shipSprite.setX(MathUtils.clamp(shipSprite.getX(), -(shipSprite.getWidth() / 4),
                worldWidth - shipSprite.getWidth() / 4 * 3));
        shipSprite
                .setY(MathUtils.clamp(shipSprite.getY(), 0, worldHeight - shipSprite.getHeight()));

        if (Gdx.input.isKeyPressed(Input.Keys.J)) {
            // If the amount of bullet on screen is smaller than max amount, then allow shooting.
            if (bulletHolder.shipBullets.size < currentBullet.getMaxBulletOnScreen()
                    && timerSinceLastShot >= currentBullet.getFireRate() && !isDead) {
                float iniX = shipSprite.getX() + shipSprite.getWidth() / 2;
                float iniY = shipSprite.getY() + shipSprite.getHeight() / 64 * 48;
                bulletHolder.shipBullets
                        .add(new BasicBullet(basicBulletIdleFrames, iniX, iniY, 0, 1, batch));
                timerSinceLastShot = 0;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.L) && !isInAnimation) {
            isInAnimation = true;
        }
    }

    public void takeDamage() {
        lives -= 1;
        if (lives == 0) {
            isDead = true;
        }
        timerSinceLastDamage = 0;
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
    }

    public void drawDebug() {
        debugRenderer.drawCircleHitbox(shipHurtbox);
    }

    public float getShipHurtboxCenterY() {
        return shipHurtbox.y + shipHurtbox.radius / 2;
    }

    public float getShipHurtboxCenterX() {
        return shipHurtbox.x + shipHurtbox.radius / 2;
    }

    public void dispose() {
        basicBulletIdleSheet.dispose();
        spinAnimationSheet.dispose();
    }
}
