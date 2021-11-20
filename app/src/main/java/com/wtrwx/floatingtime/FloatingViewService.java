package com.wtrwx.floatingtime;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class FloatingViewService extends Service {
    public static boolean isStarted = false;

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    private TextView timeView;

    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = 500;
        layoutParams.height = 128;
        layoutParams.x = 300;
        layoutParams.y = 300;
        updateTimeData();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showFloatingWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            timeView = new TextView(getApplicationContext());
            timeView.setLongClickable(true);
            timeView.setBackgroundColor(Color.WHITE);
            timeView.setTextSize(32);
            timeView.setWidth(500);
            timeView.setHeight(128);
            timeView.setGravity(1);
            windowManager.addView(timeView, layoutParams);
            timeView.setOnTouchListener(new FloatingOnTouchListener());
            timeView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    closeWindow();
                    return false;
                }
            });
        }
    }

    private void updateTimeData() {
        final Timer timer = new Timer();
        TimerTask task;
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                Bundle data = msg.getData();
                String val = data.getString("value");
                SpannableString ss = new SpannableString(val);
                ss.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new ForegroundColorSpan(Color.RED), 10, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                timeView.setText(ss);
                super.handleMessage(msg);
            }
        };

        task = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message msg = new Message();
                Bundle data = new Bundle();
                long result = 0;
                try {
                    result = TimeUtils.getCurrentNetworkTime();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String TimeStamp = String.valueOf(result);
                data.putString("value", TimeUtils.unix2String(TimeStamp));
                msg.setData(data);
                msg.what = 1;
                handler.sendMessage(msg);
            }
        };

        timer.schedule(task, 100, 100);
    }

    public void closeWindow() {
        isStarted = false;
        stopSelf();
        System.exit(0);
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                    windowManager.updateViewLayout(view, layoutParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }
}
