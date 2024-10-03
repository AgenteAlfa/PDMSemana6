package com.pdm.pdmsemana6

class Individuo() {
    var num_ciudades = 0
    var distancia:Int = 0 // aptitud
    var cromosoma:IntArray = IntArray(num_ciudades) { it }

    init {
        this.num_ciudades = Global.mListaCiudades.size
        this.cromosoma = IntArray(num_ciudades) { it }
        this.cromosoma.shuffle()
    }
/*
    fun get_distancia():Int{
        this.distancia = 0
        val C = Global.mListaCiudades
        for(i in (0 until C.size-1)){
            this.distancia += pitagoras(C[cromosoma[i]],C[cromosoma[i+1]])
        }
        return this.distancia
    }
    fun getCamino() : String
    {
        var str = ""
        for (c in cromosoma)
            str += "$c ->"
        return str + "${get_distancia()}"
    }*/
}