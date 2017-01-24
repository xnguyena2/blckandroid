package paulo.nguyenphong.utils;

import android.os.AsyncTask;

import paulo.nguyenphong.Interface.ButtonClickInterface;

/**
 * Created by Nguyen Phong on 1/2/2017.
 */

public class ButtonClickEventAsyncTask extends AsyncTask<String, Void, String> {

    ButtonClickInterface buttonClickInterface;

    public ButtonClickEventAsyncTask(ButtonClickInterface buttonClickInterface){
        this.buttonClickInterface = buttonClickInterface;
    }

    @Override
    protected void onPostExecute(String result) {
        buttonClickInterface.finishedEvent();
    }

    @Override
    protected String doInBackground(String... strings) {
        buttonClickInterface.startEvent();
        return null;
    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected void onProgressUpdate(Void... values) {}
}
