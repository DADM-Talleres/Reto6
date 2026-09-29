package com.example.triqui.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triqui.logic.ComputerPlayer
import com.example.triqui.logic.Difficulty
import com.example.triqui.logic.GameResult
import com.example.triqui.logic.Player
import com.example.triqui.logic.TicTacToeGame
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

data class GameUiState(
    val cells: List<Player?> = List(9) { null },
    val currentPlayer: Player = Player.X,
    val statusText: String = "Turno de X",
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val moveCount: Int = 0,
    val lastMovedBy: Player? = null
)

class GameViewModel : ViewModel() {
    private val game = TicTacToeGame()
    private val human = Player.X
    private var difficulty = Difficulty.MEDIUM
    private var computer = ComputerPlayer(self = Player.O, strategy = difficulty.toStrategy())
    private var moveCount = 0
    private var lastMovedBy: Player? = null
    private val _uiState = MutableStateFlow(toUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    fun onCellClicked(index: Int) {
        if (game.currentPlayer != human) return
        if (!game.playMove(index)) return

        lastMovedBy = human
        moveCount++
        _uiState.value = toUiState()

        if (game.result == GameResult.InProgress && game.currentPlayer == computer.self) {
            makeComputerMove()
        }
    }

    fun onDifficultySelected(newDifficulty: Difficulty) {
        difficulty = newDifficulty
        computer = ComputerPlayer(self = Player.O, strategy = newDifficulty.toStrategy())
        game.reset()
        moveCount = 0
        lastMovedBy = null
        _uiState.value = toUiState()
    }

    private fun makeComputerMove() {
        viewModelScope.launch {
            delay(1000.milliseconds)
            val move = computer.chooseMove(game.board)
            if (move >= 0) {
                game.playMove(move)
                lastMovedBy = computer.self
                moveCount++
                _uiState.value = toUiState()
            }
        }
    }

    fun onResetClicked() {
        game.reset()
        moveCount = 0
        lastMovedBy = null
        _uiState.value = toUiState()
    }

    private fun toUiState(): GameUiState {
        val status = when (val r = game.result) {
            is GameResult.InProgress ->
                if (game.currentPlayer == human) "Tu turno" else "Turno del computador"
            is GameResult.Winner ->
                if (r.player == human) "Ganaste!" else "El computador gana!"
            is GameResult.Draw -> "Empate!"
        }
        return GameUiState(
            cells = game.board.cells,
            currentPlayer = game.currentPlayer,
            statusText = status,
            difficulty = difficulty,
            moveCount = moveCount,
            lastMovedBy = lastMovedBy
        )
    }
}