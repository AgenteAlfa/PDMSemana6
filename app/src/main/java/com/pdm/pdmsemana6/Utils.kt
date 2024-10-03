package com.pdm.pdmsemana6

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap

class Utils {
    companion object
    {
        fun DibujarEje(imgCanvas: ImageView, height:Int, weight:Int): Triple<Bitmap, Canvas, Paint>{

            val mBitmap = imgCanvas.drawable.toBitmap(weight,height, Bitmap.Config.ARGB_8888)
            val mCanvas = Canvas(mBitmap)
            mCanvas.drawColor(Color.LTGRAY)
            val mPaint = Paint()
            mPaint.color = Color.GRAY
            mPaint.style = Paint.Style.STROKE
            mPaint.strokeWidth = 6F
            mPaint.isAntiAlias = true

            var alto = height.toFloat()
            var ancho = weight.toFloat()
            //Log.d(TAG, "onCreate: " + alto + " : " + ancho)
            mCanvas.drawLine(0F, alto/2, ancho, alto/2, mPaint)
            mCanvas.drawLine(ancho/2,0F,ancho/2,alto,mPaint)
            imgCanvas.setImageBitmap(mBitmap)
            return Triple(mBitmap, mCanvas, mPaint)
        }
        fun arreglo_puntos(Pts : ArrayList<Ciudad>):IntArray{
            val salida = IntArray(2*Pts.size){it}
            var contador:Int = 0
            for(i in 0..Pts.size-1){
                salida [contador] = Pts[i].x.toInt()
                salida [contador+1] = Pts[i].y.toInt()
                contador = contador + 2
            }
            return salida
        }


    }

}