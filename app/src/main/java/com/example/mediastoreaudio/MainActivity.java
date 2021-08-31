package com.example.mediastoreaudio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    MediaPlayer player;
    int position = 0; // 다시 시작 기능을 위한 현재 재생 위치 확인 변수

    AudioRecord record = null;
    AudioTrack track = null;

    boolean isRecording;
    int sampleRate = 44100;

    Button startRecord, stopRecord, playRecord = null;

    String fileName; //Obtained by intent
    MediaRecorder audioRecorder;
    Uri audiouri;
    ParcelFileDescriptor file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionCheck();

        startRecord = (Button) findViewById(R.id.start_recording);
        stopRecord = (Button) findViewById(R.id.stop_recording);
        playRecord = (Button) findViewById(R.id.play_recording);

        startRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordAudio();
            }
        });
        stopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAudio();
            }
        });
        playRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio();
            }
        });

    }
    private void recordAudio() {

        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, "ttt.mp3");
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/Recordings/");

        audiouri = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
        try {
            file = getContentResolver().openFileDescriptor(audiouri, "w");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (file != null) {
            audioRecorder = new MediaRecorder();
            audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            audioRecorder.setOutputFile(file.getFileDescriptor());
            audioRecorder.setAudioChannels(1);
            try {
                audioRecorder.prepare();
                audioRecorder.start();
                Toast.makeText(this, "녹음 시작됨.", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void stopAudio() {
        if (audioRecorder != null) {
            audioRecorder.stop();
            audioRecorder.release();
            audioRecorder = null;
            Toast.makeText(this, "녹음 중지됨.", Toast.LENGTH_SHORT).show();
        }
    }

    private void playAudio() {
        try {
            closePlayer();

            player = new MediaPlayer();
            player.setDataSource(file.getFileDescriptor());
            player.prepare();
            player.start();

            Toast.makeText(this, "재생 시작됨.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    public void permissionCheck(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1);
        }
    }


}