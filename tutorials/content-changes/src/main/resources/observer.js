function createObserver() {
    var element = document.body;
    var observer = new MutationObserver(
        function (mutations) {
            mutations.forEach(
                function (mutation) {
                    for (var node of mutation.addedNodes) {
                        if (node.className == 'class_name') {
                            window.java.onDomChanged()
                        }
                    }
                })
        });
    var config = {childList: !0};
    observer.observe(element, config)
}
createObserver();

