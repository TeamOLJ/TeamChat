package com.teamolj.teamochat

class ChatData {
    var userName: String? = null
    var message: String? = null

    // 파이어베이스에서 가져온 데이터 변환하기 위해 기본 생성자 필수
    constructor() { }

    constructor(userName: String, message: String) {
        this.userName = userName
        this.message = message
    }
}