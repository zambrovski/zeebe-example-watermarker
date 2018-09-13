package io.zeebe.example.watermark.command

data class RegisterJobWorkerCommand(
        val jobType: String,
        val workerName: String,
        val to: String
)
