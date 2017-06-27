package com.rc.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

    public static void copyFile(String path)
    {
        try
        {
            File file = new File(path);
            //clipboard.setContents(new FileTransferable(file), null);
            Transferable contents = new Transferable() {
                DataFlavor[] dataFlavors = new DataFlavor[] { DataFlavor.javaFileListFlavor };

                @Override
                public Object getTransferData(DataFlavor flavor)
                        throws UnsupportedFlavorException, IOException {
                    ArrayList<File> files = new ArrayList<>();
                    files.add(file);
                    return files;
                }

                @Override
                public DataFlavor[] getTransferDataFlavors() {
                    return dataFlavors;
                }

                @Override
                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    for (int i = 0; i < dataFlavors.length; i++) {
                        if (dataFlavors[i].equals(flavor)) {
                            return true;
                        }
                    }
                    return false;
                }
            };

            clipboard.setContents(contents, null);

        }
        catch (Exception e)
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

class FileTransferable implements Transferable
{
    private File file;

    public FileTransferable(File file)
    {
        this.file = file;
    }
    DataFlavor[] dataFlavors = new DataFlavor[]{DataFlavor.javaFileListFlavor};

    @Override
    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException
    {
        return new ArrayList<>().add(file);
    }

    @Override
    public DataFlavor[] getTransferDataFlavors()
    {
        return dataFlavors;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
        for (int i = 0; i < dataFlavors.length; i++)
        {
            if (dataFlavors[i].equals(flavor))
            {
                return true;
            }
        }
        return false;
    }
}