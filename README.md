# Perfumes store price comparator

## Description
This application extracts (scrapes) data from various perfumes stores, and then compares their prices.  You can add products to cart, and after adding few perfumes you can see what store is your cheapest option for shopping. 
Application allows your to search perfumes, add/remove them to cart, increase or reduce their quantity in your cart.

## Structure of project

_SRC folder:_
- db.clj - File for interaction with SQLite database.
- formatscrapeddata.clj - In this file are functions for formatting perfumes. When perfumes are scraped from perfume website they usually contain all of perfume info in their name. Functions from this file extract perfumes unit, weight, brand, and other relevant information.
- productsscraping.clj - File for scraping perfume data from website using Reaver.

_Test folder:_ - in this folder are tests for files (db, formatscrapeddata, handler and view).

**Libraries used**

- [Hiccup](https://github.com/weavejester/hiccup) library is used for representing HTML in Clojure.
- [Reaver](https://github.com/mischov/reaver) library is used for for extracting data out of HTML page.
- [java.jdbc](https://github.com/clojure/java.jdbc) - Clojure wrapper for JDBC-based access to databases.
- [sqlite-jdbc](https://github.com/xerial/sqlite-jdbc) - library for accessing and creating SQLite database files in Java.
- [Ring](https://github.com/ring-clojure/ring) is web application library, that is used for abstracting the details of HTTP into API;
- [Compojure](https://github.com/weavejester/compojure) is used as routing library for Ring.


## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Running
Navigate to example1 project and run lein run.
To start a web server for the application navigate to example1 project and run:

    lein ring server

## Scraping data before running server
Before starting server, if you want to scrape perfume store website you should execute line 101 in file productsscraping.clj. That function creates db, inserts stores to db, scrapes data from defined stores and saves that products to db. If you want to use existing db, which is data scraped on 24.03.2021 just use existing database - database.db.

## License
Eclipse Public License 2.0

Copyright © 2021 Ivona Heldrih
