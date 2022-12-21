package dev.jahir.frames.ui.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.jahir.frames.R
import dev.jahir.frames.data.Preferences
import dev.jahir.frames.data.models.AboutItem
import dev.jahir.frames.extensions.context.findView
import dev.jahir.frames.extensions.context.string
import dev.jahir.frames.extensions.views.tint
import dev.jahir.frames.ui.activities.base.BaseThemedActivity
import dev.jahir.frames.ui.adapters.AboutAdapter


@Suppress("UNREACHABLE_CODE")
open class AboutActivity : BaseThemedActivity<Preferences>() {

    override val preferences: Preferences by lazy { Preferences(this) }
    private val toolbar: Toolbar? by findView(R.id.toolbar)
    private val recyclerView: RecyclerView? by findView(R.id.recycler_view)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)

        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }
        toolbar?.tint()

        val adapter = AboutAdapter( getInternalAboutItems())
        recyclerView?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView?.adapter = adapter


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) supportFinishAfterTransition()
        return super.onOptionsItemSelected(item)

    }



    private fun getInternalAboutItems() = getAdditionalInternalAboutItems().apply {
        add(
            AboutItem(
                "lazrdev",
                string(R.string.lazar_description),
                "https://file.apurixz.com/user_upload/lazar.jpg",
                arrayListOf("Telegram" to "https://t.me/lazarsstuff")
            )
        )
        if (shouldIncludeContributors()) {

            add(
                AboutItem(
                    "Frames Dashboard",
                    string(R.string.frames_description),
                    "https://raw.githubusercontent.com/lazrdev/wallsweetswalls/main/frames.png",
                    arrayListOf("GitHub" to "https://github.com/jahirfiquitiva/Frames")
                )
            )
            add(
                AboutItem(
                    "Qirkl",
                    string(R.string.qirkl_description),
                    "https://raw.githubusercontent.com/lazrdev/wallsweetswalls/main/qirkl.jpg",
                    arrayListOf("Telegram" to "http://t.me/QirklStudios")
                )
            )
            add(
                AboutItem(
                    "Vlad",
                    string(R.string.qirkl_description),
                    "https://raw.githubusercontent.com/lazrdev/wallsweetswalls/main/vlad.jfif",
                    arrayListOf("Telegram" to "http://t.me/somewallpapars")
                )
            )
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    open fun getAdditionalInternalAboutItems(): ArrayList<AboutItem> = arrayListOf()

    open fun shouldIncludeContributors(): Boolean = true

}
