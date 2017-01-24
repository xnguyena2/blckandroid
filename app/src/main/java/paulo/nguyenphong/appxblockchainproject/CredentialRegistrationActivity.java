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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import paulo.nguyenphong.callbackInterface.WebsocketCallBack;

public class CredentialRegistrationActivity extends Activity implements WebsocketCallBack {

    private static final int RESULT_FROM_FRONT_IDCARD_IMAGE_UPLOAD = 1;
    private static final int RESULT_FROM_BACK_IDCARD_IMAGE_UPLOAD = 2;
    private static final int RESULT_FROM_VIDEO_UPLOAD = 3;
    public static final String RETURN_IMAGE_HASH_NAME = "HASH_NAME";
    public static final String RETURN_VIDEO_HASH_NAME = "HASH_NAME";
    private static final String TAG = "EYEVERTIFY";

    boolean isConnected = true;

    EditText email, password, firstname, lastname, birthday, sex, nationality, naturality, naturalityuf, credential,
            organissuingidentity, dateissuingidentity, organidentitymilitar, fathername, mothername;

    TextView Notification;

    Button addFrontImg, addBackImg, addVideo;
    ImageView signupBtn;

    String Scredential, Semail, Spassword, Sfirstname, Slastname, Sbirthday, Ssex, Snationality, Snaturality, Snaturalityuf,
            SfrontImg = RETURN_IMAGE_HASH_NAME,
            SbackImg = RETURN_IMAGE_HASH_NAME,
            Svideo = RETURN_VIDEO_HASH_NAME,
            Sorganissuingidentity, Sdateissuingidentity, Sorganidentitymilitar, Sfathername, Smothername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credential_registration);
        getID();
        setEventHandle();
        LoginActivity.blockchain.changeCallBack(this);
    }

    private void openImageUploadActivity(int IDResult) {
        Intent intent = new Intent(this, ImageUploadActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = editText.getText().toString();
        //intent.putExtra("USERNAME", username);
        startActivityForResult(intent, IDResult);
    }

    private void openVideoUploadActivity() {
        Intent intent = new Intent(this, VideoRecordingActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = editText.getText().toString();
        //intent.putExtra("USERNAME", username);
        startActivityForResult(intent, RESULT_FROM_VIDEO_UPLOAD);
    }

    void getID() {
        credential = (EditText) findViewById(R.id.EdtxCredential);
        email = (EditText) findViewById(R.id.EdtxEmail);
        password = (EditText) findViewById(R.id.EdtxPassword);
        firstname = (EditText) findViewById(R.id.EdtxFirstName);
        lastname = (EditText) findViewById(R.id.EdtxLastName);
        birthday = (EditText) findViewById(R.id.EdtxBirthDay);
        sex = (EditText) findViewById(R.id.EdtxSex);
        nationality = (EditText) findViewById(R.id.EdtxNationality);
        naturality = (EditText) findViewById(R.id.EdtxNaturality);
        naturalityuf = (EditText) findViewById(R.id.EdtxNaturalityUF);
        organissuingidentity = (EditText) findViewById(R.id.EdtxOrganIssuingIdentity);
        dateissuingidentity = (EditText) findViewById(R.id.EdtxDateOrganIdentity);
        organidentitymilitar = (EditText) findViewById(R.id.EdtxOrganIdentityMilitar);
        fathername = (EditText) findViewById(R.id.EdtxFatherName);
        mothername= (EditText) findViewById(R.id.EdtxMotherName);
        addFrontImg = (Button) findViewById(R.id.addFrontImgBtn);
        addBackImg = (Button) findViewById(R.id.addBackImgBtn);
        addVideo = (Button) findViewById(R.id.addVideoBtn);
        signupBtn = (ImageView) findViewById(R.id.BtnSignUp);
        Notification = (TextView) findViewById(R.id.TxtNotification);
    }

    void setEventHandle() {
        addFrontImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageUploadActivity(RESULT_FROM_FRONT_IDCARD_IMAGE_UPLOAD);
            }
        });
        addBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageUploadActivity(RESULT_FROM_BACK_IDCARD_IMAGE_UPLOAD);
            }
        });
        addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVideoUploadActivity();
            }
        });
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfomation();
                if (Semail != null && Spassword != null) {
                    if (Semail != "" && Spassword != "") {
                        if (Semail.indexOf(' ') < 0) {
                            Log.d(TAG, "Signup clickddd!" + Semail + "," + Spassword);
                            if (isConnected)
                                LoginActivity.blockchain.sendMsg(
                                        "{ " +
                                                "\"type\": \"createuser\"," +
                                                "\"credential\": \"" + Scredential + "\"," +
                                                "\"username\": \"" + Semail + "\"," +
                                                "\"passwords\": \"" + Spassword + "\"," +
                                                "\"frontidcard\": \"" + SfrontImg + "\"," +
                                                "\"backidcard\": \"" + SbackImg + "\"," +
                                                "\"videohash\": \"" + Svideo + "\"," +
                                                "\"firstname\": \"" + Sfirstname + "\"," +
                                                "\"lastname\": \"" + Slastname + "\"," +
                                                "\"sex\": \"" + Ssex + "\"," +
                                                "\"birthday\": \"" + Sbirthday + "\"," +
                                                "\"nationality\": \"" + Snationality + "\"," +
                                                "\"naturality\": \"" + Snaturality + "\"," +
                                                "\"naturalityuf\": \"" + Snaturalityuf + "\"," +
                                                "\"organissuingidentity\": \"" + Sorganissuingidentity + "\"," +
                                                "\"dateissuingidentity\": \"" + Sdateissuingidentity + "\"," +
                                                "\"organidentitymilitar\": \"" + Sorganidentitymilitar + "\"," +
                                                "\"fathername\": \"" + Sfathername + "\"," +
                                                "\"mothername\": \"" + Smothername + "\"," +
                                                "\"v\":1 " +
                                                "}"
                                );
                            else LoginActivity.blockchain.connectWebSocket();
                        }else showNotification("Wrong email type!!");
                    } else
                        showNotification("Empty username or password!");
                } else
                    showNotification("Empty username or password!");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_FROM_FRONT_IDCARD_IMAGE_UPLOAD:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    SfrontImg = data.getStringExtra(RETURN_IMAGE_HASH_NAME);
                    Log.d(TAG, SfrontImg);
                }
                break;
            case RESULT_FROM_BACK_IDCARD_IMAGE_UPLOAD:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    SbackImg = data.getStringExtra(RETURN_IMAGE_HASH_NAME);
                    Log.d(TAG, SbackImg);
                }
                break;
            case RESULT_FROM_VIDEO_UPLOAD:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Svideo = data.getStringExtra(RETURN_IMAGE_HASH_NAME);
                    Log.d(TAG, Svideo);
                }
                break;
        }
    }

    void getInfomation() {
        Semail = email.getText().toString();
        Spassword = password.getText().toString();
        Scredential = credential.getText().toString();
        Sfirstname = firstname.getText().toString();
        Slastname = lastname.getText().toString();
        Ssex = sex.getText().toString();
        Sbirthday = birthday.getText().toString();
        Snationality = nationality.getText().toString();
        Snaturality = naturality.getText().toString();
        Snaturalityuf = naturalityuf.getText().toString();
        Sorganissuingidentity=organissuingidentity.getText().toString();
        Sdateissuingidentity = dateissuingidentity.getText().toString();
        Sorganidentitymilitar = organidentitymilitar.getText().toString();
        Sfathername = fathername.getText().toString();
        Smothername = mothername.getText().toString();
    }

    private void openTransactionActivity() {
        Intent intent = new Intent(this, TransactionActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = editText.getText().toString();
        intent.putExtra("USERNAME", Semail);
        startActivity(intent);
    }

    public void showNotification(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Notification != null)
                    Notification.setText(msg);
            }
        });
    }

    @Override
    public void onRecivedMsg(String msg) {

        try {
            JSONObject mainObject = new JSONObject(msg);
            String message = mainObject.getString("msg");
            if (message.equals("createuserinfo_sucess")) {
                openTransactionActivity();
            } else {
                showNotification(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Exception ex) {
        showNotification(ex.getMessage());
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        LoginActivity.blockchain.connectWebSocket();
        isConnected = false;
        showNotification("Disconnected with blockchain pls try again!!");
    }

    @Override
    public void onOpen() {
        isConnected = true;
        showNotification("Connected blockchain!");
    }
}
