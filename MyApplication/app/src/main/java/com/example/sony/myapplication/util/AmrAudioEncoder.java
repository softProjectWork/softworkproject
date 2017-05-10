package com.example.sony.myapplication.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.io.OutputStream;

import android.app.Activity;
import android.media.MediaRecorder;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;
import android.widget.Toast;

//blog.csdn.net/zgyulongfei
//Email: zgyulongfei@gmail.com

public class AmrAudioEncoder {
    private static final String TAG = "ArmAudioEncoder";

    private static AmrAudioEncoder amrAudioEncoder = null;

    private Activity activity;

    private MediaRecorder audioRecorder;

    private boolean isAudioRecording;

    private LocalServerSocket lss;
    private LocalSocket sender, receiver;

    private Socket upSocket;

    private AmrAudioEncoder() {
    }

    public static AmrAudioEncoder getArmAudioEncoderInstance() {
        if (amrAudioEncoder == null) {
            synchronized (AmrAudioEncoder.class) {
                if (amrAudioEncoder == null) {
                    amrAudioEncoder = new AmrAudioEncoder();
                }
            }
        }
        return amrAudioEncoder;
    }

    public void initArmAudioEncoder(Activity activity, Socket upSock) {
        this.activity = activity;
        isAudioRecording = false;
        upSocket = upSock;
    }

    public void start() {
        if (activity == null) {
            showToastText("闊抽缂栫爜鍣ㄦ湭鍒濆鍖栵紝璇峰厛鎵цinit鏂规硶");
            return;
        }

        if (isAudioRecording) {
            showToastText("闊抽宸茬粡寮�濮嬬紪鐮侊紝鏃犻渶鍐嶆缂栫爜");
            return;
        }

        if (!initLocalSocket()) {
            showToastText("鏈湴鏈嶅姟寮�鍚け璐�");
            releaseAll();
            return;
        }

        if (!initAudioRecorder()) {
            showToastText("闊抽缂栫爜鍣ㄥ垵濮嬪寲澶辫触");
            releaseAll();
            return;
        }

        this.isAudioRecording = true;
        startAudioRecording();
    }

    private boolean initLocalSocket() {
        boolean ret = true;
        try {
            releaseLocalSocket();

            String serverName = "armAudioServer";
            final int bufSize = 1024;

            lss = new LocalServerSocket(serverName);

            receiver = new LocalSocket();
            receiver.connect(new LocalSocketAddress(serverName));
            receiver.setReceiveBufferSize(bufSize);
            receiver.setSendBufferSize(bufSize);

            sender = lss.accept();
            sender.setReceiveBufferSize(bufSize);
            sender.setSendBufferSize(bufSize);
        } catch (IOException e) {
            ret = false;
        }
        return ret;
    }

    private boolean initAudioRecorder() {
        if (audioRecorder != null) {
            audioRecorder.reset();
            audioRecorder.release();
        }
        audioRecorder = new MediaRecorder();
        audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        final int mono = 1;
        audioRecorder.setAudioChannels(mono);
        audioRecorder.setAudioSamplingRate(8000);
        audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        audioRecorder.setOutputFile(sender.getFileDescriptor());

        boolean ret = true;
        try {
            audioRecorder.prepare();
            audioRecorder.start();
        } catch (Exception e) {
            releaseMediaRecorder();
            showToastText("鎵嬫満涓嶆敮鎸佸綍闊虫鍔熻兘");
            ret = false;
        }
        return ret;
    }

    private void startAudioRecording() {
        new Thread(new AudioCaptureAndSendThread()).start();
    }

    public void stop() {
        if (isAudioRecording) {
            isAudioRecording = false;
        }
        releaseAll();
    }

    private void releaseAll() {
        releaseMediaRecorder();
        releaseLocalSocket();
        amrAudioEncoder = null;
    }

    private void releaseMediaRecorder() {
        try {
            if (audioRecorder == null) {
                return;
            }
            if (isAudioRecording) {
                audioRecorder.stop();
                isAudioRecording = false;
            }
            audioRecorder.reset();
            audioRecorder.release();
            audioRecorder = null;
        } catch (Exception err) {
            Log.d(TAG, err.toString());
        }
    }

