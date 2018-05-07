const element = document.getElementById('counter');
const observer = new MutationObserver(
    function(mutations) {
        window.java.onDomChanged(element.innerHTML);
    });
const config = {childList: true};
observer.observe(element, config);
