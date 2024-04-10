# Event Management Service

The event-management-service provides an interface to manage events. The service only provides an interface to get artist events at the moment but provides the base to expand into other event management interfaces. 

# Interface
See openapi documentation at TODO

## Running
### Docker 
If you simply wish to run the application, we recommend starting with Docker

#### Dependencies
- docker

#### Running
Within the base directory of the project, run the following command:
```
docker-compose up
```
#### Troubleshooting 
##### Port clashes
The app starts on 8085. You can change this port within the docker-compose.yml file.

## Design decisions 
### Package by feature

### Controller exception handling

### Concurrent calls to S3
Although we could make these calls concurrently since we're pulling all the data back, this wouldn't be the case in the real world, so I've decided to call 1 at a time to simulate calls to REST services that rely on IDs from previous calls.
