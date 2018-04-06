package com.example.arturmusayelyan.notificationsheduler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.network_options)
    RadioGroup networkOptions;
    Button defaultButton;
    private boolean buttonDefaultCheck;
    private JobScheduler myScheduler;
    private static int JOB_ID = 0;
    private Switch deviceIdle, deviceCharging,periodicSwitch;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
        //defaultButton=findViewById(R.id.defaultButton);
        buttonDefaultCheck = false;


        for (int i = 0; i < networkOptions.getChildCount(); i++) {
            ((RadioButton) networkOptions.getChildAt(i)).setOnCheckedChangeListener(onCheckedChangeListener);
        }
    }

    private void init() {
        deviceIdle = findViewById(R.id.idle_switch);
        deviceCharging = findViewById(R.id.charging_switch);
        periodicSwitch=findViewById(R.id.periodic_switch);
        seekBar = findViewById(R.id.seek_bar);

        final TextView label = findViewById(R.id.seek_bar_label);
        final TextView seekBarProgress = findViewById(R.id.seek_bar_progress);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress > 0) {
                    seekBarProgress.setText(String.valueOf(progress) + " s");
                } else {
                    seekBarProgress.setText("Not Set");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        periodicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    label.setText("Periodic interval");
                }
                else {
                    label.setText("Override deadline");
                }
            }
        });
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.isPressed()) {
                buttonDefaultCheck = true;
                if (isChecked) {
                    Toast.makeText(MainActivity.this, buttonView.getText(), Toast.LENGTH_SHORT).show();
                    if (!buttonDefaultCheck) {
                        ((RadioButton) defaultButton).setChecked(false);
                    }
                }
            }
        }
    };

    public void scheduleJob(View view) {
        int selectedNetworkId = networkOptions.getCheckedRadioButtonId();

        int selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;

        switch (selectedNetworkId) {
            case R.id.no_network:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
                break;
            case R.id.any_network:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY;
                break;
            case R.id.wifi_network:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_UNMETERED;
                break;
        }

        int seekBarInteger = seekBar.getProgress();
        boolean seekBarSet = seekBarInteger > 0;

        myScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName serviceName = new ComponentName(getPackageName(), NotificationJobService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceName);
//                .setPeriodic(3000)
        builder.setRequiredNetworkType(selectedNetworkOption);
        builder.setRequiresDeviceIdle(deviceIdle.isChecked());
        builder.setRequiresCharging(deviceCharging.isChecked());

        if(periodicSwitch.isChecked()){
            if(seekBarSet){
                builder.setPeriodic(seekBarInteger*1000);
            }
            else {
                Toast.makeText(this,"Please set a periodic interval",Toast.LENGTH_SHORT).show();
            }
        }
        else{
            builder.setOverrideDeadline(seekBarInteger*1000);
        }


        boolean constraintSet = (selectedNetworkOption != JobInfo.NETWORK_TYPE_NONE) || deviceIdle.isChecked() || deviceCharging.isChecked() ||seekBarSet;
        if (constraintSet) {
            JobInfo myJobInfo = builder.build();
            myScheduler.schedule(myJobInfo);
            Toast.makeText(this, "Job is scheduled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Switch the internet or check the requires", Toast.LENGTH_SHORT).show();
        }

    }

    public void cancelJobs(View view) {
        if (myScheduler != null) {
            myScheduler.cancelAll();
            myScheduler = null;
            Toast.makeText(this, "Jobs Canceled", Toast.LENGTH_SHORT).show();
        }

    }

}
