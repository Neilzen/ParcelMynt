# ParcelMynt

Spring Boot application that Provides an API to calculate Parcel based on Weight and Volume

# Requirements

Create an API that will calculate the cost of delivery of a parcel based on weight and volume (volume = height * width *
length).
The API should accept the following:

1. Weight (kg)
2. Height (cm)
3. Width (cm)
4. Length (cm)

The rules for calculating cost of delivery are in order of priority

| Priority  | Rule Name     | Condition                   | Cost                 |  
|-----------|---------------|-----------------------------|----------------------|
| (Highest) | Reject        | Weight exceeds 50kg         | N/A                  |
|           | Heavy Parcel  | Weight exceeds 10kg         | PHP 20 * Weight (Kg) |
|           | Small Parcel  | Volume is less than 1500 c㎥ | PHP 0.03 * Volume    |
|           | Medium Parcel | Volume is less than 2500 c㎥ | PHP 0.04 * Volume    |
|           | Large Parcel  |                             | PHP 0.05 * Volume    |

As the market tends to fluctuate in terms of pricing, the rules needs to be as flexible as possible.

Your API should also accept a voucher code that can be used to provide discounts to the customer.
To get the discount details of the submitted voucher code,
you will need to integrate with the voucher service maintained by another team.
You may check their Voucher API
definition [Where](https://app.swaggerhub.com/apis/mynt-iat/mynt-programming-exams/1.1.0) ​here​.
This also includes the details of the mock server that they have provided for your testing.

# How to use

## Pre-requisite

- Java17
- Gradle 7.5.1

## Local Build

- gradle clean build

## Run Locally

- gradle bootRun
- URL for accessing swagger-ui: http://localhost:8080/swagger-ui/index.html

# Solution

- To be able to handle changes in conditions and price, conditions and formula's have to be stored in DB to have
  a common access of data in case of producing/set up replica for this service.
- To handle such expression, this service
  utilizes [SpelExpression](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/expression/spel/standard/SpelExpression.html)
  which can evaluate basic arithmetic and condition.
- To process Condition, it is hard coded to only evaluate the following
  variables [`weight`,`length`,`width`,`height`,`volume`,`limit`] based on defined expression. See below table for
  sample.

| expression       | values                    | rendered expression | output |
|------------------|---------------------------|---------------------|--------|
| `weight > limit` | weight: 51, limit: 50     | 51 > 50             | true   |
| `weight > limit` | weight: 50, limit: 50     | 50 > 50             | false  |
| `volume < limit` | volume: 1499, limit: 1500 | 1499 < 1500         | true   |
| `volume < limit` | volume: 1501, limit: 1500 | 1501 < 1500         | false  |

- To process Cost, it is also hard coded to only evaluate the following
  variables [`weight`,`length`,`width`,`height`,`volume`,`cost`] based on defined expression. See below table for
  reference

| expression                         | values                                              | rendered expression         | output |
|------------------------------------|-----------------------------------------------------|-----------------------------|--------|
| `cost * weight`                    | weight: 51, cost: 20                                | 20 * 51                     | 1020   |
| `cost * volume`                    | volume: 1499, cost: 0.03                            | 0.03 * 1499                 | 44.97  |
| `cost * (length * width * height)` | length: 15, width: 13.58, height: 12.58, cost: 0.03 | 0.03 * (15 * 13.58 * 12.58) | 76.89  |

- To set up conditions and computation it has to be added into database based on table below: (However for this setup no
  Database has been established yet and was hardcoded in `ParcelConditionRepository`)

### Table Name: ParcelCondition

| column name          | data type    |
|----------------------|--------------|
| ID                   | int          |
| PRIORITY             | int          |
| RULE_NAME            | Varchar      |
| CONDITION_EXPRESSION | Varchar      |
| CONDITION_LIMIT      | Number(15,2) |
| COST                 | Number(15,2) |
| COST_EXPRESSION      | Varchar      |
| IS_REJECT            | BOOLEAN      |
| IS_ACTIVE            | BOOLEAN      |