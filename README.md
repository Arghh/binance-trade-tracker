# Binance Trade Tracker

Trades and profits tracking for day trades in one easy to use web front end. Basically it just imports all your trades, adds your partial trades together and calculates the profit you made on a daily bases or overall. More features are planned (See TODO-s). This App is build using Spring Boot, Thymeleaf and Bootstrap. Trade Tracker uses the [webcerebrium binance api.](https://github.com/webcerebrium/java-binance-api)

## Getting Started

First download and install [mariadb](https://mariadb.org/) if you haven't already. Create and new user and start it up. Trade Tracker won't start without a running db instance.

```
apt install mariadb-server
```

Alternatively: Binance Trade Tracker will also work with other SQL DB-s but you need to change the `pom.xml` and `application.properties` files accordingly.

### Installing

Easiest way to install is to clone the repo and build from source. If you are not familiar with Java or don't want to build the source code yourself, just download the release [compiled jar](https://github.com/Arghh/binance-trade-tracker/releases) and run that. You will still need to create and fill out the `app.properties` file and place it in the same folder as the jar.

#### Application.properties

##### MANDATORY FIELDS:

* __spring.datasource.username, password__ - Your user and pw from mariaDB that you created.
* __server.port__ - on what port would you like to access the App.

```
server.port=50001 => localhost:50001
```
* __TradeHistory__ - In order to import the trades you need to add the full path to the location of your `TradesHistory.xlsx` spreadsheet into `tradeHistory.path=`.

For example Windows:

```
C:/Users/Argh/Downloads/TradeHistory.xlsx
```

For example Linux:

```
home/Argh/Downloads/TradeHistory.xlsx
```
__Note:__ Please make sure you only have unique trades in the Excel spreadsheet. Trade Tracker can't filter out any duplicates. Just yet..

##### OPTIONAL FIELDS:
* BINANCE_API_KEY - Please make sure you only use a READ-ONLY key.

__Note:__ The API trade import is not yet ready.


### Deployment

In order to run Binance Trade Tracker from the jar just navigate to the folder where you downloaded it and run:

```
java -jar binance-profit-tracker-0.0.1-SNAPSHOT.jar --spring.config.location="app.properties"
```
## Todos

* Error handling
* Ignoring duplicate trades
* Prices in USD
* Proper logging
* Wallet overview
* Proper front end
* integration- and unit-tests
* performance
* daily coin prices
* historical coin data


## Digital tip jar

BTC: 16AoWFGFRE2G15x2zBLTTX7C4h13tcMToP
ETH: 0xBeA6fC790Ad1171132fC257aA8173dA20847eB92
XLM: GBJUUZKDU3PIPMX6LPS34RVAL7EBBSBP2EWU45LO4Z5PH3WFBJ55HOWA

## License

MIT