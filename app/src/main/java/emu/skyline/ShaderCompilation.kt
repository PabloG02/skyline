/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright © 2020 Skyline Team and Contributors (https://github.com/skyline-emu/)
 */

package emu.skyline

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.AndroidPaint
import androidx.compose.ui.graphics.FilterQuality
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import emu.skyline.data.AppItem
import emu.skyline.databinding.ShaderLoadingBinding

class ShaderCompilation : AppCompatActivity() {
    private val binding by lazy { ShaderLoadingBinding.inflate(layoutInflater) }
    private val missingIcon by lazy { ContextCompat.getDrawable(this, R.drawable.cript)!!.toBitmap(1024, 1024) }
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        setContentView(binding.root)

        val gameData = intent.getSerializableExtra("gameData") as AppItem

        window.setDecorFitsSystemWindows(false)
        window.insetsController?.let {
            it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            it.hide(WindowInsets.Type.systemBars())
        }
        window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

        val image = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)
        val canvas = image?.let { Canvas(it) }
        val paint = AndroidPaint()
        paint.filterQuality = FilterQuality.High
        paint.isAntiAlias = true
        canvas?.scale(2f,2f)
        gameData.icon?.let { canvas?.drawBitmap(it, 0F, 0F, paint.asFrameworkPaint()) }
        // Remove bitmap from memory      image.recycle()

        binding.gameIcon.setImageBitmap(image)
        binding.gameIconBg.setImageBitmap(gameData.icon)
        binding.gameIconBg.setRenderEffect(RenderEffect.createBlurEffect(85F, 85F, Shader.TileMode.MIRROR))

        //binding.gameTitle.text = "Cadence of Hyrule: Crypt of the NecroDancer Featuring The Legend of Zelda"
        binding.gameTitle.text = gameData.title
        binding.gameTitle.isSelected = true
        binding.gameVersion.text = gameData.version

        val handler = Handler()
        binding.progressBar.max = 500
        Thread(Runnable {
            // this loop will run until the value of i becomes 99
            var i = 0
            var percentage = 0
            while (i < binding.progressBar.max) {
                i += 1
                // Update the progress bar and display the current value
                handler.post(Runnable {
                    percentage = i*100/binding.progressBar.max
                    binding.progressBar.progress = i
                    binding.progress.text = "$i/${binding.progressBar.max} (${percentage}%)"
                })
                try {
                    Thread.sleep(20)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }).start()
    }
}
