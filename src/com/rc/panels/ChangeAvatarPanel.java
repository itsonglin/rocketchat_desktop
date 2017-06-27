package com.rc.panels;

import com.rc.components.Colors;
import com.rc.components.RCButton;
import com.rc.components.VerticalFlowLayout;
import com.rc.frames.MainFrame;
import com.rc.utils.AvatarUtil;
import com.rc.utils.IconUtil;
import com.rc.websocket.WebSocketClient;
import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

/**
 * 修改头像面板
 * <p>
 * Created by song on 23/06/2017.
 */
public class ChangeAvatarPanel extends JPanel
{
    private static ChangeAvatarPanel context;
    private JLabel imageLabel;
    private RCButton okButton;
    private JPanel contentPanel;
    private File selectedFile;
    private JLabel statusLabel;

    public ChangeAvatarPanel()
    {
        context = this;

        initComponents();
        initView();
        setListener();
    }


    private void initComponents()
    {
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(200, 200));
        imageLabel.setIcon(new ImageIcon(AvatarUtil.createOrLoadUserAvatar("song").getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
        imageLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        imageLabel.setToolTipText("点击上传本地头像");

        okButton = new RCButton("使用头像", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);
        okButton.setPreferredSize(new Dimension(100, 35));

        statusLabel = new JLabel();
        statusLabel.setText("头像应用成功");
        statusLabel.setForeground(Colors.FONT_GRAY_DARKER);
        statusLabel.setIcon(IconUtil.getIcon(this, "/image/check.png"));
        statusLabel.setVisible(false);

        contentPanel = new JPanel();
    }

    private void initView()
    {
        contentPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 10, true, false));
        contentPanel.add(imageLabel);
        contentPanel.add(okButton);
        contentPanel.add(statusLabel);


        add(contentPanel);
    }

    private void setListener()
    {
        imageLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                chooseImage();
            }
        });

        okButton.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (okButton.isEnabled())
                {
                    okButton.setEnabled(false);

                    if (selectedFile == null)
                    {
                        JOptionPane.showMessageDialog(MainFrame.getContext(), "请选择图像文件", "选择图片", JOptionPane.WARNING_MESSAGE);
                        okButton.setEnabled(true);
                        return;
                    }

                    okButton.setIcon(IconUtil.getIcon(this, "/image/sending.gif"));
                    okButton.setText("应用中...");
                    WebSocketClient.getContext().setAvatar(base64EncodeImage(selectedFile.getAbsolutePath()));
                }

                super.mouseClicked(e);
            }
        });
    }

    private void chooseImage()
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("请选择图片");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileNameExtensionFilter("图像", "jpg", "jpeg", "png"));

        fileChooser.showDialog(MainFrame.getContext(), "上传");
        selectedFile = fileChooser.getSelectedFile();
        if (selectedFile != null)
        {
            String extension = selectedFile.getName();
            if (!extension.endsWith(".jpg") && !extension.endsWith(".jpeg") && !extension.endsWith(".png"))
            {
                JOptionPane.showMessageDialog(MainFrame.getContext(), "请选择图像文件", "文件类型不正确", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try
            {
                ByteArrayOutputStream out = cutImage(selectedFile.getAbsolutePath());
                byte[] data = out.toByteArray();
                ImageIcon imageIcon = new ImageIcon(data);
                imageIcon.setImage(imageIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH));
                imageLabel.setIcon(imageIcon);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
    }

    /**
     * 图片裁剪
     *
     * @param src
     * @return
     * @throws IOException
     */
    public static ByteArrayOutputStream cutImage(String src) throws IOException
    {
        String extension = src.substring(src.lastIndexOf(".") + 1);
        /*if (extension.equals("jpeg"))
        {
            extension = "jpg";
        }*/
        Iterator iterator = ImageIO.getImageReadersByFormatName(extension);
        ImageReader reader = (ImageReader) iterator.next();
        InputStream in = new FileInputStream(src);
        ImageInputStream iis = ImageIO.createImageInputStream(in);
        reader.setInput(iis, true);
        ImageReadParam param = reader.getDefaultReadParam();

        int width = reader.getWidth(0);
        int height = reader.getHeight(0);

        int x = 0;
        int y = 0;

        if (width >= height)
        {
            width = height;
            x = (reader.getWidth(0) - width) / 2;
        }
        else
        {
            height = width;
            y = (reader.getHeight(0) - height) / 2;
        }


        Rectangle rect = new Rectangle(x, y, width, height);

        param.setSourceRegion(rect);
        BufferedImage bi = reader.read(0, param);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        ImageIO.write(bi, extension, byteArrayOutputStream);

        return byteArrayOutputStream;
    }

    /**
     * 对图片进行base64编码
     *
     * @param imgFile
     * @return
     */
    public static String base64EncodeImage(String imgFile)
    {
        InputStream inputStream = null;
        byte[] data = null;
        try
        {
            inputStream = new FileInputStream(imgFile);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return new String(Base64.encodeBase64(data));
    }

    public void restoreOKButton()
    {
        okButton.setText("使用头像");
        okButton.setIcon(null);
        okButton.setEnabled(true);
        selectedFile = null;
    }

    public void showSuccessMessage()
    {
        statusLabel.setVisible(true);
    }

    public static ChangeAvatarPanel getContext()
    {
        return context;
    }
}
