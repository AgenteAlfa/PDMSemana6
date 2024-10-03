package com.pdm.pdmsemana6

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Callback
import retrofit2.Call
import  retrofit2.Response
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.pdm.pdmsemana6.Global.Companion.mListaCiudades
import com.pdm.pdmsemana6.Global.Companion.mMejorRespuesta
import com.pdm.pdmsemana6.Global.Companion.numGeneraciones
import com.pdm.pdmsemana6.Global.Companion.probabilidad_mutacion
import com.pdm.pdmsemana6.Global.Companion.tampoblacion
import com.pdm.pdmsemana6.Utils.Companion.DibujarEje
import com.pdm.pdmsemana6.Utils.Companion.factorial
import com.pdm.pdmsemana6.databinding.ActivityMainBinding
import kotlin.math.pow

class MainActivity : AppCompatActivity() {
    private val TAG = "MAIN";
    lateinit var binding : ActivityMainBinding
    private lateinit var  imgCanvas: ImageView
    private lateinit var mBitmap: Bitmap
    private lateinit var mCanvas: Canvas
    private lateinit var mPaint: Paint
    private var Punto :Pair<Float?, Float?> = Pair(null, null)
    private val IndNodos: MutableList<TextView> = ArrayList()
    private val city_size = 40
    private lateinit var mCurva: Curva
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imgCanvas = binding.imgCanvas
        setContentView(binding.root)
        imgCanvas.post(Runnable {
            InicializarCanvas()
        })
        mCurva = Curva()
        binding.btnAgregarNodo.setOnClickListener{
            if(Punto.first == null && Punto.second == null)
            {
                Toast.makeText(baseContext, "Seleccione un lugar para agregar una ciudad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            mMejorRespuesta = null
            val rnd: java.util.Random = java.util.Random()
            val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
            mListaCiudades.add(Ciudad(Punto.first!!, Punto.second!!, color))
            Punto = Pair(null, null)

            if (IndNodos.size < mListaCiudades.size)
            {
                var txt =  TextView(baseContext)
                txt.background = ContextCompat.getDrawable(applicationContext, R.drawable.caja)!!
                txt.gravity = binding.txtBase.gravity
                txt.setTextColor(binding.txtBase.textColors)
                txt.setTypeface(binding.txtBase.typeface)
                txt.text = "-"
                txt.layoutParams = binding.txtBase.layoutParams
                IndNodos.add(txt)
                binding.lytcontenedorNodos.addView(txt)
            }
            for (n in IndNodos)
            {n.visibility = View.GONE}
            for ((index, n:Ciudad) in Global.mListaCiudades.withIndex())
            {
                IndNodos[index].text = "${index + 1}"
                IndNodos[index].backgroundTintList = ColorStateList.valueOf(n.ccolor)
                IndNodos[index].visibility = View.VISIBLE
            }

            ActualizarImgCanvas()
            LlamarAPI()
        }

        binding.btnEjecutar.setOnClickListener {LlamarAPI()}

    }
    fun LlamarAPI()
    {
        if (Global.mListaCiudades.size < 4) return
        val requestData = RequestData(
            listOf(
                mListaCiudades.size.toFloat(),
                tampoblacion.toFloat(),
                probabilidad_mutacion.toFloat(),
                numGeneraciones.toFloat()
            ),
            Utils.arreglo_puntos(mListaCiudades)
        )
        val call = RetrofitAGviajero.aGviajeroAPI.predict(requestData)
        call.enqueue(object : Callback<ResponseData> {
            override fun onResponse(
                call: Call<ResponseData>,
                response: Response<ResponseData>
            ) {
                if (response.isSuccessful) {
                    val responseData = response.body()
                    responseData?.let {
                        val I = Individuo()
                        I.cromosoma = it.prediction.toIntArray()
                        mMejorRespuesta = I
                        ActualizarImgCanvas()

                    }
                } else {
                    val myToast =
                        Toast.makeText(applicationContext, "Error1", Toast.LENGTH_LONG)
                    myToast.show()
                }
            }

            override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                var mensaje: String = t.message.toString()
                val myToast =
                    Toast.makeText(applicationContext, "Error2:" + mensaje, Toast.LENGTH_LONG)
                myToast.show()
            }
        })
        mCurva.limpiar()
    }

