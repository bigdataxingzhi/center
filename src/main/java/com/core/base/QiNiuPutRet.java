package com.core.base;

/**
 * 
 * @author 星志
 *
 */
public final class QiNiuPutRet {
    public String key;
    public String hash;
    public String bucket;
    public int width;
    public int height;
    

    public QiNiuPutRet() {
		super();
	}


	public QiNiuPutRet(String key, String hash, String bucket, int width, int height) {
		super();
		this.key = key;
		this.hash = hash;
		this.bucket = bucket;
		this.width = width;
		this.height = height;
	}


	public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}


	public String getHash() {
		return hash;
	}


	public void setHash(String hash) {
		this.hash = hash;
	}


	public String getBucket() {
		return bucket;
	}


	public void setBucket(String bucket) {
		this.bucket = bucket;
	}


	public int getWidth() {
		return width;
	}


	public void setWidth(int width) {
		this.width = width;
	}


	public int getHeight() {
		return height;
	}


	public void setHeight(int height) {
		this.height = height;
	}


	@Override
    public String toString() {
        return "QiNiuPutRet{" +
                "key='" + key + '\'' +
                ", hash='" + hash + '\'' +
                ", bucket='" + bucket + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
