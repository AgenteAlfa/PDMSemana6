package com.pdm.pdmsemana6

// Los datos como se van a enviar
data class RequestData(val data:List<Float>,val data2:IntArray)
// los datos como se van a recibir
data class ResponseData(val prediction:List<Int>)
