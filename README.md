# binance trade tracker

Trades and profits tracking for bots and day trades all in one web frontend for easy use. This app is build using Spring Boot, Thymeleaf and Bootstrap. Basically it adds your partial trades together, calculates the profit you made on a daily bases or overall. More features are planned (See TODO-s). Trade tracker uses the [webcerebrium binance api.](https://github.com/webcerebrium/java-binance-api)

## Getting Started

First download and install [mariadb](https://mariadb.org/) if you haven't already.

```
apt install mariadb-server
```

Create and new user and start it up. Trade tracker won't start without a running db instance. Alternatively: Binance Trade Tracker will also work with other SQL db-s, but you need to change the `pom.xml` and `application.properties` files accordingly.

### Installing

Easiest way to install is to just clone the repo and build from source. If you are not familiar with Java or just lazy download the release [compiled jar](https://github.com/Arghh/binance-trade-tracker/releases) and run that. You will still need to create and fill out the `your_conf_name.properties` file and place it in the same folder as you downloaded the jar.

### Application.properties

##### MANDATORY FIELDS:

* __spring.datasource.username, password__ - Your user and pw from mariaDB that you created.
* __server.port__ - on what port would you like to access the app. 
* __TradeHistory__ - In order to import the trades you need to add the full path to the location of your `TradesHistory.xlsx` spreadsheet into `tradeHistory.path=`.

For example Windows:

```
C:/Users/Argh/Downloads/TradeHistory.xlsx
```

For example Linux:

```
home/Argh/Downloads/TradeHistory.xlsx
```
__Note:__ Please make sure you only have unique trades in the Excel spreadsheet. Trade tracker can't filter out any duplicates. Just yet..

```
server.port=50001 => localhost:50001
```

##### OPTIONAL FIELDS:
* BINANCE_API_KEY - Please make sure you only use a READ-ONLY key.
__Note:__ The API trade import is not yet ready.


### Deployment

In order to run Binance Trade Tracker from the jar just navigate to the folder where you downloaded it and run:

```
java -jar binance-profit-tracker-0.0.1-SNAPSHOT.jar --spring.config.location="*your_conf_name*.properties"
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