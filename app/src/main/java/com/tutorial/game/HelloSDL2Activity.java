package com.tutorial.game;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import org.libsdl.app.SDLActivity;

public class HelloSDL2Activity extends SDLActivity
{
    private static Vibrator m_vibrator = null;

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
}
