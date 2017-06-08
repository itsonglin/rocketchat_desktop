package tasks;

import org.json.JSONObject;

/**
 * Created by song on 08/06/2017.
 */
public interface HttpResponseListener
{
    void onResult(JSONObject ret);
}
