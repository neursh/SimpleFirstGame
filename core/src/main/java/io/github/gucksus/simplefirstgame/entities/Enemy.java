package io.github.gucksus.simplefirstgame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
    float hurtboxOffsetX;
    float hurtboxOffsetY;
    public boolean isDead = false;
    public boolean isMoving;
    public boolean isInvulnerable;
    public boolean isInvisible;
    public boolean previouslyInScreen;
    public int numberOfTimeAllowedOnScreenLeft = 1;
    public float nextFrameXDifference;
    public float nextFrameYDifference;
    movingType currentMovingType;
    enum movingType {Straight, Curve}

    Animation<TextureRegion> shootAnimation;
    int shootAnimationFrameNum;
    int deathAnimationFrameNum;
    float stateTime;

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

    public void initializeAnimation(Texture shootAnimationSheet, int shootAnimationFrameNum) {
        TextureRegion[][] temp = TextureRegion.split(shootAnimationSheet, shootAnimationSheet.getWidth() / shootAnimationFrameNum, shootAnimationSheet.getHeight());
        TextureRegion shootFrames[] = new TextureRegion[shootAnimationFrameNum];
        System.arraycopy(temp[0], 0, shootFrames, 0, shootAnimationFrameNum);
        shootAnimation = new Animation<>(0.1f, shootFrames);
        this.shootAnimationFrameNum = shootAnimationFrameNum;
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
        if (health <= 0) {
            isDead = true;
            isInvisible = true;
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

}
