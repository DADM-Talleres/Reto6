package com.example.triqui.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.triqui.logic.Difficulty
import com.example.triqui.logic.Player
import com.example.triqui.R

@Composable
fun GameScreen(viewModel: GameViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val soundPlayer = remember { SoundPlayer(context) }

    DisposableEffect(Unit) {
        onDispose { soundPlayer.release() }
    }

    LaunchedEffect(state.moveCount) {
        if (state.moveCount > 0) {
            state.lastMovedBy?.let { soundPlayer.playMove(it) }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = state.statusText, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        DifficultySelector(
            selected = state.difficulty,
            onSelect = viewModel::onDifficultySelected
        )

        for (row in 0..2) {
            Row {
                for (col in 0..2) {
                    val index = row * 3 + col
                    Cell(mark = state.cells[index]) { viewModel.onCellClicked(index) }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(onClick = viewModel::onResetClicked) {Text("Reiniciar")}
    }

}

@Composable
fun DifficultySelector(selected: Difficulty, onSelect: (Difficulty) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Difficulty.entries.forEach { difficulty ->
            val isSelected = difficulty == selected
            Button(
                onClick = { onSelect(difficulty) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isSelected)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(difficulty.label)
            }

        }
    }
}

@Composable
fun Cell(mark: Player?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .padding(4.dp)
            .border(1.dp, Color.Black)
            .clickable(enabled = mark == null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        val painter = when (mark) {
            Player.X -> painterResource(id = R.drawable.x)
            Player.O -> painterResource(id = R.drawable.circle)
            null -> null
        }
        painter?.let {
            Image(
                painter = it,
                contentDescription = mark?.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            )
        }
    }
}