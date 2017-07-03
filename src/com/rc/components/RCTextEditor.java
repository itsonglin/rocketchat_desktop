package com.rc.components;

import com.rc.components.message.FileEditorThumbnail;
import com.rc.utils.ClipboardUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Created by song on 03/07/2017.
 */
public class RCTextEditor extends JTextPane
{
    @Override
    public void paste()
    {
        Object data = ClipboardUtil.paste();
        if (data instanceof String)
        {
            this.replaceSelection((String) data);
        }
        else if (data instanceof ImageIcon)
        {
            ImageIcon icon = (ImageIcon) data;
            adjustAndinsertIcon(icon);
        }
        else if (data instanceof java.util.List)
        {
            java.util.List<Object> list = (java.util.List<Object>) data;
            for (Object obj : list)
            {
                // 图像
                if (obj instanceof ImageIcon)
                {
                    adjustAndinsertIcon((ImageIcon) obj);
                }
                // 文件
                else if (obj instanceof String)
                {
                    FileEditorThumbnail thumbnail = new FileEditorThumbnail((String) obj);
                    this.insertComponent(thumbnail);
                }
            }
        }
    }

    /**
     * 插入图片到编辑框，并自动调整图片大小
     *
     * @param icon
     */
    private void adjustAndinsertIcon(ImageIcon icon)
    {
        int iconWidth = icon.getIconWidth();
        int iconHeight = icon.getIconHeight();
        float scale = iconWidth * 1.0F / iconHeight;
        boolean needToScale = false;
        int max = 100;
        if (iconWidth >= iconHeight && iconWidth > max)
        {
            iconWidth = max;
            iconHeight = (int) (iconWidth / scale);
            needToScale = true;
        }
        else if (iconHeight >= iconWidth && iconHeight > max)
        {
            iconHeight = max;
            iconWidth = (int) (iconHeight * scale);
            needToScale = true;
        }

        if (needToScale)
        {
            ImageIcon scaledIcon = new ImageIcon(icon.getImage().getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH));
            scaledIcon.setDescription(icon.getDescription());
            this.insertIcon(scaledIcon);
        }
        else
        {
            this.insertIcon(icon);
        }
    }
}