package org.thinker.thinker.infrastructure.core.shell.builtin.jcommander.converters

import android.view.Gravity
import com.beust.jcommander.IStringConverter

class GravityConverter : IStringConverter<Int>
{
    override fun convert(value: String?): Int
    {
        return when (value!!.lowercase())
        {
            "top" -> Gravity.TOP
            "bottom" -> Gravity.BOTTOM
            "left" -> Gravity.START
            "center" -> Gravity.CENTER
            "right" -> Gravity.END
            "top-left" -> Gravity.TOP or Gravity.START
            "top-center" -> Gravity.TOP or Gravity.CENTER_HORIZONTAL
            "top-right" -> Gravity.TOP or Gravity.END
            "bottom-left" -> Gravity.BOTTOM or Gravity.START
            "bottom-right" -> Gravity.BOTTOM or Gravity.END
            "bottom-center" -> Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            "center-left" -> Gravity.CENTER_VERTICAL or Gravity.START
            "center-right" -> Gravity.CENTER_VERTICAL or Gravity.END
            else -> Gravity.BOTTOM
        }
    }
}