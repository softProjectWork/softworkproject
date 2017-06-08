package com.example.sony.myapplication.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

//blog.csdn.net/zgyulongfei
//Email: zgyulongfei@gmail.com

public class AmrAudioPlayer {
    private static final String TAG = "AmrAudioPlayer";

    private static AmrAudioPlayer playerInstance = null;

    private long alreadyReadByteCount = 0;

    private MediaPlayer audioPlayer;
    //private Handler handler = new Handler();

    private final String cacheFileName = "audioCacheFile";
    private File cacheFile;
    private int cacheFileCount = 0;

    // 鐢ㄦ潵璁板綍鏄惁宸茬粡浠巆acheFile涓鍒舵暟鎹埌鍙︿竴涓猚ache涓�
    private boolean hasMovedTheCacheFlag;

    private boolean isPlaying;
    private Activity activity;

    private boolean isChaingCacheToAnother;
    private Socket downSocket;

    private AmrAudioPlayer() {
    }

    public static AmrAudioPlayer getAmrAudioPlayerInstance() {
        if (playerInstance == null) {
            synchronized (AmrAudioPlayer.class) {
                if (playerInstance == null) {
                    playerInstance = new AmrAudioPlayer();
                }
            }
        }
        return playerInstance;
    }

    public void initAmrAudioPlayer(Activity activity, Socket downSock) {
        this.activity = activity;
        this.downSocket = downSock;
        //Looper.prepare();
        //handler = new Handler();
        deleteExistCacheFile();
        initCacheFile();
    }

    private void deleteExistCacheFile() {
        File cacheDir = activity.getCacheDir();
        File[] needDeleteCacheFiles = cacheDir.listFiles();
        for (int index = 0; index < needDeleteCacheFiles.length; ++index) {
            File cache = needDeleteCacheFiles[index];
            if (cache.isFile()) {
                if (cache.getName().contains(cacheFileName.trim())) {
                    Log.e(TAG, "delete cache file: " + cache.getName());
                    cache.delete();
                }
            }
        }
        needDeleteCacheFiles = null;
    }

    private void initCacheFile() {
        cacheFile = null;
        cacheFile = new File(activity.getCacheDir(), cacheFileName);
    }

    public void start() {
        isPlaying = true;
        isChaingCacheToAnother = false;
        Log.d("audio player", "begin");
        setHasMovedTheCacheToAnotherCache(false);
        Log.d("audio player", "cached");
        new Thread(new NetAudioPlayerThread()).start();
    }

    public void stop() {
        isPlaying = false;
        isChaingCacheToAnother = false;
        setHasMovedTheCacheToAnotherCache(false);
        releaseAudioPlayer();
        deleteExistCacheFile();
        cacheFile = null;
        //handler = null;
    }

    private void releaseAudioPlayer() {
        playerInstance = null;
        if (audioPlayer != null) {
            try {
                if (audioPlayer.isPlaying()) {
                    audioPlayer.pause();
                }
                audioPlayer.release();
                audioPlayer = null;
            } catch (Exception e) {
            }
        }
    }

    private boolean hasMovedTheCacheToAnotherCache() {
        return hasMovedTheCacheFlag;
    }

    private void setHasMovedTheCacheToAnotherCache(boolean result) {
        hasMovedTheCacheFlag = result;
    }

    private class NetAudioPlayerThread implements Runnable {
        // 浠庢帴鍙楁暟鎹紑濮嬭绠楋紝褰撶紦瀛樺ぇ浜嶪NIT_BUFFER_SIZE鏃跺�欏紑濮嬫挱鏀�
        private final int INIT_AUDIO_BUFFER = 128;
        // 鍓�1绉掔殑鏃跺�欐挱鏀炬柊鐨勭紦瀛樼殑闊充箰
        private final int CHANGE_CACHE_TIME = 1000;

        public void run() {
            try {
                Socket socket = downSocket;
                Log.d("audio player", "play");
                receiveNetAudioThenPlay(socket);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage() + "浠庢湇鍔＄鎺ュ彈闊抽澶辫触銆傘�傘��");
            }
        }

