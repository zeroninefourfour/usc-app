package com.tutorial.game;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;

import org.libsdl.app.SDLActivity;

import java.util.Collections;

public class HelloSDL2Activity extends SDLActivity
{
    private static Vibrator m_vibrator = null;

    // Android's gesture navigation swallows touches that start inside the
    // system gesture inset at the bottom edge, and OEM three-finger gestures
    // (screenshot etc.) take the whole stream. When that happens the view gets
    // an ACTION_CANCEL and every held button is released at once - the "touch
    // suddenly dropped in the middle of a hold" symptom.
    //
    // The touch pad layout puts the FX strips and the lower part of the BT
    // lanes exactly there, so ask the system to keep its hands off the bottom
    // band of the screen. The platform caps the exclusion at 200dp per edge,
    // 120dp is enough to cover the whole button console on any phone we care
    // about and still leaves the rest of the system gestures intact.
    private static final int GESTURE_EXCLUSION_HEIGHT_DP = 120;

    // Called from native code (Input.cpp touch handling) to give touch button
    // presses arcade-style haptic feedback. Safe to call from any thread; on
    // failure it just logs and does nothing.
    public static void hapticVibrate(int durationMs)
    {
        try
        {
            if (m_vibrator == null)
            {
                Context ctx = getContext();
                if (ctx == null)
                    return;
                m_vibrator = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
            }
            if (m_vibrator == null || !m_vibrator.hasVibrator())
                return;
            m_vibrator.vibrate(VibrationEffect.createOneShot(
                durationMs, VibrationEffect.DEFAULT_AMPLITUDE));
        }
        catch (Exception e)
        {
            Log.w("SDL", "hapticVibrate failed: " + e.toString());
        }
    }

    // Ask the system not to interpret swipes that start at the bottom of the
    // play area as its own gestures. Must be re-applied after layout changes
    // (rotation, immersive mode transitions), hence the onResume/onFocus hook.
    private void applyGestureExclusion()
    {
        if (Build.VERSION.SDK_INT < 29)
            return;
        try
        {
            View decor = getWindow() != null ? getWindow().getDecorView() : null;
            if (decor == null)
                return;
            final int width = decor.getWidth();
            final int height = decor.getHeight();
            if (width <= 0 || height <= 0)
                return;

            final int wanted = Math.round(
                GESTURE_EXCLUSION_HEIGHT_DP * getResources().getDisplayMetrics().density);
            final int band = Math.min(wanted, height);
            Rect bottom = new Rect(0, height - band, width, height);
            decor.setSystemGestureExclusionRects(Collections.singletonList(bottom));
        }
        catch (Exception e)
        {
            // Some OEM builds throw here (unknown to the platform); the game
            // still works, it just keeps the system gesture behaviour.
            Log.w("SDL", "applyGestureExclusion failed: " + e.toString());
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        applyGestureExclusion();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            applyGestureExclusion();
    }
}
