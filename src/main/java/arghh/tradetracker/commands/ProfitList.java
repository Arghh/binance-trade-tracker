package arghh.tradetracker.commands;

import java.util.Date;

public class ProfitList {
    private String buyPrice;
    private Date buyTime;
    private long id;
    private String profit;
    private String quantity;
    private String sellPrice;
    private Date sellTime;
    private String symbol;
    private String timeDifference;
    private String totalProfit;

    public String getBuyPrice() {
	return buyPrice;
    }

    public Date getBuyTime() {
	return buyTime;
    }

    public long getId() {
	return id;
    }

    public String getProfit() {
	return profit;
    }

    public String getQuantity() {
	return quantity;
    }

    public String getSellPrice() {
	return sellPrice;
    }

    public Date getSellTime() {
	return sellTime;
    }

    public String getSymbol() {
	return symbol;
    }

    public String getTimeDifference() {
	return timeDifference;
    }

    public String getTotalProfit() {
	return totalProfit;
    }

    public void setBuyPrice(String buyPrice) {
	this.buyPrice = buyPrice;
    }

    public void setBuyTime(Date buyTime) {
	this.buyTime = buyTime;
    }

    public void setId(long id) {
	this.id = id;
    }

    public void setProfit(String profit) {
	this.profit = profit;
    }

    public void setQuantity(String quantity) {
	this.quantity = quantity;
    }

    public void setSellPrice(String sellPrice) {
	this.sellPrice = sellPrice;
    }

    public void setSellTime(Date sellTime) {
	this.sellTime = sellTime;
    }

    public void setSymbol(String symbol) {
	this.symbol = symbol;
    }

    public void setTimeDifference(String timeDifference) {
	this.timeDifference = timeDifference;
    }

    public void setTotalProfit(String totalProfit) {
	this.totalProfit = totalProfit;
    }

}
