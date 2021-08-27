package com.teamolj.teamochat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.teamolj.teamochat.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity()  {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
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