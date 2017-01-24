package paulo.nguyenphong.appxblockchainproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import paulo.nguyenphong.blockchain.ConnectBlockchain;
import paulo.nguyenphong.callbackInterface.WebsocketCallBack;

public class LoginActivity extends Activity implements WebsocketCallBack {
    String TAG = "EYEVERTIFY";
    Button Login;
    TextView Notification,SignUp;
    EditText UserName,Password;
    Context ctx;
    public static ConnectBlockchain blockchain;// = new ConnectBlockchain();
    String username,userpassword;
    boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        blockchain = new ConnectBlockchain(this);
        blockchain.connectWebSocket();
        Login = (Button) findViewById(R.id.BtnLogin);
        SignUp = (TextView) findViewById(R.id.BtnSignUp);
        UserName = (EditText) findViewById(R.id.EdTxUserName);
        Password = (EditText) findViewById(R.id.EdTxPassword);
        Notification = (TextView) findViewById(R.id.TxtVNotification);
        ctx = this;
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegistrationActivity();
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Login click");
                String usn = UserName.getText().toString();
                String psw = Password.getText().toString();
                username = usn;
                userpassword = null;
                if (usn != null && psw != null) {
                    if (usn != "" && psw != null) {
                        if (usn.indexOf(' ') < 0 && psw.indexOf(' ') < 0) {
                            userpassword = psw;
                            if (isConnected) {
                                blockchain.sendMsg("{ \"type\": \"login\", \"username\": \"" + usn + "\", \"passwords\": \"" + psw + "\", \"v\":1 }");
                            }else{
                                blockchain.connectWebSocket();
                                Notification.setText("Sorry this app disconnect with blockchain, try again in few second!!");
                            }
                        }
                    }
                }
            }
        });
        Button adminStart = (Button) findViewById(R.id.startAdmin);
        adminStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(ctx, AdminActivity.class);
                //EditText editText = (EditText) findViewById(R.id.edit_message);
                //String message = editText.getText().toString();
                //intent.putExtra("USERNAME", username);
                startActivity(intent);*/
            }
        });
    }

    @Override
    public void onRecivedMsg(String msg) {
        try {
            JSONObject mainObject = new JSONObject(msg);
            if (mainObject.getString("msg").equals("login_success")) {
                Log.d(TAG, "Open transaction Activity!");
                openTransactionActivity();
            } else
                showNotification("Wrong username or password");
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
        showNotification("Disconnect blockchain!!");
    }

    @Override
    public void onOpen() {
        isConnected = true;
        showNotification("Connected to blockchain!!");
    }

    public void showNotification(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Notification.setText(msg);
            }
        });
    }

    private void openRegistrationActivity(){
        Intent intent = new Intent(this, CredentialRegistrationActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = editText.getText().toString();
        //intent.putExtra("USERNAME", username);
        startActivity(intent);
    }

    private void openTransactionActivity(){
        Intent intent = new Intent(this, TransactionActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = editText.getText().toString();
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }

    private class DownloadFilesTask extends AsyncTask<String, String, String> {
        protected String doInBackground(String... urls) {


            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(ctx);
            String url = urls[0];//Url+"signup.php?username="+usn+"&password="+psw;

// Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            //mTextView.setText("Response is: "+ response.substring(0,500));
                            if(response.indexOf("success")>=0){
                                openTransactionActivity();
                            }else {
                                Notification.setText("Wrong username or password!");
                            }
                            Log.d(TAG,response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Notification.setText("Wrong username or password!");
                }
            });
// Add the request to the RequestQueue.
            queue.add(stringRequest);
            return null;
        }

        protected void onProgressUpdate(String... progress) {
        }

        protected void onPostExecute(String result) {
        }
    }
}
