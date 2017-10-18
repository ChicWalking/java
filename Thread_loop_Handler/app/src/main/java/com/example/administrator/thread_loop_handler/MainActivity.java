package com.example.administrator.thread_loop_handler;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.MessageQueue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
/**
 * @author wxb
 * Android中的消息处理实例之一
 * * 一、在主线程内发送消息
 * 1.使用post
 * 2.使用postDelay
 * 3.使用sendMessage
 * 4.使用Message.sentToTarget
 * 二、在子线程中使用Handler
 * 1.使用post
 * 2.使用postDelay
 * 3.使用sendMessage
 * 4.使用Message.sentToTarget
 */

//第一个例子描述了如何在多个线程中发送消息，并在主线程中统一接收和处理这些消息
public class MainActivity extends Activity {
    private Runnable runnable=null;
    private Runnable runnableDelay=null;
    private Runnable runnableInThread=null;
    private Runnable runnableDelayInThread=null;
    private static TextView tv;
    private static TextView tvOnOtherThread;

    //自定义Message类型
    public final static int MESSAGE_WXB_1 = 1;
    public final static int MESSAGE_WXB_2 = 2;
    public final static int MESSAGE_WXB_3 = 3;
    public final static int MESSAGE_WXB_4 = 4;
    public final static int MESSAGE_WXB_5 = 5;

    private static Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case MESSAGE_WXB_1:
                    tv.setText("invoke sendMessage in main thread");
                    break;
                case MESSAGE_WXB_2:
                    tv.setText("Message.sendToTarget in main thread");
                    break;
                case MESSAGE_WXB_3:
                    tvOnOtherThread.setText("invoke sendMessage in other thread");
                    break;
                case MESSAGE_WXB_4:
                    tvOnOtherThread.setText("Message.sendToTarget in other thread");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) this.findViewById(R.id.tvOnMainThread);
        tvOnOtherThread = (TextView) this.findViewById(R.id.tvOnOtherThread);

        //方法1.post
        runnable = new Runnable(){
            public void run(){
                tv.setText(getString(R.string.postRunnable));
            }
        };
        Button handler_post = (Button) this.findViewById(R.id.btnHandlerpost);
        handler_post.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.post(runnable);
            }
        });

        //方法2：postDelay
        runnableDelay = new Runnable(){
            public void run(){
                tv.setText(getString(R.string.postRunnableDelay));
            }
        };

        Button handler_post_delay = (Button) this.findViewById(R.id.btnHandlerPostdelay);
        handler_post_delay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.postDelayed(runnableDelay, 1000);  //1秒后执行
            }
        });

        //方法3：sendMessage
        Button btnSendMessage = (Button) this.findViewById(R.id.btnSendMessage);
        btnSendMessage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Message msg = mHandler.obtainMessage();
                msg.what = MESSAGE_WXB_1;
                mHandler.sendMessage(msg);
            }
        });

        //方法4：Message.sendToTarget
        Button btnSendtoTarget = (Button) this.findViewById(R.id.btnSendtoTarget);
        btnSendtoTarget.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Message msg = mHandler.obtainMessage();
                msg.what = MESSAGE_WXB_2;
                msg.sendToTarget();
            }
        });

        //在其他线程中发送消息
        //1.post
        runnableInThread = new Runnable(){
            public void run(){
                tvOnOtherThread.setText(getString(R.string.postRunnableInThread));
            }
        };

        Button btnPost = (Button) this.findViewById(R.id.btnPost);
        btnPost.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        mHandler.post(runnableInThread);
                    }
                }.start();
            }
        });

        //2.postDelay
        runnableDelayInThread = new Runnable(){
            public void run(){
                tvOnOtherThread.setText(getString(R.string.postRunnableDelayInThread));
            }
        };

        Button btnPostDelay = (Button) this.findViewById(R.id.btnPostDelay);
        btnPostDelay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        mHandler.postDelayed(runnableDelayInThread, 1000);
                    }
                }.start();
            }
        });

        //3.sendMessage
        Button btnSendMessage2 = (Button) this.findViewById(R.id.btnSendMessage2);
        btnSendMessage2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        Message msg = mHandler.obtainMessage();
                        msg.what = MESSAGE_WXB_3;
                        mHandler.sendMessage(msg);
                    }
                }.start();
            }
        });

        //方法4：Message.sendToTarget
        Button btnSendToTarget2 = (Button) this.findViewById(R.id.btnSendToTarget2);
        btnSendToTarget2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        mHandler.obtainMessage(MESSAGE_WXB_4).sendToTarget();
                    }
                }.start();
            }
        });
    }
}

//第二个例子则描述如何在子线程中建立一个消息循环，并从主线程发送消息给子线程，让子线程处理这些消息。
//public class MainActivity extends Activity {
//    private static TextView tv = null;
//
//    //自定义Message类型
//    public final static int MESSAGE_WXB_1 = 1;
//
//    //主线程中创建Handler
//    private static Handler mHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            switch(msg.what){
//                case MESSAGE_WXB_1:
//                    tv.setText("主线程发送，子线程接收消息后回发，主线程修改UI");
//                    break;
//            }
//            super.handleMessage(msg);
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        tv = (TextView) this.findViewById(R.id.textView1);
//
//        //创建子线程
//        new LooperThread().start();
//
//        //点击按钮向子线程发送消息
//        Button btn = (Button) this.findViewById(R.id.btnSendMessage);
//        btn.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                LooperThread.sHandler.sendEmptyMessage(MESSAGE_WXB_1);
//            }
//        });
//    }
//
//    //定义子线程
//    static class LooperThread extends Thread {
//        public static Handler sHandler = null;
//
//        public void run() {
//            //创建消息循环
//            Looper.prepare();
//
//            sHandler = new Handler() {
//                public void handleMessage(Message msg) {
//                    switch(msg.what){
//                        case MESSAGE_WXB_1:
//                            mHandler.sendEmptyMessage(MESSAGE_WXB_1);
//                            break;
//                    }
//                }
//            };
//            //开启消息循环
//            Looper.loop();
//        }
//    }
//}

