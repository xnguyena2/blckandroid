package paulo.nguyenphong.google.cloud;

/**
 * Created by Nguyen Phong on 12/29/2016.
 */

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.StorageObject;

import java.io.File;
import java.io.*;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class CloudStorage {

    static String TAG = "EYEVERTIFY";
    static Activity activity=null;
//http://stackoverflow.com/questions/18002293/uploading-image-from-android-to-gcs

    static Storage storage=null;
    public static String uploadFile(Activity activity2,String bucketName, String filePath, String hashName) {
        String objectName;
        activity = activity2;
        try {
            Storage storage = getStorage();
            StorageObject object = new StorageObject();
            object.setBucket(bucketName);
            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(filePath);

            InputStream stream = new FileInputStream(file);

            try {
                Log.d(TAG, "Test");
                String contentType = URLConnection.guessContentTypeFromStream(stream);
                InputStreamContent content = new InputStreamContent(contentType, stream);

                Storage.Objects.Insert insert = storage.objects().insert(bucketName, null, content);
                objectName = hashName + file.getName().substring(file.getName().indexOf('.'));
                insert.setName(objectName);
                insert.execute();

            } finally {
                stream.close();
            }
        } catch (Exception e) {
            class Local {
            }
            ;
            Log.d(TAG, "Sub: " + Local.class.getEnclosingMethod().getName() + " Error code: " + e.getMessage());

            e.printStackTrace();
            objectName = null;
        }
        return objectName;
    }

    private static Storage getStorage() {

        try {

            if (storage == null)
            {
                HttpTransport httpTransport = new NetHttpTransport();
                JsonFactory jsonFactory = new JacksonFactory();
                List<String> scopes = new ArrayList<String>();
                scopes.add(StorageScopes.DEVSTORAGE_FULL_CONTROL);

                Credential credential = new GoogleCredential.Builder()
                        .setTransport(httpTransport)
                        .setJsonFactory(jsonFactory)
                        .setServiceAccountId("174812814028-compute@developer.gserviceaccount.com") //Email
                        .setServiceAccountPrivateKeyFromP12File(getTempPkc12File())
                        .setServiceAccountScopes(scopes).build();

                storage = new Storage.Builder(httpTransport, jsonFactory,
                        credential)
                        .build();
            }

            return storage;
        }catch(Exception e)
        {
            class Local {}; Log.d(TAG,"Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());

        }
        Log.d(TAG,"Storage object is null ");
        return null;
    }

    private static File getTempPkc12File() {
        try {
            // xxx.p12 export from google API console
            InputStream pkc12Stream = activity.getResources().getAssets().open("Meu primeiro projeto-0eb6c2155699.p12");
            File tempPkc12File = File.createTempFile("temp_pkc12_file", "p12");
            OutputStream tempFileStream = new FileOutputStream(tempPkc12File);
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = pkc12Stream.read(bytes)) != -1) {
                tempFileStream.write(bytes, 0, read);
            }
            return tempPkc12File;
        }catch(Exception e)
        {
            class Local {}; Log.d(TAG,"Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());

        }
        Log.d(TAG," getTempPkc12File is null");
        return null;
    }
}