        private void receiveNetAudioThenPlay(Socket socket) throws Exception {
            InputStream inputStream = socket.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(cacheFile);

            final int BUFFER_SIZE = 100 * 1024;// 100kb buffer size
            byte[] buffer = new byte[BUFFER_SIZE];
            Log.d("audio player", "");

            int testTime = 10;
            try {
                Log.d("audio player", "middle");
                alreadyReadByteCount = 0;
                while (isPlaying) {
                    Log.d("audio player", "read");
                    int numOfRead = inputStream.read(buffer);
                    Log.d("audio player", "readend + num = " + Integer.toString(numOfRead));
                    if (numOfRead <= 0) {
                        Log.d("audio recv", "success");
                        continue;
                        //break;
                    }
                    alreadyReadByteCount += numOfRead;
                    Log.d("audio player", "trans");
                    outputStream.write(buffer, 0, numOfRead);
                    outputStream.flush();
                    try {
                        if (testTime++ >= 1) {
                            Log.e(TAG, "cacheFile=" + cacheFile.length());
                            testWhetherToChangeCache();
                            testTime = 0;
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                    // 濡傛灉澶嶅埗浜嗘帴鏀剁綉缁滄祦鐨刢ache锛屽垯鎵ц姝ゆ搷浣�
                    if (hasMovedTheCacheToAnotherCache() && !isChaingCacheToAnother) {
                        // 灏嗘帴鏀剁綉缁滄祦鐨刢ache鍒犻櫎锛岀劧鍚庨噸0寮�濮嬪瓨鍌�
                        // initCacheFile();
                        outputStream = new FileOutputStream(cacheFile);
                        setHasMovedTheCacheToAnotherCache(false);
                        alreadyReadByteCount = 0;
                    }
                    Log.d("audio player", "later");
                }
            } catch (Exception e) {
                errorOperator();
                e.printStackTrace();
                Log.e(TAG, "socket disconnect...:" + e.getMessage());
                throw new Exception("socket disconnect....");
            } finally {
                buffer = null;
                stop();
            }
        }

        private void testWhetherToChangeCache() throws Exception {
            if (audioPlayer == null) {
                firstTimeStartPlayer();
            } else {
                changeAnotherCacheWhenEndOfCurrentCache();
            }
        }

        private void firstTimeStartPlayer() throws Exception {
            // 褰撶紦瀛樺凡缁忓ぇ浜嶪NIT_AUDIO_BUFFER鍒欏紑濮嬫挱鏀�
            if (alreadyReadByteCount >= INIT_AUDIO_BUFFER) {
                Runnable r = new Runnable() {
                    public void run() {
                        try {
                            File firstCacheFile = createFirstCacheFile();
                            // 璁剧疆宸茬粡浠巆ache涓鍒舵暟鎹紝鐒跺悗浼氬垹闄よ繖涓猚ache
                            setHasMovedTheCacheToAnotherCache(true);
                            audioPlayer = createAudioPlayer(firstCacheFile);
                            audioPlayer.start();
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage() + " :in firstTimeStartPlayer() fun");
                        } finally {
                        }
                    }
                };
                r.run();
                //handler.post(r);
            }
        }

        private File createFirstCacheFile() throws Exception {
            String firstCacheFileName = cacheFileName + (cacheFileCount++);
            File firstCacheFile = new File(activity.getCacheDir(), firstCacheFileName);
            // 涓轰粈涔堜笉鐩存帴鎾斁cacheFile锛岃�岃澶嶅埗cacheFile鍒颁竴涓柊鐨刢ache锛岀劧鍚庢挱鏀炬鏂扮殑cache锛�
            // 鏄负浜嗛槻姝㈡綔鍦ㄧ殑璇�/鍐欓敊璇紝鍙兘鍦ㄥ啓鍏acheFile鐨勬椂鍊欙紝
            // MediaPlayer姝ｈ瘯鍥捐鏁版嵁锛� 杩欐牱鍙互闃叉姝婚攣鐨勫彂鐢熴��
            moveFile(cacheFile, firstCacheFile);
            return firstCacheFile;

        }

        private void moveFile(File oldFile, File newFile) throws IOException {
            if (!oldFile.exists()) {
                throw new IOException("oldFile is not exists. in moveFile() fun");
            }
            if (oldFile.length() <= 0) {
                throw new IOException("oldFile size = 0. in moveFile() fun");
            }
            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(oldFile));
            BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(newFile,
                    false));

            final byte[] AMR_HEAD = new byte[] { 0x23, 0x21, 0x41, 0x4D, 0x52, 0x0A };
            writer.write(AMR_HEAD, 0, AMR_HEAD.length);
            writer.flush();

