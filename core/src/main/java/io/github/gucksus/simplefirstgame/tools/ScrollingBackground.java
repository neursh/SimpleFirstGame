package io.github.gucksus.simplefirstgame.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;

public class ScrollingBackground {
    Texture backgroundAnimationSheet;
    float speed = 2f;
    Animation<TextureRegion> backgroundAnimation;
    float stateTime;
    final int FRAME_NUM = 6;
    final float frameDuration = 0.3f;
    boolean isInAnimation;
    float offsetY;
    float width = 2;
    float height = 2;
    float animationProb = .001f;
    float worldHeight;
    SpriteBatch batch;

    public ScrollingBackground(float worldHeight, SpriteBatch batch) {
        backgroundAnimationSheet = new Texture("Background/background_animation_sheet3.png");
        TextureRegion[][] tmp = TextureRegion.split(backgroundAnimationSheet, backgroundAnimationSheet.getWidth() / FRAME_NUM, backgroundAnimationSheet.getHeight());
        TextureRegion[] backgroundTileAnimationFrames = new TextureRegion[FRAME_NUM];
        System.arraycopy(tmp[0], 0, backgroundTileAnimationFrames, 0, FRAME_NUM);
        backgroundAnimation = new Animation<>(frameDuration, backgroundTileAnimationFrames);
        this.worldHeight = worldHeight;
        this.batch = batch;
    }

    void checkForAnimationTrigger() {
        double randomNum = Math.random();
        if (!isInAnimation && randomNum <= animationProb) {
            isInAnimation = true;
            System.out.println("BG animation triggered");
        }
    }

    public void backgroundUpdate() {
        processOffsetY ();
        checkForAnimationTrigger();
    }

    void processOffsetY () {
        float delta = Gdx.graphics.getDeltaTime();
        offsetY -= speed * delta;
        if (-offsetY + worldHeight < worldHeight) {
            offsetY -= height;
        }
    }

    public void draw() {
        float delta = Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = backgroundAnimation.getKeyFrame(stateTime, false);
        if (isInAnimation) {
            stateTime += delta;
            currentFrame = backgroundAnimation.getKeyFrame(stateTime, false);
            if (backgroundAnimation.getKeyFrameIndex(stateTime) == FRAME_NUM - 1) {
                stateTime = 0;
                isInAnimation = false;
            }
        }
        for (float i = 0; i < 8; i += width) {
            for (float j = offsetY; j < -offsetY + 11; j += height) {
                batch.draw(currentFrame, i, j, width, height);
            }
        }
    }

    public void dispose() {
        backgroundAnimationSheet.dispose();
    }
}
