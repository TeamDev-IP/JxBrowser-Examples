var element = document.getElementById('counter');
var observer = new MutationObserver(
    function(mutations) {
        window.java.onDomChanged(element.innerHTML);
    });
var config = {childList: true};
observer.observe(element, config);


