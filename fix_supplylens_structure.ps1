# SupplyLens - Automatic Structure Fix Script
$ROOT = "C:\APLIKACJE\supplylens-android"
cd $ROOT

Write-Host "=== SupplyLens Structure Fix ===" -ForegroundColor Cyan

# Create directories
$dirs = @(
    "feature\search\src\main\java\com\eurotax\supplylens\feature\search",
    "feature\watchlist\src\main\java\com\eurotax\supplylens\feature\watchlist",
    "feature\alerts\src\main\java\com\eurotax\supplylens\feature\alerts"
)

foreach ($d in $dirs) {
    New-Item -ItemType Directory -Path $d -Force | Out-Null
    Write-Host "Created: $d" -ForegroundColor Green
}

# Move files
$moves = @{
    "SearchScreen_FINAL.kt" = "feature\search\src\main\java\com\eurotax\supplylens\feature\search\SearchScreen.kt"
    "WatchlistScreen_FINAL.kt" = "feature\watchlist\src\main\java\com\eurotax\supplylens\feature\watchlist\WatchlistScreen.kt"
    "AlertsScreen_FINAL.kt" = "feature\alerts\src\main\java\com\eurotax\supplylens\feature\alerts\AlertsScreen.kt"
}

foreach ($src in $moves.Keys) {
    if (Test-Path $src) {
        Copy-Item $src $moves[$src] -Force
        Write-Host "Moved: $src" -ForegroundColor Green
    }
}

# Cleanup
$temps = @("SearchScreen.kt", "SearchScreen_TEMP.kt", "WatchlistScreen.kt", "AlertsScreen.kt")
foreach ($t in $temps) {
    if (Test-Path $t) { Remove-Item $t -Force }
}

Write-Host "`nFix Complete!" -ForegroundColor Green
Write-Host "Next: .\gradlew.bat assembleDevDebug" -ForegroundColor Cyan
