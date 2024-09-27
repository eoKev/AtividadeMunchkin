package com.example.munchkin.model

data class Jogador(
    val nome: String,
    var level: Int,
    var bonus: Int,
    var modificadores: Int
) {
    val poder: Int
        get() = level + bonus + modificadores

}
