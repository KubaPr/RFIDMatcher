package com.kpro.rfidmatcher;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static String TAG = MainActivity.class.getSimpleName();

    private NfcAdapter nfcAdapter;
    private TagPair tagPair;

    private TextView firstTextView;
    private TextView secondTextView;
    private TextView pairedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        tagPair = new TagPair();

        firstTextView = (TextView) findViewById(R.id.first);
        secondTextView = (TextView) findViewById(R.id.second);
        pairedTextView = (TextView) findViewById(R.id.paired);

        Button resetButton = (Button) findViewById(R.id.reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagPair.setFirstTag(null);
                tagPair.setSecondTag(null);
                firstTextView.setText(null);
                secondTextView.setText(null);
                pairedTextView.setText(null);
            }
        });

        handleIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupForegroundDispatch(this, nfcAdapter);
    }

    @Override
    protected void onPause() {
        stopForegroundDispatch(this, nfcAdapter);
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (isNfcAction(intent)) {
            final BetterTag tag = new BetterTag((Tag) intent.getExtras().get(NfcAdapter.EXTRA_TAG));
            if (tagPair.getFirstTag() == null) {
                tagPair.setFirstTag(tag.getId());
                firstTextView.setText("Pierwsza etykieta:\n" + tagPair.getFirstTag());
            } else if (tagPair.getFirstTag().equals(tag.getId())) {
                new AlertDialog.Builder(this)
                        .setMessage("Nie można sparować etykiety samej ze sobą.")
                        .show();
            } else {
                new AlertDialog.Builder(this)
                        .setMessage("Wykryto etykietę: " + tag.getId() + ". Czy chcesz ją sparować z pierwszą etykietą?")
                        .setPositiveButton("TAK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tagPair.setSecondTag(tag.getId());
                                firstTextView.setText(null);
                                secondTextView.setText(null);

                                StringBuilder sb = new StringBuilder();
                                sb.append("Sparowane etykiety:\n");
                                sb.append("\t");
                                sb.append("1. ");
                                sb.append(tagPair.getFirstTag());
                                sb.append("\n");
                                sb.append("\t");
                                sb.append("2. ");
                                sb.append(tagPair.getSecondTag());
                                pairedTextView.setText(sb.toString());
                            }
                        })
                        .show();
            }
        }
    }

    public static void setupForegroundDispatch(final AppCompatActivity activity, NfcAdapter adapter) {
        Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);
        adapter.enableForegroundDispatch(activity, pendingIntent, null, null);
    }

    public static void stopForegroundDispatch(final AppCompatActivity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    private boolean isNfcAction(Intent intent) {
        return NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()) ||
                NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction());
    }

}
