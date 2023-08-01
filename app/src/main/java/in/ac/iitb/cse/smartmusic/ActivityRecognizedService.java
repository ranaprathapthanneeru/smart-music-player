package in.ac.iitb.cse.smartmusic;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

public class ActivityRecognizedService extends IntentService {

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities( result.getProbableActivities() );
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {

        Intent cmi = new Intent(ActivityRecognizedService.this, MainActivity.class);
        cmi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        cmi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        cmi.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        int runC = -1, stillC = -1, walkC = -1;
        for( DetectedActivity activity : probableActivities ) {
            switch( activity.getType() ) {
                case DetectedActivity.RUNNING: {
                    Log.e( "ActivityRecogition", "Running: " + activity.getConfidence() );
                    runC = activity.getConfidence();
                    break;
                }
                case DetectedActivity.STILL: {
                    Log.e( "ActivityRecogition", "Still: " + activity.getConfidence() );
                    stillC = activity.getConfidence();
                    break;
                }
                case DetectedActivity.WALKING: {
                    Log.e( "ActivityRecogition", "Walking: " + activity.getConfidence() );
                    walkC = activity.getConfidence();
                    break;
                }
            }
        }

        if(stillC != -1) {
            cmi.putExtra("Action", "still");
            Log.d("log", "handleDetectedActivities: Still detected, sending extra as Action: " + cmi.getStringExtra("Action"));
            //startActivity(cmi);
            return;
        }
        if(runC != -1 || walkC != -1) {
            if(runC > walkC) {
                cmi.putExtra("Action", "running");
                //startActivity(cmi);
            }
            else {
                cmi.putExtra("Action", "walking");
                //startActivity(cmi);
            }
        }
    }

}