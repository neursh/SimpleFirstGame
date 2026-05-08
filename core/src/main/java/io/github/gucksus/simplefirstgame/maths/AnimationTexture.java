package io.github.gucksus.simplefirstgame.maths;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.gucksus.simplefirstgame.animation.CallableMath;

public class AnimationTexture implements CallableMath<TextureRegion> {
    Animation<TextureRegion> animation;

    public AnimationTexture(Animation<TextureRegion> animation) {
        this.animation = animation;
    }

    @Override
    public TextureRegion get(float progess) {
        return animation.getKeyFrame(animation.getAnimationDuration() * progess);
    }
}
