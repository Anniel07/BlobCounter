package com.example.myblob

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.*

/**
 * The orientation of this class is only vertical , this is done in manifest
 */
class MainActivity : AppCompatActivity() {
    private var w = 0
    private var h = 0
    /**
     *
     * vertically or horizontally neighborhood
     */
    private val dx = intArrayOf(1, 0, -1, 0)
    private val dy = intArrayOf(0, 1, 0, -1)

    /**
     * Means constantes que representan valores en el grid, tambien cacda constante
     * coincide con el color de ese cuadrado en canvas
     */
    private val RED = 0 // no se puede visitar cuadrados rojos

    private val WHITE = 1 // por visitar

    private val GRAY = 2 // visitado despues del backtracing

    /**
     * the positions matches the constant values WHITE to BLUE
     */
    val paletteColors= arrayOf(Color.RED, Color.WHITE, Color.GRAY)

    /**
     * calcluar  row and column and block size dependiendo dimensiones del dispositivo
     */
    private var ROWS = 0

    private var COLS = 0

    private val BLOCK_SIZE = 30F

    /**
     * The grid , its values are one of the four constanst WHITE to BLUE.
     */
   private lateinit var grid: Array<IntArray>

    lateinit var info: TextView
    lateinit var combo: Spinner
    lateinit var blobCanvas: CanvasView
    private lateinit var lny : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        info = findViewById<TextView>(R.id.infoTV) as TextView
        combo = findViewById<Spinner>(R.id.spinner) as Spinner
        lny =  findViewById<LinearLayout>(R.id.linearLy) as LinearLayout
        lny.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            //when this code is called the w,h are obtained, and the rows and cols
            //are calculate dynamically based in dimensions of dispositivo and the
            //height of linearLayout
            //Log.i("Info" , view.height.toString())
            if(ROWS==0) {
                // this code only happen once
                //at this point ROWS is not calculated, and is necessary recalculate
                ROWS = ( (h- view.height - BLOCK_SIZE) / BLOCK_SIZE ).toInt()
                COLS = ( w / BLOCK_SIZE).toInt()
                grid = Array(ROWS) {IntArray(COLS)}
                generateBlob()
            }
            return@addOnLayoutChangeListener
        }


        var options = ArrayList<String>()

        var i = 10
        while (i <= 90) {
            options.add("$i% fill")
            i += 10
        }
        val arrayAdap = ArrayAdapter(this@MainActivity, R.layout.support_simple_spinner_dropdown_item, options)
        combo.adapter = arrayAdap
        combo.setSelection(3) //select 40%

        blobCanvas = findViewById(R.id.canvas)

        var btnCt = findViewById<Button>(R.id.countBtn) as Button
        var btnNew = findViewById<Button>(R.id.newBtn) as Button
        btnCt.setOnClickListener { countBlob() }
        btnNew.setOnClickListener { generateBlob() }

    }


    /**
     * Count the amount of regions when there is a blob
     */
    private fun countBlob() {
        var ct = 0
        for (i in 0 until ROWS) {
            for (j in 0 until COLS) {
                if (countBlobAt(i, j) > 0) {
                    ct++
                }
            }
        }
        info.text = "The number of blobs is $ct"
        drawBlob()
    }

    /**
     * Count blob at mousePosition
     * @param row
     * @param col
     * @return
     */
    private fun countBlobAt(row: Int, col: Int): Int {
        var ct = 0
        if (valid(row, col) && grid[row][col] === GRAY) {
            ct++
            grid[row][col] = RED
            for (i in 0 until dx.size) {
                val nr = row + dy[i]
                val nc = col + dx[i]
                ct += countBlobAt(nr, nc)
            }
        }
        return ct
    }

    /**
     * Generar a blob. Depending of the percent of the combo
     *
     */
    private fun generateBlob() {
        val prob: Double = (combo.selectedItemPosition + 1) / 10.0
        for (i in 0 until ROWS) {
            for (j in 0 until COLS) {
                grid[i][j] = if (Math.random() < prob) GRAY else WHITE
            }
        }

        drawBlob()
        info.text = "Click a square to get the block size."
    }

    /**
     * Draw the entere blob
     */
    private fun drawBlob() {
        for (i in 0 until ROWS) {
            for (j in 0 until COLS) {
                drawSquare(i, j)
                if (grid[i][j] === RED) //change to gray for the next drawing
                    grid[i][j] = GRAY
            }
        }
        blobCanvas.invalidate() //needed
    }

    /**
     * respond to touch events
     */

    fun mousePressed(e: MotionEvent) {
        val row = (e.y / BLOCK_SIZE).toInt()
        val col = (e.x / BLOCK_SIZE).toInt()
        if (!valid(row, col)) //outside the dimensions of the grid
            return
        val ct = countBlobAt(row, col)
        if (ct == 0)
            info.text = "There is no blob at ($row,$col)"
        else if (ct == 1)
            info.text = "Blob at ($row,$col) contains $ct square."
        else info.text = "Blob at ($row,$col) contains $ct squares."
        drawBlob()
    }

    /**
     * Draw a square, always make the drawing on fx Thread
     *
     * @param row
     * @param col
     */
    private fun drawSquare(row: Int, col: Int) {
        // Precondition: All values of grid[row][col] are the constatns defined above
        val color: Int = paletteColors[grid[row][col]]
        var left = col * BLOCK_SIZE
        var top = row * BLOCK_SIZE
        var right = left + BLOCK_SIZE
        var bottom = top + BLOCK_SIZE

        blobCanvas.drawSquare(left, top, right, bottom , color)


    }

    private fun valid(row: Int, col: Int): Boolean {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS
    }

    /**
     * for calculate the numbers of ROW and COLS and draw initialiy the blob
     */
    fun setDim(w: Int, h: Int) {
        this.w = w
        this.h = h
    }
}