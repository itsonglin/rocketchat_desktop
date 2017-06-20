package com.rc.utils;

import java.awt.*;
import java.awt.datatransfer.*;
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
