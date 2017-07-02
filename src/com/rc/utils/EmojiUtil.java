package com.rc.utils;

import javax.swing.*;

/**
 * Created by song on 2017/7/1.
 */
public class EmojiUtil
{

    /**
     * 获取Emoji表情
     *
     * @param code emoji代码，形式如 {@code :dog:}
     * @return
     */
    public static ImageIcon getEmoji(Object context, String code)
    {
        String iconPath = "/emoji/" + code.subSequence(1, code.length() - 1) + ".png";
        return new ImageIcon(context.getClass().getResource(iconPath));
    }

    /**
     * 判断给定的emoji代码是否可识别
     * @param code
     * @return
     */
    public static boolean isRecognizableEmoji(Object context, String code)
    {
        return getEmoji(context, code) != null;
    }
}