            try {
                byte[] buffer = new byte[1024];
                int numOfRead = 0;
                Log.d(TAG, "POS...newFile.length=" + newFile.length() + "  old=" + oldFile.length());
                while ((numOfRead = reader.read(buffer, 0, buffer.length)) != -1) {
                    writer.write(buffer, 0, numOfRead);
                    writer.flush();
                }
                Log.d(TAG, "POS..AFTER...newFile.length=" + newFile.length());
            } catch (IOException e) {
                Log.e(TAG, "moveFile error.. in moveFile() fun." + e.getMessage());
                throw new IOException("moveFile error.. in moveFile() fun.");
            } finally {
                if (reader != null) {
                    reader = null;
                }
                if (writer != null) {
                    writer = null;
                }
            }
        }

        private MediaPlayer createAudioPlayer(File audioFile) throws IOException {
            MediaPlayer mPlayer = new MediaPlayer();

            // It appears that for security/permission reasons, it is better to
            // pass
            // a FileDescriptor rather than a direct path to the File.
            // Also I have seen errors such as "PVMFErrNotSupported" and
            // "Prepare failed.: status=0x1" if a file path String is passed to
            // setDataSource(). So unless otherwise noted, we use a
            // FileDescriptor here.
            FileInputStream fis = new FileInputStream(audioFile);
            mPlayer.reset();
            mPlayer.setDataSource(fis.getFD());
            mPlayer.prepare();
            return mPlayer;
        }

        private void changeAnotherCacheWhenEndOfCurrentCache() throws IOException {
            // 妫�鏌ュ綋鍓峜ache鍓╀綑鏃堕棿
            long theRestTime = audioPlayer.getDuration() - audioPlayer.getCurrentPosition();
            Log.e(TAG, "theRestTime=" + theRestTime + "  isChaingCacheToAnother="
                    + isChaingCacheToAnother);
            if (!isChaingCacheToAnother && theRestTime <= CHANGE_CACHE_TIME) {
                isChaingCacheToAnother = true;

                Runnable r = new Runnable() {
                    public void run() {
                        try {
                            File newCacheFile = createNewCache();
                            // 璁剧疆宸茬粡浠巆ache涓鍒舵暟鎹紝鐒跺悗浼氬垹闄よ繖涓猚ache
                            setHasMovedTheCacheToAnotherCache(true);
                            transferNewCacheToAudioPlayer(newCacheFile);
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage()
                                    + ":changeAnotherCacheWhenEndOfCurrentCache() fun");
                        } finally {
                            deleteOldCache();
                            isChaingCacheToAnother = false;
                        }
                    }
                };
                r.run();
                //handler.post(r);
            }
        }

        private File createNewCache() throws Exception {
            // 灏嗕繚瀛樼綉缁滄暟鎹殑cache澶嶅埗鍒皀ewCache涓繘琛屾挱鏀�
            String newCacheFileName = cacheFileName + (cacheFileCount++);
            File newCacheFile = new File(activity.getCacheDir(), newCacheFileName);
            Log.e(TAG, "before moveFile............the size=" + cacheFile.length());
            moveFile(cacheFile, newCacheFile);
            return newCacheFile;
        }

        private void transferNewCacheToAudioPlayer(File newCacheFile) throws Exception {
            MediaPlayer oldPlayer = audioPlayer;

            try {
                audioPlayer = createAudioPlayer(newCacheFile);
                audioPlayer.start();
            } catch (Exception e) {
                Log.e(TAG, "filename=" + newCacheFile.getName() + " size=" + newCacheFile.length());
                Log.e(TAG, e.getMessage() + " " + e.getCause() + " error start..in transfanNer..");
            }
            try {
                oldPlayer.pause();
                oldPlayer.reset();
                oldPlayer.release();
            } catch (Exception e) {
                Log.e(TAG, "ERROR release oldPlayer.");
            } finally {
                oldPlayer = null;
            }
        }

        private void deleteOldCache() {
            int oldCacheFileCount = cacheFileCount - 1;
            String oldCacheFileName = cacheFileName + oldCacheFileCount;
            File oldCacheFile = new File(activity.getCacheDir(), oldCacheFileName);
            if (oldCacheFile.exists()) {
                oldCacheFile.delete();
            }
        }

        private void errorOperator() {
        }
    }

}

