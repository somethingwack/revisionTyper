Param(
    [string]$Configuration = "release",
    [string]$JavaHome = $env:JAVA_HOME
)

if (-not (Test-Path -LiteralPath "gradlew.bat")) {
    Write-Error "Run this script from the project root (where gradlew.bat lives)."
    exit 1
}

if ([string]::IsNullOrEmpty($JavaHome)) {
    Write-Error "JAVA_HOME is not set. Point it to a JDK 17+ that includes jpackage."
    exit 1
}

$jpackage = Join-Path $JavaHome 'bin/jpackage.exe'
if (-not (Test-Path -LiteralPath $jpackage)) {
    Write-Error "Cannot find jpackage.exe inside $JavaHome. Install a full JDK (not JRE)."
    exit 1
}

Write-Host "Cleaning and assembling distribution..." -ForegroundColor Cyan
& .\gradlew.bat clean installDist --console=plain
if ($LASTEXITCODE -ne 0) {
    Write-Error "Gradle build failed."
    exit $LASTEXITCODE
}

$inputDir = Join-Path (Resolve-Path .).Path 'build\install\RevisionTyper\lib'
if (-not (Test-Path -LiteralPath $inputDir)) {
    Write-Error "Cannot find distribution lib directory at $inputDir"
    exit 1
}

$destDir = Join-Path (Resolve-Path .).Path 'build\jpackage'
New-Item -ItemType Directory -Force -Path $destDir | Out-Null

$iconPath = Join-Path (Resolve-Path .).Path 'src\main\resources\icons\RevWriter.ico'

Write-Host "Running jpackage..." -ForegroundColor Cyan
& $jpackage `
    --type exe `
    --name RevisionTyper `
    --app-version 1.0.0 `
    --input $inputDir `
    --main-jar "RevisionTyper-1.0.0.jar" `
    --main-class com.revisiontyper.RevisionMain `
    --dest $destDir `
    --icon $iconPath `
    --win-shortcut `
    --win-menu

if ($LASTEXITCODE -ne 0) {
    Write-Error "jpackage failed."
    exit $LASTEXITCODE
}

Write-Host "Windows installer created at $destDir" -ForegroundColor Green
