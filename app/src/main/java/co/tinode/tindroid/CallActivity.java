package co.tinode.tindroid;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import co.tinode.tindroid.media.VxCard;
import co.tinode.tinodesdk.AlreadySubscribedException;
import co.tinode.tinodesdk.ComTopic;
import co.tinode.tinodesdk.PromisedReply;
import co.tinode.tinodesdk.Tinode;
import co.tinode.tinodesdk.Topic;
import co.tinode.tinodesdk.model.ServerMessage;

// Jitsi meet parameters
import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.BroadcastIntentHelper;
import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;

import java.net.MalformedURLException;
import java.net.URL;

import timber.log.Timber;


public class CallActivity extends AppCompatActivity  {

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onBroadcastReceived(intent);
        }
    };



    private static final String TAG = "CallActivity";

    static final String FRAGMENT_ACTIVE = "active_call";
    static final String FRAGMENT_INCOMING = "incoming_call";

    public static final String INTENT_ACTION_CALL_INCOMING = "tindroidx.intent.action.call.INCOMING";
    public static final String INTENT_ACTION_CALL_START = "tindroidx.intent.action.call.START";

    private boolean mTurnScreenOffWhenDone;

    private Tinode mTinode;

    private String mTopicName;
    private int mSeq;
    private ComTopic<VxCard> mTopic;
    private EventListener mLoginListener;

    private final BroadcastReceiver mFinishCallBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            CallActivity.this.finishCall();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // JitsiMeet parameters
        // Initialize default options for Jitsi Meet conferences.
        URL serverURL;
        try {
            // When using JaaS, replace "https://meet.jit.si" with the proper serverURL
            // serverURL = new URL("https://meet.jit.si");
            serverURL = new URL("https://meet.ffmuc.net/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid server URL!");
        }
        JitsiMeetConferenceOptions defaultOptions
                = new JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
                // When using JaaS, set the obtained JWT here
                // .setToken("eyJraWQiOiJ2cGFhcy1tYWdpYy1jb29raWUtZTdlMjYxYTU2ZWY4NDEyYzgwNzFkZWY5ZTVhYjQ0ZTUvNjA2ZTM3LVNBTVBMRV9BUFAiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJqaXRzaSIsImlzcyI6ImNoYXQiLCJpYXQiOjE3MTY3MDMzMTksImV4cCI6MTcxNjcxMDUxOSwibmJmIjoxNzE2NzAzMzE0LCJzdWIiOiJ2cGFhcy1tYWdpYy1jb29raWUtZTdlMjYxYTU2ZWY4NDEyYzgwNzFkZWY5ZTVhYjQ0ZTUiLCJjb250ZXh0Ijp7ImZlYXR1cmVzIjp7ImxpdmVzdHJlYW1pbmciOnRydWUsIm91dGJvdW5kLWNhbGwiOnRydWUsInNpcC1vdXRib3VuZC1jYWxsIjpmYWxzZSwidHJhbnNjcmlwdGlvbiI6dHJ1ZSwicmVjb3JkaW5nIjp0cnVlfSwidXNlciI6eyJoaWRkZW4tZnJvbS1yZWNvcmRlciI6ZmFsc2UsIm1vZGVyYXRvciI6dHJ1ZSwibmFtZSI6InJyYWJlYW5kcmlhbWFyb3pva3kiLCJpZCI6Imdvb2dsZS1vYXV0aDJ8MTE2OTU1NjIwMDQ1NTg3NTI5NjAyIiwiYXZhdGFyIjoiIiwiZW1haWwiOiJycmFiZWFuZHJpYW1hcm96b2t5QGdtYWlsLmNvbSJ9fSwicm9vbSI6IioifQ.DL8gJrI-b9wRfvysnUoZpwbL2GyPnEEygNJ_KascfIgaYSw_Y1PVzJZ-PlRSquBxVOGmoP_8iILP3wZShILtQL0jCXOqPi-xOhANCYO9gaMHHJqEJtDJwXrPXCaaJECCumcNSJOHVWg30kz0JNiwKDDczk0NNRmIRVcwYOwsuOvbS1O0We-Ggo2kdL_VpwEVS_kit1tob7g525fE7qqRsAKE5Az-OOnU7ThQEdr_06PphT4_UMb7BazutWerp4gz6WvGN1p7ZzHoTqSwTmUzcwzb-phwEJ4HsJkSrXqwGywuIY8jsOKsJzfLxG1bnimMs5zQ9U27ikT3KSHtL6I1hg")
                // Different features flags can be set
                // .setFeatureFlag("toolbox.enabled", false)
                // .setFeatureFlag("filmstrip.enabled", false)
                .setFeatureFlag("welcomepage.enabled", false)
                .build();
        JitsiMeet.setDefaultConferenceOptions(defaultOptions);

        registerForBroadcastMessages();

        NotificationManager nm = getSystemService(NotificationManager.class);
        nm.cancel(CallManager.NOTIFICATION_TAG_INCOMING_CALL, 0);

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.registerReceiver(mFinishCallBroadcastReceiver, new IntentFilter(Const.INTENT_ACTION_CALL_CLOSE));

        final Intent intent = getIntent();
        final String action = intent != null ? intent.getAction() : null;
        if (action == null) {
            Log.w(TAG, "No intent or no valid action, unable to proceed");
            finish();
            return;
        }

        // Using action once.
        intent.setAction(null);

        mTinode = Cache.getTinode();

        mTopicName = intent.getStringExtra(Const.INTENT_EXTRA_TOPIC);
        mSeq = intent.getIntExtra(Const.INTENT_EXTRA_SEQ, -1);
        // noinspection unchecked
        mTopic = (ComTopic<VxCard>) mTinode.getTopic(mTopicName);
        if (mTopic == null) {
            Log.e(TAG, "Invalid topic '" + mTopicName + "'");
            finish();
            return;
        }

        Cache.setSelectedTopicName(mTopicName);
        mLoginListener = new EventListener();
        mTinode.addListener(mLoginListener);

        Bundle args = new Bundle();
        args.putBoolean(Const.INTENT_EXTRA_CALL_AUDIO_ONLY,
                intent.getBooleanExtra(Const.INTENT_EXTRA_CALL_AUDIO_ONLY, false));
        String fragmentToShow;
        switch (action) {
            case INTENT_ACTION_CALL_INCOMING:
                // Incoming call started by the ser
                boolean accepted = intent.getBooleanExtra(Const.INTENT_EXTRA_CALL_ACCEPTED, false);
                fragmentToShow = accepted ? FRAGMENT_ACTIVE : FRAGMENT_INCOMING;
                args.putString(Const.INTENT_EXTRA_CALL_DIRECTION, "incoming");
                break;

            case INTENT_ACTION_CALL_START:
                // Call started by the current user.
                args.putString(Const.INTENT_EXTRA_CALL_DIRECTION, "outgoing");
                fragmentToShow = FRAGMENT_ACTIVE;
                break;

            default:
                Log.e(TAG, "Unknown call action '" + action + "'");
                finish();
                return;
        }
        setContentView(R.layout.activity_call);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // Turn screen on and unlock.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //KeyguardManager mgr = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            //mgr.requestDismissKeyguard(this, null);
        }

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mTurnScreenOffWhenDone = !pm.isInteractive();
        // Try to reconnect and subscribe.
        topicAttach();
        showFragment(fragmentToShow, args);
    }

    private void registerForBroadcastMessages() {
        IntentFilter intentFilter = new IntentFilter();

        /**
         * This registers for every possible event sent from JitsiMeetSDK
         * *If only some of the events are needed, the for loop can be replaced
         * with individual statements:
           ex:  intentFilter.addAction(BroadcastEvent.Type.AUDIO_MUTED_CHANGED.getAction());
                intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_TERMINATED.getAction());
                ... other events
         **/
        for (BroadcastEvent.Type type : BroadcastEvent.Type.values()) {
            intentFilter.addAction(type.getAction());
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        if (mTinode != null) {
            mTinode.removeListener(mLoginListener);
        }
        Cache.endCallInProgress();

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.unregisterReceiver(mFinishCallBroadcastReceiver);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        if (mTurnScreenOffWhenDone && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(false);
            setTurnScreenOn(false);
        }

        super.onDestroy();
    }

    private void onBroadcastReceived(Intent intent) {
        if (intent != null) {
            BroadcastEvent event = new BroadcastEvent(intent);

            switch (event.getType()) {
                case CONFERENCE_JOINED:
                    Timber.i("Conference Joined with url%s", event.getData().get("url"));
                    break;
                case PARTICIPANT_JOINED:
                    Timber.i("Participant joined%s", event.getData().get("name"));
                    break;
            }
        }
    }

    private void hangUp() {
        Intent hangupBroadcastIntent = BroadcastIntentHelper.buildHangUpIntent();
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(hangupBroadcastIntent);
    }

    void acceptCall() {

        String text = "lovasoaRoom";
        Bundle args = new Bundle();
        args.putString(Const.INTENT_EXTRA_CALL_DIRECTION, "incoming");
        showFragment(FRAGMENT_ACTIVE, args);
//        JitsiMeetUserInfo tmpUser = new JitsiMeetUserInfo();
//        tmpUser.setDisplayName("Rova");

//            JitsiMeetConferenceOptions options
//                    = new JitsiMeetConferenceOptions.Builder()
//                    .setRoom(text)
//                    .setUserInfo(tmpUser)
//                    // Settings for audio and video
//                    //.setAudioMuted(true)
//                    .setVideoMuted(true)
//                    .build();
//        JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
//                .setRoom(text)
//                .setConfigOverride("requireDisplayName", false)
//                .setConfigOverride("reqiureInviteOthers", false)
//                .setFeatureFlag("invite.enabled", false)// <-- add this line
//                .setFeatureFlag("add-people.enabled", false)// <-- add this line
//                .setFeatureFlag("meeting-name.enabled", false)
//                .setFeatureFlag("raise-hand.enabled", false)
//                .setFeatureFlag("reactions.enabled", false)
//                .setFeatureFlag("recording.enabled", false)
//                .setFeatureFlag("resolution", "set")
//                .setFeatureFlag("unsaferoomwarning.enabled", false)
//                .setFeatureFlag("meeting-password.enabled", false)
//                .build();
//        // Launch the new activity with the given options. The launch() method takes care
        // of creating the required Intent and passing the options.
//        JitsiMeetActivity.launch(this, options);

    }

    void declineCall() {
        Cache.endCallInProgress();
        // Send message to server that the call is declined.
        if (mTopic != null) {
            mTopic.videoCallHangUp(mSeq);
        }
        finish();
    }

    void finishCall() {
        finish();
    }

    void showFragment(String tag, Bundle args) {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        FragmentManager fm = getSupportFragmentManager();

        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment == null) {
            switch (tag) {
                case FRAGMENT_INCOMING:
                    fragment = new IncomingCallFragment();
                    break;
                case FRAGMENT_ACTIVE:
                    fragment = new CallFragment();
                    break;
                default:
                    throw new IllegalArgumentException("Failed to create fragment: unknown tag " + tag);
            }
        } else if (args == null) {
            // Retain old arguments.
            args = fragment.getArguments();
        }

        args = args != null ? args : new Bundle();
        args.putString(Const.INTENT_EXTRA_TOPIC, mTopicName);
        args.putInt(Const.INTENT_EXTRA_SEQ, mSeq);

        if (fragment.getArguments() != null) {
            fragment.getArguments().putAll(args);
        } else {
            fragment.setArguments(args);
        }

        FragmentTransaction trx = fm.beginTransaction();
        if (!fragment.isAdded()) {
            trx = trx.replace(R.id.contentFragment, fragment, tag)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        } else if (!fragment.isVisible()) {
            trx = trx.show(fragment);
        }

        if (!trx.isEmpty()) {
            trx.commit();
        }
    }

    private void topicAttach() {
        if (mTopic.isAttached()) {
            mTopic.videoCallRinging(mSeq);
            return;
        }

        if (!mTinode.isAuthenticated()) {
            // If connection is not ready, wait for completion. This method will be called again
            // from the onLogin callback;
            Cache.getTinode().reconnectNow(true, false, false);
            return;
        }

        Topic.MetaGetBuilder builder = mTopic.getMetaGetBuilder()
                .withDesc()
                .withLaterData()
                .withDel();

        mTopic.subscribe(null, builder.build())
                .thenApply(new PromisedReply.SuccessListener<ServerMessage>() {
                    @Override
                    public PromisedReply<ServerMessage> onSuccess(ServerMessage result) {
                        mTopic.videoCallRinging(mSeq);
                        return null;
                    }
                })
                .thenCatch(new PromisedReply.FailureListener<ServerMessage>() {
                    @Override
                    public PromisedReply<ServerMessage> onFailure(Exception err) {
                        if (err instanceof AlreadySubscribedException) {
                            mTopic.videoCallRinging(mSeq);
                        } else {
                            Log.w(TAG, "Subscribe failed", err);
                            declineCall();
                        }
                        return null;
                    }
                });
    }

    private class EventListener implements Tinode.EventListener {
        @Override
        public void onLogin(int code, String txt) {
            if (code < ServerMessage.STATUS_MULTIPLE_CHOICES) {
                topicAttach();
            } else {
                declineCall();
            }
        }
    }
}
