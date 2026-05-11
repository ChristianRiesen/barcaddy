package com.christianriesen.barcaddy.ui.components

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.christianriesen.barcaddy.data.CodeKind
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix

/**
 * Human-readable barcode formats Barcaddy lets users pick from. Each maps to a
 * ZXing BarcodeFormat plus a natural-aspect hint so 1D codes don't render square.
 */
enum class BarcaddyFormat(
    val displayName: String,
    val zxing: BarcodeFormat,
    val kind: CodeKind,
    val isOneDimensional: Boolean,
) {
    CODE_128("Code 128", BarcodeFormat.CODE_128, CodeKind.BARCODE, true),
    CODE_39 ("Code 39",  BarcodeFormat.CODE_39,  CodeKind.BARCODE, true),
    EAN_13  ("EAN-13",   BarcodeFormat.EAN_13,   CodeKind.BARCODE, true),
    EAN_8   ("EAN-8",    BarcodeFormat.EAN_8,    CodeKind.BARCODE, true),
    UPC_A   ("UPC-A",    BarcodeFormat.UPC_A,    CodeKind.BARCODE, true),
    UPC_E   ("UPC-E",    BarcodeFormat.UPC_E,    CodeKind.BARCODE, true),
    ITF     ("ITF",      BarcodeFormat.ITF,      CodeKind.BARCODE, true),
    QR_CODE     ("QR",          BarcodeFormat.QR_CODE,     CodeKind.QR, false),
    DATA_MATRIX ("Data Matrix", BarcodeFormat.DATA_MATRIX, CodeKind.QR, false),
    PDF_417     ("PDF417",      BarcodeFormat.PDF_417,     CodeKind.QR, false),
    AZTEC       ("Aztec",       BarcodeFormat.AZTEC,       CodeKind.QR, false);

    companion object {
        fun forName(name: String): BarcaddyFormat =
            values().firstOrNull { it.name == name } ?: CODE_128

        fun barcodeFormats() = values().filter { it.kind == CodeKind.BARCODE }
        fun qrFormats() = values().filter { it.kind == CodeKind.QR }
    }
}

private fun encodeBitmap(
    value: String,
    format: BarcaddyFormat,
    @androidx.annotation.ColorInt fg: Int,
    @androidx.annotation.ColorInt bg: Int,
): Bitmap? {
    if (value.isBlank()) return null
    val (w, h) = if (format.isOneDimensional) 800 to 240 else 600 to 600
    val hints = mapOf(EncodeHintType.MARGIN to 1)
    return try {
        val matrix: BitMatrix = MultiFormatWriter().encode(value, format.zxing, w, h, hints)
        val bw = matrix.width
        val bh = matrix.height
        val pixels = IntArray(bw * bh)
        for (y in 0 until bh) {
            val row = y * bw
            for (x in 0 until bw) {
                pixels[row + x] = if (matrix.get(x, y)) fg else bg
            }
        }
        Bitmap.createBitmap(bw, bh, Bitmap.Config.ARGB_8888).also {
            it.setPixels(pixels, 0, bw, 0, 0, bw, bh)
        }
    } catch (e: WriterException) {
        null
    } catch (e: IllegalArgumentException) {
        // ZXing throws this for invalid input (e.g. non-digit EAN-13).
        null
    }
}

@Composable
fun CodeImage(
    value: String,
    format: BarcaddyFormat,
    modifier: Modifier = Modifier,
    foreground: Color = Color.Black,
    background: Color = Color.White,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val fgInt = foreground.toArgb()
    val bgInt = background.toArgb()
    val bitmap = remember(value, format, fgInt, bgInt) {
        encodeBitmap(value, format, fgInt, bgInt)
    }
    if (bitmap == null) {
        Box(modifier.background(background), contentAlignment = Alignment.Center) {
            Text(
                "Code preview unavailable for this value",
                modifier = Modifier.padding(12.dp),
                color = Color(AndroidColor.GRAY),
            )
        }
        return
    }
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale,
        filterQuality = FilterQuality.None,
    )
}
