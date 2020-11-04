package com.fenix.app.util;

import android.widget.TextView;

public final class TextViewUtil {

    public static void ScrollToBottom(TextView textView) {
        final int scrollAmount = textView.getLayout().getLineTop(textView.getLineCount()) - textView.getHeight();
        if (scrollAmount > 0)
            textView.scrollTo(0, scrollAmount);
        else
            textView.scrollTo(0, 0);
    }

}
