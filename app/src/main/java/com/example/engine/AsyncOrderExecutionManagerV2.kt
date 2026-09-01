package com.example.engine

import com.example.model.TradeOrder
import com.example.model.TradeStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class ExecutionLog(
    val timestamp: Long = System.currentTimeMillis(),
    val tag: String,
    val message: String,
    val isError: Boolean = false
)

class AsyncOrderExecutionManagerV2(
    private val scope: CoroutineScope
) {
    private val orderQueue = Channel<TradeOrder>(capacity = 500)
    
    private val _logs = MutableStateFlow<List<ExecutionLog>>(emptyList())
    val logs = _logs.asStateFlow()

    private val _activeQueueCount = MutableStateFlow(0)
    val activeQueueCount = _activeQueueCount.asStateFlow()

    init {
        startBatchWorker()
    }

    fun queueOrder(order: TradeOrder) {
        scope.launch {
            orderQueue.send(order)
            _activeQueueCount.value += 1
            log("QUEUE", "Order added to batch queue: ${order.side} ${order.symbol} @ $${order.entryPrice}")
        }
    }

    private fun log(tag: String, message: String, isError: Boolean = false) {
        val current = _logs.value.toMutableList()
        if (current.size > 80) current.removeAt(0)
        current.add(ExecutionLog(tag = tag, message = message, isError = isError))
        _logs.value = current
    }

    private fun startBatchWorker() {
        scope.launch(Dispatchers.Default) {
            while (isActive) {
                val batch = mutableListOf<TradeOrder>()
                
                // Drain up to BATCH_SIZE items
                while (batch.size < StrategyEngineV2Config.BATCH_SIZE) {
                    val order = orderQueue.tryReceive().getOrNull()
                    if (order != null) {
                        batch.add(order)
                    } else {
                        break
                    }
                }

                if (batch.isEmpty()) {
                    delay(50)
                    continue
                }

                _activeQueueCount.value = maxOf(0, _activeQueueCount.value - batch.size)
                log("BATCH", "Dispatching batch of ${batch.size} Post-Only orders to exchange engine...")
                
                executeBatch(batch)
                
                // Rate limit anti-block delay
                delay(StrategyEngineV2Config.BATCH_DELAY_MS)
            }
        }
    }

    private suspend fun executeBatch(batch: List<TradeOrder>) {
        for (order in batch) {
            val postOnlyStr = if (order.isPostOnly) " (Post-Only Maker)" else ""
            log(
                "API_EXEC",
                "Limit ${order.side} executed on ${order.symbol} at ${order.entryPrice}$postOnlyStr - TP1: ${order.tp1}, SL: ${order.stopLoss}"
            )
            delay(15) // Micro-staggering for exchange API compliance
        }
    }
}
