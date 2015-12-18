package cn.onlysoft.xmultithreaddownload;


import java.io.File;

/**
 * Created by Aspsine on 2015/4/20.
 */
public class DownloadRequest {
    private String mUri;

    private File mFolder;

    private CharSequence mTitle;

    private CharSequence mDescription;

    private CharSequence mIconUrl;

    private boolean mScannable;

    private DownloadRequest() {
    }

    private DownloadRequest(String uri, File folder, CharSequence title, CharSequence description, boolean scannable) {
        this.mUri = uri;
        this.mFolder = folder;
        this.mTitle = title;
        this.mDescription = description;
        this.mScannable = scannable;
    }

    public String getUri() {
        return mUri;
    }

    public File getFolder() {
        return mFolder;
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    public CharSequence getDescription() {
        return mDescription;
    }

    public CharSequence getmIconUrl() {
        return mIconUrl;
    }

    public boolean isScannable() {
        return mScannable;
    }

    public static class Builder {

        private String mUri;

        private File mFolder;

        private CharSequence mTitle;

        private CharSequence mDescription;

        private CharSequence mIconUrl;

        private boolean mScannable;

        public Builder() {
        }

        public Builder setUri(String uri) {
            this.mUri = uri;
            return this;
        }

        public Builder setFolder(File folder) {
            this.mFolder = folder;
            return this;
        }

        public Builder setTitle(CharSequence title) {
            this.mTitle = title;
            return this;
        }

        public Builder setDescription(CharSequence description) {
            this.mDescription = description;
            return this;
        }

        public Builder setScannable(boolean scannable) {
            this.mScannable = scannable;
            return this;
        }

        public Builder setIconUrl(CharSequence iconUrl){
            this.mIconUrl=iconUrl;
            return  this;
        }

        public DownloadRequest build() {
            DownloadRequest request = new DownloadRequest(mUri, mFolder, mTitle, mDescription, mScannable);
            return request;
        }
    }
}
