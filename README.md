# SupplyLens Android

A Kotlin Android application built with Jetpack Compose for supply chain management and blockchain integration.

## Architecture

This project follows a multi-module architecture:

### Core Modules
- **core:ui** - Shared UI components and theming (Material 3)
- **core:network** - Network layer with WalletConnect v2 integration and CEX connectors
- **core:domain** - Domain models and business logic interfaces (AlertPolicy, SecurityReport)
- **core:data** - Data layer and repositories (DataStore-based persistence)

### Feature Modules
- **feature:search** - Search functionality
- **feature:token** - Token management with security badges
- **feature:watchlist** - Watchlist features
- **feature:trade** - Trading functionality with wallet connection
- **feature:alerts** - Alert management
- **feature:settings** - App settings with alert threshold configuration

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Build System**: Gradle Kotlin DSL
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)
- **Code Quality**: ktlint, Detekt
- **Data Persistence**: DataStore (Preferences)

## Build Flavors

The app has three product flavors for different environments:

- **dev** - Development environment
  - Application ID: `com.eurotax.supplylens.dev`
  - API URL: `https://api-dev.supplylens.io`
  
- **stage** - Staging environment
  - Application ID: `com.eurotax.supplylens.stage`
  - API URL: `https://api-stage.supplylens.io`
  
- **prod** - Production environment
  - Application ID: `com.eurotax.supplylens`
  - API URL: `https://api.supplylens.io`

## Localization

The app supports the following languages:
- 🇬🇧 English (EN) - Default
- 🇵🇱 Polish (PL)
- 🇷🇺 Russian (RU) - TODO
- 🇺🇦 Ukrainian (UK) - TODO
- 🇨🇳 Simplified Chinese (ZH-CN) - TODO
- 🇰🇷 Korean (KO) - TODO
- 🇩🇪 German (DE) - TODO
- 🇪🇸 Spanish (ES) - TODO
- 🇫🇷 French (FR) - TODO
- 🇧🇷 Brazilian Portuguese (PT-BR) - TODO
- 🇹🇷 Turkish (TR) - TODO
- 🇮🇳 Hindi (HI) - TODO
- 🇻🇳 Vietnamese (VI) - TODO

## Features

### Alert Thresholds
The app provides configurable alert thresholds for token risk assessment:

**Presets:**
- **Conservative** - More risk-averse thresholds for cautious investors
- **Balanced** - Default/recommended thresholds (default)
- **Aggressive** - More tolerant thresholds for higher risk tolerance

**Threshold Categories:**
- LP Lock: Lock duration and percentage thresholds
- Taxes: Buy/sell tax percentage limits
- Top Holders: Concentration limits
- Liquidity: Liquidity pool size requirements
- Unlocks: Token unlock schedules
- Futures: Funding rates and open interest
- Volume Anomaly: Volume/price pattern detection

**Free vs PRO:**
- **FREE**: View current thresholds (read-only)
- **PRO**: Edit thresholds, switch presets, set per-token overrides

### Security Features
- Security report scoring (0-100)
- Risk badges: Low, Medium, High, Critical
- LP lock analysis
- Tax assessment (buy/sell)
- Ownership information (renounced, upgradeable)

### Wallet Integration
- WalletConnect v2 support (stub implementation)
- EVM wallet connection
- Transaction and message signing
- Session management

### CEX Integration
- Trading service interface
- Mock implementations for Binance and KuCoin
- Order placement and cancellation
- Symbol information retrieval

## Building the Project

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 17 or newer
- Android SDK 34

### Build Commands

```bash
# Make gradlew executable
chmod +x gradlew

# Clean build
./gradlew clean

# Build debug APK (all flavors)
./gradlew assembleDebug

# Build specific flavor
./gradlew assembleDevDebug
./gradlew assembleStageDebug
./gradlew assembleProdDebug

# Build release bundle
./gradlew bundleRelease

# Run linters
./gradlew ktlintCheck
./gradlew detekt

# Format code
./gradlew ktlintFormat

# Run tests
./gradlew test

# Install debug APK
./gradlew installDevDebug
```

