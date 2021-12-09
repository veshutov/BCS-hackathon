package com.bcs.competition.integrations.prices;

import java.math.BigDecimal;

/**
 * @author veshutov
 **/
public interface TickerPriceProvider {
    BigDecimal getPrice(String ticker);
    void refreshTickerPrice(String ticker);
}
