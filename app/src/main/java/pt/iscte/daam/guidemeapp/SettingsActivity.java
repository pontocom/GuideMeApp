package pt.iscte.daam.guidemeapp;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    protected SeekBar sbRange;
    protected TextView tvRange;
    protected EditText etApiURL;
    protected ProgressBar pbSave;
    protected SharedPreferences appdata;

    protected String defaultURL = "http://10.211.55.10:3000";
    protected int mRange;
    protected String mURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        appdata = getSharedPreferences("GuideMeAppData",0);
        mRange = appdata.getInt("RANGE", 50);
        mURL = appdata.getString("URL", defaultURL);

        sbRange = (SeekBar) findViewById(R.id.sBRange);
        tvRange = (TextView) findViewById(R.id.tvRange);
        etApiURL = (EditText) findViewById(R.id.etAPIURL);
        pbSave = (ProgressBar) findViewById(R.id.pBSave);

        sbRange.setProgress(mRange);
        tvRange.setText(mRange + " Km");
        etApiURL.setText(mURL);

        sbRange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvRange.setText(i + " Km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mRange = seekBar.getProgress();
            }
        });
    }

    public void clickSaveSettings(View v) {
        pbSave.setVisibility(ProgressBar.VISIBLE);

        defaultURL = etApiURL.getText().toString();

        SharedPreferences.Editor editor = appdata.edit();
        editor.putInt("RANGE", mRange);
        editor.putString("URL", defaultURL);
        editor.commit();

        pbSave.setVisibility(ProgressBar.INVISIBLE);

        finish();
    }

    public void clickCancel(View v) {
        finish();
    }
}
