package com.dhex.shipping.services;

import com.dhex.shipping.exceptions.InvalidArgumentDhexException;
import com.dhex.shipping.exceptions.NotValidShippingStatusException;
import com.dhex.shipping.model.ShippingRequest;
import com.dhex.shipping.model.ShippingStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

public class ShippingServiceTest {

    public static final String GLOBAL_LOCATION = "Calle Los Negocios 444";
    public static final String GLOBAL_SENDER = "Jorge Quispe";
    public static final String GLOBAL_RECEIVER = "Juan Perez";
    public static final String GLOBAL_INVALID_STATUS = "invalid status";
    public static final String ON_HOLD_STATUS = "on hold";
    public static final int NEGATIVE_NUMBER = -10;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static ShippingRequest shippingRequest;
    private static ShippingService shippingService;
    private static ShippingStatus shippingStatus;

    @Before
    public void setUp() {
        shippingService = new ShippingService();
        register();
        registerShippingStatus();
        addShippingStatus();
    }

    private void register() {
        shippingRequest = shippingService
                .registerRequest(new SendingRequestParameterList(GLOBAL_RECEIVER, GLOBAL_SENDER, GLOBAL_LOCATION, 133, null));
    }

    private void registerShippingStatus() {
        shippingStatus = shippingService
                .registerStatus(shippingRequest.getId(), GLOBAL_LOCATION, ON_HOLD_STATUS, null);
    }

    private void addShippingStatus() {
        shippingRequest.addStatus(shippingStatus);
    }

    @Test
    public void shouldReturnAnIdAndDateWhenRegistering() {
        assertThat(shippingRequest.getId(), is(notNullValue()));
        assertThat(shippingRequest.getRegistrationMoment(), is(notNullValue()));
    }

    @Test
    public void shouldThrowInvalidArgumentDhexExceptionWhenCostIsLessThanZero() {
        expectedException.expect(InvalidArgumentDhexException.class);
        shippingService
                .registerRequest(new SendingRequestParameterList(GLOBAL_RECEIVER, GLOBAL_SENDER, GLOBAL_LOCATION, NEGATIVE_NUMBER, null));
    }

    @Test
    public void shouldThrowInvalidArgumentDhexExceptionWhenMandatoryFieldIsMissing() {
        expectedException.expect(InvalidArgumentDhexException.class);
        shippingService
                .registerRequest(new SendingRequestParameterList(null, GLOBAL_SENDER, GLOBAL_LOCATION, 100, null));
    }

    @Test
    public void shouldReturnAnIdAndDateWhenRegisteringShipmentStatus() {
        assertThat(shippingStatus.getId(), is(notNullValue()));
        assertThat(shippingStatus.getMoment(), is(notNullValue()));
    }

    @Test
    public void shouldThrowNotValidShippingStatusExceptionWhenAnyOtherStatusIsSent() {
        expectedException.expect(NotValidShippingStatusException.class);
        expectedException.expectMessage(containsString(GLOBAL_INVALID_STATUS));
        shippingService
                .registerStatus(shippingRequest.getId(), GLOBAL_LOCATION, GLOBAL_INVALID_STATUS, null);
    }

    @Test
    public void shouldThrowInvalidArgumentDhexExceptionWhenMissingRequestIdWhenRegisteringShipmentStatus() {
        expectedException.expect(InvalidArgumentDhexException.class);
        expectedException.expectMessage(containsString("RequestId"));
        shippingService
                .registerStatus(null, GLOBAL_LOCATION, GLOBAL_INVALID_STATUS, null);
    }

    @Test
    public void shouldThrowInvalidArgumentDhexExceptionWhenMissingLocationWhenRegisteringShipmentStatus() {
        expectedException.expect(InvalidArgumentDhexException.class);
        expectedException.expectMessage(containsString("Location"));
        shippingService
                .registerStatus(shippingRequest.getId(), null, GLOBAL_INVALID_STATUS, null);
    }

    @Test
    public void shouldThrowInvalidArgumentDhexExceptionWhenMissingStatusWhenRegisteringShipmentStatus() {
        expectedException.expect(InvalidArgumentDhexException.class);
        expectedException.expectMessage(containsString("Status"));
        shippingService
                .registerStatus(shippingRequest.getId(), GLOBAL_LOCATION, null, null);
    }

}