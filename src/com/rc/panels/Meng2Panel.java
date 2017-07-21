package com.rc.panels;

import com.rc.components.Colors;
import com.rc.listener.ExpressionListener;
import com.rc.utils.IconUtil;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;

import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by song on 04/07/2017.
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
                JPanel panel = (JPanel) e.getSource();
                panel.setBackground(Colors.SCROLL_BAR_TRACK_LIGHT);
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                JPanel panel = (JPanel) e.getSource();
                panel.setBackground(Colors.WINDOW_BACKGROUND);
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

            String name = codeList[i].substring(2, codeList[i].length() - 2);
            File imgFile = new File(getClass().getResource(iconPath + name + ".gif").getPath());
            try
            {
                ImageInputStream imageInputStream = new FileImageInputStream(imgFile);
                ImageIcon icon = new ImageIcon(getFirstFrameInGif(imageInputStream).getScaledInstance(50, 50, Image.SCALE_SMOOTH));

                JPanel panel = new ExpressionItem(codeList[i], icon, name, new Dimension(60, 60), new Dimension(50, 50));
                panel.addMouseListener(listener);

                add(panel);

            }
            catch (IOException e)
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
    private BufferedImage getFirstFrameInGif(ImageInputStream src)
    {
        try
        {

            ImageReaderSpi readerSpi = new GIFImageReaderSpi();
            GIFImageReader gifReader = (GIFImageReader) readerSpi.createReaderInstance();
            gifReader.setInput(src);
            return gifReader.read(0);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
