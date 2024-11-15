package com.google.android.material.floatingactionbutton;

import android.animation.Animator;

/* loaded from: classes.dex */
class AnimatorTracker {
    private Animator currentAnimator;

    public void onNextAnimationStart(Animator animator) {
        cancelCurrent();
        this.currentAnimator = animator;
    }

    public void cancelCurrent() {
        if (this.currentAnimator != null) {
            this.currentAnimator.cancel();
        }
    }

    public void clear() {
        this.currentAnimator = null;
    }
}
