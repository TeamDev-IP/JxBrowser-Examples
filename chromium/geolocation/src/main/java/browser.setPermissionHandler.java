browser.setPermissionHandler(new PermissionHandler() {
    @Override
    public PermissionStatus onRequestPermission(PermissionRequest request) {
        if (request.getPermissionType() == PermissionType.GEOLOCATION) {
            return PermissionStatus.GRANTED;
        }
        return PermissionStatus.DENIED;
    }
});
