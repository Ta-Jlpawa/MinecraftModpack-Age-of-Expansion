param([ValidateSet('original', 'azimuth', 'fixed', 'runtime')][string]$Mode = 'fixed')
$ErrorActionPreference = 'Stop'
Push-Location (Join-Path $PSScriptRoot '..')
try {
    $javaBin = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME 'bin' } else { 'G:/Java/jdk_21/bin' }
    $gradleCache = if ($env:GRADLE_USER_HOME) { Join-Path $env:GRADLE_USER_HOME 'caches/modules-2/files-2.1' } else { Join-Path $env:USERPROFILE '.gradle/caches/modules-2/files-2.1' }
    $entries = @(Get-Content 'build/moddev/routerClientSmokeLegacyClasspath.txt')
    $entries += @(Get-ChildItem 'build/createDependencies' -Filter '*.jar' | ForEach-Object FullName)
    $entries += (Resolve-Path 'build/moddev/artifacts/neoforge-21.1.248.jar').Path
    $entries += @(Get-ChildItem "$gradleCache/net.neoforged/bus/8.0.5","$gradleCache/net.neoforged.fancymodloader/loader/4.0.43" -Recurse -Filter '*.jar' | Where-Object Name -NotMatch 'sources|javadoc' | ForEach-Object FullName)
    $classpath = ($entries | Select-Object -Unique) -join ';'
    New-Item -ItemType Directory -Force 'build/registrateRegression' | Out-Null
    & "$javaBin/javac.exe" -proc:none -encoding UTF-8 -cp $classpath -d build/registrateRegression src/regression/java/RegistrateListenerRegression.java
    if ($LASTEXITCODE -ne 0) { throw 'Regression harness compilation failed' }
    & "$javaBin/java.exe" -cp "build/registrateRegression;$classpath" RegistrateListenerRegression $Mode
    if ($LASTEXITCODE -ne 0) { throw "Registrate listener regression failed ($Mode)" }
} finally { Pop-Location }
