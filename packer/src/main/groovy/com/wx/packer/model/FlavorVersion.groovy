package com.wx.packer.model

class FlavorVersion {
    String branchName;
    String shortBranch;
    int branchVersion;
    int localChanges;
    String commit;

    String meta


    private int major = 0;
    private int minor = -1;
    private int patch = -1;
    private int build = -1;

    int versionCode = 0
    String versionName = "1.0"

    FlavorVersion(){

    }



}