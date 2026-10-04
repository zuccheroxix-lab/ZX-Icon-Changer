package com.example

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.core.app.ApplicationProvider
import com.example.image.IconConfig
import com.example.image.IconProcessor
import com.example.image.IconShape
import com.example.shortcut.ShortcutCreator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ZX Icon Changer", appName)
  }

  @Test
  fun `test icon processor produces non-null bitmap`() {
    val source = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    val config = IconConfig(
      shape = IconShape.ROUNDED,
      scale = 1.0f,
      rotation = 0f,
      offsetX = 0f,
      offsetY = 0f,
      padding = 10,
      backgroundColor = Color.BLACK,
      isTransparentBg = false
    )
    val result = IconProcessor.processIcon(source, config, 256)
    assertNotNull(result)
    assertEquals(256, result.width)
    assertEquals(256, result.height)
  }
}
