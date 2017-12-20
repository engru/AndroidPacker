package com.wx.packer.util


public class XGitUtil {


    public static int getVersionCode() {
        def cmd = "git rev-list HEAD --first-parent --count"
        return cmd.execute().text.trim().toInteger()
    }

    public static String getGitTag(){
        def cmd = "git describe --tags --abbrev=0"
        return cmd.execute().text.trim()

    }

    public static String getVersionTag(){
        def tag = getGitTag()

        if(tag.startsWith("v") || tag.startsWith("V")){
            tag = tag.substring(1)
        }

        if(tag == null || tag.equals("")){
            tag = "1.0"
        }

        return tag
    }


    public static String getVersionBuild(){
        String tag = getGitTag()
        String cmd = "git rev-list HEAD --first-parent --count"
        if(tag != null && !tag.equals("")){
            cmd = "git rev-list HEAD ^"+tag+" --first-parent --count"
        }
        return cmd.execute().text.trim().toInteger()
    }


    public static String getVersionName(){

        return getVersionTag()+"."+getVersionBuild()
    }

    public static String gitVersionTag() {
        def cmd = 'git describe --tags --dirty'
        def version = cmd.execute().text.trim()

        //def pattern = "-(\d+)-g"
        def pattern = /-(\d+)-g/
        def matcher = version =~ pattern

        if (matcher) {
            version = version.substring(0, matcher.start()) + "." + matcher[0][1]
        } else {
            version = version + ".0"
        }

        return version
    }
}