package ru.limedev.endecrypter.core.impl;

/*
 * MIT License
 *
 * Copyright (c) 2020 Tim Meleshko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import com.github.amlcurran.showcaseview.ShowcaseDrawer;

import ru.limedev.endecrypter.R;

public class ShowcaseViewRectangle implements ShowcaseDrawer {

    private final float width;
    private final float height;
    private final Paint eraserPaint;
    private final Paint basicPaint;
    private final int eraseColour;
    private final RectF renderRect;
    private final boolean fullX;

    public ShowcaseViewRectangle(@NonNull Resources resources, float width, float height, boolean fullX) {
        this.width = width;
        this.height = height;
        this.fullX = fullX;
        PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY);
        eraserPaint = new Paint();
        eraserPaint.setXfermode(xfermode);
        eraseColour = resources.getColor(R.color.custom_showcase_bg);
        basicPaint = new Paint();
        renderRect = new RectF();
    }

    @Override
    public void setShowcaseColour(int color) {
        eraserPaint.setColor(color);
    }

    @Override
    public void drawShowcase(Bitmap buffer, float x, float y, float scaleMultiplier) {
        Canvas bufferCanvas = new Canvas(buffer);
        if (fullX) {
            renderRect.left = 0;
            renderRect.right = width;
        } else {
            renderRect.left = x - width / 2f;
            renderRect.right = x + width / 2f;
        }
        renderRect.top = y - height / 2f;
        renderRect.bottom = y + height / 2f;
        bufferCanvas.drawRect(renderRect, eraserPaint);
    }

    @Override
    public int getShowcaseWidth() {
        return (int) width;
    }

    @Override
    public int getShowcaseHeight() {
        return (int) height;
    }

    @Override
    public float getBlockedRadius() {
        return width;
    }

    @Override
    public void setBackgroundColour(int backgroundColor) {}

    @Override
    public void erase(Bitmap bitmapBuffer) {
        bitmapBuffer.eraseColor(eraseColour);
    }

    @Override
    public void drawToCanvas(Canvas canvas, Bitmap bitmapBuffer) {
        canvas.drawBitmap(bitmapBuffer, 0, 0, basicPaint);
    }
}
