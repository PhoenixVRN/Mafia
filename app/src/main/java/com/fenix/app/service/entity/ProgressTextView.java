package com.fenix.app.service.entity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;


/**
 * Реализуем прогресс бар
 */
public class ProgressTextView extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {
    // Максимальное значение шкалы
    public int mMaxValue;

    // Конструкторы
    public ProgressTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ProgressTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressTextView(Context context) {
        super(context);
    }

    // Установка максимального значения
    public void setMaxValue(int maxValue) {
        //     mMaxValue = maxValue;
    }

    // Установка значения
    public synchronized void setValue(int value, int mMaxValue) {
        // Установка новой надписи
        this.setText(String.valueOf(value + "/" + mMaxValue));
        this.setTextSize(12);
        this.setTextColor(Color.BLACK);

        // Drawable, отвечающий за фон
        LayerDrawable background = (LayerDrawable) this.getBackground();

        // Достаём Clip, отвечающий за шкалу, по индексу 1
        ClipDrawable barValue = (ClipDrawable) background.getDrawable(1);

        // Устанавливаем уровень шкалы
        int newClipLevel = (int) (value * 10000 / mMaxValue);
        barValue.setLevel(newClipLevel);

        // Уведомляем об изменении Drawable
        drawableStateChanged();
    }

    public synchronized void setValueBut(int value, int mMaxValue) {
        // Установка новой надписи
 /*       this.setText(String.valueOf(value + "/" + mMaxValue));
        this.setTextSize(12);
        this.setTextColor(Color.BLACK);*/

        // Drawable, отвечающий за фон
        LayerDrawable background = (LayerDrawable) this.getBackground();

        // Достаём Clip, отвечающий за шкалу, по индексу 1
        ClipDrawable barValue = (ClipDrawable) background.getDrawable(1);

        // Устанавливаем уровень шкалы
        int newClipLevel = (int) (value * 10000 / mMaxValue);
        barValue.setLevel(newClipLevel);

        // Уведомляем об изменении Drawable
        drawableStateChanged();
    }
}
