package com.donfuy.android.today.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun SwipeableRow(
    onItemClicked: () -> Unit,
    onSwipedLeft: () -> Unit,
    onSwipedRight: () -> Unit,
    swipeLeftContent: @Composable () -> Unit,
    swipeRightContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val offsetX = remember { mutableStateOf(Animatable(0f)) }
    var size by remember { mutableStateOf(IntSize.Zero) }

    Box {
        // Swiping Left
        if (offsetX.value.value < 0) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .height(with(LocalDensity.current) { size.height.toDp() })
            ) {
                swipeLeftContent()
            }
        }
        // Swiping Right
        if (offsetX.value.value > 0) {
            swipeRightContent()
        }

        Surface(
            Modifier
                .clickable { onItemClicked() }
                .swipeable(
                    onSwipedLeft = onSwipedLeft,
                    onSwipedRight = onSwipedRight,
                    offsetX
                )
                .onSizeChanged { size = it }
        ) {
            content()
        }
    }

}


/**
 * The modified element can be horizontally swiped away.
 *
 * NOTE: Adapted from AnimationCodelab
 *
 * @param onSwipedLeft Called when the element is swiped to the right edge of the screen.
 * @param onSwipedRight Called when the element is swiped to the left edge of the screen.
 */
private fun Modifier.swipeable(
    onSwipedLeft: () -> Unit,
    onSwipedRight: () -> Unit,
    offsetX: MutableState<Animatable<Float, AnimationVector1D>>
): Modifier = composed {

    val offset = remember { Animatable(0f) }

    pointerInput(Unit) {
        // Used to calculate a settling position of a fling animation
        val decay = splineBasedDecay<Float>(this)

        // Wrap in a coroutine scope to use suspend functions for touch events and animation.
        coroutineScope {
            while (true) {
                // Wait for a touch down event
                val pointerId = awaitPointerEventScope { awaitFirstDown().id }

                // Touch detected, the animation should be stopped
                offsetX.value.stop()

                // Prepare for drag events and record velocity of a fling
                val velocityTracker = VelocityTracker()

                // Wait for drag events
                awaitPointerEventScope {
                    horizontalDrag(pointerId) { change ->
                        // Apply the drag change to the Animatable offset
                        val horizontalDragOffset = offsetX.value.value + change.positionChange().x
                        launch {
                            offsetX.value.snapTo(horizontalDragOffset)
                        }
                        // Record the velocity of the drag
                        velocityTracker.addPosition(change.uptimeMillis, change.position)
                        // Consume the gesture event, not passed to external
                        change.consume()
                    }
                }

                // Dragging finished. Calculate the velocity of the fling
                val velocity = velocityTracker.calculateVelocity().x
                // Calculate the eventual position where the fling should settle based on the
                // current offset value and velocity
                val targetOffsetX = decay.calculateTargetValue(offsetX.value.value, velocity)
                // Set the upper and lower bounds so that the animation stops when it reaches
                // the edge
                offsetX.value.updateBounds(
                    lowerBound = -size.width.toFloat(),
                    upperBound = size.width.toFloat()
                )
                launch {
                    // Slide back the element if the settling position does not go beyond the
                    // the size of the element. Remove the element if it does.
                    if (targetOffsetX.absoluteValue <= size.width) {
                        // Not enough velocity; Slid back.
                        offsetX.value.animateTo(targetValue = 0f, initialVelocity = velocity)
                    } else {
                        // Enough velocity to slide away the element to the edge
                        offsetX.value.animateDecay(velocity, decay)

                        // If velocity is negative, swiped left
                        // If velocity is positive, swiped right
                        if (velocity > 0) onSwipedRight() else onSwipedLeft()

                    }
                }
            }
        }
    }
        .offset {
            IntOffset(offsetX.value.value.roundToInt(), 0)
        }
}