FROM ubuntu:20.04

ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=UTC

RUN apt update

# Install JDK.
RUN apt install -y openjdk-8-jdk

# Install Chromium's runtime dependencies.
RUN apt install -y \
    libasound2 \
    libatk-bridge2.0-0 \
    libatk1.0-0 \
    libatspi2.0-0 \
    libc6 \
    libcairo2  \
    libcups2 \
    libdbus-1-3 \
    libdrm2 \
    libexpat1 \
    libfontconfig1 \
    libgbm1 \
    libgcc-s1 \
    libglib2.0-0 \
    libgtk-3-0 \
    libnspr4 \
    libnss3 \
    libpango-1.0-0 \
    libpulse0 \
    libx11-6 \
    libxcb1 \
    libxcomposite1 \
    libxdamage1 \
    libxext6 \
    libxfixes3 \
    libxkbcommon0 \
    libxrandr2 \
    libxshmfence1 \
    libxtst6 \
    xdg-utils

# Install a virtual X11 server that is required for Chromium to work.
RUN apt install -y xvfb

# Copy a script that starts a virtual X11 server and launches the Java application.
COPY startup.sh .

# Make sure the script is executable.
RUN chmod +x startup.sh

# Copy the Java project
COPY project/ project

# Build the Java project.
RUN cd project && ./gradlew build

WORKDIR /
ENTRYPOINT ["sh", "-c", "/startup.sh"]
