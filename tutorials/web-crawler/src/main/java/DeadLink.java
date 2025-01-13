/*
 *  Copyright 2025, TeamDev. All rights reserved.
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

import com.teamdev.jxbrowser.examples.webcrawler.Link;
import com.teamdev.jxbrowser.net.NetError;
import java.util.Objects;

/**
 * A problematic or dead link.
 */
final class DeadLink extends Link {

    private final NetError netError;

    private DeadLink(String url, NetError netError) {
        super(url);
        checkNotNull(netError);
        this.netError = netError;
    }

    /**
     * Returns a {@code DeadLink} instance for the given problematic {@code url}
     * and the {@code netError} status describing the reason why the URL link is
     * problematic or dead.
     *
     * @param url      the problematic or dead link URL
     * @param netError the status describing the reason why the URL link is
     *                 problematic or dead
     */
    static DeadLink of(String url, NetError netError) {
        return new DeadLink(url, netError);
    }

    /**
     * Returns the network error describing the reason why the link is treated
     * as problematic or dead.
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
        if (!super.equals(o)) {
            return false;
        }
        DeadLink deadLink = (DeadLink) o;
        return netError == deadLink.netError;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), netError);
    }

    @Override
    public String toString() {
        return super.toString() + " " + netError;
    }
}
