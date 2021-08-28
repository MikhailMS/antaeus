## Antaeus

Antaeus (/ænˈtiːəs/), in Greek mythology, a giant of Libya, the son of the sea god Poseidon and the Earth goddess Gaia. He compelled all strangers who were passing through the country to wrestle with him. Whenever Antaeus touched the Earth (his mother), his strength was renewed, so that even if thrown to the ground, he was invincible. Heracles, in combat with him, discovered the source of his strength and, lifting him up from Earth, crushed him to death.

Welcome to our challenge.

## The challenge

As most "Software as a Service" (SaaS) companies, Pleo needs to charge a subscription fee every month.
Our database contains a few invoices for the different markets in which we operate.
Your task is to build the logic that will schedule payment of those invoices on the first day of the month.
While this may seem simple, there is space for some decisions to be taken and you will be expected to justify them.

## Instructions

Fork this repo with your solution. Ideally, we'd like to see your progression through commits, and don't forget to update the README.md to explain your thought process.

Please let us know how long the challenge takes you.
We're not looking for how speedy or lengthy you are.
It's just really to give us a clearer idea of what you've produced in the time you decided to take.
Feel free to go as big or as small as you want.

## Developing

Requirements:
- \>= Java 11 environment

Open the project using your favorite text editor. If you are using IntelliJ, you can open the `build.gradle.kts` file and it is gonna setup the project in the IDE for you.

### Building

```
./gradlew build
```

### Running

There are 2 options for running Anteus.
You either need libsqlite3 or docker.
Docker is easier but requires some docker knowledge.
We do recommend docker though.

*Running Natively*

Native java with sqlite (requires libsqlite3):

If you use homebrew on MacOS `brew install sqlite`.

```
./gradlew run
```

*Running through docker*

Install docker for your platform

```
docker build -t antaeus
docker run antaeus
```

### App Structure
The code given is structured as follows. Feel free however to modify the structure to fit your needs.
```
├── buildSrc
|  | gradle build scripts and project wide dependency declarations
|  └ src/main/kotlin/utils.kt 
|      Dependencies
|
├── pleo-antaeus-app
|       main() & initialization
|
├── pleo-antaeus-core
|       This is probably where you will introduce most of your new code.
|       Pay attention to the PaymentProvider and BillingService class.
|
├── pleo-antaeus-data
|       Module interfacing with the database. Contains the database 
|       models, mappings and access layer.
|
├── pleo-antaeus-models
|       Definition of the Internal and API models used throughout the
|       application.
|
└── pleo-antaeus-rest
        Entry point for HTTP REST API. This is where the routes are defined.
```

### Main Libraries and dependencies
* [Exposed](https://github.com/JetBrains/Exposed) - DSL for type-safe SQL
* [Javalin](https://javalin.io/) - Simple web framework (for REST)
* [kotlin-logging](https://github.com/MicroUtils/kotlin-logging) - Simple logging framework for Kotlin
* [JUnit 5](https://junit.org/junit5/) - Testing framework
* [Mockk](https://mockk.io/) - Mocking library
* [Sqlite3](https://sqlite.org/index.html) - Database storage engine

Happy hacking 😁!

### Diary
1. 14-15th August 2021 (total 5 hours)
Unfortunately for myself, I was fighting the issue, when Gradle in docker was failing with
```
Error: build daemon disappeared unexpectedly
```
At some point it got resolved, not sure what helped to fix that. Glad it is out of the way now, so I can start working on the challenge :)
2. 28th August 2021 (total 4 hours)
Had a look through available modules to id where I need to make changes to complete the challenge
```
pleo-antaeus-app
 --> AntaeusApp.kt - **need to initialise actual billing service**, which would be executed at the frequency defined in the challenge
 --> utils.kt      - getPaymentProvider looks like a dummy function that simulates response from 3rd party payment provider (I assume payment provider would actually handle the payment and such would return False if customer has insufficient funds)

pleo-antaeus-core
 --> exceptions - **don't touch this submodule**
 --> external
     --> PaymentProvider.kt - an interface that represents actions that are available via 3rd party system, that should be handling payments
 --> services
     --> BillingService.kt  - **this is where challenge is**; need to initialise a service such a way that it would be running at specific frequency, ie first day of each month
     --> CustomerService.kt - provide an access to DB methods to fetch user(s)
     --> InvoiceService.kt  - **need to be updated to pick up new methods from AntaeusDal** provide an access to DB methods to fetch invoice(s)

pleo-antaeus-data
 --> AntaeusDal  - defines calls to DB. **need to add call to update Invoice record (update its status)
 --> mappings.kt - defines mappings between database rows and Kotlin objects
 --> tables.kt   - defines database tables and their schemas

pleo-antaeus-models - **so far I believe there is nothing to be changed or added here**

pleo-antaeus-rest - **so far I believe there is nothing to be changed or added here**, endpoints are
 --> /
     --> rest/
         --> health
         --> v1/
             --> invoices/{id}
             --> customers/{id}
```
3.