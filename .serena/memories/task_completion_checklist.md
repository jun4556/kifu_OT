# Task Completion Checklist

## After Code Changes

### 1. Compile Java Code
```cmd
# If api module was changed
cd api
ant javac

# If drawer module was changed
cd drawer
ant javac
```
✓ Check for compilation errors
✓ Fix any errors before proceeding

### 2. GWT Compile (drawer only)
```cmd
cd drawer
ant gwtc
```
⚠️ Note: GWT compilation takes time (Java to JavaScript translation)
- JVM args used: `-Xss16M -Xmx1024M`
- May take several minutes
✓ Verify successful compilation

### 3. Full Build Verification
```cmd
# API module
cd api
ant dist

# Drawer module  
cd drawer
ant build
```

### 4. Test in Development Mode (Optional)
```cmd
cd drawer
ant hosted
```
Or:
```cmd
cd drawer
ant oophm
```
✓ Verify UI works correctly
✓ Test new functionality
✓ Check browser console for errors

### 5. Create Deployment Package (Before Deploy)
```cmd
cd drawer
ant war
```
Output: `KIfU4.war`

## Clean Build (If Issues Occur)
```cmd
# Clean all
cd api
ant clean

cd ..\drawer
ant clean

# Rebuild
cd ..\api
ant dist

cd ..\drawer
ant build
```

## Pre-Commit Checklist
- [ ] No compilation errors
- [ ] GWT compilation successful
- [ ] Code follows project conventions
- [ ] If OT-related changes: libraries downloaded to drawer/war/WEB-INF/lib/
- [ ] If database changes: SQL migration tested
- [ ] Integration tests passed (if applicable)
- [ ] UTF-8 encoding maintained
- [ ] Comments/documentation updated (Japanese if applicable)
- [ ] Tested in browser (if UI changes)
- [ ] Database schema changes documented (if any)

## Code Review Points
- [ ] Existing functionality still works
- [ ] New features work as expected
- [ ] UI displays correctly
- [ ] No console errors
- [ ] Database operations successful (if applicable)
- [ ] Proper error handling

## Testing (Manual)
Since no automated tests are configured:
- [ ] Test in hosted mode
- [ ] Verify browser compatibility
- [ ] Check operation logs (if applicable)
- [ ] Test with sample exercises/tasks
- [ ] Verify data persistence

## Deployment Preparation
- [ ] WAR file created successfully
- [ ] Database migrations prepared (if needed)
- [ ] Configuration files updated
- [ ] `appengine-web.xml` configured correctly

## Environment Verification
- [ ] GWT SDK at `C:\gwt-2.8.2-custom`
- [ ] Java 8 or compatible installed
- [ ] MySQL running (if testing locally)
- [ ] All dependencies in `war/WEB-INF/lib/`

## Logging and Debugging
- Client-side logs: Browser console
- Server-side logs: Application server logs
- Operation logs: Database `operation_log` table
- Canvas logs: `drawer\war\canvasLog.txt` (if used)

## Documentation Updates
- [ ] Javadoc generated (if public API changed)
- [ ] NextAction.txt updated (if needed)
- [ ] README files updated (if applicable)
