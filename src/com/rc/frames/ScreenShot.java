package com.rc.frames;

import com.rc.components.Colors;
import com.rc.panels.ChatPanel;
import com.rc.utils.ClipboardUtil;
import com.rc.utils.IconUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenShot extends JFrame
{
    private int startX, startY, endX, endY;
    private BufferedImage image = null;
    private BufferedImage tempImage = null;
    private BufferedImage saveImage = null;
    private JDialog controlDialog = new JDialog();
    private int selectedWidth;
    private int selectedHeight;
    private int drawX;
    private int drawY;
    private boolean mouseDragged = false;
    private int maxWidth;
    private int maxHeight;
    boolean isShown = false;
    private boolean inSelectArea = false;

    public ScreenShot() throws AWTException
    {
        setUndecorated(true);
        setBackground(Colors.DARK);

        setOpacity(0); //初始时设置窗口为透明，防止窗口闪烁

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

        screenShot();
        initControlDialog();
        setListeners();
    }

    private void setListeners()
    {
        this.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                int x = e.getX();
                int y = e.getY();

                if (x >= drawX && x <= drawX + selectedWidth && y >= drawY && y <= drawY + selectedHeight)
                {
                    inSelectArea = true;
                    System.out.println("鼠标在选定区域");
                }
                else
                {
                    inSelectArea = false;
                }

                startX = e.getX();
                startY = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                if (mouseDragged)
                {
                    mouseDragged = false;

                    controlDialog.setBounds(drawX, drawY + selectedHeight, 200, 50);
                    controlDialog.setVisible(true);
                }
            }


            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getClickCount() >= 2)
                {
                    close();
                    ClipboardUtil.copyImage(saveImage);
                    ChatPanel.getContext().paste();
                }

                super.mouseClicked(e);
            }
        });

        //对于鼠标移动的监听
        this.addMouseMotionListener(new MouseMotionAdapter()
        {
            @Override
            public void mouseDragged(MouseEvent e)
            {
                mouseDragged = true;
                endX = e.getX();
                endY = e.getY();

                Image tempImage2 = createImage(ScreenShot.this.getWidth(), ScreenShot.this.getHeight());
                Graphics g = tempImage2.getGraphics();
                g.drawImage(tempImage, 0, 0, null);

                if (inSelectArea)
                {
                    int xDistance = e.getX() - startX;
                    int yDistance = e.getY() - startY;

                    System.out.println(xDistance);
                    drawX += xDistance;
                    drawY += yDistance;

                    // 保证不会越界
                    drawX = drawX < 0 ? 0 : drawX;
                    drawY = drawY < 0 ? 0 : drawY;
                    drawX = drawX + selectedWidth > maxWidth ? maxWidth - selectedWidth : drawX;
                    drawY = drawY + selectedHeight > maxHeight ? maxHeight - selectedHeight : drawY;

                    startX = e.getX();
                    startY = e.getY();
                }
                else
                {
                    drawX = Math.min(startX, endX);
                    drawY = Math.min(startY, endY);
                    selectedWidth = Math.abs(endX - startX) + 1;
                    selectedHeight = Math.abs(endY - startY) + 1;
                }



                g.setColor(Color.CYAN);
                g.drawRect(drawX - 1, drawY - 1, selectedWidth + 1, selectedHeight + 1);

                selectedWidth = selectedWidth > maxWidth ? maxWidth : selectedWidth;
                selectedHeight = selectedHeight > maxHeight ? maxHeight : selectedHeight;

                saveImage = image.getSubimage(drawX, drawY, selectedWidth, selectedHeight);
                g.drawImage(saveImage, drawX, drawY, null);

                ScreenShot.this.getGraphics().drawImage(tempImage2,
                        0, 0, ScreenShot.this);

                if (controlDialog.isVisible())
                {
                    controlDialog.setVisible(false);
                }

            }
        });

        KeyListener keyListener = new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    close();
                }
                else if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    close();
                    ClipboardUtil.copyImage(saveImage);
                    ChatPanel.getContext().paste();
                }
            }
        };

        addKeyListener(keyListener);
        controlDialog.addKeyListener(keyListener);
    }

    private void screenShot() throws AWTException
    {
        //获取默认屏幕设备
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice screen = environment.getDefaultScreenDevice();

        //获取屏幕尺寸
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds(0, 0, d.width, d.height);

        //获取屏幕截图
        Robot robot = new Robot(screen);
        image = robot.createScreenCapture(new Rectangle(0, 0, d.width, d.height));

        maxWidth = image.getWidth();
        maxHeight = image.getHeight();
    }

    private void initControlDialog()
    {
        controlDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        controlDialog.setAlwaysOnTop(true);
        controlDialog.setUndecorated(true);
        controlDialog.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));

        JLabel okLabel = new JLabel(IconUtil.getIcon(this, "/image/ok.png"));
        JLabel cancelLabel = new JLabel(IconUtil.getIcon(this, "/image/cancel.png"));
        JLabel downloadLabel = new JLabel(IconUtil.getIcon(this, "/image/download.png"));
        Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

        downloadLabel.setHorizontalAlignment(SwingConstants.CENTER);
        downloadLabel.setCursor(handCursor);
        downloadLabel.setToolTipText("保存截图");

        okLabel.setHorizontalAlignment(SwingConstants.CENTER);
        okLabel.setCursor(handCursor);
        okLabel.setToolTipText("确定");

        cancelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cancelLabel.setCursor(handCursor);
        cancelLabel.setToolTipText("取消");


        downloadLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                close();
                try
                {
                    saveImage();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
                super.mouseClicked(e);
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                downloadLabel.setIcon(IconUtil.getIcon(this, "/image/download_active.png"));
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                downloadLabel.setIcon(IconUtil.getIcon(this, "/image/download.png"));
                super.mouseExited(e);
            }
        });

        okLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                close();
                ClipboardUtil.copyImage(saveImage);
                ChatPanel.getContext().paste();
                super.mouseClicked(e);
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                okLabel.setIcon(IconUtil.getIcon(this, "/image/ok_active.png"));
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                okLabel.setIcon(IconUtil.getIcon(this, "/image/ok.png"));
                super.mouseExited(e);
            }
        });
        cancelLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
               close();
                super.mouseClicked(e);
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                cancelLabel.setIcon(IconUtil.getIcon(this, "/image/cancel_active.png"));
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                cancelLabel.setIcon(IconUtil.getIcon(this, "/image/cancel.png"));
                super.mouseExited(e);
            }
        });

        controlDialog.add(okLabel);
        controlDialog.add(cancelLabel);
        controlDialog.add(downloadLabel);
    }

    @Override
    public void paint(Graphics g)
    {
        RescaleOp ro = new RescaleOp(0.6f, 0, null);
        tempImage = ro.filter(image, null);
        g.drawImage(tempImage, 0, 0, this);

        if (!isShown)
        {
            setOpacity(1);
            isShown = true;
        }
    }

    //保存图像到文件
    public void saveImage() throws IOException
    {
        JFileChooser jfc = new JFileChooser();
        jfc.setDialogTitle("保存");

        //文件过滤器，用户过滤可选择的文件
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG", "png");
        jfc.setFileFilter(filter);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddHHmmss");
        String filename = "Helichat_screen_shot_" + sdf.format(new Date());

        File filePath = FileSystemView.getFileSystemView().getHomeDirectory();
        File defaultFile = new File(filePath + File.separator + filename + ".png");
        jfc.setSelectedFile(defaultFile);

        int flag = jfc.showSaveDialog(this);
        if (flag == JFileChooser.APPROVE_OPTION)
        {
            File file = jfc.getSelectedFile();
            String path = file.getPath();
            if (!(path.endsWith(".png") || path.endsWith("PNG")))
            {
                path += ".png";
            }
            //写入文件
            ImageIO.write(saveImage, "png", new File(path));
        }
    }


    private void close()
    {
        controlDialog.setVisible(false);
        setVisible(false);
        dispose();
    }

}
