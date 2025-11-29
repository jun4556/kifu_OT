# Code Style and Conventions

## Language
- **Primary**: Java (GWT/Java 8)
- **Target**: JavaScript (via GWT compiler)

## Naming Conventions

### Java Code
- **Classes**: PascalCase (e.g., `UMLArtifact`, `GWTUMLDrawer`, `DrawerPanel`)
- **Methods**: camelCase (e.g., `getSymbolsOverview`, `createDiagram`)
- **Variables**: camelCase
- **Constants**: UPPER_SNAKE_CASE (expected)
- **Packages**: lowercase with dots (e.g., `com.objetdirect.gwt.umlapi.client`)

### File Organization
- Source files: `src/`
- Compiled classes: `war/WEB-INF/classes/`
- Libraries: `war/WEB-INF/lib/`
- Static resources: `src/.../public/` and `war/`

## Coding Standards

### Java
- **Encoding**: UTF-8
- **Java Version**: Java 8 (source="8" target="8")
- **Line Endings**: CRLF (Windows)

### GWT Specific
- Client-side code: `client` package
- Server-side code: `server` package
- Shared beans: `client/beans` package
- GWT module definitions: `.gwt.xml` files
- Entry point: Specified in `.gwt.xml` as `<entry-point class="..."/>`

### Service Interfaces
GWT RPC pattern:
- Synchronous interface: `XxxService.java`
- Asynchronous interface: `XxxServiceAsync.java`

## Package Structure Pattern
```
com.objetdirect.gwt.[module].client/
  ├── artifacts/      # UI artifacts/elements
  ├── beans/         # Data beans
  ├── helpers/       # Helper utilities
  ├── [feature]/     # Feature packages
  │   ├── Service.java
  │   └── ServiceAsync.java
  └── public/        # Static resources (CSS, JS, images)
```

## Documentation
- Javadoc generation included in build
- Output: `doc/` directory
- Custom stylesheet: `doc/redstylesheet.css`
- Japanese comments and documentation widely used

## Comment Style
- Project contains mixed Japanese and English comments
- Documentation files in `drawer/`:
  - `NextAction.txt` - Next action items
  - `DBMemo.txt` - Database notes
  - `Exercise.txt` - Exercise/task notes
  - `reflection.txt` - Reflection feature notes

## Text Resources
- Japanese UI text managed in `TextResource.java` or `DrawerTextResource.java`
- Full-width character handling: `Zenkaku.java` helper class

## Build Configuration
- `build.xml`: Ant build scripts
- Debug enabled: `debug="true" debuglevel="lines,vars,source"`
- No warnings: `nowarn="true"`

## Best Practices
1. Always use UTF-8 encoding for Java files
2. Follow GWT client/server package separation
3. Use GWT RPC pattern for async services
4. Include source files in JAR for GWT compilation
5. Test in hosted mode before GWT compile
6. Document in Japanese for this project
