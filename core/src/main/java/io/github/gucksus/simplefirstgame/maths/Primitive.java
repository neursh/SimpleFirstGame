package io.github.gucksus.simplefirstgame.maths;

import io.github.gucksus.simplefirstgame.animation.CallableMath;

public class Primitive implements CallableMath<Float> {
    public Float get(float progress) {
        return progress;
    }
}
