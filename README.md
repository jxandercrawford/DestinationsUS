# DestinationsUS
A simple ETL pipeline in Scala utilizing the [Typelevel](https://typelevel.org) libraries.

### Overview

The goal of this project is to build a data pipeline to safely ingest transit data pertaining to US air travel from the [Bureau of Transportation Statistics](https://transtats.bts.gov) and load them into a PostgreSQL database. This data can be downloaded as zip files containing a data dictionary and a csv file. A general overview of the pipeline is depicted below.

![abstract dataflow diagram](resources/documentation/pipeline_overview_diagram.png)

The database that the flights are written to is a simple data model only containing 2 tables. The first table consists of airports and the second flights. Each flight has an id and a date in which it occurred. This is then paired with a reference to the airport table for the origin and destination. The entity relation diagram can be seen below.

![erd](resources/documentation/erd.png)

While in most cases this pipeline will run monthly to collect the data from the most recent month, the initial loading of data will require many consecutive months to be run. In order to handle this type of loading operation the pipeline is able to concurrently run with multiple sources.

![concurrency_diagram](resources/documentation/concurrency_diagram.png)
