package com.norman.android.hdrsample.player.decode;


abstract class DecoderImpl implements Decoder {

    /**
     * 根据MediaCodec的生命周期创建状态值
     */

    /**
     * 类new以后未创建的状态
     */
    private static final int DECODE_UNINITIALIZED = 0;
    /**
     * 创建Codec后
     */
    private static final int DECODE_CREATE = 1;
    /**
     * 配置Codec后
     */
    private static final int DECODE_CONFIGURE = 2;
    /**
     * 启动
     */
    private static final int DECODE_START = 3;
    /**
     * 暂停
     */
    private static final int DECODE_PAUSE = 4;
    /**
     * 恢复
     */
    private static final int DECODE_RESUME = 5;
    /**
     * 已经销毁，还可以重新create
     */
    private static final int DECODE_DESTROY = 6;

    /**
     * 释放这个类，不能重新create
     */
    private static final int DECODE_RELEASE = 7;

    private int state = DECODE_UNINITIALIZED;


    @Override
    public synchronized void create(String mimeType) {
        if (state != DECODE_UNINITIALIZED &&
                state != DECODE_DESTROY) {
            return;
        }
        state = DECODE_CREATE;
        onCreate(mimeType);
    }

    @Override
    public synchronized void configure(Decoder.Configuration configuration) {
        if (state != DECODE_CREATE) {
            return;
        }
        state = DECODE_CONFIGURE;
        onConfigure(configuration);
    }



    @Override
    public synchronized void start() {
        if (state != DECODE_CONFIGURE) {
            return;
        }
        state = DECODE_START;
        onStart();
    }



    @Override
    public synchronized void pause() {
        if (!isRunning()) {
            return;
        }
        state = DECODE_PAUSE;
        onPause();
    }


    @Override
    public synchronized void resume() {
        if (!isPaused()) {
            return;
        }
        state = DECODE_RESUME;
        onResume();
    }


    @Override
    public synchronized void flush() {
        if (!isStarted()) {
            return;
        }
        onFlush();
    }

    @Override
    public synchronized void stop() {
        if (!isStarted()) {
            return;
        }
        state = DECODE_CREATE;
        onStop();
    }

    @Override
    public void reset() {
        if (!isConfigured()) {
            return;
        }
        state = DECODE_CREATE;
        onReset();
    }


    @Override
    public synchronized void destroy() {
        if (!isCreated()) {
            return;
        }
        state = DECODE_DESTROY;
        onDestroy();
    }


    @Override
    public synchronized void release() {
        if (state == DECODE_RELEASE) {
            return;
        }
        state = DECODE_RELEASE;
        onRelease();
    }


    @Override
    public synchronized boolean isRunning() {
        return state == DECODE_START || state == DECODE_RESUME;
    }

    @Override
    public synchronized boolean isRelease() {
        return state == DECODE_RELEASE;
    }

    @Override
    public synchronized boolean isPaused() {
        return state == DECODE_PAUSE;
    }

    @Override
    public synchronized boolean isCreated() {
        return state == DECODE_CREATE
                ||state == DECODE_CONFIGURE
                || state == DECODE_START
                || state == DECODE_PAUSE
                || state == DECODE_RESUME;
    }

    @Override
    public synchronized boolean isConfigured() {
        return state == DECODE_CONFIGURE
                || state == DECODE_START
                || state == DECODE_PAUSE
                || state == DECODE_RESUME;
    }

    @Override
    public synchronized boolean isStarted() {
        return state == DECODE_START
                || state == DECODE_PAUSE
                || state == DECODE_RESUME;
    }


    protected abstract void onCreate(String mimeType);


    protected abstract void onConfigure(Decoder.Configuration configuration);

    protected abstract void onStart();

    protected abstract void onPause();

    protected abstract void onResume();


    protected abstract void onStop();


    protected abstract void onReset();

    protected abstract void onFlush();

    protected abstract void onDestroy();

    protected abstract void onRelease();

}
