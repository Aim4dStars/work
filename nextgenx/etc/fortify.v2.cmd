set PATH=%PATH%;%M2_HOME%\bin;"d:\Program Files\HP_Fortify\HP_Fortify_SCA_and_Apps_4.31\bin"

echo "mobile-client:: start uploading to fortify"

SET WEB_SCAN_FPR_FILE=artifacts/fortify/MobileClient-WebView-Dev.fpr
SET IOS_SCAN_FPR_FILE=artifacts/fortify/MobileClient-IOS-Dev.fpr
SET ANDROID_SCAN_FPR_FILE=artifacts/fortify/MobileClient-Android-Dev.fpr
SET PLATFORM=%1

mkdir artifacts
mkdir artifacts\fortify

REM WebView scan
if "%PLATFORM%" == "web" (
    echo "mobile-client:: scanning web code"
    sourceanalyzer -b MobileClient-WebView-Dev -clean
    sourceanalyzer -Xss8G -Xms8G -Xmx8G -b MobileClient-WebView-Dev -exclude "/app/lib:app/mocks" "/app/**/*.js" ""/app/**/*.html" -debug-verbose -logfile artifacts/fortify/web.log
    sourceanalyzer -Xss8G -Xms8G -Xmx8G -b MobileClient-WebView-Dev -scan -f %WEB_SCAN_FPR_FILE% -debug-verbose -logfile artifacts/fortify/web.log
    ReportGenerator -Xmx8G -Xms8G -Xss8G -format pdf -f "artifacts/fortify/MobileClient-WebView-Dev.pdf" -template OWASP2013.xml -source %WEB_SCAN_FPR_FILE%
    echo "mobile-client:: web code scan completed"
)

REM ios app scan
if "%PLATFORM%" == "ios" (
    echo "mobile-client:: scanning ios code"
    sourceanalyzer -b MobileClient-IOS-Dev -clean
    sourceanalyzer -Xss8G -Xms8G -Xmx8G -b MobileClient-IOS-Dev xcodebuild -workspace "platforms/ios/BT Panorama.xcworkspace" -scheme "BT Panorama" -configuration "Debug" -sdk iphoneos
    sourceanalyzer -Xss8G -Xms8G -Xmx8G -b MobileClient-IOS-Dev -scan -f %IOS_SCAN_FPR_FILE% -debug-verbose -logfile artifacts/fortify/ios.log
    ReportGenerator -Xmx8G -Xms8G -Xss8G -format pdf -f "$(pwd)/artifacts/fortify/MobileClient-IOS-Dev.pdf" -template OWASP2013.xml -source %IOS_SCAN_FPR_FILE%
    echo "mobile-client:: ios code scan completed"
)

REM android app scan
if "%PLATFORM%" == "android" (
    echo "mobile-client:: scanning android code $ANDROID_HOME"
    sourceanalyzer -b MobileClient-Android-Dev -clean
    sourceanalyzer -Xss8G -Xms8G -Xmx8G -b MobileClient-Android-Dev -cp "platforms/android/libs/**/*.jar" -extdirs "platforms/android/build:extras/google/m2repository:extras/android/m2repository:platforms/android-22" -source "1.7" "platforms/android/src" -debug-verbose -logfile artifacts/fortify/android.log
    sourceanalyzer -Xss8G -Xms8G -Xmx8G -b MobileClient-Android-Dev -scan -f %ANDROID_SCAN_FPR_FILE% -debug-verbose -logfile artifacts/fortify/android.log
    ReportGenerator -Xmx8G -Xms8G -Xss8G -format pdf -f "artifacts/fortify/MobileClient-Android-Dev.pdf" -template OWASP2013.xml -source %ANDROID_SCAN_FPR_FILE%
    echo "mobile-client:: android code scan completed"
)