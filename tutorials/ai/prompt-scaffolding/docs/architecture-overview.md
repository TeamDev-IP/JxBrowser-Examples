# Desktop Web App Architecture Overview

This document describes the reusable architecture pattern for creating cross-platform desktop applications using JxBrowser with Java backend and modern web frontend technologies.

## ⚠️ CRITICAL PREREQUISITES FOR LLMs ⚠️

### JxBrowser License Requirement
- **COMMERCIAL PRODUCT** requiring license from [TeamDev JxBrowser](https://www.teamdev.com/jxbrowser)
- **MUST SET** environment variable: `JXBROWSER_LICENSE_KEY=your-license-key`
- **USE IN CODE**: `System.getenv("JXBROWSER_LICENSE_KEY")`

### 📦 Required Gradle Imports
**MANDATORY**: Always include these imports at the top of `build.gradle.kts`:
```kotlin
// MANDATORY at top of build.gradle.kts
import com.google.protobuf.gradle.id
import org.gradle.api.JavaVersion.VERSION_17
```

### SwingUtilities Threading Rule
- ❌ NEVER wrap entire application in `SwingUtilities.invokeLater()`
- ✅ Only wrap Swing component creation

## High-Level Architecture

Hybrid desktop architecture combining:
- **Java Backend**: Business logic, gRPC services, data persistence
- **Web Frontend**: React + TypeScript + Tailwind CSS
- **JxBrowser**: Chromium engine bridging Java and JavaScript
- **gRPC Communication**: Protocol Buffers for type-safe communication

```
┌─────────────────────────────────────────────────────────────┐
│                    Desktop Application                      │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │   Java Backend  │◄──►│        JxBrowser Engine         │ │
│  │                 │    │                                 │ │
│  │ • Business Logic│    │ • Chromium Browser              │ │
│  │ • gRPC Services │    │ • JavaScript-Java Bridge       │ │
│  │ • Data Models   │    │ • Custom URL Schemes           │ │
│  │ • File I/O      │    │                                 │ │
│  └─────────────────┘    │  ┌───────────────────────────┐  │ │
│                         │  │     Web Frontend          │  │ │
│                         │  │ • React + TypeScript      │  │ │
│                         │  │ • shadcn/ui + Tailwind    │  │ │
│                         │  │ • gRPC-Web Client         │  │ │
│                         │  └───────────────────────────┘  │ │
│                         └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

## Project Structure

```
desktop-web-app/
├── build.gradle.kts          # Main build configuration
├── proto/                    # Protocol Buffer definitions
├── src/main/java/            # Java backend
│   ├── App.java             # Entry point
│   ├── AppInitializer.java  # Bootstrap & configuration
│   ├── AppDetails.java      # App metadata & paths
│   ├── [domain]/            # Business domains
│   │   ├── *Service.java    # gRPC implementations
│   │   └── *.java          # Data models
│   └── production/          # Production utilities
│       ├── MimeTypes.java
│       └── UrlRequestInterceptor.java
└── web-app/                 # Frontend application
    ├── package.json
    ├── vite.config.ts
    ├── buf.gen.yaml        # Protobuf generation
    └── src/
        ├── main.tsx        # Entry point
        ├── gen/           # Generated protobuf types
        ├── rpc/           # gRPC clients
        └── components/    # React components
```

### Frontend Structure
```
web-app/
├── package.json             # NPM dependencies and scripts
├── vite.config.ts          # Build tool configuration
├── tailwind.config.js      # Styling configuration
├── buf.gen.yaml           # Protobuf code generation config
├── src/
│   ├── main.tsx           # Application entry point
│   ├── App.tsx            # Main React component
│   ├── gen/               # Generated protobuf types (auto-generated)
│   ├── rpc/               # gRPC client configuration
│   │   ├── client.ts      # gRPC transport setup
│   │   └── *-client.ts    # Service-specific clients
│   ├── components/        # React components
│   │   ├── ui/           # Reusable UI components (shadcn/ui)
│   │   └── *.tsx         # Feature-specific components
│   ├── hooks/            # Custom React hooks
│   ├── lib/              # Utility functions
│   ├── context/          # React context providers
│   ├── converter/        # Data conversion utilities
│   └── storage/          # Client-side storage utilities
└── dist/                 # Built assets (auto-generated)
```

## Build Configuration

### Complete build.gradle.kts Template

```kotlin
// ⚠️ MANDATORY IMPORTS - Always include at the top
import com.google.protobuf.gradle.id
import org.gradle.api.JavaVersion.VERSION_17

plugins {
    java
    id("com.google.protobuf") version "0.9.1"
    id("application")
    
    // ⚠️ CRITICAL: Official JxBrowser Gradle plugin
    id("com.teamdev.jxbrowser") version "1.2.1"
}

java {
    sourceCompatibility = VERSION_17
    targetCompatibility = VERSION_17
}

jxbrowser {
    version = "8.9.4"  // Set JxBrowser version
}

dependencies {
    // ⚠️ CRITICAL: JxBrowser dependencies using official plugin
    implementation(jxbrowser.currentPlatform)  // Auto-detects platform
    implementation(jxbrowser.swing)            // For Swing integration
    
    // gRPC & Protocol Buffers
    implementation("com.linecorp.armeria:armeria-grpc:1.30.1")
    implementation("com.linecorp.armeria:armeria:1.30.1")
    implementation("com.linecorp.armeria:armeria-grpc-protocol:1.30.1")
    implementation("com.google.protobuf:protobuf-java:3.21.12")
    
    // Utilities
    implementation("com.google.code.gson:gson:2.10.1")
}

application {
    applicationDefaultJvmArgs = listOf("-Dapp.dev.mode=true")
    mainClass.set("com.teamdev.jxbrowser.examples.App")
}
sourceSets {
    main {
        proto {
            srcDir("proto")
            include("*.proto")
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.21.12"
        plugins {
            // ✅ CORRECT - requires import above
            id("grpc") { artifact = "io.grpc:protoc-gen-grpc-java:1.67.1" }
        }
        generateProtoTasks {
            all().forEach { task ->
                task.plugins {
                    // ✅ CORRECT - requires import above
                    id("grpc") {}
                }
            }
        }
    }
}

// Build pipeline tasks
tasks.register("buildWeb") {
    dependsOn("installNpmPackages", "generateJsProto")
    // Builds frontend assets into web-app/dist/
}

tasks.jar {
    archiveFileName.set(mainJar)  // Explicitly set JAR name
    dependsOn(tasks.named("buildWeb"))
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from({
        configurations.runtimeClasspath.get().map {
            if (it.isDirectory) it else zipTree(it)
        }
    })

    doLast {
        copy {
            from("build/libs/$mainJar")
            into("build/dist")
        }
    }
}
```

### 2. ✅ JxBrowser License Usage
```java
// ✅ CORRECT - Always use environment variable directly
var optionsBuilder = EngineOptions.newBuilder(HARDWARE_ACCELERATED)
        .licenseKey(System.getenv("JXBROWSER_LICENSE_KEY")); // Direct env var access
```

### 3. ✅ Required Environment Variable
```bash
# Set this before running the application
export JXBROWSER_LICENSE_KEY=your-license-key-from-teamdev
```

### 4. ⚠️ CRITICAL SwingUtilities Usage
```java
// ❌ WRONG - Never wrap entire application
SwingUtilities.invokeLater(() -> {
    // All application code here - BLOCKS AWT thread!
});

// ✅ CORRECT - Only wrap UI creation
public void initialize() throws InterruptedException {
    setupLogging();                    // Main thread
    var engine = createEngine();       // Main thread
    var browser = engine.newBrowser();  // Main thread
    setupUI(engine, browser);          // Uses invokeLater internally for UI only
    initializeRpc(browser);            // Main thread - server.blockUntilShutdown() OK
}
```

### 6. 🛠️ Enable DevTools in Development Mode
```java
// ✅ CORRECT - Always enable devtools in development mode
private static void setupBrowserCallbacks(Browser browser) {
    browser.set(InjectJsCallback.class, params -> {
        JsObject window = params.frame().executeJavaScript("window");
        if (window != null) {
            window.putProperty("rpcPort", RPC_PORT);
        }
        return InjectJsCallback.Response.proceed();
    });

    if (!isProductionMode()) {
        browser.devTools().show();  // ✅ Enable devtools in dev mode
    }
}
```

### Frontend Dependencies (package.json)

```json
{
  "dependencies": {
    // Core React ecosystem
    "react": "^19.1.0",
    "react-dom": "^19.1.0",
    "react-router-dom": "^6.22.1",

    // gRPC communication (ConnectRPC codegen v2)
    "@connectrpc/connect": "2.0.0-rc.2",
    "@connectrpc/connect-web": "2.0.0-rc.2",
    "@bufbuild/protobuf": "2.2.1",

    // UI framework
    "@radix-ui/react-avatar": "^1.1.3",
    "@radix-ui/react-dialog": "^1.1.6",
    "@radix-ui/react-dropdown-menu": "^2.1.6",
    "@radix-ui/react-label": "^2.1.2",
    "@radix-ui/react-popover": "^1.1.6",
    "@radix-ui/react-switch": "^1.1.3",
    "tailwindcss": "^4.1.1",        // Tailwind 4
    "@tailwindcss/postcss": "^4.1.1",
    "lucide-react": "^0.507.0",     // Icons
    "clsx": "^2.1.0",
    "tailwind-merge": "^3.1.0",

    // Development tools
    "vite": "^5.1.0",               // Build tool
    "typescript": "^5.2.2"          // Type safety
  },
  "devDependencies": {
    // Protocol Buffer generation (codegen v2)
    "@bufbuild/buf": "1.46.0",
    "@bufbuild/protoc-gen-es": "2.2.1"
  }
}
```

### Frontend Development Guidelines

#### 🎨 Use shadcn/ui Components (NOT Custom Components)

**CRITICAL**: Always use shadcn/ui components instead of building components from scratch.

**✅ CORRECT - Install and use shadcn/ui components:**
```bash
# Install components as needed
npx shadcn@latest add button
npx shadcn@latest add input
npx shadcn@latest add card
npx shadcn@latest add dialog
npx shadcn@latest add dropdown-menu
npx shadcn@latest add form
npx shadcn@latest add table
npx shadcn@latest add tabs
npx shadcn@latest add select
npx shadcn@latest add checkbox
```

**Then use in React components:**
```tsx
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"

export function MyComponent() {
  return (
    <Card>
      <CardHeader>
        <CardTitle>Settings</CardTitle>
      </CardHeader>
      <CardContent>
        <Input placeholder="Enter value..." />
        <Button variant="default">Save</Button>
      </CardContent>
    </Card>
  )
}
```

**TypeScript/Frontend Issues:**
- **"Cannot mix BigInt and other types"**: This is fixed by protobuf utility functions that handle BigInt conversion

**❌ WRONG - Don't build basic components from scratch:**
```tsx
// Don't do this - use shadcn/ui instead
export function CustomButton({ children, onClick }) {
  return (
    <button 
      className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
      onClick={onClick}
    >
      {children}
    </button>
  )
}
```

**Available shadcn/ui Components:**
- `button`, `input`, `textarea`, `select`, `checkbox`, `radio-group`
- `card`, `dialog`, `popover`, `tooltip`, `sheet`
- `table`, `form`, `tabs`, `accordion`, `collapsible`
- `dropdown-menu`, `navigation-menu`, `breadcrumb`
- `alert`, `badge`, `progress`, `skeleton`, `spinner`
- And many more - check [shadcn/ui docs](https://ui.shadcn.com)

## Data Model Architecture

### Protocol Buffer Definitions
Data models are defined once in `.proto` files and code is generated for both Java and TypeScript:

```protobuf
// Example: prefs.proto
syntax = "proto3";
package com.teamdev.jxbrowser.prefs;

// Service definition
service PrefsService {
  rpc GetAccount(google.protobuf.Empty) returns (Account);
  rpc SetAccount(Account) returns (google.protobuf.Empty);
  // ... other methods
}

// Data models
message Account {
  string email = 1;
  string full_name = 2;
  TwoFactorAuthentication two_factor_authentication = 3;
  bool biometric_authentication = 4;
}

enum TwoFactorAuthentication {
  EMAIL = 0;
  SMS = 1;
  PASS_KEY = 2;
}
```

### Code Generation Process
1. **Java**: Gradle plugin generates classes in `build/generated/source/proto/`
2. **TypeScript**: Buf CLI generates types in `web-app/src/gen/` using **codegen v2**

### Proto Generation Configuration
The proto files should be located in the **root `proto/` directory**, and the `buf.gen.yaml` should reference it:

```yaml
# web-app/buf.gen.yaml
version: v2
inputs:
  - directory: ../proto  # Points to root proto directory
plugins:
  - local: protoc-gen-es
    opt: target=ts
    out: src/gen
```

## Communication Layer

### gRPC Service (Java)
```java
public final class PrefsService extends PrefsServiceImplBase {
    @Override
    public void getAccount(Empty request, StreamObserver<Account> responseObserver) {
        responseObserver.onNext(appPrefs.account());
        responseObserver.onCompleted();
    }
}
```

### gRPC Client (TypeScript)
```typescript
import { createGrpcWebTransport } from "@connectrpc/connect-web";
import { createClient } from "@connectrpc/connect";
import { PrefsService } from "@/gen/prefs_pb.ts";

declare const rpcPort: Number; // Injected by JxBrowser

const transport = createGrpcWebTransport({
    baseUrl: `http://localhost:${rpcPort}`,
});

const prefsClient = createClient(PrefsService, transport);
```

### JavaScript-Java Bridge
JxBrowser injects the gRPC port into the JavaScript context. **The RPC port should be the same for both web UI and Java app (e.g., 9090 or 50051)**:

```java
// Java side (AppInitializer.java)
private static final int RPC_PORT = 50051; // Same port for both frontend and backend

browser.set(InjectJsCallback.class, params -> {
    JsObject window = params.frame().executeJavaScript("window");
    if (window != null) {
        window.putProperty("rpcPort", RPC_PORT);
    }
    return InjectJsCallback.Response.proceed();
});
```

### CORS Configuration for Armeria Server
The gRPC server must be configured with proper CORS support. This requires the `armeria-grpc-protocol` dependency:

```java
// AppInitializer.java - initializeRpc method
private static void initializeRpc(Browser browser) throws InterruptedException {
    var serverBuilder = Server.builder().http(RPC_PORT);
    var corsBuilder = CorsService.builder(appUrl())
            .allowRequestMethods(HttpMethod.POST)
            .allowRequestHeaders(
                    HttpHeaderNames.CONTENT_TYPE,
                    HttpHeaderNames.of("x-grpc-web"),
                    HttpHeaderNames.of("x-user-agent"))
            .exposeHeaders(GrpcHeaderNames.GRPC_STATUS,
                    GrpcHeaderNames.GRPC_MESSAGE,
                    GrpcHeaderNames.ARMERIA_GRPC_THROWABLEPROTO_BIN);

    serverBuilder.service(GrpcService.builder()
                    .addService(new YourService())
                    .build(),
            corsBuilder.newDecorator(),
            LoggingService.newDecorator());

    try (var server = serverBuilder.build()) {
        server.start();
        browser.navigation().loadUrl(appUrl());
        server.blockUntilShutdown();
    }
}
```

### Frontend Build Pipeline
1. **NPM Install**: Install Node.js dependencies
2. **Generate Protobuf**: Create TypeScript types from `.proto` files
3. **Vite Build**: Compile and bundle React application
4. **Asset Integration**: Include built assets in Java JAR

## URL Request Interception

### Custom Scheme Handling
To properly set up URL request interception, you must register a custom scheme as shown in [JxBrowser documentation](https://teamdev.com/jxbrowser/docs/guides/network/#registering-custom-scheme). In production mode, the application uses a custom URL scheme (`jxbrowser://`) to serve frontend assets from JAR resources instead of external files.

### URL Request Interceptor Implementation

**Complete implementation with proper imports and Java 17+ syntax:**

```java
// Required imports - use these exact imports
import com.teamdev.jxbrowser.net.HttpHeader;
import com.teamdev.jxbrowser.net.HttpStatus;
import com.teamdev.jxbrowser.net.UrlRequestJob;
import com.teamdev.jxbrowser.net.callback.InterceptUrlRequestCallback;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

public final class UrlRequestInterceptor implements InterceptUrlRequestCallback {
    private static final String CONTENT_ROOT = "/web";
    private static final String INDEX_HTML = "/index.html";
    private static final String CONTENT_TYPE = "Content-Type";

    @Override
    public Response on(Params params) {
        var url = params.urlRequest().url();
        
        // Check if URL matches our custom scheme
        if (url.contains(AppDetails.appUrl())) {
            var uri = URI.create(url);
            var fileName = uri.getPath().equals("/") ? INDEX_HTML : uri.getPath();
            
            // Create URL request job and serve file
            var job = urlRequestJob(params, fileName);
            readFile(fileName, job);
            return InterceptUrlRequestCallback.Response.intercept(job);
        }
        
        // Let other requests proceed normally
        return InterceptUrlRequestCallback.Response.proceed();
    }

    private static UrlRequestJob urlRequestJob(InterceptUrlRequestCallback.Params params, String file) {
        var builder = UrlRequestJob.Options.newBuilder(HttpStatus.OK);
        builder.addHttpHeader(contentType(file));
        return params.newUrlRequestJob(builder.build());
    }

    private static void readFile(String fileName, UrlRequestJob job) {
        try (var stream = UrlRequestInterceptor.class.getResourceAsStream(CONTENT_ROOT + fileName)) {
            if (stream == null) {
                throw new FileNotFoundException(CONTENT_ROOT + fileName);
            }
            job.write(stream.readAllBytes());
            job.complete();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static HttpHeader contentType(String file) {
        return HttpHeader.of(CONTENT_TYPE, MimeTypes.mimeType(file).value());
    }
}
```

### Resource Serving Pipeline

1. **URL Matching**: Interceptor checks if request URL matches the custom scheme
2. **Path Resolution**: Maps URL path to resource file (defaults to `index.html` for root)
3. **Resource Loading**: Reads file from JAR classpath (`/web/` directory)
4. **MIME Type Detection**: Determines appropriate Content-Type header
5. **Response Creation**: Creates HTTP response with proper headers and content

### MIME Type Handling

The application includes a comprehensive MIME type mapping system:

```java
import com.teamdev.jxbrowser.net.MimeType;
import java.util.Map;
import java.util.HashMap;
import static java.util.Locale.ENGLISH;
import static com.teamdev.jxbrowser.net.MimeType.OCTET_STREAM;

public final class MimeTypes {
    private static final Map<String, MimeType> MIME_TYPES = Map.of(
        "html", MimeType.of("text/html"),
        "css", MimeType.of("text/css"),
        "js", MimeType.of("application/javascript"),
        "json", MimeType.of("application/json"),
        "png", MimeType.of("image/png"),
        "jpg", MimeType.of("image/jpeg"),
        "jpeg", MimeType.of("image/jpeg"),
        "gif", MimeType.of("image/gif"),
        "svg", MimeType.of("image/svg+xml"),
        "ico", MimeType.of("image/x-icon"),
        "woff", MimeType.of("font/woff"),
        "woff2", MimeType.of("font/woff2"),
        "ttf", MimeType.of("font/ttf")
    );

    public static MimeType mimeType(String fileName) {
        var fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        return MIME_TYPES.getOrDefault(fileExtension.toLowerCase(ENGLISH), OCTET_STREAM);
    }
}
```

**Supported File Types**:
- **Web Assets**: `.html`, `.css`, `.js`, `.json`
- **Images**: `.png`, `.jpg`, `.gif`, `.svg`, `.ico`, `.webp`
- **Fonts**: `.woff`, `.woff2`, `.ttf`
- **Media**: `.mp3`, `.mp4`, `.wav`, `.webm`
- **Documents**: `.pdf`, `.txt`, `.xml`

### Engine Configuration
```java
private static Engine createEngine() {
    var optionsBuilder = EngineOptions.newBuilder(HARDWARE_ACCELERATED)
            .userDataDir(AppDetails.INSTANCE.chromiumUserDataDir())
            .licenseKey(System.getenv("JXBROWSER_LICENSE_KEY")); // ✅ CORRECT - Direct env var access
    
    // Register custom scheme only in production mode
    if (isProductionMode()) {
        // Use Scheme type, not String
        private static final Scheme SCHEME = of("jxbrowser");
        optionsBuilder.addScheme(SCHEME, new UrlRequestInterceptor());
    }
    
    return Engine.newInstance(optionsBuilder.build());
}
```

### Application Details Configuration

The `AppDetails` class provides application-specific configuration:

```java
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppDetails {
    public static final AppDetails INSTANCE = new AppDetails();
    
    private static final String APP_NAME = "YourApp";
    private static final String CUSTOM_HOST = "my-app.com";
    
    public Path chromiumUserDataDir() {
        return userDataDir().resolve("chromium");
    }
    
    public Path appResourcesDir() {
        return userDataDir();
    }
    
    private Path userDataDir() {
        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");
        
        if (os.contains("win")) {
            return Paths.get(System.getenv("LOCALAPPDATA"), APP_NAME);
        } else if (os.contains("mac")) {
            return Paths.get(userHome, "Library", "Application Support", APP_NAME);
        } else {
            return Paths.get(userHome, ".local", "share", APP_NAME);
        }
    }
    
    public static String appUrl() {
        return isProductionMode() ? 
            CUSTOM_SCHEME + "://" + CUSTOM_HOST : 
            "http://localhost:5173";
    }
    
    public static boolean isProductionMode() {
        return !Boolean.getBoolean("app.dev.mode");
    }
    
    public static String getAppIconFileName() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("mac") ? "app.icns" : "app.ico";
    }
}
```

### Development vs Production Modes

**Development Mode** (set via JVM property in build.gradle.kts):
```kotlin
application {
    applicationDefaultJvmArgs = listOf("-Dapp.dev.mode=true")
    mainClass.set("com.teamdev.jxbrowser.examples.App")
}
```

- Frontend served by Vite dev server (HMR enabled)
- JxBrowser loads `http://localhost:5173`
- No URL interception needed
- DevTools enabled
- Live reload for both frontend and backend changes
- gRPC server runs alongside the main Java application (not as separate Gradle task)

**Production Mode**:
- Frontend assets bundled into JAR resources
- JxBrowser loads `jxbrowser://my-app.com` (custom scheme)
- URL interceptor serves assets from classpath
- Optimized builds
- Single executable JAR

### Resource Directory Structure

Frontend build assets are included in the JAR under `/web/`:

```
JAR resources/
├── web/                    # Frontend build output
│   ├── index.html         # Main HTML file
│   ├── assets/           # Vite-generated assets
│   │   ├── *.js         # JavaScript bundles
│   │   ├── *.css        # Stylesheets
│   │   └── *.woff2      # Font files
│   └── *.ico            # Favicon and icons
├── mime-types.properties  # MIME type mappings
└── *.png                 # Application icons
```

### Implementation Guidelines for New Projects

// Only wrap UI code in invokeLater
SwingUtilities.invokeLater(() -> {
// Only Swing component creation here
});

// Main thread handles blocking operations
initializeRpc(browser); // server.blockUntilShutdown() OK here
```

3. **Set Up Resource Path**:
   - Ensure frontend builds to correct output directory
   - Configure Gradle to include assets in JAR
   - Update interceptor's `CONTENT_ROOT` path

4. **Handle MIME Types**:
   - Copy `mime-types.properties` or create custom mappings
   - Implement appropriate Content-Type header logic

5. **Test Both Modes**:
   - Development: Direct Vite server access
   - Production: Custom scheme with intercepted requests

This URL interception pattern enables seamless packaging of web assets within the desktop application while maintaining development workflow efficiency.

## Data Persistence

### Java-side Persistence
- **JSON Files**: Using Gson for preferences (`preferences.json`)
- **Binary Files**: Using Protocol Buffers binary format for complex data
- **Platform-specific Locations**: 
  - Windows: `%LOCALAPPDATA%/AppName/`
  - macOS: `~/Library/Application Support/AppName/`

### Frontend State Management
- React hooks for local state
- gRPC clients for server communication
- Local storage for temporary data

## Packaging and Distribution

### Native Installers

The build system includes tasks to create native installers using Java's `jpackage` tool. These tasks are defined in `build.gradle.kts`:

#### macOS DMG Installer
```bash
# Build macOS DMG installer
./gradlew packageDmg
```

**Generated Gradle Task:**
```kotlin
tasks.register<Exec>("packageDmg") {
    dependsOn(tasks.build)

    commandLine(
        "jpackage",
        "--input", "./build/dist",              // Input directory with JAR
        "--main-jar", mainJar,                  // Main JAR file name
        "--name", applicationName,              // Application name
        "--app-version", version,               // Version from build.gradle.kts
        "--type", "dmg",                       // macOS DMG format
        "--main-class", application.mainClass.get(),
        "--dest", "./build/installer",         // Output directory
        "--icon", "src/main/resources/app.icns", // macOS icon file
    )
}
```

#### Windows EXE Installer
```bash
# Build Windows EXE installer  
./gradlew packageExe
```

**Generated Gradle Task:**
```kotlin
tasks.register<Exec>("packageExe") {
    dependsOn(tasks.build)

    commandLine(
        "jpackage",
        "--input", "./build/dist",              // Input directory with JAR
        "--main-jar", mainJar,                  // Main JAR file name
        "--name", applicationName,              // Application name
        "--app-version", version,               // Version from build.gradle.kts
        "--type", "exe",                       // Windows EXE format
        "--main-class", application.mainClass.get(),
        "--dest", "./build/installer",         // Output directory
        "--win-dir-chooser",                   // Allow user to choose install directory
        "--win-menu",                          // Add to Windows Start Menu
        "--win-shortcut-prompt",               // Prompt to create desktop shortcut
        "--icon", "src/main/resources/app.ico", // Windows icon file
    )
}
```

#### Required Configuration Variables
```kotlin
// In build.gradle.kts
group = "com.teamdev.jxbrowser.gallery"
version = "1.0"

val applicationName = "JxBrowserWebApp"
val mainJar = "$applicationName-$version.jar"

application {
    mainClass.set("com.teamdev.jxbrowser.examples.App")
}
```

#### Prerequisites for Native Packaging

**For macOS DMG:**
- macOS development environment
- Xcode Command Line Tools installed
- Valid Apple Developer ID (for code signing)

**For Windows EXE:**
- Windows development environment
- [WiX Toolset](https://github.com/wixtoolset/wix3/releases/tag/wix3141rtm) 3.14.1 or higher
- Valid code signing certificate (optional but recommended)

#### Generated Output
- **macOS**: `./build/installer/YourApp-1.0.dmg`
- **Windows**: `./build/installer/YourApp-1.0.exe`

### JAR Distribution
```bash
# Fat JAR with all dependencies (platform-independent)
./gradlew build
# Generates: ./build/dist/YourApp-1.0.jar
```

**Important**: The JAR task is configured to use an explicit filename and create a fat JAR:

```kotlin
tasks.jar {
    archiveFileName.set(mainJar)  // Uses custom name instead of project name
    dependsOn(tasks.named("buildWeb"))
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from({
        configurations.runtimeClasspath.get().map {
            if (it.isDirectory) it else zipTree(it)
        }
    })

    doLast {
        copy {
            from("build/libs/$mainJar")      // Copy from libs to dist
            into("build/dist")               // Native installers expect it here
        }
    }
}
```

This configuration ensures:
- **Custom JAR Name**: Uses `applicationName-version.jar` instead of project directory name
- **Fat JAR**: Includes all dependencies for standalone execution
- **Proper Location**: Copies JAR to `build/dist/` where native installer tasks expect it
- **Main Class**: Sets manifest for direct execution via `java -jar`

### Complete Build Pipeline
```bash
# Full build with web assets and JAR
./gradlew clean build

# Build native installers (platform-specific)
./gradlew packageDmg    # On macOS
./gradlew packageExe    # On Windows
```

## Frontend Framework Requirements

### Tailwind CSS 4 Setup
This architecture uses Tailwind CSS 4. Here's the complete setup:

**1. Install Dependencies**:
```json
// package.json
{
  "devDependencies": {
    "tailwindcss": "^4.1.1",
    "@tailwindcss/postcss": "^4.1.1",
    "postcss": "^8.4.35"
  }
}
```

**2. Tailwind Configuration**:
```javascript
// tailwind.config.js
export default {
  darkMode: ["class"],
  content: [
    './index.html',
    './src/**/*.{js,ts,jsx,tsx}',
  ],
  theme: {
    container: {
      center: true,
      padding: "2rem",
      screens: {
        "2xl": "1400px",
      },
    },
    extend: {
      colors: {
        border: "hsl(var(--border))",
        input: "hsl(var(--input))",
        ring: "hsl(var(--ring))",
        background: "hsl(var(--background))",
        foreground: "hsl(var(--foreground))",
        primary: {
          DEFAULT: "hsl(var(--primary))",
          foreground: "hsl(var(--primary-foreground))",
        },
        secondary: {
          DEFAULT: "hsl(var(--secondary))",
          foreground: "hsl(var(--secondary-foreground))",
        },
        muted: {
          DEFAULT: "hsl(var(--muted))",
          foreground: "hsl(var(--muted-foreground))",
        },
        accent: {
          DEFAULT: "hsl(var(--accent))",
          foreground: "hsl(var(--accent-foreground))",
        },
        card: {
          DEFAULT: "hsl(var(--card))",
          foreground: "hsl(var(--card-foreground))",
        },
      },
      borderRadius: {
        lg: "var(--radius)",
        md: "calc(var(--radius) - 2px)",
        sm: "calc(var(--radius) - 4px)",
      },
    },
  },
  plugins: [],
}
```

**3. PostCSS Configuration**:
```javascript
// postcss.config.js
export default {
  plugins: {
    '@tailwindcss/postcss': {},
  },
}
```

**4. CSS Setup**:
```css
/* src/index.css */
@import "tailwindcss";

@layer base {
  :root {
    --background: 0 0% 100%;
    --foreground: 222.2 84% 4.9%;
    --card: 0 0% 100%;
    --card-foreground: 222.2 84% 4.9%;
    --popover: 0 0% 100%;
    --popover-foreground: 222.2 84% 4.9%;
    --primary: 222.2 47.4% 11.2%;
    --primary-foreground: 210 40% 98%;
    --secondary: 210 40% 96%;
    --secondary-foreground: 222.2 84% 4.9%;
    --muted: 210 40% 96%;
    --muted-foreground: 215.4 16.3% 46.9%;
    --accent: 210 40% 96%;
    --accent-foreground: 222.2 84% 4.9%;
    --destructive: 0 84.2% 60.2%;
    --destructive-foreground: 210 40% 98%;
    --border: 214.3 31.8% 91.4%;
    --input: 214.3 31.8% 91.4%;
    --ring: 222.2 84% 4.9%;
    --radius: 0.5rem;
  }

  .dark {
    --background: 222.2 84% 4.9%;
    --foreground: 210 40% 98%;
    --card: 222.2 84% 4.9%;
    --card-foreground: 210 40% 98%;
    --popover: 222.2 84% 4.9%;
    --popover-foreground: 210 40% 98%;
    --primary: 210 40% 98%;
    --primary-foreground: 222.2 47.4% 11.2%;
    --secondary: 217.2 32.6% 17.5%;
    --secondary-foreground: 210 40% 98%;
    --muted: 217.2 32.6% 17.5%;
    --muted-foreground: 215 20.2% 65.1%;
    --accent: 217.2 32.6% 17.5%;
    --accent-foreground: 210 40% 98%;
    --destructive: 0 62.8% 30.6%;
    --destructive-foreground: 210 40% 98%;
    --border: 217.2 32.6% 17.5%;
    --input: 217.2 32.6% 17.5%;
    --ring: 212.7 26.8% 83.9%;
  }
}
```

## Reusability Guidelines

### For New Projects

1. **Copy Core Structure**:
   - `build.gradle.kts` (update app name, main class, set development mode)
   - `AppInitializer.java` (core bootstrap logic, CORS configuration)
   - `AppDetails.java` (app-specific configuration)
   - `UrlRequestInterceptor.java` (for custom scheme handling)

2. **Define Data Models**:
   - Create `.proto` files in **root `proto/` directory**
   - Define services and messages for your domain
   - Ensure `buf.gen.yaml` points to `../proto`

3. **Implement Services**:
   - Extend `*ServiceImplBase` for each service
   - Implement business logic and data persistence
   - Configure CORS with `armeria-grpc-protocol` dependency

4. **Build Frontend**:
   - Use `web-app/` structure as template
   - Set up Tailwind CSS 4 configuration
   - Use **codegen v2** for TypeScript proto generation
   - Leverage generated TypeScript types with ConnectRPC

5. **Configure Build and Environment**:
   - Set up development mode with `-Dapp.dev.mode=true`
   - Configure `JXBROWSER_LICENSE_KEY` environment variable
   - Update `package.json` with project-specific dependencies
   - Ensure gRPC server runs in same process (not separate Gradle task)
   - Use consistent RPC port for frontend and backend

### Recommended Dependency Versions

**Java Dependencies:**
```kotlin
// JxBrowser 8.9.4 (latest version)
implementation(jxbrowser.macArm)    
implementation(jxbrowser.swing)     

// Armeria (gRPC server)
implementation("com.linecorp.armeria:armeria-grpc:1.30.1")
implementation("com.linecorp.armeria:armeria:1.30.1") 
implementation("com.linecorp.armeria:armeria-grpc-protocol:1.30.1")  // Required for CORS

// Protocol Buffers
implementation("com.google.protobuf:protobuf-java:3.21.12")

// Utilities
implementation("com.google.code.gson:gson:2.10.1")
implementation("javax.annotation:javax.annotation-api:1.3.2")
```

**Frontend Dependencies:**
```json
{
  "dependencies": {
    "react": "^19.1.0",
    "react-dom": "^19.1.0",
    "react-router-dom": "^6.22.1",
    "@connectrpc/connect": "2.0.0-rc.2",
    "@connectrpc/connect-web": "2.0.0-rc.2",
    "@bufbuild/protobuf": "2.2.1",
    "@radix-ui/react-avatar": "^1.1.3",
    "@radix-ui/react-dialog": "^1.1.6",
    "@radix-ui/react-dropdown-menu": "^2.1.6",
    "@radix-ui/react-label": "^2.1.2",
    "@radix-ui/react-popover": "^1.1.6",
    "@radix-ui/react-switch": "^1.1.3",
    "tailwindcss": "^4.1.1",
    "lucide-react": "^0.507.0",
    "vite": "^5.1.0",
    "typescript": "^5.2.2"
  },
  "devDependencies": {
    "@bufbuild/buf": "1.46.0",
    "@bufbuild/protoc-gen-es": "2.2.1"
  }
}
```

## Gradle Build Configuration

The [template](../template) directory contains a minimal Gradle setup that serves as a 
foundation for creating demos and examples. This directory is specifically designed to 
help LLMs (Language Learning Models) and developers quickly bootstrap new Gradle 
projects.

**Purpose:**
- Provides a clean Gradle environment for testing and demonstrations
- Allows running `gradlew --version` and future Gradle tasks
- Serves as a starting point for creating new components or features

**Structure:**
```
../template/
├── gradlew                           # Gradle wrapper script (Unix/macOS)
├── gradlew.bat                       # Gradle wrapper script (Windows)
├── settings.gradle.kts               # Basic Gradle settings
└── gradle/
└── wrapper/
├── gradle-wrapper.jar        # Gradle wrapper executable
└── gradle-wrapper.properties # Gradle wrapper configuration

**Usage:**
```bash
cd ../template
./gradlew --version  # Verify Gradle installation
./gradlew tasks      # List available tasks (when build.gradle.kts is added)
```

### JxBrowser License Configuration

**CRITICAL: JxBrowser license must be provided via environment variable**

JxBrowser is a **commercial library** that requires a valid license. The license is NOT available in Maven Central and must be obtained from TeamDev.

**License Setup:**
1. **Get License**: Obtain JxBrowser license from [TeamDev](https://www.teamdev.com/jxbrowser)
2. **Set Environment Variable**: `JXBROWSER_LICENSE_KEY=your-license-key-here`
3. **Use in Code**: Always read from environment variable

**Important Notes:**
- JxBrowser binaries are NOT in Maven Central
- License is required for both development and production
- TeamDev provides evaluation licenses for testing
- Contact TeamDev for commercial licensing

## Java Development Guidelines

### Java 17+ Syntax Requirements

This project uses **Java 17** or higher. LLMs and developers should use modern Java syntax including:

### ❌ Common Mistakes

```java
// WRONG - Don't use String for scheme
optionsBuilder.addScheme("jxbrowser", interceptor);

// WRONG - Missing protobuf import causes this error
create("grpc") { ... } // Should be id("grpc")

// WRONG - Blocks AWT thread
SwingUtilities.invokeLater(() -> {
    // All application code - BLOCKS when server.blockUntilShutdown()
});
```

**✅ CORRECT - Only wrap Swing UI code:**
```java
public void initialize() throws InterruptedException {
    // ✅ CORRECT - Non-UI code runs on main thread
    setupLogging();
    var engine = createEngine();
    var browser = engine.newBrowser();
    setupUI(engine, browser);        // This method uses invokeLater internally
    setupBrowserCallbacks(browser);
    initializeRpc(browser);          // This calls server.blockUntilShutdown() - OK on main thread
}

private static void setupUI(Engine engine, Browser browser) {
    // ✅ CORRECT - Only UI creation wrapped in invokeLater
    SwingUtilities.invokeLater(() -> {
        var frame = new JFrame("Dashboard");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                engine.close();
            }
        });
        
        setAppIcon(frame);
        frame.add(BrowserView.newInstance(browser), BorderLayout.CENTER);
        frame.setSize(1200, 800);
        frame.setMinimumSize(new Dimension(640, 560));
        frame.setMaximumSize(new Dimension(1280, 900));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    });
}
```

## Frontend Guidelines

### Use shadcn/ui Components
```bash
# Install components instead of building custom ones
npx shadcn@latest add button input card dialog
```

```tsx
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"

export function MyComponent() {
  return (
    <Card>
      <CardHeader>
        <CardTitle>Settings</CardTitle>
      </CardHeader>
      <CardContent>
        <Button>Save</Button>
      </CardContent>
    </Card>
  )
}
```

## Data Persistence

- **JSON Files**: Gson for preferences (platform-specific paths)
- **Binary Files**: Protocol Buffers for complex data
- **Locations**:
   - Windows: `%LOCALAPPDATA%/AppName/`
   - macOS: `~/Library/Application Support/AppName/`

## Reusability for New Projects

1. **Copy Structure**: `build.gradle.kts`, `AppInitializer.java`, `AppDetails.java`
2. **Define Models**: Create `.proto` files in root `proto/` directory
3. **Implement Services**: Extend `*ServiceImplBase` classes
4. **Build Frontend**: Use shadcn/ui, Tailwind CSS 4, TypeScript
5. **Configure Environment**: Set `JXBROWSER_LICENSE_KEY`

### Key Benefits
- **Type Safety**: Protocol Buffers across Java/TypeScript
- **Modern UI**: React ecosystem with professional components
- **Cross-platform**: Single codebase for Windows/macOS/Linux
- **Developer Experience**: Hot reload, TypeScript, modern tooling
- **Performance**: Native Java with optimized web rendering

This architecture provides a solid foundation for building professional desktop applications combining Java's power with modern web technologies.