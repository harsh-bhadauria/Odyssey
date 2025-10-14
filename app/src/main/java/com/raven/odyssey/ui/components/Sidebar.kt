package com.raven.odyssey.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun Sidebar(
    isOpen: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sidebarWidth = 280.dp
    val offsetX by animateDpAsState(
        targetValue = if (isOpen) 0.dp else -sidebarWidth,
        animationSpec = spring(),
        label = "sidebar_offset"
    )

    if (isOpen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onClose() }
        )
    }

    Scaffold(
        modifier = modifier
            .width(sidebarWidth)
            .fillMaxHeight()
            .offset(x = offsetX)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        SidebarContent(modifier = Modifier.padding(it))
    }
}

@Composable
fun SidebarContent(modifier: Modifier = Modifier) {
    Text("HELLO", modifier = modifier.background(Color.Red).padding(4.dp))
}
