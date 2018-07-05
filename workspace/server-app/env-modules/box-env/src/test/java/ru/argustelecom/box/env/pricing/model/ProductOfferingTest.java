package ru.argustelecom.box.env.pricing.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import ru.argustelecom.box.env.stl.Money;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductOfferingTest {

    @Mock
    ProductOffering offering;
    @Mock
    AbstractPricelist pricelist;

    @Test
    public void shouldCalculatePriceWithoutTax() {

        Whitebox.setInternalState(offering, "price", new Money("19.92"));
        Whitebox.setInternalState(offering, "pricelist", pricelist);
        when(pricelist.getTaxMultiplier()).thenReturn(new BigDecimal("0.21"));
        doCallRealMethod().when(offering).getPriceWithoutTax();

        Money excludeVat = offering.getPriceWithoutTax();
        assertEquals(0, excludeVat.compareRounded(new Money("16.46")));
    }
}