# SiemensJava
**Notes on changes:**
- Added validation exception handler in controller for neat client feedback.
- Ensured correct HTTP status codes (404 for missing, 204 for delete, 201 for create).
- Removed shared mutable state in `processItemsAsync`; now uses local futures.
- Comprehensive unit tests on service and controller for CRUD, async behavior, validation paths.
