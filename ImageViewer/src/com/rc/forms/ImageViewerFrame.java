package com.rc.forms;

import com.sun.javaws.IconUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Created by song on 2017/6/25.
 */
public class ImageViewerFrame extends JFrame
{
    private int minWidth;
    private int minHeight;

    private int maxWidth;
    private int maxHeight;

    private JLabel imageLabel;

    private String imagePath;
    private Toolkit tooKit;


    public ImageViewerFrame()
    {
        tooKit = Toolkit.getDefaultToolkit();

        initComponents();
        initView();
        initSize();

        imagePath = "F:\\test.jpg";
        //imagePath = "C:\\Users\\song\\Pictures\\user-cover.jpg";

        setImage();

        setListeners();

    }


    private void initSize()
    {
        // 窗口最小宽度、高度
        minWidth = 300;
        minHeight = 300;

        // 窗口最大宽度、高度
        Dimension screenSize = tooKit.getScreenSize();
        int screenSizeWidth = (int) screenSize.getWidth();
        int screenSizeHeight = (int) screenSize.getHeight();

        maxWidth = (int) (screenSizeWidth * 0.6);
        maxHeight = (int) (screenSizeHeight * 0.8);
    }

    private void setImage()
    {


        ImageIcon imageIcon = new ImageIcon(imagePath);

        int imageWidth = imageIcon.getIconWidth();
        int imageHeight = imageIcon.getIconHeight();
        float imageScale = imageWidth * 1.0F / imageHeight; // 图像宽高比

        int actualWidth = imageWidth;
        int actualHeight = imageHeight;

        boolean needScale = false; // 是否需要对图片进行缩放
        if (imageWidth >= imageHeight)
        {
            if (imageWidth > maxWidth)
            {
                actualWidth = maxWidth;
                actualHeight = (int) (actualWidth / imageScale);
                needScale = true;
            }
            else if (imageWidth < minWidth)
            {
                actualWidth = minWidth;
                actualHeight = (int) (actualWidth / imageScale);

            }
        }
        else
        {
            if (imageHeight > maxHeight)
            {
                actualHeight = maxHeight;
                actualWidth = (int) (actualHeight * imageScale);
                needScale = true;
            }
            else if (imageHeight < minHeight)
            {
                actualHeight = minHeight;
                actualWidth = (int) (actualHeight * imageScale);
            }
        }

        if (needScale)
        {
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(actualWidth, actualHeight, Image.SCALE_SMOOTH));
        }

        imageLabel.setIcon(imageIcon);

        this.setSize(new Dimension(actualWidth, actualHeight));
        this.setLocation((tooKit.getScreenSize().width - actualWidth) / 2,
                (tooKit.getScreenSize().height - actualHeight) / 2);
    }


    private void initComponents()
    {
        imageLabel = new JLabel();
        imageLabel.setBackground(new Color(240, 240, 240));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void initView()
    {
        add(imageLabel);
    }

    private void setListeners()
    {

    }
}
