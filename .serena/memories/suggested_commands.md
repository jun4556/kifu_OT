# Suggested Commands (Windows)

## Build Commands

### API Module
```cmd
cd api
ant dist
```
Individual targets:
- `ant javac` - Compile Java sources
- `ant jar` - Create JAR file
- `ant javadoc` - Generate documentation
- `ant clean` - Clean build artifacts

### Drawer Module
```cmd
cd drawer
ant build
```
Individual targets:
- `ant javac` - Compile Java sources
- `ant gwtc` - GWT compile to JavaScript
- `ant war` - Create WAR file (KIfU4.war)
- `ant clean` - Clean build artifacts

## Development Mode
```cmd
cd drawer
ant hosted
```
Alternative:
```cmd
cd drawer
ant oophm
```

## Database Commands
Import SQL files using MySQL:
```cmd
mysql -u root -p gwtumldrawer < drawer\kifu6.sql
```

SQL files available:
- `api\20150903kifu3.sql`
- `api\20161019kifu5.sql`
- `api\operation_log.sql` (OT implementation)
- `drawer\kifu6.sql`
- `drawer\kifu6_akagidp.sql`

Create operation_log table (OT implementation):
```cmd
setup_database.bat
```
or manually:
```cmd
mysql -u root -p < api\operation_log.sql
```

## OT Implementation Commands

### Download Required Libraries
```cmd
download_libraries.bat
```
This downloads:
- diff-match-patch-1.2.jar
- javax.websocket-api-1.1.jar
- gson-2.8.9.jar

### Build with OT Support
```cmd
cd drawer
ant clean
ant build
ant war
```

### Deploy to Tomcat
```cmd
copy drawer\KIfU4.war %CATALINA_HOME%\webapps\
```

Note: Requires Tomcat 8 or later for WebSocket support.

## Windows Shell Commands

### Navigation
- `cd api` - Change to api directory
- `cd drawer` - Change to drawer directory
- `cd ..` - Go up one directory
- `dir` - List directory contents
- `dir /s /b *.java` - Find all Java files recursively

### File Operations
- `type filename` - Display file contents
- `more filename` - Display file with pagination
- `copy source dest` - Copy file
- `move source dest` - Move/rename file
- `del filename` - Delete file
- `mkdir dirname` - Create directory
- `rmdir /s dirname` - Delete directory tree

### Search
- `find "text" filename` - Search in file
- `findstr /s /i "pattern" *.java` - Recursive search in Java files

## Git Commands
```cmd
git status
git add .
git commit -m "message"
git push
git pull
git log --oneline
git branch
```

## Full Build Process
```cmd
rem Build API first
cd api
ant clean
ant dist

rem Then build drawer
cd ..\drawer
ant clean
ant build
ant war
```

## Quick Compile (Java only, no GWT)
```cmd
cd drawer
ant javac
```

## Project Setup
Ensure GWT SDK is installed at:
- `C:\gwt-2.8.2-custom`

Verify Java version:
```cmd
java -version
javac -version
```
Should be Java 8 or compatible.
