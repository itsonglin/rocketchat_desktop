package tasks;

import com.rc.utils.HttpUtil;
import org.json.JSONObject;

/**
 * Created by song on 08/06/2017.
 */
public class HttpGetTask extends HttpTask
{
    @Override
    public void execute(String url)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                String ret = HttpUtil.get(url, requestParams, requestParams);
                JSONObject retJson = new JSONObject(ret);
                if (listener != null)
                {
                    listener.onResult(retJson);
                }
            }
        }).start();

    }
}
