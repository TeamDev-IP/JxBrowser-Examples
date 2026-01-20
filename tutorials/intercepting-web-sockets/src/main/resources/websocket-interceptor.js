// Save reference to the native WebSocket.
const NativeWebSocket = window.WebSocket;

// Override the global WebSocket constructor.
window.WebSocket = function(url, protocols) {
    const socket = new NativeWebSocket(url, protocols);

    // Intercept incoming messages.
    socket.addEventListener('message', (event) => {
        if (window.onWebSocketReceived) {
            window.onWebSocketReceived(event.data);
        }
    });

    // Intercept outgoing messages.
    const originalSend = socket.send;
    socket.send = function(data) {
        if (window.onWebSocketSent) {
            window.onWebSocketSent(data);
        }
        originalSend.call(socket, data);
    };

    return socket;
};

// Preserve the prototype chain.
window.WebSocket.prototype = NativeWebSocket.prototype;
