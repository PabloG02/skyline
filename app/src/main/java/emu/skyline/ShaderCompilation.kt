/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright © 2020 Skyline Team and Contributors (https://github.com/skyline-emu/)
 */

package emu.skyline

import android.content.pm.ActivityInfo
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import emu.skyline.databinding.ShaderLoadingBinding

class ShaderCompilation : AppCompatActivity() {
    private val binding by lazy { ShaderLoadingBinding.inflate(layoutInflater) }
    private val missingIcon by lazy { ContextCompat.getDrawable(this, R.drawable.cript)!!.toBitmap(1024, 1024) }
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        setContentView(binding.root)
        binding.gameTitle.isSelected = true
        binding.gameIcon.setImageBitmap(missingIcon)
        binding.gameIconBg.setImageBitmap(missingIcon)
        binding.gameIconBg.setRenderEffect(RenderEffect.createBlurEffect(85F, 85F, Shader.TileMode.MIRROR))
        window.setDecorFitsSystemWindows(false)
        window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        window.insetsController?.let {
            it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            it.hide(WindowInsets.Type.systemBars())
        }
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
