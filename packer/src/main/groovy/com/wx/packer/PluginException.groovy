package com.wx.packer

import org.gradle.api.GradleException

class PluginException extends GradleException {
    PluginException() {
//        super("See docs on ${Const.HOME_PAGE}")
        super()
    }

    PluginException(final String message) {
        super(message)
    }

    PluginException(final String message, final Throwable cause) {
        super(message, cause)
    }
}