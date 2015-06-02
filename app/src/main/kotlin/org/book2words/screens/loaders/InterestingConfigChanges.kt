package org.book2words.screens.loaders

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources

public class InterestingConfigChanges {

    val mLastConfiguration = Configuration()

    var mLastDensity: Int = 0

    fun applyNewConfig(pResources: Resources): Boolean {

        val configChanges = mLastConfiguration.updateFrom(pResources.getConfiguration())
        val densityChanged = mLastDensity != pResources.getDisplayMetrics().densityDpi
        if (densityChanged || (configChanges and (ActivityInfo.CONFIG_LOCALE or ActivityInfo.CONFIG_UI_MODE or ActivityInfo.CONFIG_SCREEN_LAYOUT)) != 0) {
            mLastDensity = pResources.getDisplayMetrics().densityDpi
            return true
        }
        return false
    }
}
