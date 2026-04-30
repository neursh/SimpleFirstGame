package io.github.gucksus.simplefirstgame.maths;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.gucksus.simplefirstgame.animation.CallableMath;

public class PathLerp implements CallableMath<Vector2> {
    private final Array<Vector2> path = new Array<>();
    private final Array<Float> segmentLengths = new Array<>();
    private final float totalLength;

    public PathLerp(Array<Vector2> points) {
        for (Vector2 point : points) {
            this.path.add(point.cpy());
        }

        float total = 0;
        for (int i = 0; i < this.path.size - 1; i++) {
            float len = (float) Math.hypot(this.path.get(i + 1).x - this.path.get(i).x,
                    this.path.get(i + 1).y - this.path.get(i).y);
            segmentLengths.add(len);
            total += len;
        }
        this.totalLength = total;
    }

    @Override
    public Vector2 get(float progress) {
        if (progress <= 0)
            return this.path.first();
        if (progress >= 1)
            return this.path.get(this.path.size - 1);

        float target = progress * totalLength;
        float accumulated = 0;

        for (int i = 0; i < this.segmentLengths.size; i++) {
            float segLen = this.segmentLengths.get(i);
            if (accumulated + segLen >= target) {
                float localT = (target - accumulated) / segLen;
                Vector2 a = this.path.get(i), b = this.path.get(i + 1);
                return new Vector2(a.x + (b.x - a.x) * localT, a.y + (b.y - a.y) * localT);
            }
            accumulated += segLen;
        }

        return this.path.get(this.path.size - 1);
    }

}
