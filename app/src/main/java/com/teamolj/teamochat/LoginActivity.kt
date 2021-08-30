package com.teamolj.teamochat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.teamolj.teamochat.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity()  {
    private lateinit var binding: ActivityLoginBinding
    private val TAG = "로그인화면"

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val currentUser = auth.currentUser
        if(currentUser != null){
            // https://firebase.google.com/docs/cloud-messaging/android/receive?authuser=2#handling_messages
            // background 상태에서 수신한 FCM의 data payload는 런처 인텐트의 extra로 들어온다는 점 활용
            // 애초에 서버에서 notification이 아닌 data message를 보내면 되는 문제지만, 콘솔에서는 notification이 필수로 들어가는 탓에...
            val check = intent.extras

            val mainIntent = Intent(this, MainActivity::class.java)
            mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            if (check != null)
                mainIntent.putExtra("isNotification", check)
            startActivity(mainIntent)
        }

        binding.btnLogin.setOnClickListener {
            binding.btnLogin.isClickable = false
            closeKeyboard()

            // 로그인 시도
            val userId = binding.txtUserID.text.toString().trim()
            val email = userId + getString(R.string.email_domain)
            val password = binding.txtUserPwd.text.toString().trim()

            if (!userId.isEmpty() && !password.isEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            App.prefs.setString("userID", userId)

                            // 토큰 생성(없을 경우) 및 가져오기(있을 경우) 함수
                            FirebaseMessaging.getInstance().token.addOnCompleteListener(
                                OnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                                    return@OnCompleteListener
                                }

                                // Get new FCM registration token
                                val token = task.result
                                Log.d(TAG, "FCM registration Token: $token")
                            })

                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                            binding.btnLogin.isClickable = true
                        }
                    }
            }
            else {
                Toast.makeText(this, "아이디와 비밀번호를 작성하세요.", Toast.LENGTH_SHORT).show()
                binding.btnLogin.isClickable = true
            }
        }
    }

    private fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
        }
    }
}