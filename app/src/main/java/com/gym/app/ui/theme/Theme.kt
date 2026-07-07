package com.gym.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

/**
 * التطبيق عربي فقط (Arabic-only, no language switching), لذلك يتم فرض اتجاه
 * الواجهة RTL دائمًا بشكل صريح عبر [LocalLayoutDirection]، بغض النظر عن لغة
 * النظام في الجهاز. هذا يستخدم دعم RTL القياسي المدمج في Compose فقط
 * (بدون أي منطق RTL مخصص إضافي).
 */
@Composable
fun GymTheme(content: @Composable () -> Unit) {
    val colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
