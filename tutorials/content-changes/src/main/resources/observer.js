function createObserver() {
    var element = document.body;
    var observer = new MutationObserver(
        function (mutations) {
            mutations.forEach(
                function (mutation) {
                    for (var node of mutation.addedNodes) {
                        if (node.className === 'countdown') {
                            window.java.onDomChanged()
                        }
                    }
                })
        });
    var config = {childList: !0};
    observer.observe(element, config)
}
createObserver();

