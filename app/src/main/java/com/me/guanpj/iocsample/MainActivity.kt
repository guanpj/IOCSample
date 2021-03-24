package com.me.guanpj.iocsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        goToRepositoryActivity.setOnClickListener {
            RepositoryActivityBuilder.start(this, "Kotlin", "JetBrains", 111, "https://github.com/jetbrains/kotlin")
        }
        goToUserActivity.setOnClickListener {
            UserActivityBuilder.start(this, 30, "guanpj", "Kotliner", "打杂的", "北京")
        }
    }
}