package com.ebd.lib.zip;

public interface ZipListener {
    void zipSuccess();
    void zipStart();
    void zipProgress(int progress);
    void zipFail();
}
