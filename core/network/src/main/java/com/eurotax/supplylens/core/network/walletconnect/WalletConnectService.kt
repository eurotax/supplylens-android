package com.eurotax.supplylens.core.network.walletconnect

/**
 * WalletConnect v2 stub implementation
 * This is a placeholder for future WalletConnect integration
 */
interface WalletConnectService {
    /**
     * Initialize WalletConnect session
     */
    suspend fun initialize()

    /**
     * Connect to a wallet using WalletConnect
     */
    suspend fun connect(): Result<String>

    /**
     * Disconnect from the current wallet session
     */
    suspend fun disconnect()

    /**
     * Get the current connection status
     */
    fun isConnected(): Boolean

    /**
     * Get the connected wallet address
     */
    fun getWalletAddress(): String?
}

/**
 * Stub implementation of WalletConnectService
 */
class WalletConnectServiceImpl : WalletConnectService {
    private var connected = false
    private var walletAddress: String? = null

    override suspend fun initialize() {
        // TODO: Initialize WalletConnect v2 SDK
    }

    override suspend fun connect(): Result<String> {
        // TODO: Implement actual WalletConnect connection
        return Result.failure(NotImplementedError("WalletConnect integration not yet implemented"))
    }

    override suspend fun disconnect() {
        connected = false
        walletAddress = null
    }

    override fun isConnected(): Boolean = connected

    override fun getWalletAddress(): String? = walletAddress
}
