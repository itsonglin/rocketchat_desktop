package com.rc.app;


import com.rc.components.message.JIMSendTextPane;
import com.rc.forms.ImageViewerFrame;
import com.rc.panels.EmojiPanel;
import com.rc.utils.AvatarUtil;
import com.rc.utils.EmojiUtil;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.vdurmont.emoji.EmojiParser;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by song on 14/06/2017.
 */

class Test
{
    public static void main(String[] args) throws IOException, FontFormatException
    {

        String aa = "\uD83D\uDE42";
        String str = ":smile:";
        String out = EmojiParser.parseToUnicode(str);
        System.out.println(out);


        /*File file = new File("F:\\emoji");
        File[] imgs = file.listFiles();
        for (File img : imgs)
        {
            BufferedImage bufferedImage = ImageIO.read(img);
            Image scaledImage = bufferedImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);

            BufferedImage outImage = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);

            // 获取Graphics2D
            Graphics2D g2d = outImage.createGraphics();

            g2d.drawImage(scaledImage, 0, 0, null);

            ImageIO.write(outImage, "png", new File("F:\\emoji2\\" + img.getName()));
        }*/
    }
}

