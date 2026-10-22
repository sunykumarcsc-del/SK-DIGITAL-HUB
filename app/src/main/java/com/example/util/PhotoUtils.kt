package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

object PhotoUtils {

    fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Prepares a standard 3.5cm x 4.5cm passport photo from a bitmap,
     * applying optional solid background color (e.g., White, Studio Sky Blue, Grey, Red).
     */
    fun processPassportPhoto(
        source: Bitmap,
        bgColor: Int = Color.WHITE,
        outputWidth: Int = 413, // 35mm at 300 DPI
        outputHeight: Int = 531  // 45mm at 300 DPI
    ): Bitmap {
        val result = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)

        // Draw background
        canvas.drawColor(bgColor)

        // Calculate aspect fill
        val srcRatio = source.width.toFloat() / source.height.toFloat()
        val targetRatio = outputWidth.toFloat() / outputHeight.toFloat()

        val srcRect = if (srcRatio > targetRatio) {
            // Source is wider than target -> crop sides
            val cropWidth = (source.height * targetRatio).roundToInt()
            val left = (source.width - cropWidth) / 2
            Rect(left, 0, left + cropWidth, source.height)
        } else {
            // Source is taller than target -> crop bottom/top (keep top bias for head)
            val cropHeight = (source.width / targetRatio).roundToInt()
            val top = ((source.height - cropHeight) * 0.25f).roundToInt().coerceAtLeast(0)
            Rect(0, top, source.width, (top + cropHeight).coerceAtMost(source.height))
        }

        val destRect = Rect(0, 0, outputWidth, outputHeight)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(source, srcRect, destRect, paint)

        // Draw subtle 1px border around passport photo
        val borderPaint = Paint().apply {
            color = Color.LTGRAY
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawRect(0f, 0f, outputWidth.toFloat(), outputHeight.toFloat(), borderPaint)

        return result
    }

