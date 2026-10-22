package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object PdfUtils {

    /**
     * Converts a list of Bitmaps into a multi-page A4 PDF document.
     */
    fun createPdfFromBitmaps(
        context: Context,
        bitmaps: List<Bitmap>,
        fileName: String = "SK_CSC_Document_${System.currentTimeMillis()}.pdf"
    ): File? {
        if (bitmaps.isEmpty()) return null

        val doc = PdfDocument()
        val pageWidth = 595 // standard A4 points @ 72 dpi
        val pageHeight = 842
        val margin = 36 // 0.5 inch margin
        val printableW = pageWidth - (margin * 2)
        val printableH = pageHeight - (margin * 2)

        try {
            bitmaps.forEachIndexed { index, bitmap ->
                val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, index + 1).create()
                val page = doc.startPage(pageInfo)
                val canvas = page.canvas

                // Draw background
                canvas.drawColor(Color.WHITE)

                // Scale bitmap to fit within margins maintaining aspect ratio
                val srcW = bitmap.width.toFloat()
                val srcH = bitmap.height.toFloat()
                val scale = minOf(printableW / srcW, printableH / srcH)
                val targetW = (srcW * scale).toInt()
                val targetH = (srcH * scale).toInt()

                val left = margin + (printableW - targetW) / 2
                val top = margin + (printableH - targetH) / 2

                val destRect = Rect(left, top, left + targetW, top + targetH)
                canvas.drawBitmap(bitmap, null, destRect, Paint(Paint.FILTER_BITMAP_FLAG))

                // Page numbering & footer
                val footerPaint = Paint().apply {
                    color = Color.GRAY
                    textSize = 9f
                }
                canvas.drawText("SK CSC CENTER, ETAWAH — Page ${index + 1} of ${bitmaps.size}", margin.toFloat(), (pageHeight - 15).toFloat(), footerPaint)

                doc.finishPage(page)
            }

            val dir = File(context.filesDir, "sk_csc_pdf")
            if (!dir.exists()) dir.mkdirs()
            val outputFile = File(dir, fileName)
            val outputStream = FileOutputStream(outputFile)
            doc.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            doc.close()
            return outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            doc.close()
            return null
        }
    }

    /**
     * Generates a neat, print-ready Indian Job/Government format Resume / Biodata PDF.
     */
    fun createResumePdf(
        context: Context,
        name: String,
        fatherName: String,
        dob: String,
        gender: String,
        category: String,
        phone: String,
        email: String,
        address: String,
        education: String,
        skills: String,
        experience: String,
        photoBitmap: Bitmap?
    ): File? {
        val doc = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val margin = 40f

        try {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = doc.startPage(pageInfo)
            val canvas = page.canvas
            canvas.drawColor(Color.WHITE)

            // Header Title
            val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#0C2340")
                textSize = 22f
                isFakeBoldText = true
            }
            canvas.drawText("CURRICULUM VITAE (BIODATA)", margin, 60f, titlePaint)

            // Header bar
            val barPaint = Paint().apply {
                color = Color.parseColor("#FF6F00")
                strokeWidth = 3f
            }
            canvas.drawLine(margin, 70f, pageWidth - margin, 70f, barPaint)

            var y = 100f

            // If photo provided, draw on top right
            photoBitmap?.let { bmp ->
                val photoW = 85
                val photoH = 105
                val photoLeft = (pageWidth - margin - photoW).toInt()
                val photoRect = Rect(photoLeft, 85, photoLeft + photoW, 85 + photoH)
                canvas.drawBitmap(bmp, null, photoRect, Paint(Paint.FILTER_BITMAP_FLAG))
                val border = Paint().apply {
                    color = Color.DKGRAY
                    style = Paint.Style.STROKE
                    strokeWidth = 1f
                }
                canvas.drawRect(Rect(photoLeft, 85, photoLeft + photoW, 85 + photoH), border)
            }

            val boldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#0C2340")
                textSize = 12f
                isFakeBoldText = true
            }
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#212121")
                textSize = 11f
            }
            val sectionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#0C2340")
                textSize = 13f
                isFakeBoldText = true
            }
            val sectionBg = Paint().apply {
                color = Color.parseColor("#EEF2F6")
            }

            // Contact Info
            canvas.drawText("Name: $name", margin, y, boldPaint); y += 18f
            canvas.drawText("Father's Name: $fatherName", margin, y, textPaint); y += 16f
            canvas.drawText("Date of Birth: $dob | Gender: $gender", margin, y, textPaint); y += 16f
            canvas.drawText("Category: $category", margin, y, textPaint); y += 16f
            canvas.drawText("Contact No: $phone", margin, y, textPaint); y += 16f
            if (email.isNotEmpty()) {
                canvas.drawText("Email: $email", margin, y, textPaint); y += 16f
            }
            canvas.drawText("Address: $address", margin, y, textPaint); y += 30f

            // Section: Educational Qualification
            canvas.drawRect(margin, y - 14f, pageWidth - margin, y + 6f, sectionBg)
            canvas.drawText("ACADEMIC QUALIFICATIONS", margin + 6f, y, sectionPaint); y += 22f

            val eduLines = education.split("\n")
            eduLines.forEach { line ->
                if (line.isNotBlank()) {
                    canvas.drawText("• ${line.trim()}", margin + 10f, y, textPaint)
                    y += 18f
                }
            }
            y += 12f

            // Section: Technical Skills
            if (skills.isNotBlank()) {
                canvas.drawRect(margin, y - 14f, pageWidth - margin, y + 6f, sectionBg)
                canvas.drawText("KEY SKILLS & STRENGTHS", margin + 6f, y, sectionPaint); y += 22f
                skills.split("\n").forEach { line ->
                    if (line.isNotBlank()) {
                        canvas.drawText("• ${line.trim()}", margin + 10f, y, textPaint)
                        y += 18f
                    }
                }
                y += 12f
            }

            // Section: Experience
            if (experience.isNotBlank()) {
                canvas.drawRect(margin, y - 14f, pageWidth - margin, y + 6f, sectionBg)
                canvas.drawText("WORK EXPERIENCE", margin + 6f, y, sectionPaint); y += 22f
                experience.split("\n").forEach { line ->
                    if (line.isNotBlank()) {
                        canvas.drawText("• ${line.trim()}", margin + 10f, y, textPaint)
                        y += 18f
                    }
                }
                y += 12f
            }

            // Declaration
            y = maxOf(y + 20f, 680f)
            canvas.drawRect(margin, y - 14f, pageWidth - margin, y + 6f, sectionBg)
            canvas.drawText("DECLARATION", margin + 6f, y, sectionPaint); y += 24f
            canvas.drawText("I hereby declare that all the information given above is true and correct to the best of my knowledge.", margin + 10f, y, textPaint); y += 45f

            canvas.drawText("Date: ________________", margin + 10f, y, textPaint)
            canvas.drawText("Signature: ________________", pageWidth - margin - 150f, y, textPaint)

            // Footer
            val footer = Paint().apply { color = Color.GRAY; textSize = 9f }
            canvas.drawText("Formatted & Prepared at SK CSC CENTER — Kataiyapur, Etawah (Ph: 7906093113)", margin, pageHeight - 20f, footer)

            doc.finishPage(page)

            val dir = File(context.filesDir, "sk_csc_pdf")
            if (!dir.exists()) dir.mkdirs()
            val outputFile = File(dir, "Resume_${name.replace(" ", "_")}.pdf")
            val outputStream = FileOutputStream(outputFile)
            doc.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            doc.close()
            return outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            doc.close()
            return null
        }
    }

    /**
     * Share file with external apps (WhatsApp, Print, Gmail, Telegram)
     */
    fun shareFile(context: Context, file: File, mimeType: String = "application/pdf") {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share Document via..."))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
