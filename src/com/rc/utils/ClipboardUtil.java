package com.rc.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by song on 20/06/2017.
 */
public class ClipboardUtil
{
    private static Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    public static void copyString(String content)
    {
        if (content != null)
        {
            Transferable tText = new StringSelection(content);
            clipboard.setContents(tText, null);
        }
    }

    public static void copyImage(String path)
    {
        try
        {
            Image image = ImageIO.read(new File(path));
            clipboard.setContents(new ImageTransferable(image), null);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static String pasteString()
    {
        Transferable transferable = clipboard.getContents(null);

        if (transferable != null)
        {

            if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor))
            {
                try
                {
                    return (String) transferable.getTransferData(DataFlavor.stringFlavor);
                }
                catch (UnsupportedFlavorException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}

class ImageTransferable implements Transferable
{
    private Image image;

    public ImageTransferable(Image image)
    {

        this.image = image;
    }
    public DataFlavor[] getTransferDataFlavors()
    {
        return new DataFlavor[]{DataFlavor.imageFlavor};
    }

    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
        return DataFlavor.imageFlavor.equals(flavor);
    }

    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException
    {
        if (isDataFlavorSupported(flavor))
            return image;
        throw new UnsupportedFlavorException(flavor);
    }
}