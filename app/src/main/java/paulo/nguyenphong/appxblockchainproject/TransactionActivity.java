package paulo.nguyenphong.appxblockchainproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import paulo.nguyenphong.appxblockchainproject.LoginActivity;
import paulo.nguyenphong.callbackInterface.WebsocketCallBack;


public class TransactionActivity extends Activity implements WebsocketCallBack {

    String TAG = "EYEVERTIFY";
    Context ctx;
    Button BtnSendmoney, BtnGetnotification;
    EditText EdtReciver, EdtNumberMoney;
    TextView Notification, TxtBlance;
    String username = "", userpassword = "";
    private Timer myEyeDetectTimer;
    boolean isConnected = true, updateBlance = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        BtnSendmoney = (Button) findViewById(R.id.BtnSendmoney);
        EdtReciver = (EditText) findViewById(R.id.EdtxReciverName);
        EdtNumberMoney = (EditText) findViewById(R.id.EdtxMoney);
        Notification = (TextView) findViewById(R.id.TxtNotification);
        TxtBlance = (TextView) findViewById(R.id.TxtBlance);
        LoginActivity.blockchain.changeCallBack(this);
        myEyeDetectTimer = new Timer();
        myEyeDetectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                EyeDetectTimerMethod();
            }

        }, 0, 5000);
        BtnSendmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recivername = EdtReciver.getText().toString();
                String money = EdtNumberMoney.getText().toString();
                if (recivername != null && money != null) {
                    if (recivername != "" && money != "") {
                        if (recivername.indexOf(' ') < 0) {
                            //Log.d(TAG,"{ \"type\": \"transaction\", \"sender\": \"" + username + "\", \"reciver\": \"" + recivername + "\", \"value\": \"" + money + "\", \"v\":1 }");
                            if (isConnected)
                                LoginActivity.blockchain.sendMsg("{ \"type\": \"transaction\", \"sender\": \"" + username + "\", \"reciver\": \"" + recivername + "\", \"value\": \"" + money + "\", \"v\":1 }");
                            else LoginActivity.blockchain.connectWebSocket();
                        }
                    }
                }
            }
        });
        BtnGetnotification = (Button) findViewById(R.id.BtnGetnotification);
        BtnGetnotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGetNotificationActivity();
            }
        });
        ctx = this;
    }

    private void openGetNotificationActivity() {
        Intent intent = new Intent(this, NotificationActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = editText.getText().toString();
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }

    public void showNotification(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Notification.setText(msg);
            }
        });
    }

    public void setBlance(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TxtBlance.setText(msg);
            }
        });
    }

    private void EyeDetectTimerMethod() {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(EyeDetectTimer_Tick);
    }

    private Runnable EyeDetectTimer_Tick = new Runnable() {
        public void run() {
            if (updateBlance) {
                if (isConnected) {
                    LoginActivity.blockchain.sendMsg("{ \"type\": \"getblance\", \"username\": \"" + username + "\", \"passwords\": \"" + userpassword + "\", \"v\":1 }");
                    updateBlance = false;
                } else LoginActivity.blockchain.connectWebSocket();
            }
        }
    };

    @Override
    public void onRecivedMsg(String msg) {
        if (msg.indexOf("transaction") > 0) {
            showNotification("Pls wait system update!");
            updateBlance = true;
        } else if (msg.indexOf("user_balance") > 0) {
            try {
                JSONObject mainObject = new JSONObject(msg);
                String bl = mainObject.getString("bl");
                setBlance(bl);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(Exception ex) {

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        isConnected = false;
        showNotification("Disconnected with blockchain pls try again!!");
    }

    @Override
    public void onOpen() {
        isConnected = true;
        showNotification("Connected blockchain!");
    }
}
