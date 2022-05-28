package com.donfuy.android.today.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(0.dp)
)

val Shapes.addcontainer: Shape
    get() = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)