    private void releaseLocalSocket() {
        try {
            if (sender != null) {
                sender.close();
            }
            if (receiver != null) {
                receiver.close();
            }
            if (lss != null) {
                lss.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        sender = null;
        receiver = null;
        lss = null;
    }

    private boolean isAudioRecording() {
        return isAudioRecording;
    }

    private void showToastText(String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    private class AudioCaptureAndSendThread implements Runnable {
        public void run() {
            try {
                sendAmrAudio();
            } catch (Exception e) {
                Log.e(TAG, "sendAmrAudio() 鍑洪敊");
            }
        }

        private void sendAmrAudio() throws Exception {
            DataInputStream dataInput = new DataInputStream(receiver.getInputStream());
            skipAmrHead(dataInput);

            final int SEND_FRAME_COUNT_ONE_TIME = 10;// 姣忔鍙戦��10甯х殑鏁版嵁锛�1甯уぇ绾�32B
            // AMR鏍煎紡瑙佸崥瀹細http://blog.csdn.net/dinggo/article/details/1966444
            final int BLOCK_SIZE[] = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0 };

            byte[] sendBuffer = new byte[1024];
            while (isAudioRecording()) {
                int offset = 0;
                for (int index = 0; index < SEND_FRAME_COUNT_ONE_TIME; ++index) {
                    if (!isAudioRecording()) {
                        break;
                    }
                    dataInput.read(sendBuffer, offset, 1);
                    int blockIndex = (int) (sendBuffer[offset] >> 3) & 0x0F;
                    int frameLength = BLOCK_SIZE[blockIndex];
                    readSomeData(sendBuffer, offset + 1, frameLength, dataInput);
                    offset += frameLength + 1;
                }
                upSend(upSocket, sendBuffer, offset);
            }
            upSocket.close();
            dataInput.close();
            releaseAll();
        }

        private void skipAmrHead(DataInputStream dataInput) {
            final byte[] AMR_HEAD = new byte[] { 0x23, 0x21, 0x41, 0x4D, 0x52, 0x0A };
            int result = -1;
            int state = 0;
            try {
                while (-1 != (result = dataInput.readByte())) {
                    if (AMR_HEAD[0] == result) {
                        state = (0 == state) ? 1 : 0;
                    } else if (AMR_HEAD[1] == result) {
                        state = (1 == state) ? 2 : 0;
                    } else if (AMR_HEAD[2] == result) {
                        state = (2 == state) ? 3 : 0;
                    } else if (AMR_HEAD[3] == result) {
                        state = (3 == state) ? 4 : 0;
                    } else if (AMR_HEAD[4] == result) {
                        state = (4 == state) ? 5 : 0;
                    } else if (AMR_HEAD[5] == result) {
                        state = (5 == state) ? 6 : 0;
                    }

                    if (6 == state) {
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "read mdat error...");
            }
        }

        private void readSomeData(byte[] buffer, int offset, int length, DataInputStream dataInput) {
            int numOfRead = -1;
            while (true) {
                try {
                    numOfRead = dataInput.read(buffer, offset, length);
                    if (numOfRead == -1) {
                        Log.d(TAG, "amr...no data get wait for data coming.....");
                        Thread.sleep(100);
                    } else {
                        offset += numOfRead;
                        length -= numOfRead;
                        if (length <= 0) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "amr..error readSomeData");
                    break;
                }
            }
        }

        private void upSend(Socket upSocket, byte[] buffer, int sendLength) {
            try {
                //InetAddress ip = InetAddress.getByName(CommonConfig.SERVER_IP_ADDRESS.trim());
                //int port = CommonConfig.AUDIO_SERVER_UP_PORT;

                byte[] sendBuffer = new byte[sendLength];
                System.arraycopy(buffer, 0, sendBuffer, 0, sendLength);

                //DatagramPacket packet = new DatagramPacket(sendBuffer, sendLength);
                //packet.setAddress(ip);
                //packet.setPort(port);
                OutputStream output = upSocket.getOutputStream();
                output.write(sendBuffer);
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

