package paulo.nguyenphong.appxblockchainproject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import paulo.nguyenphong.Interface.ButtonClickInterface;
import paulo.nguyenphong.camera.Camera2VideoFragment;
import paulo.nguyenphong.google.cloud.CloudStorage;
import paulo.nguyenphong.hash.Hash;

public class VideoRecordingActivity extends Activity implements RecognitionListener,ButtonClickInterface {
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    private static final int REQUEST_VIDEO_CAPTURE = 1002;
    private static final String TAG = "EYEVERTIFY";

    boolean isVoiceRecogize = false;
    boolean isDeviceSpeechSuport = true;

    String videoHashName = "video_hash";

    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    Context context;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case VOICE_RECOGNITION_REQUEST_CODE:
                if (resultCode == RESULT_OK){
                    ArrayList<String> textMatchlist = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    if (!textMatchlist.isEmpty()) {
                        Camera2VideoFragment.notifiTxt.setText(Camera2VideoFragment.notifiTxt.getText() + textMatchlist.get(0));
                    }
                }
                else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
                    showToastMessage("Audio Error");

                }
                else if ((resultCode == RecognizerIntent.RESULT_CLIENT_ERROR)){
                    showToastMessage("Client Error");

                }
                else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
                    showToastMessage("Network Error");
                }
                else if (resultCode == RecognizerIntent.RESULT_NO_MATCH){
                    showToastMessage("No Match");
                }
                else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
                    showToastMessage("Server Error");
                }
                break;
            case REQUEST_VIDEO_CAPTURE:
                if (resultCode == RESULT_OK){
                }
                break;
        }
    }

    void  showToastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }


    public  void  CheckVoiceRecognition(){
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH),0);
        if (activities.size()==0){
            isDeviceSpeechSuport = false;
            Toast.makeText(this,"Voice recognizer not present",Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_recording);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2VideoFragment.newInstance())
                    .commit();
        }

        context = this;

        CheckVoiceRecognition();

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 3000);

    }

    public void beginVoiceReczg(){
        if(isDeviceSpeechSuport) {
            try {
                if (!isVoiceRecogize) {
                    //dispatchTakeVideoIntent();
                    speech.startListening(recognizerIntent);
                    isVoiceRecogize = !isVoiceRecogize;
                } else {
                    speech.stopListening();
                    isVoiceRecogize = !isVoiceRecogize;
                }
                Camera2VideoFragment.notifiTxt.setText("");
            } catch (ActivityNotFoundException a) {
                Toast t = Toast.makeText(getApplicationContext(),
                        "Opps! Your device doesn't support Speech to Text",
                        Toast.LENGTH_SHORT);
                t.show();
            }
        }else {
            Toast.makeText(context,"Voice recognizer not present",Toast.LENGTH_LONG).show();
        }
    }

    public void uploadVideo(String pathfile) {
        try {
            File file = new File(pathfile);
            videoHashName = Hash.createHash() + "_" + file.getName();
            CloudStorage.uploadFile(this, "paulophototest", pathfile, videoHashName);
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void finishedActivity() {
        try {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(RegistrationActivity.RETURN_VIDEO_HASH_NAME, videoHashName);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.i(TAG, "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float v) {
        Log.i(TAG, "onRmsChanged: " + v);
    }

    @Override
    public void onBufferReceived(byte[] bytes) {
        Log.i(TAG, "onBufferReceived: " + bytes);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(TAG, "onEndOfSpeech");
        isVoiceRecogize = false;
    }

    @Override
    public void onError(int i) {
        String errorMessage = getErrorText(i);
        Log.d(TAG, "FAILED " + errorMessage);
        Camera2VideoFragment.notifiTxt.setText(errorMessage);
    }

    @Override
    public void onResults(Bundle bundle) {
        Log.i(TAG, "onResults");
    }

    @Override
    public void onPartialResults(Bundle bundle) {
        Log.i(TAG, "onPartialResults");
        ArrayList<String> matches = bundle
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";

        Camera2VideoFragment.notifiTxt.setText(text);
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        Log.i(TAG, "onEvent");
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    @Override
    public void startEvent() {

    }

    @Override
    public void finishedEvent() {

    }
}
