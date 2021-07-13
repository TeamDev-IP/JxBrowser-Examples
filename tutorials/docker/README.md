# Deploying a JxBrowser application with Docker

This example demonstrates how you can prepare and launch a Docker image with a JxBrowser application.

Please visit our [Docker tutorial](https://jxbrowser-support.teamdev.com/docs/tutorials/integration/docker.html) that will guide you through this example step-by-step.

## Building and Launching an Example

1. [Insert your license key](https://jxbrowser-support.teamdev.com/docs/guides/licensing.html#adding-the-license-to-a-project)
   into [the demo application](project/src/main/java/DemoApp.java#L43).
2. Build a Docker image:
   `sudo docker build -t jxbrowser .`
3. Start a container:
   `sudo docker run --shm-size=1gb -t jxbrowser`
   _Please note that the `--shm-size=1gb` argument is required. By default, Docker's shared memory is limited to 64MB, which is not enough for Chromium to work._
