package ir.sobhe.khoobha;

import android.app.*;
import android.app.Activity;
import android.content.Intent;

/**
 * Created by hadi on 14/5/10 AD.
 */
public class SyncService extends IntentService {

    private int result = Activity.RESULT_CANCELED;
    public static final String NOTIFICATION = "ir.sobhe.khoobha";

    public SyncService(){
        super("SyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // code for doing service task


        //when everything has been done
        publishResult(result);
        stopSelf();


    }

    private void publishResult(int result){
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra("result", result);
        sendBroadcast(intent);
    }
}
