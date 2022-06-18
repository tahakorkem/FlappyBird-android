package com.tahakorkem.flappybird

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import kotlin.math.max
import kotlin.math.round
import kotlin.math.sqrt
import kotlin.random.Random


class FlappyBirdView(context: Context) : View(context) {

    private var pipeStartX: Int = 0
    private var ratio: Float = 1f
    private var isGameStarted: Boolean = false
    private var isGameOver: Boolean = false
    private val UPDATE_MILLIS = 30L
    private var canvasWidth: Int = 0
    private var canvasHeight: Int = 0
    private val backgroundImage: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.bg)
    private var birdX = 0
    private var birdY = 0
    private var gravityVelocity = 0
    private var pipeVelocity = 0
    private var force = 0

    val pipe = BitmapFactory.decodeResource(resources, R.drawable.pipe)
    var pipeX = 0
    var pipeY = 0

    val dangerZone = BitmapFactory.decodeResource(resources, R.drawable.danger_zone)

    val pipeList = mutableListOf<Pipe>()

    var birdFrame = 0
    var pipeFrame = 0

    var score = 0

    private val birdW
        get() = bird[0]!!.width
    private val birdH
        get() = bird[0]!!.height
    private val groundImageWidth
        get() = groundImage[0]!!.width
    private val groundImageHeight
        get() = groundImage[0]!!.height

    private val runnable = Runnable {
        invalidate()
    }

    //private val bird: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.bird_1)
    private var paint = Paint()
    private val bird = arrayOfNulls<Bitmap>(3)
    private val groundImage = arrayOfNulls<Bitmap>(12)

    init {
        //paint.isFilterBitmap = false
        //paint.isAntiAlias = false
        paint.isDither = false

        bird[0] = BitmapFactory.decodeResource(resources, R.drawable.bird_1)
        bird[1] = BitmapFactory.decodeResource(resources, R.drawable.bird_2)
        bird[2] = BitmapFactory.decodeResource(resources, R.drawable.bird_3)
        groundImage[0] = BitmapFactory.decodeResource(resources, R.drawable.ground_1)
        groundImage[1] = BitmapFactory.decodeResource(resources, R.drawable.ground_2)
        groundImage[2] = BitmapFactory.decodeResource(resources, R.drawable.ground_3)
        groundImage[3] = BitmapFactory.decodeResource(resources, R.drawable.ground_4)
        groundImage[4] = BitmapFactory.decodeResource(resources, R.drawable.ground_5)
        groundImage[5] = BitmapFactory.decodeResource(resources, R.drawable.ground_6)
        groundImage[6] = BitmapFactory.decodeResource(resources, R.drawable.ground_7)
        groundImage[7] = BitmapFactory.decodeResource(resources, R.drawable.ground_8)
        groundImage[8] = BitmapFactory.decodeResource(resources, R.drawable.ground_9)
        groundImage[9] = BitmapFactory.decodeResource(resources, R.drawable.ground_10)
        groundImage[10] = BitmapFactory.decodeResource(resources, R.drawable.ground_11)
        groundImage[11] = BitmapFactory.decodeResource(resources, R.drawable.ground_12)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event!!.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //Check if the x and y position of the touch is inside the bitmap
//                if (x > bitmapXPosition && x < bitmapXPosition + bitmapWidth && y > bitmapYPosition && y < bitmapYPosition + bitmapHeight) {
//                    //Bitmap touched
//                }
                if (!isGameStarted) {
                    gravityVelocity = 10
                    pipeVelocity = 3
                    isGameStarted = true
                    if (isGameOver) {
                        isGameOver = false
                        handler.postDelayed(runnable, UPDATE_MILLIS)
                    }
                }
                force = 30

                return true
            }
        }
        return false
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val backgroundWidth = (backgroundImage.width * ratio).toInt()
        val backgroundHeight = (backgroundImage.height * ratio).toInt()

        val dstBackground = Rect(
            0,
            0,
            backgroundWidth,
            backgroundHeight
        )

        canvas?.drawBitmap(backgroundImage, null, dstBackground, paint)

        val pipeWidth = (pipe.width * ratio).toInt()
        val pipeHeight = (pipe.height * ratio).toInt()

        val groundWidth = (groundImageWidth * ratio).toInt()
        val groundHeight = (groundImageHeight * ratio).toInt()

        val birdWidth = (birdW * ratio).toInt()
        val birdHeight = (birdH * ratio).toInt()

        pipeList.forEach {

            if (isGameStarted)
                it.moveFrame++

            it.x -= (pipeVelocity * ratio).toInt()

            val dstPipe = Rect(0 + it.x, 0 + it.y, pipeWidth + it.x, pipeHeight + it.y)

            canvas?.drawBitmap(pipe, null, dstPipe, paint)

            //canvas?.drawBitmap(dangerZone, it.x.toFloat(), it.y + 580 * ratio, paint)
            //420-580 geçiş alanı

            val safeZone = (it.y + 420 * ratio).toInt()..(it.y + 580 * ratio).toInt()

            if (!it.isPassed
                && birdX + birdWidth in it.x + pipeWidth / 2..(it.x + pipeWidth)
                && (birdY..birdY + birdHeight intersect safeZone).isNotEmpty()
            ) {
                it.isPassed = true
                score++
            }

            if ((birdX..(birdX + birdWidth) intersect it.x..(it.x + pipeWidth)).isNotEmpty()
                && (birdY..(birdY + birdHeight) intersect safeZone).isEmpty()
            ) {
                isGameOver = true
            }

        }

        if (pipeList.isNotEmpty() && pipeList[0].moveFrame == 300)
            pipeList.removeAt(0)


        birdY += ((gravityVelocity - force) * ratio).toInt()
        if (birdY < 0) {
            birdY = 0
            force = 0
        }
        if (birdY > backgroundHeight - groundHeight - birdHeight) {
            birdY = backgroundHeight - groundHeight - birdHeight
            force = 0
        }

        val dstBird = Rect(
            0 + birdX,
            0 + birdY,
            birdWidth + birdX,
            birdHeight + birdY
        )

        canvas?.drawBitmap(bird[birdFrame % 12 / 4]!!, null, dstBird, paint)


        val dstGround = Rect(
            0,
            backgroundHeight - groundHeight,
            backgroundWidth,
            backgroundHeight
        )

        canvas?.drawBitmap(groundImage[birdFrame * 2 % 12]!!, null, dstGround, paint)

        if (++birdFrame == 24)
            birdFrame = 0

        if (isGameStarted) {
            if (++pipeFrame == 50) {
                val pipeRandomH = Random.nextInt(-300, -150)
                pipeList.add(Pipe(pipeStartX, pipeRandomH, ratio))
                pipeFrame = 0
            }
        }

        if (force > 0) {
            force -= sqrt(force.toDouble() / 2).toInt()
        }

        if (birdY == backgroundHeight - groundHeight - birdHeight) {
            //gameover
            isGameOver = true
            isGameStarted = false
        }

        val paintText = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            textSize = 100f
            typeface = Typeface.DEFAULT_BOLD
            //canvas?.drawPaint(paint)
        }
        canvas?.drawText(score.toString(), canvasWidth / 2f, 100 * ratio, paintText)


        if (!isGameStarted) {
            force = round(11.5f - birdFrame).toInt()

            val tapTopStart = BitmapFactory.decodeResource(resources, R.drawable.tap_to_play)

            val tapWidth = (tapTopStart.width * ratio).toInt()
            val tapHeight = (tapTopStart.height * ratio).toInt()

            val tapX = (canvasWidth - tapWidth) / 2
            val tapY = (canvasHeight - tapHeight) / 2

            val dstTap = Rect(
                0 + tapX,
                0 + tapY,
                tapWidth + tapX,
                tapHeight + tapY
            )

            canvas?.drawBitmap(tapTopStart, null, dstTap, paint)
        }

        if (!isGameOver)
            handler.postDelayed(runnable, UPDATE_MILLIS)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        canvasWidth = w
        canvasHeight = h

        val ratioW = canvasWidth.toFloat() / backgroundImage.width
        val ratioH = canvasHeight.toFloat() / backgroundImage.height
        ratio = max(ratioW, ratioH)

        birdX = (((canvasWidth - birdW) / 2f) * 2 / 3).toInt()
        birdY = (canvasHeight - birdH) / 2

        pipeStartX = (canvasWidth * 5f / 4).toInt()
        pipeX = pipeStartX

        pipeList.add(Pipe(pipeStartX, Random.nextInt(-300, -150), ratio))

        super.onSizeChanged(w, h, oldw, oldh)
    }

    data class Pipe(
        var x: Int,
        private val yTemp: Int,
        val ratio: Float,
        var moveFrame: Int = 0,
        var isPassed: Boolean = false
    ) {
        val y: Int
            get() = (yTemp * ratio).toInt()
    }

}