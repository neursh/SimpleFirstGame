package io.github.gucksus.simplefirstgame.maths;

import io.github.gucksus.simplefirstgame.animation.CallableMath;

import com.badlogic.gdx.math.Vector2;

public class CubicBezier implements CallableMath<Vector2> {
    private Vector2 p1;
    private Vector2 p2;
    private Vector2 p3;
    private Vector2 p4;

    public CubicBezier(Vector2 p1, Vector2 p2, Vector2 p3, Vector2 p4) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
    }

    @Override
    public Vector2 get(float progress) {
        if (progress < 0) {
            progress = 0;
            return p1;
        }
        if (progress > 1) {
            progress = 1;
            return p2;
        }

        float inverse = 1 - progress;
        float a = inverse * inverse * inverse;
        float b = 3 * inverse * inverse * progress;
        float c = 3 * inverse * progress * progress;
        float d = progress * progress * progress;

        return new Vector2(a * p1.x + b * p2.x + c * p3.x + d * p4.x,
                a * p1.y + b * p2.y + c * p3.y + d * p4.y);
    }
}
