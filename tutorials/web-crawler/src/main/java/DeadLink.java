/*
 *  Copyright 2021, TeamDev. All rights reserved.
 *
 *  Redistribution and use in source and/or binary forms, with or without
 *  modification, must retain the above copyright notice and the following
 *  disclaimer.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 *  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 *  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import static com.google.common.base.Preconditions.checkNotNull;

import com.teamdev.jxbrowser.net.NetError;
import java.util.Objects;

/**
 * Represents a problematic or dead link.
 */
public final class DeadLink {

    private final String url;
    private final NetError netError;

    private DeadLink(String url, NetError netError) {
        checkNotNull(url);
        checkNotNull(netError);

        this.url = url;
        this.netError = netError;
    }

    /**
     * Returns a {@code DeadLink} instance for the given problematic {@code url} and the {@code
     * netError} status describing the reason why the URL link is problematic or dead.
     *
     * @param url      the problematic or dead link URL
     * @param netError the status describing the reason why the URL link is problematic or dead
     */
    static DeadLink of(String url, NetError netError) {
        return new DeadLink(url, netError);
    }

    /**
     * Returns URL of the problematic or dead link.
     */
    String url() {
        return url;
    }

    /**
     * Returns the network error describing the reason why the URL link is problematic.
     */
    NetError netError() {
        return netError;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DeadLink deadLink = (DeadLink) o;
        return Objects.equals(url, deadLink.url) && netError == deadLink.netError;
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, netError);
    }

    @Override
    public String toString() {
        return "DeadLink{" +
                "url='" + url + '\'' +
                ", netError=" + netError +
                '}';
    }
}
