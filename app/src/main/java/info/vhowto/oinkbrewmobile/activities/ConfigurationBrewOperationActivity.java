package info.vhowto.oinkbrewmobile.activities;

import android.content.DialogInterface;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.materialdrawer.Drawer;

import info.vhowto.oinkbrewmobile.R;
import info.vhowto.oinkbrewmobile.domain.Configuration;
import info.vhowto.oinkbrewmobile.domain.Log;
import info.vhowto.oinkbrewmobile.domain.TimerButtonState;
import info.vhowto.oinkbrewmobile.remote.ConfigurationRequest;
import info.vhowto.oinkbrewmobile.remote.LogRequest;
import info.vhowto.oinkbrewmobile.remote.RequestObjectCallback;

public class ConfigurationBrewOperationActivity extends AppCompatActivity implements RequestObjectCallback<Log> {

    private static long TIMER_PERIOD = 5000;

    private Configuration configuration;
    private Menu menu;
    private Drawer drawer;
    private TextView chrono;
    private CountDownTimer timer;
    private int limit = 1;
    private TimerButtonState timerButtonState = TimerButtonState.NOT_RUNNING;
    private ImageButton timerButton;
    private long timerMillisUntilFinished;
    private Ringtone ringtone;
    private Handler handler;
    private Runnable runnable;
    private int requestErrorCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_brew_operation);

        configuration = (Configuration)getIntent().getSerializableExtra("item");

        Toolbar toolbar = (Toolbar) findViewById(R.id.configuration_brew_operation_toolbar);
        toolbar.setTitle(configuration.name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chrono = (TextView) findViewById(R.id.configuration_timer);
        chrono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTimerClick();
            }
        });

        timerButton = (ImageButton) findViewById(R.id.configuration_timer_button);
        timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTimerButtonClick();
            }
        });
        timerButton.setColorFilter(Color.GRAY);


        if (!configuration.archived) {
            TextView targetCard = (TextView) findViewById(R.id.configuration_target);
            targetCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //adjustTargetTemperature();
                }
            });
        }

        //chart = configureChart();
        fetchLogData();

        if (!configuration.archived) {
            startTimer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fetchLogData();

        if (menu.findItem(R.id.action_refresh_automatically).isChecked() && !configuration.archived)
            startTimer();
    }

    private void fetchLogData() {
        LogRequest.getLogs(configuration.brewpi.device_id, configuration.pk, limit, this);
    }

    public void onRequestSuccessful() {
        // target temp change request successful;
        Toast.makeText(getApplicationContext(), R.string.configuration_target_update_success, Toast.LENGTH_LONG).show();
        requestErrorCount = 0;
    }

    public void onRequestSuccessful(Log item) {

    }

    public void onRequestFailure(int statusCode, String errorMessage) {
        switch (statusCode) {
            case 400:
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                requestErrorCount++;
                if (requestErrorCount < 5) {
                    ConfigurationRequest.update(configuration.clone(), this);
                }
            case 404:
                Toast.makeText(getApplicationContext(), getString(R.string.error_log_empty), Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void onTimerClick() {
        final ConfigurationBrewOperationActivity callback = this;
        View view = getLayoutInflater().inflate(R.layout.content_timer, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.action_set_timer);
        builder.setView(view);

        final NumberPicker minutes = (NumberPicker)view.findViewById(R.id.minutes);

        minutes.setMinValue(0);
        minutes.setMaxValue(120);
        minutes.setValue(60);

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                timerMillisUntilFinished = 0;
                createTimer(minutes.getValue() * 60 * 1000);
                timerButton.setColorFilter(getResources().getColor(R.color.material_orange_500));
            }
        });

        builder.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void onTimerButtonClick() {
        String currentTime = chrono.getText().toString();

        if (timer == null || (currentTime.equals("00:00") && timerButtonState != TimerButtonState.ALARM_ON)) {
            return;
        }

        switch (timerButtonState) {
            case NOT_RUNNING:
            case PAUSED:
                if (timerMillisUntilFinished > 0) {
                    createTimer(timerMillisUntilFinished);
                }
                timer.start();
                timerButton.setImageResource(R.drawable.pause_circle);
                timerButtonState = TimerButtonState.RUNNING;
                break;
            case RUNNING:
                timer.cancel();
                timerMillisUntilFinished = Integer.parseInt(currentTime.substring(0, 1)) * 60 * 1000;
                timerMillisUntilFinished += Integer.parseInt(currentTime.substring(3, 5)) * 1000;
                timerButton.setImageResource(R.drawable.play_circle);
                timerButtonState = TimerButtonState.PAUSED;
                break;
            case ALARM_ON:
                if (ringtone != null) {
                    ringtone.stop();
                }
                if (currentTime.equals("00:00")) {
                    timerButton.setImageResource(R.drawable.play_circle);
                    timerButton.setColorFilter(Color.GRAY);
                    timerButtonState = TimerButtonState.NOT_RUNNING;
                    timer = null;
                }
                else {
                    timerButton.setImageResource(R.drawable.pause_circle);
                    timerButtonState = TimerButtonState.RUNNING;
                }
                break;
        }
    }

    private void createTimer(long timeToElapse) {
        if (timer != null)
            timer.cancel();

        timer = new CountDownTimer(timeToElapse, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes=(millisUntilFinished/1000)/60;
                long seconds=(millisUntilFinished/1000)%60;
                chrono.setText(String.format("%02d:%02d", minutes, seconds));

                if ((minutes%10) == 0 && seconds == 0) {
                    if (ringtone != null)
                        ringtone.stop();

                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    ringtone.play();

                    timerButton.setImageResource(R.drawable.check_circle);
                    timerButtonState = TimerButtonState.ALARM_ON;
                }
            }

            @Override
            public void onFinish() {
                chrono.setText(String.format("%02d:%02d", 0, 0));

                timerButton.setImageResource(R.drawable.check_circle);
                timerButtonState = TimerButtonState.ALARM_ON;

                if (ringtone != null)
                    ringtone.stop();

                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
                ringtone.play();
            }
        };

        long minutes=(timeToElapse/1000)/60;
        long seconds=(timeToElapse/1000)%60;
        chrono.setText(String.format("%02d:%02d", minutes, seconds));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_configuration_brew_operation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startTimer() {
        if (handler == null) {
            handler = new Handler();
        }
        if (runnable == null) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    fetchLogData();
                    handler.postDelayed(this, TIMER_PERIOD);
                }
            };
        }
        handler.postDelayed(runnable, TIMER_PERIOD);
    }

    private void stopTimer() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