    /**
     * Generates a printable 4x6 inch (1200 x 1800 px @ 300 DPI) sheet with 8 passport photos,
     * complete with cut guide lines and operator branding!
     */
    fun create4x6PassportSheet(passportPhoto: Bitmap): Bitmap {
        val sheetWidth = 1800  // 6 inches
        val sheetHeight = 1200 // 4 inches
        val sheet = Bitmap.createBitmap(sheetWidth, sheetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(sheet)

        // White paper background
        canvas.drawColor(Color.WHITE)

        val rows = 2
        val cols = 4
        val photoW = 380
        val photoH = 490

        val totalGridW = cols * photoW
        val totalGridH = rows * photoH
        val marginX = (sheetWidth - totalGridW) / (cols + 1)
        val marginY = (sheetHeight - totalGridH) / (rows + 1)

        val cutGuidePaint = Paint().apply {
            color = Color.parseColor("#B0BEC5")
            style = Paint.Style.STROKE
            strokeWidth = 2f
            pathEffect = DashPathEffect(floatArrayOf(10f, 8f), 0f)
        }

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#455A64")
            textSize = 24f
            isFakeBoldText = true
        }

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val left = marginX + c * (photoW + marginX)
                val top = marginY + r * (photoH + marginY)
                val destRect = Rect(left, top, left + photoW, top + photoH)

                canvas.drawBitmap(passportPhoto, null, destRect, Paint(Paint.FILTER_BITMAP_FLAG))

                // Cut marks
                canvas.drawRect(
                    (left - 4).toFloat(),
                    (top - 4).toFloat(),
                    (left + photoW + 4).toFloat(),
                    (top + photoH + 4).toFloat(),
                    cutGuidePaint
                )
            }
        }

        // Footer branding watermark
        canvas.drawText("SK CSC CENTER — ETAWAH | 4x6 Photo Sheet (35x45mm) | Ph: 7906093113", 60f, (sheetHeight - 25).toFloat(), textPaint)

        return sheet
    }

    /**
     * Generates A4 sheet (2480 x 3508 @ 300 DPI) with 24 or 32 photos for bulk passport jobs.
     */
    fun createA4PassportSheet(passportPhoto: Bitmap, count: Int = 24): Bitmap {
        val sheetW = 2480
        val sheetH = 3508
        val sheet = Bitmap.createBitmap(sheetW, sheetH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(sheet)
        canvas.drawColor(Color.WHITE)

        val cols = 4
        val rows = 6
        val photoW = 450
        val photoH = 580

        val marginX = (sheetW - (cols * photoW)) / (cols + 1)
        val marginY = 160 // top margin below header

        val guidePaint = Paint().apply {
            color = Color.LTGRAY
            style = Paint.Style.STROKE
            strokeWidth = 2f
            pathEffect = DashPathEffect(floatArrayOf(12f, 10f), 0f)
        }

        val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#0C2340")
            textSize = 42f
            isFakeBoldText = true
        }
        canvas.drawText("SK CSC CENTER — PASSPORT PHOTO A4 PRINT SHEET (24 Photos)", 100f, 100f, headerPaint)

        var placed = 0
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (placed >= count) break
                val left = marginX + c * (photoW + marginX)
                val top = marginY + r * (photoH + 30)
                val rect = Rect(left, top, left + photoW, top + photoH)

                canvas.drawBitmap(passportPhoto, null, rect, Paint(Paint.FILTER_BITMAP_FLAG))
                canvas.drawRect(
                    (left - 4).toFloat(),
                    (top - 4).toFloat(),
                    (left + photoW + 4).toFloat(),
                    (top + photoH + 4).toFloat(),
                    guidePaint
                )
                placed++
            }
        }

        return sheet
    }

    /**
     * Combines Front and Back of an Aadhaar, PAN, Voter or Ration card onto a single printable page
     * formatted to standard CR80 PVC dimensions (85.6mm x 54mm) with official cut markers.
     */
    fun combineIdCardLayout(
        front: Bitmap,
        back: Bitmap,
        isSideBySide: Boolean = true,
        sheetSizeA4: Boolean = true
    ): Bitmap {
        val outWidth = if (sheetSizeA4) 2480 else 1800
        val outHeight = if (sheetSizeA4) 3508 else 1200
        val result = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        canvas.drawColor(Color.WHITE)

        // Card standard dimensions in pixels (CR80 ratio 1.585)
        val cardW = 1011
        val cardH = 638

        val borderPaint = Paint().apply {
            color = Color.parseColor("#37474F")
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }

        val guidePaint = Paint().apply {
            color = Color.parseColor("#90A4AE")
            style = Paint.Style.STROKE
            strokeWidth = 2f
            pathEffect = DashPathEffect(floatArrayOf(15f, 10f), 0f)
        }

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#0C2340")
            textSize = 38f
            isFakeBoldText = true
        }

        val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.DKGRAY
            textSize = 28f
        }

        canvas.drawText("SK CSC CENTER — ID CARD DUAL-SIDE PRINT PREVIEW", 100f, 120f, titlePaint)
        canvas.drawText("Standard PVC CR-80 Dimension (85.6mm × 54.0mm) | Ready to Cut & Laminate", 100f, 170f, subPaint)

        if (isSideBySide) {
            val startY = 320
            val spacing = 120
            val startX = (outWidth - (cardW * 2 + spacing)) / 2

            // Front
            val frontRect = Rect(startX, startY, startX + cardW, startY + cardH)
            canvas.drawBitmap(front, null, frontRect, Paint(Paint.FILTER_BITMAP_FLAG))
            canvas.drawRoundRect(RectF(frontRect), 20f, 20f, borderPaint)
            canvas.drawRect(RectF(frontRect.left - 10f, frontRect.top - 10f, frontRect.right + 10f, frontRect.bottom + 10f), guidePaint)
            canvas.drawText("FRONT SIDE", (startX + 20).toFloat(), (startY - 15).toFloat(), subPaint)

            // Back
            val backX = startX + cardW + spacing
            val backRect = Rect(backX, startY, backX + cardW, startY + cardH)
            canvas.drawBitmap(back, null, backRect, Paint(Paint.FILTER_BITMAP_FLAG))
            canvas.drawRoundRect(RectF(backRect), 20f, 20f, borderPaint)
            canvas.drawRect(RectF(backRect.left - 10f, backRect.top - 10f, backRect.right + 10f, backRect.bottom + 10f), guidePaint)
            canvas.drawText("BACK SIDE", (backX + 20).toFloat(), (startY - 15).toFloat(), subPaint)
        } else {
            val startX = (outWidth - cardW) / 2
            val spacing = 100
            val startY = 280

            // Front
            val frontRect = Rect(startX, startY, startX + cardW, startY + cardH)
            canvas.drawBitmap(front, null, frontRect, Paint(Paint.FILTER_BITMAP_FLAG))
            canvas.drawRoundRect(RectF(frontRect), 20f, 20f, borderPaint)
            canvas.drawRect(RectF(frontRect.left - 10f, frontRect.top - 10f, frontRect.right + 10f, frontRect.bottom + 10f), guidePaint)
            canvas.drawText("FRONT (सामने का भाग)", (startX + 20).toFloat(), (startY - 15).toFloat(), subPaint)

            // Back
            val backY = startY + cardH + spacing
            val backRect = Rect(startX, backY, startX + cardW, backY + cardH)
            canvas.drawBitmap(back, null, backRect, Paint(Paint.FILTER_BITMAP_FLAG))
            canvas.drawRoundRect(RectF(backRect), 20f, 20f, borderPaint)
            canvas.drawRect(RectF(backRect.left - 10f, backRect.top - 10f, backRect.right + 10f, backRect.bottom + 10f), guidePaint)
            canvas.drawText("BACK (पीछे का भाग)", (startX + 20).toFloat(), (backY - 15).toFloat(), subPaint)
        }

        return result
    }

    /**
     * Document Enhancement: High-contrast B&W Xerox Photocopy effect, or Grayscale scan filter.
     */
    fun enhanceDocument(source: Bitmap, mode: String, contrast: Float = 1.3f): Bitmap {
        val result = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        when (mode) {
            "BW_XEROX" -> {
                // High contrast grayscale
                val cm = ColorMatrix()
                cm.setSaturation(0f)
                val scale = contrast
                val translate = (-0.5f * scale + 0.5f) * 255f
                val contrastMatrix = floatArrayOf(
                    scale, 0f, 0f, 0f, translate,
                    0f, scale, 0f, 0f, translate,
                    0f, 0f, scale, 0f, translate,
                    0f, 0f, 0f, 1f, 0f
                )
                cm.postConcat(ColorMatrix(contrastMatrix))
                paint.colorFilter = ColorMatrixColorFilter(cm)
            }
            "GRAYSCALE" -> {
                val cm = ColorMatrix()
                cm.setSaturation(0f)
                paint.colorFilter = ColorMatrixColorFilter(cm)
            }
            "COLOR_ENHANCE" -> {
                val cm = ColorMatrix()
                cm.setSaturation(1.25f)
                val scale = 1.15f
                val translate = (-0.5f * scale + 0.5f) * 255f
                val contrastMatrix = floatArrayOf(
                    scale, 0f, 0f, 0f, translate,
                    0f, scale, 0f, 0f, translate,
                    0f, 0f, scale, 0f, translate,
                    0f, 0f, 0f, 1f, 0f
                )
                cm.postConcat(ColorMatrix(contrastMatrix))
                paint.colorFilter = ColorMatrixColorFilter(cm)
            }
            else -> {}
        }

        canvas.drawBitmap(source, 0f, 0f, paint)
        return result
    }

    /**
     * Resizes and compresses photo to target dimensions and quality.
     */
    fun compressAndResize(
        bitmap: Bitmap,
        targetWidth: Int,
        targetHeight: Int,
        format: Bitmap.CompressFormat,
        quality: Int
    ): Pair<ByteArray, Bitmap> {
        val scaled = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
        val stream = ByteArrayOutputStream()
        scaled.compress(format, quality.coerceIn(1, 100), stream)
        val bytes = stream.toByteArray()
        return Pair(bytes, scaled)
    }

    /**
     * Saves bitmap to local app storage and returns the absolute path.
     */
    fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String): File {
        val dir = File(context.filesDir, "sk_csc_tools")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, fileName)
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
        out.flush()
        out.close()
        return file
    }
}
