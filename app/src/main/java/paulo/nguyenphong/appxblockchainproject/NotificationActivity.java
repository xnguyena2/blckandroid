package paulo.nguyenphong.appxblockchainproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import paulo.nguyenphong.callbackInterface.WebsocketCallBack;

public class NotificationActivity extends Activity implements WebsocketCallBack {
    String TAG = "EYEVERTIFY";

    boolean isConnected = true;
    String username;
    TextView notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        LoginActivity.blockchain.changeCallBack(this);
        LoginActivity.blockchain.sendMsg("{ \"type\": \"getuserinfomation\", \"username\": \"" + username + "\", \"v\":1 }");
        notification = (TextView)findViewById(R.id.TxtNotification);
    }

    @Override
    public void onRecivedMsg(String msg) {
        try {
            JSONObject mainObject = new JSONObject(msg);

            JSONObject userInfomation = new JSONObject(mainObject.getString("userinfomation"));

            if (mainObject.getString("msg").equals("user_infomation") && userInfomation != null) {
                JSONArray ntfa = userInfomation.getJSONArray("notification");
                if (ntfa != null)
                    notification.setText(ntfa.getString(ntfa.length() - 1));
                else notification.setText("have not notification from server");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Exception ex) {

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        isConnected = false;
        LoginActivity.blockchain.connectWebSocket();
    }

    @Override
    public void onOpen() {
        isConnected = true;
    }
}
