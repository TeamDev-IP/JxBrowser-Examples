#!/bin/sh
#
#  Copyright 2025, TeamDev. All rights reserved.
#
#  Redistribution and use in source and/or binary forms, with or without
#  modification, must retain the above copyright notice and the following
#  disclaimer.
#
#  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
#  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
#  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
#  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
#  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
#  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
#  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
#  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
#  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
#  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
#  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#

# Launch a virtual X11 server for Chromium.
#
# If DISPLAY is not set, start a virtual X server (headless mode).
# If DISPLAY is set, connect to the host X server (desktop mode).
if [ -z "${DISPLAY:-}" ]; then
  Xvfb :0 -screen 0 1920x1080x24+32 &
  export DISPLAY=:0
fi

# Switch to the project directory.
cd project

# Start the Java application.
./gradlew run