## GitHub Actions

### Build Workflow (`.github/workflows/build.yml`)
Runs on push to main/develop and on pull requests:
1. Validates Gradle wrapper
2. Runs ktlint
3. Runs detekt
4. Runs unit tests
5. Builds debug APK

### Release Workflow (`.github/workflows/release-internal.yml`)
Triggered by tags (`v*.*.*`) or manual dispatch:
1. Builds release bundle
2. Signs with keystore from secrets
3. Uploads to Google Play Internal track
4. Uploads AAB as workflow artifact

**Required Secrets:**
- `KEYSTORE_BASE64` - Base64-encoded keystore file
- `KEYSTORE_PASSWORD` - Keystore password
- `KEY_ALIAS` - Key alias
- `KEY_ALIAS_PASSWORD` - Key alias password
- `PLAY_JSON_KEY` - Google Play service account JSON

## Code Style

This project uses:
- **ktlint** for Kotlin code formatting
  - Wildcard imports are banned
  - Max line length: 120 characters
  
- **Detekt** for static code analysis
  - Magic numbers allowed in tests
  - Custom configuration in `config/detekt/detekt.yml`
  
- **.editorconfig** for IDE settings

Run `./gradlew ktlintFormat` to auto-format code.

### Pre-commit Hook (Optional)

Add a pre-commit hook to run linters automatically:

```bash
#!/bin/bash
# .git/hooks/pre-commit

echo "Running ktlint..."
./gradlew ktlintCheck

if [ $? -ne 0 ]; then
    echo "ktlint check failed. Run './gradlew ktlintFormat' to fix."
    exit 1
fi

echo "Running detekt..."
./gradlew detekt

if [ $? -ne 0 ]; then
    echo "detekt check failed. Please fix the issues."
    exit 1
fi
```

## Feature Flags

Feature flags are controlled via BuildConfig:

```kotlin
// Enable mock CEX trading (development only)
if (BuildConfig.DEBUG) {
    val mockBinance = MockBinanceTradingService(enableMock = true)
}
```

## Security Notes

⚠️ **Important Security Guidelines:**

1. **No API keys in code** - All API keys and secrets must be stored in:
   - GitHub Secrets (for CI/CD)
   - Local `local.properties` (for local development, gitignored)
   
2. **No private keys** - Wallet private keys are NEVER stored in the app
   - WalletConnect manages keys securely
   
3. **Keystore security** - Release keystore must be:
   - Stored securely (not in repository)
   - Base64-encoded for GitHub Secrets
   - Password-protected

4. **ProGuard** - Release builds use ProGuard for:
   - Code obfuscation
   - Resource shrinking
   - Minification

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Run linters and tests (`./gradlew ktlintCheck detekt test`)
4. Commit your changes (`git commit -m 'Add amazing feature'`)
5. Push to the branch (`git push origin feature/amazing-feature`)
6. Open a Pull Request

See [PULL_REQUEST_TEMPLATE.md](.github/PULL_REQUEST_TEMPLATE.md) for PR guidelines.

## Issue Reporting

Use the issue templates:
- [Bug Report](.github/ISSUE_TEMPLATE/bug.md)
- [Feature Request](.github/ISSUE_TEMPLATE/feature.md)

## License

Copyright © 2025 Eurotax

## WalletConnect Integration

The app includes a stub implementation for WalletConnect v2 in the `core:network` module. This is a placeholder for future blockchain wallet integration.

To implement:
1. Add WalletConnect SDK dependencies
2. Implement `WalletConnector` interface
3. Configure supported chains and methods
4. Handle session proposal and approval flows

## CEX Integration

CEX trading services are defined in `core/network/cex/CexTradingService.kt`:

- `MockBinanceTradingService` - Binance-style mock
- `MockKuCoinTradingService` - KuCoin-style mock

To enable in development:
```kotlin
val cexService = MockBinanceTradingService(enableMock = true)
```

For production integration:
1. Add exchange SDK dependencies
2. Implement real API calls
3. Configure API credentials via secrets
4. Add proper error handling and rate limiting

