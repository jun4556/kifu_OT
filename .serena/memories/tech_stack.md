# Tech Stack

## Core Technologies
- **GWT (Google Web Toolkit) 2.8.2-custom**: Java to JavaScript compiler
- **Apache Ant**: Build tool
- **Java 8**: Programming language (source="8" target="8")
- **MySQL**: Database

## Frontend
- GWT Canvas: Graphics rendering
- Dojo Toolkit: UI widgets (embedded in drawer/src/.../public/)
- WebSocket: Real-time communication
- gwt-canvas: Canvas library
- GWTCanvas and GlassPanel widgets

## Backend
- HikariCP: Database connection pooling
- GWT RPC: Client-server communication
- **Java WebSocket API (javax.websocket-api 1.1)**: WebSocket support for OT implementation
- **Gson 2.8.9**: JSON processing for WebSocket messages
- **diff-match-patch 1.2**: Text diff/patch operations for OT

## Development Environment
- **GWT SDK Path**: `C:\gwt-2.8.2-custom`
- **Encoding**: UTF-8
- **IDE**: Eclipse (`.settings/` directories present)
- **OS**: Windows
- **Application Server**: Tomcat 8+ (required for WebSocket support)

## Build System
Apache Ant with the following targets:

### api module
- `javac`: Compile Java sources
- `jar`: Create JAR file (gwt-umlapi.jar)
- `javadoc`: Generate Javadoc
- `dist`: Build everything (javadoc + jar)
- `clean`: Clean build artifacts

### drawer module
- `javac`: Compile Java sources
- `gwtc`: GWT compile (Java to JavaScript)
- `build`: Full build (javac + gwtc)
- `war`: Create WAR file (KIfU4.war)
- `hosted`: Run in hosted/dev mode
- `oophm`: Run in OOPHM hosted mode
- `clean`: Clean build artifacts

## JVM Settings
- GWT compile: `-Xss16M -Xmx1024M`
- Hosted mode: `-Xmx256M`

## Deployment
- Google App Engine compatible (appengine-web.xml present)
- WAR file deployment format
