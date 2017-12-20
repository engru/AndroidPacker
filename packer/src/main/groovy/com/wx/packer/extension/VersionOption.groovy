package com.wx.packer.extension

import com.wx.packer.GitUtil
import com.wx.packer.PluginException
import com.wx.packer.model.FlavorVersion
import com.wx.packer.model.VersionType
import com.wx.packer.util.XGitUtil
import org.gradle.api.Project

class VersionOption {

    private Project project
    private FlavorVersion flavor;
    private File versionPropsFile
    private String type

    VersionOption(Project project){
        this.project = project
        flavor = new FlavorVersion()
        boolean isGitExist = GitUtil.isGitExist(project.rootDir.absolutePath)
        if(isGitExist){
            type(VersionType.TYPE_GIT)
        }else{
            type(VersionType.TYPE_PROP)
        }
    }


    public File getVersionFile(){
        return versionPropsFile
    }
    public String getType(){
        return this.type
    }

    public void type(String type){

        this.type = type
        switch (type){
            case VersionType.TYPE_GIT:
                loadGitVersion()
                break;

            case VersionType.TYPE_PROP:
                loadPropVersion()
                break

            case VersionType.TYPE_INTD:
                break;

            default:
                throw new PluginException("Error wrong versiontype $type",new Throwable())
                break;
        }

        project.android.defaultConfig{
            //println("--setdefaultconfig---")
            it.setVersionCode(this.getVersionCode())
            it.setVersionName(this.getVersionName())

        }

    }

    private void loadGitVersion(){
        //println("--loadGitversion---")
        boolean isGitExist = GitUtil.isGitExist(project.rootDir.absolutePath)

        if(!isGitExist){
            throw new PluginException("Could not find git repository, please check you have create git repository",new Throwable())
        }

        flavor.versionCode = XGitUtil.getVersionCode()
        flavor.versionName = XGitUtil.getVersionName()
    }

    private void loadPropVersion(){

        //println("--loadPropversion---")

        versionPropsFile = new File(project.buildFile.getParent() + "/version.properties")
        if (versionPropsFile.canRead()) {
            def Properties versionProps = new Properties()
            versionProps.load(new FileInputStream(versionPropsFile))
            if (versionProps['VERSION_CODE'] == null) {
                versionProps['VERSION_CODE'] = "0"
            }

            if(versionProps['VERSION_TAG'] == null) {
                versionProps['VERSION_TAG'] = "1.0"
            }

            if(versionProps['VERSION_BUILD'] == null){
                versionProps['VERSION_BUILD'] = versionProps['VERSION_CODE']
            }

            String tag = versionProps['VERSION_TAG'].toString()
            int code = Integer.valueOf(versionProps['VERSION_CODE'].toString()) + 1
            int build = Integer.valueOf(versionProps['VERSION_BUILD'].toString()) +1

            flavor.versionCode = code
            flavor.versionName = tag+"."+build

        } else {
            throw new PluginException("Could not read version.properties file in path \""
                    + versionPropsFile.getAbsolutePath() + "\" \r\n" +
                    "Please create this file and add it to your VCS (git, svn, ...).")
        }
    }




    int getVersionCode(){
        return flavor.versionCode
    }

    String getVersionName(){
        return flavor.versionName
    }

}