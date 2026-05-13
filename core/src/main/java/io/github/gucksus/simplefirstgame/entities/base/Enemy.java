package io.github.gucksus.simplefirstgame.entities.base;

import java.util.UUID;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import io.github.gucksus.simplefirstgame.Constants;
import io.github.gucksus.simplefirstgame.animation.AnimSpec;
import io.github.gucksus.simplefirstgame.entities.MainShip;
import io.github.gucksus.simplefirstgame.maths.AnimationTexture;
import io.github.gucksus.simplefirstgame.tools.BoxWithOffset;
import io.github.gucksus.simplefirstgame.tools.BulletHolder;
import io.github.gucksus.simplefirstgame.tools.DebugRenderer;
import io.github.gucksus.simplefirstgame.waves.Wave;

/**
 * <b>YOU HAVE TO DECLARE THESE VARIABLE IN SUBCLASSES:</b> <i>health, hitboxOffsetX and
 * hitboxOffsetY, hurtboxOffsetX and hurtboxOffsetY, shootPointOffsetX and shootPointOffsetY,
 * hitbox, hurtbox, bulletTexture, animationIntervalTime, shootAnimationRepeat.</i> <br>
 * <b><i>SET THESE VARIABLES AS 0 IF THE ENEMY DOES NOT HAVE SHOOT OR/AND DEATH ANIMATION:
 * shootAnimationFrameNum, deathAnimationFrameNum.</i></b>
 *
 * **YOU HAVE TO DECLARE THESE VARIABLE IN SUBCLASSES:**
 *
 *
 */
public abstract class Enemy {
    protected float health;
    protected Sprite sprite;
    protected Vector2 textureSize;
    protected Vector2 pixelLength;
    protected float width;
    protected float height;

    protected Array<BoxWithOffset> hitboxes = new Array<>();
    protected Array<BoxWithOffset> hurtboxes = new Array<>();
    protected Array<Vector2> shootPointsOffsets = new Array<>();

    protected boolean isDead = false;
    public boolean isMoving;
    protected boolean isInvulnerable;
    protected boolean isInvisible;
    protected boolean isHarmless;
    protected boolean shootInThisAnimation;

    public Array<Timer.Task> tasks = new Array<>();

    private String id = UUID.randomUUID().toString();

    protected Animation<TextureRegion> idleAnimation;
    protected Animation<TextureRegion> shootAnimation;
    AnimSpec<TextureRegion> shootAnimSpec;
    protected Animation<TextureRegion> deathAnimation;
    AnimSpec<TextureRegion> deathAnimSpec;
    protected int shootSpriteIndex;
    protected float idleFrameInterval = 0.1f;
    protected float shootFrameInterval = 0.1f;
    protected float deathFrameInterval = 0.1f;
    protected float animationInterval;
    protected float deathAnimationTimer;
    protected TextureRegion[] bulletIdleFrames;

    public SpriteBatch batch;
    protected DebugRenderer debugRenderer;
    protected MainShip mainShip;
    protected float worldWidth;
    protected float worldHeight;
    public Wave wave;

    Vector2 centerPoint;
    float radius;

    protected BulletHolder bulletHolder;

    public boolean shootAnimationActivated;

    protected float takeDamageTimer = 67;
    protected float takeDamageInterval = .2f;

    public Enemy(TextureRegion staticTexture, float iniX, float iniY, float width, float height,
            MainShip mainShip, Wave wave) {
        this.width = width;
        this.height = height;
        textureSize = new Vector2(staticTexture.getRegionWidth(), staticTexture.getRegionHeight());
        pixelLength = new Vector2(width / textureSize.x, height / textureSize.y);
        sprite = new Sprite(staticTexture);
        sprite.setSize(width, height);
        sprite.setPosition(iniX - width / 2, iniY - height / 2);
        this.batch = Constants.batch;
        this.debugRenderer = Constants.debugRenderer;
        this.mainShip = mainShip;
        this.worldWidth = Constants.worldWidth;
        this.worldHeight = Constants.worldHeight;
        this.wave = wave;
        this.bulletHolder = wave.level.bulletHolder;
    }

