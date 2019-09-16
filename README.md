# account-api
- This is a Prof of Concept of a RESTful API for monetary operations between accounts without using Spring libraries.


### Explicit requirements:
1. Use Java
2. Should be simple and to the point (e.g. no need to implement any authentication).
3. Assume the API is invoked by multiple systems and services on behalf of end users (Should be Thread Safe).
4. Light weight frameworks (Spring is not allowed)
5. The datastore should run in-memory for the sake of this test.
6. The final result should be executable as a standalone program (should not require a
pre-installed container/server).
7. Demonstrate with tests that the API works as expected.

### The API:
The logic behind this API is very simple. 
The whole flow is divided in 2 parts.
1. API receives requests, transforms it into a Transaction Object (Status = NEW), add it to a thread safe queue, and stores it in the database.(For history, and auditing purposes)
	- At this point the api should return with http status '202 Accepted' and a message in the body informing that the transaction was added to the queue for future processing.
2. There is a Processor running in a configurable time interval, this process will read transactions from the queue, perform the monetary operations accordingly, and update its status on database.
	- Validations are being applied. Such as, not transfer higher amount than it is available in the account. In case of any failure the Transaction will be updated with status FAILED and the field Transaction.FAILURE_REASON will have a brief description of what happened.
	- It is possible to configure the time interval for the processor by chaning the property processor.scheduled.fixeddelay (Default is 3 seconds) in the file *application.yml* located at *src/main/resources/*
	
### How to run:
 - This API comes with a gradle wrapper, so even if you don't have gradle installed you should be able to build and run it.
 - It is possible to configure the api by changing and/or adding configuration properties in the *'application.yml'* file located at *'src/main/resources/'* - E.g. Database details, Processor time interval...

First navigates to Root project folder and then
  
    ./gradlew run

-or-

    ./gradlew build
    java -jar ./build/libs/account-api-0.1-all.jar
  
 - *Unit tests and Integration tests are being executed in each build cicle.*
 - *Intergration tests can take up to 2 minutes to run, if you want to skip then just add '-xintegrationTest' to the gradle command*

### Exposed endpoints are:
| Method | Endpoint                                         | Description                                                                               |          
|:------:|:------------------------------------------------:|:-----------------------------------------------------------------------------------------:|
| PUT    | /account/create                                  | Create and Return new account (This is here to facilitate testing)                        |
| GET    | /account                                         | Retrieves all accounts (This is here to facilitate testing)                               |
| GET    | /account/{id}                                    | Retrieves specific account (This is here to facilitate testing)                           |
| POST   | /account/{id}/deposit/{amount}                   | Perform deposit                                                                           |
| POST   | /account/{id}/withdraw/{amount}                  | Perform withdraw                                                                          |
| POST   | /account/{from}/transfer/{amount}/toaccount/{to} | Perform transfer                                                                          | 
| GET    | /account/{accountId}/transaction                 | Retrieves history of transactions for specified account (Ordered by the most recent ones) |


### Transaction Lifecycle:
	Every transaction will go through these Status.
	
	1 - NEW
	2 - PROCESSING
	3 - COMPLETED | FAILED
	
### Technology Stack:

	1 - Micronaut: Lightweight framework for build microservices and serverless applications
		More info: https://micronaut.io/
	2 - Gradle: Build Tool
		More info: https://gradle.org/
	3 - H2 Database: In Memory database
		More info: https://www.h2database.com/
	4 - Hibernate: Entity management
		More info: http://hibernate.org/
	5 - Junit5: Testing framework
		More info: https://junit.org/junit5/
		
##### Next steps:		

  - Include Swagger
  - Implement MDC logging to track requests on logs easier
  - Replace the 'Processor' by some messaging system, maybe kafka

		
