package io.github.gucksus.simplefirstgame.animation;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import io.github.gucksus.simplefirstgame.helpers.AtomicFloat;

public class AnimSpec<T> {
    /**
     * This is a custom inteface that contains only one abstract method. Such method can be
     * customized on the fly and can take in different types.
     */
    @FunctionalInterface
    public interface AnimationCallback<T> {
        void execute(T value, float progress);
    }

    private final CallableMath<T> math;
    private final AnimationCallback<T> callback;

    private final AtomicBoolean finished = new AtomicBoolean(false);

    private final AtomicFloat delaySec = new AtomicFloat(0);
    private final AtomicFloat animateSec = new AtomicFloat(0);
    private final AtomicFloat waitSec = new AtomicFloat(0);
    private final AtomicInteger repeat = new AtomicInteger(0);

    private final AtomicFloat delayedSec = new AtomicFloat(0);
    private final AtomicFloat animatedSec = new AtomicFloat(0);
    private final AtomicFloat waitedSec = new AtomicFloat(0);
    private final AtomicInteger repeated = new AtomicInteger(0);

    private final AtomicFloat progress = new AtomicFloat(0);

    private final AtomicBoolean paused = new AtomicBoolean(false);

    /**
     * AnimSpec describes how an animation plays in seconds format. The class allows manual frame
     * handling, but best paired with AnimScheduler for simplicity.
     *
     * @param math A class that can be used to get value on each frame for your actions.
     * @param callback A function that calls every animate frame, this is where you would update
     *        your objects.
     * @param delay Delay in seconds before animate, doesn't trigger callback.
     * @param animate Fetch math value and callback in seconds.
     * @param wait Delay after animate, this value allows animation sequencing.
     * @param repeat How many repeat times for the animation.
     */
    public AnimSpec(CallableMath<T> math, AnimationCallback<T> callback, float delay, float animate,
            float wait, int repeat) {
        this.math = math;
        this.callback = callback;

        this.setDelay(delay);
        this.setAnimate(animate);
        this.setWait(wait);
        this.setRepeat(repeat);
    }

    /**
     * This method updates the animation.
     *
     * @param delta delta time.
     * @return If this animation is finished or not.
     */
    public boolean update(float delta) {
        // Check if the animation has repeated enough times.
        if (this.repeated.get() > this.repeat.get()) {
            this.finished.set(true);
            return false;
        }

        // Check for pausing.
        if (this.paused.get()) {
            return true;
        }

        // Check for and update delay.
        if (this.delayedSec.get() < this.delaySec.get()) {
            this.delayedSec.set(this.delayedSec.get() + delta);
            return true;
        }

        if (progress.get() == 0) {
            this.callback.execute(this.math.get(0), 0);
        }

        this.animatedSec.set(this.animatedSec.get() + delta);
        // Update progess of the animation through elapsed time.
        this.progress.set(Math.clamp((float) this.animatedSec.get() / this.animateSec.get(), 0, 1));

        // If this animation is still running, call for update method.
        if (this.waitedSec.get() == 0) {
            this.callback.execute(this.math.get(this.progress.get()), this.progress.get());
        }

        // If the animation is finished running, update wait time. If the wait time is enough, reset
        // the animation.
        if (progress.get() == 1) {
            if (this.waitedSec.get() < this.waitSec.get()) {
                this.waitedSec.set(this.waitedSec.get() + delta);
                return true;
            }

            this.repeated.incrementAndGet();
            this.reset(false);
        }

        return true;
    }

    public void pause() {
        this.paused.set(true);
    }

    public void resume() {
        this.paused.set(false);
    }

    public void reset(boolean destructive) {
        if (destructive) {
            this.repeated.set(0);
            this.paused.set(false);
            this.finished.set(false);
        }
        this.delayedSec.set(0);
        this.animatedSec.set(0);
        this.waitedSec.set(0);
        this.progress.set(0);
    }

    public void setDelay(float delay) {
        this.delaySec.set(delay);
    }

    public Duration getDelay() {
        return Duration.ofMillis((long) this.delaySec.get());
    }

    public void setAnimate(float animate) {
        this.animateSec.set(animate);
    }

    public Duration getDuration() {
        return Duration.ofMillis((long) this.animateSec.get());
    }

    public void setWait(float wait) {
        this.waitSec.set(wait);
    }

    public Duration getWait() {
        return Duration.ofMillis((long) this.waitSec.get());
    }

    public void setRepeat(int repeat) {
        this.repeat.set(repeat);
    }

    public long getRepeat() {
        return this.repeat.get();
    }

    public boolean isFinished() {
        return this.finished.get();
    }

    public boolean isPaused() {
        return this.paused.get();
    }
}
