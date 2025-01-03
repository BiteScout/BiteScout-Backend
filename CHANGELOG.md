# CHANGELOG

All the changes and updates documented in this file.

## [0.9.4] 2025-01-03
### Added
- Review documentation

## [0.9.3] 2025-01-01

### Added

- Unit tests for review and review interactions

### Fixed

- Review service delete interaction operation from wrong repository

## [0.9.2] - 2024-12-31

### Added

- Security configurations and authorization, authentication are completely done including relationships with other services.
- Tests are added for restaurant and user service, added related clients and new endpoints.
- Exceptions are handled in restaurant service.

### Fixed
- Subtle endpoint fixes.
---

## [0.9.1] - 2024-12-28

### Added

- Functionality for review, reservation and notification services extracting user id from the jwt and using it as a parameter automatically.
- Endpoint for deleting review interactions and getting a review's all interactions in the form of a like-dislike count and a list of text interactions.

### Fixed
- Email in notification not parsing the variables passed.
- Restaurant client not being able to parse Point type in notification service.
- Notification email displaying wrong date.

---

## [0.9.0] - 2024-12-26

### Added
- CHANGELOG.md file with latest updates.

### Updated
- Documentation for Restaurant and User services enhanced with JSON examples.
- Removed deprecated features from documentation (e.g., file storage service replaced with cloud solutions).

---

## [0.8.4] - 2024-12-22

### Fixed
- Role-related bugs in the Authentication service resolved.
- Verification mail configuration updated and Thymeleaf dependency added.
- ID generation in Ranking and Review services changed from `IDENTITY` to `UUID`.
- Addressed environmental variable handling for email templates in Authentication service.
- User request DTO updated to remove nested `UserDetails` object; fields flattened.
- Improved DTO visualization for location data in User service by implementing point serialization/deserialization.
- Fixed bugs in Ranking and Authentication services related to cloud URL integration.

### Added
- Security configuration for Restaurant and User services.
- Endpoints for deleting and finding users by username in User service.

---

## [0.8.0] - 2024-12-20

### Added
- Cloud services integration for file uploads in User service.
- Ranking service integrated with cloud storage solutions.

### Updated
- URL fields in User, Ranking, and Authentication configurations to include cloud URLs.

---

## [0.7.1] - 2024-12-17

### Fixed
- Bugs in Ranking service: `getAverageRating` and related endpoint issues.

---

## [0.7.0] - 2024-12-16

### Fixed
- Field naming inconsistencies (`_` issues) resolved in Ranking service.
- Circular dependency in Authentication service fixed.

---

## [0.6.0] - 2024-12-15

### Updated
- Aligned DTOs between Authentication and User services.
- Minor naming improvements in Authentication service.

---

## [0.5.2] - 2024-12-13

### Added
- Hibernate Spatial for handling location data in Restaurant service.

### Updated
- Improved location handling in Restaurant service, updating entities, repositories, and service logic.

---

## [0.5.0] - 2024-12-12

### Fixed
- Bugs in Notification and Reservation services.
- PostGIS configuration added for managing geospatial data in Restaurant database.

---

## [0.4.1] - 2024-12-10

### Added
- Documentation for Restaurant and User services.

---

## [0.4.0] - 2024-12-08

### Added
- Ranking and Restaurant services, including CRUD functionality.
- Filtering options (cuisine, search, price range) for Restaurant service.
- Kafka topic configuration bug fixed.

---

## [0.3.1] - 2024-12-07

### Fixed
- ID generation strategy for User and Favorite entities changed from `IDENTITY` to `AUTO`.

---

## [0.3.0] - 2024-12-04

### Added
- Email verification functionality for user accounts.
- Documentation for Authentication and Gateway services.

---

## [0.2.1] - 2024-11-30

### Added
- Email templates and email sending functionality.

---

## [0.2.0] - 2024-11-24

### Added
- Notification service documentation.

---

## [0.1.3] - 2024-11-22

### Added
- File storage integration using cloud services.
- Security configuration and JWT support for User service.
- Authentication service: Login, Register, and Unit tests.
- Review service CRUD operations.

### Updated
- User service restructured: entities, DTOs, and endpoints redesigned.

---

## [0.1.2] - 2024-11-18

### Added
- User service with endpoints, business logic, repositories, and DTOs.

---

## [0.1.1] - 2024-11-15

### Added
- OpenFeign configuration in Notification service for communication with Restaurant and User services.
- Partial implementation of Email service.

---

## [0.1.0] - 2024-11-10

### Added
- Review service backend structure (controller, mapper, repository, DTOs, and service).

---

## [0.0.9] - 2024-11-08

### Added
- Reservation service and Notification service integration.
- Kafka configurations for Notification and Reservation services.
- DTOs, services, and exception handling for backend services.

---

## [0.0.8] - 2024-10-31

### Updated
- Migrated Notification service database from MongoDB to PostgreSQL.
- Fixed Gateway service configuration and pom.xml issues.

---

## [0.0.7] - 2024-10-25

### Added
- Project starting files and base structure.
- Dependencies, `docker-compose.yml`, Config Server, and configurations for each service.
- Discovery Server and API Gateway.

---

## [0.0.1] - 2024-10-16

### Added
- Created backend repository.
- Initial README and LICENSE files.

---

> **Note:** For complete details, please refer to individual commit messages linked to specific changes.
