package io.github.gucksus.simplefirstgame.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public abstract class Enemy {
    public float health;
    float amplitude;
    float frequency;
    float sinTimer = 0;
    public Sprite sprite;
    float initialX;
    public Rectangle hitbox;
    public Rectangle hurtbox;
    public float width;
    public float height;
    float hitboxOffsetX;
    float hitboxOffsetY;
    float shootPointOffsetX;
    float shootPointOffsetY;
    float hurtboxOffsetX;
    float hurtboxOffsetY;
    public boolean isDead = false;
    public boolean isMoving;
    public boolean isInvulnerable;
    public boolean isInvisible;
    public boolean previouslyInScreen;
    public boolean isHarmless;
    public int numberOfTimeAllowedOnScreenLeft = 1;
    public float nextFrameXDifference;
    public float nextFrameYDifference;
    movingType currentMovingType;
    enum movingType {Straight, Curve}

    Animation<TextureRegion> shootAnimation;
    public Animation<TextureRegion> deathAnimation;
    int shootAnimationFrameNum;
    int shootAnimationRepeat = 3;
    float animationIntervalTimer;
    float animationIntervalTime = 3;
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

    void shoot(MainShip ship) {

    }

}
