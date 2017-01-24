package paulo.nguyenphong.blockchain;


import java.net.URI;
import java.net.URISyntaxException;

import paulo.nguyenphong.callbackInterface.WebsocketCallBack;

/**
 * Created by Nguyen Phong on 11/7/2016.
 */

public class ConnectBlockchain {
    private MyWebSocketClient mWebSocketClient;
    String TAG = "EYEVERTIFY_webSocket";
    WebsocketCallBack callBack;

    public ConnectBlockchain(WebsocketCallBack callBack){
        this.callBack = callBack;
    }

    public void changeCallBack(WebsocketCallBack callBack){
        this.callBack = callBack;
        this.mWebSocketClient.changeCalBack(callBack);
    }

    public void sendMsg(String jsonString){
        mWebSocketClient.send(jsonString);
    }

    public void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://appxBlockchain132.mybluemix.net:");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        mWebSocketClient = new MyWebSocketClient(uri,callBack);
        mWebSocketClient.connect();
    }
}