    fun ActualizarImgCanvas() {

        DibujarEje(imgCanvas, imgCanvas.height, imgCanvas.width)
        if (Punto.first != null && Punto.second != null) {
            binding.btnAgregarNodo.isEnabled = true
            mPaint.color = Color.RED
            mCanvas.drawCircle(Punto.first!!, Punto.second!!, 10F, mPaint)
            mCanvas.drawLine(
                Punto.first!!,
                Punto.second!! + 10,
                Punto.first!!,
                Punto.second!! + 30f,
                mPaint
            )
            mCanvas.drawLine(
                Punto.first!!,
                Punto.second!! - 10,
                Punto.first!!,
                Punto.second!! - 30f,
                mPaint
            )
            mCanvas.drawLine(
                Punto.first!! + 10,
                Punto.second!!,
                Punto.first!! + 30f,
                Punto.second!!,
                mPaint
            )
            mCanvas.drawLine(
                Punto.first!! - 10,
                Punto.second!!,
                Punto.first!! - 30f,
                Punto.second!!,
                mPaint
            )
        }
        //Dibujar Ciudades
        var contador = 1
        mPaint.color = Color.BLACK
        mPaint.textSize = 32f
        for (ciudad in Global.mListaCiudades) {
            //Dibujando la ciudad
            val dwCity = ContextCompat.getDrawable(applicationContext, R.drawable.ic_ciudad)!!
            dwCity.setTint(ciudad.ccolor)
            mCanvas.drawBitmap(
                dwCity.toBitmap(city_size, city_size),
                ciudad.x - city_size / 2,
                ciudad.y - city_size / 2,
                mPaint
            )
            mCanvas.drawText("$contador", ciudad.x + city_size, ciudad.y, mPaint)
            contador += 1
        }
        if (mMejorRespuesta != null)
        {
            Log.d(TAG, "onResponse: CROMOSOMA DE TAMAÑO: ${mMejorRespuesta!!.cromosoma.size} => ${mMejorRespuesta!!.cromosoma.joinToString(" ")}")

            mPaint.color = Color.GRAY
            mPaint.strokeWidth = 1.5f
            for(i in 0..mMejorRespuesta!!.cromosoma.size - 2)
            {
                Log.d(TAG, "ActualizarImgCanvas: $i con la ciudad ${mMejorRespuesta!!.cromosoma[i]}")
                val C1 = Global.mListaCiudades[mMejorRespuesta!!.cromosoma[i]]
                val C2 = Global.mListaCiudades[mMejorRespuesta!!.cromosoma[i + 1]]
                val VDx = C2.x - C1.x
                val CDy = C2.y - C1.y
                mCanvas.drawLine(C1.x, C1.y, C2.x, C2.y, mPaint)

            }
        }

        if(mMejorRespuesta != null)
        {
            for (n in IndNodos)
            {n.visibility = View.GONE}
            var index = 0
            for (ci in Global.mMejorRespuesta!!.cromosoma)
            {
                val n = Global.mListaCiudades[ci]
                IndNodos[index].text = "${ci + 1}"
                IndNodos[index].backgroundTintList = ColorStateList.valueOf(n.ccolor)
                IndNodos[index].visibility = View.VISIBLE
                index += 1
            }
        }
        mPaint.color = Color.BLACK
        mPaint.strokeWidth = 4f

        if(mMejorRespuesta != null)
        {
            val n = mMejorRespuesta!!.cromosoma.size - 1
            val pasos = 50
            if(mCurva.isActivo())
            {
                Log.d(TAG, "ActualizarPuntos: CURVA ACTIVA -> CARGANDO CURVA")
                //Cargar curva
                val lstPuntos = mCurva.getLstPuntos().toMutableList()
                //Dibujar rectas
                lstPuntos.add(0, Pair(mListaCiudades[mMejorRespuesta!!.cromosoma.first()].x, mListaCiudades[mMejorRespuesta!!.cromosoma.first()].y))
                lstPuntos.add( Pair(mListaCiudades[mMejorRespuesta!!.cromosoma.last()].x, mListaCiudades[mMejorRespuesta!!.cromosoma.last()].y))
                for(ndI in 0..lstPuntos.size - 2)
                {
                    mCanvas.drawLine(lstPuntos[ndI].first, lstPuntos[ndI].second, lstPuntos[ndI + 1].first, lstPuntos[ndI + 1].second, mPaint)
                }
            }
            else
            {
                //Genera la lista de puntos por donde pasa la linea bezier
                val lst = mMejorRespuesta!!.cromosoma
                Log.d(TAG, "ActualizarPuntos: CURVA INACTIVA -> PREPARANDO CURVA CON ${lst.size} PUNTOS")
                val lstPuntos: MutableList<Pair<Float, Float>> = ArrayList()
                for (pI in 1..<pasos)
                {
                    val t =  pI/pasos.toFloat()
                    var Psx = 0f
                    var Psy = 0f
                    for (i in 0..n)
                    {
                        val Cn_i = factorial(n) / (factorial(i) * factorial(n - i))
                        //Log.d(TAG, "ActualizarPuntos: Comb ($i $n) es $Cn_i")
                        Psx += Cn_i * mListaCiudades[lst[i]].x * ((1 - t).pow(n - i)) * (t.pow(i))
                        Psy += Cn_i * mListaCiudades[lst[i]].y * ((1 - t).pow(n - i)) * (t.pow(i))
                    }
                    //Log.d(TAG, "ActualizarPuntos: PUNTO Bt = $Psx - $Psy")
                    //mcanvas.drawCircle(Psx, Psy, 3f, mpaint)
                    //Guardar curva
                    lstPuntos.add(Pair(Psx, Psy))
                }
                //Guardar puntos
                mCurva.setLstPuntos(lstPuntos)
                //Dibujar rectas
                lstPuntos.add(0, Pair(mListaCiudades[lst.first()].x, mListaCiudades[lst.first()].y))
                lstPuntos.add(Pair(mListaCiudades[lst.last()].x, mListaCiudades[lst.last()].y))
                for(ndI in 0..lstPuntos.size - 2)
                {
                    mCanvas.drawLine(lstPuntos[ndI].first, lstPuntos[ndI].second, lstPuntos[ndI + 1].first, lstPuntos[ndI + 1].second, mPaint)
                }

            }


        }


    }
    @SuppressLint("ClickableViewAccessibility")
    private fun InicializarCanvas()
    {
        val (mb, mc, mp) = DibujarEje(imgCanvas,imgCanvas.height, imgCanvas.width)
        mBitmap = mb
        mCanvas = mc
        mPaint = mp
        Log.d(TAG, "InicializarCanvas: INICIADO")
        binding.imgCanvas.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, e: MotionEvent): Boolean {
                Punto = Pair(e.x, e.y)
                ActualizarImgCanvas()

                //ActualizarPuntos(mCanvas, mPaint)
                binding.imgCanvas.setImageBitmap(mBitmap)

                return true
            }
        })
    }
}