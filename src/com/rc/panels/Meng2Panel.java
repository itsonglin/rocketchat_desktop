package com.rc.panels;

import com.rc.components.Colors;
import com.rc.listener.ExpressionListener;
import com.rc.utils.IconUtil;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;

import javax.imageio.ImageIO;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.URISyntaxException;

/**
 * Created by song on 04/07/2017.
 * 萌二表情选择列表
 */
public class Meng2Panel extends JPanel
{
    private ExpressionListener expressionListener;
    private JPopupMenu parentPopup;

    public Meng2Panel()
    {
        initComponents();
        initView();
        initData();

    }

    private void initData()
    {
        MouseListener listener = new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                ExpressionItem panel = (ExpressionItem) e.getSource();
                ImageIcon icon = IconUtil.getIcon(this, "/expression/meng2/" + panel.getDisplayName() + ".gif", 50,50, Image.SCALE_FAST);
                panel.setImage(icon);

                panel.setBackground(Colors.SCROLL_BAR_TRACK_LIGHT);
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                ExpressionItem panel = (ExpressionItem) e.getSource();
                panel.setBackground(Colors.WINDOW_BACKGROUND);

                String iconPath = "/expression/meng2/";
                InputStream inputStream = getClass().getResourceAsStream(iconPath + panel.getDisplayName() + ".gif");
                ImageIcon icon = new ImageIcon(getFirstFrameInGif(inputStream).getScaledInstance(50, 50, Image.SCALE_SMOOTH));
                panel.setImage(icon);

                super.mouseExited(e);
            }

            @Override
            public void mouseClicked(MouseEvent e)
            {
                ExpressionItem panel = (ExpressionItem) e.getSource();
                panel.setBackground(Colors.WINDOW_BACKGROUND);

                if (expressionListener != null)
                {
                    expressionListener.onSelected(panel.getCode());
                    if (parentPopup != null)
                    {
                        parentPopup.setVisible(false);
                    }

                }
                super.mouseClicked(e);
            }
        };

        String[] codeList = new String[]{
                " :cry: ",
                " :grin: ",
                " :grinning: ",
                " :wave: ",
                " :hugging: ",
                " :kissing_closed_eyes: ",
                " :neutral_face: ",
                " :ok: ",
                " :question: ",
                " :relieved: ",
                " :sleeping: ",
                " :sleepy: ",
                " :smiley: ",
                " :stuck_out_tongue: ",
                " :stuck_out_tongue_winking_eye: ",
                " :unamused: ",
                " :clap: ",
                " :ok_hand: ",
                " :angry: ",
                " :no_good: ",
        };

        String iconPath = "/expression/meng2/";
        for (int i = 0; i < 20; i++)
        {
            if (i >= codeList.length)
            {
                add(new JPanel());
                return;
            }

            try
            {
                String name = codeList[i].substring(2, codeList[i].length() - 2);
                InputStream inputStream = getClass().getResourceAsStream(iconPath + name + ".gif");
                ImageIcon icon = new ImageIcon(getFirstFrameInGif(inputStream).getScaledInstance(50, 50, Image.SCALE_SMOOTH));

                JPanel panel = new ExpressionItem(codeList[i], icon, name, new Dimension(60, 60), new Dimension(50, 50));
                panel.addMouseListener(listener);

                add(panel);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void initComponents()
    {
        //setPreferredSize(new Dimension(400,300));
        this.setLayout(new GridLayout(4, 5, 3, 0));

    }

    private void initView()
    {

    }

    public void setExpressionListener(ExpressionListener expressionListener, JPopupMenu parentPopup)
    {
        this.expressionListener = expressionListener;
        this.parentPopup = parentPopup;
    }

    /**
     * 取GIF图片的第0帧
     *
     * @param src
     * @return
     */
    private BufferedImage getFirstFrameInGif(InputStream src)
    {
        try
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            byte buffer[] = new byte[1024];
            int len = 0;
            while ((len = src.read(buffer)) != -1)
            {
                bos.write(buffer, 0, len);
            }


            //截取第一张图
            BufferedImage bimage = ImageIO.read(new ByteArrayInputStream(bos.toByteArray(), 0, bos.size()));
            src.close();
            bos.close();
            return bimage;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