    public void initializeIdleAnimation(TextureRegion[] idleAnimationFrames) {
        idleAnimation = new Animation<>(idleFrameInterval, idleAnimationFrames);
        AnimationTexture idle = new AnimationTexture(idleAnimation);
        AnimSpec<TextureRegion> idleAnimSpec = new AnimSpec<>(idle, (value, progress) -> {
            this.sprite.setRegion(value);
        }, 0, idleAnimation.getAnimationDuration(), 0, 999);
        Constants.textureAnimScheduler.play(id + "Idle", idleAnimSpec);
    }

    public void initializeShootAnimation(TextureRegion[] shootAnimationFrames) {
        shootAnimation = new Animation<>(shootFrameInterval, shootAnimationFrames);
        AnimationTexture shoot = new AnimationTexture(shootAnimation);
        shootAnimSpec = new AnimSpec<>(shoot, (value, progress) -> {
            this.shootUpdate(progress);
            this.sprite.setRegion(value);
        }, 1, shootAnimation.getAnimationDuration(), 0, 100);
        Constants.textureAnimScheduler.play(id + "Shoot", shootAnimSpec);
    }

    void shootUpdate(float progress) {
        if (shootAnimation.getKeyFrameIndex(
                shootAnimation.getAnimationDuration() * progress) == shootSpriteIndex) {
            shoot();
            return;
        }

        if (shootAnimation.getKeyFrameIndex(
                shootAnimation.getAnimationDuration() * progress) == shootSpriteIndex + 1) {
            shootInThisAnimation = false;
            return;
        }

        if (progress == 1) {
            Constants.textureAnimScheduler.resume(id + "Idle");
            return;
        }

        if (progress == 0)
            Constants.textureAnimScheduler.pause(id + "Idle");
    }

    /**
     *
     * @param shootPointX The X coordinate of the shoot point.
     * @param shootPointY The Y coordinate of the shoot point.
     * @param dx The X direction of the vector.
     * @param dy The Y direction of the vector.
     * @return The bullet type of this enemy.
     */
    protected abstract Bullet returnBulletType(float shootPointX, float shootPointY, float dx,
            float dy);

    public void shoot() {
        if (!shootInThisAnimation && !isDead) {
            for (Vector2 shootPointOffset : shootPointsOffsets) {
                shootInThisAnimation = true;
                float shootPointX = sprite.getX() + shootPointOffset.x;
                float shootPointY = sprite.getY() + shootPointOffset.y;
                float dx = mainShip.getShipHurtboxCenterX() - shootPointX;
                float dy = mainShip.getShipHurtboxCenterY() - shootPointY;

                bulletHolder.enemyBullets.add(returnBulletType(shootPointX, shootPointY, dx, dy));
            }
        }
    }

    public void initializeDeathAnimation(TextureRegion[] deathAnimationFrames) {
        deathAnimation = new Animation<>(deathFrameInterval, deathAnimationFrames);
        AnimationTexture death = new AnimationTexture(deathAnimation);
        deathAnimSpec = new AnimSpec<>(death, (value, progress) -> {
            this.sprite.setRegion(value);
        }, 0, deathAnimation.getAnimationDuration(), 0, 99);
    }

    public void triggerDeathAnimation() {
        Constants.textureAnimScheduler.stop(id + "Idle");
        Constants.textureAnimScheduler.stop(id + "Shoot");
        Constants.textureAnimScheduler.play(id + "Death", deathAnimSpec);
    }

    public boolean isDeathAnimationFinished() {
        float delta = Gdx.graphics.getDeltaTime();
        if (isDead)
            deathAnimationTimer += delta;
        return deathAnimationTimer >= deathAnimation.getAnimationDuration();
    }

    public void draw() {
        sprite.draw(batch);
    }

    public void drawDebug() {
        drawHitbox();
    }

