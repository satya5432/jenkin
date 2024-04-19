# Test task

## Getting started
* Install postgresql (9+) and maven
* See db_bootstrap folder for db setup instructions
* Run mvn test to see if everything appears alright

## Testing the server startup & simple search
> mvn test
Or
> mvn clean test 

## Running the local server // application migration to spring boot
> mvn spring-boot:run
Or
> mvn clean spring-boot:run

## I observed that in you run the app in spring boot embedded server on first run it takes time, probably to initialize all components
## on subsequent runs it work fine.

## Querying the local server
> $ curl -v "http://localhost:12000/?merchant=8a3045ace8e164e896e2337fd9a12ca2&msisdn=373000028"

### a full payment report for a single merchant for a single day 

 > $ curl -v "http://localhost:12000/e45992391f8f609a497eee0cbafaf991/singleDay?searchDate=2017-10-13"
 
### a full payment report for a single merchant for a 3 month period
 
  > $ curl -v "http://localhost:12000/e45992391f8f609a497eee0cbafaf991/quarter?searchDate=2017-10-13"

## I dont like pre-chosen component jetty, db driver or the database itself 
> You can swap anything you like except the data set.

> If you choose alternative data store it must be able to receive new records rapidly over time.
