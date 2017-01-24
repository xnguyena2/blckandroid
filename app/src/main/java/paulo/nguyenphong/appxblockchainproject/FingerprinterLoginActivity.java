package paulo.nguyenphong.appxblockchainproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pass.Spass;
import com.samsung.android.sdk.pass.SpassFingerprint;

import java.util.ArrayList;

public class FingerprinterLoginActivity extends Activity implements Handler.Callback {

    private SpassFingerprint mSpassFingerprint;
    private Spass mSpass;
    private Context mContext;

    private boolean needRetryIdentify = false;
    private boolean onReadyIdentify = false;
    private boolean onReadyEnroll = false;
    private boolean hasRegisteredFinger = false;

    private boolean isFeatureEnabled_fingerprint = false;
    private boolean isFeatureEnabled_index = false;
    private boolean isFeatureEnabled_uniqueId = false;
    private boolean isFeatureEnabled_custom = false;
    private boolean isFeatureEnabled_backupPw = false;

    private ArrayList<Integer> designatedFingersDialog = null;

    private Button mLoginBtn;

    private Handler mHandler;
    private static final int MSG_AUTH = 1000;
    private static final int MSG_AUTH_UI_WITH_PW = 1001;
    private static final int MSG_AUTH_UI_WITHOUT_PW = 1002;
    private static final int MSG_CANCEL = 1003;
    private static final int MSG_REGISTER = 1004;
    private static final int MSG_GET_NAME = 1005;
    private static final int MSG_GET_UNIQUEID = 1006;
    private static final int MSG_AUTH_INDEX = 1007;
    private static final int MSG_AUTH_UI_INDEX = 1008;
    private static final int MSG_AUTH_UI_CUSTOM_LOGO = 1009;
    private static final int MSG_AUTH_UI_CUSTOM_TRANSPARENCY = 1010;
    private static final int MSG_AUTH_UI_CUSTOM_DISMISS = 1011;
    private static final int MSG_AUTH_UI_CUSTOM_BUTTON_STANDBY = 1012;

