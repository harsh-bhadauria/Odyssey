package com.raven.odyssey.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlin.math.pow
import kotlin.random.Random

@Composable
fun DoodleCelebration(
    trigger: Int, // Increment this to fire the animation
    modifier: Modifier = Modifier,
    rayCount: Int = 12, // Reduced count
    radius: Float = 300f,
    color: Color = Color.Black
) {
    // 1. Animation State
    val animatable = remember { Animatable(0f) }

    // 2. Path State (Remembered so they don't change every frame)
    val paths = remember(trigger) {
        if (trigger > 0) {
            List(rayCount) {
                generateRandomDoodlePath(radius) to (Random.nextFloat() * 2f + 2f) // Path + Random Stroke Width
            }
        } else emptyList()
    }

    // 3. Trigger Animation
    LaunchedEffect(trigger) {
        if (trigger > 0) {
            animatable.snapTo(0f)
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600, easing = LinearOutSlowInEasing)
            )
        }
    }

    // 4. Draw
    // Only show the Popup when animation is active or has just started
    val isAnimating = animatable.value > 0f && animatable.value < 1f

    if (isAnimating || (trigger > 0 && animatable.value == 0f)) {
        Popup(
            alignment = Alignment.Center,
            properties = PopupProperties(
                focusable = false,
                clippingEnabled = false,
                excludeFromSystemGesture = true
            )
        ) {
            // Using requiredSize to force the celebration to be large enough even if the parent container is small
            Box(
                modifier = modifier.requiredSize((radius * 2 / 2.5).dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val progress = animatable.value
                    if (progress > 0f && progress < 1f) {
                        val centerOffset = Offset(size.width / 2, size.height / 2)

                        paths.forEachIndexed { index, (pathData, strokeWidth) ->
                            // Distribute rays around the circle with slight random angle jitter
                            val baseAngle = (360f / rayCount) * index
                            val angleJitter = Random.nextFloat() * 10f - 5f
                            val angle = baseAngle + angleJitter

                            rotate(degrees = angle, pivot = centerOffset) {
                                val pathMeasure = PathMeasure()
                                pathMeasure.setPath(pathData, false)

                                val segmentPath = Path()

                                // "Flying" effect: Start point chases the end point
                                // The tail starts moving after 20% progress
                                val tailProgress = (progress - 0.2f).coerceAtLeast(0f) / 0.8f
                                // Apply an easing to the tail so it catches up gracefully
                                val tailEased = tailProgress.pow(1.5f)

                                val startDistance = pathMeasure.length * tailEased
                                val stopDistance = pathMeasure.length * progress

                                if (stopDistance > startDistance) {
                                    pathMeasure.getSegment(
                                        startDistance = startDistance,
                                        stopDistance = stopDistance,
                                        destination = segmentPath,
                                        startWithMoveTo = true
                                    )

                                    translate(left = centerOffset.x, top = centerOffset.y) {
                                        drawPath(
                                            path = segmentPath,
                                            color = color.copy(alpha = 1f - tailEased), // Fade out as it flies away
                                            style = Stroke(
                                                width = strokeWidth.dp.toPx(),
                                                cap = StrokeCap.Round
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Helper to generate a random "loopy" or "wavy" path
fun generateRandomDoodlePath(maxRadius: Float): Path {
    val path = Path()
    path.moveTo(0f, 0f)

    // Randomize length
    val length = Random.nextFloat() * (maxRadius * 0.4f) + (maxRadius * 0.6f)

    when (Random.nextInt(3)) {
        0 -> {
            // S-Curve (Squiggle)
            val amplitude = Random.nextFloat() * 15f + 5f
            val sign = if(Random.nextBoolean()) 1 else -1
            path.cubicTo(
                length * 0.33f, amplitude * sign,
                length * 0.66f, -amplitude * sign,
                length, 0f
            )
        }
        1 -> {
             // Simple Arch
            val amplitude = (Random.nextFloat() * 20f + 5f) * (if(Random.nextBoolean()) 1 else -1)
            path.cubicTo(
                length * 0.4f, amplitude,
                length * 0.6f, amplitude,
                length, 0f
            )
        }
        else -> {
             // Loop / Curl
            val amplitude = Random.nextFloat() * 20f + 10f
            val sign = if(Random.nextBoolean()) 1 else -1
            path.cubicTo(
                length * 0.3f, amplitude * sign,
                length * 0.6f, -amplitude * sign * 1.5f,
                length, 0f
            )
        }
    }
    return path
}
