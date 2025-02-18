package com.norman.android.hdrsample.opengl;

import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.view.Surface;

/**
 * 纹理Surface
 */
public class GLTextureSurface extends Surface {

    private final SurfaceTexturePlus surfaceTexture;

    private boolean release;

    private GLTextureSurface(SurfaceTexturePlus texture) {
        super(texture);
        this.surfaceTexture = texture;
    }

    public GLTextureSurface(int textureId) {
        this(new SurfaceTexturePlus(textureId));
    }


    /**
     * 是否已经关联纹理
     * @return
     */
    public boolean isAttached() {
        return surfaceTexture.isAttached();
    }

    /**
     * 每一帧数据更新的回调
     * @param listener
     */
    public void setOnFrameAvailableListener(OnFrameAvailableListener listener) {
        surfaceTexture.setOnFrameAvailableListener(surfaceTexture -> listener.onFrameAvailable(GLTextureSurface.this));
    }

    public void setOnFrameAvailableListener(final OnFrameAvailableListener listener, Handler handler) {
        surfaceTexture.setOnFrameAvailableListener(surfaceTexture -> listener.onFrameAvailable(GLTextureSurface.this),handler);
    }


    public void setDefaultBufferSize(int width, int height) {
        surfaceTexture.setDefaultBufferSize(width, height);
    }

    public void updateTexImage() {
        surfaceTexture.updateTexImage();
    }

    public void detachFromGLContext() {
        surfaceTexture.detachFromGLContext();
    }

    public void attachToGLContext(int texName) {
        surfaceTexture.attachToGLContext(texName);
    }

    public void getTransformMatrix(float[] mtx) {
        surfaceTexture.getTransformMatrix(mtx);
    }


    public long getTimestamp() {
        return surfaceTexture.getTimestamp();
    }

    public int getTextureId() {
        return surfaceTexture.getTextureId();
    }

    @Override
    public final synchronized void release() {
        if (release) {
            return;
        }
        release = true;
        super.release();
        this.surfaceTexture.release();
        onRelease();

    }
    protected void onRelease(){

    }
    public synchronized boolean isRelease(){
        return  release;
    }

    /**
     * SurfaceTexture的加强版，支持获取getTextureId、isAttached
     */
    static class SurfaceTexturePlus extends SurfaceTexture {
        boolean release = false;
        boolean finalize = false;

        boolean attached = true;

        private int textureId;



        public SurfaceTexturePlus(int texName) {
            super(texName);
            textureId = texName;
        }

        @Override
        public synchronized void attachToGLContext(int texName) {
            if (isRelease()) return;
            if (isAttached()) {
                throw new IllegalStateException("already attached by texture " + textureId + ", please detachFromGLContext");
            }
            super.attachToGLContext(texName);
            textureId = texName;
            attached = true;
        }

        @Override
        public synchronized void detachFromGLContext() {
            if (!isAttached()) return;
            super.detachFromGLContext();
            attached  =false;
        }

        public synchronized boolean isAttached() {
            return attached && !isRelease();
        }

        public synchronized int getTextureId() {
            return textureId;
        }

        @Override
        public void setOnFrameAvailableListener(SurfaceTexture.OnFrameAvailableListener listener) {
            if (isRelease()) return;
            super.setOnFrameAvailableListener(listener, null);
        }

        @Override
        public void setOnFrameAvailableListener(final SurfaceTexture.OnFrameAvailableListener listener, Handler handler) {
            if (isRelease()) return;
            super.setOnFrameAvailableListener(listener, handler);
        }

        @Override
        public void setDefaultBufferSize(int width, int height) {
            if (isRelease()) return;
            super.setDefaultBufferSize(width, height);
        }

        @Override
        public void updateTexImage() {
            if (isRelease() || !isAttached()) return;
            super.updateTexImage();
        }

        @Override
        public void getTransformMatrix(float[] mtx) {
            if (isRelease()) return;
            super.getTransformMatrix(mtx);
        }

        @Override
        public synchronized void release() {
            if (isRelease()) return;
            super.release();
            release = false;
        }

        @Override
        protected synchronized void finalize() throws Throwable {
            finalize = true;//解决finalize后再调用release产生的崩溃问题
            super.finalize();
        }

        public synchronized boolean isRelease() {
            return release || finalize;
        }
    }

    public interface OnFrameAvailableListener {
        void onFrameAvailable(GLTextureSurface surface);
    }
}