package com.pdm.pdmsemana6

class Curva {
    private var Activo: Boolean = false
    private val lstPuntos: MutableList<Pair<Float, Float>> = ArrayList()

    fun isActivo() : Boolean{return Activo}
    fun getLstPuntos(): List<Pair<Float, Float>>{return lstPuntos.toList()}
    fun setLstPuntos(lst :MutableList<Pair<Float, Float>>)
    {
        if(!Activo)
        {
            lstPuntos.addAll(lst)
            Activo = true
        }

    }
    fun limpiar()
    {
        Activo = false
        lstPuntos.clear()
    }

}