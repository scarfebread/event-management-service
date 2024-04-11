# Event Management Service
The event-management-service provides an interface to manage events. The service only provides an interface to get artist events at the moment but provides the base to expand into other event management interfaces.

# Interface
## GET /artist/(id)
Gets the artist by the artist ID
### Example request
```
GET /artist/21
```

### Example response
```
{
    "name": "HRH Prog",
    "id": "21",
    "imgSrc": "//some-base-url/hrh-prog.jpg",
    "url": "/hrh-prog-tickets/artist/21",
    "rank": 1,
    "events":
    [
        {
            "title": "Huge Live",
            "id": "13",
            "dateStatus": "multiDate",
            "timeZone": null,
            "startDate": null,
            "venue":
            {
                "name": "O2 Academy Sheffield",
                "url": "/o2-academy-sheffield-tickets-sheffield/venue/41",
                "city": "Sheffield",
                "id": "41"
            },
            "hiddenFromSearch": false
        },
        {
            "title": "A festival Live",
            "id": "7",
            "dateStatus": "singleDate",
            "timeZone": null,
            "startDate": null,
            "venue":
            {
                "name": "O2 Academy Brixton",
                "url": "/o2-academy-brixton/venue/45",
                "city": "London",
                "id": "45"
            },
            "hiddenFromSearch": null
        },
        {
            "title": "Fusion Prog",
            "id": "1",
            "dateStatus": "singleDate",
            "timeZone": "Europe/London",
            "startDate": "2020-10-17T00:00:00",
            "venue":
            {
                "name": "O2 Academy Sheffield",
                "url": "/o2-academy-sheffield-tickets-sheffield/venue/41",
                "city": "Sheffield",
                "id": "41"
            },
            "hiddenFromSearch": false
        }
    ]
}
```

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

### Gradle / IDE
For making changes, you can run via Gradle or your IDE of choice.

#### Dependencies
- Java 17

#### Running via gradle 
Within the base directory of the project, run the following command:
```
./gradlew bootRun
```

## Design decisions 
### Technologies
#### Spring WebFlux
WebFlux has been used to write reactive and non-blocking code, providing an HTTP interface and HTTP client. WebFlux will also be useful for future endpoints to relieve back pressure by streaming responses. 

### Package structure
#### /routes
Here the HTTP routes are defined along with the relevant service layer, which contains the business logic. 

There sub packages for artist, event, venue, as even though there's only a requirement for artist, the application can be expanded to add more routes.

#### /client
Here we define the upstream clients to each of the services. The client directory should be treated as a utility package and not depend on anything other than other utility packages.

### Concurrent calls to S3
Although we could make all 3 calls concurrently since we're pulling all the data back, this wouldn't be the case in the real world, so I've decided to call the artist and event endpoints concurrently, with venues off the back of the event response.

### Data integrity issues
There is a possibility that the event references IDs that do not exist in the corresponding services. If an artist does not exist in the artist endpoint, a 404 will be returned. However, if a venue does not exist on the venue endpoint, a partial response will be returned and a WARN will be logged.

## Next steps
### Expose interface documentation
At the moment the interface is defined in the README, whereas for a production application we should expose via OpenAPI documentation.

### Extract config 
The upstream REST endpoints are hard coded in the app. We should extract them into config to be injected via environment variables.

### Integration tests
Although we have unit test coverage, we'll need integrations tests to validate the end to end scenarios of the application. 
