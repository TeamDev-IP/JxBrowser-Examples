# Eclipse RCP application with embedded JxBrowser

This project is an example Eclipse workspace that demonstrates how to use JxBrowser in Eclipse RCP
projects.

Creating of this example project is described in
the [tutorial](https://jxbrowser-support.teamdev.com/docs/tutorials/eclipse/rcp-application.html).

## Build and launch

1. Build the project using Maven and Apache Tycho:
   
   ```bash
   $ ./mvn clean verify
   ```
   
2. Navigate to the `com.teamdev.jxbrowser.demo.product/target/products` directory:

   ```bash
   $ cd com.teamdev.jxbrowser.demo.product/target/products
   $ ls      
   com.teamdev.jxbrowser.demo.product-win32.win32.x86_64.zip
   com.teamdev.jxbrowser.demo.product-win32.win32.aarch64.zip
   com.teamdev.jxbrowser.demo.product-linux.gtk.aarch64.tar.gz     
   com.teamdev.jxbrowser.demo.product-linux.gtk.x86_64.tar.gz      
   com.teamdev.jxbrowser.demo.product-macosx.cocoa.aarch64.tar.gz
   com.teamdev.jxbrowser.demo.product-macosx.cocoa.x86_64.tar.gz

   ```
3. Unpack the archive for your platform.
4. Double-click the `eclipse[.exe]` file in the unpacked archive.

