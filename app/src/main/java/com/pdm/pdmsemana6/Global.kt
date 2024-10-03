package com.pdm.pdmsemana6

class Global {
    companion object{
        var numGeneraciones = 50
        var tampoblacion: Int = 20
        var probabilidad_mutacion:Double = 0.1
        var mListaCiudades = ArrayList<Ciudad>()
        var mMejorRespuesta: Individuo? = null
    }
}