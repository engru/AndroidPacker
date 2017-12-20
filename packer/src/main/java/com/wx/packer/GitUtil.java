package com.wx.packer;

import org.gradle.api.Project;

import java.io.File;

/**
 * Created by alex on 17-8-26.
 */

public class GitUtil {

    public static boolean isGitExist(String path){
        if(!path.endsWith(".git")){
            path += "/.git";
        }
        File file = new File(path);
        if (file.exists()) {
            return true;
        }
        return false;
    }



}
