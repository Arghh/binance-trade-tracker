# binance trade tracker

Trades and profits tracking for bots and day trades all in one web frontend for easy use. This app is build using Spring Boot, Thymeleaf and Bootstrap. Basically it adds your partial trades together, calculates the profit you made on a daily bases or overall. More features are planned (See TODO-s). Trade tracker uses the [webcerebrium binance api.](https://github.com/webcerebrium/java-binance-api)

## Getting Started

First download and install [mariadb](https://mariadb.org/) if you haven't already. Create and new user and start it up. Trade tracker won't start without a running db instance. Alternatively: Binance Trade Tracker will also work with other SQL db-s, but you need to change the `pom.xml` `application.properties` accordingly.

### Installing

Easiest way to install just clone the repo and build from source. If you are not familiar with Java or just lazy download the release [compiled jar]() and run that. You will need to create a `*yourname*.properties` file yourself and fill it [like here.](https://github.com/Arghh/binance-trade-tracker/blob/master/src/main/resources/application.properties) In order to run Binance Trade Tracker jar just navigate to the folder where you downloaded the .jar and run:

```
java -jar binance-profit-tracker-0.0.1-SNAPSHOT.jar --spring.config.name=*your properties name*.properties
```

### Application.properties

##### MANDATORY FIELDS:

* __TradeHistory__ - In order to import the trades you need to add the full path to `tradeHistory.path=` the location of your `TradesHistory.xlsx` spreadsheet.
Windows:

```
C:/Users/Argh/Downloads/TradeHistory.xlsx
```

Linux:

```
C:/Users/Argh/Downloads/TradeHistory.xlsx
```
__Note:__ Please make sure you only have unique trades in the spreadsheet. Trade tracker can't filter out any duplicates. Just yet..

* __spring.datasource.username, password__ - Your user and pw from mariaDB that you created.
* __server.port__ - on what port would you like to access the app. 

```
server.port=50001 => localhost:50001
```

##### OPTIONAL FIELDS:
* BINANCE_API_KEY - Please make sure you only use a READ-ONLY key.
__Note:__ The API trade import is not yet ready.


```
Give examples
```

## Todos

* Error handling
* Ignoring duplicate trades
* Prices in USD
* Proper logging
* Wallet overview
* Proper frontend
* integration- and unit-tests
* performance
* daily coin prices
* historical coin data


## License

MIT