package com.example.triqui.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triqui.R
import com.example.triqui.data.ScoreData
import com.example.triqui.logic.Difficulty
import com.example.triqui.logic.Player

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

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Board(
                cells = state.cells,
                onCellClick = viewModel::onCellClicked,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )

            Spacer(Modifier.width(24.dp))

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = state.statusText, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                ScoreBoard(score = state.score)
                Spacer(Modifier.height(16.dp))
                DifficultySelector(
                    selected = state.difficulty,
                    onSelect = viewModel::onDifficultySelected
                )
                Spacer(Modifier.height(16.dp))
                Button(onClick = viewModel::onResetClicked) { Text("Reiniciar") }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = state.statusText, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))
            ScoreBoard(score = state.score)
            Spacer(Modifier.height(16.dp))

            DifficultySelector(
                selected = state.difficulty,
                onSelect = viewModel::onDifficultySelected
            )
            Spacer(Modifier.height(16.dp))

            Board(
                cells = state.cells,
                onCellClick = viewModel::onCellClicked,
                modifier = Modifier.weight(1f).fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Button(onClick = viewModel::onResetClicked) { Text("Reiniciar") }
        }
    }
}

@Composable
fun Board(
    cells: List<Player?>,
    onCellClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val boardSize = minOf(maxWidth, maxHeight)
        val cellSize = boardSize / 3

        Column(modifier = Modifier.size(boardSize)) {
            for (row in 0..2) {
                Row {
                    for (col in 0..2) {
                        val index = row * 3 + col
                        Cell(
                            mark = cells[index],
                            size = cellSize,
                            onClick = { onCellClick(index) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreBoard(score: ScoreData, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth()) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            ScoreItem(label = "Tú", value = score.humanWins)
        }
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            ScoreItem(label = "Empates", value = score.ties)
        }
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            ScoreItem(label = "Computador", value = score.computerWins)
        }
    }
}

@Composable
fun ScoreItem(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value.toString(), style = MaterialTheme.typography.titleLarge)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
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
fun Cell(mark: Player?, size: Dp, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(size)
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