    public void update() {
        float delta = Gdx.graphics.getDeltaTime();
        takeDamageTimer += delta;
        moveUpdate();
        updateEnemyHitboxAndHurtbox();
        hitboxCheck();
        hurtboxCheck();
    }

    protected void moveUpdate() {}

    void updateEnemyHitboxAndHurtbox() {
        for (BoxWithOffset hitbox : hitboxes) {
            hitbox.update(sprite.getX(), sprite.getY());
        }
        for (BoxWithOffset hurtbox : hurtboxes) {
            hurtbox.update(sprite.getX(), sprite.getY());
        }
    }

    void drawHitbox() {
        for (BoxWithOffset hitbox : hitboxes) {
            debugRenderer.drawHitbox(hitbox.getBox());
        }
        for (BoxWithOffset hurtbox : hurtboxes) {
            debugRenderer.drawHurtbox(hurtbox.getBox());
        }
    }

    void hitboxCheck() {
        for (BoxWithOffset hitbox : hitboxes) {
            if (Intersector.overlaps(mainShip.shipHurtbox, hitbox.getBox())
                    && mainShip.timerSinceLastDamage > mainShip.invulnerableDuration && !isHarmless)
                mainShip.takeDamage();
        }

        for (Bullet enemyBullet : bulletHolder.enemyBullets) {
            if (enemyBullet.getDamage() == 0) {
                if (Intersector.overlaps(mainShip.shipHurtbox, enemyBullet.getRectangleHitbox())) {
                    bulletHolder.enemyBullets.removeValue(enemyBullet, true);
                }
                continue;
            }


            if (enemyBullet.isCircle()) {
                for (Bullet bulletTerminator : bulletHolder.bulletTerminators) {
                    if (Intersector.overlaps(enemyBullet.getCircleHitbox(),
                            bulletTerminator.getRectangleHitbox())) {
                        bulletHolder.enemyBullets.removeValue(enemyBullet, true);
                        continue;
                    }
                }
                if (Intersector.overlaps(enemyBullet.getCircleHitbox(), mainShip.shipHurtbox)) {
                    mainShip.takeDamage();
                }

            } else {
                for (Bullet bulletTerminator : bulletHolder.bulletTerminators) {
                    if (Intersector.overlaps(enemyBullet.getRectangleHitbox(),
                            bulletTerminator.getRectangleHitbox())) {
                        bulletHolder.enemyBullets.removeValue(enemyBullet, true);
                        continue;
                    }
                }
                if (Intersector.overlaps(mainShip.shipHurtbox, enemyBullet.getRectangleHitbox())) {
                    mainShip.takeDamage();
                }
            }
        }
    }

    void hurtboxCheck() {
        for (BoxWithOffset hurtbox : hurtboxes) {
            for (int i = bulletHolder.shipBullets.size - 1; i >= 0; i--) {
                Bullet bullet = bulletHolder.shipBullets.get(i);
                if (Intersector.overlaps(bullet.rectangleHitbox.getBox(), hurtbox.getBox())
                        && !isInvulnerable) {
                    takeDamage(bullet.getDamage());
                    bulletHolder.shipBullets.removeIndex(i);
                }
            }
            for (Bullet power : bulletHolder.shipPower) {
                if (Intersector.overlaps(power.getRectangleHitbox(), hurtbox.getBox())
                        && !isInvulnerable) {
                    takeDamage(power.getDamage());
                }
            }
        }
    }

    protected void takeDamage(float damage) {
        if (takeDamageTimer >= takeDamageInterval) {
            health -= damage;
            if (health <= 0) {
                isDead = true;
                isInvulnerable = true;
                isHarmless = true;
                triggerDeathAnimation();
            }
            takeDamageTimer = 0;
        }
    }

    public boolean getIsDead() {
        return isDead;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Vector2 getCoordinate() {
        return new Vector2(sprite.getX() + width / 2, sprite.getY() + height / 2);
    }

    public void setPosition(Vector2 newPosition) {
        sprite.setCenter(newPosition.x, newPosition.y);
    }

    public String getId() {
        return id;
    }
}
