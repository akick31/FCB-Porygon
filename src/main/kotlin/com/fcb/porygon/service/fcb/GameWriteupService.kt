package com.fcb.porygon.service.fcb

import com.fcb.porygon.models.game.GameScenario
import com.fcb.porygon.repositories.GameWriteupRepository
import org.springframework.stereotype.Service

@Service
class GameWriteupService(
    private val gameWriteupRepository: GameWriteupRepository,
) {
    /**
     * Get a game message by scenario
     * @param gameScenario
     */
    fun getGameMessageByScenario(
        gameScenario: GameScenario
    ): String {
        val writeups = gameWriteupRepository.findByScenario(
            gameScenario.result.description,
            gameScenario.batterOnFirst,
            gameScenario.batterOnSecond,
            gameScenario.batterOnThird,
            gameScenario.runsScored
        )

        return if (writeups.isNotEmpty()) {
            writeups.random().gameWriteup
        } else {
            "No message found"
        }
    }
}
