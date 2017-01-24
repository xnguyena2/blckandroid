package paulo.nguyenphong.appxblockchainproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import paulo.nguyenphong.callbackInterface.WebsocketCallBack;


public class RegistrationActivity extends Activity implements WebsocketCallBack {

    private static final int RESULT_FROM_IMAGE_UPLOAD = 1;
    public static final String RETURN_IMAGE_HASH_NAME = "HASH_NAME";
    public static final String RETURN_VIDEO_HASH_NAME = "HASH_NAME";
    private static final int RESULT_FROM_VIDEO_UPLOAD = 2;
    private static final String TAG = "EYEVERTIFY";


    EditText firstName, lastName, birthDay, address, city, country, phone, email, password;
    TextView Notification;
    ImageView signUp;
    Button addImgBtn, addVideoBtn;
    String username,userpassword;
    boolean isConnected = true;
    Context ctx;
    boolean isRegisteSuccess = false;

    String imageHash = "imagehash", videoHash = "videoHash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        LoginActivity.blockchain.changeCallBack(this);
        firstName = (EditText) findViewById(R.id.EdtxFirstName);
        lastName = (EditText) findViewById(R.id.EdtxLastName);
        birthDay = (EditText) findViewById(R.id.EdtxBirthDay);
        address = (EditText) findViewById(R.id.EdtxAddress);
        city = (EditText) findViewById(R.id.EdtxCity);
        country = (EditText) findViewById(R.id.EdtxCountry);
        phone = (EditText) findViewById(R.id.EdtxPhone);
        email = (EditText) findViewById(R.id.EdtxEmail);
        password = (EditText) findViewById(R.id.EdtxPassword);
        signUp = (ImageView) findViewById(R.id.BtnSignUp);
        Notification = (TextView) findViewById(R.id.TxtVNotification);
        addImgBtn = (Button) findViewById(R.id.addImgBtn);
        addVideoBtn = (Button) findViewById(R.id.addVideoBtn);

        addImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageUploadActivity();
            }
        });

        addVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVideoUploadActivity();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Signup click!");
                String usn = email.getText().toString();
                String psw = password.getText().toString();
                username = usn;
                userpassword = null;
                if (usn != null && psw != null) {
                    if (usn != "" && psw != "") {
                        if (usn.indexOf(' ') < 0) {
                            userpassword = psw;
                            Log.d(TAG, "Signup clickddd!" + usn + "," + psw);
                            if (isConnected)
                                LoginActivity.blockchain.sendMsg("{ \"type\": \"createuser\", \"username\": \"" + username + "\", \"passwords\": \"" + userpassword + "\"," +
                                        " \"imagehash\": \"" + imageHash + "\", \"videohash\": \"" + videoHash + "\", \"v\":1 }");
                            else LoginActivity.blockchain.connectWebSocket();
                        }
                    } else
                        Notification.setText("Empty username or password!");
                } else
                    Notification.setText("Empty username or password!");
            }
        });
        ctx = this;
    }

    private void openTransactionActivity(){
        Intent intent = new Intent(this, TransactionActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = editText.getText().toString();
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_FROM_IMAGE_UPLOAD:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    imageHash = data.getStringExtra(RETURN_IMAGE_HASH_NAME);
                    Log.d(TAG, imageHash);
                }
                break;
            case RESULT_FROM_VIDEO_UPLOAD:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    videoHash = data.getStringExtra(RETURN_VIDEO_HASH_NAME);
                    Log.d(TAG, videoHash);
                }
                break;
        }
    }

    private void openImageUploadActivity(){
        Intent intent = new Intent(this, ImageUploadActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = editText.getText().toString();
        //intent.putExtra("USERNAME", username);
        startActivityForResult(intent,RESULT_FROM_IMAGE_UPLOAD);
    }

    private void openVideoUploadActivity(){
        Intent intent = new Intent(this, VideoRecordingActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = editText.getText().toString();
        //intent.putExtra("USERNAME", username);
        startActivityForResult(intent,RESULT_FROM_VIDEO_UPLOAD);
    }

    @Override
    public void onRecivedMsg(String msg) {
        if (msg.indexOf("createuserinfo_sucess") >= 0) {
            isRegisteSuccess = true;
        }else if(isRegisteSuccess && msg.indexOf("chainstats")>0){
            openTransactionActivity();
        }
        else if(msg.indexOf("Error when checkexitaccount")>=0)
            showNotification("Error when registe this account!");
        else if(msg.indexOf("This account alredy exit")>=0)
            showNotification("This account alredy exit!");
    }

    public void showNotification(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Notification.setText(msg);
            }
        });
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