    private SpassFingerprint.IdentifyListener mIdentifyListenerDialog = new SpassFingerprint.IdentifyListener() {
        @Override
        public void onFinished(int eventStatus) {
            log("identify finished : reason =" + getEventStatusName(eventStatus));
            int FingerprintIndex = 0;
            boolean isFailedIdentify = false;
            onReadyIdentify = false;
            try {
                FingerprintIndex = mSpassFingerprint.getIdentifiedFingerprintIndex();
            } catch (IllegalStateException ise) {
                log(ise.getMessage());
            }
            if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS) {
                log("onFinished() : Identify authentification Success with FingerprintIndex : " + FingerprintIndex);
                openRegistrationActivity();
            } else if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS) {
                log("onFinished() : Password authentification Success");
            } else if (eventStatus == SpassFingerprint.STATUS_USER_CANCELLED
                    || eventStatus == SpassFingerprint.STATUS_USER_CANCELLED_BY_TOUCH_OUTSIDE) {
                log("onFinished() : User cancel this identify.");
            } else if (eventStatus == SpassFingerprint.STATUS_TIMEOUT_FAILED) {
                log("onFinished() : The time for identify is finished.");
            } else if (!mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT_AVAILABLE_PASSWORD)) {
                if (eventStatus == SpassFingerprint.STATUS_BUTTON_PRESSED) {
                    log("onFinished() : User pressed the own button");
                    Toast.makeText(mContext, "Please connect own Backup Menu", Toast.LENGTH_SHORT).show();
                }
            } else {
                log("onFinished() : Authentification Fail for identify");
                isFailedIdentify = true;
            }
            if (!isFailedIdentify) {
                resetIdentifyIndexDialog();
            }
        }

        @Override
        public void onReady() {
            log("identify state is ready");
        }

        @Override
        public void onStarted() {
            log("User touched fingerprint sensor");
        }

        @Override
        public void onCompleted() {
            log("the identify is completed");
        }
    };

    private static String getEventStatusName(int eventStatus) {
        switch (eventStatus) {
            case SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS:
                return "STATUS_AUTHENTIFICATION_SUCCESS";
            case SpassFingerprint.STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS:
                return "STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS";
            case SpassFingerprint.STATUS_TIMEOUT_FAILED:
                return "STATUS_TIMEOUT";
            case SpassFingerprint.STATUS_SENSOR_FAILED:
                return "STATUS_SENSOR_ERROR";
            case SpassFingerprint.STATUS_USER_CANCELLED:
                return "STATUS_USER_CANCELLED";
            case SpassFingerprint.STATUS_QUALITY_FAILED:
                return "STATUS_QUALITY_FAILED";
            case SpassFingerprint.STATUS_USER_CANCELLED_BY_TOUCH_OUTSIDE:
                return "STATUS_USER_CANCELLED_BY_TOUCH_OUTSIDE";
            case SpassFingerprint.STATUS_BUTTON_PRESSED:
                return "STATUS_BUTTON_PRESSED";
            case SpassFingerprint.STATUS_OPERATION_DENIED:
                return "STATUS_OPERATION_DENIED";
            case SpassFingerprint.STATUS_AUTHENTIFICATION_FAILED:
            default:
                return "STATUS_AUTHENTIFICATION_FAILED";
        }
    }

    private void startIdentifyDialog(boolean backup) {
        if (onReadyIdentify == false) {
            onReadyIdentify = true;
            try {
                if (mSpassFingerprint != null) {
                    setIdentifyIndexDialog();
                    mSpassFingerprint.startIdentifyWithDialog(FingerprinterLoginActivity.this, mIdentifyListenerDialog, backup);
                }
                if (designatedFingersDialog != null) {
                    log("Please identify finger to verify you with " + designatedFingersDialog.toString() + " finger");
                } else {
                    log("Please identify finger to verify you");
                }
            } catch (IllegalStateException e) {
                onReadyIdentify = false;
                resetIdentifyIndexDialog();
                log("Exception: " + e);
            }
        } else {
            log("The previous request is remained. Please finished or cancel first");
        }
    }

    private void setIdentifyIndexDialog() {
        if (isFeatureEnabled_index) {
            if (mSpassFingerprint != null && designatedFingersDialog != null) {
                mSpassFingerprint.setIntendedFingerprintIndex(designatedFingersDialog);
            }
        }
    }

    private void makeIdentifyIndexDialog(int i) {
        if (designatedFingersDialog == null) {
            designatedFingersDialog = new ArrayList<Integer>();
        }
        for(int j = 0; j< designatedFingersDialog.size(); j++){
            if(i == designatedFingersDialog.get(j)){
                return;
            }
        }
        designatedFingersDialog.add(i);
    }

    private void resetIdentifyIndexDialog() {
        designatedFingersDialog = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprinter_login);
        mLoginBtn = (Button)findViewById(R.id.mLoginBtn);
        mLoginBtn.setOnClickListener(onButtonClick);
        mContext = this;
        mHandler = new Handler(this);
        mSpass = new Spass();

        try {
            mSpass.initialize(FingerprinterLoginActivity.this);
        } catch (SsdkUnsupportedException e) {
            log("Exception: " + e);
        } catch (UnsupportedOperationException e) {
            log("Fingerprint Service is not supported in the device");
        }
        isFeatureEnabled_fingerprint = mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT);

        if (isFeatureEnabled_fingerprint) {
            mSpassFingerprint = new SpassFingerprint(FingerprinterLoginActivity.this);
            log("Fingerprint Service is supported in the device.");
            log("SDK version : " + mSpass.getVersionName());
        } else {
            //logClear();
            log("Fingerprint Service is not supported in the device.");
            return;
        }

        isFeatureEnabled_index = mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT_FINGER_INDEX);
        isFeatureEnabled_custom = mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT_CUSTOMIZED_DIALOG);
        isFeatureEnabled_uniqueId = mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT_UNIQUE_ID);
        isFeatureEnabled_backupPw = mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT_AVAILABLE_PASSWORD);

    }

    private void openRegistrationActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = editText.getText().toString();
        //intent.putExtra("USERNAME", username);
        startActivity(intent);
    }

    private Button.OnClickListener onButtonClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.mLoginBtn:
                    mHandler.sendEmptyMessage(MSG_AUTH_UI_WITHOUT_PW);
                    break;
            }
        }
    };

    private void log(String text) {
        //final String txt = text;
        Log.d("EYEVERTIFY",text);
/*
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mItemArray.add(0, txt);
                mListAdapter.notifyDataSetChanged();
            }
        });*/
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            /*case MSG_AUTH:
                startIdentify();
                break;*/
            case MSG_AUTH_UI_WITH_PW:
                startIdentifyDialog(true);
                break;
            case MSG_AUTH_UI_WITHOUT_PW:
                startIdentifyDialog(false);
                break;
            /*case MSG_CANCEL:
                cancelIdentify();
                break;
            case MSG_REGISTER:
                registerFingerprint();
                break;
            case MSG_GET_NAME:
                getFingerprintName();
                break;
            case MSG_GET_UNIQUEID:
                getFingerprintUniqueID();
                break;
            case MSG_AUTH_INDEX:
                makeIdentifyIndex(1);
                startIdentify();
                break;
            case MSG_AUTH_UI_INDEX:
                makeIdentifyIndexDialog(2);
                makeIdentifyIndexDialog(3);
                startIdentifyDialog(false);
                break;
            case MSG_AUTH_UI_CUSTOM_LOGO:
                setDialogTitleAndLogo();
                startIdentifyDialog(false);
                break;
            case MSG_AUTH_UI_CUSTOM_TRANSPARENCY:
                setDialogTitleAndTransparency();
                startIdentifyDialog(false);
                break;
            case MSG_AUTH_UI_CUSTOM_DISMISS:
                setDialogTitleAndDismiss();
                startIdentifyDialog(false);
                break;
            case MSG_AUTH_UI_CUSTOM_BUTTON_STANDBY:
                setDialogButtonAndStandbyText();
                startIdentifyDialog(false);
                break;*/
        }
        return true;
    }
}
