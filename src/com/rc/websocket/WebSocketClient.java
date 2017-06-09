package com.rc.websocket;

import com.neovisionaries.ws.client.*;
import com.rc.websocket.handler.WebSocketListenerAdapter;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

/**
 * Created by song on 09/06/2017.
 */
public class WebSocketClient
{
    String hostname = "https://rc.shls-leasing.com";
    private WebSocket webSocket;
    public String ConnectionStatus;
    private int LAST_RECONNECT_TIME;

    public WebSocketClient()
    {
        prepareWebSocket();
        webSocket.connectAsynchronously();
    }

    private void prepareWebSocket()
    {
        WebSocketFactory webSocketFactory = new WebSocketFactory();
        // Create a custom SSL context.
        SSLContext context = null;
        try
        {
            context = NaiveSSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        webSocketFactory.setSSLContext(context);
        String url = hostname + "/websocket";

        try
        {
            webSocket = null;
            webSocket = webSocketFactory.createSocket(url)
                    .setAutoFlush(true)
                    .addListener(new WebSocketListenerAdapter()
                    {
                        @Override
                        public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception
                        {
                            System.out.println("+++++++onStateChanged: " + newState.toString());
                        }

                        @Override
                        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception
                        {
                            System.out.println("+++++++onConnected: ");

                            //subscriptionHelper.sendConnectRequest();
                        }


                        @Override
                        public void onConnectError(WebSocket websocket, WebSocketException cause) throws Exception
                        {
                            ConnectionStatus = "disconnected";
                            LAST_RECONNECT_TIME = 0;
                            System.out.println("+++++++onConnectError: " + cause.getMessage());
                           /* if (cause.getMessage().startsWith("Failed to connect to") && !networkDisabled)
                            {
                                Log.e("restartApplication", "restartApplication");
                                restartApplication();
                            }
                            else if (!networkDisabled)
                            {
                                sendBroadcast(MainFrameActivity.WEBSOCKET_TO_ACTIVITY_ACTION, EVENT_CONNECT_ERROR);
                            }
                            else if (networkDisabled)
                            {
                                sendBroadcast(MainFrameActivity.WEBSOCKET_TO_ACTIVITY_ACTION, EVENT_NETWORK_DISABLED);
                            }*/
                        }

                        @Override
                        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception
                        {
                            System.out.println("+++++++onDisconnected");
                            ConnectionStatus = "disconnected";

                            /*if (!networkDisabled)
                            {
                                System.out.println("==========重新连接。。。。");
                                sendBroadcast(MainFrameActivity.WEBSOCKET_TO_ACTIVITY_ACTION, EVENT_RECONNECTING);
                                startWebSocketClient();
                            }
                            else
                            {
                                Log.e("onDisconnected", "连接已断开，网络不可用，放弃重连");
                            }*/
                        }

                        @Override
                        public void onTextMessage(WebSocket websocket, String text) throws Exception
                        {
                           // handleMessage(text);
                            System.out.println(text);
                        }
                    });


        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
