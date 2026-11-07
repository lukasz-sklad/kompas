package one.lmk.kompas

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.Surface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            var azimuth by remember { mutableFloatStateOf(0f) }

            DisposableEffect(Unit) {
                val sm = context.getSystemService(SensorManager::class.java)
                val sensor = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
                val matrix = FloatArray(9)
                val remappedMatrix = FloatArray(9)
                val orientation = FloatArray(3)

                val listener = object : SensorEventListener {
                    override fun onSensorChanged(e: SensorEvent) {
                        SensorManager.getRotationMatrixFromVector(matrix, e.values)

                        // Pobierz rzeczywistą orientację ekranu telefonu (pion, bok w lewo, bok w prawo, odwrotnie)
                        @Suppress("DEPRECATION")
                        val displayRotation = (context as? Activity)?.windowManager?.defaultDisplay?.rotation
                            ?: Surface.ROTATION_0

                        var axisX = SensorManager.AXIS_X
                        var axisY = SensorManager.AXIS_Y

                        // Remapowanie osi sensora do orientacji ekranu
                        when (displayRotation) {
                            Surface.ROTATION_90 -> {
                                axisX = SensorManager.AXIS_Y
                                axisY = SensorManager.AXIS_MINUS_X
                            }
                            Surface.ROTATION_180 -> {
                                axisX = SensorManager.AXIS_MINUS_X
                                axisY = SensorManager.AXIS_MINUS_Y
                            }
                            Surface.ROTATION_270 -> {
                                axisX = SensorManager.AXIS_MINUS_Y
                                axisY = SensorManager.AXIS_X
                            }
                            else -> {
                                axisX = SensorManager.AXIS_X
                                axisY = SensorManager.AXIS_Y
                            }
                        }

                        SensorManager.remapCoordinateSystem(matrix, axisX, axisY, remappedMatrix)
                        SensorManager.getOrientation(remappedMatrix, orientation)
                        azimuth = ((Math.toDegrees(orientation[0].toDouble()) + 360) % 360).toFloat()
                    }
                    override fun onAccuracyChanged(s: Sensor?, a: Int) {}
                }
                sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
                onDispose { sm.unregisterListener(listener) }
            }

            val dirs = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
            val fullDirs = arrayOf(
                "PÓŁNOC",
                "PÓŁNOCNY WSCHÓD",
                "WSCHÓD",
                "POŁUDNIOWY WSCHÓD",
                "POŁUDNIE",
                "POŁUDNIOWY ZACHÓD",
                "ZACHÓD",
                "PÓŁNOCNY ZACHÓD"
            )
            val dirIndex = ((azimuth + 22.5f) / 45).toInt() % 8
            val dir = dirs[dirIndex]
            val fullDir = fullDirs[dirIndex]

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF101010))
                    .safeDrawingPadding()
            ) {
                val isLandscape = maxWidth > maxHeight

                if (isLandscape) {
                    // Tryb poziomy (bokiem) - kompas po lewej, dane po prawej. Wykorzystuje szerokość ekranu!
                    val canvasSize = minOf(maxHeight * 0.78f, 260.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Box(
                            modifier = Modifier.weight(1.1f),
                            contentAlignment = Alignment.Center
                        ) {
                            CompassDial(azimuth = azimuth, size = canvasSize)
                        }

                        Column(
                            modifier = Modifier.weight(0.9f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "${azimuth.roundToInt()}°",
                                color = Color.White,
                                fontSize = 56.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-1).sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = dir,
                                color = Color(0xFFFF453A),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = fullDir,
                                color = Color(0xFF8E8E93),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                } else {
                    // Tryb pionowy (portrait) - uporządkowany układ bez ryzyka nałożenia igły na tekst
                    val canvasSize = minOf(maxWidth * 0.70f, 280.dp)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Spacer(Modifier.height(10.dp))

                        CompassDial(azimuth = azimuth, size = canvasSize)

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${azimuth.roundToInt()}° $dir",
                                color = Color.White,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = fullDir,
                                color = Color(0xFF8E8E93),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CompassDial(azimuth: Float, size: Dp) {
    Canvas(Modifier.size(size)) {
        val r = this.size.minDimension / 2f
        val needleLen = r * 0.82f
        val needleHalfWidth = r * 0.13f

        // Subtelny zewnętrzny pierścień tarczy
        drawCircle(
            color = Color(0xFF24242E),
            radius = r * 0.96f,
            style = Stroke(width = 2.dp.toPx())
        )
        // Wewnętrzny przerywany pierścień
        drawCircle(
            color = Color(0xFF1C1C24),
            radius = r * 0.86f,
            style = Stroke(width = 1.2.dp.toPx())
        )

        // Znacznik N na tarczy u góry
        val nPath = Path().apply {
            moveTo(center.x, center.y - r * 0.95f)
            lineTo(center.x - 5.dp.toPx(), center.y - r * 0.86f)
            lineTo(center.x + 5.dp.toPx(), center.y - r * 0.86f)
            close()
        }
        drawPath(nPath, Color(0xFFFF453A))

        // Obracająca się igła 3D
        rotate(-azimuth) {
            // Północ - Lewa fasetka (jaskrawa czerwień)
            drawPath(
                Path().apply {
                    moveTo(center.x, center.y - needleLen)
                    lineTo(center.x - needleHalfWidth, center.y)
                    lineTo(center.x, center.y)
                    close()
                },
                Color(0xFFFF453A)
            )
            // Północ - Prawa fasetka (ciemniejsza czerwień w cieniu)
            drawPath(
                Path().apply {
                    moveTo(center.x, center.y - needleLen)
                    lineTo(center.x, center.y)
                    lineTo(center.x + needleHalfWidth, center.y)
                    close()
                },
                Color(0xFFD70015)
            )
            // Południe - Lewa fasetka (srebrzysta w cieniu)
            drawPath(
                Path().apply {
                    moveTo(center.x, center.y + needleLen)
                    lineTo(center.x, center.y)
                    lineTo(center.x - needleHalfWidth, center.y)
                    close()
                },
                Color(0xFFAEAEB2)
            )
            // Południe - Prawa fasetka (czysta biel)
            drawPath(
                Path().apply {
                    moveTo(center.x, center.y + needleLen)
                    lineTo(center.x + needleHalfWidth, center.y)
                    lineTo(center.x, center.y)
                    close()
                },
                Color(0xFFF2F2F7)
            )
            // Centralny nit / łożysko igły
            drawCircle(Color(0xFF181820), radius = needleHalfWidth * 0.55f, center = center)
            drawCircle(
                Color.White,
                radius = needleHalfWidth * 0.55f,
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )
            drawCircle(Color(0xFFFF453A), radius = needleHalfWidth * 0.28f, center = center)
        }
    }
}
