package com.atguigu.myapplicationtest.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.myapplicationtest.R;
import com.atguigu.myapplicationtest.View.VitamioVideoView;
import com.atguigu.myapplicationtest.domain.MediaItem;
import com.atguigu.myapplicationtest.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;

/**
 * Created by My on 2017/5/22.
 */

public class VitamioVideoPlayerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PROGRESS = 0;
    private static final int HIDE_MEDIACONTROLLER = 1;
    private static final int SHOW_NET_SPEED = 2;

    private static final int DEFUALT_SCREEN = 0;
    private static final int FULL_SCREEN = 1;

    private VitamioVideoView vv;
    private Uri uri;
    private ArrayList<MediaItem> mediaItems;
    ;

    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystemTime;
    private Button btnVoice;
    private SeekBar seekbarVoice;
    private Button btnSwitchPlayer;
    private LinearLayout llBottom;
    private TextView tvCurrentTime;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnPre;
    private Button btnStartPause;
    private Button btnNext;
    private Button btnSwitchScreen;
    private LinearLayout ll_buffering;
    private LinearLayout ll_loading;
    private TextView tv_loading_net_speed;
    private TextView tv_net_speed;
    private Utils utils;
    private MyBroadCastReceiver receiver;
    private int position;
    private GestureDetector detector;

    private boolean isFullScreen = false;
    private int screenHeight;
    private int screenWidth;
    private int videoWidth;
    private int videoHeight;

    private int currentVoice;
    private AudioManager am;
    private int maxVoice;
    private boolean isMute = false;
    private boolean isNetUri = true;

    private void findViews() {
        Vitamio.isInitialized(getApplicationContext());
        setContentView(R.layout.activity_vitamio_video_player);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystemTime = (TextView) findViewById(R.id.tv_system_time);
        btnVoice = (Button) findViewById(R.id.btn_voice);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        btnSwitchPlayer = (Button) findViewById(R.id.btn_switch_player);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btnExit = (Button) findViewById(R.id.btn_exit);
        btnPre = (Button) findViewById(R.id.btn_pre);
        btnStartPause = (Button) findViewById(R.id.btn_start_pause);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnSwitchScreen = (Button) findViewById(R.id.btn_switch_screen);
        vv = (VitamioVideoView) findViewById(R.id.vv);
        ll_buffering = (LinearLayout) findViewById(R.id.ll_buffering);
        tv_net_speed = (TextView) findViewById(R.id.tv_net_speed);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        tv_loading_net_speed = (TextView) findViewById(R.id.tv_loading_net_speed);
        btnVoice.setOnClickListener(this);
        btnSwitchPlayer.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnPre.setOnClickListener(this);
        btnStartPause.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnSwitchScreen.setOnClickListener(this);

        seekbarVoice.setMax(maxVoice);
        seekbarVoice.setProgress(currentVoice);

        handler.sendEmptyMessage(SHOW_NET_SPEED);
    }


    @Override
    public void onClick(View v) {
        if (v == btnVoice) {
            isMute = !isMute;

            updateVoice(isMute);


        } else if (v == btnSwitchPlayer) {
            switchPlayer();
        } else if (v == btnExit) {
            finish();
        } else if (v == btnPre) {
            setPreVideo();
        } else if (v == btnStartPause) {
            setStartOrPause();
        } else if (v == btnNext) {
            setNextVideo();
        } else if (v == btnSwitchScreen) {
            if (isFullScreen) {
                setVideoType(DEFUALT_SCREEN);
            } else {
                setVideoType(FULL_SCREEN);
            }
        }

        handler.removeMessages(HIDE_MEDIACONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
    }

    private void switchPlayer() {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("如果当前为万能播放器播放，当播放有色块，播放质量不好，请切换到系统播放器播放")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       // startSystemPlayer();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void updateVoice(boolean isMute) {
        if (isMute) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            seekbarVoice.setProgress(0);
        } else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVoice, 0);
            seekbarVoice.setProgress(currentVoice);
        }
    }

    private void setVideoType(int videoType) {
        switch (videoType) {
            case FULL_SCREEN:
                isFullScreen = true;
                btnSwitchScreen.setBackgroundResource(R.drawable.btn_switch_screen_default_selector);
                vv.setVideoSize(screenWidth, screenHeight);

                break;
            case DEFUALT_SCREEN:
                isFullScreen = false;
                btnSwitchScreen.setBackgroundResource(R.drawable.btn_switch_screen_full_selector);
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;

                int width = screenWidth;
                int height = screenHeight;

                if (mVideoWidth * height < width * mVideoHeight) {
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    height = width * mVideoHeight / mVideoWidth;
                }
                vv.setVideoSize(width, height);

                break;
        }
    }

    private void setStartOrPause() {
        if (vv.isPlaying()) {
            vv.pause();
            btnStartPause.setBackgroundResource(R.drawable.btn_start_selector);
        } else {
            vv.start();
            btnStartPause.setBackgroundResource(R.drawable.btn_pause_selector);
        }
    }

    private int preCurrentPosition;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_NET_SPEED:
                    if (isNetUri) {
                        String netSpeed = utils.getNetSpeed(VitamioVideoPlayerActivity.this);
                        tv_loading_net_speed.setText("正在加载中...." + netSpeed);
                        tv_net_speed.setText("正在缓冲...." + netSpeed);
                        sendEmptyMessageDelayed(SHOW_NET_SPEED, 1000);
                    }
                    break;
                case PROGRESS:
                    int currentPosition = (int) vv.getCurrentPosition();
                    seekbarVideo.setProgress(currentPosition);

                    tvCurrentTime.setText(utils.stringForTime(currentPosition));

                    tvSystemTime.setText(getSystemTime());

                    if (isNetUri) {
                        int bufferPercentage = vv.getBufferPercentage();
                        int totalBuffer = bufferPercentage * seekbarVideo.getMax();
                        int secondaryProgress = totalBuffer / 100;
                        seekbarVideo.setSecondaryProgress(secondaryProgress);
                    } else {
                        seekbarVideo.setSecondaryProgress(0);
                    }

                    if (isNetUri && vv.isPlaying()) {

                        int duration = currentPosition - preCurrentPosition;
                        if (duration < 500) {
                            ll_buffering.setVisibility(View.VISIBLE);
                        } else {
                            ll_buffering.setVisibility(View.GONE);
                        }

                        preCurrentPosition = currentPosition;
                    }

                    sendEmptyMessageDelayed(PROGRESS, 1000);

                    break;
                case HIDE_MEDIACONTROLLER:
                    hideMediaController();
                    break;
            }
        }
    };

    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();

        findViews();
        getData();

        setListener();
        setData();


    }

    private void setData() {

        if (mediaItems != null && mediaItems.size() > 0) {

            MediaItem mediaItem = mediaItems.get(position);
            tvName.setText(mediaItem.getName());
            vv.setVideoPath(mediaItem.getData());
            isNetUri = utils.isNetUri(mediaItem.getData());

        } else if (uri != null) {
            vv.setVideoURI(uri);
            tvName.setText(uri.toString());
            isNetUri = utils.isNetUri(uri.toString());
        }

        setButtonStatus();

    }

    private void getData() {
        uri = getIntent().getData();
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        position = getIntent().getIntExtra("position", 0);

    }

    private void initData() {
        utils = new Utils();

        receiver = new MyBroadCastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, intentFilter);


        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                setStartOrPause();
                super.onLongPress(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (isFullScreen) {
                    setVideoType(DEFUALT_SCREEN);
                } else {
                    setVideoType(FULL_SCREEN);
                }
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (isShowMediaController) {
                    hideMediaController();
                    handler.removeMessages(HIDE_MEDIACONTROLLER);
                } else {
                    showMediaController();
                    handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
                }
                return super.onSingleTapConfirmed(e);
            }
        });


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;

        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

    }

    private float dowY;
    private int mVol;
    private float touchRang;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dowY = event.getY();
                mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                touchRang = Math.min(screenHeight, screenWidth);//screenHeight
                handler.removeMessages(HIDE_MEDIACONTROLLER);
                break;
            case MotionEvent.ACTION_MOVE:
                float endY = event.getY();
                float distanceY = dowY - endY;
                float delta = (distanceY / touchRang) * maxVoice;


                if (delta != 0) {
                    int mVoice = (int) Math.min(Math.max(mVol + delta, 0), maxVoice);

                    updateVoiceProgress(mVoice);
                }


                break;
            case MotionEvent.ACTION_UP:
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean isShowMediaController = false;

    private void hideMediaController() {
        llBottom.setVisibility(View.INVISIBLE);
        llTop.setVisibility(View.GONE);
        isShowMediaController = false;
    }

    public void showMediaController() {
        llBottom.setVisibility(View.VISIBLE);
        llTop.setVisibility(View.VISIBLE);
        isShowMediaController = true;
    }


    class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            setBatteryView(level);

        }
    }

    private void setBatteryView(int level) {
        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    private void setListener() {
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoWidth = mp.getVideoWidth();
                videoHeight = mp.getVideoHeight();
                int duration = (int) vv.getDuration();
                seekbarVideo.setMax(duration);
                tvDuration.setText(utils.stringForTime(duration));
                vv.start();

                handler.sendEmptyMessage(PROGRESS);

                ll_loading.setVisibility(View.GONE);

                hideMediaController();

                setVideoType(DEFUALT_SCREEN);

            }
        });

        vv.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                showErrorDialog();
                return true;
            }
        });

        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                setNextVideo();
            }
        });

        seekbarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    vv.seekTo(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeMessages(HIDE_MEDIACONTROLLER);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
            }
        });

        seekbarVoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateVoiceProgress(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void showErrorDialog() {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("当前视频不可播放，请检查网络或者视频文件是否有损坏！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

  /*  private void startSystemPlayer() {
        if(vv != null){
            vv.stopPlayback();
        }
        Intent intent = new Intent(this, SystemVideoPlayerActivity.class);
        if(mediaItems != null && mediaItems.size() >0){
            Bundle bunlder = new Bundle();
            bunlder.putSerializable("videolist",mediaItems);
            intent.putExtra("position",position);
            intent.putExtras(bunlder);
        }else if(uri != null){
            intent.setData(uri);
        }
        startActivity(intent);
        finish();
    }*/

    private void updateVoiceProgress(int progress) {
        currentVoice = progress;
        am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVoice, 0);
        seekbarVoice.setProgress(currentVoice);
        if (currentVoice <= 0) {
            isMute = true;
        } else {
            isMute = false;
        }

    }

    private void setPreVideo() {
        position--;
        if (position > 0) {
            MediaItem mediaItem = mediaItems.get(position);
            isNetUri = utils.isNetUri(mediaItem.getData());
            ll_loading.setVisibility(View.VISIBLE);
            vv.setVideoPath(mediaItem.getData());
            tvName.setText(mediaItem.getName());

            setButtonStatus();

        }

    }

    private void setNextVideo() {
        position++;
        if (position < mediaItems.size()) {
            MediaItem mediaItem = mediaItems.get(position);
            isNetUri = utils.isNetUri(mediaItem.getData());
            ll_loading.setVisibility(View.VISIBLE);

            vv.setVideoPath(mediaItem.getData());
            tvName.setText(mediaItem.getName());

            setButtonStatus();


        } else {
            Toast.makeText(this, "退出播放器", Toast.LENGTH_SHORT).show();
            finish();


        }

    }

    private void setButtonStatus() {
        if (mediaItems != null && mediaItems.size() > 0) {
            setEnable(true);

            if (position == 0) {
                btnPre.setBackgroundResource(R.drawable.btn_pre_gray);
                btnPre.setEnabled(false);
            }

            if (position == mediaItems.size() - 1) {
                btnNext.setBackgroundResource(R.drawable.btn_next_gray);
                btnNext.setEnabled(false);
            }

        } else if (uri != null) {
            setEnable(false);
        }
    }

    private void setEnable(boolean b) {
        if (b) {
            btnPre.setBackgroundResource(R.drawable.btn_pre_selector);
            btnNext.setBackgroundResource(R.drawable.btn_next_selector);
        } else {
            btnPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnNext.setBackgroundResource(R.drawable.btn_next_gray);
        }
        btnPre.setEnabled(b);
        btnNext.setEnabled(b);
    }

    @Override
    protected void onDestroy() {

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }

        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }

        super.onDestroy();


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            currentVoice--;
            if(currentVoice < 0) {
                currentVoice = 0;
            }
            updateVoiceProgress(currentVoice);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            currentVoice++;
            if(currentVoice > maxVoice) {
                currentVoice = maxVoice;
            }
            updateVoiceProgress(currentVoice);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
