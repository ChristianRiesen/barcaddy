package com.christianriesen.barcaddy.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.christianriesen.barcaddy.ui.components.BarcaddyFormat
import com.christianriesen.barcaddy.ui.components.IconBtn
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory

@Composable
fun ScanScreen(
    onClose: () -> Unit,
    onCaptured: (value: String, format: BarcaddyFormat) -> Unit,
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED,
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted -> hasPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Box(Modifier.fillMaxSize().background(Color(0xFF0B0C10))) {
        if (hasPermission) {
            ScannerView(onCaptured = onCaptured)
        } else {
            PermissionPrompt(onRequest = { permissionLauncher.launch(Manifest.permission.CAMERA) })
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 6.dp, end = 12.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconBtn(Icons.Default.Close, onClose, "Close", tint = Color.White)
            Text(
                "Scan a code",
                modifier = Modifier.weight(1f),
                color = Color(0xFFF7F3EC),
                fontSize = 14.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            Spacer(Modifier.width(44.dp))
        }
    }
}

@Composable
private fun ScannerView(onCaptured: (String, BarcaddyFormat) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val captured = remember { mutableStateOf(false) }
    val scannerRef = remember { mutableStateOf<DecoratedBarcodeView?>(null) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> scannerRef.value?.resume()
                Lifecycle.Event.ON_PAUSE -> scannerRef.value?.pause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            scannerRef.value?.pause()
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            DecoratedBarcodeView(ctx).apply {
                val formats = listOf(
                    BarcodeFormat.CODE_128, BarcodeFormat.CODE_39, BarcodeFormat.CODE_93,
                    BarcodeFormat.EAN_13, BarcodeFormat.EAN_8,
                    BarcodeFormat.UPC_A, BarcodeFormat.UPC_E,
                    BarcodeFormat.ITF, BarcodeFormat.CODABAR,
                    BarcodeFormat.QR_CODE, BarcodeFormat.DATA_MATRIX,
                    BarcodeFormat.PDF_417, BarcodeFormat.AZTEC,
                )
                barcodeView.decoderFactory = DefaultDecoderFactory(formats)
                setStatusText("")
                decodeContinuous(object : BarcodeCallback {
                    override fun barcodeResult(result: BarcodeResult) {
                        if (captured.value) return
                        val text = result.text ?: return
                        val format = result.barcodeFormat ?: return
                        captured.value = true
                        pause()
                        onCaptured(text, format.toBarcaddyFormat())
                    }

                    override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) = Unit
                })
                resume()
                scannerRef.value = this
            }
        },
    )
}

private fun BarcodeFormat.toBarcaddyFormat(): BarcaddyFormat = when (this) {
    BarcodeFormat.CODE_128 -> BarcaddyFormat.CODE_128
    BarcodeFormat.CODE_39 -> BarcaddyFormat.CODE_39
    BarcodeFormat.EAN_13 -> BarcaddyFormat.EAN_13
    BarcodeFormat.EAN_8 -> BarcaddyFormat.EAN_8
    BarcodeFormat.UPC_A -> BarcaddyFormat.UPC_A
    BarcodeFormat.UPC_E -> BarcaddyFormat.UPC_E
    BarcodeFormat.ITF -> BarcaddyFormat.ITF
    BarcodeFormat.QR_CODE -> BarcaddyFormat.QR_CODE
    BarcodeFormat.DATA_MATRIX -> BarcaddyFormat.DATA_MATRIX
    BarcodeFormat.PDF_417 -> BarcaddyFormat.PDF_417
    BarcodeFormat.AZTEC -> BarcaddyFormat.AZTEC
    // Code 93 / Codabar aren't in our format list; fall back to Code 128 which
    // can re-encode the same digits/letters back to a scannable form.
    else -> BarcaddyFormat.CODE_128
}

@Composable
private fun PermissionPrompt(onRequest: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Camera permission needed",
            color = Color.White,
            fontSize = 22.sp,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Barcaddy needs camera access to scan barcodes and QR codes. Nothing leaves your device.",
            color = Color(0xCCFFFFFF),
            fontSize = 14.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        Box(
            Modifier
                .background(Color(0xFFFF5C3C), RoundedCornerShape(999.dp))
                .clickable(onClick = onRequest)
                .padding(horizontal = 24.dp, vertical = 12.dp),
        ) {
            Text("Grant permission", color = Color.White, fontSize = 14.sp)
        }
    }
}
