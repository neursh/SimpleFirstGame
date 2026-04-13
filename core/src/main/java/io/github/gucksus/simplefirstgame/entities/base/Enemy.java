package io.github.gucksus.simplefirstgame.entities.base;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import io.github.gucksus.simplefirstgame.entities.MainShip;

public abstract class Enemy {
    public float health;
    protected float amplitude;
    protected float frequency;
    float sinTimer = 0;
    public Sprite sprite;
    float initialX;
    public Rectangle hitbox;
    public Rectangle hurtbox;
    public float width;
    public float height;
    protected float hitboxOffsetX;
    protected float hitboxOffsetY;
    protected float shootPointOffsetX;
    protected float shootPointOffsetY;
    protected float hurtboxOffsetX;
    protected float hurtboxOffsetY;
    public boolean isDead = false;
    public boolean isMoving;
    public boolean isInvulnerable;
    public boolean isInvisible;
    public boolean previouslyInScreen;
    public boolean isHarmless;
    boolean shootInThisAnimation;
    public int numberOfTimeAllowedOnScreenLeft = 1;
    public float nextFrameXDifference;
    public float nextFrameYDifference;
    movingType currentMovingType;
    enum movingType {Straight, Curve}
    protected Texture bulletTexture;

    Animation<TextureRegion> shootAnimation;
    public Animation<TextureRegion> deathAnimation;
    int shootAnimationFrameNum;
    protected int shootAnimationRepeat = 3;
    float animationIntervalTimer;
    protected float animationIntervalTime = 3;
    int deathAnimationFrameNum;
    public float stateTime;
    enum AnimationType {Static, Shoot, Death}
    AnimationType currentAnimationType = AnimationType.Static;

    // This constructor initializes width, height, sprite, initial position and neglect everything else. Therefore,
    // you have to add it in the subclass.
    public Enemy(TextureRegion staticTexture, float iniX, float iniY, float width, float height) {
        this.width = width;
        this.height = height;
        sprite = new Sprite(staticTexture);
        sprite.setSize(width, height);
        sprite.setPosition(iniX, iniY);
        initialX = iniX;
        currentMovingType = movingType.Straight;
    }

    public void initializeShootAnimation(TextureRegion[] shootAnimationFrames) {
        shootAnimation = new Animation<>(0.1f, shootAnimationFrames);
        this.shootAnimationFrameNum = shootAnimationFrames.length;
        stateTime = 0;
    }

    public void initializeDeathAnimation(TextureRegion[] deathAnimationFrames) {
        deathAnimation = new Animation<>(0.1f, deathAnimationFrames);
        this.deathAnimationFrameNum = deathAnimationFrames.length;
        stateTime = 0;
    }

    public void updatePosition(float delta) {
        if (!isMoving)
            return;
        switch (currentMovingType) {
            case Straight:
                moveStraight();
                break;
            case Curve:
                moveCurve(delta);
        }
    }

    public void moveStraight() {
        sprite.translate(nextFrameXDifference, nextFrameYDifference);
        updateEnemyHitboxAndHurtboxWhenMoved();
    }

    public void moveCurve(float delta) {
        sinTimer += delta;

        float newX = initialX + MathUtils.sin(sinTimer * frequency) * amplitude;

        float newY = sprite.getY() + nextFrameYDifference;

        sprite.setPosition(newX, newY);
        updateEnemyHitboxAndHurtboxWhenMoved();
    }

    public void updateEnemyHitboxAndHurtboxWhenMoved() {
        hitbox.setPosition(sprite.getX() + hitboxOffsetX, sprite.getY() + hitboxOffsetY);
        hurtbox.setPosition(sprite.getX() + hurtboxOffsetX, sprite.getY() + hurtboxOffsetY);
    }

    public boolean isInScreenThisFrame(float worldWidth, float worldHeight) {
        return (sprite.getX() > -width && sprite.getX() < worldWidth && sprite.getX() > -height && sprite.getY() < worldHeight);
    }

    public void updateStatus(float worldWidth, float worldHeight) {
        if (health <= 0 && currentAnimationType != AnimationType.Death) {
            isDead = true;
            isInvulnerable = true;
            isHarmless = true;
            triggerDeathAnimation();
        }
        if (isInScreenThisFrame(worldWidth, worldHeight) && !previouslyInScreen) {
            numberOfTimeAllowedOnScreenLeft--;
            previouslyInScreen = true;
        } else if (!isInScreenThisFrame(worldWidth, worldHeight) && previouslyInScreen) {
            previouslyInScreen = false;
        }
        if (numberOfTimeAllowedOnScreenLeft == 0 && !isInScreenThisFrame(worldWidth, worldHeight) && !isInvulnerable) {
            isInvulnerable = true;
        }
    }

    public void triggerShootAnimation() {
        currentAnimationType = AnimationType.Shoot;
        stateTime = 0;
    }

    public void triggerDeathAnimation() {
        currentAnimationType = AnimationType.Death;
        stateTime = 0;
    }

    public void draw(SpriteBatch batch, float delta) {
        switch (currentAnimationType) {
            case Static:
                sprite.draw(batch);
                if (shootAnimationRepeat != 0 && animationIntervalTimer < animationIntervalTime) {
                    animationIntervalTimer += delta;
                } else if (shootAnimationRepeat != 0 && animationIntervalTimer >= animationIntervalTime) {
                    shootAnimationRepeat--;
                    animationIntervalTimer = 0;
                    shootInThisAnimation = false;
                    triggerShootAnimation();
                }
                break;
            case Shoot:
                if (shootAnimationFrameNum != 0) {
                    stateTime += delta;
                    animationIntervalTimer = 0;
                    TextureRegion currentFrame = shootAnimation.getKeyFrame(stateTime);
                    if (shootAnimation.isAnimationFinished(stateTime)) {
                        currentAnimationType = AnimationType.Static;
                    }
                    batch.draw(currentFrame, sprite.getX(), sprite.getY(), width, height);
                }
                break;
            case Death:
                if (deathAnimationFrameNum != 0) {
                    stateTime += delta;
                    TextureRegion currentFrame = deathAnimation.getKeyFrame(stateTime);
                    batch.draw(currentFrame, sprite.getX(), sprite.getY(), width, height);
                }
        }
    }

    boolean shootThisFrame() {
        return (shootAnimation.getKeyFrameIndex(stateTime) == 6);
    }

    protected abstract EnemyBullet returnBulletType(float shootPointX, float shootPointY, float dx, float dy);

    public EnemyBullet shoot(MainShip mainShip) {
        if (shootThisFrame() && !shootInThisAnimation && !isDead) {
            shootInThisAnimation = true;
            float shootPointX = sprite.getX() + shootPointOffsetX;
            float shootPointY = sprite.getY() + shootPointOffsetY;
            float dx = mainShip.getShipHurtboxCenterX() - shootPointX;
            float dy = mainShip.getShipHurtboxCenterY() - shootPointY;
            return returnBulletType(shootPointX, shootPointY, dx, dy);
        }
        else return null;
    }

    public void dispose() {
        bulletTexture.dispose();
    }

    public void debugShowShootPoint() {
        System.out.println(sprite.getX() + shootPointOffsetX + " and " + (sprite.getY() + shootPointOffsetY));
    }

}
