# Welcome to JxBrowser Examples

This repository contains a set of [examples](#examples) and 
[tutorials](#tutorials) you can use to walk through the main features of 
[JxBrowser](https://www.teamdev.com/jxbrowser).

## About JxBrowser

JxBrowser is a commercial cross-platform Java library that lets you integrate a 
Chromium-based web browser control into your Java Swing, JavaFX, Compose, or 
SWT desktop application to display modern web pages built with HTML5, CSS3, 
JavaScript.

With JxBrowser, you can display modern web pages, PDFs, WebGL, work with DOM, 
JavaScript, WebRTC, network, printing, call Java from JavaScript, manage file 
downloads, convert HTML to PNG, debug web pages with DevTools, configure proxy, 
manage cookies, handle authentication, fill web forms, and much more.

## Examples

In the `examples` directory, you will find granular code samples that 
demonstrate how to use a single JxBrowser feature each. 

We group examples in five submodules. The `core` submodule shows the features
of JxBrowser's core API, and the rest of submodules are dedicated to the 
features specific for a UI toolkit: `swing`, `javafx`, `swt`, `compose`.

### Running examples

To run the examples:

1. Make sure your environment meets the
   [software and hardware requirements][requirements].

2. Clone this repository:
    ```bash
    git clone https://github.com/TeamDev-IP/JxBrowser-Examples
    ```
3. Open the Gradle project in `examples` directory in your favourite IDE:
   - [Intellij IDEA][idea]
   - [Eclipse][eclipse]
   - [NetBeans][netbeans]

4. [Get][get-evaluation] a free 30-day trial license key.

5. To run an example please specify the `jxbrowser.license.key` VM parameter:
    ```bash
    -Djxbrowser.license.key=<your_trial_license_key>
    ```
 
## Tutorials

In the `tutorials` folder you will find projects that we feature in the
[tutorials][tutorials] section on our website and our [blog][blog].

To run a specific tutorial, check `README.md` in its folder and 
[get][get-evaluation] a free 30-day trial license key.

## What's Next

- Learn about JxBrowser [architecture](https://jxbrowser-support.teamdev.com/docs/guides/introduction/architecture.html).
- Take a look at JxBrowser [features](https://jxbrowser-support.teamdev.com/docs/guides/engine.html).
- Explore the JxBrowser [API](https://jxbrowser-support.teamdev.com/docs/reference/).


## Terms and Privacy

The information in this repository is provided on the following terms: https://www.teamdev.com/terms-and-privacy


[requirements]: https://jxbrowser-support.teamdev.com/docs/guides/introduction/requirements.html
[idea]: https://www.jetbrains.com/help/idea/gradle.html#gradle_import
[eclipse]: https://marketplace.eclipse.org/content/buildship-gradle-integration#group-details
[netbeans]: https://netbeans.org/features/java/build-tools.html
[get-evaluation]: https://www.teamdev.com/jxbrowser#evaluate
[tutorials]: https://teamdev.com/jxbrowser/docs/tutorials/
[blog]: https://teamdev.com/jxbrowser/blog/category/tutorials/
