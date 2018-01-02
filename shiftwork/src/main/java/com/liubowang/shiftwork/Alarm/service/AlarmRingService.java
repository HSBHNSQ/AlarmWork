package com.liubowang.shiftwork.Alarm.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;


import com.liubowang.shiftwork.Alarm.utils.ConsUtils;
import com.liubowang.shiftwork.Util.SWConst;
import com.liubowang.shiftwork.Util.SharedPreferencesUtil;

import java.io.File;
import java.io.IOException;

public class AlarmRingService extends Service {
    private String Song;
    private MediaPlayer mPlayer;
    private Vibrator mVibrator;
    private int currentVolume = -1;

    public AlarmRingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Song=intent.getStringExtra("resid");
        if (Song==null){
            Song="everybody.mp3";
        }
        if (Song.length() == 0){
            Song="everybody.mp3";
        }
        if(Song.contains("/")){
            //说明是自定义铃声
            File songFile = new File(Song);
            if (!songFile.exists()){
                Song="everybody.mp3";
            }
        }
        ringTheAlarm(Song);
        return super.onStartCommand(intent, flags, startId);
    }

    private void ringTheAlarm(String song) {
        AssetFileDescriptor assetFileDescriptor= null;
        try {
            int progress = SharedPreferencesUtil.getInt(this, SWConst.ALARM_VOLUME, 100);
            AudioManager mAudioManager = (AudioManager) this.getSystemService(this.AUDIO_SERVICE);
            int maxVolume= mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
            currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
            int setVolume=(maxVolume*progress)/100;
            mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, setVolume, 0);
            mPlayer=new MediaPlayer();
            mPlayer.reset();
            mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            if(song.contains("/")){
                //说明是自定义铃声
                mPlayer.setDataSource(song);
            }else{
                assetFileDescriptor = this.getAssets().openFd(song);
                mPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
                        assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            }
            mPlayer.setVolume(1f, 1f);
            mPlayer.setLooping(true);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopTheAlarm(){
        if(mPlayer!=null){
            AudioManager mAudioManager = (AudioManager) this.getSystemService(this.AUDIO_SERVICE);
            if (currentVolume == -1) {
                currentVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
            }
            mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, currentVolume, 0);
            mPlayer.stop();
            mPlayer.release();
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        if(SharedPreferencesUtil.getBoolean(this, SWConst.VIBRATE_SWITCH,false)){
            startVibrate();
        }
    }

    private void stopVibrate() {
        if(mVibrator!=null){
            mVibrator.cancel();
        }
    }

    private void startVibrate() {
        mVibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        if(mVibrator.hasVibrator()){
            mVibrator.vibrate(new long[]{500, 1500, 500, 1500}, 0);//off on off on
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTheAlarm();
        stopVibrate();
        Log.d("alarm", "铃声关闭");
    }

}
