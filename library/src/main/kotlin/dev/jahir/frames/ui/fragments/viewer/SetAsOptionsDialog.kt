package dev.jahir.frames.ui.fragments.viewer

import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat.getColor
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import dev.jahir.frames.R
import dev.jahir.frames.extensions.fragments.mdDialog
import dev.jahir.frames.extensions.fragments.negativeButton
import dev.jahir.frames.extensions.fragments.positiveButton
import dev.jahir.frames.extensions.fragments.singleChoiceItems
import dev.jahir.frames.extensions.fragments.title
import dev.jahir.frames.ui.activities.ViewerActivity

class SetAsOptionsDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        this.dialog?.window?.setGravity(Gravity.BOTTOM)

        return requireContext().mdDialog {
            title(R.string.apply_to)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            val titleView = View.inflate(context, R.layout.item_apply_title, null)
            setCustomTitle(titleView)
            this.background=resources.getDrawable(R.drawable.apply_background)}

            singleChoiceItems(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    R.array.set_wallpaper_options
                else R.array.set_wallpaper_options_pre_nougat,
                -1
            )

            positiveButton(android.R.string.ok) {
                val listView = (dialog as? AlertDialog)?.listView
                if ((listView?.checkedItemCount ?: 0) > 0) {
                    val checkedItemPosition = listView?.checkedItemPosition ?: -1
                    val actualOption =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) checkedItemPosition
                        else checkedItemPosition + 2
                    (activity as? ViewerActivity)?.startApply(actualOption)
                }
                dismiss()
            }
            negativeButton(android.R.string.cancel) { dismiss() }
        }
    }

    override fun dismiss() {
        try {
            super.dismiss()
        } catch (e: Exception) {
        }
    }

    companion object {
        internal const val TAG = "set_wallpaper_options_dialog"
    }
}