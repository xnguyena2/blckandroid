package paulo.nguyenphong.blockchain;

import android.util.Log;


import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import paulo.nguyenphong.callbackInterface.WebsocketCallBack;

/**
 * Created by Nguyen Phong on 11/13/2016.
 */

public class MyWebSocketClient extends WebSocketClient {
    String TAG = "EYEVERTIFY_webSocket";
    WebsocketCallBack callBack;

    public MyWebSocketClient(URI serverURI, WebsocketCallBack callBack) {
        super(serverURI);
        this.callBack = callBack;
    }

    public void changeCalBack(WebsocketCallBack callBack){
        this.callBack = callBack;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d(TAG, "Connected");
        callBack.onOpen();
    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG,"message recived:"+message);
        callBack.onRecivedMsg(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG, "Closed " + reason);
        callBack.onClose(code, reason, remote);
    }

    @Override
    public void onError(Exception ex) {
        Log.d(TAG, "error: "+ex.toString());
        callBack.onError(ex);
    }
}
