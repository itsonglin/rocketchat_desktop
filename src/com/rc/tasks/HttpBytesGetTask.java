package com.rc.tasks;

import com.rc.utils.HttpUtil;

/**
 * Created by song on 2017/6/13.
 */
public class HttpBytesGetTask extends HttpTask
{
    @Override
    public void execute(String url)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                byte[] ret = HttpUtil.getBytes(url, headers, requestParams);
                if (listener != null)
                {
                    listener.onResult(ret);
                }
            }
        }).start();
    }
}
