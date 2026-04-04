package io.github.gucksus.simplefirstgame.tools;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

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

    public ScrollingBackground(float worldHeight) {
        backgroundAnimationSheet = new Texture("background_animation_sheet3.png");
        TextureRegion[][] tmp = TextureRegion.split(backgroundAnimationSheet, backgroundAnimationSheet.getWidth() / FRAME_NUM, backgroundAnimationSheet.getHeight());
        TextureRegion[] backgroundTileAnimationFrames = new TextureRegion[FRAME_NUM];
        System.arraycopy(tmp[0], 0, backgroundTileAnimationFrames, 0, FRAME_NUM);
        backgroundAnimation = new Animation<>(frameDuration, backgroundTileAnimationFrames);
    }

    void checkForAnimationTrigger() {
        double randomNum = Math.random();
        if (!isInAnimation && randomNum <= animationProb) {
            isInAnimation = true;
            System.out.println(1);
        }
    }

    public void backgroundUpdate(float delta, float worldHeight) {
        processOffsetY (delta, worldHeight);
        checkForAnimationTrigger();
    }

    void processOffsetY (float delta, float worldHeight) {
        offsetY -= speed * delta;
        if (-offsetY + worldHeight < worldHeight) {
            offsetY -= height;
        }
    }

    public void draw(Batch batch, float delta) {
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
